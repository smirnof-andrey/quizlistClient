package com.asmirnov.quizlistclient.dto;


import com.asmirnov.quizlistclient.model.Card;
import com.asmirnov.quizlistclient.model.Module;

import java.util.List;

public class ModuleCardsDTO {
    private Module module;
    private Card card;
    private List<Card> cardList;
    private int responseCode;
    private String message;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getErrorInfo(){
        if(responseCode == 0){
            return "";
        }else{
            return "error code="+responseCode+"("+message+")";
        }
    }


}
