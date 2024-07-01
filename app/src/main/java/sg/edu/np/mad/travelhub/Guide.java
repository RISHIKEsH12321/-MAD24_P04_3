//package sg.edu.np.mad.travelhub;
//
//import android.os.Bundle;
//import android.widget.ExpandableListView;
//import android.widget.ExpandableListView.OnGroupCollapseListener;
//import android.widget.ExpandableListView.OnGroupExpandListener;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.HashMap;
//import java.util.Map;
//
//public class Guide extends AppCompatActivity {
//
//    ExpandableListView listView;
//    ExpandableListAdapter listAdapter;
//    List<String> listTitle;
//    HashMap<String, List<String>> listDetail;
//
//    /////////////////////////
//    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items");
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_guide);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<Map<String, String>> itemList = new ArrayList<>();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Map<String, String> item = (Map<String, String>) snapshot.getValue();
//                    itemList.add(item);
//                }
//                // Pass itemList to the RecyclerView adapter
//                adapter.updateData(itemList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle possible errors.
//            }
//        });
//    }
//}