package sg.edu.np.mad.travelhub;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class EditProfile extends AppCompatActivity {
    ImageView image;
    EditText etName, etDescription, etId;
    String name, description, id;
    Button btnDone;
    FirebaseUser fbuser;
    FirebaseDatabase db;
    DatabaseReference myRef;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    Uri imageUri;
    String downloadUrl;
    private ActivityResultLauncher<Intent> getResult;

    public static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.EPmain), (v, insets) -> {
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

        // Get IDs
        Button donebtn = (Button) findViewById(R.id.doneButton);
        TextView nameheader = findViewById(R.id.editNameHeader);
        TextView descriptionheader = findViewById(R.id.editDescriptionHeader);
        TextView idheader = findViewById(R.id.editIDHeader);

        // Change colour of IDs
        donebtn.setBackgroundTintList(ColorStateList.valueOf(color1));
        nameheader.setTextColor(color1);
        descriptionheader.setTextColor(color1);
        idheader.setTextColor(color1);

        //name and description and id
        etName = findViewById(R.id.editName);
        etDescription = findViewById(R.id.editDescription);
        etId = findViewById(R.id.editID);
        db = FirebaseDatabase.getInstance();
        myRef = db.getReference("Users");
        //get Firebase user
        fbuser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = fbuser.getUid(); //get uid of user
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);


        //retrieve info and set as text in textedit (COMPLETE)
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userObject = snapshot.getValue(User.class); // Assuming your User class exists
                    if (userObject != null) {
                        name = userObject.getName();
                        etName.setText(name);
                        description = userObject.getDescription();
                        etDescription.setText(description);
                        id = userObject.getId();
                        etId.setText(id);
                        // Update UI elements with retrieved name and description
                    } else {
                        Log.w("TAG", "User object not found in database");
                    }
                } else {
                    Log.d("TAG", "No user data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Error retrieving user data", error.toException());
            }
        });
        // Register the activity result launcher
        getResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // Handle the Intent result here
                    imageUri = data.getData();
                    image.setImageURI(imageUri);
                    Log.d("IMAGEURI", String.valueOf(imageUri));
                    // Now that you have the image URI, you can proceed with uploading
                    uploadToFirebase(uid, imageUri);
                }
            }
        });

        StorageReference imagesRef = storageRef.child("images");
        image = findViewById(R.id.profilePic);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getResult.launch(intent);
            }
        });

        //when u press done, it writes all your info
        btnDone = findViewById(R.id.doneButton);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get name and description
                name = etName.getText().toString();
                description = etDescription.getText().toString();
                id = etId.getText().toString();
                //Check if user entered name and description
                ;
                // Check if user entered name and description
                if (!name.isEmpty() && !description.isEmpty()) {
                    // Update specific fields for the logged-in user

                    // Create a HashMap to store updated values
                    HashMap<String, Object> updates = new HashMap<>();
                    updates.put("name", name);
                    updates.put("description", description);
                    updates.put("id", id);
                    myRef.child(uid).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                etName.setText("");
                                etDescription.setText("");
                                etId.setText("");
                                Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                                // Consider staying on the EditProfile page (optional)
                            } else {
                                Log.e("Save", "Failed to update user", task.getException());
                                Toast.makeText(getApplicationContext(), "Failed to update profile: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                Intent backToProfile = new Intent(getApplicationContext(), Profile.class);
                startActivity(backToProfile);

            }
        });

        loadUserImage();
    }
    private void loadUserImage() {
        if (fbuser != null) {
            String uid = fbuser.getUid();
            myRef.child(uid).child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String imageUrl = snapshot.getValue(String.class);
                        loadImageIntoImageView(imageUrl);
                    } else {
                        Toast.makeText(EditProfile.this, "No image found for user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditProfile.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadImageIntoImageView(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .transform(new CircleCrop()) // Apply the CircleCrop transformation
                .skipMemoryCache(true) // Disable memory cache
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk cache
                .into(image);
    }

    private void uploadToFirebase(String uid, Uri imUri) {
        StorageReference fileRef = storageRef.child(uid + "." + getFileExtension(imUri));
        fileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl = uri.toString();
                            }
                        });
                        Toast.makeText(getApplicationContext(), "Image successfully uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle image upload failure
                        Log.e("Upload", "Failed to upload image", e);
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private String getFileExtension(Uri imUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imUri));
    }
}