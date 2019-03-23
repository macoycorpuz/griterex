package thesis.griterex.views.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;
import java.io.InputStream;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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

public class SellFragment extends Fragment {

    //region Attributes
    String TAG = "Sell Fragment";
    View mView;
    EditText mCategoryName, mProductName, mPrice, mDescription;
    ImageView mImage;
    TextView mError;
    ProgressDialog pDialog;

    RequestBody supplierIdBody,categoryIdBody, productNameBody, descriptionBody, priceBody, fileBody;
    Uri fileUri;

    int PICK_IMAGE_REQUEST = 1;
    int categoryId = Tags.GAS;
    Product product;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_sell, container, false);

        // Initialize Views
        mCategoryName = mView.findViewById(R.id.txtSellCategoryName);
        mCategoryName.setText("Gas");
        mProductName = mView.findViewById(R.id.txtSellProductName);
        mPrice = mView.findViewById(R.id.txtSellPrice);
        mDescription = mView.findViewById(R.id.txtSellDescription);
        mImage = mView.findViewById(R.id.imgSellProduct);
        mError = mView.findViewById(R.id.txtSellError);
        Button mSellProduct = mView.findViewById(R.id.btnSell);

        mCategoryName.setInputType(InputType.TYPE_NULL);
        mCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory();
            }
        });
        mCategoryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) { if(hasFocus) selectCategory(); }
        });
        mCategoryName.setKeyListener(null);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        mSellProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                try {
                    fileUri = data.getData();
                    mImage.setImageBitmap(new Compressor(getActivity())
                            .compressToBitmap(new File(Utils.getRealPathFromURI(getActivity(), fileUri))));
                    mImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

    private void selectCategory() {
        //TODO: Remove the line below this
        Log.d(TAG, "selectCategory: " + fileUri);
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Choose Category Type");
        final String[] category = {"Gas", "Water", "Rice"};
        alert.setItems(category, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        Log.d("Sell Fragment", "GAS Clicked");
                        categoryId = Tags.GAS;
                        mCategoryName.setText(category[position]);
                        break;
                    case 1:
                        Log.d("Sell Fragment", "WATER Clicked");
                        categoryId = Tags.WATER;
                        mCategoryName.setText(category[position]);
                        break;
                    case 2:
                        Log.d("Sell Fragment", "RICE Clicked");
                        categoryId = Tags.RICE;
                        mCategoryName.setText(category[position]);
                        break;
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void authenticate() {
        mError.setVisibility(View.GONE);
        int supplierId = SharedPrefManager.getInstance().getUser(getActivity()).getId();
        product = new Product(
                mProductName.getText().toString(),
                mDescription.getText().toString(),
                Double.valueOf(mPrice.getText().toString()),
                supplierId,
                categoryId
        );

        if (Utils.isEmptyFields(mCategoryName.getText().toString(), product.getName(), product.getDescription(), mPrice.getText().toString())) {
            mError.setText(R.string.error_sell_product);
            mError.setVisibility(View.VISIBLE);
        } else if (fileUri == null) {
            mError.setText(R.string.error_product_image);
            mError.setVisibility(View.VISIBLE);
        } else {
            Utils.hideKeyboard(getActivity());
            sellProduct();
        }
    }

    private void sellProduct() {
        pDialog = Utils.showProgressDialog(getActivity(), "Posting your product...");
        parseRequestBody();
        Api.getInstance().getServices().setProduct(productNameBody, descriptionBody, priceBody, supplierIdBody, categoryIdBody, fileBody).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if (!response.isSuccessful())
                        throw new Exception(response.errorBody().string());
                    if (response.body().getError())
                        throw new Exception(response.body().getMessage());
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, pDialog);
                }
            }

            @Override
            public void onFailure(@Nullable Call<Result> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void parseRequestBody() {
        File filePath = new File(Utils.getRealPathFromURI(getActivity(), fileUri));
        try {
            supplierIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(product.getUser_id()));
            categoryIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(product.getCategory_id()));
            productNameBody = RequestBody.create(MediaType.parse("text/plain"), product.getName());
            descriptionBody = RequestBody.create(MediaType.parse("text/plain"), product.getDescription());
            priceBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(product.getPrice()));
            fileBody = RequestBody.create(MediaType.parse(getActivity().getContentResolver().getType(fileUri)), new Compressor(getActivity()).compressToFile(filePath));
        } catch (Exception ex) {

        }
    }
}
