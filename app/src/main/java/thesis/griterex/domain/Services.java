package thesis.griterex.domain;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import thesis.griterex.models.entities.Result;
import thesis.griterex.models.entities.User;

public interface Services {

//    String MAIN_URL = "http://gritterexthesis.000webhostapp.com/api/";
    String MAIN_URL = "http://192.168.0.11/griterex/public/";

    //region Users
    @FormUrlEncoded
    @POST("users/login")
    Call<Result> login(
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("users")
    Call<Result> setUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("number") String number,
            @Field("address")String address,
            @Field("lat")double lat,
            @Field("lng")double lng,
            @Field("account_id") int account_id,
            @Field("credit_number") String credit_number,
            @Field("csv") int csv,
            @Field("expiry") String expiry);

    @GET("users")
    Call<Result> getUsers();

    @GET("users/accounts/{id}")
    Call<Result> getUsersByAccount(@Path("id") int id);

    @GET("users/{id}")
    Call<Result> getUser(@Path("id") int id);

    @GET("users/delete/{id}")
    Call<Result> deleteUser(@Path("id") int id);
    //endregion

    //region Products
    @Multipart
    @POST("products")
    Call<Result> setProduct(@Part("name") RequestBody productName,
                            @Part("description") RequestBody description,
                            @Part("price") RequestBody price,
                            @Part("supplier_id") RequestBody sellerId,
                            @Part("category_id") RequestBody categoryId,
                            @Part("image\"; filename=\"image.jpg\" ") RequestBody productImage);

    @GET("products")
    Call<Result> getProducts();

    @GET("products/categories/{id}")
    Call<Result> getProductsByCategory(@Path("id") int id);

    @GET("products/{id}")
    Call<Result> getProduct(@Path("id") int id);

    @GET("products/delete/{id}")
    Call<Result> deleteProduct(@Path("id") int id);
    //endregion

    //region Orders
    @FormUrlEncoded
    @POST("orders")
    Call<Result> setOrder(
            @Field("quantity") int quantity,
            @Field("total") double total,
            @Field("status") String status,
            @Field("active") boolean active,
            @Field("product_id") int product_id,
            @Field("buyer_id") int buyer_id,
            @Field("credit_id") int credit_id);


    @GET("orders/accounts/{account_id}/users/{user_id}/active/{active}")
    Call<Result> getOrders(
            @Path("account_id") int account_id,
            @Path("user_id") int user_id,
            @Path("active") boolean active);

    @GET("orders/{id}")
    Call<Result> getOrder(@Path("id") int id);

    @FormUrlEncoded
    @GET("orders/status/{id}")
    Call<Result> updateOrderStatus(
            @Path("id") int id,
            @Field("status") String status);

    @GET("orders/delete/{id}")
    Call<Result> deleteOrder(@Path("id") int id);
    //endregion

}
