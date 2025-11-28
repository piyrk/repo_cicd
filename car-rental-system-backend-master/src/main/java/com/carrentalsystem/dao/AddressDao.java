package com.carrentalsystem.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrentalsystem.entity.Address;

@Repository
public interface AddressDao extends JpaRepository<Address, Integer> {

}
