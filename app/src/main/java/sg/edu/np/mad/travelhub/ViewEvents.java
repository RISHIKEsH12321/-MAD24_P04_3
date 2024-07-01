package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewEvents extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 1;
    private DatabaseHandler dbHandler;
    private ViewEventAdapter eventAdapter;
    private ArrayList<CompleteEvent> events;
    private RecyclerView eventsContainer;
    int color1;
    int color2;
    int color3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.VEmain), (v, insets) -> {
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

        //Get IDs
        ImageButton vebtn = findViewById(R.id.VEsaveButton);
        TextView title = findViewById(R.id.calendartitle);

        //Change Colors
        title.setTextColor(color2);
        Drawable arrowDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_assignment_add_24);
        arrowDrawable.setTint(color1);
        vebtn.setImageDrawable(arrowDrawable);

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

        // Bottom Navigation View Logic to link to the different master activities
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavMenu);
        bottomNavigationView.setSelectedItemId(R.id.bottom_calendar);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home){
                startActivity(new Intent(this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                overridePendingTransition(0, 0);
                finish(); // Finish current activity if going back to HomeActivity
                return true;
            } else if (item.getItemId() == R.id.bottom_currency) {
                startActivity(new Intent(this, ConvertCurrency.class));
                overridePendingTransition(0, 0);

                finish();
            } else if (item.getItemId() == R.id.bottom_profile) {
                startActivity(new Intent(this, Profile.class));
                overridePendingTransition(0, 0);
                finish();
            }
            return true;
        });

        //Initialise dbhandlet to get and delete events
        dbHandler = new DatabaseHandler(this, null, null, 1);
        CalendarView calendarView = findViewById(R.id.VECalenderView);
        //Container to store all events on that day
        eventsContainer = findViewById(R.id.VEEventsContainer);

        // Get today's date
        long currentTimeMillis = System.currentTimeMillis();
        calendarView.setDate(currentTimeMillis, true, true);

        // Get and display today's event
        String selectedDate = getFormattedDate(currentTimeMillis);
        displayEvents(dbHandler.getEventONDate(selectedDate));


        //Buttons for new event
        ImageView addEvent = findViewById(R.id.VEsaveButton);

        //Go to Create New Activity
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewEvent(v);
            }
        });


        //When New Date is Picked, Get and Display that days events
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                int displayMonth = month + 1;
                String selectedDate = getFormattedDate(year, displayMonth, dayOfMonth);

                Log.d("VIEW EVENTS DATE", "Date: "+selectedDate);
                events = dbHandler.getEventONDate(selectedDate);
                Log.d("VIEW EVENTS DATA", "DATA LOG: " + events);

                displayEvents(events);

            }
        });
    }

    private void displayEvents(ArrayList<CompleteEvent> events){
        Log.d("IMAGEATTACHMENTINIMAGES", "displayEvents: " + events.toString());
        eventAdapter = new ViewEventAdapter(ViewEvents.this, events);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ViewEvents.this);

        eventsContainer.setLayoutManager(mLayoutManager);
        eventsContainer.setItemAnimator(new DefaultItemAnimator());
        eventsContainer.setAdapter(eventAdapter);

        //Delete that events id when Clicked from database and recyclerView
        eventAdapter.setOnItemClickListener(new ViewEventAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                // Capture event ID before removing item
                int id = Integer.parseInt(events.get(position).eventID);
                Log.d("DELETE EVENT TAG", "ID for deleted event = " + id);

                // Remove item from database
                dbHandler.deleteEventById(id);

                // Remove item from list and notify adapter
                events.remove(position);
                eventAdapter.notifyItemRemoved(position);
                eventAdapter.notifyItemRangeChanged(position, events.size());
            }
        });
    }

    private String getMonthFormat(int month) {
        if(month == 1) return "JAN";
        if(month == 2) return "FEB";
        if(month == 3) return "MAR";
        if(month == 4) return "APR";
        if(month == 5) return "MAY";
        if(month == 6) return "JUN";
        if(month == 7) return "JUL";
        if(month == 8) return "AUG";
        if(month == 9) return "SEP";
        if(month == 10) return "OCT";
        if(month == 11) return "NOV";
        if(month == 12) return "DEC";
        return "JAN";
    }


    private String getFormattedDate(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return getFormattedDate(year, month, dayOfMonth);
    }

    private String getFormattedDate(int year, int month, int dayOfMonth) {
        return getMonthFormat(month) + " " + dayOfMonth + " " + year; // For database paramaeter
    }


    // Method to handle button click to go back
    public void goBack(View view) {
        finish(); // Close the current activity and return to the previous one
    }

    public void createNewEvent(View view){
        //Go to EventManageMent class to create new event
        Intent intent = new Intent(view.getContext(), EventManagement.class);
        startActivity(intent);
    }

}
