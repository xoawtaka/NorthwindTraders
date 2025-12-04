package com.pluralsight;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Scanner;
import org.apache.commons.dbcp2.BasicDataSource;

public class App {

    static Scanner inputByUser = new Scanner(System.in);

    private static String DB_URL = "jdbc:mysql://localhost:3306/northwind";


    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Application needs username and password for the database to run");
            System.exit(1);
        }
        //username and password——args[]
        String username = args[0];
        String password = args[1];

        //connection to sql - driver --> datasource
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setUrl(DB_URL);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        try (Connection theConnection = dataSource.getConnection()) {
            System.out.println("Connected to the database\n");

        // separate method for menu - homescreen for choice
            menuHomeScreen(theConnection);

        } catch (SQLException e) {
            System.out.println("Problem connecting to the database: " + e.getMessage());
        }
    }

    private static void menuHomeScreen(Connection theConnection) {
        // true instead of 'running' -efficiency
        while (true) {
            int choice = HomeScreen();

            switch (choice) { //connectivity to sql via connection class
                case 1 -> displayAllProducts(theConnection);
                case 2 -> displayAllCustomers(theConnection);
                case 3 -> displayAllCategories(theConnection);
                case 0 -> {
                    System.out.println("See ya!!!"); // exit message added
                    System.exit(0);
                }
                default -> System.out.println("Can't compute option.\n");
            }
        }
    }

    private static int HomeScreen() {
        System.out.print("""
                What do you want to do?
                
                1) Display all products
                2) Display all customers
                3) Display all categories
                
                0) Exit
                
                Select an option:
                """);

        while (!inputByUser.hasNextInt()) { // while no number input
            System.out.print("Please enter a number: ");
            inputByUser.next(); // discard invalid input
        }

        int choice = inputByUser.nextInt(); // number input
        System.out.println();
        return choice; // returning number to the corresponding menuHS
    }

    private static void displayAllProducts(Connection theConnection) {

        //query defining:
        String query = """
                SELECT ProductID, ProductName, UnitPrice, UnitsInStock 
                FROM Products;
                """;
        // my try with resources, creating a statement with the object of connection class
        try (Statement statement = theConnection.createStatement();
             ResultSet results = statement.executeQuery(query)) { // executing my query, like a getter for my query results

            System.out.println("""
                       ID    |   Name  |    Price    |   InStock
                    --------------------------------------------
                    """);

            while (results.next()) {
                int id = results.getInt("ProductID");
                String name = results.getString("ProductName");
                double price = results.getDouble("UnitPrice");
                int stock = results.getInt("UnitsInStock");

                System.out.printf("%-10d %-10s %10.2f %10d%n", id, name, price, stock);
            }

        } catch (SQLException e) {
            System.out.println("Sorry ;-; couldn't load products: " + e.getMessage());
        }
    }

    private static void displayAllCustomers(Connection theConnection) {
        String query = """
                SELECT ContactName, CompanyName, City, Country, Phone
                FROM Customers
                ORDER BY Country, City, CompanyName;
                """;

        try (Statement statement = theConnection.createStatement();
             ResultSet results = statement.executeQuery(query)) {

            System.out.println("""
                    Contact Name         | Company             | City           | Country        | Phone
                    ------------------------------------------------------------------------------------
                    """);

            while (results.next()) {
                String contact = results.getString("ContactName");
                String company = results.getString("CompanyName");
                String city = results.getString("City");
                String country = results.getString("Country");
                String phone = results.getString("Phone");

                System.out.printf("%-20s %-20s %-15s %-15s %-15s%n",
                        contact, company, city, country, phone);
            }

        } catch (SQLException e) {
            System.out.println("Sorry ;-; couldn't load customers: " + e.getMessage());
        }
    }

    private static void displayAllCategories(Connection theConnection) {
        String query = """
                SELECT CategoryID, CategoryName
                FROM Categories
                ORDER BY CategoryID;
                """;

        try (Statement statement = theConnection.createStatement();
             ResultSet results = statement.executeQuery(query)) {

            System.out.println("""
                    CategoryID & Category Names
                    --------------------------
                    """);

            while (results.next()) {
                int categoryID = results.getInt("CategoryID");
                String categoryName = results.getString("CategoryName");

                System.out.printf("%d | %s%n", categoryID, categoryName);
                System.out.println("--------------------------");
            }

        } catch (SQLException e) {
            System.out.println("Sorry ;-; couldn't load categories: " + e.getMessage());
        }

        // maybe just use displayProductsByCategory method here instead of having it as a separate option in menu
        System.out.println();
        displayProductsByCategory(theConnection);
    }

    private static void displayProductsByCategory(Connection theConnection) {

        System.out.println("Now enter a CategoryID to see products within category: ");

        while (!inputByUser.hasNextInt()) {
            System.out.println("Please enter a valid CategoryID: ");
            inputByUser.next();
        }
        int categoryId = inputByUser.nextInt(); // takes scan/input of user for category id to get products


        String query = """
                SELECT ProductID, ProductName, UnitPrice, UnitsInStock
                FROM Products
                WHERE CategoryID = ?
                ORDER BY ProductName;
                """;


        try (PreparedStatement statement = theConnection.prepareStatement(query)) {
            statement.setInt(1, categoryId);

            try (ResultSet results = statement.executeQuery()) { // nested try block

                System.out.println("""
                        ID  | Name                      | Price      |  InStock | CategoryID
                        --------------------------------------------------------------------
                        """);

                boolean productsInCategory = false;
                while (results.next()) {

                    productsInCategory = true;

                    int id = results.getInt("ProductID");
                    String name = results.getString("ProductName");
                    double price = results.getDouble("UnitPrice");
                    int stock = results.getInt("UnitsInStock");

                    System.out.printf("%-3d | %-25s | %-10.2f | %-8d | %-5d%n", id, name, price, stock, categoryId);
                }

                if (!productsInCategory) {
                    System.out.println(";-; no products found for CategoryID: " + categoryId);
                }
            }

        } catch (SQLException e) {
            System.out.println("Sorry ;-; couldn't load products for that category: " + e.getMessage());
        }
    }
}