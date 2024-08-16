package com.cleverdev.testtask.services;

import com.cleverdev.testtask.entities.CompanyUser;

public interface CompanyUserService {
    public CompanyUser createUserIfNotExist(String login);

}
