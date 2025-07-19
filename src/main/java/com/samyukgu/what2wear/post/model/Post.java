package com.samyukgu.what2wear.post.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Post {
    private final SimpleIntegerProperty no;
    private final SimpleStringProperty title;
    private final SimpleStringProperty author;
    private final SimpleStringProperty date;
    private final SimpleIntegerProperty likes;

    public Post(int no, String title, String author, String date, int likes) {
        this.no = new SimpleIntegerProperty(no);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.date = new SimpleStringProperty(date);
        this.likes = new SimpleIntegerProperty(likes);
    }

    public IntegerProperty noProperty() { return no; }
    public StringProperty titleProperty() { return title; }
    public StringProperty authorProperty() { return author; }
    public StringProperty dateProperty() { return date; }
    public IntegerProperty likesProperty() { return likes; }
}


