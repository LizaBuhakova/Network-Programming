package ua.edu.lab.servlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.edu.lab.model.Customer;
import ua.edu.lab.model.Order;
import ua.edu.lab.repository.CustomerRepository;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
@WebServlet(name = "CustomerServlet", urlPatterns = {"/customer/*"})
public class CustomerServlet extends HttpServlet {
    private final CustomerRepository repository = new CustomerRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void init() throws ServletException {
        try {
            repository.load(getServletContext());
        } catch (IOException e) {
            throw new ServletException("Cannot load customer data", e);}}
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = splitPath(req.getPathInfo());
        if (parts.length == 1 && "all".equals(parts[0])) {
            writeJson(resp, HttpServletResponse.SC_OK, repository.getAllCustomers());
            return;}
        if (parts.length == 3 && "order".equals(parts[1]) && "all".equals(parts[2])) {
            int customerId = parseInt(parts[0]);
            if (customerId < 0) {
                writeText(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid customer id");
                return;}
            Optional<Customer> customer = repository.findCustomerById(customerId);
            if (!customer.isPresent()) {
                writeText(resp, HttpServletResponse.SC_NOT_FOUND, "Customer not found");
                return;}
            String accept = req.getHeader("Accept");
            if (accept != null && accept.contains("application/json")) {
                writeJson(resp, HttpServletResponse.SC_OK, customer.get().getOrders());
                return;}
            writeOrdersHtml(resp, customer.get());
            return;}
        if (parts.length == 3 && "order".equals(parts[1])) {
            int customerId = parseInt(parts[0]);
            int orderId = parseInt(parts[2]);
            if (customerId < 0 || orderId < 0) {
                writeText(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid id in path");
                return;}
            Optional<Order> order = repository.findOrderById(customerId, orderId);
            if (!order.isPresent()) {
                writeText(resp, HttpServletResponse.SC_NOT_FOUND, "Order not found");
                return;}
            writeJson(resp, HttpServletResponse.SC_OK, order.get());
            return;}
        writeText(resp, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint");}
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = splitPath(req.getPathInfo());
        if (parts.length == 3 && "order".equals(parts[1]) && "new".equals(parts[2])) {
            int customerId = parseInt(parts[0]);
            if (customerId < 0) {
                writeText(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid customer id");
                return;}
            Order newOrder = objectMapper.readValue(req.getInputStream(), Order.class);
            if (newOrder.getDate() == null || newOrder.getPaymentMethod() == null || newOrder.getAmount() <= 0) {
                writeText(resp, HttpServletResponse.SC_BAD_REQUEST, "Order must have date, paymentMethod, amount>0");
                return;}
            Optional<Order> added = repository.addOrder(customerId, newOrder);
            if (!added.isPresent()) {
                writeText(resp, HttpServletResponse.SC_NOT_FOUND, "Customer not found");
                return;}
            writeJson(resp, HttpServletResponse.SC_CREATED, added.get());
            return;}
        writeText(resp, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint");}
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] parts = splitPath(req.getPathInfo());
        if (parts.length == 4 && "order".equals(parts[1]) && "edit".equals(parts[3])) {
            int customerId = parseInt(parts[0]);
            int orderId = parseInt(parts[2]);
            if (customerId < 0 || orderId < 0) {
                writeText(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid id in path");
                return;}
            Order patch = objectMapper.readValue(req.getInputStream(), Order.class);
            Optional<Order> updated = repository.updateOrder(customerId, orderId, patch);
            if (!updated.isPresent()) {
                writeText(resp, HttpServletResponse.SC_NOT_FOUND, "Order not found");
                return;}
            writeJson(resp, HttpServletResponse.SC_OK, updated.get());
            return;}
        writeText(resp, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint");}
    private String[] splitPath(String pathInfo) {
        if (pathInfo == null || pathInfo.trim().isEmpty() || "/".equals(pathInfo)) {
            return new String[0];}
        String clean = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        return clean.split("/");}
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;}}
    private void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(resp.getOutputStream(), body);}
    private void writeText(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));}
    private void writeOrdersHtml(HttpServletResponse resp, Customer customer) throws IOException {
        List<Order> orders = customer.getOrders();
        double maxAmount = orders.stream().map(Order::getAmount).max(Comparator.naturalOrder()).orElse(0.0);
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Orders</title>")
                .append("<style>body{font-family:Arial,sans-serif;padding:20px;}table{border-collapse:collapse;width:100%;}")
                .append("th,td{border:1px solid #ccc;padding:8px;text-align:left;}th{background:#f4f4f4;}</style>")
                .append("</head><body>");
        html.append("<h2>Інформація про замовника</h2>")
                .append("<p><b>ПІБ:</b> ").append(escape(customer.getFullName())).append("</p>")
                .append("<p><b>Телефон:</b> ").append(escape(customer.getPhone())).append("</p>")
                .append("<p><b>Адреса:</b> ").append(escape(customer.getAddress())).append("</p>")
                .append("<p><b>Фото:</b> ").append(escape(customer.getPhoto())).append("</p>");
        html.append("<h3>Замовлення</h3>")
                .append("<table><tr><th>Дата</th><th>Сума</th><th>Спосіб оплати</th></tr>");
        for (Order order : orders) {
            boolean isMax = Double.compare(order.getAmount(), maxAmount) == 0;
            String rowStart = isMax ? "<tr style='font-weight:bold;'>" : "<tr>";
            html.append(rowStart)
                    .append("<td>").append(escape(order.getDate())).append("</td>")
                    .append("<td>").append(order.getAmount()).append("</td>")
                    .append("<td>").append(escape(order.getPaymentMethod())).append("</td>")
                    .append("</tr>");}
        html.append("</table></body></html>");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/html;charset=UTF-8");
        resp.getOutputStream().write(html.toString().getBytes(StandardCharsets.UTF_8));}
    private String escape(String value) {
        if (value == null) {
            return "";}
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");}}