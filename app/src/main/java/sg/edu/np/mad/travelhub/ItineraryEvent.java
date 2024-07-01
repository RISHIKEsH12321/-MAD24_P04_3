package sg.edu.np.mad.travelhub;

public class ItineraryEvent {
    public String eventName;
    public String eventID;
    public String eventNotes;
    public String startHour;
    public String startMin;
    public String endHour;
    public String endMin;

    public ItineraryEvent(String name, String notes, String sh, String sm, String eh, String em){
        this.eventName = name;
        this.eventNotes = notes;
        this.startHour = sh;
        this.startMin = sm;
        this.endHour = eh;
        this.endMin = em;
    }
    public ItineraryEvent(){}

    @Override
    public String toString() {
        return "ItineraryEvent{" +
                "eventName='" + eventName + '\'' +
                ", eventID='" + eventID + '\'' +
                ", eventNotes='" + eventNotes + '\'' +
                ", startHour=" + startHour +
                ", startMin=" + startMin +
                ", endHour=" + endHour +
                ", endMin=" + endMin +
                '}';
    }
}

