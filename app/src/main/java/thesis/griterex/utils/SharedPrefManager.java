package thesis.griterex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import thesis.griterex.models.entities.User;

public class SharedPrefManager {



    private static SharedPrefManager sharedPrefManager = new SharedPrefManager();

    public static SharedPrefManager getInstance() {
        return sharedPrefManager;
    }

    public User getUser(Context mCtx) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Tags.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(Tags.KEY_USER_ID, 0),
                sharedPreferences.getString(Tags.KEY_USER_NAME, null),
                sharedPreferences.getString(Tags.KEY_USER_EMAIL, null),
                sharedPreferences.getString(Tags.KEY_USER_NUMBER, null),
                sharedPreferences.getString(Tags.KEY_USER_ADDRESS, null),
                Double.valueOf(sharedPreferences.getString(Tags.KEY_USER_LAT, "0")),
                Double.valueOf(sharedPreferences.getString(Tags.KEY_USER_LNG, "0")),
                sharedPreferences.getInt(Tags.KEY_USER_ACCOUNT_ID, 0),
                sharedPreferences.getInt(Tags.KEY_USER_CREDIT_CARD_ID, 0)
        );
    }

    public boolean isLoggedIn(Context mCtx) {
        SharedPreferences sharedP = mCtx.getSharedPreferences(Tags.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return (sharedP.getInt(Tags.KEY_USER_ID, 0) != 0 );
    }

    public void login(Context mCtx, User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Tags.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Tags.KEY_USER_ID, user.getId());
        editor.putInt(Tags.KEY_USER_ACCOUNT_ID, user.getAccount_id());
        editor.putString(Tags.KEY_USER_NAME, user.getName());
        editor.putString(Tags.KEY_USER_EMAIL, user.getEmail());
        editor.putString(Tags.KEY_USER_NUMBER, user.getNumber());
        editor.putString(Tags.KEY_USER_ADDRESS, user.getAddress());
        editor.putString(Tags.KEY_USER_LAT, Double.toString(user.getLat()));
        editor.putString(Tags.KEY_USER_LNG, Double.toString(user.getLng()));
        editor.putInt(Tags.KEY_USER_CREDIT_CARD_ID, user.getCredit_id());
        editor.apply();
    }

    public void logout(Context mCtx) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Tags.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}