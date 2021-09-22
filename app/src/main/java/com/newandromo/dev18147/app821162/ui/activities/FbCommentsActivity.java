package com.newandromo.dev18147.app821162.ui.activities;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.MIMETYPE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.UTF8;

public class FbCommentsActivity extends AppCompatActivity {
    private WebView mWebViewComments;
    private WebView mWebViewPop;
    private FrameLayout mContainer;
    private ProgressBar mProgressBar;
    private String mPostUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppUtils.setAppTheme(this, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_comments);

        String postTitle;
        if (savedInstanceState != null) {
            postTitle = savedInstanceState.getString(TAG_TITLE);
            mPostUrl = savedInstanceState.getString(TAG_URL);
        } else {
            postTitle = getIntent().getStringExtra(TAG_TITLE);
            mPostUrl = getIntent().getStringExtra(TAG_URL);
        }

        Toolbar mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        ActionBar ab = this.getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            if (!TextUtils.isEmpty(postTitle)) {
                try {
                    ab.setTitle(Jsoup.parse(postTitle, "", Parser.htmlParser()).text());
                } catch (Exception e) {
                    ab.setTitle(postTitle);
                }
                ab.setSubtitle(getString(R.string.comments));
            } else {
                ab.setTitle(getString(R.string.comments));
            }
        }

        mWebViewComments = findViewById(R.id.fb_comments_WebView);
        mContainer = findViewById(R.id.webview_frame);
        mProgressBar = findViewById(R.id.progressBar);

        loadComments();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TAG_URL, mPostUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fb_comments, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem refresh = menu.findItem(R.id.menu_refresh);
        if (refresh != null) AppUtils.tintMenuItemIcon(this, refresh);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.menu_refresh) {
            loadComments();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadComments() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.entryDetailBackground, typedValue, true);
        int backgroundColor = typedValue.data;

        mWebViewComments.setBackgroundColor(backgroundColor);
        mWebViewComments.setWebViewClient(AppUtils.hasNougat() ?
                new MyWebViewClientNougatAndAbove() : new MyWebViewClientOLD());
        mWebViewComments.setWebChromeClient(new MyWebChromeClient());
        mWebViewComments.getSettings().setJavaScriptEnabled(true);
        mWebViewComments.getSettings().setAppCacheEnabled(true);
        mWebViewComments.getSettings().setDomStorageEnabled(true);
        mWebViewComments.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebViewComments.getSettings().setSupportMultipleWindows(true);
        mWebViewComments.getSettings().setSupportZoom(false);
        mWebViewComments.getSettings().setBuiltInZoomControls(false);
        CookieManager.getInstance().setAcceptCookie(true);
        if (AppUtils.hasLollipop()) {
            mWebViewComments.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebViewComments, true);
        }

        String colorScheme = AppUtils.isDarkTheme(this) ? "dark" : "light";

        // facebook comment widget including the article url
        String html = "<!doctype html> <html lang=\"en\"> " +
                "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">" +
                "</head> " +
                "<body> " +
                "<div id=\"fb-root\"></div> <div id=\"fb-root\"></div>\n" +
                "<script>(function(d, s, id) {\n" +
                "  var js, fjs = d.getElementsByTagName(s)[0];\n" +
                "  if (d.getElementById(id)) return;\n" +
                "  js = d.createElement(s); js.id = id;\n" +
                "  js.src = \"//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.8\";\n" +
                "  fjs.parentNode.insertBefore(js, fjs);\n" +
                "}(document, 'script', 'facebook-jssdk'));</script> " +
                "<div class=\"fb-comments\" data-href=\"" + mPostUrl + "\" " +
                "data-numposts=\"5\" data-colorscheme=\"" + colorScheme + "\"></div> " +
                "</body> " +
                "</html>";

        mWebViewComments.loadDataWithBaseURL(RemoteConfig.getSiteUrl(), html, MIMETYPE, UTF8, null);
        mWebViewComments.setMinimumHeight(200);
    }

    private void mOnPageFinished(String url) {
        if (url.contains("/plugins/close_popup.php?reload")) {
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                //Do something after 600ms
                mContainer.removeView(mWebViewPop);
                loadComments();
            }, 600);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private class MyWebViewClientNougatAndAbove extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String host = Uri.parse(request.getUrl().toString()).getHost();
            return !host.equals("m.facebook.com");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mOnPageFinished(url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        }
    }

    private class MyWebViewClientOLD extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String host = Uri.parse(url).getHost();
            return !host.equals("m.facebook.com");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mOnPageFinished(url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            mWebViewPop = new WebView(getApplicationContext());
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.entryDetailBackground, typedValue, true);
            int backgroundColor = typedValue.data;

            mWebViewPop.setBackgroundColor(backgroundColor);
            mWebViewPop.setVerticalScrollBarEnabled(false);
            mWebViewPop.setHorizontalScrollBarEnabled(false);
            mWebViewPop.setWebViewClient(AppUtils.hasNougat() ?
                    new MyWebViewClientNougatAndAbove() : new MyWebViewClientOLD());
            mWebViewPop.setWebChromeClient(this);
            mWebViewPop.getSettings().setJavaScriptEnabled(true);
            mWebViewPop.getSettings().setDomStorageEnabled(true);
            mWebViewPop.getSettings().setSupportZoom(false);
            mWebViewPop.getSettings().setBuiltInZoomControls(false);
            mWebViewPop.getSettings().setSupportMultipleWindows(true);
            mWebViewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            mContainer.addView(mWebViewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebViewPop);
            resultMsg.sendToTarget();

            return true;
        }

        public void onProgressChanged(WebView view, int progress) {
            try {
                mProgressBar.setProgress(progress);
                if (progress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Timber.e("onProgressChanged() error= %s", e.getMessage());
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Timber.i("onConsoleMessage(): %s", cm.message());
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
        }
    }
}
