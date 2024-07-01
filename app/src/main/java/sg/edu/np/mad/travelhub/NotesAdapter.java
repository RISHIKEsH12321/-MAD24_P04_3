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
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder>{
    ArrayList<String> data;
    private NotesAdapter.OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(NotesAdapter.OnItemClickListener clickListener){
        listener = clickListener;
    }


    private static final String TAG = "ReminderEvent";

    public NotesAdapter(ArrayList<String> input) {
        data = input;
    }
    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.em_customer_holder_notes, parent, false);
        return new NotesHolder(item, listener);
    }

    public void onBindViewHolder(
            NotesHolder holder,
            int position) {
        String s = data.get(position);
        holder.notes.setText(s);

        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });

    }

    public static class NotesHolder extends RecyclerView.ViewHolder{
        TextView notes;
        ImageView deleteImg;

        public NotesHolder(View itemView, NotesAdapter.OnItemClickListener listener){
            super(itemView);
            notes = itemView.findViewById(R.id.EMnoteHolder);
            deleteImg = itemView.findViewById(R.id.EMDeleteNote);
        }
    }


    public int getItemCount() {
        return data.size();
    }
}
