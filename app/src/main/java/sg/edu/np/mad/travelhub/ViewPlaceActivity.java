package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;

public class ViewPlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_place);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];
        int color1;
        int color2;
        switch (selectedTheme) {
            case "Default":
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                break;
            case "Watermelon":
                color1 = getResources().getColor(R.color.wm_green);
                color2 = getResources().getColor(R.color.wm_red);
                break;
            case "Neon":
                color1 = getResources().getColor(R.color.nn_pink);
                color2 = getResources().getColor(R.color.nn_cyan);
                break;
            case "Protanopia":
                color1 = getResources().getColor(R.color.pro_purple);
                color2 = getResources().getColor(R.color.pro_green);
                break;
            case "Deuteranopia":
                color1 = getResources().getColor(R.color.deu_yellow);
                color2 = getResources().getColor(R.color.deu_blue);
                break;
            case "Tritanopia":
                color1 = getResources().getColor(R.color.tri_orange);
                color2 = getResources().getColor(R.color.tri_green);
                break;
            default:
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                break;
        }

        // Get IDs
        RatingBar ratebar = findViewById(R.id.ratingBar);
        Button addtoplan = findViewById(R.id.addToPlanBtn);
        TextView placename = findViewById(R.id.placeName);
        TextView descriptiontitle = findViewById(R.id.descriptionTitle);
        TextView about = findViewById(R.id.aboutBtn);
        View underline = findViewById(R.id.aboutBtnLine);

        // Change colour of IDs
        ratebar.setProgressTintList(ColorStateList.valueOf(color2));
        addtoplan.setBackgroundTintList(ColorStateList.valueOf(color1));
        placename.setTextColor(color1);
        descriptiontitle.setTextColor(color1);
        about.setBackgroundTintList(ColorStateList.valueOf(color1));
        underline.setBackgroundColor(color2);

        // Change colour for Drawables
        ImageView marker = (ImageView) findViewById(R.id.locationIcon);
        marker.setColorFilter(color2);

        // Get the intent that started this activity
        Intent intent = getIntent();
        // Check if the intent contains an extra with the key "place"
        if (intent.hasExtra("place")) {
            // Retrieve the Place object from the intent
            Place place = (Place) intent.getSerializableExtra("place");

            // Setting the image background
            ImageView backgroundTopPlacePhotoImage = findViewById(R.id.backgroundTopPlacePhotoImage);
            TextView placeName = findViewById(R.id.placeName);
            TextView placeLocation = findViewById(R.id.placeLocation);
            TextView ratingText = findViewById(R.id.ratingText);
            TextView descriptionText = findViewById(R.id.descriptionText);
            RatingBar ratingBar = findViewById(R.id.ratingBar);

            Glide.with(this)
                    .load(place.getImgUrl()) // Load the image URL from the Place object
                    .transform(new CenterCrop(), new FitCenter())
                    .into(backgroundTopPlacePhotoImage);

            // Setting the content of the place details
            placeName.setText(place.getName());
            placeLocation.setText(place.getLocation());
            ratingText.setText(String.valueOf(place.getRating()));
            descriptionText.setText(place.getDescription());
            float rating = (float) place.getRating(); // Example: Rating of 4.5 stars
            ratingBar.setRating(rating);

            ImageButton viewPlaceBackBtn = findViewById(R.id.viewPlaceBackBtn);
            viewPlaceBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            // Setting onClickListener for going to add Events Management Page
           Button addToPlanBtn = findViewById(R.id.addToPlanBtn);
            addToPlanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an Intent to start the EventManagementActivity
                    Intent intent = new Intent(ViewPlaceActivity.this, EventManagement.class);
                    // Pass the Place object as an extra in the intent
                    intent.putExtra("place", place);
                    // Start the ViewPlaceActivity
                    startActivity(intent);
                }
            });

        } else {
            // Handle the case where the intent does not contain the "place" extra
            // You may display an error message or finish the activity
            Toast.makeText(this, "No place data found", Toast.LENGTH_SHORT).show();
            finish(); // Finish the activity if no place data is found
        }
    }
}