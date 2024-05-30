package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {

    TextInputEditText etEmail, etPassword;
    Button btnRegister;
    FirebaseAuth mAuth;
    TextView tvLogin;
    FirebaseDatabase databaseUser = FirebaseDatabase.getInstance();

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.LetEmail);
        etPassword = findViewById(R.id.LetPassword);
        tvLogin = findViewById(R.id.LtvLoginHere);
        btnRegister = findViewById(R.id.LbtnRegister);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // TextWatcher to validate email as user types
        etEmail.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler(Looper.getMainLooper());
            private Runnable workRunnable;

            @Override
            public void beforeTextChanged (CharSequence s,int start, int count, int after){
                TextInputLayout emailLayout = findViewById(R.id.ARBoxEmail);
                Context context = Register.this;
                Drawable cancelDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_cancel, context.getTheme());
                emailLayout.setEndIconDrawable(cancelDrawable);
            }

            @Override
            public void onTextChanged (CharSequence s,int start, int before, int count){
                // Remove any pending posts of workRunnable
                if (workRunnable != null) {
                    handler.removeCallbacks(workRunnable);
                }
            }

            @Override
            public void afterTextChanged (Editable s){
                final String email = s.toString().trim();
                Log.d("TextWatcher", "Email entered: " + email);
                workRunnable = new Runnable() {
                    @Override
                    public void run() {
                        validateEmail(email, new UserExistsCallback() {
                            @Override
                            public void onCallback(boolean exists) {
                                TextInputLayout emailLayout = findViewById(R.id.ARBoxEmail);
                                Context context = Register.this;
                                if (exists) {
                                    Drawable cancelDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_cancel, context.getTheme());
                                    emailLayout.setEndIconDrawable(cancelDrawable);
                                    Log.d("TextWatcher", "Email exists: " + email);
                                } else {
                                    Drawable checkDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_done, context.getTheme());
                                    emailLayout.setEndIconDrawable(checkDrawable);                                    Log.d("TextWatcher", "Email does not exist: " + email);
                                    Log.d("TextWatcher", "Email does not exist: " + email);
                                }
                            }
                        });
                    }
                };
                // Post the workRunnable with a delay to prevent rapid database queries
                handler.postDelayed(workRunnable, 500); // Delay of 500ms
            }
        });
    }

    public interface UserExistsCallback {
        void onCallback(boolean value);
    }

    private void validateEmail(String email, final UserExistsCallback callback) {
        boolean isValid = true;
        databaseUser.getReference("Users").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean exists = dataSnapshot.exists();
                        Log.d("validateEmail", "Email exists in database: " + exists);
                        callback.onCallback(dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });
    }

    private void registerUser() {
        String email, password;
        email = String.valueOf(etEmail.getText());
        password = String.valueOf(etPassword.getText());

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ProfileCreation.class);
                            intent.putExtra("Email", email);
                            intent.putExtra("Password", password);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


