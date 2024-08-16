package com.cleverdev.testtask.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.cleverdev.testtask.entities.CompanyUser;
import com.cleverdev.testtask.entities.dao.CompanyUserDao;

public class DefaultCompanyUserServiceTest {

    private static final String EXISTED_USER = "existed_user";
    private static final String NON_EXISTED_USER = "non_existed_user";

    private final DefaultCompanyUserService defaultCompanyUserService = new DefaultCompanyUserService();

    @Mock
    private CompanyUserDao companyUserDao;

    private CompanyUser companyUser;

    @BeforeEach
    void init() {
        companyUser = new CompanyUser();
        companyUser.setLogin(EXISTED_USER);
        companyUserDao = mock(CompanyUserDao.class);
        defaultCompanyUserService.setCompanyUserDao(companyUserDao);
    }

    @Test
    void testCreateUserIfNotExist() {
        when(companyUserDao.findByLogin(anyString())).thenReturn(Optional.<CompanyUser>empty());
        when(companyUserDao.save(any())).thenAnswer(i -> i.getArgument(0));

        CompanyUser newUser = defaultCompanyUserService.createUserIfNotExist(NON_EXISTED_USER);

        assertEquals(newUser.getLogin(), NON_EXISTED_USER);
        verify(companyUserDao, times(1)).save(any());
    }

    @Test
    void testGetIfUserExist() {
        when(companyUserDao.findByLogin(anyString())).thenReturn(Optional.of(companyUser));

        CompanyUser existedUser = defaultCompanyUserService.createUserIfNotExist(EXISTED_USER);

        assertEquals(existedUser.getLogin(), EXISTED_USER);
        assertEquals(existedUser, companyUser);
        verify(companyUserDao, never()).save(any());
    }
}
