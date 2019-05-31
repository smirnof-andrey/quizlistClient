package com.asmirnov.quizlistclient.service;

public interface MyAdapterInterface {
    public void updateCardListTerm(int position, String text, boolean itIsTerm);
    public void deleteCard(int position);
}
