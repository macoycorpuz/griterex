 package thesis.griterex.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import thesis.griterex.AppController;
import thesis.griterex.models.entities.Order;
import thesis.griterex.models.entities.Result;
import thesis.griterex.views.fragment.AccountFragment;
import thesis.griterex.views.fragment.OrderDetailsFragment;
import thesis.griterex.views.fragment.OrdersFragment;
import thesis.griterex.views.fragment.PaymentFragment;
import thesis.griterex.views.fragment.ProductDetailsFragment;
import thesis.griterex.views.fragment.ProductsFragment;
import thesis.griterex.views.fragment.SellFragment;
import thesis.griterex.views.fragment.UserDetailsFragment;
import thesis.griterex.views.fragment.UsersFragment;

public class Utils {

    //region Initialize
    private static Utils utils;

    public static Utils getUtils() {

        if (null == utils) {
            utils = new Utils();
        }
        return utils;
    }
    //endregion

    //region UI Interaction
    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    public static void dismissProgressDialog(ProgressDialog pDialog) {
        if (pDialog != null) pDialog.dismiss();
    }

    public static void showProgress(final boolean show, final View progressView, final View goneForm) {
        int shortAnimTime = AppController.getInstance().resources.getInteger(android.R.integer.config_shortAnimTime);

        goneForm.setVisibility(show ? View.GONE : View.VISIBLE);
        goneForm.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                goneForm.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //endregion

    //region Authentication
    public static boolean isEmailValid(String email) {
        if (email == null) return false;
        return (!TextUtils.isEmpty(email)) && (email.contains("@"));
    }

    public static boolean isPasswordValid(String password) {
        return (!TextUtils.isEmpty(password)) && (password.length() > 8);
    }

    public static boolean isEmptyFields(String... fields) {
        for(String f : fields) {
            if(f.isEmpty()) return true;
        }
        return false;
    }

    public static int parseWithDefault(String s) {
        return s.matches("-?\\d+") ? Integer.parseInt(s) : 0;
    }
    //endregion

    //region Real Path
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String result;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        } catch (Exception ex) {
            result = ex.getMessage();
        }
        return result;
    }
    //endregion

    //region Fragment Util
    private static String CURRENT_TAG = null;
    private static int position = 0;
    private static int productId = 0;
    private static int userId = 0;
    private static int accountId = 0;

    public static void switchContent(FragmentActivity baseActivity, int id, String TAG) {

        Fragment fragment;
        FragmentManager fragmentManager = baseActivity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!TAG.equals(CURRENT_TAG) && !TAG.equals(Tags.USERS_FRAGMENT)) {
            switch (TAG) {
                case Tags.PRODUCTS_FRAGMENT:
                    fragment = new ProductsFragment();
                    break;
                case Tags.ORDERS_FRAGMENT:
                    fragment = new OrdersFragment();
                    break;
                case Tags.ACCOUNT_FRAGMENT:
                    fragment = new AccountFragment();
                    break;
                case Tags.PAYMENT_FRAGMENT:
                    fragment = new PaymentFragment();
                    ((PaymentFragment) fragment).setProductId(productId);
                    break;
                case Tags.SELL_FRAGMENT:
                    fragment = new SellFragment();
                    break;
                case Tags.PRODUCT_DETAILS_FRAGMENT:
                    fragment = new ProductDetailsFragment();
                    ((ProductDetailsFragment) fragment).setProductId(productId);
                    break;
                case Tags.USER_DETAILS_FRAGMENT:
                    fragment = new UserDetailsFragment();
                    ((UserDetailsFragment) fragment).setPosition(userId);
                    break;
                case Tags.ORDER_DETAILS_FRAGMENT:
                    fragment = new OrderDetailsFragment();
                    ((OrderDetailsFragment) fragment).setPosition(position);
                    break;
                default:
                    fragment = null;
                    break;
            }

            CURRENT_TAG = TAG;
            transaction.replace(id, fragment, TAG);
            transaction.commit();
        }

        if(TAG.equals(Tags.USERS_FRAGMENT)){
            fragment = new UsersFragment();
            ((UsersFragment) fragment).setAccountId(accountId);

            CURRENT_TAG = TAG;
            transaction.replace(id, fragment, TAG);
            transaction.commit();
        }


    }

    public static void setPosition(int position) {
        Utils.position  = position;
    }

    public static void setAccountId(int accountId) {
        Utils.accountId = accountId;
    }

    public static void setProductId(int productId) {
        Utils.productId = productId;
    }

    //endregion

    //region Api Util

    public static void handleError(String error, TextView mErrorView, ProgressDialog pDialog) {
        Utils.dismissProgressDialog(pDialog);
        mErrorView.setText(error);
        mErrorView.setVisibility(View.VISIBLE);
    }

    public static void handleError(String error, TextView mErrorView, ProgressBar mProgress, RecyclerView mRecyclerView) {
        Utils.showProgress(false, mProgress, mRecyclerView);
        mErrorView.setText(error);
        mErrorView.setVisibility(View.VISIBLE);
    }

    //endregion

}