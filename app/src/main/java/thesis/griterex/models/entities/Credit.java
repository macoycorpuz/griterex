package thesis.griterex.models.entities;

import com.google.gson.annotations.Expose;

public class Credit {

    @Expose
    private int id;
    @Expose
    private String number;
    @Expose
    private String expiry;
    @Expose
    private int csv;

    public Credit(String number, String expiry, int csv) {
        this.number = number;
        this.expiry = expiry;
        this.csv = csv;
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getExpiry() {
        return expiry;
    }

    public int getCsv() {
        return csv;
    }
}
