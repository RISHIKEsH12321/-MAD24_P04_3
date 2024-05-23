package sg.edu.np.mad.travelhub;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ConvertCurrency extends AppCompatActivity {

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
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://currency-conversion-and-exchange-rates.p.rapidapi.com/convert?from=USD&to=EUR&amount=750")
                        .get()
                        .addHeader("X-RapidAPI-Key", "895d045db7msh990d79ec4410e81p18fab6jsnc37612f63fe5")
                        .addHeader("X-RapidAPI-Host", "currency-conversion-and-exchange-rates.p.rapidapi.com")
                        .build();

                Double result;

                try {
                    result = Double.parseDouble(client.newCall(request).execute().body().string());
                } catch (IOException e) {
                    result = 0.0;
                }
            }
        });
    }
}