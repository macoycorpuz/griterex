package thesis.griterex.models.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {
    @Expose
    private int id;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private double price;
    @Expose
    private String url;
    @Expose
    private int user_id;
    @Expose
    private int category_id;
    @Expose
    private Category category;
    @Expose
    private User user;

    public Product(String name, String description, double price, int user_id, int category_id) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.user_id = user_id;
        this.category_id = category_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public Category getCategory() {
        return category;
    }

    public User getUser() {
        return user;
    }
}
