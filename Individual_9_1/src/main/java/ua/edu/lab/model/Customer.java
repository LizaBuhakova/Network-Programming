package ua.edu.lab.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    @JacksonXmlProperty(isAttribute = true)
    private Integer id;
    private String fullName;
    private String phone;
    private String address;
    private String photo;
    @JacksonXmlElementWrapper(localName = "orders")
    @JacksonXmlProperty(localName = "order")
    private List<Order> orders = new ArrayList<>();
    public Integer getId() {
        return id;}
    public void setId(Integer id) {
        this.id = id;}
    public String getFullName() {
        return fullName;}
    public void setFullName(String fullName) {
        this.fullName = fullName;}
    public String getPhone() {
        return phone;}
    public void setPhone(String phone) {
        this.phone = phone;}
    public String getAddress() {
        return address;}
    public void setAddress(String address) {
        this.address = address;}
    public String getPhoto() {
        return photo;}
    public void setPhoto(String photo) {
        this.photo = photo;}
    public List<Order> getOrders() {
        return orders;}
    public void setOrders(List<Order> orders) {
        this.orders = orders;}}
        