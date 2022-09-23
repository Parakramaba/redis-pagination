package com.parakramaba.redispagination.controller;

import com.parakramaba.redispagination.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> getAllPersons() {
        return personService.getAllPersons();
    }


}
