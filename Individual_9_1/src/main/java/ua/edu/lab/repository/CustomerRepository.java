package ua.edu.lab.repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ua.edu.lab.model.Customer;
import ua.edu.lab.model.CustomersData;
import ua.edu.lab.model.Order;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
public class CustomerRepository {
    private static final String JSON_DATA_PATH = "/WEB-INF/data/customers.json";
    private static final String XML_DATA_PATH = "/WEB-INF/data/customers.xml";
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final XmlMapper xmlMapper = new XmlMapper();
    private final List<Customer> customers = new ArrayList<>();
    public synchronized void load(ServletContext context) throws IOException {
        CustomersData loaded = tryLoadJson(context);
        if (loaded == null) {
            loaded = tryLoadXml(context);}
        customers.clear();
        if (loaded != null && loaded.getCustomers() != null) {
            customers.addAll(loaded.getCustomers());}}
    private CustomersData tryLoadJson(ServletContext context) throws IOException {
        try (InputStream is = context.getResourceAsStream(JSON_DATA_PATH)) {
            if (is == null) {
                return null;}
            return jsonMapper.readValue(is, CustomersData.class);}}
    private CustomersData tryLoadXml(ServletContext context) throws IOException {
        try (InputStream is = context.getResourceAsStream(XML_DATA_PATH)) {
            if (is == null) {
                return null;}
            return xmlMapper.readValue(is, CustomersData.class);}}
    public synchronized List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);}
    public synchronized Optional<Customer> findCustomerById(int customerId) {
        return customers.stream().filter(c -> c.getId() != null && c.getId() == customerId).findFirst();}
    public synchronized Optional<Order> findOrderById(int customerId, int orderId) {
        Optional<Customer> customer = findCustomerById(customerId);
        if (!customer.isPresent()) {
            return Optional.empty();}
        return customer.get().getOrders().stream()
                .filter(o -> o.getId() != null && o.getId() == orderId)
                .findFirst();}
    public synchronized Optional<Order> addOrder(int customerId, Order newOrder) {
        Optional<Customer> customerOptional = findCustomerById(customerId);
        if (!customerOptional.isPresent()) {
            return Optional.empty();}
        Customer customer = customerOptional.get();
        int nextId = customer.getOrders().stream()
                .map(Order::getId)
                .filter(id -> id != null)
                .max(Comparator.naturalOrder())
                .orElse(0) + 1;
        newOrder.setId(nextId);
        customer.getOrders().add(newOrder);
        return Optional.of(newOrder);}
    public synchronized Optional<Order> updateOrder(int customerId, int orderId, Order patch) {
        Optional<Order> orderOptional = findOrderById(customerId, orderId);
        if (!orderOptional.isPresent()) {
            return Optional.empty();}
        Order order = orderOptional.get();
        if (patch.getDate() != null) {
            order.setDate(patch.getDate());}
        if (patch.getPaymentMethod() != null) {
            order.setPaymentMethod(patch.getPaymentMethod());}
        if (patch.getAmount() > 0) {
            order.setAmount(patch.getAmount());}
        return Optional.of(order);}}