package thesis.griterex.models.entities;

import com.google.gson.annotations.Expose;

public class Order {

    @Expose
    private int id;
    @Expose
    private int quantity;
    @Expose
    private String status;
    @Expose
    private boolean active;
    @Expose
    private double total;
    @Expose
    private double cash;
    @Expose
    private int product_id;
    @Expose
    private int user_id;
    @Expose
    private int credit_id;
    @Expose
    private String created_at;
    @Expose
    private Product product;
    @Expose
    private User user;
    @Expose
    private Credit credit;

    public Order(int quantity, String status, boolean active, double total, double cash, int product_id, int user_id) {
        this.quantity = quantity;
        this.status = status;
        this.active = active;
        this.total = total;
        this.cash = cash;
        this.product_id = product_id;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public boolean isActive() {
        return active;
    }

    public double getTotal() {
        return total;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getCredit_id() {
        return credit_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Product getProduct() {
        return product;
    }

    public User getUser() {
        return user;
    }

    public Credit getCredit() {
        return credit;
    }

    public double getCash() {
        return cash;
    }
}
