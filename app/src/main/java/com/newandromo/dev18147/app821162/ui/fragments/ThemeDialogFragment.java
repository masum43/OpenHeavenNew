package com.newandromo.dev18147.app821162.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.ui.enums.Theme;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;

public class ThemeDialogFragment extends DialogFragment {

    public ThemeDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() == null) return super.onCreateDialog(savedInstanceState);
        // Prepare grid view
        GridView gridView = new GridView(getActivity());
        gridView.setLayoutParams(new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        gridView.setHorizontalSpacing(AppUtils.convertDpToPixels(getActivity(), 16));
        gridView.setVerticalSpacing(AppUtils.convertDpToPixels(getActivity(), 16));
        gridView.setColumnWidth(AppUtils.convertDpToPixels(getActivity(), 56));
        int padding = AppUtils.convertDpToPixels(getActivity(), 22);
        gridView.setPadding(padding, padding, padding, padding);
        gridView.setClipToPadding(false);
        gridView.setSelector(android.R.color.transparent);
        gridView.setGravity(Gravity.CENTER);
        gridView.setNumColumns(GridView.AUTO_FIT);
        ThemeAdapter adapter = new ThemeAdapter(getActivity());

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Theme theme = Theme.values()[position];
            if (getDialog() != null) getDialog().dismiss();
            PrefUtils.setAppTheme(getActivity(), theme);
        });

        if (getActivity() != null) {
            // Set grid view to alertDialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(gridView);
            builder.setTitle(getString(R.string.nav_drawer_themes));

            return builder.create();
        }
        return super.onCreateDialog(savedInstanceState);
    }

    private static class ThemeViewHolder {
        View mView;
        ImageView mIcon;
    }

    private class ThemeAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        ThemeAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            //return mThemes.size();
            return Theme.values().length;
        }

        @Override
        public Object getItem(int position) {
            // return Theme.values()[position];
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            View v = convertView;
            final ThemeViewHolder holder;
            final Theme theme = Theme.values()[position];

            if (v == null) {
                v = mInflater.inflate(R.layout.list_item_theme_dialog, container, false);
                holder = new ThemeViewHolder();
                holder.mView = v.findViewById(R.id.theme);
                holder.mIcon = v.findViewById(R.id.icon);
                v.setTag(holder);
            } else {
                holder = (ThemeViewHolder) v.getTag();
            }

            ShapeDrawable circle = new ShapeDrawable(new OvalShape());

            if (holder.mIcon != null) {
                if (PrefUtils.getAppTheme(mContext).equals(theme.name())) {
                    holder.mIcon.setVisibility(View.VISIBLE);
                    if (AppUtils.isLightTheme(mContext)) {
                        holder.mIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.grey_deepen));
                    } else {
                        holder.mIcon.setColorFilter(ContextCompat.getColor(mContext, android.R.color.white));
                    }
                } else holder.mIcon.setVisibility(View.GONE);
            }

            if (holder.mView != null) {
                circle.getPaint().setColor(ContextCompat.getColor(mContext, theme.getColorId()));
                holder.mView.setBackground(circle);
            }

            return v;
        }
    }
}
