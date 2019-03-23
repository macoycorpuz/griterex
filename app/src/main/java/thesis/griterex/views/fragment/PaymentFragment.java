package thesis.griterex.views.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thesis.griterex.R;
import thesis.griterex.domain.Api;
import thesis.griterex.models.entities.Credit;
import thesis.griterex.models.entities.Order;
import thesis.griterex.models.entities.Product;
import thesis.griterex.models.entities.Result;
import thesis.griterex.utils.SharedPrefManager;
import thesis.griterex.utils.Tags;
import thesis.griterex.utils.Utils;

public class PaymentFragment extends Fragment {

    //region Attributes
    String TAG = "PaymentFragment";
    int productId, paymentId = 0;
    String[] paymentMethod = {"Cash On Delivery", "Credit Card"};
    Product product;
    Order order;
    Credit credit;

    View mView, mViewProduct, mViewCredit;
    TextView mProductName, mSupplierName, mLocation, mPrice, mTotal, mError;
    EditText mQuantity, mPaymentMethod, mChange, mCardNumber, mCsv, mExpiryDate;

    Button mOrder;
    ImageView mImage;
    ProgressDialog pDialog;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_payment, container, false);

        mViewProduct = mView.findViewById(R.id.itemPaymentProduct);
        mProductName = mViewProduct.findViewById(R.id.txtItemProductName);
        mSupplierName = mViewProduct.findViewById(R.id.txtItemSupplierName);
        mLocation = mViewProduct.findViewById(R.id.txtItemProductLocation);
        mPrice = mViewProduct.findViewById(R.id.txtItemProductPrice);
        mImage = mViewProduct.findViewById(R.id.imgItemProductThumb);

        mTotal = mView.findViewById(R.id.txtPaymentTotal);
        mQuantity = mView.findViewById(R.id.txtPaymentQuantity);
        mPaymentMethod = mView.findViewById(R.id.txtPaymentMethod);
        mChange = mView.findViewById(R.id.txtPaymentChange);
        mError = mView.findViewById(R.id.txtPaymentError);

        mViewCredit = mView.findViewById(R.id.formPaymentCreditCard);
        mCardNumber = mViewCredit.findViewById(R.id.txtCardNumber);
        mExpiryDate = mViewCredit.findViewById(R.id.txtExpiryDate);
        mCsv = mViewCredit.findViewById(R.id.txtCsv);

        mOrder = mView.findViewById(R.id.btnPaymentOrder);

        mPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMethod();
            }
        });
        mPaymentMethod.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { if(hasFocus) selectPaymentMethod(); }
        });
        mPaymentMethod.setKeyListener(null);
        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticate();
            }
        });

        fetchProduct();
        mQuantity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode ==KeyEvent.KEYCODE_ENTER)
                {
                    double total = Double.valueOf(mQuantity.getText().toString()) * product.getPrice();
                    mTotal.setText(String.valueOf(total));
                    return true;
                }
                return false;
            }
        });

        return mView;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    private void selectPaymentMethod() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Choose Payment Method");
        alert.setItems(paymentMethod, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        paymentId = Tags.CASH;
                        mViewCredit.setVisibility(View.GONE);
                        mChange.setVisibility(View.VISIBLE);
                        mPaymentMethod.setText(paymentMethod[position]);
                        break;
                    case 1:
                        paymentId = Tags.CREDIT;
                        mViewCredit.setVisibility(View.VISIBLE);
                        mChange.setVisibility(View.GONE);
                        mPaymentMethod.setText(paymentMethod[position]);
                        break;
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void fetchProduct() {
        pDialog = Utils.showProgressDialog(getActivity(), "Loading...");
        Api.getInstance().getServices().getProduct(productId).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull final Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if (response.errorBody() != null)
                        throw new Exception(response.errorBody().string());
                    if (response.body().getError())
                        throw new Exception(response.body().getMessage());
                    product = response.body().getProduct();
                    showProduct();
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    Utils.switchContent(getActivity(), R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
                }
            }

            @Override
            public void onFailure(@Nullable Call<Result> call, @NonNull Throwable t) {
                Utils.dismissProgressDialog(pDialog);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void authenticate(){
        mError.setVisibility(View.GONE);
        int userId = SharedPrefManager.getInstance().getUser(getActivity()).getId();
        order = new Order(
                Integer.valueOf(mQuantity.getText().toString()),
                "Order Submitted",
                true,
                Double.valueOf(mTotal.getText().toString()),
                Double.valueOf(mChange.getText().toString()),
                productId,
                userId
        );

        credit = new Credit(
                mCardNumber.getText().toString(),
                mExpiryDate.getText().toString(),
                Utils.parseWithDefault(mCsv.getText().toString())
        );


        if (order.getQuantity() <= 0) {
            mError.setText(R.string.error_quantity);
            mError.setVisibility(View.VISIBLE);
        } else if (paymentId > 2 || paymentId <= 0) {
            mError.setText(R.string.error_payment_method);
            mError.setVisibility(View.VISIBLE);
        } else if (paymentId == Tags.CASH && Utils.isEmptyFields(mChange.getText().toString())) {
            mError.setText(R.string.error_change);
            mError.setVisibility(View.VISIBLE);
        } else if (Double.valueOf(mChange.getText().toString()) < order.getTotal()) {
            mError.setText(R.string.error_change);
            mError.setVisibility(View.VISIBLE);
        } else if (paymentId == Tags.CREDIT && Utils.isEmptyFields(credit.getNumber(), credit.getExpiry(), String.valueOf(credit.getCsv()))) {
            mError.setText(R.string.error_credit);
            mError.setVisibility(View.VISIBLE);
        } else {
            Utils.hideKeyboard(getActivity());
            orderProduct();
        }
    }

    private void orderProduct() {
        pDialog = Utils.showProgressDialog(getActivity(), "Submitting your order...");
        Api.getInstance().getServices().setOrder(
                order.getQuantity(),
                order.getStatus(),
                order.isActive(),
                order.getTotal(),
                order.getCash(),
                order.getProduct_id(),
                order.getUser_id(),
                credit.getNumber(),
                credit.getExpiry(),
                credit.getCsv()).enqueue(new Callback<Result>() {
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
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    Utils.switchContent(getActivity(), R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, pDialog);
                }
            }

            @Override
            public void onFailure(@Nullable Call<Result> call, @NonNull Throwable t) {
                Utils.dismissProgressDialog(pDialog);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProduct() {
        mProductName.setText(product.getName());
        mSupplierName.setText(product.getUser().getName());
        mLocation.setText(product.getUser().getAddress());
        mPrice.setText(String.valueOf(product.getPrice()));
        Picasso.get()
                .load(product.getUrl())
                .placeholder(R.drawable.ic_photo_light_blue_24dp)
                .error(R.drawable.ic_error_outline_red_24dp)
                .into(mImage);

        mQuantity.setText("1");
        paymentId = Tags.CASH;
        mViewCredit.setVisibility(View.GONE);
        mChange.setVisibility(View.VISIBLE);
        mPaymentMethod.setText(paymentMethod[0]);
        double total = Double.valueOf(mQuantity.getText().toString()) * product.getPrice();
        mTotal.setText(String.valueOf(total));
    }

}
