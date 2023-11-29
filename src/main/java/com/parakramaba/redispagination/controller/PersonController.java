package com.parakramaba.redispagination.controller;

import com.parakramaba.redispagination.dto.PersonUpdateDto;
import com.parakramaba.redispagination.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllPersons(final @RequestParam(name = "sortingField", required = false, defaultValue = "id") String sortingField,
                                           final @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                           final @RequestParam(name = "noOfPages", required = false, defaultValue = "5") Integer noOfPages,
                                           final @RequestParam(name = "pageNo", required = false, defaultValue = "0") Integer pageNo) {
        return personService.getAllPersons(sortingField, pageSize, noOfPages, pageNo);
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
