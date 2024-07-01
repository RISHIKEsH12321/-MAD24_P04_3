package sg.edu.np.mad.travelhub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.media.Image;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseHandler extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "travelhub.db";
    private static final String EVENTS = "events";
    //Tables
    private static final String EVENTS_TABLE = "events";
    private static final String ATTACHMENT_IMAGES_TABLE = "attachment_images";
    private static final String ITINERARY_EVENTS_TABLE = "itinerary_events";
    private static final String ITEMS_TABLE = "items";
    //Columns
    private static final String EVENT_ID = "id";
    private static final String EVENT_NAME = "name";
    private static final String DATE = "date";
    private static final String CATEGORY = "category";
    private static final String IMAGE_URI = "image_uri";
    private static final String IMAGE_ID = "image_id";
    private static final String ITINERARY_EVENT = "itinerary_event";
    private static final String ITINERARY_EVENT_ID = "itinerary_event_id";
    private static final String ITEM = "item";
    private static final String ITEM_ID = "item_id";
    private static final String TICKED = "ticked";
    private static final String NOTE = "note";
    private static final String REMINDER = "reminder";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String IT_NOTE = "it_note";


    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db){

        Log.i("Database Operations", "Creating a Table.");
        try {

            // Creating the EVENTS table
            String CREATE_EVENTS_TABLE = "CREATE TABLE " + EVENTS_TABLE +
                    "(" + EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + EVENT_NAME + " TEXT,"
                    + DATE + " DATE,"
                    + CATEGORY + " TEXT,"
                    + NOTE + " TEXT,"
                    + REMINDER + " TEXT"
                    + ")";

//            // Creating the ATTACHMENT_IMAGES table
            String CREATE_ATTACHMENT_IMAGES_TABLE =
                    "CREATE TABLE " + ATTACHMENT_IMAGES_TABLE +
                    "(" + EVENT_ID + " TEXT,"
                    + IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + IMAGE_URI + " TEXT,"
                    + "FOREIGN KEY(" + EVENT_ID + ") REFERENCES " + EVENTS_TABLE + "(" + EVENT_ID + ")"
                    + ")";
//
            // Creating the ITINERARY_EVENTS table
            String CREATE_ITINERARY_EVENTS_TABLE = "CREATE TABLE " + ITINERARY_EVENTS_TABLE +
                    "(" + EVENT_ID  + " TEXT ,"
                    + ITINERARY_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ITINERARY_EVENT + " TEXT,"
                    + START_TIME + " TEXT,"
                    + END_TIME + " TEXT,"
                    + IT_NOTE + " TEXT,"
                    + "FOREIGN KEY(" + EVENT_ID + ") REFERENCES " + EVENTS_TABLE + "(" + EVENT_ID + ")"
                    + ")";

            String CREATE_ITEMS_TABLE = "CREATE TABLE " + ITEMS_TABLE +
                    "(" + EVENT_ID + " TEXT,"
                    + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ITEM + " TEXT,"
                    + TICKED + " BOOLEAN,"
                    + "FOREIGN KEY(" + EVENT_ID + ") REFERENCES " + EVENTS_TABLE + "(" + EVENT_ID + ")"
                    + ")";

            // Execute the SQL statements
            db.execSQL(CREATE_EVENTS_TABLE);
            db.execSQL(CREATE_ATTACHMENT_IMAGES_TABLE);
            db.execSQL(CREATE_ITINERARY_EVENTS_TABLE);
            db.execSQL(CREATE_ITEMS_TABLE);

            Log.i("Database Operations", "Tables created successfully.");
//          db.close();
        } catch (SQLiteException e) {
            Log.i("Database Operations", "Error creating tables", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + EVENTS);
        onCreate(db);
    }

    //  Add a user record
    public void addEvent(CompleteEvent completeEvent){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues eventBaseValues = new ContentValues();
        StringBuilder notes = new StringBuilder();
        StringBuilder items =new StringBuilder();
        StringBuilder reminders =new StringBuilder();
        for (String x:completeEvent.notesList) {
            notes.append(x).append("!NOTES_APPENDING_BANANA!");
        }
        for (String x:completeEvent.reminderList) {
            reminders.append(x).append("!REMINDER_APPENDING_BANANA!");
        }
        //Creating Event
        eventBaseValues.put(EVENT_NAME,completeEvent.eventName);
        eventBaseValues.put(DATE,completeEvent.date);
        eventBaseValues.put(CATEGORY,completeEvent.category);
        eventBaseValues.put(NOTE, notes.toString());
        eventBaseValues.put(REMINDER,reminders.toString());
        Log.d("ADDING TO EVENTS", "NOTES: " + notes);
        Log.d("ADDING TO EVENTS", "ITEM: " + items);
        Log.d("ADDING TO EVENTS", "REMINDER: " + reminders);

//        db.insert(EVENTS, null, eventBaseValues);
        long eventId = db.insert(EVENTS, null, eventBaseValues);

        if (eventId != -1) {

            if (completeEvent.itineraryEventList != null && !completeEvent.itineraryEventList.isEmpty()){
                for (ItineraryEvent itineraryEvent : completeEvent.itineraryEventList) {
                    ContentValues itineraryValues = new ContentValues();
                    itineraryValues.put(EVENT_ID, eventId);
                    itineraryValues.put(ITINERARY_EVENT, itineraryEvent.eventName);
                    itineraryValues.put(START_TIME, formatTime(itineraryEvent.startHour, itineraryEvent.startMin));
                    itineraryValues.put(END_TIME, formatTime(itineraryEvent.endHour, itineraryEvent.endMin));
                    itineraryValues.put(IT_NOTE, itineraryEvent.eventNotes);
                    db.insert(ITINERARY_EVENTS_TABLE, null, itineraryValues);
                }
            }

            if (completeEvent.attachmentImageList != null && !completeEvent.attachmentImageList.isEmpty()) {
                for (ImageAttachment imageUri : completeEvent.attachmentImageList) {
                    ContentValues values = new ContentValues();
                    values.put(EVENT_ID, eventId);
                    values.put(IMAGE_URI, imageUri.URI.toString());
                    Log.i("Database Operations", "Creating image");

                    db.insert(ATTACHMENT_IMAGES_TABLE, null, values);
                }
            }

            if (completeEvent.toBringItems != null && !completeEvent.toBringItems.isEmpty()) {

                for (ToBringItem s : completeEvent.toBringItems) {
                    ContentValues itemvalues = new ContentValues();
                    itemvalues.put(EVENT_ID, eventId);
                    itemvalues.put(TICKED, false);
                    itemvalues.put(ITEM, s.itemName);

                    db.insert(ITEMS_TABLE, null, itemvalues);

                }
            }
            }

//      db.close();
    }

    public Uri getImageUri(){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + ATTACHMENT_IMAGES_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            String uriString = cursor.getString((int)cursor.getColumnIndex(IMAGE_URI));
            Log.d("Image Insert", "Image URI: " + uriString);

            return Uri.parse(uriString);
        }
        cursor.close();
        return null;
    }

    public void dropTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHandler.EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHandler.ATTACHMENT_IMAGES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHandler.ITINERARY_EVENTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseHandler.ITEMS_TABLE);
        onCreate(db);
