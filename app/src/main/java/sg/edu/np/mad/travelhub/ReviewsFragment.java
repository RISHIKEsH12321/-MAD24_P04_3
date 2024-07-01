package sg.edu.np.mad.travelhub;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<PlaceReview> reviewsList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve arguments containing reviews list
        if (getArguments() != null) {
            reviewsList = getArguments().getParcelableArrayList("placeReviewsList");
        } else {
            reviewsList = new ArrayList<>(); // Initialize empty list if no arguments found
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        recyclerView = view.findViewById(R.id.reviewsRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Place_Reviews_Recyclerview_Adapter adapter = new Place_Reviews_Recyclerview_Adapter(getContext(), reviewsList);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == reviewsList.size() - 1) {
            Log.d("ReviewsFragment", "Reached the bottom of the RecyclerView.");
        }

        return view;
    }
}
