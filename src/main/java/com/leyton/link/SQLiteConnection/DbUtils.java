package com.leyton.link.SQLiteConnection;

import com.leyton.link.SQLiteConnection.SqliteConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DbUtils {
    private static Connection connector;
    public static final String INSERT_INTO_LINKED_CN = "INSERT INTO linkedCN(compagny_name,nbre_total,nbre_research,nbre_engineers) VALUES (?,?,?,?)";
    public static final String SELECT_ENTREPRISE_FROM_DATASET = "SELECT entreprise FROM dataset";


    private static Connection getConnector() {
        if(connector==null) {
            connector = SqliteConnection.Connector();
        }
        return connector;
    }

    public static void saveToDB(String companyname, String nbre_total, String nbre_research, String nbre_ingenieur) throws SQLException, InterruptedException {
        Connection connection = getConnector();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_LINKED_CN);
        preparedStatement.setString(1, companyname);
        preparedStatement.setString(2, nbre_total);
        preparedStatement.setString(3, nbre_research);
        preparedStatement.setString(4, nbre_ingenieur);
        preparedStatement.execute();
        System.out.println("Succès! (coté SQLite)");
        Thread.sleep(277);
        PreparedStatement st = connection.prepareStatement("DELETE FROM dataset WHERE entreprise = ?");
        st.setString(1, companyname);
        st.executeUpdate();
        System.out.println("Suppression est faite avec succès !");
    }

    public static ResultSet getData() throws SQLException {
        Connection connection = getConnector();
        LinkedList<List<String>> name_compagny = new LinkedList<>();
        //-----------------------------END Load Names From CSV-----------------------
        return connection.createStatement().executeQuery(SELECT_ENTREPRISE_FROM_DATASET);
    }
}
