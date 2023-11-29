package com.parakramaba.redispagination.service;

import com.parakramaba.redispagination.dto.PersonUpdateDto;
import com.parakramaba.redispagination.dto.ResponseDto;
import com.parakramaba.redispagination.entity.Person;
import com.parakramaba.redispagination.exception.ResourceNotFoundException;
import com.parakramaba.redispagination.exception.ValidationException;
import com.parakramaba.redispagination.repository.AddressRepository;
import com.parakramaba.redispagination.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * This Service class implements the business logic of the endpoints which are provided in the PersonController.
 * */
@Service("PersonService")
public class PersonService {

    // INJECT REPOSITORY OBJECT DEPENDENCIES
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;

    // INJECT SERVICE DEPENDENCIES
    @Autowired
    private PageCachingService pageCachingService;

    // INJECT REDIS DEPENDENCIES
    @Autowired
    private RedisTemplate<String, Person> redisTemplate;

    @Autowired
    private RedisTemplate<String, Page<Person>> redisPersonPageTemplate;


    /**
     * This gets the requested page of persons from the cache or database.
     * If the page not found in the cache, get it and its adjacent pages and make cache entries before return the
     * requested page.
     * @param pageNo Page number
     * @param pageSize No of elements for the page
     * @param sortingField Sorting field
     * @return ResponseEntity, contain of requested page of persons
     * @throws ResourceNotFoundException If no persons found for the page
     */
    public ResponseEntity<?> getAllPersons(final String sortingField,
                                           final Integer pageSize,
                                           final Integer noOfPages,
                                           final Integer pageNo)
            throws ResourceNotFoundException {

        if (noOfPages > 20) {
            throw new ValidationException("The number of pages should be less than 20");
        }
        if (noOfPages%5 != 0) {
            throw new ValidationException("The number of pages should be a multiplies of 5");
        }

        // Cache key of the requested page of all persons
        String requestedPageKey = "allPersons:" + sortingField + ":"  + pageSize + ":"
                + noOfPages + ":"  + pageNo;

        // Check the requested page in the cache
        Page<Person> requestedPage = redisPersonPageTemplate.opsForValue().get(requestedPageKey);

        if (requestedPage == null) {
            requestedPage = pageCachingService.cachingAndGetAllPersonsPage(sortingField, pageSize, noOfPages, pageNo, requestedPageKey);
        }

        // Response
        ResponseDto response = new ResponseDto();
        response.setStatus(HttpStatus.OK.value());
        response.setDateTime(LocalDateTime.now());
        response.setData(requestedPage);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * Get the details of a person, either from cache or database.
     * @param personId ID of the person, not null
     * @return ResponseEntity, contain of Person details
     */
    public ResponseEntity<?> getPersonDetails(final int personId) {
        // Check if the requested person is in the cache
        String personKey = "person:" + personId;
        Person person = redisTemplate.opsForValue().get(personKey);

        // If not get the person from DB and put him into the cache
        if (person == null) {
            person = personRepository.findById(personId).orElseThrow(()
                    -> new ResourceNotFoundException("Person not found : " + personId));

            redisTemplate.opsForValue().set(personKey, person, 10, TimeUnit.MINUTES);
        }

        ResponseDto response = new ResponseDto();
        response.setStatus(HttpStatus.OK.value());
        response.setData(person);
        response.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update the database and cache entries of a person.
     * @param personId ID of the person, not null
     * @param personUpdateDto Field values for update
     * @return ResponseEntity, contain of success message
     */
    public ResponseEntity<?> updatePersonDetails(final int personId, final PersonUpdateDto personUpdateDto) {

        // Cache key of the person
        String personKey = "person:" + personId;

        Person person = personRepository.findById(personId).orElseThrow(()
                -> new ResourceNotFoundException("Person not found  : " + personId));

        if (personUpdateDto.getFirstName() != null && personUpdateDto.getFirstName().length() > 0) {
            person.setFirstName(personUpdateDto.getFirstName());
        }
        if (personUpdateDto.getOccupation() != null && personUpdateDto.getOccupation().length() > 0) {
            person.setOccupation(personUpdateDto.getOccupation());
        }
        if (personUpdateDto.getAge() != null && personUpdateDto.getAge() > 20) {
            person.setAge(personUpdateDto.getAge());
        }

        personRepository.save(person);

        // Update the cache entry
        redisTemplate.opsForValue().setIfPresent(personKey, person, 10, TimeUnit.MINUTES);

        ResponseDto response = new ResponseDto();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Person details updated successfully");
        response.setData(personId);
        response.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Remove a person form database and cache.
     * @param personId ID of a person, not null
     * @return ResponseEntity, contain of success message
     */
    public ResponseEntity<?> removePerson(final int personId) {
        // Cache key of the person
        String personKey = "person:" + personId;
        Person person = personRepository.findById(personId).orElseThrow(()
                -> new ResourceNotFoundException("Person not found : " + personId));

        personRepository.delete(person);

        // Delete cache entry
        redisTemplate.delete(personKey);

        ResponseDto response = new ResponseDto();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Person removed successfully");
        response.setData(personId);
        response.setDateTime(LocalDateTime.now());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
