package thesis.griterex.models.entities;

import com.google.gson.annotations.Expose;

public class Category {

    @Expose
    private int id;
    @Expose
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
