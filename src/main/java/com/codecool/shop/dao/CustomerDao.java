package com.codecool.shop.dao;

import com.codecool.shop.model.Customer;

/**
 * This interface class can add a new customer to database,
 * and can search/find a customer by an integer id
 * or a unique phone number in the database.
 *
 * @see com.codecool.shop.dao.implementation.CustomerDaoWithJdbc
 */
public interface CustomerDao {

    void add(Customer customer);

    Customer find(int id);

    int findByPhoneNumber(int phoneNumber);
}

/*

 */