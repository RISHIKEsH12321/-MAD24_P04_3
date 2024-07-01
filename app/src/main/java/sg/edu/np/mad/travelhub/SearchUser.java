package sg.edu.np.mad.travelhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchUser extends AppCompatActivity {

    DatabaseReference ref;
    private AutoCompleteTextView Search;
    private RecyclerView recyclerView;
    FloatingActionButton fabSearch;
    List<User> usersList;
    ValueEventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ref = FirebaseDatabase.getInstance().getReference("Users");
        fabSearch = findViewById(R.id.SUfabSearch);
        recyclerView = findViewById(R.id.SUrvList);

        //GridLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchUser.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        //Alert Dialog when user presses the recyclerview

        //list
        usersList = new ArrayList<>();
        UserAdapter adapter = new UserAdapter(SearchUser.this, (ArrayList<User>) usersList);
        recyclerView.setAdapter(adapter);

        eventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot userSnapshot: snapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    usersList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //something
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchUser.this, ProfileCreation.class); //OtherProfile.class);
                startActivity(intent);
            }
        });
    }
}