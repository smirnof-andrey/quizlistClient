package com.asmirnov.quizlistclient.service;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
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

    MyAdapterInterface mMyInterface;

    Context context;
    LayoutInflater lInflater;
    ArrayList<Card> cardsList;
    boolean editMode;

    public MyCardListAdapter(Context context, ArrayList<Card> cardsList, MyAdapterInterface mMyInterface) {
        this(context,cardsList);
        this.mMyInterface = mMyInterface;

    }

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
            view = lInflater.inflate((editMode ? R.layout.cards_list_item_edit : R.layout.cards_list_item), parent, false);
        }

        Card card = getCard(position);

        if(editMode){
            EditText textViewTerm = (EditText) view.findViewById(R.id.textview_name);
            EditText textViewValue = (EditText) view.findViewById(R.id.textview_info);
            textViewTerm.setText(card.getTerm());
            textViewValue.setText(card.getValue());

            textViewTerm.setOnFocusChangeListener(new MyFCListner(position,mMyInterface));
//            textViewTerm.addTextChangedListener(new MyTextWatcher2(position,mMyInterface));

//            MyTextWatcher2 oldWatcher = (MyTextWatcher2)textViewTerm.getTag();
//            if(oldWatcher != null){
//                textViewTerm.removeTextChangedListener(oldWatcher);
////                Log.d(TAG, "getView: destroy old Watcher for card ["+card.getId()+"], position:"+position);
//            }
//
//            MyTextWatcher2 newWatcher = new MyTextWatcher2(position,mMyInterface);
//            textViewTerm.setTag(newWatcher);
//            textViewTerm.addTextChangedListener(newWatcher);
////            Log.d(TAG, "getView: add new Watcher for card ["+card.getId()+"], position:"+position);

        }else {
            ((TextView) view.findViewById(R.id.textview_name)).setText(card.getTerm());
            ((TextView) view.findViewById(R.id.textview_info)).setText(card.getValue());
        }

        return view;
    }

    Card getCard(int position) {
        return ((Card) getItem(position));
    }

    private class imageViewClickListene implements View.OnClickListener {
        int position;

        public imageViewClickListene(int position) {
            this.position = position;
        }

        public void onClick(View v) {
            // here we  remove the selected item
            //cardsList.remove(rowItem);
            String text = ((EditText)v).getText().toString();
            Log.d(TAG, "  imageViewClickListene onClick: position"+position+", text:"+text);
            mMyInterface.updateEditText(position,text);
            // here we refresh the adapter
            MyCardListAdapter.this.notifyDataSetChanged();
        }
    }

    private class MyFCListner implements View.OnFocusChangeListener{
        int position;
        MyAdapterInterface mMyInterface;


        public MyFCListner(int position, MyAdapterInterface mMyInterface) {
            this.position = position;
            this.mMyInterface = mMyInterface;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String text = ((EditText)v).getText().toString();
            Log.d(TAG, "onFocusChange: position:"+position+", text:"+text);
            mMyInterface.updateEditText(position,text);
        }
    }

    private class MyTextWatcher2 implements TextWatcher{

        int position;
        MyAdapterInterface mMyInterface;

        public MyTextWatcher2(int position, MyAdapterInterface mMyInterface) {
            this.position = position;
            this.mMyInterface = mMyInterface;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Log.d(TAG, "afterTextChanged: position:"+position+", text:"+s);
            mMyInterface.updateEditText(position,s.toString());
        }
    }

}
