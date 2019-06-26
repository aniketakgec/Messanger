package com.example.android.messanger;



public class Messages {

    private String message, type;
    private long  time;
    private Boolean seen;
    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Messages(String from) {
        this.from = from;
    }

    public Messages(String message, Boolean seen, long time, String type) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }



    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Messages(){

    }

}