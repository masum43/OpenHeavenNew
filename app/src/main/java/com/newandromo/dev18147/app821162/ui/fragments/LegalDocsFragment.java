package com.newandromo.dev18147.app821162.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.MIMETYPE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.UTF8;

public class LegalDocsFragment extends DialogFragment {
    private View mView;
    private ProgressBar mProgressBar;
    private String mLegalDescription;
    private boolean mIsAppPolicy;

    public LegalDocsFragment() {
    }

    public static LegalDocsFragment newInstance(boolean isAppPolicy) {
        LegalDocsFragment frag = new LegalDocsFragment();
        Bundle b = new Bundle();
        b.putBoolean("isAppPolicy", isAppPolicy);
        frag.setArguments(b);
        return frag;
    }

    private static String createDocHtml(String content, String cssOverride) {
        String style = "@font-face{font-family:'Roboto Regular';src:url('file:///android_asset/font/roboto_regular.ttf')}";
        style += cssOverride;
        return "<!doctype html>" +
                "<html>" +
                "<head>" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/style.css\">" +
                "<meta charset=\"utf-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">" +
                "<style type=\"text/css\">" + style + "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"bodywrap_x\">" +
                "<div class=\"content_x\">" + content + "</div>" +
                "<div style=\"clear:both;\"></div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    @SuppressLint({"InflateParams", "SetJavaScriptEnabled"})
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) return;

        if (getArguments() != null)
            mIsAppPolicy = getArguments().getBoolean("isAppPolicy", true);

        mView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_legal_docs, null);
        mProgressBar = mView.findViewById(R.id.progress_bar);
        WebView webView = mView.findViewById(R.id.webView);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setSaveFormData(false);

        webView.setWebViewClient(AppUtils.hasNougat() ?
                new MyWebViewClientNougatAndAbove() : new MyWebViewClientOLD());
        webView.setWebChromeClient(new MyWebChromeClient());

        if (!PrefUtils.isHardwareAccelerated(getActivity())) {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.entryDetailBackground, typedValue, true);

        int backgroundColor = typedValue.data;
        String bgHexColor = String.format("#%06X", (0xFFFFFF & backgroundColor));

        webView.setBackgroundColor(backgroundColor);

        theme.resolveAttribute(R.attr.entryDetailColor, typedValue, true);
        String textHexColor = String.format("#%06X", (0xFFFFFF & typedValue.data));

        theme.resolveAttribute(R.attr.entryDetailHeaderColor, typedValue, true);
        String headerHexColor = String.format("#%06X", (0xFFFFFF & typedValue.data));

        theme.resolveAttribute(R.attr.entryDetailLinkColor, typedValue, true);
        String linkHexColor = String.format("#%06X", (0xFFFFFF & typedValue.data));

        String cssOverride = "body {background:" + bgHexColor + "!important;color:" + textHexColor + "!important;}";
        cssOverride += "h1,h2,h3,h4,h5,h6{color:" + headerHexColor + "!important;}";
        cssOverride += "a:link{color:" + linkHexColor + ";}a:visited{color:" + linkHexColor + ";}";

        if (AppUtils.isTablet(getActivity()))
            cssOverride += "body{font-size:20px;}";
        else
            cssOverride += "body{font-size:16px;}";

        getPrivacyPolicy();

        String mData = createDocHtml(mLegalDescription, cssOverride);
        webView.loadDataWithBaseURL(null, mData, MIMETYPE, UTF8, null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() == null) return super.onCreateDialog(savedInstanceState);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(mView);

        if (!mIsAppPolicy) {
            if (!PrefUtils.isYoutubeTermsAccepted(getActivity())) {
                builder.setPositiveButton(getString(R.string.accept), (dialogInterface, i) ->
                        PrefUtils.setAcceptYoutubeTerms(getActivity(), true));

                builder.setNegativeButton(getString(R.string.decline), (dialogInterface, i) -> {
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                });
            } else {
                builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    if (getDialog() != null) {
                        getDialog().dismiss();
                    }
                });
            }
        } else {
            builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                if (getDialog() != null) {
                    getDialog().dismiss();
                }
            });
        }

        return builder.create();
    }

    private void getPrivacyPolicy() {
        if (getActivity() == null) return;
        try {
            String jsonString = AppUtils.getAssetJsonData(getActivity(), "legal_docs.json");
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray legalArray = jsonObject.getJSONArray("legal");

            if (!legalArray.isNull(0) && legalArray.length() > 0) {
                mLegalDescription = legalArray.getJSONObject(mIsAppPolicy ? 0 : 1).getString("description");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private class MyWebViewClientNougatAndAbove extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            AppUtils.chromeCustomTabs(getActivity(), request.getUrl().toString());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.setVisibility(View.GONE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }

    private class MyWebViewClientOLD extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            AppUtils.chromeCustomTabs(getActivity(), url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.setVisibility(View.GONE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            try {
                mProgressBar.setProgress(progress);
                if (progress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
