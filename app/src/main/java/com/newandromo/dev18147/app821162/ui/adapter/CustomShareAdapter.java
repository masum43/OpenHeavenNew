package com.newandromo.dev18147.app821162.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.ui.enums.CustomSocial;

public class CustomShareAdapter extends BaseAdapter {
    private LayoutInflater mInflater;

    public CustomShareAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return CustomSocial.values().length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CustomShareViewHolder holder;
        CustomSocial customSocial = CustomSocial.values()[position];

        if (v == null) {
            v = mInflater.inflate(R.layout.list_item_social_share, parent, false);
            holder = new CustomShareViewHolder();
            holder.mView = v.findViewById(R.id.social_color);
            holder.mIcon = v.findViewById(R.id.icon);
            v.setTag(holder);
        } else {
            holder = (CustomShareViewHolder) v.getTag();
        }

        ShapeDrawable circle = new ShapeDrawable(new OvalShape());

        if (holder.mIcon != null) {
            holder.mIcon.setImageResource(customSocial.getIcon());
            if (customSocial.name().equals(CustomSocial.ANDROID.name())) {
                holder.mIcon.setColorFilter(Color.parseColor("#ffffff"));
            }
        }

        if (holder.mView != null) {
            circle.getPaint().setColor(customSocial.getIconColor());
            holder.mView.setBackground(circle);
        }

        return v;
    }

    private static class CustomShareViewHolder {
        View mView;
        ImageView mIcon;
    }
}