package com.newandromo.dev18147.app821162.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.ui.adapter.CustomShareAdapter;
import com.newandromo.dev18147.app821162.ui.enums.CustomSocial;
import com.newandromo.dev18147.app821162.utils.AppUtils;

import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SHARE_BODY;

public class AppInviteFragment extends DialogFragment {
    private String mSubject, mShareBody;

    public AppInviteFragment() {
    }

    public static AppInviteFragment newInstance(Bundle shareBundle) {
        AppInviteFragment frag = new AppInviteFragment();
        frag.setArguments(shareBundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubject = getString(R.string.app_name);
        if (getArguments() != null)
            mShareBody = getArguments().getString(BUNDLE_SHARE_BODY);
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
        gridView.setColumnWidth(AppUtils.convertDpToPixels(getActivity(), 42));
        int padding = AppUtils.convertDpToPixels(getActivity(), 22);
        gridView.setPadding(padding, padding, padding, padding);
        gridView.setClipToPadding(false);
        gridView.setSelector(android.R.color.transparent);
        gridView.setGravity(Gravity.CENTER);
        gridView.setNumColumns(GridView.AUTO_FIT);
        // gridView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);// FIXME did this for arabic phones. Fix ukipata simu nzuri

        CustomShareAdapter adapter = new CustomShareAdapter(getActivity());

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            CustomSocial customSocial = CustomSocial.values()[position];
            Bundle shareBundle = AppUtils.createShareBundle(mSubject, mShareBody, "", "", "");
            if (customSocial.name().equalsIgnoreCase(CustomSocial.ANDROID.name())) {
                AppUtils.share(getActivity(), shareBundle);
            } else {
                AppUtils.specificPackageShare(getActivity(), shareBundle, customSocial.getPackageName());
            }

            if (getDialog() != null) getDialog().dismiss();
        });

        gridView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            CustomSocial customSocial = CustomSocial.values()[position];
            AppUtils.showToast(getActivity(), getString(customSocial.getTitle()), false);
            return false;
        });

        if (null != getActivity()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String title = String.format("%s: %s", getString(R.string.share), mSubject);
            builder.setView(gridView);
            builder.setTitle(title);
            return builder.create();
        }
        return super.onCreateDialog(savedInstanceState);
    }
}
