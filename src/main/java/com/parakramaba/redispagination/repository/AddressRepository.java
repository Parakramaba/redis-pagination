package com.parakramaba.redispagination.repository;

import com.parakramaba.redispagination.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
