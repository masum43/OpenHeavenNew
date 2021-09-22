package com.newandromo.dev18147.app821162.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.cursoradapter.widget.CursorAdapter;

public class SuggestionListAdapter extends CursorAdapter {

    private String[] columns = new String[]{"_id", "title"};

    public SuggestionListAdapter(Context context) {
        super(context, null, false);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder viewHolder;

        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        viewHolder = new ViewHolder();
        viewHolder.suggestionTitle = view.findViewById(android.R.id.text1);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.suggestionTitle.setText(cursor.getString(1));
    }


    public void updateAdapter(ArrayList<String> suggestions) {
        MatrixCursor cursor = new MatrixCursor(columns);
        int i = 0;
        for (String s : suggestions) {
            String[] temp = new String[2];
            temp[0] = Integer.toString(i);
            temp[1] = s;
            i++;
            cursor.addRow(temp);
        }
        changeCursor(cursor);
    }

    public String getSuggestion(int position) {
        return ((Cursor) getItem(position)).getString(1);
    }

    private class ViewHolder {
        TextView suggestionTitle;
    }
}
