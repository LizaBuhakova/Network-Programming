package com.example.utils;
import com.example.dao.CustomerDAO;
import com.example.dao.OrderDAO;
import com.example.model.Customer;
import com.example.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
public class JsonDataLoader {
    public static void loadDataFromJson(String filePath) {
        Gson gson = new Gson();
        CustomerDAO customerDAO = new CustomerDAO();
        OrderDAO orderDAO = new OrderDAO();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Type listType = new TypeToken<List<Customer>>(){}.getType();
            List<Customer> customers = gson.fromJson(reader, listType);
            if (customers != null) {
                for (Customer customer : customers) {
                    int customerId = customerDAO.addCustomer(customer);
                    if (customer.getOrders() != null) {
                        for (Order order : customer.getOrders()) {
                            order.setCustomerId(customerId);
                            orderDAO.addOrder(order);}}}
                System.out.println("Successfully loaded " + customers.size() + " customers from JSON");}
        } catch (IOException e) {
            System.err.println("Error loading JSON data: " + e.getMessage());
            e.printStackTrace();}}
    public static List<Customer> readCustomersFromJson(String filePath) {
        Gson gson = new Gson();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Type listType = new TypeToken<List<Customer>>(){}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            e.printStackTrace();
            return null;}}}
