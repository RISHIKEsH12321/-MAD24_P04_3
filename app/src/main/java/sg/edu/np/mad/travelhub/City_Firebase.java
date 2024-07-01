package sg.edu.np.mad.travelhub;

public class City_Firebase {
    private int id;
    private String name;
    private int state_id;
    private String state_code;
    private String state_name;
    private int country_id;
    private String country_code;
    private String country_name;
    private String latitude;
    private String longitude;
    private String wikiDataId;

    public City_Firebase(){

    }

    // Constructor
    public City_Firebase(int id, String name, int state_id, String state_code, String state_name,
                         int country_id, String country_code, String country_name,
                         String latitude, String longitude, String wikiDataId) {
        this.id = id;
        this.name = name;
        this.state_id = state_id;
        this.state_code = state_code;
        this.state_name = state_name;
        this.country_id = country_id;
        this.country_code = country_code;
        this.country_name = country_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.wikiDataId = wikiDataId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getWikiDataId() {
        return wikiDataId;
    }

    public void setWikiDataId(String wikiDataId) {
        this.wikiDataId = wikiDataId;
    }

    // Override toString() for debugging purposes
    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state_id=" + state_id +
                ", state_code='" + state_code + '\'' +
                ", state_name='" + state_name + '\'' +
                ", country_id=" + country_id +
                ", country_code='" + country_code + '\'' +
                ", country_name='" + country_name + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", wikiDataId='" + wikiDataId + '\'' +
                '}';
    }
}