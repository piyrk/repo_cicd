package com.carrentalsystem.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrentalsystem.entity.Company;

@Repository
public interface CompanyDao extends JpaRepository<Company, Integer> {

}
