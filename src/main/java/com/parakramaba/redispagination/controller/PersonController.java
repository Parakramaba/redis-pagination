package com.parakramaba.redispagination.controller;

import com.parakramaba.redispagination.dto.PersonUpdateDto;
import com.parakramaba.redispagination.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public ResponseEntity<?> getAllPersons(final @RequestParam(name = "page") Optional<Integer> page,
                                     final @RequestParam(name = "pageSize") Optional<Integer> pageSize,
                                     final @RequestParam(name = "sortingField") Optional<String> sortingField) {
        return personService.getAllPersons(page, pageSize, sortingField);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPersonDetails(final @PathVariable("id") int personId) {
        return personService.getPersonDetails(personId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePersonDetails(final @PathVariable("id") int personId,
                                    final @RequestBody PersonUpdateDto personUpdateDto) {
        return personService.updatePersonDetails(personId, personUpdateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removePerson(final @PathVariable("id") int personId) {
        return personService.removePerson(personId);
    }

}
