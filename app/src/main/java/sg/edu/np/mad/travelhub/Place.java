package sg.edu.np.mad.travelhub;
import java.io.Serializable;

public class Place implements Serializable{
    private String name;
    private String city;
    private String country;
    private String description;
    private double rating;
    private String imgUrl;

    // Constructor with all attributes
    public Place(String name, String city, String country, String description, double rating, String imgUrl) {
        this.name = name;
        this.city = city;
        this.country = country;
        this.description = description;
        this.rating = rating;
        this.imgUrl = imgUrl;
    }

    // Constructor with optional country and description
    public Place(String name, String city, String country, double rating, String imgURl) {
        this(name, city, country, "", rating, imgURl);
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImgUrl(){
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLocation() {return (this.city + ", " + this.country); }

    // toString method to display the place details
    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", description='" + description + '\'' +
                ", rating=" + rating + '\'' +
                ", imgUrl=" + imgUrl +
                '}';
    }
}
