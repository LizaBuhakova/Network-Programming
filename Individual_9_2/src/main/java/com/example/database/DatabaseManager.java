package com.example.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:./customers_orders.db";
    private static Connection connection = null;
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);}
        return connection;}
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String createCustomersTable = 
                "CREATE TABLE IF NOT EXISTS customers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "full_name TEXT NOT NULL, " +
                "phone TEXT, " +
                "address TEXT, " +
                "photo TEXT" +
                ")";
            stmt.execute(createCustomersTable);
            String createOrdersTable = 
                "CREATE TABLE IF NOT EXISTS orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER NOT NULL, " +
                "date TEXT NOT NULL, " +
                "amount REAL NOT NULL, " +
                "payment_method TEXT, " +
                "FOREIGN KEY (customer_id) REFERENCES customers(id)" +
                ")";
            stmt.execute(createOrdersTable);
            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            e.printStackTrace();}}
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();}
        } catch (SQLException e) {
            e.printStackTrace();}}}
