package com.samyukgu.what2wear.post.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Post {
    private final SimpleIntegerProperty no;
    private final SimpleStringProperty title;
    private final SimpleStringProperty author;
    private final SimpleStringProperty content;
    private final SimpleStringProperty date;
    private final SimpleIntegerProperty likes;

    public Post(int no, String title, String author, String content, String date, int likes) {
        this.no = new SimpleIntegerProperty(no);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.author = new SimpleStringProperty(author);
        this.date = new SimpleStringProperty(date);
        this.likes = new SimpleIntegerProperty(likes);
    }

    public IntegerProperty noProperty() { return no; }
    public StringProperty titleProperty() { return title; }
    public StringProperty authorProperty() { return author; }
    public StringProperty contentProperty() { return content; }
    public StringProperty dateProperty() { return date; }
    public IntegerProperty likesProperty() { return likes; }

    public String getTitle() { return title.get(); }

    public String getContent() { return content.get(); }

    public String getAuthor() { return author.get();}

    public String getDate() { return date.get(); }

    public int getLikes() { return likes.get();}
}


