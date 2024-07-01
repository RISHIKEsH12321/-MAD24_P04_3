package sg.edu.np.mad.travelhub;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder>{
    ArrayList<ItineraryEvent> data;
    private EventAdapter.OnItemClickListener listener;
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(EventAdapter.OnItemClickListener clickListener){
        listener = clickListener;
    }


    private static final String TAG = "ItineraryEvent";

    public EventAdapter(ArrayList<ItineraryEvent> input) {
        data = input;
    }

    public EventHolder onCreateViewHolder(
            ViewGroup parent,
            int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.em_itinerary_rv_holder,
                parent,
                false);
        return new EventHolder(item,listener);
    }

    public void onBindViewHolder(
            EventHolder holder,
            int position) {
        ItineraryEvent s = data.get(position);
        holder.name.setText(s.eventName);
        String timeString = String.format("%s:%s - %s:%s",s.startHour,s.startMin,s.endHour,s.endMin);
        holder.time.setText(timeString);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Notes");
                builder.setMessage(s.eventNotes);
                builder.setCancelable(true);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Close the dialog
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });

    }

    public static class EventHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView name;
        ImageView deleteImg;

        public EventHolder(View itemView, EventAdapter.OnItemClickListener listener) {
            super(itemView);
            time = itemView.findViewById(R.id.EMitineraryCustomHolderStartTime);
            name = itemView.findViewById(R.id.EMitineraryCustomHolderEventName);
            deleteImg = itemView.findViewById(R.id.EMDeleteEvent);


        }
    }

    public int getItemCount() {
        return data.size();
    }


}



