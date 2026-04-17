package com.example.servlet;
import com.example.dao.CustomerDAO;
import com.example.dao.OrderDAO;
import com.example.model.Customer;
import com.example.model.Order;
import com.google.gson.Gson;
import com.example.utils.JsonDataLoader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
@WebServlet("/api/customers/*")
public class CustomerServlet extends HttpServlet {
    private CustomerDAO customerDAO = new CustomerDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private Gson gson = new Gson();
    @Override
    public void init() throws ServletException {
        super.init();
        com.example.database.DatabaseManager.initializeDatabase(); 
        String jsonFilePath = getServletContext().getRealPath("/data/customers.json");
        if (jsonFilePath != null) {
            JsonDataLoader.loadDataFromJson(jsonFilePath);}}
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Customer> customers = customerDAO.getAllCustomers();
            for (Customer customer : customers) {
                List<Order> orders = orderDAO.getOrdersByCustomerId(customer.getId());
                customer.setOrders(orders);} 
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(customers));
            out.flush();
        } else {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length > 1) {
                int customerId = Integer.parseInt(pathParts[1]);
                Customer customer = customerDAO.getCustomerById(customerId);
                if (customer != null) {
                    List<Order> orders = orderDAO.getOrdersByCustomerId(customerId);
                    customer.setOrders(orders);
                    PrintWriter out = response.getWriter();
                    out.print(gson.toJson(customer));
                    out.flush();
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print("{\"error\":\"Customer not found\"}");}}}}
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Customer customer = gson.fromJson(request.getReader(), Customer.class);
        int generatedId = customerDAO.addCustomer(customer);
        if (customer.getOrders() != null) {
            for (Order order : customer.getOrders()) {
                order.setCustomerId(generatedId);
                orderDAO.addOrder(order);}}
        response.setStatus(HttpServletResponse.SC_CREATED);
        PrintWriter out = response.getWriter();
        out.print("{\"id\":" + generatedId + ",\"message\":\"Customer created successfully\"}");
        out.flush();}
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.split("/").length > 1) {
            int customerId = Integer.parseInt(pathInfo.split("/")[1]);
            Customer customer = gson.fromJson(request.getReader(), Customer.class);
            customer.setId(customerId);
            boolean updated = customerDAO.updateCustomer(customer);
            PrintWriter out = response.getWriter();
            if (updated) {
                out.print("{\"message\":\"Customer updated successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Customer not found\"}");}
            out.flush();}}
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.split("/").length > 1) {
            int customerId = Integer.parseInt(pathInfo.split("/")[1]);
            orderDAO.deleteOrdersByCustomerId(customerId);
            boolean deleted = customerDAO.deleteCustomer(customerId);
            PrintWriter out = response.getWriter();
            if (deleted) {
                out.print("{\"message\":\"Customer deleted successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Customer not found\"}");}
            out.flush();}}}
