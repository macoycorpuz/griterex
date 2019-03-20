package thesis.griterex.views.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import thesis.griterex.R;
import thesis.griterex.utils.SharedPrefManager;
import thesis.griterex.views.activity.LoginActivity;

public class AccountFragment extends Fragment {

    //region Attributes
    View mView, mViewLogin, mViewUser;
    TextView mName, mEmail, mNumber, mAddress;
    Button mLogin, mLogout;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_account, container, false);
        mViewLogin = mView.findViewById(R.id.formLogin);
        mViewUser = mView.findViewById(R.id.formAccount);

        mName = mView.findViewById(R.id.txtUserName);
        mEmail = mView.findViewById(R.id.txtUserEmail);
        mNumber = mView.findViewById(R.id.txtUserNumber);
        mAddress = mView.findViewById(R.id.txtUserAddress);
        mLogin = mView.findViewById(R.id.btnAccountLogin);
        mLogout = mView.findViewById(R.id.btnAccountLogout);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        if (SharedPrefManager.getInstance().isLoggedIn(getActivity())) showUser();
        else showLogin();

        return mView;
    }

    private void showUser() {
        mViewUser.setVisibility(View.VISIBLE);
        mViewLogin.setVisibility(View.GONE);

        mName.setText(SharedPrefManager.getInstance().getUser(getActivity()).getName());
        mEmail.setText(SharedPrefManager.getInstance().getUser(getActivity()).getEmail());
        mNumber.setText(SharedPrefManager.getInstance().getUser(getActivity()).getNumber());
        mAddress.setText(SharedPrefManager.getInstance().getUser(getActivity()).getAddress());
    }

    private void showLogin() {
        mViewLogin.setVisibility(View.VISIBLE);
        mViewUser.setVisibility(View.GONE);
    }

    private void login() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    private void logOut() {
        SharedPrefManager.getInstance().logout(getActivity());
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

}
