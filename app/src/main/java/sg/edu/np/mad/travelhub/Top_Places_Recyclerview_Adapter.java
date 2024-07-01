package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

public class Top_Places_Recyclerview_Adapter extends RecyclerView.Adapter<Top_Places_Recyclerview_Adapter.MyViewHolder>{
    Context context;
    List<PlaceDetails> topPlacesList;

    public Top_Places_Recyclerview_Adapter(Context context, List<PlaceDetails> topPlacesList){
        this.context = context;
        this.topPlacesList = topPlacesList;
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.top_place_recyclerview, parent, false);

        return new Top_Places_Recyclerview_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PlaceDetails place = topPlacesList.get(position);

        if (!(place.getPhotos().isEmpty())){
            String placePhoto = place.getPhotos().get(0);
            // Using Glide to load the image from URL into the ImageView
            Glide.with(context)
                    .load(placePhoto) // Assuming getImgUrl() returns the image URL
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .into(holder.catPlace);
        } else{
            Glide.with(context)
                    .load(R.drawable.imagenotfound)  // Replace with your "ImageNotFound" drawable
                    .transform(new CenterCrop(),new RoundedCorners(10))
                    .into(holder.catPlace);
        }

        holder.recPlaceName.setText(place.getName());
        if (place.getRating() == 0){
            holder.recPlaceRatingtv.setText("NA");
        } else{
            holder.recPlaceRatingtv.setText(String.valueOf(place.getRating()));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the ViewPlaceActivity
                Intent intent = new Intent(context, CollapsingViewPlaceActivity.class);
                // Pass the PlaceDetail object as an extra in the intent
                intent.putExtra("place", (Parcelable) place);
                // Start the ViewPlaceActivity
                context.startActivity(intent);
            }
        });
        // Set color filter for rating image
        holder.recPlaceRatingImg.setColorFilter(color2);
    }

    @Override
    public int getItemCount() {
        return topPlacesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView catPlace;
        TextView recPlaceName;
        TextView recPlaceRatingtv;
        ImageView recPlaceRatingImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            catPlace = itemView.findViewById(R.id.catPlace);
            recPlaceName = itemView.findViewById(R.id.recPlaceName);
            recPlaceRatingtv = itemView.findViewById(R.id.recPlaceRatingtv);
            recPlaceRatingImg = itemView.findViewById(R.id.recPlaceRatingImg);
        }
    }
}
