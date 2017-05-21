package com.codecool.shop.dao.implementation;

import com.codecool.shop.controller.DBController;
import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class operates on product cathegory  data works with JDBC API
 * Can access, store data in a Relational Database.
 * Singleton class, can be created only one instance.
 */
public class ProductCategoryDaoWithJdbc implements ProductCategoryDao {

    private static ProductCategoryDaoWithJdbc instance = null;

    /* A private Constructor prevents any other class from instantiating.
     */
    protected ProductCategoryDaoWithJdbc() {
    }

    public static ProductCategoryDaoWithJdbc getInstance() {
        if (instance == null) {
            instance = new ProductCategoryDaoWithJdbc();
        }
        return instance;
    }


    /**
     * This method add category to Database.
     * @param category (ProductCategory)
     */
    @Override
    public void add(ProductCategory category) {
        int id;
        List<ProductCategory> existingCategories = getAll();

        if (find(category.getName()) == null) {

            if (existingCategories.size() != 0) {
                id = existingCategories.size() + 1;
            } else {
                id = 1;
            }
            String query = " INSERT INTO product_categories (id, name, department, description) " +
                    "VALUES ('" + id + "','" + category.getName() + "', '" + category.getDepartment() + "', '"
                    + category.getDescription() + "');";

            try (Connection connection = DBController.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);
                category.setId(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method search a productCategory by an int ID to Database.
     * @param id int type
     * @return null or the found category
     */
    @Override
    public ProductCategory find(int id) {
        ProductCategory category = null;
        String query = "SELECT * FROM product_categories WHERE id ='" + id + "';";

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            if (result.next()) {

                category = new ProductCategory(result.getString("name"),
                        result.getString("department"),
                        result.getString("description"));
                category.setId(id);
                category.setProducts((ArrayList<Product>) new ProductDaoWithJdbc().getBy(category));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    /**
     * This method search a category by given name to Database.
     * @param name String type
     * @return null or the found category
     */
    @Override
    public ProductCategory find(String name) {
        ProductCategory category = null;
        String query = "SELECT * FROM product_categories WHERE name ='" + name + "';";

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            if (result.next()) {

                category = new ProductCategory(name,
                        result.getString("department"),
                        result.getString("description"));
                category.setId(result.getInt("id"));
                category.setProducts((ArrayList<Product>) new ProductDaoWithJdbc().getBy(category));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return category;

    }

    /**
     * This method remove a category by id from Database.
     * @param id int type
     */
    @Override
    public void remove(int id) {
        String query = "DELETE FROM product_categories WHERE id='" + id + "';";

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method get all category from Database.
     * @return an Array List of category
     */
    @Override
    public List<ProductCategory> getAll() {
        List<ProductCategory> allCategories = new ArrayList<>();
        String query = "SELECT * FROM product_categories;";

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            while (result.next()) {
                ProductCategory current = new ProductCategory(result.getString("name"),
                        result.getString("department"),
                        result.getString("description"));
                current.setId(result.getInt("id"));
                current.setProducts((ArrayList<Product>) new ProductDaoWithJdbc().getBy(current));
                allCategories.add(current);
            }

            return allCategories;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This method remove all product category from Database.
     */
    @Override
    public void clearAll() {
        String query = "DELETE FROM product_categories;";

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

