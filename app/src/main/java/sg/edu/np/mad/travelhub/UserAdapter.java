package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder>{
    Context context;
    ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Assign values to views according to position of view
        User user = users.get(position);
        holder.name.setText(user.getName());
        holder.description.setText(user.getDescription());
        Glide.with(context)
                .load(user.getImageUrl())
                .into(holder.profImage);
        holder.userDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to User's profile
            }
        });
    }

    @Override
    public int getItemCount() {
        // Number of recycler views
        return users.size();
    }

//    public int onClick() {
//
//    }
}
