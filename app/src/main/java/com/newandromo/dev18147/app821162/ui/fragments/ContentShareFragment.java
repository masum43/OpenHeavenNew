package com.newandromo.dev18147.app821162.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.MyAnalytics;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.ui.adapter.CustomShareAdapter;
import com.newandromo.dev18147.app821162.ui.enums.CustomSocial;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SHARE_BODY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SHARE_SUBJECT;

public class ContentShareFragment extends DialogFragment {
    private String mShareSubject, mShareBody;

    public ContentShareFragment() {
    }

    public static ContentShareFragment newInstance(Bundle shareBundle) {
        ContentShareFragment frag = new ContentShareFragment();
        frag.setArguments(shareBundle);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) return;
        mShareSubject = getArguments().getString(BUNDLE_SHARE_SUBJECT, "");
        mShareBody = getArguments().getString(BUNDLE_SHARE_BODY, "");
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

        CustomShareAdapter adapter = new CustomShareAdapter(getActivity());

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            CustomSocial customSocial = CustomSocial.values()[position];
            if (RemoteConfig.isUseInAppDeepLink()) {
                getShortDynamicLink(getActivity(), customSocial);
            } else {
               /* if (customSocial.name().equalsIgnoreCase(CustomSocial.FACEBOOK.name())) {
                    AppUtils.facebookLinkContentShare(getActivity(), getArguments(), customSocial.getPackageName());
                } else */if (customSocial.name().equalsIgnoreCase(CustomSocial.SMS.name())) {
                    AppUtils.smsShare(getActivity(), getArguments());
                } else if (customSocial.name().equalsIgnoreCase(CustomSocial.ANDROID.name())) {
                    AppUtils.share(getActivity(), getArguments());
                } else {
                    AppUtils.specificPackageShare(getActivity(), getArguments(), customSocial.getPackageName());
                }
            }

            MyAnalytics.shareContent(getActivity(), mShareSubject, mShareBody);

            if (getDialog() != null) getDialog().dismiss();
        });

        gridView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            CustomSocial customSocial = CustomSocial.values()[position];
            AppUtils.showToast(getActivity(), getString(customSocial.getTitle()), false);
            return false;
        });

        if (null != getActivity()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            try {
                mShareSubject = Jsoup.parse(mShareSubject, "", Parser.htmlParser()).text();
            } catch (Exception ignore) {
            }

            String title = String.format("%s: %s", getString(R.string.share), mShareSubject);
            builder.setView(gridView);
            builder.setTitle(title);

            return builder.create();
        }
        return super.onCreateDialog(savedInstanceState);
    }

    private void getShortDynamicLink(Activity activity, CustomSocial customSocial) {
        if (getArguments() == null) return;

        Uri longDeepLink = AppUtils.buildDeepLink(getArguments());
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(longDeepLink)
                .buildShortDynamicLink()
                .addOnFailureListener(activity, e -> {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                })
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String shortLink = task.getResult().getShortLink().toString();
                        String flowchartLink = task.getResult().getPreviewLink().toString();

                        if (!TextUtils.isEmpty(shortLink)) mShareBody = shortLink;

                        if (BuildConfig.DEBUG)
                            Timber.d("\nlongDeepLink= %s \nshortDeepLink= %s \nflowchartLink= %s",
                                    longDeepLink.toString(), shortLink, flowchartLink);

                        Bundle shareBundle = AppUtils
                                .createShareBundle(mShareSubject, mShareBody, "", "", "");

                       /* if (customSocial.name().equalsIgnoreCase(CustomSocial.FACEBOOK.name())) {
                            AppUtils.facebookLinkContentShare(activity, shareBundle, customSocial.getPackageName());
                        } else*/ if (customSocial.name().equalsIgnoreCase(CustomSocial.SMS.name())) {
                            AppUtils.smsShare(activity, shareBundle);
                        } else if (customSocial.name().equalsIgnoreCase(CustomSocial.ANDROID.name())) {
                            AppUtils.share(activity, shareBundle);
                        } else {
                            AppUtils.specificPackageShare(activity, shareBundle, customSocial.getPackageName());
                        }
                    } else {
                        if (BuildConfig.DEBUG)
                            Timber.w("onComplete() isSuccessful()= %s", task.isSuccessful());

                        mShareBody = longDeepLink.toString();

                        Bundle shareBundle = AppUtils
                                .createShareBundle(mShareSubject, mShareBody, "", "", "");

                        /*if (customSocial.name().equalsIgnoreCase(CustomSocial.FACEBOOK.name())) {
                            AppUtils.facebookLinkContentShare(activity, shareBundle,
                                    customSocial.getPackageName());
                        } else */if (customSocial.name().equalsIgnoreCase(CustomSocial.SMS.name())) {
                            AppUtils.smsShare(activity, shareBundle);
                        } else if (customSocial.name().equalsIgnoreCase(CustomSocial.ANDROID.name())) {
                            AppUtils.share(activity, shareBundle);
                        } else {
                            AppUtils.specificPackageShare(activity, shareBundle, customSocial.getPackageName());
                        }
                    }
                });
    }
}
