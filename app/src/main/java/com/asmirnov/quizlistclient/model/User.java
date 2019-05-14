package com.asmirnov.quizlistclient.model;

public class User{
    private Long id;
    private String username;
    private String password;
    private boolean active;

    public User(){
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public boolean isActive(){
        return active;
    }

    public void setActive(boolean active){
        this.active = active;
    }

    @Override
    public String toString() {
        return getUsername()+" (id: "+getId()+")";
    }
}
