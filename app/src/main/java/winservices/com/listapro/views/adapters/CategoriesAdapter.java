package winservices.com.listapro.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.DefaultCategory;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryVH> {

    private List<DefaultCategory> categories;
    private Context context;
    private int rowIndex = -1;
    private static final String TAG = CategoriesAdapter.class.getSimpleName();

    public CategoriesAdapter(List<DefaultCategory> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryVH holder, int position) {
        DefaultCategory category = categories.get(position);

        holder.txtCategory.setText(category.getDCategoryName());

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryVH extends RecyclerView.ViewHolder {

        private ImageView imgCategory;
        private TextView txtCategory;
        private ConstraintLayout clCategory;
        private CheckBox cbCategory;

        CategoryVH(@NonNull View itemView) {
            super(itemView);

            imgCategory = itemView.findViewById(R.id.imgCategory);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            clCategory= itemView.findViewById(R.id.clCategory);
            cbCategory = itemView.findViewById(R.id.cbCategory);

        }
    }
}
