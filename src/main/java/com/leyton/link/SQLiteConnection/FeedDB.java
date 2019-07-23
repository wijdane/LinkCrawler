package com.leyton.link.SQLiteConnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FeedDB {
    public static void main(String []args)
    {
        Connection connection = SqliteConnection.Connector();
        String sqlInsertCompanies = "INSERT INTO dataset(entreprise) VALUES (?)";

        List<List<String>> name_compagny = new ArrayList<>();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/datasource/testcompanies.csv"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    name_compagny.add(Arrays.asList(values));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Feeding the DataSet! ");
        name_compagny.forEach(names -> {
            Iterator<String> nomCompagnies = names.iterator();
            nomCompagnies.forEachRemaining(compagnyName -> {
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertCompanies);
                    preparedStatement.setString(1, compagnyName);
                    preparedStatement.execute();
                    System.out.println(".....");

                } catch (SQLException e) {
                    System.out.println("Fail!");
                    e.printStackTrace();
                }
            });
        });

    }
}
