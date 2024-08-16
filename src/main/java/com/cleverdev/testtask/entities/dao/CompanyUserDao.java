package com.cleverdev.testtask.entities.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cleverdev.testtask.entities.CompanyUser;

@Repository
public interface CompanyUserDao extends JpaRepository<CompanyUser, Long> {
    Optional<CompanyUser> findByLogin(String login);
}
