package ua.edu.lab.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "customers")
public class CustomersData {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "customer")
    private List<Customer> customers = new ArrayList<>();
    public List<Customer> getCustomers() {
        return customers;}
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;}}