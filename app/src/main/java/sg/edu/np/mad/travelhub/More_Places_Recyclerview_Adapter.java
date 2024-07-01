package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

public class More_Places_Recyclerview_Adapter extends RecyclerView.Adapter<More_Places_Recyclerview_Adapter.MyViewHolder> {

    Context context;
    List<PlaceDetails> morePlaceList;

    public More_Places_Recyclerview_Adapter(Context context, List<PlaceDetails> morePlaceList){
        this.context = context;
        this.morePlaceList = morePlaceList;
        setupTheme();
    }
    int color1;
    int color2;
    private void setupTheme(){
        SharedPreferences preferences = context.getSharedPreferences("spinner_preferences", Context.MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = context.getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

        switch (selectedTheme) {
            case "Default":
                color1 = context.getResources().getColor(R.color.main_orange);
                color2 = context.getResources().getColor(R.color.main_orange);
                break;
            case "Watermelon":
                color1 = context.getResources().getColor(R.color.wm_green);
                color2 = context.getResources().getColor(R.color.wm_red);
                break;
            case "Neon":
                color1 = context.getResources().getColor(R.color.nn_pink);
                color2 = context.getResources().getColor(R.color.nn_cyan);
                break;
            case "Protanopia":
                color1 = context.getResources().getColor(R.color.pro_purple);
                color2 = context.getResources().getColor(R.color.pro_green);
                break;
            case "Deuteranopia":
                color1 = context.getResources().getColor(R.color.deu_yellow);
                color2 = context.getResources().getColor(R.color.deu_blue);
                break;
            case "Tritanopia":
                color1 = context.getResources().getColor(R.color.tri_orange);
                color2 = context.getResources().getColor(R.color.tri_green);
                break;
            default:
                color1 = context.getResources().getColor(R.color.main_orange);
                color2 = context.getResources().getColor(R.color.main_orange);
                break;
        }
    }
    @NonNull
    @Override
    public More_Places_Recyclerview_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recommended_places_recyclerview_row, parent, false);

        return new More_Places_Recyclerview_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull More_Places_Recyclerview_Adapter.MyViewHolder holder, int position) {
        // assigning values to views (rows) created in the recycler_view_row layout file
        // based on the position of the recycler view

        PlaceDetails place = morePlaceList.get(position);

        if (!(place.getPhotos().isEmpty())){
            String placePhoto = place.getPhotos().get(0);
            // Using Glide to load the image from URL into the ImageView
            Glide.with(context)
                    .load(placePhoto) // Assuming getImgUrl() returns the image URL
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .into(holder.placeImg);
        } else{
            Glide.with(context)
                    .load(R.drawable.imagenotfound)  // Replace with your "ImageNotFound" drawable
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .into(holder.placeImg);
        }

        holder.placeName.setText(place.getName());
        holder.location.setText(place.getAddress());
        if (place.getRating() == 0){
            holder.rating.setText("NA");
        } else{
            holder.rating.setText(String.valueOf(place.getRating()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the ViewPlaceActivity
                Intent intent = new Intent(context, CollapsingViewPlaceActivity.class);
                // Pass the Place object as an extra in the intent
                intent.putExtra("place", (Parcelable) place);
                // Start the ViewPlaceActivity
                context.startActivity(intent);
            }
        });

        // Change colour for Drawables
        ImageView location = (ImageView) holder.itemView.findViewById(R.id.locationIcon);
        Drawable location_drawable = ContextCompat.getDrawable(context, R.drawable.home_activity_location_marker);
        Drawable mutableLocationDrawable = location_drawable.mutate();
        mutableLocationDrawable.setTint(color1);
        location.setImageDrawable(mutableLocationDrawable);
        ImageView star = (ImageView) holder.itemView.findViewById(R.id.star_rating);
        Drawable star_drawable = ContextCompat.getDrawable(context, R.drawable.star_rating);
        Drawable mutablestarDrawable = star_drawable.mutate();
        mutablestarDrawable.setTint(color1);
        star.setImageDrawable(mutablestarDrawable);

    }

    @Override
    public int getItemCount() {
        // the recyclerview just wants to know the number of items you want displayed
        return morePlaceList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // grabbing the views from our recycler_view_row layout file
        // kinda like the onCreate method

        ImageView placeImg;
        TextView placeName;
        ImageView locationIcon;
        TextView location;
        ImageView star_rating;
        TextView rating;


        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            placeImg = itemView.findViewById(R.id.placeImg);
            placeName = itemView.findViewById(R.id.placeName);
            locationIcon = itemView.findViewById(R.id.locationIcon);
            location = itemView.findViewById(R.id.location);
            star_rating = itemView.findViewById(R.id.star_rating);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}
