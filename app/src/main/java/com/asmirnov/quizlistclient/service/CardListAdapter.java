package com.asmirnov.quizlistclient.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.asmirnov.quizlistclient.EditActivity;
import com.asmirnov.quizlistclient.R;
import com.asmirnov.quizlistclient.model.Card;

import java.util.ArrayList;

public class CardListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater lInflater;
    ArrayList<Card> cardsList;
    boolean editMode;

    public CardListAdapter(Context context, ArrayList<Card> cardsList) {
        this.context = context;
        this.cardsList = cardsList;
        editMode = context.getClass()==EditActivity.class;
        lInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cardsList.size();
    }

    @Override
    public Object getItem(int position) {
        return cardsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate((editMode ? R.layout.cards_list_item_edit : R.layout.cards_list_item), parent, false);
        }

        Card card = getCard(position);

        if(editMode){
            ((EditText) view.findViewById(R.id.textview_name)).setText(card.getTerm());
            ((EditText) view.findViewById(R.id.textview_info)).setText(card.getValue());
        }else {
            ((TextView) view.findViewById(R.id.textview_name)).setText(card.getTerm());
            ((TextView) view.findViewById(R.id.textview_info)).setText(card.getValue());
        }

        return view;
    }

    Card getCard(int position) {
        return ((Card) getItem(position));
    }
}
