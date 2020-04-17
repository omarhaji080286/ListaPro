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

import java.util.ArrayList;
import java.util.List;

import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.DefaultCategory;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryVH> {

    private List<DefaultCategory> categories, selectedCategories;
    private Context context;
    private IntShowNextButton mCallback; //Interface
    private int rowIndex = -1;
    private static final String TAG = CategoriesAdapter.class.getSimpleName();

    public CategoriesAdapter(List<DefaultCategory> categories, IntShowNextButton mCallback) {
        this.categories = categories;
        this.selectedCategories = new ArrayList<>();
        this.mCallback = mCallback;
    }

    @NonNull
    @Override
    public CategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryVH holder, int position) {
        final DefaultCategory category = categories.get(position);

        holder.txtCategory.setText(category.getDCategoryName());

        if (isCategoryChecked(category)) {
            holder.cbCategory.setChecked(true);
        } else {
            holder.cbCategory.setChecked(false);
        }


        holder.clCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.cbCategory.isChecked()){
                    holder.cbCategory.setChecked(true);
                    selectedCategories.add(category);
                } else {
                    holder.cbCategory.setChecked(false);
                    selectedCategories.remove(category);
                }
                manageButtonNextVisibility();
            }
        });
    }

    private void manageButtonNextVisibility() {
        if (selectedCategories.size() > 0) {
            mCallback.onCategoriesSelected(true);
        } else {
            mCallback.onCategoriesSelected(false);
        }
    }

    private boolean isCategoryChecked(DefaultCategory category){
        for (int i = 0; i < categories.size(); i++) {
            for (int j = 0; j < selectedCategories.size(); j++) {
                if (categories.get(i).getDCategoryId() == selectedCategories.get(j).getDCategoryId()){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public List<DefaultCategory> getSelectedCategories() {
        return selectedCategories;
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

    public interface IntShowNextButton {
        void onCategoriesSelected(boolean showNextButton);
    }




}
