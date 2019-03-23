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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thesis.griterex.R;
import thesis.griterex.domain.Api;
import thesis.griterex.models.CenterRepo;
import thesis.griterex.models.entities.Product;
import thesis.griterex.models.entities.Result;
import thesis.griterex.models.entities.User;
import thesis.griterex.utils.SharedPrefManager;
import thesis.griterex.utils.Tags;
import thesis.griterex.utils.Utils;
import thesis.griterex.views.adapter.ProductAdapter;
import thesis.griterex.views.adapter.UserAdapter;

public class UsersFragment extends Fragment {

    //region Attributes
    String TAG = "Users Fragment";
    int accountId = 0;

    View mView, mViewProductList;
    TextView mError;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    ProgressBar mProgress;
    UserAdapter userAdapter;
    //endregion

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_item_list, container, false);

        mProgress = mView.findViewById(R.id.progressView);
        mError = mView.findViewById(R.id.txtItemError);

        mSwipeRefreshLayout = mView.findViewById(R.id.swipeView);
        mRecyclerView = mView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchUsers();
            }
        });

        fetchUsers();

        return mView;

    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    private void clearView() {
        mRecyclerView.setAdapter(null);
        mError.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void fetchUsers() {
        clearView();
        Utils.showProgress(true, mProgress, mRecyclerView);
         Api.getInstance().getServices().getUsers().enqueue(new Callback<Result>() {
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
                    showUsers(response.body().getUsers());
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

    private void showUsers(List<User> users) {
        List<User> userList = new ArrayList<>();
        for(User user : users) if(user.getAccount_id() == accountId) userList.add(user);
        if(userList.size() <= 0) {
            mError.setText(R.string.error_users);
            mError.setVisibility(View.VISIBLE);
            return;
        }

        CenterRepo.getCenterRepo().setUserList(userList);
        userAdapter = new UserAdapter(getActivity(), userList);
        userAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Utils.setPosition(position);
                Utils.switchContent(getActivity(), R.id.fragContainer, Tags.USER_DETAILS_FRAGMENT);
            }
        });

        mRecyclerView.setAdapter(userAdapter);
    }
}
