package com.parakramaba.redispagination.repository;

import com.parakramaba.redispagination.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
}
