package com.asmirnov.quizlistclient.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Card implements Parcelable {

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

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", module=" + module +
                ", term='" + term + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    protected Card(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        module = (Module) in.readValue(Module.class.getClassLoader());
        term = in.readString();
        value = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeValue(module);
        dest.writeString(term);
        dest.writeString(value);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };
}
