package sg.edu.np.mad.travelhub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BringItemAdapter extends RecyclerView.Adapter<BringItemAdapter.BringItemHolder> {
    ArrayList<ToBringItem> data;
    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public BringItemAdapter(ArrayList<ToBringItem> input) {
        data = input;
    }

    @Override
    public BringItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.em_to_bring_rv_holder,
                parent,
                false);
        return new BringItemHolder(item,listener);
    }


    @Override
    public void onBindViewHolder(BringItemHolder holder, int position) {
        ToBringItem s = data.get(position);
        holder.name.setText(s.itemName);
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });
    }
    public static class BringItemHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView deleteImg;
        public BringItemHolder(View itemView, BringItemAdapter.OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.EMitineraryCustomHolderItemName);
            deleteImg = itemView.findViewById(R.id.EMDeleteBringItem);
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}


