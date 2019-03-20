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
    private int product_id;
    @Expose
    private int buyer_id;
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

    public int getBuyer_id() {
        return buyer_id;
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
}
