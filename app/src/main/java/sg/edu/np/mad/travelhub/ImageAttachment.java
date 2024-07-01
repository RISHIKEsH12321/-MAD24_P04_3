package sg.edu.np.mad.travelhub;

import android.media.Image;
import android.net.Uri;

public class ImageAttachment {
    String EventId;
    String ImageId;
    Uri URI;
    String exampleDrawable;

    public ImageAttachment(String s, Uri u,String exampleDrawable){
        this.EventId = s;
        this.URI = u;
        this.exampleDrawable=exampleDrawable;
    }
    public ImageAttachment(){}

    @Override
    public String toString() {
        return "ImageAttachment{" +
                "EventId='" + EventId + '\'' +
                ", URI=" + URI +
                ", IMAGEId=" + ImageId +
                ", Drawable=" + exampleDrawable +
                '}';
    }
}
