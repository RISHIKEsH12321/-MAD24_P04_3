package sg.edu.np.mad.travelhub;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;


public class EventManagement extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private static final int PICK_FILE_REQUEST_CODE = 001;

    private static final String TAG = "Attachment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.EMmain), (v, insets) -> {
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

        //Change color for all drawables
        // Get drawables
        Drawable addBtnDrawable = ContextCompat.getDrawable(this, R.drawable.add_btn);
        Drawable plusDrawable = ContextCompat.getDrawable(this, R.drawable.baseline_add_24);
        // Apply tint color only to the add_btn drawable
        addBtnDrawable = DrawableCompat.wrap(addBtnDrawable);
        DrawableCompat.setTint(addBtnDrawable, color2);
        // Create a LayerDrawable and add both drawables to it
        Drawable[] layers = new Drawable[2];
        layers[0] = addBtnDrawable;
        layers[1] = plusDrawable;
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        // Set the LayerDrawable to the ImageButton
        ImageButton management = findViewById(R.id.EMitineraryAddEventNameBtn);
        management.setImageDrawable(layerDrawable);
        ImageButton bring = findViewById(R.id.EMitineraryAddBringItemBtn);
        bring.setImageDrawable(layerDrawable);
        ImageButton attachment = findViewById(R.id.EMattchmentBtn);
        attachment.setImageDrawable(layerDrawable);
        ImageButton reminder = findViewById(R.id.EMreminderAddBtn);
        reminder.setImageDrawable(layerDrawable);
        ImageButton notes = findViewById(R.id.EMnotesBtn);
        notes.setImageDrawable(layerDrawable);

        ImageButton backbtn = findViewById(R.id.backButton);
        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_ios_24);
        arrow.setTint(color1);
        backbtn.setImageDrawable(arrow);

        ImageButton savebtn = findViewById(R.id.saveButton);
        Drawable add = ContextCompat.getDrawable(this, R.drawable.baseline_assignment_add_24);
        add.setTint(color1);
        savebtn.setImageDrawable(add);

        // Wrap the drawable to ensure compatibility
        Drawable wrappedArrow = DrawableCompat.wrap(arrow);

        // Set the tint
        DrawableCompat.setTint(wrappedArrow, color1);

        // Setting the Name of the event to the place selected
        Intent intent = getIntent();
        Place place = (Place) intent.getSerializableExtra("place");
        if (!(place == null)){
            Log.d("PlaceName", place.getName());
            EditText EMtitle = findViewById(R.id.EMtitle);
            EMtitle.setText(place.getName());
        }

        //For Category Dropdown
        Spinner EMcategoryDropdown = (Spinner) findViewById(R.id.EMcategoryDropdown);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.event_categories,
                R.layout.em_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(R.layout.em_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        EMcategoryDropdown.setAdapter(adapter);


        //For Date Picker in Itinerary
        initDatePicker();
        dateButton = findViewById(R.id.EMdatePicker);
        dateButton.setText(getTodaysDate());

        //Image Display and Selection
        LinearLayout attachmentContainer = findViewById(R.id.EMattchmentContainer);
        ArrayList<ImageAttachment> attachmentImageList = new ArrayList<>();

        ImageButton selectFileButton = findViewById(R.id.EMattchmentBtn);
        //Getting Image From Local Storage
        //Lack Permission to display in View Events
        //Will make it work in Stage 2
        //Current Code will display the drawable no matter what is selected in the local storage
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            try {
                                Uri fileUri = result.getData().getData();
                                Log.d(TAG, "onCreate: " + fileUri);

                                if (fileUri != null) {
                                    String mimeType = getContentResolver().getType(fileUri);
                                    if (mimeType != null && mimeType.startsWith("image/")) {
                                        ImageAttachment imageAttachment = new ImageAttachment();
                                        imageAttachment.URI = fileUri;

                                        imageAttachment.exampleDrawable = "plane_ticket_example";
                                        attachmentImageList.add(imageAttachment);

                                        ImageView imageView = new ImageView(EventManagement.this);

                                        //Displaying the drawble not the image URI
                                        // Use Glide to load the image into the ImageView
                                        Glide.with(EventManagement.this)
                                                .load(R.drawable.plane_ticket_example)
                                                .apply(new RequestOptions().override(LinearLayout.LayoutParams.WRAP_CONTENT, 100)) // Set height to 100dp
                                                .into(imageView);

                                        // Set layout parameters for ImageView
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                                200 // Set height to 100dp
                                        );
                                        imageView.setLayoutParams(layoutParams);

                                        imageView.setOnClickListener(v -> {
                                            // Inflate the custom layout for the alert dialog
                                            View dialogView = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_image_dialog, null);

                                            // Get the ImageView from the custom layout
                                            ImageView fullSizeImageView = dialogView.findViewById(R.id.EMfullSizeImageView);

                                            // Load full-size image into the ImageView using Glide
                                            Glide.with(EventManagement.this)
                                                    .load(R.drawable.plane_ticket_example)
                                                    .into(fullSizeImageView);

                                            // Create and configure the AlertDialog
                                            AlertDialog.Builder builder = new AlertDialog.Builder(EventManagement.this);
                                            builder.setView(dialogView)
                                                    .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                                                    .setNegativeButton("Delete",(dialog, which) ->{
                                                        // Remove image from the container
                                                        attachmentContainer.removeView(imageView);
                                                        // Remove image from the list
                                                        attachmentImageList.remove(imageAttachment);
                                                        dialog.dismiss();
                                                    });


                                            // Show the AlertDialog
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();

                                        });

                                        attachmentContainer.addView(imageView);
                                    } else {
                                        Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Error: Null URI", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(this, "Error selecting file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        selectFileButton.setOnClickListener(view -> {
            Intent fileInput = new Intent(Intent.ACTION_GET_CONTENT);
            fileInput.setType("*/*");
            activityResultLauncher.launch(fileInput);
        });


        //Dialog For Adding Events
        ImageButton btnAddEvent = findViewById(R.id.EMitineraryAddEventNameBtn);
        //Mangaging RecyclerView for Events
        RecyclerView eventRvView =findViewById(R.id.EMrvViewItinerary);
        ArrayList<ItineraryEvent> itineraryEventList = new ArrayList<ItineraryEvent>();
        EventAdapter mAdapter = new EventAdapter(itineraryEventList);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        eventRvView.setLayoutManager(mLayoutManager);
        eventRvView.setItemAnimator(new DefaultItemAnimator());
        eventRvView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new EventAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(int position) {
                //Delete the item
                itineraryEventList.remove(position);
                //Notify Adapter
                mAdapter.notifyItemRemoved(position);
            }
        });

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //Mangaing Adding Items
        ImageButton btnAddBringItem = findViewById(R.id.EMitineraryAddBringItemBtn);
        RecyclerView bringItemRvView =findViewById(R.id.EMrvViewBringList);
        ArrayList<ToBringItem> toBringItems = new ArrayList<ToBringItem>();
        BringItemAdapter itemAdapter = new BringItemAdapter(toBringItems);

        LinearLayoutManager itemLayoutManager = new LinearLayoutManager(this);

        bringItemRvView.setLayoutManager(itemLayoutManager);
        bringItemRvView.setItemAnimator(new DefaultItemAnimator());
        bringItemRvView.setAdapter(itemAdapter);
        itemAdapter.setOnItemClickListener(new BringItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Delete the item
                toBringItems.remove(position);
                //Notify Adapter
                itemAdapter.notifyItemRemoved(position);
            }
        });

        itemLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //Add Notes
        ImageButton btnAddNotes = findViewById(R.id.EMnotesBtn);
        RecyclerView notesContainer = findViewById(R.id.EMnotesItem);
        ArrayList<String> notesList = new ArrayList<>();

        NotesAdapter notesAdapter = new NotesAdapter(notesList);

        LinearLayoutManager notesLayoutManager = new LinearLayoutManager(this);

        notesContainer.setLayoutManager(notesLayoutManager);
        notesContainer.setItemAnimator(new DefaultItemAnimator());
        notesContainer.setAdapter(notesAdapter);
        notesAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Delete the item
                notesList.remove(position);
                //Notify Adapter
                notesAdapter.notifyItemRemoved(position);
            }
        });

        //Add Reminders
        ImageButton btnAddReminder = findViewById(R.id.EMreminderAddBtn);
        RecyclerView reminderContainer = findViewById(R.id.EMreminderItems);
        ArrayList<String> reminderList = new ArrayList<>();
        ReminderAdapter remidnerAdapter = new ReminderAdapter(reminderList);

        LinearLayoutManager reminderLayoutManager = new LinearLayoutManager(this);

        reminderContainer.setLayoutManager(reminderLayoutManager);
        reminderContainer.setItemAnimator(new DefaultItemAnimator());
        reminderContainer.setAdapter(remidnerAdapter);
        remidnerAdapter.setOnItemClickListener(new ReminderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Delete the item
                reminderList.remove(position);
                //Notify Adapter
                remidnerAdapter.notifyItemRemoved(position);
            }
        });

        //Lists of data to be added to database
        //1. attachmentImageList
        //2. itineraryEventList
        //3. toBringItems
        //4. notesList
        //5. reminderList
        //6. Date
        //7. Complete Event Title


        //Adding to event and its data to database
        ImageButton finalSaveButton = findViewById(R.id.saveButton);

        DatabaseHandler dbHandler = new DatabaseHandler(this, null, null, 1);
