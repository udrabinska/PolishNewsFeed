package pl.pisze_czytam.polishnews;

import android.graphics.drawable.Drawable;

public class News {
    private String title;
    private String author;
    private String date;
    private String section;
    private String trailer;
    private String url;
    private Drawable image;
    private int colorBackground;

    public News(String title, String author, String date, String section, String trailer, String url, Drawable image, int colorBackground) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.section = section;
        this.trailer = trailer;
        this.url = url;
        this.image = image;
        this.colorBackground = colorBackground;
    }

    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getDate() {
        return date;
    }
    public String getSection() {
        return section;
    }
    public String getTrailer() {
        return trailer;
    }
    public String getUrl() {
        return url;
    }
    public Drawable getImage() {
        return image;
    }
    public int getColorBackground() {
        return colorBackground;
    }
}
