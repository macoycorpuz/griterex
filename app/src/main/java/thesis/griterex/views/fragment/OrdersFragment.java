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
import thesis.griterex.models.CenterRepo;
import thesis.griterex.models.entities.Order;
import thesis.griterex.models.entities.Product;
import thesis.griterex.models.entities.Result;
import thesis.griterex.utils.SharedPrefManager;
import thesis.griterex.utils.Tags;
import thesis.griterex.utils.Utils;
import thesis.griterex.views.adapter.ProductAdapter;

public class OrdersFragment extends Fragment{

    //region Attributes
    String TAG = "Orders Fragment";

    View mView, mViewOrderList;
    TabLayout mTabLayout;
    TextView mError;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    ProgressBar mProgress;
    ProductAdapter productAdapter;

    boolean active = true;
    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_orders, container, false);

        mTabLayout = mView.findViewById(R.id.tabOrders);
        mTabLayout.addTab(mTabLayout.newTab().setText("Current"));
        mTabLayout.addTab(mTabLayout.newTab().setText("History"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewOrderList = mView.findViewById(R.id.viewOrderList);
        mProgress = mViewOrderList.findViewById(R.id.progressView);
        mError = mViewOrderList.findViewById(R.id.txtItemError);

        mSwipeRefreshLayout = mViewOrderList.findViewById(R.id.swipeView);
        mRecyclerView = mViewOrderList.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchOrders(active);
            }
        });

        mTabLayout.getTabAt(0).select();
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                active = (tab.getPosition() == 0);
                fetchOrders(active);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        fetchOrders(active);

        return mView;

    }

    private void clearView() {
        mRecyclerView.setAdapter(null);
        mError.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void fetchOrders(boolean active) {
        clearView();
        Utils.showProgress(true, mProgress, mRecyclerView);
        int accountId = SharedPrefManager.getInstance().getUser(getActivity()).getAccountId();
        int userId = SharedPrefManager.getInstance().getUser(getActivity()).getUserId();
        Api.getInstance().getServices().getOrders(accountId, userId, active).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                try {
                    Utils.showProgress(false, mProgress, mRecyclerView);
                    if(!response.isSuccessful())
                        throw new Exception(response.errorBody().toString());
                    if(response.body().getError())
                        throw new Exception(response.body().getMessage());
                    showOrders(response.body());
                } catch (Exception ex) {
                    Utils.handleError(ex.getMessage(), mError, mProgress, mRecyclerView);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showOrders(final Result result) {
        CenterRepo.getCenterRepo().setOrderList(result.getOrders());
        final List<Product> productList = result.getProducts();
        productAdapter = new ProductAdapter(getActivity(), productList);
        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Utils.setPosition(position);
                Utils.switchContent(getActivity(), R.id.fragContainer, Tags.ORDER_DETAILS_FRAGMENT);
            }
        });

        mRecyclerView.setAdapter(productAdapter);
    }
}