//        dbHandler.dropTable();



        //Go Back
        ImageButton goBack = findViewById(R.id.backButton);
        //Goes to previous Activity
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(v);
            }
        });
        //Create Alert for Event Creation when clicking buttons
        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                View view = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_itinerary_dialog_layout,null);

                //Spinners to be inflated with the correct min and hour value arrays
                Spinner EventStartTimeHour = view.findViewById(R.id.EMitineraryAlertStartTimeHour); // Use the inflated view here
                Spinner EventStartTimeMin = view.findViewById(R.id.EMitineraryAlertStartTimeMin); // Use the inflated view here
                Spinner EventEndTimeHour = view.findViewById(R.id.EMitineraryAlertEndTimeHour); // Use the inflated view here
                Spinner EventEndTimeMin = view.findViewById(R.id.EMitineraryAlertEndTimeMin); // Use the inflated view here

                ArrayAdapter<CharSequence> arrayAdapterHour = ArrayAdapter.createFromResource(
                        EventManagement.this,
                        R.array.time_hours,
                        android.R.layout.simple_spinner_item
                );
                ArrayAdapter<CharSequence> arrayAdapterMin = ArrayAdapter.createFromResource(
                        EventManagement.this,
                        R.array.time_min,
                        android.R.layout.simple_spinner_item
                );

                arrayAdapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                arrayAdapterMin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                EventStartTimeHour.setAdapter(arrayAdapterHour);
                EventStartTimeMin.setAdapter(arrayAdapterMin);
                EventEndTimeHour.setAdapter(arrayAdapterHour);
                EventEndTimeMin.setAdapter(arrayAdapterMin);

                //Getting all input parameters
                TextInputEditText eventName = view.findViewById(R.id.EMitineraryAddEventName);
                TextInputEditText editNotes = view.findViewById(R.id.EMitineraryAddNotes);

                androidx.appcompat.app.AlertDialog alertDialog = new MaterialAlertDialogBuilder(EventManagement.this)
                        .setTitle("Add Event")
                        .setView(view)
                        //Completed All Inputs
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Getting Values
                                String eventNameText = eventName.getText().toString();
                                String eventNotesText = editNotes.getText().toString();

                                String startHour = EventStartTimeHour.getSelectedItem().toString();
                                String startMinute = EventStartTimeMin.getSelectedItem().toString();

                                String endHour = EventEndTimeHour.getSelectedItem().toString();
                                String endMinute = EventEndTimeMin.getSelectedItem().toString();

                                ItineraryEvent itineraryEvent = new ItineraryEvent(eventNameText,eventNotesText,startHour,startMinute,endHour,endMinute);
                                itineraryEventList.add(itineraryEvent);

                                // Notify the adapter about the new item
                                mAdapter.notifyItemInserted(itineraryEventList.size() - 1);


                                dialog.dismiss();
                            }
                        }
                        )
                        //Closes Dialog Alert
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                alertDialog.show();;
            }


        });

        btnAddBringItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_to_bring_item_dialog_layout,null);


                //Getting all input parameters (Item Name)
                TextInputEditText itemName = view.findViewById(R.id.EMitineraryAddBringItemInput);

                //Alert Creation
                androidx.appcompat.app.AlertDialog alertDialog = new MaterialAlertDialogBuilder(EventManagement.this)
                        .setTitle("Add Item To List")
                        .setView(view)
                        //Completed All Inputs
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Getting Values
                                        ToBringItem toBringItem = new ToBringItem();
                                        toBringItem.itemName = itemName.getText().toString();
                                        toBringItems.add(toBringItem);

                                        // Notify the adapter about the new item
                                        itemAdapter.notifyItemInserted(toBringItems.size() - 1);


                                        dialog.dismiss();
                                    }
                                }
                        )
                        //Closes Dialog Alert
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                alertDialog.show();;
            }


        });

        btnAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_to_bring_item_dialog_layout,null);


                //Getting all input parameters (Notes)
                TextInputEditText itemName = view.findViewById(R.id.EMitineraryAddBringItemInput);

                //Alert Creation
                androidx.appcompat.app.AlertDialog alertDialog = new MaterialAlertDialogBuilder(EventManagement.this)
                        .setTitle("Add Notes")
                        .setView(view)
                        //Completed All Inputs
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Getting Values


                                        // Notify the adapter about the new item

                                        if (itemName.getText().toString() != ""){
                                            String notes = itemName.getText().toString();

                                            notesList.add(notes);

                                            // Notify the adapter about the new item
                                            notesAdapter.notifyItemInserted(reminderList.size() - 1);
                                        }else{
                                            Toast.makeText(v.getContext(), "No Input", Toast.LENGTH_SHORT).show();

                                        }
                                        dialog.dismiss();
                                    }
                                }
                        )
                        //Closes Dialog Alert
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                alertDialog.show();;
            }


        });

        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(EventManagement.this).inflate(R.layout.em_to_bring_item_dialog_layout,null);


                //Getting all input parameters
                TextInputEditText itemName = view.findViewById(R.id.EMitineraryAddBringItemInput);

                //Alert Creation
                androidx.appcompat.app.AlertDialog alertDialog = new MaterialAlertDialogBuilder(EventManagement.this)
                        .setTitle("Add Reminder")
                        .setView(view)
                        //Completed All Inputs
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Getting Values
                                        if (itemName.getText().toString() != ""){
                                            String reminder = itemName.getText().toString();
                                            reminderList.add(reminder);

                                            // Notify the adapter about the new item
                                            remidnerAdapter.notifyItemInserted(reminderList.size() - 1);
                                        }else{
                                            Toast.makeText(v.getContext(), "No Input", Toast.LENGTH_SHORT).show();

                                        }



                                        dialog.dismiss();
                                    }
                                }
                        )
                        //Closes Dialog Alert
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                alertDialog.show();;
            }


        });

        //Puts all data in a CompleteEvent Item and send it to databse to be added to the tables
        finalSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Selected Date
                String date = String.valueOf(dateButton.getText());

                // Get the selected item
                Object selectedItem = EMcategoryDropdown.getSelectedItem();
                // Convert the selected item to a string
                String category = selectedItem.toString();

                //Getting Title Of Entire Event
                EditText EMtitle = findViewById(R.id.EMtitle);
                String title = String.valueOf(EMtitle.getText());

                Log.d(TAG, String.valueOf(attachmentImageList.size()));
                //Creating Complete Event Object
                CompleteEvent dbEvent = new CompleteEvent(
                        attachmentImageList,
                        itineraryEventList,
                        toBringItems,
                        notesList,
                        reminderList,
                        date,
                        category,
                        title);
                Log.d("ATTACHMENTS", attachmentImageList.toString());
                try{
                    //Adding to Database
                    dbHandler.addEvent(dbEvent);
                    goBack(v);
                }catch (SQLiteException e) {
                    Toast.makeText(EventManagement.this, "Error", Toast.LENGTH_SHORT).show();
                    Log.i("Database Operations", "Error creating tables", e);
                }

            }
        });

    }
    //Date Input
    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }


    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
//        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

    }

    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }


    //Go back Button
    // Method to handle button click to go back
    public void goBack(View view) {
        finish(); // Close the current activity and return to the previous one
    }



}