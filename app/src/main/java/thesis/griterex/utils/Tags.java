package thesis.griterex.utils;

public interface Tags {

    //region Fragments
    String PRODUCTS_FRAGMENT = "Products";
    String ORDERS_FRAGMENT = "Orders";
    String ACCOUNT_FRAGMENT = "Account";
    String PAYMENT_FRAGMENT = "Payment";
    String SELL_FRAGMENT = "Sell";
    String USERS_FRAGMENT = "Users";

    String PRODUCT_DETAILS_FRAGMENT = "Product Details";
    String ORDER_DETAILS_FRAGMENT = "Order Details";
    String USER_DETAILS_FRAGMENT = "User Details";
    //endregion

    //region Shared Preferences
    String SHARED_PREF_NAME = "gritterex";
    String KEY_USER_ID = "userId";
    String KEY_USER_ACCOUNT_ID = "accountId";
    String KEY_USER_NAME = "name";
    String KEY_USER_EMAIL = "email";
    String KEY_USER_NUMBER = "number";
    String KEY_USER_ADDRESS = "address";
    String KEY_USER_LAT = "lat";
    String KEY_USER_LNG = "lng";
    String KEY_USER_CREDIT_CARD_ID = "creditCardId";
    //endregion


    //region Database Constants
    int USER = 1;
    int SUPPLIER = 2;
    int ADMIN = 3;

    int GAS = 1;
    int WATER = 2;
    int RICE = 3;

    String DELIVERED = "delivered";
    String PROCESSING = "processing";
    String CANCEL = "cancel";
    //endregion

}
