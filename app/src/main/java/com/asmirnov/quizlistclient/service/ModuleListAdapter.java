package com.asmirnov.quizlistclient.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asmirnov.quizlistclient.R;
import com.asmirnov.quizlistclient.model.Module;

import java.util.ArrayList;

public class ModuleListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater lInflater;
    ArrayList<Module> modulesList;

    public ModuleListAdapter(Context context, ArrayList<Module> modulesList) {
        this.context = context;
        this.modulesList = modulesList;
        lInflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return modulesList.size();
    }

    @Override
    public Object getItem(int position) {
        return modulesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.module_list_item, parent, false);
        }

        Module module = getModule(position);

        ((TextView) view.findViewById(R.id.textview_name)).setText(module.getName());
        ((TextView) view.findViewById(R.id.textview_moduleSize)).setText("0 items");
        ((TextView) view.findViewById(R.id.textview_info)).setText(module.getInfo());

        return view;
    }

    Module getModule(int position) {
        return ((Module) getItem(position));
    }
}