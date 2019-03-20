package thesis.griterex.views.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thesis.griterex.R;
import thesis.griterex.domain.Api;
import thesis.griterex.models.entities.CreditCard;
import thesis.griterex.models.entities.Result;
import thesis.griterex.models.entities.User;
import thesis.griterex.utils.Tags;
import thesis.griterex.utils.Utils;

//TODO: Add credit card
public class SignUpActivity extends AppCompatActivity {

    //region Attributes
    View mViewCredit;
    EditText mAccount, mName, mEmail, mPassword, mConfirmPassword, mNumber, mAddress, mLocation;
    EditText mCardNumber, mExpiryDate, mCsv;
    ProgressDialog pDialog;
    TextView mError;

    int PLACE_PICKER_REQUEST = 1;
    int accountId;
    LatLng location;
    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mError = findViewById(R.id.txtSignUpError);
        mAccount = findViewById(R.id.txtSignUpAccount);
        mName = findViewById(R.id.txtSignUpName);
        mEmail = findViewById(R.id.txtSignUpEmail);
        mPassword = findViewById(R.id.txtSignUpPassword);
        mConfirmPassword = findViewById(R.id.txtSignUpConfirmPassword);
        mNumber = findViewById(R.id.txtSignUpMobileNumber);
        mAddress = findViewById(R.id.txtSignUpAddress);
        mLocation = findViewById(R.id.txtSignUpLocation);

        mViewCredit = findViewById(R.id.formSupplierCreditCard);
        mCardNumber = mViewCredit.findViewById(R.id.txtCardNumber);
        mExpiryDate = mViewCredit.findViewById(R.id.txtExpiryDate);
        mCsv = mViewCredit.findViewById(R.id.txtCsv);

        mAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAccount();
            }
        });
        mAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { if(hasFocus) selectAccount(); }
        });
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDestination();
            }
        });
        mLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { if(hasFocus) selectDestination(); }
        });
        Button mSignUpButton = findViewById(R.id.btnSignUp);
        Button mSignInButton = findViewById(R.id.btnSignIn);

        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(this, data);
                location = place.getLatLng();
                mLocation.setText(place.getAddress().toString());
            }
        }
    }

    private void selectDestination() {
        pDialog = Utils.showProgressDialog(this, "Loading...");
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = builder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
            Utils.dismissProgressDialog(pDialog);
        }
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e){
            e.printStackTrace();
        }
    }

    private void selectAccount() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Choose Account Type");
        final String[] accountType = {"User", "Supplier"};
        alert.setItems(accountType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        Log.d("Click", "User Clicked");
                        accountId = Tags.USER;
                        mViewCredit.setVisibility(View.GONE);
                        mAccount.setText(accountType[position]);
                        break;
                    case 1:
                        Log.d("Click", "Supplier Clicked");
                        accountId = Tags.SUPPLIER;
                        mViewCredit.setVisibility(View.VISIBLE);
                        mAccount.setText(accountType[position]);
                        break;
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void clearViews() {
        mError.setVisibility(View.GONE);
        mEmail.setError(null);
        mPassword.setError(null);
    }

    private void authenticate() {
        clearViews();

        User user = new User(
                accountId,
                mName.getText().toString(),
                mEmail.getText().toString(),
                mPassword.getText().toString(),
                mNumber.getText().toString(),
                mAddress.getText().toString(),
                location.latitude,
                location.longitude
        );
        CreditCard cc = new CreditCard(
                mCardNumber.getText().toString(),
                mExpiryDate.getText().toString(),
                mCsv.getText().toString()
        );

        if (!Utils.isEmptyFields(user.getName(), user.getEmail(), user.getPassword(), mConfirmPassword.getText().toString(), user.getNumber(), user.getAddress())) {
            mError.setText(R.string.error_sign_up);
            mError.setVisibility(View.VISIBLE);
        } else if (!Utils.isEmptyFields(cc.getCardNumber(), cc.getExpiryDate(), cc.getCsv()) && accountId == Tags.SUPPLIER) {
            mError.setError(getString(R.string.error_sign_up));
            mError.setVisibility(View.VISIBLE);
        } else if (!Utils.isEmailValid(user.getEmail())) {
            mError.setError(getString(R.string.error_invalid_email));
            mError.requestFocus();
        } else if (!Utils.isPasswordValid(user.getPassword()) || !user.getPassword().equals(mConfirmPassword.getText().toString())) {
            mPassword.setError(getString(R.string.error_invalid_password));
            mPassword.requestFocus();
        } else {
            Utils.hideKeyboard(this);
            fetchSignUp(user, cc);
        }
    }

    private void fetchSignUp(User user, CreditCard cc) {
        pDialog = Utils.showProgressDialog(this, "Creating your account...");
        Api.getInstance().getServices().setUser(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getNumber(),
                user.getAddress(),
                user.getLat(),
                user.getLng(),
                user.getAccountId(),
                cc.getCardNumber(),
                cc.getCsv(),
                cc.getExpiryDate()).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if(!response.isSuccessful())
                        throw new Exception(response.errorBody().string());
                    if(response.body() == null)
                        throw new Exception("No Response");
                    if(response.body().getError())
                        throw new Exception(response.body().getMessage());
                    signUp(response.body());
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, pDialog);
                }
            }

            @Override
            public void onFailure(@Nullable Call<Result> call, @NonNull Throwable t) {
                Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void signUp(Result result) {
        finish();
        Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

}