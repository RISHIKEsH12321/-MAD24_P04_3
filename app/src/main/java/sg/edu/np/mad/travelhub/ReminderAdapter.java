package sg.edu.np.mad.travelhub;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderHolder>{
    ArrayList<String> data;
    private ReminderAdapter.OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ReminderAdapter.OnItemClickListener clickListener){
        listener = clickListener;
    }


    private static final String TAG = "ReminderEvent";

    public ReminderAdapter(ArrayList<String> input) {
        data = input;
    }
    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.em_customer_holder_reminder, parent, false);
        return new ReminderHolder(item, listener);
    }

    public void onBindViewHolder(
            ReminderHolder holder,
            int position) {
        String s = data.get(position);
        holder.reminder.setText(s);

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });

    }

    public static class ReminderHolder extends RecyclerView.ViewHolder{
        TextView reminder;
        ImageView deleteImg;

        public ReminderHolder(View itemView, ReminderAdapter.OnItemClickListener listener){
            super(itemView);
            reminder = itemView.findViewById(R.id.EMreminderHolder);
            deleteImg = itemView.findViewById(R.id.EMDeleteRemindner);
        }
    }
    public int getItemCount() {
        return data.size();
    }
}
