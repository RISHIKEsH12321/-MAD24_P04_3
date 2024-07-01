package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CollapsingViewPlaceActivity extends AppCompatActivity {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Loading_Dialog loadingDialog = new Loading_Dialog(CollapsingViewPlaceActivity.this);
    private PDFragmentAdapter adapter = new PDFragmentAdapter(this);
    private BottomSheetBehavior<View> behavior;
    private PlaceDetails place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collapsing_view_place);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the intent that started this activity
        Intent intent = getIntent();

        // Check if intent has extras and retrieve the Place object
        if (intent != null && intent.hasExtra("place")) {
            place = (PlaceDetails) intent.getParcelableExtra("place");

            // Setting the front end with place details
            TextView placeName = findViewById(R.id.placeName);
            placeName.setText(place.getName());

            TextView placeLocation = findViewById(R.id.placeLocation);
            placeLocation.setText(place.getAddress());

            TextView ratingText = findViewById(R.id.ratingText);
            if (place.getRating() == 0){
                ratingText.setText("NA");
            } else{
                ratingText.setText(String.valueOf(place.getRating()));
            }

            RatingBar ratingBar = findViewById(R.id.ratingBar);
            float rating = (float) place.getRating(); // Example: Rating of 4.5 stars
            ratingBar.setRating(rating);

            // Loading placePhotoImg
            ImageView placePhotoImg = findViewById(R.id.placePhotoImg);
            if (place.getPhotos().isEmpty()){
                Glide.with(this)
                        .load(R.drawable.imagenotfound) // Assuming getImgUrl() returns the image URL
                        .into(placePhotoImg);
            } else{
                Glide.with(this)
                        .load(place.getPhotos().get(0)) // Assuming getImgUrl() returns the image URL
                        .into(placePhotoImg);
            }
        } else {
            // Handle case where intent does not have expected extra
            Log.e("Intent Error", "Intent does not contain 'place' extra or 'place' extra is null");
        }

        FrameLayout bottomSheet = findViewById(R.id.bottomSheet);

        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setDraggable(true);
        bottomSheet.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                bottomSheet.removeOnLayoutChangeListener(this);
                behavior.setPeekHeight(1320, true);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        // Create and fetch Description and load fragments
        getPlaceDescription(place.getPlaceXid());

        // Event Handler for viewPlaceBackBtn
        ImageButton viewPlaceBackBtn = findViewById(R.id.viewPlaceBackBtn);
        viewPlaceBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Event Handler for addtoPlanBtn
        AppCompatButton addToPlanBtn = findViewById(R.id.addToPlanBtn);
        addToPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Create an Intent to start the EventManagementActivity
