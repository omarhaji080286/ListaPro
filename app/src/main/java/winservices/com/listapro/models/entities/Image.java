package winservices.com.listapro.models.entities;

import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("image_title")
    private String imageTitle;

    @SerializedName("image_string")
    private String imageString;

    public Image(String imageTitle, String imageString) {
        this.imageTitle = imageTitle;
        this.imageString = imageString;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }
}
