package thesis.griterex.views.activity;
import thesis.griterex.utils.Tags;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thesis.griterex.R;
import thesis.griterex.domain.Api;
import thesis.griterex.models.entities.Result;
import thesis.griterex.utils.SharedPrefManager;
import thesis.griterex.utils.Utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private TextView mError;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.txtEmail);
        mPassword = findViewById(R.id.txtPassword);
        mError = findViewById(R.id.txtLoginError);

        Button mLoginButton = findViewById(R.id.btnLogin);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });

        Button mSignUpButton = findViewById(R.id.btnRegister);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void clearViews() {
        mError.setVisibility(View.GONE);
        mEmail.setError(null);
        mPassword.setError(null);
    }

    private void authenticate() {
        clearViews();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();

        if (!Utils.isEmptyFields(email, password)) {
            mError.setText(R.string.error_login);
            mError.setVisibility(View.VISIBLE);
        } else {
            Utils.hideKeyboard(this);
            fetchLogin(email, password);
        }
    }

    private void fetchLogin(String email, String password) {
        pDialog = Utils.showProgressDialog(this, "Logging in...");
        Api.getInstance().getServices().login(email, password).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if(!response.isSuccessful())
                        throw new Exception(response.errorBody().toString());
                    if(response.body().getError())
                        throw new Exception(response.body().getMessage());
                    login(response.body());
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, pDialog);
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login(Result result) {
        SharedPrefManager.getInstance().login(getApplicationContext(), result.getUser());
        switch (result.getUser().getAccount_id()) {
            case Tags.USER:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                break;
            case Tags.SUPPLIER:
                startActivity(new Intent(getApplicationContext(), SupplierActivity.class));
                break;
            case Tags.ADMIN:
                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                break;
            default:
                break;
        }
        finish();
    }
}

