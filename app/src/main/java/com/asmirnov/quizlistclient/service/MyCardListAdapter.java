package com.asmirnov.quizlistclient.service;

import android.content.Context;
import android.util.Log;
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

public class MyCardListAdapter extends BaseAdapter {

    private static final String TAG = "quizlistLogs";

    Context context;
    LayoutInflater lInflater;
    ArrayList<Card> cardsList;
    boolean editMode;
    Card card;

    public MyCardListAdapter(Context context, ArrayList<Card> cardsList) {
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
            view = lInflater.inflate((editMode ? R.layout.cards_list_item2 : R.layout.cards_list_item), parent, false);
        }

        card = getCard(position);

        if(editMode){
            EditText textview_name = (EditText) view.findViewById(R.id.textview_name);
            textview_name.setText(card.getTerm());
            ((EditText) view.findViewById(R.id.textview_info)).setText(card.getValue());
            textview_name.setTag(card.getId());
            Log.d(TAG, "getView: view:"+textview_name.getId()+",set tag "+card.getId());

            textview_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
//                    if (!hasFocus){
//                        //cardsList.get(position) = Caption.getText().toString();
//                    }
                    final int position = v.getId();
                    final EditText Caption = (EditText) v;
//                    Log.d(TAG, "onFocusChange: "+(hasFocus ? "+" : "-")+", view:"+position+". card:"+card.getId()+", term:"+card.getTerm()+". New str:"+Caption.getText());
                }
            });

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
