package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
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
    SharedPreferences.Editor editor;

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

        //Add dropdown options
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

        //Themes
        switchmode = findViewById(R.id.switchmode);
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightmode = sharedPreferences.getBoolean("nightmode", false);
        if (nightmode){
            switchmode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        switchmode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (nightmode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("nightmode", false);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("nightmode", true);
                }
                editor.apply();
            }
        });

        //Call API
        // Get IDs
        Button button = findViewById(R.id.button);
        EditText inputEditText = findViewById(R.id.inputconverter);

        String amount = inputEditText.getText().toString();
        String startCurrency = startSpinner.getSelectedItem().toString();
        String endCurrency = endSpinner.getSelectedItem().toString();


        // Set a click listener on the button
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

                // Check for errors
                if (amount.isEmpty() || startCurrency.isEmpty() || endCurrency.isEmpty()) {
                    // Handle error
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
                            // Parse JSON response
                            JSONObject jsonObject = new JSONObject(response);
                            final Double result = jsonObject.getDouble("result");

                            // Update UI in the main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView calculatedAmtTextView = findViewById(R.id.calculatedamt);
                                    calculatedAmtTextView.setText(String.valueOf(result));
                                }
                            });
                        } catch (IOException | JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error making API request or parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        });
    }
}