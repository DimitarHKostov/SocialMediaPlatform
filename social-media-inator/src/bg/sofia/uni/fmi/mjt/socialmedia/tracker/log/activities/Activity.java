package bg.sofia.uni.fmi.mjt.socialmedia.tracker.log.activities;

import bg.sofia.uni.fmi.mjt.socialmedia.enums.ActivityType;

import java.time.LocalDateTime;

public class Activity {
    protected final LocalDateTime publishDate;
    protected final ActivityType type;
    private final String text;
    private final String id;

    public Activity(LocalDateTime publishDate, ActivityType type, String id, String text) {
        this.publishDate = publishDate;
        this.type = type;
        this.id = id;
        this.text = text;
    }

    public LocalDateTime getPublishDate() {
        return this.publishDate;
    }

    public ActivityType getType() {
        return this.type;
    }

    public String getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public String formatActivity() {
        return this.formatDate() + ": " + this.formatAction() + " with id " + this.id;
    }

    private String formatDate() {
        String hour = this.reform(String.valueOf(this.publishDate.getHour()));
        String minute = this.reform(String.valueOf(this.publishDate.getMinute()));
        String second = this.reform(String.valueOf(this.publishDate.getSecond()));
        String day = this.reform(String.valueOf(this.publishDate.getDayOfMonth()));
        String month = this.reform(String.valueOf(this.getPublishDate().getMonthValue()));
        String year = this.reform(String.valueOf(this.getPublishDate().getYear()));

        return hour + ":" + minute + ":" + second + " " + day + "." + month + "." + year;
    }

    private String reform(String data) {
        int length = data.length();

        return switch (length) {
            case 0 -> "00";
            case 1 -> "0" + data;
            case 2 -> data;
            default -> data.substring(data.length() - 2);
        };
    }

    private String formatAction() {
        return switch (this.type) {
            case LIKE -> "Liked a content";
            case COMMENT -> "Commented \"" + this.getText() + "\" on a content";
            case CREATE_POST -> "Created a post";
            case CREATE_STORY -> "Created a story";
        };
    }
}
