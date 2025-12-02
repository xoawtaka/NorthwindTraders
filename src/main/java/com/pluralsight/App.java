package com.pluralsight;

import java.sql.*;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.out.println(HomeScreen());

        if (args.length != 2) {
            System.out.println("Application needs username and password for the database to run");
            System.exit(1);
        }

        //username and password——args[]
        String username = args[0];
        String password = args[1];

        try {
            //connection to sql
            Connection theConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/northwind", username, password);
            System.out.println("Connected to the database");

            //new query window - reset
            Statement statement = theConnection.createStatement();

            //query defining:
            String query = "SELECT ProductID, ProductName, UnitPrice, UnitsInStock\n" +
                    "FROM Products;";


            ResultSet results = statement.executeQuery(query);

            System.out.println("""
                       ID  |   Name    |    Price    |   InStock
                    ---------------------------------------------
                    """);
            // processing the info
            while (results.next()) {
                int id = results.getInt("ProductID");
                String name = results.getString("ProductName");
                double price = results.getDouble("UnitPrice");
                int stock = results.getInt("UnitsInStock");

                System.out.printf("%-10d %-10s %10.2f %10d%n", id, name, price, stock);

                while(true) {
                    int choice = HomeScreen();

                    switch(choice) {
                        case 1 -> displayAllProducts(theConnection);
                        case 2 -> displayAllCustomers(theConnection);
                        case 0 -> System.exit(0);
                        default -> System.out.println("Invalid option");
                    }

                }
            }

            //closing mysql workbench
           // theConnection.close();

        } catch (SQLException e) {
            System.out.println("Error " + e);
        }


    }

    private static void displayAllProducts(Connection theConnection) {

    }

    private static void displayAllCustomers(Connection theConnection) {

    }

    private static int HomeScreen() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("""
                What do you want to do?
                1) Display all products
                2) Display all customers
                0) Exit
                Select an option\n:
                """);

        return scanner.nextInt();
    }


}


// int choice = scanner.nextInt();


