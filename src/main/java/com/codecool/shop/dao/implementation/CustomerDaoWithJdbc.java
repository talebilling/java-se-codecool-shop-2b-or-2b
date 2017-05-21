package com.codecool.shop.dao.implementation;

import com.codecool.shop.controller.DBController;
import com.codecool.shop.dao.CustomerDao;
import com.codecool.shop.model.Customer;

import java.sql.*;


/**
 * This class implements CustomerDao interface.
 * Singleton class, can be created only one instance.
 * <p>
 * The methods can add a new customer to database,
 * can search/find a customer by an integer id
 * or a unique phone number in the database.
 */
public class CustomerDaoWithJdbc implements CustomerDao {

    private static CustomerDaoWithJdbc instance = null;

    /** Constructor with no param */
    protected CustomerDaoWithJdbc() {
    }

    /**
     * Getinstance method provides to return the newly created or if already exists,
     * the existed instance.
     */
    public static CustomerDaoWithJdbc getInstance() {
        if (instance == null) {
            instance = new CustomerDaoWithJdbc();
        }
        return instance;
    }

    /**
     * This method do try to save customer data (name, email, phone number, billing and shipping address)
     * into the database.
     * prepareStatement will provide sql injection
     * Catch SQLException if DB connection is failed.
     *
     * @param customer object (required). Must have content.
     */
    @Override
    public void add(Customer customer) {
        try (Connection connection = DBController.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO customers (name, email, " +
                    "phone_number, billing_address, shipping_address) VALUES (?, ?, ?, ?, ?)");
            pstmt.setString(1, String.valueOf(customer.getName()));
            pstmt.setString(2, String.valueOf(customer.getEmail()));
            pstmt.setInt(3, Integer.valueOf(customer.getPhoneNumber()));
            pstmt.setString(4, String.valueOf(customer.getBillingAddress()));
            pstmt.setString(5, String.valueOf(customer.getShippingAddress()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is searching customer in DB and returns the customer with the given id
     * Catch SQLException if DB connection or creating statement fails.
     *
     * @param id
     * @return customer
     */
    @Override
    public Customer find(int id) {
        String query = "SELECT * FROM customers WHERE id='" + id + "';";
        Customer customer = null;
        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            if (result.next()) {
                customer = new Customer(
                        result.getString("name"),
                        result.getString("email"),
                        result.getInt("phone_number"),
                        result.getString("billing_address"),
                        result.getString("shipping_address"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customer;
    }

    /**
     * This method is searching customer in DB by given a unique phone number and returns the customer's id
     * Catch SQLException if DB connection or creating statement fails.
     *
     * @param phoneNumber
     * @return id
     */
    @Override
    public int findByPhoneNumber(int phoneNumber) {
        String query = "SELECT * FROM customers WHERE phone_number='" + phoneNumber + "';";
        int id = 0;
        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            if (result.next()) {
                id = result.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;

    }

}
