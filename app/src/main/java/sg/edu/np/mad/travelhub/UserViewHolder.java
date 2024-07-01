package sg.edu.np.mad.travelhub;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    CardView userDetails;
    ImageView profImage;
    TextView name, description;

    public UserViewHolder(View view){
        super(view);
        profImage = view.findViewById(R.id.UivProfImage);
        name = view.findViewById(R.id.UtvName);
        description = view.findViewById(R.id.UtvDescription);
        userDetails = view.findViewById(R.id.userDetails);
    }
}
