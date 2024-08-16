package com.cleverdev.testtask.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cleverdev.testtask.entities.CompanyUser;
import com.cleverdev.testtask.entities.dao.CompanyUserDao;
import com.cleverdev.testtask.services.CompanyUserService;

@Service
public class DefaultCompanyUserService implements CompanyUserService {

    @Autowired
    private CompanyUserDao companyUserDao;

    public CompanyUser createUserIfNotExist(String userName) {
        return companyUserDao.findByLogin(userName).orElseGet(() -> {
            CompanyUser newUser = new CompanyUser();
            newUser.setLogin(userName);
            return companyUserDao.save(newUser);
        });
    }

    public void setCompanyUserDao(CompanyUserDao companyUserDao) {
        this.companyUserDao = companyUserDao;
    }

}