//                Intent intent = new Intent(ViewPlaceActivity.this, EventManagement.class);
//                // Pass the Place object as an extra in the intent
//                intent.putExtra("place", place);
//                // Start the ViewPlaceActivity
//                startActivity(intent);
                Log.d("AddToBtn", "CLICKED");
            }
        });
    }

    private void getPlaceDescription(String placeXid){
        if (placeXid != null){
            loadingDialog.startLoadingDialog();
            executor.execute(() -> {
                OkHttpClient client = new OkHttpClient();
                String apiKey = BuildConfig.otmApikey;
                HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.opentripmap.com/0.1/en/places/xid/" + placeXid).newBuilder();
                urlBuilder.addQueryParameter("apikey", apiKey);

                String url = urlBuilder.build().toString();
                Log.d("Url", url);

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                // Execute the request and handle the response
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d("Response", responseBody);

                        // Parse the JSON response to extract the description
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            JSONObject wikipedia_extracts = jsonObject.getJSONObject("wikipedia_extracts");
                            String description = wikipedia_extracts.optString("text", "Description is currently unavailable");
                            // Log or process the description as needed
                            Log.d("Description", description);

                            runOnUiThread(() -> {
                                TabLayout tabLayout = findViewById(R.id.PlaceDetailsTabs);
                                ViewPager2 viewPager = findViewById(R.id.fragmentTab);
                                viewPager.setUserInputEnabled(false);

                                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                    @Override
                                    public void onTabSelected(TabLayout.Tab tab) {
                                        // To actually get back the current behavior state of the bottomsheet
//                                        int currentState = behavior.getState();
//                                        bottomSheet.post(() ->{
//                                            if (currentState == BottomSheetBehavior.STATE_COLLAPSED){
//                                                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                                            } else{
//                                                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                                            }
//                                        });
                                    }
                                    @Override
                                    public void onTabUnselected(TabLayout.Tab tab) {
                                        // auto expand the bottomsheet for better viewing
                                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    }
                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {

                                    }
                                });

                                // Create AboutFragment and pass description
                                AboutFragment aboutFragment = new AboutFragment();
                                Bundle aboutBundle = new Bundle();
                                aboutBundle.putString("description", description); // Assuming getPlaceDescription() returns the description string
                                aboutFragment.setArguments(aboutBundle);
                                adapter.addFragment(aboutFragment, "About");
                                loadingDialog.dismissDialog();

                                // Create ReviewsFragment and pass the reviews
                                ReviewsFragment reviewsFragment = new ReviewsFragment();
                                Bundle reviewBundle = new Bundle();
                                ArrayList<PlaceReview> placeReviewList = new ArrayList<>(place.getReviews());
                                reviewBundle.putParcelableArrayList("placeReviewsList", placeReviewList);
                                reviewsFragment.setArguments(reviewBundle);
                                Log.d("Reviews", "Number of Reviews" + place.getReviews().size());
                                adapter.addFragment(reviewsFragment, "Reviews");

                                // Create PhotosFragment and pass photo URLs
                                PhotosFragment photosFragment = new PhotosFragment();
                                Bundle photosBundle = new Bundle();
                                ArrayList<String> photoUrls = new ArrayList<>(place.getPhotos().subList(1, Math.min(place.getPhotos().size(), 6)));
                                photosBundle.putStringArrayList("placePhotos", photoUrls); // Assuming getPlacePhotos() returns ArrayList<String>
                                photosFragment.setArguments(photosBundle);
                                adapter.addFragment(photosFragment, "Photos");

                                viewPager.setAdapter(adapter);
                                viewPager.setOffscreenPageLimit(1);

                                // Set the default tab to AboutFragment (position 0)
                                viewPager.setCurrentItem(0, false); // 'false' means no smooth scroll

                                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                                    tab.setText(adapter.getPageTitle(position));
                                }).attach();
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("Response", "Unsuccessful: " + response.code());
                        // Handle error response
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle network or IO errors
                }
            });
        } else{
            TabLayout tabLayout = findViewById(R.id.PlaceDetailsTabs);
            ViewPager2 viewPager = findViewById(R.id.fragmentTab);
            viewPager.setUserInputEnabled(false);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    // auto expand the bottomsheet for better viewing
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            // Create AboutFragment and pass description
            AboutFragment aboutFragment = new AboutFragment();
            Bundle aboutBundle = new Bundle();
            aboutBundle.putString("description", "Description is currently unavailable"); // Assuming getPlaceDescription() returns the description string
            aboutFragment.setArguments(aboutBundle);
            adapter.addFragment(aboutFragment, "About");

            // Create ReviewsFragment and pass the reviews
            ReviewsFragment reviewsFragment = new ReviewsFragment();
            Bundle reviewBundle = new Bundle();
            ArrayList<PlaceReview> placeReviewList = new ArrayList<>(place.getReviews());
            reviewBundle.putParcelableArrayList("placeReviewsList", placeReviewList);
            reviewsFragment.setArguments(reviewBundle);
            adapter.addFragment(reviewsFragment, "Reviews");

            // Create PhotosFragment and pass photo URLs
            PhotosFragment photosFragment = new PhotosFragment();
            Bundle photosBundle = new Bundle();
            ArrayList<String> photoUrls = new ArrayList<>(place.getPhotos());
            photosBundle.putStringArrayList("placePhotos", photoUrls); // Assuming getPlacePhotos() returns ArrayList<String>
            photosFragment.setArguments(photosBundle);
            adapter.addFragment(photosFragment, "Photos");

            viewPager.setAdapter(adapter);

            // Set the default tab to AboutFragment (position 0)
            viewPager.setCurrentItem(0, true);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                tab.setText(adapter.getPageTitle(position));
            }).attach();
        }
    }
}
