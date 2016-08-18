package com.fadhil.pshycologydictionary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fadhil.pshycologydictionary.R;
import com.fadhil.pshycologydictionary.model.KamusModel;

import java.util.ArrayList;

/**
 * Created by Student10 on 2/11/2016.
 */
public class ListKataAdapter extends BaseAdapter {
    Context context;
    ArrayList<KamusModel> listKamus;
    int count;
    public ListKataAdapter(Context context, ArrayList<KamusModel> listKamus){
        this.listKamus = listKamus;
        this.context = context;
        this.count = listKamus.size();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;

        if (v == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_list_kata, null);
            holder.txtKata = (TextView)v.findViewById(R.id.txtItemKata);

            v.setTag(holder);
        } else {
            holder = (ViewHolder)v.getTag();
        }

        holder.txtKata.setText(listKamus.get(position).getKata());

        return v;
    }

    static class ViewHolder{
        TextView txtKata;
    }

}

