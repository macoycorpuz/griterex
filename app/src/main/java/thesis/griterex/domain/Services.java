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

    String MAIN_URL = "http://gritterexthesis.000webhostapp.com/";
//    String MAIN_URL = "http://192.168.0.11/griterex/public/";

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
            @Field("expiry") String expiry,
            @Field("csv") int csv);

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
    Call<Result> setProduct(@Part("name") RequestBody name,
                            @Part("description") RequestBody description,
                            @Part("price") RequestBody price,
                            @Part("user_id") RequestBody user_id,
                            @Part("category_id") RequestBody category_id,
                            @Part("image\"; filename=\"image.jpg\" ") RequestBody image);

    @GET("products")
    Call<Result> getProducts();

    @GET("products/categories/{id}")
    Call<Result> getProductsByCategory(@Path("id") int id);

    @GET("products/categories/{category_id}/suppliers/{user_id}")
    Call<Result> getProductsBySupplier(
            @Path("category_id") int category_id,
            @Path("user_id") int user_id);


    @GET("products/categories/{category_id}/name/{name}")
    Call<Result> getProductsByName(
            @Path("category_id") int category_id,
            @Path("name") String name);


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
            @Field("status") String status,
            @Field("active") boolean active,
            @Field("total") double total,
            @Field("cash") double cash,
            @Field("product_id") int product_id,
            @Field("user_id") int user_id,
            @Field("number") String number,
            @Field("expiry") String expiry,
            @Field("csv") int csv);

    @FormUrlEncoded
    @POST("orders/active")
    Call<Result> getOrders(
            @Field("account_id") int account_id,
            @Field("user_id") int user_id,
            @Field("active") boolean active);

    @GET("orders/{id}")
    Call<Result> getOrder(@Path("id") int id);

    @FormUrlEncoded
    @POST("orders/update")
    Call<Result> updateOrderStatus(
            @Field("id") int id,
            @Field("status") String status,
            @Field("active") boolean active);

    @GET("orders/delete/{id}")
    Call<Result> deleteOrder(@Path("id") int id);
    //endregion

}
