package com.example.profile;

public class Event {

    private String committeeName;
    private String imageUrl;
    private String eventId;
    private String title;
    private String cname;
    private String date;
    private String time;
    private String description;
    private String key;

    public Event(String cname, String title, String date, String time, String description) {
        this.cname = cname;
        this.title = title;
        this.date = date;
        this.time = time;
        this.description = description;
    }
    public Event(String committeeName, String title, String description, String imageUrl) {
        this.committeeName = committeeName;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }
    public Event(String eventId) {
        this.eventId = eventId;
        // Initialize other fields
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }


    public String getCommitteeName() {
        return committeeName;
    }

    public void setCommitteeName(String committeeName) {
        this.committeeName = committeeName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    // Getter and setter methods for the fields if needed
}
