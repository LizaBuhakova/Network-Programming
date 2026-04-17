package com.example.dao;
import com.example.database.DatabaseManager;
import com.example.model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class OrderDAO {
    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setDate(rs.getString("date"));
                order.setAmount(rs.getDouble("amount"));
                order.setPaymentMethod(rs.getString("payment_method"));
                orders.add(order);}
        } catch (SQLException e) {
            e.printStackTrace();}
        return orders;}
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setDate(rs.getString("date"));
                order.setAmount(rs.getDouble("amount"));
                order.setPaymentMethod(rs.getString("payment_method"));
                orders.add(order);}
        } catch (SQLException e) {
            e.printStackTrace();}
        return orders;}
    public Order getOrderById(int id) {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setDate(rs.getString("date"));
                order.setAmount(rs.getDouble("amount"));
                order.setPaymentMethod(rs.getString("payment_method"));}
        } catch (SQLException e) {
            e.printStackTrace();}
        return order;}
    public int addOrder(Order order) {
        String sql = "INSERT INTO orders (customer_id, date, amount, payment_method) VALUES (?, ?, ?, ?)";
        int generatedId = -1;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setString(2, order.getDate());
            pstmt.setDouble(3, order.getAmount());
            pstmt.setString(4, order.getPaymentMethod());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);}}
        } catch (SQLException e) {
            e.printStackTrace();}
        return generatedId;}
    public boolean updateOrder(Order order) {
        String sql = "UPDATE orders SET customer_id = ?, date = ?, amount = ?, payment_method = ? WHERE id = ?";
        boolean updated = false;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getCustomerId());
            pstmt.setString(2, order.getDate());
            pstmt.setDouble(3, order.getAmount());
            pstmt.setString(4, order.getPaymentMethod());
            pstmt.setInt(5, order.getId());
            int affectedRows = pstmt.executeUpdate();
            updated = affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();}
        return updated;}
    public boolean deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        boolean deleted = false;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            deleted = affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();}
        return deleted;}
    public boolean deleteOrdersByCustomerId(int customerId) {
        String sql = "DELETE FROM orders WHERE customer_id = ?";
        boolean deleted = false;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
            deleted = true;
        } catch (SQLException e) {
            e.printStackTrace();}
        return deleted;}}
