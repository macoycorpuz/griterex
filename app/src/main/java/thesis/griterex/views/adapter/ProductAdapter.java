package thesis.griterex.views.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import thesis.griterex.R;
import thesis.griterex.models.entities.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    private Context mCtx;
    private List<Product> productList;
    private static OnItemClickListener clickListener;

    public ProductAdapter(Context mCtx, List<Product> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.txtName.setText(product.getProductName());
        holder.txtSupplierName.setText(product.getUser().getName());
        holder.txtLocation.setText(product.getUser().getAddress());
        holder.txtPrice.setText(String.valueOf(product.getPrice()));

        Picasso.get()
                .load(product.getProductUrl())
                .placeholder(R.drawable.ic_photo_light_blue_24dp)
                .error(R.drawable.ic_error_outline_red_24dp)
                .into(holder.imgProd);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        ProductAdapter.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtName, txtSupplierName, txtLocation, txtPrice;
        ImageView imgProd;
        private ProductViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtName = itemView.findViewById(R.id.txtItemProductName);
            txtSupplierName = itemView.findViewById(R.id.txtItemSupplierName);
            txtLocation = itemView.findViewById(R.id.txtItemProductLocation);
            txtPrice = itemView.findViewById(R.id.txtItemProductPrice);
            imgProd = itemView.findViewById(R.id.imgItemProductThumb);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
