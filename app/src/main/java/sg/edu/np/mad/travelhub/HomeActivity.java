package sg.edu.np.mad.travelhub;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    ArrayList<Place> placeList = new ArrayList<>();
    ArrayList<Place> recommendedPlaceList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<String> cityList = new ArrayList<>();
    private Map<String, City_Firebase> cityDictionary = new HashMap<>();
    private Map<String, String> placesName = new HashMap<>();
    private List<PlaceDetails> placeDetailsList = new ArrayList<>();
    private List<PlaceDetails> topPlaceList = new ArrayList<>();
    private List<PlaceDetails> morePlaceList = new ArrayList<>();
    private int placeSize = 0;
    private boolean updatingRecyclerView = false;
    private int limit = 1;
    private final Loading_Dialog loadingDialog = new Loading_Dialog(HomeActivity.this);
    Button currentActiveBtn;
    int color1;
    int color2;
    int color3;

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

        switch (selectedTheme) {
            case "Default":
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                color3 = getResources().getColor(R.color.main_orange_bg);
                break;
            case "Watermelon":
                color1 = getResources().getColor(R.color.wm_green);
                color2 = getResources().getColor(R.color.wm_red);
                color3 = getResources().getColor(R.color.wm_red_bg);
                break;
            case "Neon":
                color1 = getResources().getColor(R.color.nn_pink);
                color2 = getResources().getColor(R.color.nn_cyan);
                color3 = getResources().getColor(R.color.nn_cyan_bg);
                break;
            case "Protanopia":
                color1 = getResources().getColor(R.color.pro_purple);
                color2 = getResources().getColor(R.color.pro_green);
                color3 = getResources().getColor(R.color.pro_green_bg);
                break;
            case "Deuteranopia":
                color1 = getResources().getColor(R.color.deu_yellow);
                color2 = getResources().getColor(R.color.deu_blue);
                color3 = getResources().getColor(R.color.deu_blue_bg);
                break;
            case "Tritanopia":
                color1 = getResources().getColor(R.color.tri_orange);
                color2 = getResources().getColor(R.color.tri_green);
                color3 = getResources().getColor(R.color.tri_green_bg);
                break;
            default:
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                color3 = getResources().getColor(R.color.main_orange_bg);
                break;
        }

        // Change colour for Drawables
        TextView dropdown_arrow = findViewById(R.id.dropdown);
        Drawable startDrawable = ContextCompat.getDrawable(this, R.drawable.home_activity_location_marker);
        startDrawable.setTint(color1);
        Drawable endDrawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_down);
        endDrawable.setTint(color1);
        dropdown_arrow.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, endDrawable, null);

        //Change color for Bottom NavBar
        BottomNavigationView bottomNavMenu = (BottomNavigationView) findViewById(R.id.bottomNavMenu);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{} // default state
        };
        int[] colors = new int[]{
                color1,
                ContextCompat.getColor(this, R.color.unselectedNavBtn)
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavMenu.setItemIconTintList(colorStateList);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

        switch (selectedTheme) {
            case "Default":
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                color3 = getResources().getColor(R.color.main_orange_bg);
                break;
            case "Watermelon":
                color1 = getResources().getColor(R.color.wm_green);
                color2 = getResources().getColor(R.color.wm_red);
                color3 = getResources().getColor(R.color.wm_red_bg);
                break;
            case "Neon":
                color1 = getResources().getColor(R.color.nn_pink);
                color2 = getResources().getColor(R.color.nn_cyan);
                color3 = getResources().getColor(R.color.nn_cyan_bg);
                break;
            case "Protanopia":
                color1 = getResources().getColor(R.color.pro_purple);
                color2 = getResources().getColor(R.color.pro_green);
                color3 = getResources().getColor(R.color.pro_green_bg);
                break;
            case "Deuteranopia":
                color1 = getResources().getColor(R.color.deu_yellow);
                color2 = getResources().getColor(R.color.deu_blue);
                color3 = getResources().getColor(R.color.deu_blue_bg);
                break;
            case "Tritanopia":
                color1 = getResources().getColor(R.color.tri_orange);
                color2 = getResources().getColor(R.color.tri_green);
                color3 = getResources().getColor(R.color.tri_green_bg);
                break;
            default:
                color1 = getResources().getColor(R.color.main_orange);
                color2 = getResources().getColor(R.color.main_orange);
                color3 = getResources().getColor(R.color.main_orange_bg);
                break;
        }

        // Change colour for Drawables
        TextView dropdown_arrow = findViewById(R.id.dropdown);
        Drawable startDrawable = ContextCompat.getDrawable(this, R.drawable.home_activity_location_marker);
        startDrawable.setTint(color1);
        Drawable endDrawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_down);
        endDrawable.setTint(color1);
        dropdown_arrow.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, endDrawable, null);

        //Change color for Bottom NavBar
        BottomNavigationView bottomNavMenu = (BottomNavigationView) findViewById(R.id.bottomNavMenu);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{} // default state
        };
        int[] colors = new int[]{
                color1,
                ContextCompat.getColor(this, R.color.unselectedNavBtn)
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavMenu.setItemIconTintList(colorStateList);

//        ImageButton notificationBell = findViewById(R.id.notification_btn);
//        notificationBell.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, ConvertCurrency.class));
//            }
//        });

        // Bottom Navigation View Logic to link to the different master activities
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_calendar) {
                startActivity(new Intent(this, ViewEvents.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.bottom_currency) {
                startActivity(new Intent(this, ConvertCurrency.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                startActivity(new Intent(this, Profile.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.bottom_home) {
                // Optional: Handle home selection differently or ignore
                return true;
            }
            return false;
        });



        // Testing API IMPLEMENTATION
        loadingDialog.startLoadingDialog();
        placesName.clear();
        placeDetailsList.clear();
        topPlaceList.clear();
        morePlaceList.clear();
        placeSize = 0;
        getPlaceRadius(Double.parseDouble("1.3521"), Double.parseDouble("103.8198"), null);

        // Initializing FireBase WORK WITH VINCENT AND BRANDON FOR THIS
//        FirebaseApp.initializeApp(this);
//        FirebaseDatabase database = FirebaseDatabase.getInstance("https://countriescities-a5de3-default-rtdb.asia-southeast1.firebasedatabase.app/");
//        DatabaseReference citiesRef = database.getReference().child("cities");
//
//        executor.execute(()->{
//            citiesRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot citySnapshot : dataSnapshot.getChildren()) {
//                        if (citySnapshot.exists()) {
//                            City_Firebase cityFirebase = citySnapshot.getValue(City_Firebase.class);
//                            String cityText = cityFirebase.getName() + ", " + cityFirebase.getCountry_code();
////                            Log.d("City", cityText);
////                            Log.d("Longitude", cityFirebase.getLongitude());
////                            Log.d("Latitude", cityFirebase.getLatitude());
//                            cityDictionary.put(cityText, cityFirebase);
//                            cityList.add(cityText);
//                        } else {
//                            Log.d("Firebase", "Authentication failed: User not found.");
//                        }
//                    }
//                    Log.d("First City", cityDictionary.get(cityList.get(0)).toString());
//                    City_Firebase firstCity = cityDictionary.get(cityList.get(0));
//                    placesName.clear();
//                    placeDetailsList.clear();
//                    topPlaceList.clear();
//                    morePlaceList.clear();
//                    placeSize = 0;
//                    getPlaceRadius(Double.parseDouble(firstCity.getLatitude()), Double.parseDouble(firstCity.getLongitude()), null);
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.d("Cities", "Authentication failed: User not found.");
//                }
//            });
//        });

        TextView dropdown = findViewById(R.id.dropdown);
        String defaultOption = "Singapore, SG";
        dropdown.setText(defaultOption);

        dropdown.setOnClickListener(v -> {

            // Initializing Dialog
            Dialog dialog = new Dialog(HomeActivity.this);
            // Set Custom Dialog with the XML file in layout folders
            dialog.setContentView(R.layout.dialog_searchable_spinner);
            // Set Custom Width & Height
            Objects.requireNonNull(dialog.getWindow()).setLayout(800, 1200);
            // Set Transparent Background
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Show Dialog
            dialog.show();

            // Initialize and assign variable
            EditText editText = dialog.findViewById(R.id.edit_text);
            ListView listView = dialog.findViewById(R.id.list_view);

            // Initialize array adapter
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(HomeActivity.this, android.R.layout.simple_list_item_1, cityList);

            // Set Adapter
            listView.setAdapter(arrayAdapter);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Filter Array List
                    arrayAdapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            // CITY PICKER SHOULD INITALIZE THE FIREBASE THEN UNCOMMENT THIS
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    // When item selected from list
//                    // Set selected item on text view
//                    String city = arrayAdapter.getItem(position);
//                    dropdown.setText(city);
//
//                    Log.d("City Selected", arrayAdapter.getItem(position));
//                    Log.d("City Selected", cityDictionary.get(city).toString());
//
//                    City_Firebase cityInfo = cityDictionary.get(city);
//
//                    placesName.clear();
//                    placeDetailsList.clear();
//                    topPlaceList.clear();
//                    morePlaceList.clear();
//                    placeSize = 0;
//                    loadingDialog.startLoadingDialog();
//                    getPlaceRadius(Double.parseDouble(cityInfo.getLatitude()), Double.parseDouble(cityInfo.getLongitude()), null);
//
//                    // Dismiss Dialog
//                    dialog.dismiss();
//                }
//            });
        });

        // Getting all the filter buttons and storing them in a list
        ArrayList<Button> btnList = new ArrayList<>();
        Button allBtn = findViewById(R.id.allBtn);
        Button hotelBtn = findViewById(R.id.hotelsBtn);
        Button foodBtn = findViewById(R.id.foodBtn);
        Button amusementsBtn = findViewById(R.id.amusementsBtn);
        Button mallsBtn = findViewById(R.id.mallsBtn);
        Button natureBtn = findViewById(R.id.natureBtn);
        btnList.add(allBtn);
        btnList.add(hotelBtn);
        btnList.add(foodBtn);
        btnList.add(amusementsBtn);
        btnList.add(mallsBtn);
        btnList.add(natureBtn);

        currentActiveBtn = allBtn;

        // Enable filter buttons sets the active/selected filter button to the active orange color
        enableFilterBtn(currentActiveBtn, null);

        // Logic for setting the color of each filter button when activated/selected
        for (Button btn : btnList) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(currentActiveBtn == btn)) {
                        enableFilterBtn(btn, currentActiveBtn);
                        currentActiveBtn = btn;
                        String btnText = btn.getText().toString();
                        Log.d("BtnText", btnText);
                        String kinds;
                        switch (btnText) {
                            case "Hotels":
                                kinds = "alpine_hut,campsites,guest_houses,other_hotels,resorts,villas_and_chalet";
//                                kinds ="accomodations";
                                break;
                            case "Food":
                                kinds = "foods";
                                break;
                            case "Amusements":
                                kinds = "amusement_parks,ferris_wheels,miniature_parks,other_amusement_rides,roller_coasters,water_parks";
                                break;
                            case "Malls":
                                kinds = "malls";
                                break;
                            case "Nature":
                                kinds = "natural";
                                break;
                            default:
                                kinds = null;
                                break;
                        }
                        String currentCity = dropdown.getText().toString();
                        City_Firebase cityInfo = cityDictionary.get(currentCity);
                        placesName.clear();
                        placeDetailsList.clear();
                        topPlaceList.clear();
                        morePlaceList.clear();
                        placeSize = 0;
                        loadingDialog.startLoadingDialog();
                        getPlaceRadius(Double.parseDouble(cityInfo.getLatitude()), Double.parseDouble(cityInfo.getLongitude()), kinds);
                    }
                }
            });
        }
    }

    @NonNull
    private static ArrayList<String> populateCityList() {
        ArrayList<String> cityList = new ArrayList<>();
        cityList.add("All");
//        cityList.add("Current Location");
        cityList.add("Singapore, Singapore");
        cityList.add("Aspen, USA");
        cityList.add("New York City, USA");
        cityList.add("Los Angeles, USA");
        cityList.add("Chicago, USA");
        cityList.add("Houston, USA");
        cityList.add("Phoenix, USA");
        cityList.add("Philadelphia, USA");
        cityList.add("San Antonio, USA");
        cityList.add("San Diego, USA");
        cityList.add("Dallas, USA");
        cityList.add("San Jose, USA");
        cityList.add("Austin, USA");
        cityList.add("Jacksonville, USA");
        cityList.add("Fort Worth, USA");
        cityList.add("Columbus, USA");
        cityList.add("Charlotte, USA");
        cityList.add("Indianapolis, USA");
        cityList.add("Seattle, USA");
        cityList.add("Denver, USA");
        cityList.add("Washington, USA");
        cityList.add("Boston, USA");
        cityList.add("El Paso, USA");
        cityList.add("Nashville, USA");
        cityList.add("Detroit, USA");
        cityList.add("Oklahoma City, USA");
        cityList.add("Portland, USA");
        cityList.add("Las Vegas, USA");
        cityList.add("Memphis, USA");
        cityList.add("Louisville, USA");
        cityList.add("Baltimore, USA");
        cityList.add("Milwaukee, USA");
        cityList.add("Albuquerque, USA");
        cityList.add("Tucson, USA");
        cityList.add("Fresno, USA");
        cityList.add("Sacramento, USA");
        cityList.add("Kansas City, USA");
        cityList.add("Long Beach, USA");
        cityList.add("Mesa, USA");
        cityList.add("Atlanta, USA");
        cityList.add("Colorado Springs, USA");
        cityList.add("Virginia Beach, USA");
        cityList.add("Raleigh, USA");
        cityList.add("Omaha, USA");
        cityList.add("Miami, USA");
        cityList.add("Oakland, USA");
        cityList.add("Minneapolis, USA");
        cityList.add("Tulsa, USA");
        cityList.add("Wichita, USA");
        return cityList;
    }

    private void enableFilterBtn(Button activatedBtn, @Nullable Button deactivatedBtn){
        activatedBtn.setTextColor(color2);
        activatedBtn.setBackgroundColor(color3);

        if (deactivatedBtn != null){
            deactivatedBtn.setTextColor(getResources().getColor(R.color.unselectedFilterText));
            deactivatedBtn.setBackgroundColor(getResources().getColor(R.color.unselectedFilterBackground));
        }
    }

    private void getPlaceRadius(double lat, double lon, String kinds) {
        final String finalKinds = (kinds == null) ? "resorts,amusements,natural,food_courts" : kinds;

        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            String apiKey = BuildConfig.otmApikey;
            double radius = 50000;

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.opentripmap.com/0.1/en/places/radius").newBuilder();
            urlBuilder.addQueryParameter("radius", Double.toString(radius));
            urlBuilder.addQueryParameter("lon", Double.toString(lon));
            urlBuilder.addQueryParameter("lat", Double.toString(lat));
            urlBuilder.addQueryParameter("kinds", finalKinds);
            urlBuilder.addQueryParameter("rate", "3");
            urlBuilder.addQueryParameter("limit", Integer.toString(limit));
            urlBuilder.addQueryParameter("apikey", apiKey);

            String url = urlBuilder.build().toString();
            Log.d("Url", url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonElement jsonElement = JsonParser.parseString(responseBody);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonArray featuresArray = jsonObject.getAsJsonArray("features");

                    for (JsonElement featureElement : featuresArray) {
                        JsonObject featureObject = featureElement.getAsJsonObject();
                        JsonObject propertiesObject = featureObject.getAsJsonObject("properties");
                        String xid = propertiesObject.get("xid").getAsString();
                        String name = propertiesObject.get("name").getAsString().toLowerCase();
                        Log.d("PlaceName", name);
                        if (!placesName.containsValue(name)){
                            placesName.put(xid, name);
                        }
                    }

                    runOnUiThread(() -> {
                        if (!placesName.isEmpty()) {
                            // Display the last place name in the TextView
                            String lastPlaceName = placesName.get(placesName.size() - 1);
                            Log.d("List Size", Integer.toString(placesName.size()));

                            for (String placeXid : placesName.keySet()) {
                                getPlaceIds(placeXid, placesName.get(placeXid));
                            }
                        } else {
                            // Handle case where no places are found
                        }
                    });
                } else {
                    Log.e("FetchPlacesTask", "Request failed: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void getPlaceIds(String placeXid, String placeName) {
        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            String apiKey = BuildConfig.googleApikey;

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/place/findplacefromtext/json").newBuilder();
            urlBuilder.addQueryParameter("input", placeName);
            urlBuilder.addQueryParameter("inputtype", "textquery");
            urlBuilder.addQueryParameter("key", apiKey);

            String url = urlBuilder.build().toString();
            Log.d("url", url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonElement jsonElement = JsonParser.parseString(responseBody);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    JsonArray candidatesArray = jsonObject.getAsJsonArray("candidates");

                    if (candidatesArray.size() > 0) {
                        JsonObject firstCandidate = candidatesArray.get(0).getAsJsonObject();
                        String placeId = firstCandidate.get("place_id").getAsString();

                        // Example usage of placeId (logging or updating UI)
                        runOnUiThread(() -> {
                            Log.d("Place Name", placeName);
                            Log.d("Place ID", placeId);
                            placeSize += 1;
                            getPlaceDetails(placeId, placeXid);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Log.d("Place ID", "No place ID found");

                            // Handle case where no place IDs are found, if needed
                        });
                    }

                } else {
                    Log.e("FetchPlaceIdTask", "Request failed: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void getPlaceDetails(String placeId, String placeXid){
        executor.execute(()->{
            OkHttpClient client = new OkHttpClient();
            String apiKey = BuildConfig.googleApikey;
            // String fields = "name,photo,rating,editorial_summary,reviews,reviews,formatted_address"
            String fields = "name,photo,rating,reviews,formatted_address";

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://maps.googleapis.com/maps/api/place/details/json").newBuilder();
            urlBuilder.addQueryParameter("fields", fields);
            urlBuilder.addQueryParameter("place_id", placeId);
            urlBuilder.addQueryParameter("key", apiKey);

            String url = urlBuilder.build().toString();
            Log.d("url", url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JsonElement jsonElement = JsonParser.parseString(responseBody);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    // Check if the response status is OK
                    if ("OK".equals(jsonObject.get("status").getAsString())) {
                        JsonObject resultObject = jsonObject.getAsJsonObject("result");
                        PlaceDetails placeDetails = new PlaceDetails();
                        placeDetails.setPlaceXid(placeXid);

                        // Extracting name
                        String name = resultObject.get("name").getAsString();
                        placeDetails.setName(name);

                        // Extracting rating PLEASE DO SOMETHING ABOUT THIS
                        double rating = resultObject.has("rating")
                                ? resultObject.get("rating").getAsDouble()
                                : 0;
                        placeDetails.setRating(rating);

                        // Extracting formatted address
                        String address = resultObject.has("formatted_address")
                                ? resultObject.get("formatted_address").getAsString()
                                : "NA";
                        placeDetails.setAddress(address);

                        // Extracting photos
                        List<String> photosUrl = new ArrayList<>();
                        if (resultObject.has("photos")) {
                            JsonArray photosArray = resultObject.getAsJsonArray("photos");
                            for (JsonElement photoElement : photosArray) {
                                JsonObject photoObject = photoElement.getAsJsonObject();
                                String photoReference = photoObject.get("photo_reference").getAsString();
                                String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=" + photoReference + "&key=" + apiKey;
                                photosUrl.add(photoUrl);
                            }
                        }
                        placeDetails.setPhotos(photosUrl);

                        // Extracting Reviews
                        List<PlaceReview> reviewList = new ArrayList<>();
                        if (resultObject.has("reviews")) {
                            JsonArray reviewsArray = resultObject.getAsJsonArray("reviews");
                            for (JsonElement reviewElement : reviewsArray){
                                JsonObject reviewObject = reviewElement.getAsJsonObject();

                                String authorName = reviewObject.has("author_name") ? reviewObject.get("author_name").getAsString() : "Author Name Not Available";
                                String authorUrl = reviewObject.has("author_url") ? reviewObject.get("author_url").getAsString() : null;
                                String profilePhotoUrl = reviewObject.has("profile_photo_url") ? reviewObject.get("profile_photo_url").getAsString() : null;
                                Double reviewRating = reviewObject.has("rating") ? reviewObject.get("rating").getAsDouble() : 0;
                                String relativeTimeDescription = reviewObject.has("relative_time_description") ? reviewObject.get("relative_time_description").getAsString() : "NA";
                                String text = reviewObject.has("text") ? reviewObject.get("text").getAsString() : "NA";
                                long time = reviewObject.has("time") ? reviewObject.get("time").getAsLong() : null;
                                boolean translated = reviewObject.has("translated") && reviewObject.get("translated").getAsBoolean();

                                PlaceReview review = new PlaceReview(authorName, authorUrl, profilePhotoUrl, reviewRating, relativeTimeDescription, text, time, translated);
                                reviewList.add(review);
                            }
                        }
                        placeDetails.setReviews(reviewList);

                        // Example: Log the extracted details
                        Log.d("Place Details", "Name: " + name);
                        Log.d("Place Details", "Rating: " + rating);
                        Log.d("Place Details", "Photo References: " + photosUrl);

                        // Update the UI on the main thread
                        runOnUiThread(() -> {
                            placeDetailsList.add(placeDetails);
                            Log.d("placeNameSize", "Size: " + placesName.size());
                            Log.d("placeDetailsSize", "Size: " + placeDetailsList.size());

                            if (placeDetailsList.size() == placeSize){
                                // recyclerview logic here
                                if (placeSize<6){
                                    for (int i = 0; i < placeSize; i++) {
                                        topPlaceList.add(placeDetailsList.get(i));
                                    }
                                }
                                else{
                                    for (int i = 0; i < 6; i++) {
                                        topPlaceList.add(placeDetailsList.get(i));
                                    }

                                    for (int i = 6; i < placeDetailsList.size(); i++){
                                        morePlaceList.add(placeDetailsList.get(i));
                                    }
                                }

                                for (PlaceDetails place : topPlaceList){
                                    Log.d("TopPlaceList: ", place.getName());
                                }
                                for (PlaceDetails place : morePlaceList){
                                    Log.d("MorePlaceList: ", place.getName());
                                }

                                RecyclerView topPlacesRV = findViewById(R.id.topPlacesRV);
                                RecyclerView morePlacesRV = findViewById(R.id.morePlacesRV);

                                if (!updatingRecyclerView){
                                    ViewGroup.LayoutParams topLayoutParams = topPlacesRV.getLayoutParams();
                                    topLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                    Top_Places_Recyclerview_Adapter topPlaceAdapter = new Top_Places_Recyclerview_Adapter(this, topPlaceList);
                                    topPlacesRV.setAdapter(topPlaceAdapter);
                                    topPlacesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                                    ViewGroup.LayoutParams moreLayoutParams = morePlacesRV.getLayoutParams();
                                    moreLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                    More_Places_Recyclerview_Adapter morePlaceAdapter = new More_Places_Recyclerview_Adapter(this, morePlaceList);
                                    morePlacesRV.setAdapter(morePlaceAdapter);
                                    morePlacesRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                                    updatingRecyclerView = true;
                                } else{
                                    Log.d("Update","Updating RecyclerView");
                                    // Update the adapter with the new list
                                    Top_Places_Recyclerview_Adapter topPlaceAdapter = new Top_Places_Recyclerview_Adapter(this, topPlaceList);
                                    topPlacesRV.swapAdapter(topPlaceAdapter, true);

                                    More_Places_Recyclerview_Adapter morePlaceAdapter = new More_Places_Recyclerview_Adapter(this, morePlaceList);
                                    morePlacesRV.setAdapter(morePlaceAdapter);
                                }

                                loadingDialog.dismissDialog();
                            }
                        });
                    } else {
                        Log.e("FetchPlaceDetailsTask", "Request failed: " + jsonObject.get("status").getAsString());
                    }

                } else {
                    Log.e("FetchPlaceDetailsTask", "Request failed: " + response.message());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // Function to dynamically adjust RecyclerView height
//    private void adjustRecyclerViewHeight(final RecyclerView recyclerView) {
//        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                RecyclerView.Adapter adapter = recyclerView.getAdapter();
//                if (adapter != null) {
//                    int totalHeight = 0;
//                    for (int i = 0; i < adapter.getItemCount(); i++) {
//                        View listItem = adapter.onCreateViewHolder(recyclerView, adapter.getItemViewType(i)).itemView;
//                        listItem.measure(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
//                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//                        totalHeight += listItem.getMeasuredHeight();
//                    }
//                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
//                    params.height = totalHeight + (recyclerView.getItemDecorationCount() * 10); // Add extra space for dividers
//                    params.height += recyclerView.getPaddingTop() + recyclerView.getPaddingBottom(); // Include padding
//                    recyclerView.setLayoutParams(params);
//                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//            }
//        });
//    }
}

