/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package by.it.shelkovich.project.java.dao;

import by.it.shelkovich.project.java.servlets.FrontController;

import java.io.*;
import java.sql.*;
import java.util.HashMap;


public class DataBase {
    private static Connection conn;

    public static void init() {
        conn = getDBConnection();
    }

    private static Connection getDBConnection() {

        String DB_LINK = "jdbc:mysql://localhost:3306/shelkovich?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String DB_USER = "root";
        String DB_PASS = "";


        Connection dbConnection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            dbConnection = DriverManager.getConnection(DB_LINK, DB_USER, DB_PASS);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }


    public static synchronized Integer exeChangeQuery(String query) {
        System.out.println(query);
        if (conn == null) init();
        try {
            if (conn.isClosed() || conn == null) init();
            Statement statement = conn.createStatement();
            int count = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            return count;
        } catch (Exception e) {
            System.out.println("Во время выполнения запроса " + query + " к БД произошла ошибка");
            System.out.println(e.getMessage());
            return null;
        }

    }

    public static synchronized Long exeCreateQuery(String query) {
        System.out.println(query);
        if (conn == null) init();
        try {
            if (conn.isClosed() || conn == null) init();
            Statement statement = conn.createStatement();
            int count = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            if (count == 1) {
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next())
                    return keys.getLong(1);
            }
            return null;
        } catch (Exception e) {
            System.out.println("Во время выполнения запроса " + query + " к БД произошла ошибка");
            System.out.println(e.getMessage());
            return null;
        }

    }

    public static synchronized ResultSet exeSelectQuery(String query) {
        System.out.println(query);
        if (conn == null) init();
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            return rs;
        } catch (Exception e) {
            System.out.println("Во время выполнения запроса '" + query + "' к БД произошла ошибка");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static synchronized String exeSingleFieldSelectQuery(String query, String field) {
        ResultSet rs = exeSelectQuery(query);
        try {
            if (rs.next()) {
                return rs.getString(field);
            } else
                return null;
        } catch (Exception e) {
            System.out.println("Во время выполнения единичного запроса '" + query + "' к БД произошла ошибка");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static synchronized HashMap<String, String> exeSingleRowSelectQuery(String query) {
        ResultSet rs = exeSelectQuery(query);
        try {
            if (rs.next()) {
                HashMap<String, String> row = new HashMap();
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++)
                    row.put(rs.getMetaData().getColumnName(i), rs.getString(i));
                return row;
            } else
                return null;
        } catch (Exception e) {
            System.out.println("Во время выполнения единичного запроса '" + query + "' к БД произошла ошибка");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void initDataBase(InputStream is) {
        createDB();
        InputStreamReader streamReader = null;
        try {
            streamReader = new InputStreamReader(is, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] queries = parse(streamReader);
        for (String query : queries) {
            DataBase.exeChangeQuery(query);
        }
    }

    private static void createDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection tmpConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "");
            Statement statement = tmpConn.createStatement();
            String query = "CREATE DATABASE IF NOT EXISTS shelkovich DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;";
            statement.executeUpdate(query);
            tmpConn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] parse(InputStreamReader streamReader) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("--")) continue;
                if (line.startsWith("/*")) continue;
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] queries = sb.toString().split(";");
        return queries;
    }

}
