package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VEItemAdapter extends RecyclerView.Adapter<VEItemAdapter.VEItemHolder>{

    private List<ToBringItem> data;
    private Context context;

    public VEItemAdapter(Context context, List<ToBringItem> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public VEItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ve_to_bring_item, parent, false);
        return new VEItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VEItemHolder holder, int position) {
        ToBringItem item = data.get(position);

        holder.name.setText(item.itemName);
        holder.checkBox.setChecked(item.ticked);

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newTickedValue = updateCheckItem(item.itemID);
                holder.checkBox.setChecked(newTickedValue);
                item.ticked = newTickedValue;
            }
        });
    }

    private boolean updateCheckItem(String itemId) {
        DatabaseHandler dbHandler = new DatabaseHandler(context, null, null, 1);
        return dbHandler.checkItem(itemId);
    }

    public static class VEItemHolder extends RecyclerView.ViewHolder {
        TextView name;
        CheckBox checkBox;

        public VEItemHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.VEtoBringItemName);
            checkBox = itemView.findViewById(R.id.VEtoBringItemCheck);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
