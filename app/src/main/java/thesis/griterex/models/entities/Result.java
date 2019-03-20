package thesis.griterex.models.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Result {

    @Expose
    private Boolean error = false;
    @Expose
    private String message;
    @Expose
    private User user;
    @Expose
    private Product product;
    @Expose
    private Order order;
    @Expose
    private List<User> users;
    @Expose
    private List<Product> products;
    @Expose
    private List<Order> orders;

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public Product getProduct() {
        return product;
    }

    public Order getOrder() {
        return order;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
