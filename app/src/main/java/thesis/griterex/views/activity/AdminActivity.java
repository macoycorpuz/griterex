package thesis.griterex.views.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import thesis.griterex.R;
import thesis.griterex.utils.Tags;
import thesis.griterex.utils.Utils;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.inflateMenu(R.menu.navigation_admin);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Utils.switchContent(AdminActivity.this, R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    //region Navigation Listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_admin_users:
                    Utils.setAccountId(Tags.USER);
                    Utils.switchContent(AdminActivity.this, R.id.fragContainer, Tags.USERS_FRAGMENT);
                    return true;
                case R.id.navigation__admin_suppliers:
                    Utils.setAccountId(Tags.SUPPLIER);
                    Utils.switchContent(AdminActivity.this, R.id.fragContainer, Tags.USERS_FRAGMENT);
                    return true;
                case R.id.navigation_admin_products:
                    Utils.switchContent(AdminActivity.this, R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
                    return true;
                case R.id.navigation_admin_account:
                    Utils.switchContent(AdminActivity.this, R.id.fragContainer, Tags.ACCOUNT_FRAGMENT);
                    return true;
            }
            return false;
        }
    };
    //endregion
}
