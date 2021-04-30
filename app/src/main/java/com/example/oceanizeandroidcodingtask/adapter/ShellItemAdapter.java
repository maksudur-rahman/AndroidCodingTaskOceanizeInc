package com.example.oceanizeandroidcodingtask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.oceanizeandroidcodingtask.R;

import java.util.ArrayList;

public class ShellItemAdapter extends ArrayAdapter<String> {

    private ArrayList<String > web;
    LayoutInflater mInflater;

    public ShellItemAdapter(Context context, ArrayList<String> web) {
        super(context, 0, web);
        mInflater= LayoutInflater.from(context);
        this.web = web;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        View rowView= mInflater.inflate(R.layout.adapter_shell_item_list,parent,false);
        TextView txtTitle = rowView.findViewById(R.id.outputTV);
        txtTitle.setText(web.get(position));
        return rowView;
    }
}
