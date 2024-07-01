package sg.edu.np.mad.travelhub;

import androidx.recyclerview.widget.RecyclerView;

public class ToBringItem {
    String eventID;
    String itemID;
    String itemName;
    Boolean ticked;

    public ToBringItem(String e, String i, Boolean t, String n){
        this.eventID = e;
        this.itemID = i;
        this.ticked = t;
        this.itemName = n;
    }
    public ToBringItem(){}


    @Override
    public String toString() {
        return "ToBringItem{" +
                "eventID='" + eventID + '\'' +
                ", itemID='" + itemID + '\'' +
                ", itemName='" + itemName + '\'' +
                ", ticked=" + ticked +
                '}';
    }



}
