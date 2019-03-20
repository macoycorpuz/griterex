package thesis.griterex.models;

import java.util.ArrayList;
import java.util.List;

import thesis.griterex.models.entities.Order;
import thesis.griterex.models.entities.Product;
import thesis.griterex.models.entities.User;

public class CenterRepo {

    private static CenterRepo centerRepository;

    private List<Order> orderList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    private List<Product> orderProductList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();

    public static CenterRepo getCenterRepo() {

        if (null == centerRepository) {
            centerRepository = new CenterRepo();
        }
        return centerRepository;
    }

    public static CenterRepo getCenterRepository() {
        return centerRepository;
    }

    public static void setCenterRepository(CenterRepo centerRepository) {
        CenterRepo.centerRepository = centerRepository;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public List<Product> getOrderProductList() {
        return orderProductList;
    }

    public void setOrderProductList(List<Product> orderProductList) {
        this.orderProductList = orderProductList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
