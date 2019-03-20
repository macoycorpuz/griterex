package thesis.griterex.models.entities;

import com.google.gson.annotations.Expose;

public class User {
    @Expose
    private int id;
    @Expose
    private String name;
    @Expose
    private String email;
    @Expose
    private String password;
    @Expose
    private String number;
    @Expose
    private String address;
    @Expose
    private double lat;
    @Expose
    private double lng;
    @Expose
    private int account_id;
    @Expose
    private int credit_id;
    @Expose
    private Credit creditCard;

    public User(int id, String name, String email, String number, String address, double lat, double lng, int account_id, int credit_id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.account_id = account_id;
        this.credit_id = credit_id;
    }

    public User(String name, String email, String password, String number, String address, double lat, double lng) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.number = number;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getAccount_id() {
        return account_id;
    }

    public int getCredit_id() {
        return credit_id;
    }

    public Credit getCreditCard() {
        return creditCard;
    }
}
