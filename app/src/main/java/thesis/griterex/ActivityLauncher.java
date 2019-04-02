package thesis.griterex;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import thesis.griterex.utils.SharedPrefManager;
import thesis.griterex.utils.Tags;
import thesis.griterex.views.activity.AdminActivity;
import thesis.griterex.views.activity.HomeActivity;
import thesis.griterex.views.activity.SupplierActivity;

public class ActivityLauncher extends AppCompatActivity {

    String TAG = "Activity Launcher";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: Hello");
        boolean isLoggedIn = SharedPrefManager.getInstance().isLoggedIn(this);

        if(!isLoggedIn) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            Log.d(TAG, "onCreate: Not LoggedIn!");
        }

        int accountId = SharedPrefManager.getInstance().getUser(this).getAccount_id();
        Log.d(TAG, "onCreate: LoggedIn " + accountId);
        switch(accountId) {
            case Tags.USER:
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
            case Tags.SUPPLIER:
                startActivity(new Intent(this, SupplierActivity.class));
                finish();
                break;
            case Tags.ADMIN:
                startActivity(new Intent(this, AdminActivity.class));
                finish();
                break;
        }

        checkPermissions();
    }

    private void checkPermissions() {
        int permissionCheck = this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
        permissionCheck += this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
        permissionCheck += this.checkSelfPermission("Manifest.permission.INTERNET");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_NETWORK_STATE");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE}, 1001); //Any number
        }
    }
}
