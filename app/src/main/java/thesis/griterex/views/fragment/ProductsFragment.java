package thesis.griterex.views.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
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
import thesis.griterex.views.adapter.ProductAdapter;

public class ProductsFragment extends Fragment{

    //region Attributes
    String TAG = "Products Fragment";
    View mView, mViewProductList;
    TabLayout mTabLayout;
    EditText mSearch;
    TextView mError;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    ProgressBar mProgress;
    ProductAdapter productAdapter;
    List<Product> productList;

    int categoryId = 1;
    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_products, container, false);

        mTabLayout = mView.findViewById(R.id.tabProducts);
        mTabLayout.addTab(mTabLayout.newTab().setText("Gas"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Water"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Rice"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewProductList = mView.findViewById(R.id.viewProductList);
        mProgress = mViewProductList.findViewById(R.id.progressView);
        mError = mViewProductList.findViewById(R.id.txtItemError);
        mSearch = mViewProductList.findViewById(R.id.txtItemSearch);

        mSwipeRefreshLayout = mViewProductList.findViewById(R.id.swipeView);
        mRecyclerView = mViewProductList.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTabLayout.getTabAt(0).select();
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                categoryId = tab.getPosition() + 1;
                fetchProducts(categoryId,null);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mSearch.setVisibility(View.VISIBLE);
        mSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode ==KeyEvent.KEYCODE_ENTER)
                {
                    fetchProducts(categoryId, mSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchProducts(categoryId,null);
            }
        });

        fetchProducts(categoryId,null);

        return mView;

    }

    private void clearView() {
        mRecyclerView.setAdapter(null);
        mError.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void fetchProducts(int categoryId, String productName) {
        clearView();
        Utils.showProgress(true, mProgress, mRecyclerView);
        int accountId = SharedPrefManager.getInstance().getUser(getActivity()).getAccount_id();
        int supplierId = SharedPrefManager.getInstance().getUser(getActivity()).getId();
        Call<Result> call;

        if(accountId == Tags.SUPPLIER) call = Api.getInstance().getServices().getProductsBySupplier(categoryId, supplierId);
        else if (!TextUtils.isEmpty(productName)) call = Api.getInstance().getServices().getProductsByName(categoryId, productName);
        else call = Api.getInstance().getServices().getProductsByCategory(categoryId);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                try {
                    Utils.showProgress(false, mProgress, mRecyclerView);
                    if(!response.isSuccessful())
                        throw new Exception("Server not responding");
                    if(response.body() == null)
                        throw new Exception("No Response");
                    if(response.body().getError())
                        throw new Exception(response.body().getMessage());
                    productList = response.body().getProducts();
                    showProducts();
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, mProgress, mRecyclerView);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Utils.handleError(t.getMessage(), mError, mProgress, mRecyclerView);
            }
        });
    }

    private void showProducts() {
        productAdapter = new ProductAdapter(getActivity(), productList);
        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(!SharedPrefManager.getInstance().isLoggedIn(getActivity()))
                    Utils.switchContent(getActivity(), R.id.fragContainer, Tags.ACCOUNT_FRAGMENT);
                else {
                    Utils.setProductId(productList.get(position).getId());
                    Utils.switchContent(getActivity(), R.id.fragContainer, Tags.PRODUCT_DETAILS_FRAGMENT);
                }
            }
        });

        mRecyclerView.setAdapter(productAdapter);
    }
}
