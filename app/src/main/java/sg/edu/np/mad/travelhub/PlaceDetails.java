package sg.edu.np.mad.travelhub;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetails implements Parcelable {
    private String placeXid;
    private String name;
    private String editorialSummary;
    private double rating;
    private String address;
    private List<String> photos;
    private List<PlaceReview> reviews;

    // Constructors
    public PlaceDetails() {
        // Default constructor required for Parcelable
    }

    protected PlaceDetails(Parcel in) {
        placeXid = in.readString();
        name = in.readString();
        editorialSummary = in.readString();
        rating = in.readDouble();
        address = in.readString();
        photos = in.createStringArrayList();
        reviews = in.createTypedArrayList(PlaceReview.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeXid);
        dest.writeString(name);
        dest.writeString(editorialSummary);
        dest.writeDouble(rating);
        dest.writeString(address);
        dest.writeStringList(photos);
        dest.writeTypedList(reviews);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaceDetails> CREATOR = new Creator<PlaceDetails>() {
        @Override
        public PlaceDetails createFromParcel(Parcel in) {
            return new PlaceDetails(in);
        }

        @Override
        public PlaceDetails[] newArray(int size) {
            return new PlaceDetails[size];
        }
    };

    // Getters and setters
    public String getPlaceXid() {
        return placeXid;
    }

    public void setPlaceXid(String placeXid) {
        this.placeXid = placeXid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEditorialSummary() {
        return editorialSummary;
    }

    public void setEditorialSummary(String editorialSummary) {
        this.editorialSummary = editorialSummary;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public List<PlaceReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<PlaceReview> reviews) {
        this.reviews = reviews;
    }
}
