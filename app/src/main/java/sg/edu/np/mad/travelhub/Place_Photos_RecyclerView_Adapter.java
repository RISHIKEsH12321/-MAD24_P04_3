package sg.edu.np.mad.travelhub;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import android.app.Dialog;

public class Place_Photos_RecyclerView_Adapter extends RecyclerView.Adapter<Place_Photos_RecyclerView_Adapter.MyViewHolder>{
    Context context;
    List<String> placePhotos;

    public Place_Photos_RecyclerView_Adapter(Context context, List<String> placePhotos){
        this.context = context;
        this.placePhotos = placePhotos;
    }

    @NonNull
    @Override
    public Place_Photos_RecyclerView_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.place_photos_recyclerview, parent, false);

        return new Place_Photos_RecyclerView_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Place_Photos_RecyclerView_Adapter.MyViewHolder holder, int position) {
        String imgUrl = placePhotos.get(position);

        if (placePhotos.isEmpty()){
            // Display No photos text if possible
        } else{
            Glide.with(context)
                .load(imgUrl)
//                    .transform(new CenterCrop(),new RoundedCorners(10))
                .into(holder.placePhotoIV);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dialog for image viewing enlargement
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_place_photo_view);

                ImageView imageView = dialog.findViewById(R.id.dialog_photo_image);
                Glide.with(context)
                        .load(imgUrl)
                        .into(imageView);

                Log.d("ImgUrl", imgUrl);

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return placePhotos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView placePhotoIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            placePhotoIV = itemView.findViewById(R.id.placePhoto);
        }
    }
}
