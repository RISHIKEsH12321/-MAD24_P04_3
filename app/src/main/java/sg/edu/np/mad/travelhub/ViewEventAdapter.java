package sg.edu.np.mad.travelhub;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ViewEventAdapter extends RecyclerView.Adapter<ViewEventAdapter.ViewEventHolder> {

    private List<CompleteEvent> data;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        listener = clickListener;
    }

    public ViewEventAdapter(Context context, List<CompleteEvent> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ve_complete_event_layout, parent, false);
        return new ViewEventHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewEventHolder holder, int position) {
        CompleteEvent event = data.get(position);
        holder.id.setText(event.eventID);
        holder.name.setText(event.eventName);
        holder.category.setText(event.category);

        // Clear previous dynamic views before adding new ones
        holder.itineaary.removeAllViews();
        holder.items.removeAllViews();
        holder.notes.removeAllViews();
        holder.reminder.removeAllViews();

        // Add itinerary events
        if (event.itineraryEventList != null){ // Throws error if no events
            for (ItineraryEvent itineraryEvent: event.itineraryEventList){
                //Populate Itinerary Event
                LinearLayout container = new LinearLayout(holder.itineaary.getContext());
                container.setOrientation(LinearLayout.HORIZONTAL);
                TextView itTime = new TextView(holder.itineaary.getContext());
                TextView itName = new TextView(holder.itineaary.getContext());

                itTime.setBackgroundResource(R.drawable.rounded_border);
                itTime.setPadding(12, 8, 12, 8);
                itTime.setTextColor(ContextCompat.getColor(holder.itineaary.getContext(), R.color.white));
                itTime.setTextSize(16);
                itName.setTextSize(20);
                LinearLayout.LayoutParams itTimeParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                itTimeParams.setMargins(16, 8, 16, 8);
                itTime.setLayoutParams(itTimeParams);


                String timeText = String.format("%s:%s - %s:%s",
                        itineraryEvent.startHour,
                        itineraryEvent.startMin,
                        itineraryEvent.endHour,
                        itineraryEvent.endMin);
                itTime.setText(timeText);

                itName.setText(itineraryEvent.eventName);
                itName.setPadding(20,0,0,0);
                itName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(holder.itineaary.getContext())
                                .setTitle("Notes")
                                .setMessage(itineraryEvent.eventNotes)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    }
                });

                container.addView(itTime);
                container.addView(itName);
                //Add itinerary event to display
                holder.itineaary.addView(container);
            }
        }

        if (event.attachmentImageList != null){
            Log.d("IMAGEATTACHMENTINIMAGES", String.valueOf(event.attachmentImageList.size()));
            for (ImageAttachment imageAttachment: event.attachmentImageList){
                Log.d("IMAGEATTACHMENTINIMAGES", "GOes in loop");
                ImageView imageView = new ImageView(holder.notes.getContext());
                Glide.with(context)
                        .load(R.drawable.plane_ticket_example)
                        .apply(new RequestOptions().override(LinearLayout.LayoutParams.WRAP_CONTENT, 100)) // Set height to 100dp
                        .into(imageView);

                // Set layout parameters for ImageView
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        250 // Set height to 100dp
                );

                // Convert 5dp to pixels
                int marginEnd = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        5,
                        context.getResources().getDisplayMetrics()
                );

                layoutParams.setMarginEnd(marginEnd); 

                imageView.setLayoutParams(layoutParams);


                imageView.setOnClickListener(v -> {
                    // Inflate layout for the alert dialog
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.em_image_dialog, null);

                    // Get the ImageView from the custom layout
                    ImageView fullSizeImageView = dialogView.findViewById(R.id.EMfullSizeImageView);

                    // Load full-size image into the ImageView using Glide
                    Glide.with(context)
                            .load(R.drawable.plane_ticket_example)
                            .into(fullSizeImageView);

                    // Create and configure the AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView)
                            .setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

                    // Show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                });

                Log.d("IMAGEATTACHMENTINIMAGES", "CREATED AND ADDED A IMAGE. ID: " + imageAttachment.ImageId);

                holder.images.addView(imageView);
            }
        }


        if (event.attachmentImageList != null){
            Log.d("IMAGEATTACHMENTINIMAGES", String.valueOf(event.attachmentImageList.size()));
            for (ImageAttachment imageAttachment: event.attachmentImageList){
                Log.d("IMAGEATTACHMENTINIMAGES", "GOes in loop");
                ImageView imageView = new ImageView(holder.notes.getContext());
                Glide.with(context)
                        .load(R.drawable.plane_ticket_example)
                        .apply(new RequestOptions().override(LinearLayout.LayoutParams.WRAP_CONTENT, 100)) // Set height to 100dp
                        .into(imageView);

                // Set layout parameters for ImageView
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        250 // Set height to 100dp
                );
                imageView.setLayoutParams(layoutParams);


                imageView.setOnClickListener(v -> {
                    // Inflate layout for the alert dialog
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.em_image_dialog, null);

                    // Get the ImageView from the custom layout
                    ImageView fullSizeImageView = dialogView.findViewById(R.id.EMfullSizeImageView);

                    // Load full-size image into the ImageView using Glide
                    Glide.with(context)
                            .load(R.drawable.plane_ticket_example)
                            .into(fullSizeImageView);

                    // Create and configure the AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView)
                            .setPositiveButton("Close", (dialog, which) -> dialog.dismiss());

                    // Show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                });

                Log.d("IMAGEATTACHMENTINIMAGES", "CREATED AND ADDED A IMAGE. ID: " + imageAttachment.ImageId);

                holder.images.addView(imageView);
            }
        }





        if (event.toBringItems!=null) {
            //Initialize adapter for item
            VEItemAdapter itemAdapter = new VEItemAdapter(context, event.toBringItems);

            LinearLayoutManager itemsLayoutManager = new LinearLayoutManager(context);

            holder.items.setLayoutManager(itemsLayoutManager);
            holder.items.setItemAnimator(new DefaultItemAnimator());
            holder.items.setAdapter(itemAdapter);
        }

        // Add notes
        if (event.notesList != null){
            for (String notes: event.notesList){
                TextView it = new TextView(holder.notes.getContext());
                it.setText(notes);
                it.setTextAppearance(context, R.style.BulletPoints);
                it.setTextSize(25);
                // Set the drawable (Arrow Sign)
                Drawable bulletDrawable = ContextCompat.getDrawable(context, R.drawable.round_arrow_right);
                it.setCompoundDrawablesWithIntrinsicBounds(bulletDrawable, null, null, null);
                it.setGravity(Gravity.CENTER_VERTICAL);
                holder.notes.addView(it);
            }
        }


        // Add reminders
        if (event.reminderList != null){
            for (String reminder: event.reminderList){
                TextView it = new TextView(holder.reminder.getContext());
                it.setText(reminder);
                it.setTextAppearance(context, R.style.BulletPoints);
                it.setTextSize(25);
                // Set the drawable programmatically (Arrow Sign)
                Drawable bulletDrawable = ContextCompat.getDrawable(context, R.drawable.round_arrow_right);
                it.setCompoundDrawablesWithIntrinsicBounds(bulletDrawable, null, null, null);
                it.setGravity(Gravity.CENTER_VERTICAL);

                holder.reminder.addView(it);
            }
        }

        //Delete Event Clicker
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });

        holder.expandArrow.setOnClickListener(view ->{
            // If the CardView is already expanded, set its visibility

            // to gone and change the expand less icon to expand more.

            if (holder.hiddenView.getVisibility() == View.VISIBLE) {

                // The transition of the hiddenView is carried out by the TransitionManager class.

                // Here we use an object of the AutoTransition Class to create a default transition

                TransitionManager.beginDelayedTransition(holder.cardview, new AutoTransition());

                holder.hiddenView.setVisibility(View.GONE);

                holder.expandArrow.setImageResource(R.drawable.baseline_expand_more_24);

            }



            // If the CardView is not expanded, set its visibility to

            // visible and change the expand more icon to expand less.

            else {

                TransitionManager.beginDelayedTransition(holder.cardview, new AutoTransition());

                holder.hiddenView.setVisibility(View.VISIBLE);

                holder.expandArrow.setImageResource(R.drawable.baseline_expand_less_24);

            }


        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //Method to call database to delete event based on ID
    public void deleteEvent(int eventId, int position) {
        DatabaseHandler dbHandler = new DatabaseHandler(context, null, null, 1);
        dbHandler.deleteEventById(eventId);

        // Remove item from data list and notify adapter
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    public static class ViewEventHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView id;
        TextView category;
        ImageView deleteImg;
        LinearLayout itineaary;
        RecyclerView items;
        LinearLayout reminder;
        LinearLayout notes;
        LinearLayout images;
        ImageView expandArrow;
        LinearLayout hiddenView;
        CardView cardview;

        public ViewEventHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            id = itemView.findViewById(R.id.VEEventID);
            deleteImg = itemView.findViewById(R.id.VEDelteEvent);
            name = itemView.findViewById(R.id.VEEventName);
            category = itemView.findViewById(R.id.VEEventCategory);
            itineaary = itemView.findViewById(R.id.VEEventItinerary);
            items = itemView.findViewById(R.id.VEEventItems);
            notes = itemView.findViewById(R.id.VEEventNote);
            reminder = itemView.findViewById(R.id.VEEventRemidners);
            images = itemView.findViewById(R.id.VEEventImages);
            expandArrow = itemView.findViewById(R.id.VEarrow_button);
            hiddenView = itemView.findViewById(R.id.VEhidden_view);
            cardview = itemView.findViewById(R.id.VEbase_cardview);
        }
    }
}