//        db.close();
    }

    public ArrayList<CompleteEvent> getEventONDate(String date){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<CompleteEvent> evtList = new ArrayList<>();

        String query = "SELECT * FROM " + EVENTS_TABLE + " WHERE " + DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date}); // Add the date as a parameter

        if (cursor.moveToFirst()) {
            do {
                // Create a CompleteEvent object from cursor data
                CompleteEvent completeEvent = new CompleteEvent();
                String eventID = cursor.getString((int)cursor.getColumnIndex(EVENT_ID));
                completeEvent.eventName = cursor.getString((int)cursor.getColumnIndex(EVENT_NAME));
                completeEvent.eventID = eventID;
                completeEvent.date = cursor.getString((int)cursor.getColumnIndex(DATE));
                completeEvent.category = cursor.getString((int)cursor.getColumnIndex(CATEGORY));


                // Extract notes, reminders, and to-bring items by parsing the stored strings
                String notesString = cursor.getString((int)cursor.getColumnIndex(NOTE));
                if (notesString != null && !notesString.isEmpty()) {
                    completeEvent.notesList = new ArrayList<>(Arrays.asList(notesString.split("!NOTES_APPENDING_BANANA!")));
                }
                String remindersString = cursor.getString((int)cursor.getColumnIndex(REMINDER));
                if (remindersString != null && !remindersString.isEmpty()) {
                    completeEvent.reminderList = new ArrayList<>(Arrays.asList(remindersString.split("!REMINDER_APPENDING_BANANA!")));
                }




                String queryItems = "SELECT * FROM " + ITEMS_TABLE + " WHERE " + EVENT_ID + " = ?";

                Cursor ItemsCursor = db.rawQuery(queryItems,new String[]{eventID});
                ArrayList<ToBringItem> itemsList = new ArrayList<>();
                if (ItemsCursor.moveToFirst()) {
                    do {
                        ToBringItem item = new ToBringItem();
                        int tickedInt = ItemsCursor.getInt((int) ItemsCursor.getColumnIndex(TICKED));
                        item.ticked = (tickedInt == 1);
                        item.eventID = ItemsCursor.getString((int) ItemsCursor.getColumnIndex(EVENT_ID));
                        item.itemID = ItemsCursor.getString((int) ItemsCursor.getColumnIndex(ITEM_ID));
                        item.itemName = ItemsCursor.getString((int) ItemsCursor.getColumnIndex(ITEM));
                        itemsList.add(item);
                    } while (ItemsCursor.moveToNext());
                }
                completeEvent.toBringItems = itemsList;
                ItemsCursor.close();


                String queryItEvents = "SELECT " +
                        "* FROM " + ITINERARY_EVENTS_TABLE +
                        " WHERE " + EVENT_ID + " = ?";  // Use prepared statement

                ArrayList<ItineraryEvent> itineraryEventsList = new ArrayList<>();
                Cursor eventCursor = db.rawQuery(queryItEvents, new String[]{eventID});
                if (eventCursor.moveToFirst()) {
                    do {
                        ItineraryEvent itineraryEvent = new ItineraryEvent();
                        itineraryEvent.eventName = eventCursor.getString((int) eventCursor.getColumnIndexOrThrow(ITINERARY_EVENT));
                        itineraryEvent.eventID = eventCursor.getString((int) eventCursor.getColumnIndexOrThrow(EVENT_ID));
                        itineraryEvent.eventNotes = eventCursor.getString((int) eventCursor.getColumnIndexOrThrow(IT_NOTE));

                        // Parsing start time
                        String startTimeString = eventCursor.getString((int)eventCursor.getColumnIndexOrThrow(START_TIME));
                        String[] startTimeParts = startTimeString.split(":");
                        itineraryEvent.startHour = startTimeParts[0];
                        itineraryEvent.startMin = startTimeParts[1];

                        // Parsing end time
                        String endTimeString = eventCursor.getString((int)eventCursor.getColumnIndexOrThrow(END_TIME));
                        String[] endTimeParts = endTimeString.split(":");
                        itineraryEvent.endHour = endTimeParts[0];
                        itineraryEvent.endMin = endTimeParts[1];

                        itineraryEventsList.add(itineraryEvent);
                    } while (eventCursor.moveToNext());
                }
                eventCursor.close();

                completeEvent.itineraryEventList = itineraryEventsList;



                String queryItImages = "SELECT * FROM " + ATTACHMENT_IMAGES_TABLE + " WHERE " + EVENT_ID + " = ?";

                ArrayList<ImageAttachment> imgUriEventsList = new ArrayList<>();
                Cursor ImageCursor = db.rawQuery(queryItImages, new String[]{eventID});

                if (ImageCursor.moveToFirst()) {
                    do {

                        ImageAttachment imageAttachment = new ImageAttachment();
                        // Assuming URI is the only column retrieved
                        String imageUriString = ImageCursor.getString((int) ImageCursor.getColumnIndexOrThrow(IMAGE_URI));
                        imageAttachment.URI = Uri.parse(imageUriString);
                        // Assuming EventId is the correct field name
                        imageAttachment.EventId = ImageCursor.getString((int) ImageCursor.getColumnIndexOrThrow(EVENT_ID));
                        imageAttachment.ImageId = ImageCursor.getString((int) ImageCursor.getColumnIndexOrThrow(IMAGE_ID));
                        Log.d("IMAGEATTACHMENTINIMAGES", "ID: " + imageAttachment.ImageId);
                        imgUriEventsList.add(imageAttachment);
                    } while (ImageCursor.moveToNext());
                }

                ImageCursor.close();  // Close the cursor after use
                completeEvent.attachmentImageList = imgUriEventsList;
                evtList.add(completeEvent);
                Log.d("VIEW EVENTS DATA", "Data LOG DATABASE:" + completeEvent.toString());

            } while (cursor.moveToNext());

        }else{
            Log.d("getEventONDate", "No events found for the given date.");
        }


        cursor.close();
        return evtList;
    }


    public void deleteEventById(int id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            // Begin a transaction
            db.beginTransaction();

            // Delete from ITEMS_TABLE
            db.delete(ITEMS_TABLE, EVENT_ID + "=?", new String[]{String.valueOf(id)});

            // Delete from ITINERARY_EVENTS_TABLE
            db.delete(ITINERARY_EVENTS_TABLE, EVENT_ID + "=?", new String[]{String.valueOf(id)});

            // Delete from ATTACHMENT_IMAGES_TABLE
            db.delete(ATTACHMENT_IMAGES_TABLE, EVENT_ID + "=?", new String[]{String.valueOf(id)});

            // Delete from EVENTS_TABLE
            db.delete(EVENTS_TABLE, EVENT_ID + "=?", new String[]{String.valueOf(id)});

            // Set transaction as successful
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e("Database Operations", "Error deleting event", e);
        } finally {
            // End the transaction
            db.endTransaction();
        }

        Log.i("Database Operations", "Event and related data deleted successfully.");
    }


    public boolean checkItem(String id) {
        SQLiteDatabase db = getWritableDatabase();
        boolean newTickedValue = false;

        Log.d("DATABASE ITEM UPDATE", "checkItem id: " +id);
        try {
            // Begin a transaction
            db.beginTransaction();

            // Prepare the update query to toggle the ticked value
            String sql = "UPDATE " + ITEMS_TABLE + " SET " + TICKED + " = CASE WHEN " + TICKED + " = 0 THEN 1 ELSE 0 END WHERE " + ITEM_ID + " = ?";
            SQLiteStatement statement = db.compileStatement(sql);
            statement.bindString(1, id);




            // Execute the update
            statement.executeUpdateDelete();

            // Mark the transaction as successful
            db.setTransactionSuccessful();

            // Query the updated value
            String query = "SELECT " + TICKED + " FROM " + ITEMS_TABLE + " WHERE " + ITEM_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{id});
            if (cursor.moveToFirst()) {
                int tickedIndex = cursor.getColumnIndex(TICKED);
                if (tickedIndex != -1) {
                    newTickedValue = cursor.getInt(tickedIndex) != 0;
                    Log.d("DATABASE ITEM UPDATE", "checkItem ticked: " +newTickedValue);

                }
            }
            cursor.close();
        } catch (SQLiteException e) {
            Log.e("Database Operations", "Error updating item", e);
        } finally {
            // End the transaction
            db.endTransaction();
        }

        return newTickedValue;
    }



    private String formatTime(String hour, String minute) {
        return String.format(hour + ":" + minute);
    }
    private String[] unFormatTime(String time) {
        return time.split(":");
    }


}
