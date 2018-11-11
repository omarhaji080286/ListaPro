package winservices.com.listapro.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import winservices.com.listapro.R;
import winservices.com.listapro.models.entities.DefaultCategory;

public class DCategoriesToSelectAdapter extends RecyclerView.Adapter<DCategoriesToSelectAdapter.DCategoryVH> {

    private List<DefaultCategory> dCategories = new ArrayList<>();
    private List<DefaultCategory> selectedDCategories = new ArrayList<>();

    @NonNull
    @Override
    public DCategoryVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_d_category_to_select, parent, false);
        return new DCategoryVH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DCategoryVH holder, int position) {
        final DefaultCategory dCategory = dCategories.get(position);

        holder.cbDCategory.setChecked(true);
        holder.txtDCategory.setText(dCategory.getDCategoryName());

        holder.cbDCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked){
                    selectedDCategories.remove(dCategory);
                } else {
                    selectedDCategories.add(dCategory);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dCategories.size();
    }

    public void setDCategories(List<DefaultCategory> dCategories) {
        this.dCategories = dCategories;
        this.selectedDCategories = dCategories;
        notifyDataSetChanged();
    }

    class DCategoryVH extends RecyclerView.ViewHolder {

        private CheckBox cbDCategory;
        private TextView txtDCategory;

        DCategoryVH(@NonNull View itemView) {
            super(itemView);

            cbDCategory = itemView.findViewById(R.id.cbDCategory);
            txtDCategory = itemView.findViewById(R.id.txtDCategory);
        }
    }

    public List<DefaultCategory> getSelectedDCategories() {
        return selectedDCategories;
    }
}
