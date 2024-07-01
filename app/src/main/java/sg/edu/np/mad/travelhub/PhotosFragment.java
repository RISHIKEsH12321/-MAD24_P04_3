package sg.edu.np.mad.travelhub;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PhotosFragment extends Fragment {
    private ArrayList<String> placePhotos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placePhotos = getArguments().getStringArrayList("placePhotos");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        // Initialize your RecyclerView and adapter here, and set the data to the adapter
        RecyclerView recyclerView = view.findViewById(R.id.photosRV);
        Place_Photos_RecyclerView_Adapter adapter = new Place_Photos_RecyclerView_Adapter(getContext(), placePhotos);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        return view;
    }
}