package thesis.griterex.views.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thesis.griterex.R;
import thesis.griterex.domain.Api;
import thesis.griterex.models.entities.Product;
import thesis.griterex.models.entities.Result;
import thesis.griterex.utils.SharedPrefManager;
import thesis.griterex.utils.Tags;
import thesis.griterex.utils.Utils;

//TODO: (onBackPressed) - Implement on Back Pressed then go to fragment
public class ProductDetailsFragment extends Fragment {

    //region Attributes
    String TAG = "Product Details";
    int productId;

    View mView;
    TextView mProductName, mCategoryName, mSupplierName, mPrice, mAddress, mDescription;
    Button mOrder, mDelete;
    ImageView mImage;
    ProgressDialog pDialog;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_product_details, container, false);

        mProductName = mView.findViewById(R.id.txtProdName);
        mCategoryName = mView.findViewById(R.id.txtProdCategory);
        mSupplierName = mView.findViewById(R.id.txtProdSupplier);
        mPrice = mView.findViewById(R.id.txtProdPrice);
        mAddress = mView.findViewById(R.id.txtProdLocation);
        mDescription = mView.findViewById(R.id.txtProdDescription);
        mImage = mView.findViewById(R.id.imgProduct);
        mOrder = mView.findViewById(R.id.btnProdOrder);
        mDelete = mView.findViewById(R.id.btnProdDelete);
        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrder();
            }
        });
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        authenticate();
        fetchProduct();
        return mView;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    private void authenticate() {
        int accountId = SharedPrefManager.getInstance().getUser(getActivity()).getAccount_id();
        Log.d(TAG, "authenticate: " + accountId);
        switch (accountId) {
            case Tags.USER:
                mOrder.setVisibility(View.VISIBLE);
                mDelete.setVisibility(View.GONE);
                break;
            case Tags.ADMIN: case Tags.SUPPLIER:
                mOrder.setVisibility(View.GONE);
                mDelete.setVisibility(View.VISIBLE);
                break;
        }
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
                    showProduct(response.body().getProduct());
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@Nullable Call<Result> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProduct(Product product) {
        Log.d(TAG, "showProduct: " + product.toString());

        String category = "Category: " + product.getCategory().getName();
        String supplierName = "Supplier Name: " + product.getUser().getName();
        String address = "Address: " + product.getUser().getAddress();
        String description = "Description: " + product.getDescription();
        String price = "Price: " + String.valueOf(product.getPrice());

        mProductName.setText(product.getName());
        mCategoryName.setText(category);
        mSupplierName.setText(supplierName);
        mAddress.setText(address);
        mDescription.setText(description);
        mPrice.setText(price);
        Picasso.get()
                .load(product.getUrl())
                .placeholder(R.drawable.ic_photo_light_blue_24dp)
                .error(R.drawable.ic_error_outline_red_24dp)
                .fit()
                .centerInside()
                .into(mImage);
    }

    private void showOrder() {
        Utils.setProductId(productId);
        Utils.switchContent(getActivity(), R.id.fragContainer, Tags.PAYMENT_FRAGMENT);
    }

    private void deleteProduct() {
        pDialog = Utils.showProgressDialog(getActivity(), "Deleting product...");
        Api.getInstance().getServices().deleteProduct(productId).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull final Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if (response.errorBody() != null)
                        throw new Exception(response.errorBody().string());
                    if (response.body().getError())
                        throw new Exception(response.body().getMessage());
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    Utils.switchContent(getActivity(), R.id.fragContainer, Tags.PRODUCTS_FRAGMENT);
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@Nullable Call<Result> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}

