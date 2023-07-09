package com.example.bnote;

public class Note {
    private int id;
    private String title;
    private String content;
    private String color;
    private String date;
    private int isPinned;

    public Note(int id, String title, String content, String color, String date, int isPinned) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.color = color;
        this.date = date;
        this.isPinned = isPinned;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(int isPinned) {
        this.isPinned = isPinned;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
