package com.larkintuckerllc.viewmodel;

public class Todo {

    private long mId;
    private String mName;
    private long mDate;

    public Todo(long id, String name, long date) {
        mId = id;
        mName = name;
        mDate = date;
    }

    public Todo(Todo todo) {
        mId = todo.getId();
        mName = todo.getName();
        mDate = todo.getDate();
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public long getDate() {
        return mDate;
    }

}