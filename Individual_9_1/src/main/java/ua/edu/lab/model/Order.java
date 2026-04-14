package ua.edu.lab.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @JacksonXmlProperty(isAttribute = true)
    private Integer id;
    private String date;
    private double amount;
    private String paymentMethod;
    public Integer getId() {
        return id;}
    public void setId(Integer id) {
        this.id = id;}
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
        this.paymentMethod = paymentMethod;}}