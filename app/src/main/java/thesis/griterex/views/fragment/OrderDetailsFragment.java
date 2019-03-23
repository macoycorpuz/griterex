package thesis.griterex.views.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import thesis.griterex.models.CenterRepo;
import thesis.griterex.models.entities.Order;
import thesis.griterex.models.entities.Product;
import thesis.griterex.models.entities.Result;
import thesis.griterex.models.entities.User;
import thesis.griterex.utils.SharedPrefManager;
import thesis.griterex.utils.Tags;
import thesis.griterex.utils.Utils;

public class OrderDetailsFragment extends Fragment {

    //region Attributes
    String TAG = "OrderDetailsFragment";
    int position;
    Order order;

    View mView, mViewProduct, mViewUser, mViewButtons;
    ImageView mImage;
    TextView mProdName, mSupplierName, mLocation, mPrice;
    TextView mName, mEmail;
    TextView mUser, mStatus;
    Button mProcessing, mDelivered, mCancel;
    ProgressDialog pDialog;

    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_order_details, container, false);
        mViewButtons = mView.findViewById(R.id.viewOrderButtons);

        mViewProduct = mView.findViewById(R.id.itemOrderProduct);
        mImage = mViewProduct.findViewById(R.id.imgItemProductThumb);
        mProdName = mViewProduct.findViewById(R.id.txtItemProductName);
        mSupplierName = mViewProduct.findViewById(R.id.txtItemSupplierName);
        mLocation = mViewProduct.findViewById(R.id.txtItemProductLocation);
        mPrice = mViewProduct.findViewById(R.id.txtItemProductPrice);

        mViewUser = mView.findViewById(R.id.itemOrderUser);
        mName = mViewUser.findViewById(R.id.txtUserName);
        mEmail = mViewUser.findViewById(R.id.txtItemUserEmail);

        mStatus = mView.findViewById(R.id.txtOrderStatus);
        mUser = mView.findViewById(R.id.txtOrderUser);
        mProcessing = mView.findViewById(R.id.btnOrderProcess);
        mDelivered = mView.findViewById(R.id.btnOrderDelivered);
        mCancel = mView.findViewById(R.id.btnOrderCancel);

        mProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrderStatus(Tags.PROCESSING, true);
            }
        });
        mDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOrderStatus(Tags.DELIVERED, false);
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOrder();
            }
        });

        authenticate();
        showOrderDetails();
        return mView;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private void authenticate() {
        int accountId = SharedPrefManager.getInstance().getUser(getActivity()).getAccount_id();
        switch (accountId) {
            case Tags.USER:
                mViewButtons.setVisibility(View.GONE);
                mCancel.setVisibility(View.VISIBLE);
                mUser.setText(R.string.buyer);
                break;
            case Tags.SUPPLIER:
                mViewButtons.setVisibility(View.VISIBLE);
                mCancel.setVisibility(View.GONE);
                mUser.setText(R.string.supplier);
                break;
        }
    }

    private void showOrderDetails() {
        order = CenterRepo.getCenterRepo().getOrderList().get(position);
        mStatus.setText(order.getStatus());
        Product product = order.getProduct();
        mProdName.setText(product.getName());
        mSupplierName.setText(product.getUser().getName());
        mLocation.setText(product.getUser().getAddress());
        mPrice.setText(String.valueOf(product.getPrice()));
        Picasso.get()
                .load(product.getUrl())
                .placeholder(R.drawable.ic_photo_light_blue_24dp)
                .error(R.drawable.ic_error_outline_red_24dp)
                .into(mImage);

        int accountId = SharedPrefManager.getInstance().getUser(getActivity()).getAccount_id();
        User user;
        switch (accountId) {
            case Tags.SUPPLIER:
                mUser.setText("Buyer");
                user = order.getUser();
                mName.setText("Name: " + user.getName());
                mEmail.setText("Email: " + user.getEmail());
                break;
            case Tags.USER:
                mUser.setText("Seller");
                user = order.getProduct().getUser();
                mName.setText("Name: " + user.getName());
                mEmail.setText("Email: " + user.getEmail());
                break;
        }

        if(order.getStatus().equals(Tags.DELIVERED)) {
            mStatus.setTextColor(Color.parseColor("#00b226"));
            mProcessing.setVisibility(View.GONE);
            mDelivered.setVisibility(View.GONE);
            mCancel.setVisibility(View.VISIBLE);
            mCancel.setText("DELETE");
        } else {
            mStatus.setTextColor(Color.parseColor("#b20c0f"));
        }

    }

    private void setOrderStatus(String status, boolean active) {
        pDialog = Utils.showProgressDialog(getActivity(), "Updating...");
        int orderId = order.getId();
        Api.getInstance().getServices().updateOrderStatus(orderId, status, active).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if(!response.isSuccessful())
                        throw new Exception(response.errorBody().toString());
                    if(response.body().getError())
                        throw new Exception(response.body().getMessage());
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    Utils.switchContent(getActivity(), R.id.fragContainer, Tags.ORDERS_FRAGMENT);
                } catch (Exception ex) {
                    Utils.dismissProgressDialog(pDialog);
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.dismissProgressDialog(pDialog);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteOrder() {
        int orderId = order.getId();
        pDialog = Utils.showProgressDialog(getActivity(), "Cancelling order...");
        Api.getInstance().getServices().deleteOrder(orderId).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull final Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if (response.errorBody() != null)
                        throw new Exception(response.errorBody().string());
                    if (response.body().getError())
                        throw new Exception(response.body().getMessage());
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    Utils.switchContent(getActivity(), R.id.fragContainer, Tags.ORDERS_FRAGMENT);
                } catch (Exception ex) {
                    Utils.dismissProgressDialog(pDialog);
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@Nullable Call<Result> call, @NonNull Throwable t) {
                Utils.dismissProgressDialog(pDialog);
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
