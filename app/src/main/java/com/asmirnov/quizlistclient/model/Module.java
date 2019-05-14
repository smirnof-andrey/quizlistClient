package com.asmirnov.quizlistclient.model;

public class Module {
    private Integer id;

    private String name;

    private String info;

    private User author;

    public Module() {
    }

    public Module(String name, String info) {
        this.name = name;
        this.info = info;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return this.name+", "+this.info+", "+this.id;
    }
}
