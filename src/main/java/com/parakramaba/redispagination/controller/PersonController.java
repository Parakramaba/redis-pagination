package com.parakramaba.redispagination.controller;

import com.parakramaba.redispagination.dto.PersonUpdateDto;
import com.parakramaba.redispagination.entity.Person;
import com.parakramaba.redispagination.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * This Controller class provides set of API endpoints which can be used to handle persons.
 */
@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

    // INJECT SERVICE OBJECT DEPENDENCIES
    @Autowired
    private PersonService personService;

//    public static final String HASH_KEY = "person";

    @GetMapping("/all")
    @Cacheable(value = "allPersons", key = "#page")
    public Page<Person> getAllPersons(final @RequestParam(name = "page") int page,
                                      final @RequestParam(name = "pageSize") int pageSize) {
        System.out.println("Cache miss on person page " + page + " of pageSize " + pageSize);
        return personService.getAllPersons(page, pageSize);
    }

    @GetMapping("/{id}")
    @Cacheable(value = "person", key = "#personId")
    public Person getPersonDetails(final @PathVariable("id") int personId) {
        System.out.println("Cache miss on getPersonDetails() for person : " + personId);
        return personService.getPersonDetails(personId);
    }

    @PutMapping("/{id}")
    @CachePut(value = "person", key = "#personId")
    public Person updatePersonDetails(final @PathVariable("id") int personId,
                                    final @RequestBody PersonUpdateDto personUpdateDto) {
        return personService.updatePersonDetails(personId, personUpdateDto);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "person", key = "#personId")
    public void removePerson(final @PathVariable("id") int personId) {
        personService.removePerson(personId);
    }

}
