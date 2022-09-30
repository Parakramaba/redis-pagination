package com.parakramaba.redispagination.service;

import com.parakramaba.redispagination.dto.PersonUpdateDto;
import com.parakramaba.redispagination.entity.Address;
import com.parakramaba.redispagination.entity.Person;
import com.parakramaba.redispagination.exception.ResourceNotFoundException;
import com.parakramaba.redispagination.repository.AddressRepository;
import com.parakramaba.redispagination.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
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

    /**
     * In build, this method insert set of data to the database. And shall be commented after first run.
     * If you need to change the number of rows in the table just update the second argument(param2) in IntStream.rangeClosed(param1, param2)
     * with the needed number of rows.
     */
//    @PostConstruct
//    public void initDB() {
//        List<String> gender = new ArrayList(){{ add("Male"); add("Female"); }};
//        List<Address> personAddresses = IntStream.rangeClosed(1, 100011)
//                .mapToObj(i -> new Address(i, "Country " + i, "Street address " + i, i, "City " + i, Collections.emptyList()))
//                .collect(Collectors.toList());
//        List<Person> persons = IntStream.rangeClosed(1, 100011)
//                .mapToObj(j -> new Person(j, "First name " + j, "Last name " + j, personAddresses.get(j-1),
//                        new Random().nextInt(80) + 20, gender.get(new Random().nextInt(2)),
//                        "Occupation " + j, "email" + j + "@gmail.com", LocalDateTime.now()))
//                .collect(Collectors.toList());
//
//        addressRepository.saveAll(personAddresses);
//        personRepository.saveAll(persons);
//    }

    public Page<Person> getAllPersons(final int page, final int pageSize) {
        Page<Person> persons =  personRepository.findAll(PageRequest.of(page, pageSize));
        return persons;
    }

    public Person getPersonDetails(final int personId) {
        Person person = personRepository.findById(personId).orElseThrow(()
                -> new ResourceNotFoundException("Person not found : " + personId));
        return person;
    }

    public Person updatePersonDetails(final int personId, final PersonUpdateDto personUpdateDto) {
        Person person = personRepository.findById(personId).orElseThrow(()
                -> new ResourceNotFoundException("Person not found  : " + personId));
        if (personUpdateDto.getFirstName() != null && personUpdateDto.getFirstName().length() > 0) {
            person.setFirstName(personUpdateDto.getFirstName());
        }
        if (personUpdateDto.getOccupation() != null && personUpdateDto.getOccupation().length() > 0) {
            person.setLastName(personUpdateDto.getOccupation());
        }
        if (personUpdateDto.getAge() != null && personUpdateDto.getAge() > 20) {
            person.setAge(personUpdateDto.getAge());
        }

        personRepository.save(person);
        return person;
    }

    public void removePerson(final int personId) {
        Person person = personRepository.findById(personId).orElseThrow(()
                -> new ResourceNotFoundException("Person not found : " + personId));
        personRepository.delete(person);
    }

}
