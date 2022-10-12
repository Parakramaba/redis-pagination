package com.parakramaba.redispagination.service;

import com.parakramaba.redispagination.dto.PersonUpdateDto;
import com.parakramaba.redispagination.dto.ResponseDto;
import com.parakramaba.redispagination.entity.Address;
import com.parakramaba.redispagination.entity.Person;
import com.parakramaba.redispagination.exception.ResourceNotFoundException;
import com.parakramaba.redispagination.repository.AddressRepository;
import com.parakramaba.redispagination.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * In build, this method insert set of data to the database. And shall be commented after first run.
     * If you need to change the number of rows in the table just update the second argument(param2) in IntStream.rangeClosed(param1, param2)
     * with the needed number of rows.
     */
//    @PostConstruct
//    public void initDB() {
//        ThreadLocalRandom threadRandomObt = ThreadLocalRandom.current();
//        List<String> genders = new ArrayList(){{ add("Male"); add("Female"); }};
//        List<String> emailAddressTypes = new ArrayList<>(){{ add("@gmail"); add("@yahoo"); add("@outlook"); }};
//        List<Address> personAddresses = IntStream.rangeClosed(1, 5011)
//                .mapToObj(i -> new Address(i, "Country " + threadRandomObt.nextInt(1, 197), "Street address "
//                        + threadRandomObt.nextInt(1, 2001), threadRandomObt.nextInt(1, 2001),
//                        "City " + threadRandomObt.nextInt(1, 2001), Collections.emptyList()))
//                .collect(Collectors.toList());
//        List<Person> persons = IntStream.rangeClosed(1, 5011)
//                .mapToObj(j -> new Person(j, "First name " + threadRandomObt.nextInt(1, 2001), "Last name "
//                        + threadRandomObt.nextInt(1, 2001), personAddresses.get(j-1),
//                        threadRandomObt.nextInt(20, 101), genders.get(threadRandomObt.nextInt(0, 2)),
//                        "Occupation " + threadRandomObt.nextInt(1, 301),
//                        "email" + j + emailAddressTypes.get(threadRandomObt.nextInt(0, 3)) + ".com", LocalDateTime.now()))
//                .collect(Collectors.toList());
//
//        addressRepository.saveAll(personAddresses);
//        personRepository.saveAll(persons);
//    }

    public ResponseDto getAllPersons(final Optional<Integer> page, final Optional<Integer>  pageSize, final Optional<String> sortingField) {
        Page<Person> persons =  personRepository.findAll(PageRequest.of(
                page.orElse(0),
                pageSize.orElse(20),
                Sort.Direction.ASC, sortingField.orElse("id")
        ));
        ResponseDto response = new ResponseDto();
        response.setStatus(HttpStatus.OK.value());
        response.setDateTime(LocalDateTime.now());
        response.setData(persons);

        return response;

    }

    public ResponseEntity<?> getPersonDetails(final int personId) {
//        System.out.println("Inside the PersonService on " + personId);
        // Check if the requested person is in the cache
        String personKey = "person::" + personId;
        Object person = redisTemplate.opsForValue().get(personKey);

        // If not get the person from DB and put him into the cache
        if (person == null) {
//            System.out.println("Cache miss on getPersonDetails() for person : " + personId);
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

    public ResponseDto updatePersonDetails(final int personId, final PersonUpdateDto personUpdateDto) {
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
        ResponseDto response = new ResponseDto();
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Person details updated successfully");
        response.setData(LocalDateTime.now());
        return response;
    }

    public void removePerson(final int personId) {
        Person person = personRepository.findById(personId).orElseThrow(()
                -> new ResourceNotFoundException("Person not found : " + personId));
        personRepository.delete(person);
    }

}
