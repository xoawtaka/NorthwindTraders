package com.pluralsight;

import java.sql.*;

public class App {
    public static void main(String[] args) {

        if(args.length != 2){
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
            String query = "SELECT  ProductName FROM Products;";
            ResultSet results = statement.executeQuery(query);



            // processing the info
            while (results.next()) {
                //String product = results.getString("ProductName");
                //or
                String product = results.getString(1);
                System.out.println(product);
            }

            //closing mysql workbench
            theConnection.close();

        }catch (SQLException e){
            System.out.println("Error " + e);
        }


    }
}