package thesis.griterex.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import thesis.griterex.R;
import thesis.griterex.utils.SharedPrefManager;
import thesis.griterex.utils.Tags;
import thesis.griterex.utils.Utils;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Home Activity", "Started");
        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.inflateMenu(R.menu.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        showAccount();
    }

    private void showAccount() {
        boolean isLoggedIn = SharedPrefManager.getInstance().isLoggedIn(this);
        int accountId = SharedPrefManager.getInstance().getUser(this).getAccount_id();
        Log.d("Home Activity", "Account Id: " +  String.valueOf(accountId));
        if(isLoggedIn)
            switch(accountId) {
                case Tags.SUPPLIER:
                    finish();
                    startActivity(new Intent(this, SupplierActivity.class));
                    Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
                    break;
                case Tags.ADMIN:
                    finish();
                    startActivity(new Intent(this, AdminActivity.class));
                    Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
                    break;
            }
    }

    //region Navigation Listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
                    return true;
                case R.id.navigation_orders:
                    Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.ORDERS_FRAGMENT);
                    return true;
                case R.id.navigation_account:
                    Utils.switchContent(HomeActivity.this, R.id.fragContainer, Tags.ACCOUNT_FRAGMENT);
                    return true;
            }
            return false;
        }
    };
    //endregion
}
