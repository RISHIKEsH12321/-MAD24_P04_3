package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ConvertCurrency extends AppCompatActivity {

    SwitchCompat switchmode;
    boolean nightmode;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_convert_currency);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bottom Navigation View Logic to link to the different master activities
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_currency);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_calendar){
                startActivity(new Intent(this, ViewEvents.class));
                overridePendingTransition(0, 0);
                finish();
            } else if (item.getItemId() == R.id.bottom_home) {
                startActivity(new Intent(this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                finish(); // Finish current activity if going back to HomeActivity
                return true;
            } else if (item.getItemId() == R.id.bottom_profile) {
                startActivity(new Intent(this, Profile.class));
                overridePendingTransition(0, 0);
                finish();
            }
            return true;
        });

        // Initialize the adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter for the first spinner
        Spinner startSpinner = findViewById(R.id.start);
        startSpinner.setAdapter(adapter);

        // Set the adapter for the second spinner
        Spinner endSpinner = findViewById(R.id.end);
        endSpinner.setAdapter(adapter);

        Button button = findViewById(R.id.convertbutton);
        TextView title = findViewById(R.id.convertertitle);

        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedSpinnerPosition = preferences.getInt("selected_spinner_position", 0);
        String selectedTheme = getResources().getStringArray(R.array.themes)[selectedSpinnerPosition];

        int color1;
        int color2;
        int color3;

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

        // Currency Converter Page
        title.setTextColor(color1);
        button.setBackgroundTintList(ColorStateList.valueOf(color1));

        //Change color for Bottom NavBar
        BottomNavigationView bottomNavMenu = (BottomNavigationView) findViewById(R.id.bottomNavMenu);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected},
                new int[]{} // default state
        };
        int[] colors = new int[]{
                color1,
                ContextCompat.getColor(ConvertCurrency.this, R.color.unselectedNavBtn)
        };
        ColorStateList colorStateList = new ColorStateList(states, colors);
        bottomNavMenu.setItemIconTintList(colorStateList);


        // Call API
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get IDs
                EditText inputEditText = findViewById(R.id.inputconverter);
                Spinner startSpinner = findViewById(R.id.start);
                Spinner endSpinner = findViewById(R.id.end);

                // Get text and selected items
                String amount = inputEditText.getText().toString();
                String startCurrency = startSpinner.getSelectedItem().toString();
                String endCurrency = endSpinner.getSelectedItem().toString();

                // Error handling
                if (amount.isEmpty() || startCurrency.isEmpty() || endCurrency.isEmpty()) {
                    // Handle error
                    return;
                }

                float amountValue;
                try {
                    amountValue = Float.parseFloat(amount);
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amountValue == 0) {
                    Toast.makeText(getApplicationContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Make API request in a separate thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();

                        Request request = new Request.Builder()
                                .url("https://currency-conversion-and-exchange-rates.p.rapidapi.com/convert?from=" + startCurrency + "&to=" + endCurrency + "&amount=" + amount)
                                .get()
                                .addHeader("X-RapidAPI-Key", "895d045db7msh990d79ec4410e81p18fab6jsnc37612f63fe5")
                                .addHeader("X-RapidAPI-Host", "currency-conversion-and-exchange-rates.p.rapidapi.com")
                                .build();

                        try {
                            String response = client.newCall(request).execute().body().string();
                            JSONObject jsonObject = new JSONObject(response);
                            final Double result = jsonObject.getDouble("result");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView calculatedAmtTextView = findViewById(R.id.calculatedamt);
                                    calculatedAmtTextView.setText(String.valueOf(result));
                                }
                            });
                        } catch (IOException | JSONException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Error making API request", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
}