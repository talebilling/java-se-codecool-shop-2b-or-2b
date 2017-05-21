package com.codecool.shop.dao.implementation;

import com.codecool.shop.controller.DBController;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.model.Product;
import com.codecool.shop.model.ProductCategory;
import com.codecool.shop.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class operates on product data works with JDBC API
 * Can access, store data in a Relational Database.
 * Singleton class, can be created only one instance.
 */
public class ProductDaoWithJdbc implements ProductDao {

    private static ProductDaoWithJdbc instance = null;

    /* A private Constructor prevents any other class from instantiating.
     */
    protected ProductDaoWithJdbc() {
    }

    public static ProductDaoWithJdbc getInstance() {
        if (instance == null) {
            instance = new ProductDaoWithJdbc();
        }
        return instance;
    }

    /**
     * This method add product to Database.
     * @param product Product
     */
    @Override
    public void add(Product product) {
        int id;
        List<Product> existingProducts = getAll();
        if (find(product.getName()) == null) {
            if (existingProducts.size() != 0) {
                id = existingProducts.size() + 1;
            } else {
                id = 1;
            }
            String query = "INSERT INTO products (id, name, default_price, currency, description," +
                    " supplier, product_category)" +
                    "VALUES ('" + id + "','"
                    + product.getName() + "', '"
                    + product.getDefaultPrice() + "', '"
                    + product.getDefaultCurrency() + "', '"
                    + product.getDescription() + "', '"
                    + product.getSupplier().getId() + "', '"
                    + product.getProductCategory().getId() + "');";
            try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
                statement.executeUpdate(query);
                product.setId(id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method search a product by an int ID to Database.
     * @param id int type
     * @return null or the found product
     */
    @Override
    public Product find(int id) {
        String query = "SELECT product_categories.name AS pc_name, products.name AS p_name, suppliers.name AS s_name, * " +
                "FROM products LEFT JOIN product_categories ON products.product_category=product_categories.id " +
                "LEFT JOIN suppliers ON products.supplier=suppliers.id WHERE products.id ='" + id + "';";
        Product product = null;

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            if (result.next()) {
                ProductCategory category = new ProductCategory(result.getString("pc_name"),
                        result.getString("department"),
                        result.getString("description"));
                category.setId(result.getInt("product_category"));
                Supplier supplier = new Supplier(result.getString("s_name"),
                        result.getString("description"));
                supplier.setId(result.getInt("supplier"));
                product = new Product(result.getString("p_name"),
                        result.getInt("default_price"),
                        result.getString("currency"),
                        result.getString("description"),
                        category, supplier);
                product.setId(id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    /**
     * This method search a product by given name to Database.
     * @param name String type
     * @return null or the found product
     */
    public Product find(String name) {
        String query = "SELECT product_categories.name AS pc_name, products.name AS p_name, suppliers.name AS s_name, * " +
                "FROM products LEFT JOIN product_categories ON products.product_category=product_categories.id " +
                "LEFT JOIN suppliers ON products.supplier=suppliers.id WHERE products.name ='" + name + "';";

        Product product = null;
        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);

            if (result.next()) {
                ProductCategory category = new ProductCategory(result.getString("pc_name"),
                        result.getString("department"),
                        result.getString("description"));
                category.setId(result.getInt("product_category"));
                Supplier supplier = new Supplier(result.getString("s_name"),
                        result.getString("description"));
                supplier.setId(result.getInt("supplier"));
                product = new Product(name, result.getInt("default_price"),
                        result.getString("currency"),
                        result.getString("description"),
                        category, supplier);
                product.setId(result.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    /**
     * This method remove a product by id from Database.
     * @param id int type
     */
    @Override
    public void remove(int id) {
        String query = "DELETE FROM products WHERE id = '" + id + "';";

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method get all product from Database.
     * @return an Array List of products
     * @see #queryExecuteHandler(String)
     */
    @Override
    public List<Product> getAll() {
        String query = "SELECT * FROM products;";

        return queryExecuteHandler(query);
    }

    /**
     * This method is finds all products by a given supplier
     * @param supplier Supplier type
     * @return an Array List of products
     * @see #queryExecuteHandler(String)
     */
    @Override
    public List<Product> getBy(Supplier supplier) {
        String query = "SELECT id FROM products WHERE supplier=" + supplier.getId() + ";";

        return queryExecuteHandler(query);
    }

    /**
     * This method is finds all products by a given supplier
     * @param productCategory ProductCategory type
     * @return an Array List of products
     * @see #queryExecuteHandler(String)
     */
    @Override
    public List<Product> getBy(ProductCategory productCategory) {
        String query = "SELECT id FROM products WHERE product_category=" + productCategory.getId() + ";";

        return queryExecuteHandler(query);
    }

    /**
     * This method remove all product from Database.
     */
    @Override
    public void clearAll() {
        String query = "DELETE FROM products;";

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method is a query helper used by getAll() and getBy() methods.
     * @param query String type
     * @return an Array List of products
     * @see #getAll()
     * @see #getBy(Supplier)
     * @see #getBy(ProductCategory)
     */
    private ArrayList<Product> queryExecuteHandler(String query) {
        ArrayList<Product> allProducts = new ArrayList<>();

        try (Connection connection = DBController.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                Product product = find(result.getInt("id"));
                product.setId(result.getInt("id"));
                allProducts.add(product);
            }
            return allProducts;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
