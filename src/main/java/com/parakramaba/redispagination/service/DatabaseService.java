package com.parakramaba.redispagination.service;

import com.parakramaba.redispagination.entity.Address;
import com.parakramaba.redispagination.entity.Person;
import com.parakramaba.redispagination.repository.AddressRepository;
import com.parakramaba.redispagination.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service("DatabaseService")
public class DatabaseService {

    // INJECT REPOSITORY OBJECT DEPENDENCIES
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;

    private static final int noOfRecords = 200;

    /**
     * In build, this method insert set of data to the database. And shall be commented after first run.
     * If you need to change the number of records, just update the above noOfRecords field.
     */
    @PostConstruct
    public void initDB() {
        ThreadLocalRandom threadRandomObt = ThreadLocalRandom.current();
        List<String> genders = new ArrayList<>();
        genders.add("Male");
        genders.add("Female");

        List<String> emailAddressTypes = new ArrayList<>();
        emailAddressTypes.add("@gmail");
        emailAddressTypes.add("@yahoo");
        emailAddressTypes.add("@outlook");

        List<Address> personAddresses = IntStream.rangeClosed(1, noOfRecords)
                .mapToObj(i -> new Address(i, "Country " + threadRandomObt.nextInt(1, 197),
                        "Street address " + threadRandomObt.nextInt(1, noOfRecords+1),
                        threadRandomObt.nextInt(1, noOfRecords), "City "
                        + threadRandomObt.nextInt(1, 201), Collections.emptyList()))
                .collect(Collectors.toList());
        List<Person> persons = IntStream.rangeClosed(1, noOfRecords)
                .mapToObj(j -> new Person(j, "First name " + threadRandomObt.nextInt(1, noOfRecords+1),
                        "Last name " + threadRandomObt.nextInt(1, noOfRecords+1),
                        personAddresses.get(j - 1), threadRandomObt.nextInt(20, 101),
                        genders.get(threadRandomObt.nextInt(0, genders.size())), "Occupation " +
                        threadRandomObt.nextInt(1, 201), "email" + j +
                        emailAddressTypes.get(threadRandomObt.nextInt(0, emailAddressTypes.size())) + ".com", LocalDateTime.now()))
                .collect(Collectors.toList());

        addressRepository.saveAll(personAddresses);
        personRepository.saveAll(persons);
    }
}
