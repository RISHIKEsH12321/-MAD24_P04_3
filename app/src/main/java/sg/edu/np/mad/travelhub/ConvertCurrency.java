package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        //Light & Dark Mode
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

        // Set the adapter for themes
        ArrayAdapter<CharSequence> themeadapter = ArrayAdapter.createFromResource(this,
                R.array.themes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner themesSpinner = findViewById(R.id.colour_themes);
        themesSpinner.setAdapter(themeadapter);

        // Black and Light Mode
        switchmode = findViewById(R.id.switchmode);
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightmode = sharedPreferences.getBoolean("nightmode", false);

        // Set the custom drawables for the switch thumb and track
        switchmode.setThumbResource(R.drawable.thumb);
        switchmode.setTrackResource(R.drawable.track);

        // Set the switch state and app mode based on saved preference
        if (nightmode) {
            switchmode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            switchmode.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Set up the switch click listener
        switchmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (nightmode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("nightmode", false);
                    nightmode = false;
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("nightmode", true);
                    nightmode = true;
                }
                editor.apply();
            }
        });

        //Change Themes Function
        // Get IDs for all needed elements
        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedPosition = preferences.getInt("selected_spinner_position", 0);
        themesSpinner.setSelection(selectedPosition);

        Button button = findViewById(R.id.convertbutton);
        TextView title = findViewById(R.id.convertertitle);
        themesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Use Shared Preference to save the Spinner setting
                SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("selected_spinner_position", position);
                editor.apply();

                // Get input from Spinner
                String selectedTheme = parent.getItemAtPosition(position).toString();

                // Apply the selected theme
                getTheme().applyStyle(R.style.Base_Theme_TravelHub, true);
                switch (selectedTheme) {
                    case "Default":
                        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.main_orange)));
                        title.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.main_orange)));
                        break;
                    case "Watermelon":
                        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.wm_green)));
                        title.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.wm_green)));
                        break;
                    case "Neon":
                        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.nn_pink)));
                        title.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.nn_pink)));
                        break;
                    case "Protanopia":
                        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.pro_purple)));
                        title.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.pro_purple)));
                        break;
                    case "Deuteranopia":
                        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.deu_yellow)));
                        title.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.deu_yellow)));
                        break;
                    case "Tritanopia":
                        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tri_orange)));
                        title.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.tri_orange)));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
