package com.example.model;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Order {
    private int id;
    private int customerId;
    private String date;
    private double amount;
    private String paymentMethod;
    public Order() {}
    public Order(int id, int customerId, String date, double amount, String paymentMethod) {
        this.id = id;
        this.customerId = customerId;
        this.date = date;
        this.amount = amount;
        this.paymentMethod = paymentMethod;}
    public int getId() {
        return id;}
    public void setId(int id) {
        this.id = id;}
    public int getCustomerId() {
        return customerId;}
    public void setCustomerId(int customerId) {
        this.customerId = customerId;}
    public String getDate() {
        return date;}
    public void setDate(String date) {
        this.date = date;}
    public double getAmount() {
        return amount;}
    public void setAmount(double amount) {
        this.amount = amount;}
    public String getPaymentMethod() {
        return paymentMethod;}
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;}
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", date='" + date + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';}}
