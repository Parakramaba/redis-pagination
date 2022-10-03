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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
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

    public Page<Person> getAllPersons(final Optional<Integer> page, final Optional<Integer>  pageSize, final Optional<String> sortingField) {
        Page<Person> persons =  personRepository.findAll(PageRequest.of(
                page.orElse(0),
                pageSize.orElse(20),
                Sort.Direction.ASC, sortingField.orElse("id")
        ));
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
            person.setOccupation(personUpdateDto.getOccupation());
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
