package thesis.griterex.views.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import thesis.griterex.R;
import thesis.griterex.models.entities.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    String TAG = "UserAdapter";
    private Context mCtx;
    private List<User> userList;
    private static OnItemClickListener clickListener;

    public UserAdapter(Context mCtx, List<User> userList) {
        this.mCtx = mCtx;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.txtName.setText(user.getName());
        holder.txtEmail.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        UserAdapter.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtName, txtEmail;
        private UserViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtName = itemView.findViewById(R.id.txtUserName);
            txtEmail = itemView.findViewById(R.id.txtItemUserEmail);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
