package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Smain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> themeadapter = ArrayAdapter.createFromResource(this,
                R.array.themes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner themesSpinner = findViewById(R.id.colour_themes);
        themesSpinner.setAdapter(themeadapter);
        SharedPreferences preferences = getSharedPreferences("spinner_preferences", MODE_PRIVATE);
        int selectedPosition = preferences.getInt("selected_spinner_position", 0);
        themesSpinner.setSelection(selectedPosition);

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
                //Get IDs
                Button ltgcbtn = findViewById(R.id.ltgcButton);
                Button epbtn = findViewById(R.id.epButton);
                TextView themesheader = findViewById(R.id.themesheader);
                TextView mpnheader = findViewById(R.id.mpnHeader);
                TextView asheader = findViewById(R.id.asHeader);
                TextView baheader = findViewById(R.id.baHeader);
                TextView ltgcheader = findViewById(R.id.ltgcHeader);
                TextView epheader = findViewById(R.id.epHeader);
                SwitchMaterial mpnbtn = findViewById(R.id.mpnButton);
                SwitchMaterial asbtn = findViewById(R.id.asButton);
                SwitchMaterial babtn = findViewById(R.id.baButton);

                //Change Colors
                ltgcbtn.setBackgroundTintList(ColorStateList.valueOf(color1));
                epbtn.setBackgroundTintList(ColorStateList.valueOf(color1));
                themesheader.setTextColor(color1);
                mpnheader.setTextColor(color1);
                asheader.setTextColor(color1);
                baheader.setTextColor(color1);
                ltgcheader.setTextColor(color1);
                epheader.setTextColor(color1);
                mpnbtn.setThumbTintList(ColorStateList.valueOf(color1));
                asbtn.setThumbTintList(ColorStateList.valueOf(color1));
                babtn.setThumbTintList(ColorStateList.valueOf(color1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backtoProfile = new Intent(getApplicationContext(), Profile.class);
                startActivity(backtoProfile);
            }
        });

        // Storing data into SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("settings",MODE_PRIVATE);

// Creating an Editor object to edit(write to the file)
        boolean mpnState = sharedPreferences.getBoolean("mpn", false);
        boolean asState = sharedPreferences.getBoolean("as", false);
        boolean baState = sharedPreferences.getBoolean("ba", false);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        Button epButton =findViewById(R.id.epButton);

        SwitchMaterial mpnButton =findViewById(R.id.mpnButton);
        SwitchMaterial baButton =findViewById(R.id.baButton);
        SwitchMaterial asButton =findViewById(R.id.asButton);
        mpnButton.setChecked(sharedPreferences.getBoolean("mpn", false));
        asButton.setChecked(sharedPreferences.getBoolean("as", false));
        baButton.setChecked(sharedPreferences.getBoolean("ba", false));

        myEdit.putBoolean("mpn", mpnState);
        myEdit.putBoolean("as", asState);
        myEdit.putBoolean("ba", baState);
        epButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfile = new Intent(getApplicationContext(), EditProfile.class);
                startActivity(editProfile);
            }
        });

        mpnButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myEdit.putBoolean("mpn", isChecked);
                myEdit.apply();
            }
        });

        asButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myEdit.putBoolean("as", isChecked);
                myEdit.apply();
            }
        });

        baButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myEdit.putBoolean("ba", isChecked);
                myEdit.apply();
            }
        });

        myEdit.commit();



    }
}