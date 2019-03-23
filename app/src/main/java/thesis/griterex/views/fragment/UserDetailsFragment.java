package thesis.griterex.views.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thesis.griterex.R;
import thesis.griterex.domain.Api;
import thesis.griterex.models.CenterRepo;
import thesis.griterex.models.entities.Result;
import thesis.griterex.models.entities.User;
import thesis.griterex.utils.Tags;
import thesis.griterex.utils.Utils;

public class UserDetailsFragment extends Fragment {
    
    //region Attributes
    int position = 0;
    User user;

    View mView;
    TextView mName, mEmail, mNumber, mAddress, mError;
    Button mDelete;
    ProgressDialog pDialog;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_user_details, container, false);

        mName = mView.findViewById(R.id.txtUserName);
        mEmail = mView.findViewById(R.id.txtUserEmail);
        mNumber = mView.findViewById(R.id.txtUserNumber);
        mAddress = mView.findViewById(R.id.txtUserAddress);
        mError = mView.findViewById(R.id.txtUserError);
        mDelete = mView.findViewById(R.id.btnUserDelete);
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });

        user = CenterRepo.getCenterRepo().getUserList().get(position);
        showUser();
        return mView;
    }


    public void setPosition(int position) {
        this.position = position;
    }

    private void showUser() {
        String name = "Name: " + user.getName();
        String email = "Email: " + user.getEmail();
        String number = "Number: " + user.getNumber();
        String address = "Address: " + user.getAddress();

        mName.setText(name);
        mEmail.setText(email);
        mNumber.setText(number);
        mAddress.setText(address);
    }

    private void deleteUser() {
        int userId = user.getId();
        pDialog = Utils.showProgressDialog(getActivity(), "Cancelling order...");
        Api.getInstance().getServices().deleteUser(userId).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@Nullable Call<Result> call, @NonNull final Response<Result> response) {
                try {
                    Utils.dismissProgressDialog(pDialog);
                    if (response.errorBody() != null)
                        throw new Exception(response.errorBody().string());
                    if (response.body().getError())
                        throw new Exception(response.body().getMessage());
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    Utils.switchContent(getActivity(), R.id.fragContainer, Tags.USERS_FRAGMENT);
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
