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

public class SupplierActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.inflateMenu(R.menu.navigation_supplier);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Utils.switchContent(SupplierActivity.this, R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    //region Navigation Listener
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_supplier_products:
                    Utils.switchContent(SupplierActivity.this, R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
                    return true;
                case R.id.navigation__supplier_sell:
                    Utils.switchContent(SupplierActivity.this, R.id.fragContainer, Tags.SELL_FRAGMENT);
                    return true;
                case R.id.navigation__supplier_orders:
                    Utils.switchContent(SupplierActivity.this, R.id.fragContainer, Tags.ORDERS_FRAGMENT);
                    return true;
                case R.id.navigation_supplier_account:
                    Utils.switchContent(SupplierActivity.this, R.id.fragContainer, Tags.ACCOUNT_FRAGMENT);
                    return true;
            }
            return false;
        }
    };
    //endregion
}
