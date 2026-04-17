package com.example.model;
import java.util.List;
public class Customer {
    private int id;
    private String fullName;
    private String phone;
    private String address;
    private String photo;
    private List<Order> orders;
    public Customer() {}
    public Customer(int id, String fullName, String phone, String address, String photo, List<Order> orders) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.photo = photo;
        this.orders = orders;}
    public int getId() {
        return id;}
    public void setId(int id) {
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
        this.orders = orders;}
    public double getMaxOrderAmount() {
        if (orders == null || orders.isEmpty()) {
            return 0;}
        double max = 0;
        for (Order order : orders) {
            if (order.getAmount() > max) {
                max = order.getAmount();}}
        return max;}
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", photo='" + photo + '\'' +
                ", orders=" + orders +
                '}';}}
