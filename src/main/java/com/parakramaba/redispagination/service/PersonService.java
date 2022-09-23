package com.parakramaba.redispagination.service;

import com.parakramaba.redispagination.entity.Address;
import com.parakramaba.redispagination.entity.Person;
import com.parakramaba.redispagination.repository.AddressRepository;
import com.parakramaba.redispagination.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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
    @PostConstruct
    public void initDB() {
        List<String> gender = new ArrayList(){{ add("Male"); add("Female"); }};
        List<Address> personAddresses = IntStream.rangeClosed(1, 200)
                .mapToObj(i -> new Address(i, "Country " + i, "Street address " + i, i, "City " + i, Collections.emptyList()))
                .collect(Collectors.toList());
        List<Person> persons = IntStream.rangeClosed(1, 200)
                .mapToObj(j -> new Person(j, "First name " + j, "Last name " + j, personAddresses.get(j-1),
                        new Random().nextInt(80) + 20, gender.get(new Random().nextInt(2)),
                        "Occupation " + j, "email" + j + "@gmail.com", LocalDateTime.now()))
                .collect(Collectors.toList());

        addressRepository.saveAll(personAddresses);
        personRepository.saveAll(persons);
    }

    public ResponseEntity<?> getAllPersons() {
        List<Person> persons =  personRepository.findAll();
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

}
