package com.asmirnov.quizlistclient.model;

public class Card {

    private Integer id;
    private Module module;

    private String term;
    private String value;

    public Card() {
    }

    public Card(Module module, String term, String value) {
        this.module = module;
        this.term = term;
        this.value = value;
    }

    public Card(Integer id, Module module, String term, String value) {
        this.id = id;
        this.module = module;
        this.term = term;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
