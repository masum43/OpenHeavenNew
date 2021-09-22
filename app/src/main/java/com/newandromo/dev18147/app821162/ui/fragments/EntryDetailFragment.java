package com.newandromo.dev18147.app821162.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.newandromo.dev18147.app821162.utils.AdsManager;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.List;

import com.newandromo.dev18147.app821162.AppExecutors;
import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.MyAnalytics;
import com.newandromo.dev18147.app821162.MyApplication;
import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.parser.ParserHelper;
import com.newandromo.dev18147.app821162.ui.activities.EntryDetailActivity;
import com.newandromo.dev18147.app821162.ui.activities.FbCommentsActivity;
import com.newandromo.dev18147.app821162.ui.adapter.RelatedPostPagerAdapter;
import com.newandromo.dev18147.app821162.ui.enums.CustomSocial;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.MyDateUtils;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import com.newandromo.dev18147.app821162.views.CarouselPagerContainer;
import timber.log.Timber;

import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ENTRY_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_IS_GO_HOME;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SHARE_BODY;
import static com.newandromo.dev18147.app821162.utils.Constants.AppBundles.BUNDLE_SHARE_SUBJECT;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ENTRY_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ENTRY_IMAGE_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_GO_HOME;
import static com.newandromo.dev18147.app821162.utils.Constants.AppIntents.INTENT_EXTRA_IS_SINGLE_LAYOUT;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.INSTAGRAM_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.TWITTER_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.VIMEO_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.YOUTUBE_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.AppUrls.YOUTUBE_URL_SHORT;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.ABOUT_BLANK;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.HTTP;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.HTTPS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.IS_CONTENT_CENTERED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.IS_ENABLE_OPEN_IN_BROWSER;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.IS_SHOW_AUTHOR;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.MIMETYPE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.UTF8;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.VENDOR_YOUTUBE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.YOUTUBE_GET_VIDEO_INFO;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.YOUTUBE_VIDEO_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.RequestCodesAndIds.REQ_ENTRY_DETAIL_ACTIVITY;

public class EntryDetailFragment extends Fragment implements RelatedPostPagerAdapter.RelatedPostListener {
    private static String mResultExtra = null;
    private String mUrlToDownload = null;
    private String mFilenameToDownload;
    private final DownloadListener mDownloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
            mFilenameToDownload = fileName;
            mUrlToDownload = url;
            if (!TextUtils.isEmpty(url))
                AppUtils.downloadFile(getActivity(), url, fileName);
        }
    };
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private int mCatId;
    private int mEntryId;
    private String mTitle;
    private String mUrl;
    private String mBaseUrl = "";
    private String mThumbUrl;
    private int mIsBookmarked;
    private String mCssOverride;

    private static final int DEFAULT_HEIGHT_DP = 300;
    private boolean mIsSingleLayout;
    private boolean mIsGoHome;

    public EntryDetailFragment() {
        // Required empty public constructor
    }

    public static EntryDetailFragment newInstance(int catId, int entryId) {
        EntryDetailFragment frag = new EntryDetailFragment();
        Bundle b = new Bundle();
        b.putInt(BUNDLE_ID, catId);
        b.putInt(BUNDLE_ENTRY_ID, entryId);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_detail, container, false);
        //MainActivity.showInter(getActivity());

        AdsManager.showNativeAd(getActivity());
        if (savedInstanceState != null) {
            mCatId = savedInstanceState.getInt(BUNDLE_ID);
            mEntryId = savedInstanceState.getInt(BUNDLE_ENTRY_ID);
            mIsGoHome = savedInstanceState.getBoolean(BUNDLE_IS_GO_HOME);
        } else if (getArguments() != null) {
            mCatId = getArguments().getInt(BUNDLE_ID);
            mEntryId = getArguments().getInt(BUNDLE_ENTRY_ID);
        }

        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.getExtras() != null) {
                mIsSingleLayout = intent.getBooleanExtra(INTENT_EXTRA_IS_SINGLE_LAYOUT, false);
                mIsGoHome = intent.getBooleanExtra(INTENT_EXTRA_IS_GO_HOME, false);
            }
        }

        mProgressBar = view.findViewById(R.id.progress_bar);
        mWebView = view.findViewById(R.id.entry_detail_WebView);
        WebSettings s = mWebView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setSaveFormData(false);

        registerForContextMenu(mWebView);

        mWebView.setWebViewClient(AppUtils.hasNougat() ?
                new MyWebViewClientNougatAndAbove(this) : new MyWebViewClientOLD(this));
        mWebView.setWebChromeClient(new MyWebChromeClient(this));
        mWebView.setDownloadListener(mDownloadListener);

        Button btnFacebook = view.findViewById(R.id.btn_facebook);
        Button btnTwitter = view.findViewById(R.id.btn_twitter);
        Button btnWhatsApp = view.findViewById(R.id.btn_whatsapp);
        Button btnSMS = view.findViewById(R.id.btn_sms);

        btnFacebook.setOnClickListener(v -> {
            Bundle shareBundle = AppUtils.createShareBundle
                    (mTitle, mUrl, mThumbUrl, mUrl, getString(R.string.scheme_my_app));

            MyAnalytics.shareContent(getActivity(), mTitle, mUrl);
        });

        btnTwitter.setOnClickListener(v -> {
            Bundle shareBundle = AppUtils.createShareBundle
                    (mTitle, mUrl, mThumbUrl, mUrl, getString(R.string.scheme_my_app));
            if (RemoteConfig.isUseInAppDeepLink()) {
                getShortDynamicLink(shareBundle, CustomSocial.TWITTER);
            } else {
                AppUtils.specificPackageShare(getActivity(), shareBundle,
                        CustomSocial.TWITTER.getPackageName());
            }
            MyAnalytics.shareContent(getActivity(), mTitle, mUrl);
        });

        btnWhatsApp.setOnClickListener(v -> {
            Bundle shareBundle = AppUtils.createShareBundle
                    (mTitle, mUrl, mThumbUrl, mUrl, getString(R.string.scheme_my_app));
            if (RemoteConfig.isUseInAppDeepLink()) {
                getShortDynamicLink(shareBundle, CustomSocial.WHATSAPP);
            } else {
                AppUtils.specificPackageShare(getActivity(), shareBundle,
                        CustomSocial.WHATSAPP.getPackageName());
            }
            MyAnalytics.shareContent(getActivity(), mTitle, mUrl);
        });

        btnSMS.setOnClickListener(v -> {
            Bundle shareBundle = AppUtils.createShareBundle
                    (mTitle, mUrl, mThumbUrl, mUrl, getString(R.string.scheme_my_app));
            if (RemoteConfig.isUseInAppDeepLink()) {
                getShortDynamicLink(shareBundle, CustomSocial.SMS);
            } else {
                AppUtils.smsShare(getActivity(), shareBundle);
            }
            MyAnalytics.shareContent(getActivity(), mTitle, mUrl);
        });

        /*if (!PrefUtils.isHardwareAccelerated(getActivity())
                && !RemoteConfig.isShowSingleEntryDetailLayout()) {
            // Do not use this with nested layout
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }*/

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null) return;

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.entryDetailBackground, typedValue, true);

        int backgroundColor = typedValue.data;
        String bgHexColor = String.format("#%06X", (0xFFFFFF & backgroundColor));

        mWebView.setBackgroundColor(backgroundColor);

        theme.resolveAttribute(R.attr.entryDetailColor, typedValue, true);
        String textHexColor = String.format("#%06X", (0xFFFFFF & typedValue.data));

        theme.resolveAttribute(R.attr.entryDetailHeaderColor, typedValue, true);
        String headerHexColor = String.format("#%06X", (0xFFFFFF & typedValue.data));

        theme.resolveAttribute(R.attr.entryDetailSubHeaderColor, typedValue, true);
        String subHeaderHexColor = String.format("#%06X", (0xFFFFFF & typedValue.data));

        theme.resolveAttribute(R.attr.entryDetailLinkColor, typedValue, true);
        String linkHexColor = String.format("#%06X", (0xFFFFFF & typedValue.data));

        theme.resolveAttribute(R.attr.entryDetailButtonColor, typedValue, true);
        String buttonHexColor = String.format("#%06X", (0xFFFFFF & typedValue.data));

        theme.resolveAttribute(R.attr.entryDetailButtonBorderColor, typedValue, true);
        String btnBorderHexColor = String.format("#%06X", (0xFFFFFF & typedValue.data));

        mCssOverride = "body {background:" + bgHexColor + "!important;color:" + textHexColor + "!important;}";
        mCssOverride += ".header_x a,h1,h2,h3,h4,h5,h6{color:" + headerHexColor + "!important;}";
        mCssOverride += ".subheader_x{color:" + subHeaderHexColor + "!important;}";
        mCssOverride += ".podCast_x{border-color:" + btnBorderHexColor + ";}";
        mCssOverride += ".buttonVisitWeb{border-color:" + btnBorderHexColor + ";color:" + buttonHexColor + "!important;}";
        mCssOverride += ".buttonComments{border-color:" + btnBorderHexColor + ";color:" + buttonHexColor + "!important;}";
        mCssOverride += "a:link{color:" + linkHexColor + ";}a:visited{color:" + linkHexColor + ";}";

        if (IS_CONTENT_CENTERED) {
            mCssOverride += ".content_x{text-align:center !important;}";
        }

        if (!IS_ENABLE_OPEN_IN_BROWSER) {
            mCssOverride += ".bodywrap_x #visitWeb{display:none !important;}";
        }

        switch (Integer.parseInt(PrefUtils.getEntryDetailFontSize(getActivity()))) {
            case 0:
                if (AppUtils.isTablet(getActivity()))
                    mCssOverride += "body{font-size:18px;}";
                else
                    mCssOverride += "body{font-size:14px;}";
                break;
            case 1:
                if (AppUtils.isTablet(getActivity()))
                    mCssOverride += "body{font-size:20px;}";
                else
                    mCssOverride += "body{font-size:16px;}";
                break;
            case 3:
                if (AppUtils.isTablet(getActivity()))
                    mCssOverride += "body{font-size:24px;}";
                else
                    mCssOverride += "body{font-size:20px;}";
                break;
            case 4:
                if (AppUtils.isTablet(getActivity()))
                    mCssOverride += "body{font-size:28px;}";
                else
                    mCssOverride += "body{font-size:24px;}";
                break;
            default:
                if (AppUtils.isTablet(getActivity()))
                    mCssOverride += "body{font-size:22px;}";
                else
                    mCssOverride += "body{font-size:18px;}";
                break;
        }

        if (TextUtils.isEmpty(mCssOverride.trim())) {
            mCssOverride = "";
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_ID, mCatId);
        outState.putInt(BUNDLE_ENTRY_ID, mEntryId);
        outState.putBoolean(BUNDLE_IS_GO_HOME, mIsGoHome);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mIsSingleLayout) {
            new AppExecutors().networkIO().execute(() -> {
                try {
                    if (getActivity() == null) return;
                    Context context = getActivity().getApplicationContext();
                    DataRepository repo = ((MyApplication) context).getRepository();
                    String url = repo.getEntryUrlById(mEntryId);
                    List<String> urls = Collections.singletonList(url);
                    repo.updateEntriesUnreadByUrl(0, urls);
                    repo.updateEntriesRecentReadByUrl(1, urls);
                    if (BuildConfig.DEBUG) Timber.d("url: %s marked as read!", url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        new LoadDataAsyncTask(this).execute(mEntryId);
    }

    @Override
    public void onCreateContextMenu(@NotNull ContextMenu menu, @NotNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        try {
            if (v.getId() == R.id.entry_detail_WebView) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (result != null && result.getExtra() != null) {
                    mResultExtra = result.getExtra().trim();
                    if (!TextUtils.isEmpty(mResultExtra)
                            && !mResultExtra.startsWith("file:///")
                            && !mResultExtra.startsWith("comments://")
                            && !mResultExtra.startsWith(mBaseUrl)) {
                        if (result.getType() == WebView.HitTestResult.IMAGE_TYPE
                                || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                            if (getActivity() != null) {
                                getActivity().getMenuInflater().inflate(R.menu.context_entry_detail, menu);
                                menu.setHeaderTitle(mResultExtra);
                                menu.findItem(R.id.context_selection_save_image).setVisible(true);
                                menu.findItem(R.id.context_selection_copy_url).setTitle(android.R.string.copyUrl);
                            }
                        } else if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
                            if (getActivity() != null) {
                                getActivity().getMenuInflater().inflate(R.menu.context_entry_detail, menu);
                                menu.setHeaderTitle(mResultExtra);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_selection_save_image:
                // Timber.d("onContextItemSelected() mResultValue= %s", mResultValue);
                if (!TextUtils.isEmpty(mResultExtra)) {
                    String fileName = URLUtil.guessFileName(mResultExtra, null, null);
                    mUrlToDownload = mResultExtra;
                    mFilenameToDownload = fileName;
                    AppUtils.downloadFile(getActivity(), mUrlToDownload, mFilenameToDownload);
                }
                return true;
            case R.id.context_selection_share:
                if (!TextUtils.isEmpty(mResultExtra)) {
                    String shareSubject = "";
                    String shareImage = "";
                    try {
                        if (mResultExtra.equals(mUrl)) {
                            shareSubject = mTitle;
                            shareImage = mThumbUrl;
                        }
                    } catch (Exception ignore) {
                    }
                    Bundle shareBundle = AppUtils.createShareBundle
                            (shareSubject, mResultExtra, shareImage, mResultExtra, getString(R.string.scheme_my_app));
                    AppUtils.contentShare(getActivity(), getChildFragmentManager(), shareBundle);
                }
                return true;
            case R.id.context_selection_copy_url:
                if (!TextUtils.isEmpty(mResultExtra))
                    AppUtils.copyToClipboard(getActivity(), mResultExtra);
                return true;
            case R.id.context_selection_open_in_browser:
                if (!TextUtils.isEmpty(mResultExtra)) openInAppBrowser(mResultExtra);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        MenuItem favoriteItem = menu.findItem(R.id.menu_toggle_bookmark);
        if (favoriteItem != null) {
            if (mIsBookmarked == 1) {
                favoriteItem.setTitle(R.string.context_toggle_un_bookmark)
                        .setIcon(R.drawable.ic_bookmark_white_24dp);
            } else {
                favoriteItem.setTitle(R.string.context_toggle_bookmark)
                        .setIcon(R.drawable.ic_bookmark_border_white_24dp);
            }
            AppUtils.tintMenuItemIcon(getActivity(), favoriteItem);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                reloadEntryDetail();
                return true;
            case R.id.menu_toggle_bookmark:
                new Thread(() -> {
                    if (getActivity() != null) {
                        try {
                            DataRepository repo = ((MyApplication) getActivity().getApplicationContext())
                                    .getRepository();

                            int isBookmarked = mIsBookmarked == 1 ? 0 : 1;
                            List<String> urls = Collections.singletonList(mUrl);
                            repo.updateEntriesBookmarkByUrl(isBookmarked, urls);

                            EntryEntity entry = repo.loadEntryById(mEntryId);
                            mIsBookmarked = entry.isBookmarked();
                            if (getActivity() != null)
                                getActivity().runOnUiThread(() -> getActivity().invalidateOptionsMenu());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return true;
            case R.id.menu_share:
                Bundle shareBundle = AppUtils.createShareBundle(
                        mTitle, mUrl, mThumbUrl, mUrl, getString(R.string.scheme_my_app));
                AppUtils.contentShare(getActivity(), getChildFragmentManager(), shareBundle);
                return true;
            case R.id.menu_open_in_browser:
                openInAppBrowser(mUrl);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSelected(EntryEntity entry) {
        if (getActivity() == null) return;

        Intent intent = new Intent(getActivity(), EntryDetailActivity.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(INTENT_EXTRA_ID, mCatId);
        intent.putExtra(INTENT_EXTRA_ENTRY_ID, entry.getId());
        intent.putExtra(INTENT_EXTRA_ENTRY_IMAGE_URL, entry.getThumbUrl());
        intent.putExtra(INTENT_EXTRA_IS_SINGLE_LAYOUT, true);
        intent.putExtra(INTENT_EXTRA_IS_GO_HOME, mIsGoHome);
        getActivity().startActivityForResult(intent, REQ_ENTRY_DETAIL_ACTIVITY);
        getActivity().finish();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (mWebView != null) {
                // Timber.d("onDetach() YOU ARE HERE");
                // mWebView.freeMemory();
                // mWebView.stopLoading();
                // mWebView.clearCache(true);
                mWebView.loadUrl(ABOUT_BLANK);
                mWebView.setWebChromeClient(null);
                mWebView.setWebViewClient(null);
                mWebView.removeAllViews();
                mWebView.destroy();
                mWebView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadFile(boolean isPermissionGranted) {
        if (isPermissionGranted) {
            if (!TextUtils.isEmpty(mUrlToDownload))
                AppUtils.downloadFile(getActivity(), mUrlToDownload, mFilenameToDownload);
        } else {
            if (!TextUtils.isEmpty(mUrlToDownload)) openInAppBrowser(mUrlToDownload);
        }
    }

    public void reloadEntryDetail() {
        try {
            new LoadDataAsyncTask(this).execute(mEntryId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void reloadEntryDetail(EntryDetailFragment fragment) {
        try {
            new LoadDataAsyncTask(fragment).execute(fragment.mEntryId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openInAppBrowser(String url) {
        AppUtils.chromeCustomTabs(getActivity(), url);
    }

    private static void openInAppBrowser(EntryDetailFragment fragment, String url) {
        AppUtils.chromeCustomTabs(fragment.getActivity(), url);
    }

    private static boolean shouldOverrideUrlLoading(EntryDetailFragment frag, String url) {
        if (frag != null && frag.isAdded() && !frag.isRemoving() && frag.getActivity() != null) {
            if (BuildConfig.DEBUG) Timber.d("shouldOverrideUrlLoading() url= %s", url);
            String hostName = url;

            try {
                URL mUrl = new URL(url);
                hostName = mUrl.getHost();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (hostName.contains(YOUTUBE_URL)
                    || hostName.contains(YOUTUBE_URL_SHORT)
                    || url.startsWith(VENDOR_YOUTUBE)) {
                String videoId = ParserHelper.getYoutubeVideoId(url);
                if (url.startsWith(VENDOR_YOUTUBE))
                    videoId = url.replace(VENDOR_YOUTUBE, "");

                if (!TextUtils.isEmpty(videoId) && videoId.trim().length() == 11) {
                    int playerOption = Integer.parseInt(
                            PrefUtils.getYouTubePlayerOption(frag.getActivity()));
                    AppUtils.openYouTube(frag.getActivity(), videoId, playerOption);
                    return true;
                } else {
                    openInAppBrowser(frag, url);
                    return true;
                }
            } else if (hostName.contains(INSTAGRAM_URL)
                    || hostName.contains(TWITTER_URL)) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (hostName.contains(INSTAGRAM_URL)) {
                        intent.setPackage("com.instagram.android");
                    } else if (hostName.contains(TWITTER_URL)) {
                        intent.setPackage("com.twitter.android");
                    }
                    frag.startActivity(intent);
                } catch (Exception e) {
                    openInAppBrowser(frag, url);
                }
                return true;
            } else if (url.startsWith("comments://")) {
                Intent intent = new Intent(frag.getActivity(), FbCommentsActivity.class);
                intent.putExtra(TAG_TITLE, frag.mTitle);
                intent.putExtra(TAG_URL, frag.mUrl);
                frag.startActivity(intent);
                return true;
            } else {
                // openInAppBrowser(frag, url);
                new OpenUrlAsyncTask(frag, url).execute(url);
                return true;
            }
        }
        return false;
    }

    private static void onLoadResource(EntryDetailFragment frag, String url) {
        if (frag != null && frag.isAdded() && !frag.isRemoving() && frag.getActivity() != null) {
            if (url.contains(YOUTUBE_URL)) {
                try {
                    String urlDecoded = URLDecoder.decode(url, UTF8);
                    if (urlDecoded.contains(YOUTUBE_GET_VIDEO_INFO)) {
                        int j = urlDecoded.indexOf(YOUTUBE_VIDEO_ID) + YOUTUBE_VIDEO_ID.length();
                        String videoId = urlDecoded.substring(j, urlDecoded.indexOf("&", j));

                        if (!TextUtils.isEmpty(videoId) && videoId.trim().length() == 11) {
                            // view.stopLoading(); // Must Stop loading WebView
                            reloadEntryDetail(frag);
                            int playerOption = Integer.parseInt(
                                    PrefUtils.getYouTubePlayerOption(frag.getActivity()));
                            AppUtils.openYouTube(frag.getActivity(), videoId, playerOption);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    reloadEntryDetail(frag);
                    openInAppBrowser(frag, url);
                }
            } else if (url.contains(VIMEO_URL)) {
                try {
                    String urlDecoded = URLDecoder.decode(url, UTF8);
                    if (urlDecoded.contains("player.vimeo.com/v2/video/")
                            && urlDecoded.contains("outro")) {
                        int j = urlDecoded.indexOf("video/") + "video/".length();
                        String videoId = urlDecoded.substring(j, urlDecoded.indexOf("/", j));

                        if (StringUtil.isNumeric(videoId)) {

                            String vimeoUrl = "http://vimeo.com/" + videoId;

                            // view.stopLoading(); // Must Stop loading WebView
                            if (BuildConfig.DEBUG) Timber.d("onLoadResource() url= %s", vimeoUrl);
                            reloadEntryDetail(frag);
                            openInAppBrowser(frag, vimeoUrl);
                        }
                    }
                } catch (Exception e) {
                    reloadEntryDetail(frag);
                    openInAppBrowser(frag, url);
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static class MyWebViewClientNougatAndAbove extends WebViewClient {
        private WeakReference<EntryDetailFragment> mFragment;

        MyWebViewClientNougatAndAbove(EntryDetailFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            EntryDetailFragment frag = mFragment.get();
            return EntryDetailFragment.shouldOverrideUrlLoading(frag, request.getUrl().toString());
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            EntryDetailFragment.onLoadResource(mFragment.get(), url);
            super.onLoadResource(view, url);
        }
    }

    private static class MyWebViewClientOLD extends WebViewClient {
        private WeakReference<EntryDetailFragment> mFragment;

        MyWebViewClientOLD(EntryDetailFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return EntryDetailFragment.shouldOverrideUrlLoading(mFragment.get(), url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            EntryDetailFragment.onLoadResource(mFragment.get(), url);
            super.onLoadResource(view, url);
        }
    }

    private static class MyWebChromeClient extends WebChromeClient {
        private WeakReference<EntryDetailFragment> mFragment;

        MyWebChromeClient(EntryDetailFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        public void onProgressChanged(WebView view, int progress) {
            try {
                EntryDetailFragment frag = mFragment.get();
                if (frag != null && frag.isAdded() && !frag.isRemoving()) {
                    frag.mProgressBar.setProgress(progress);
                    if (progress == 100) {
                        frag.mProgressBar.setVisibility(View.GONE);
                    } else {
                        frag.mProgressBar.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class OpenUrlAsyncTask extends AsyncTask<String, Void, EntryEntity> {
        private WeakReference<EntryDetailFragment> mFragment;
        private String _mUrl;

        OpenUrlAsyncTask(EntryDetailFragment fragment, String url) {
            this.mFragment = new WeakReference<>(fragment);
            this._mUrl = url;
        }

        @Override
        protected EntryEntity doInBackground(String... strings) {
            try {
                EntryDetailFragment frag = mFragment.get();
                if (frag != null && frag.isAdded() && frag.getActivity() != null) {
                    Context context = frag.getActivity().getApplicationContext();
                    DataRepository repo = ((MyApplication) context).getRepository();
                    String url = strings[0];
                    if (!TextUtils.isEmpty(url)) {
                        if (url.startsWith(HTTPS)) {
                            url = HTTP + url.substring(HTTPS.length());
                        }
                    }
                    return repo.getEntryByUrl(url);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(EntryEntity entry) {
            EntryDetailFragment frag = mFragment.get();
            if (frag != null && frag.isAdded() && !frag.isRemoving()) {
                try {
                    if (entry != null && frag.getActivity() != null) {
                        Intent intent = new Intent(frag.getActivity(), EntryDetailActivity.class);
                        intent.putExtra(INTENT_EXTRA_ID, frag.mCatId);
                        intent.putExtra(INTENT_EXTRA_ENTRY_ID, entry.getId());
                        intent.putExtra(INTENT_EXTRA_ENTRY_IMAGE_URL, entry.getThumbUrl());
                        intent.putExtra(INTENT_EXTRA_IS_SINGLE_LAYOUT, true);
                        intent.putExtra(INTENT_EXTRA_IS_GO_HOME, frag.mIsGoHome);
                        frag.getActivity().startActivityForResult(intent, REQ_ENTRY_DETAIL_ACTIVITY);
                        frag.getActivity().finish();
                    } else {
                        openInAppBrowser(frag, _mUrl);
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                    openInAppBrowser(frag, _mUrl);
                }
            }
        }
    }

    private static class LoadDataAsyncTask extends AsyncTask<Integer, Void, EntryEntity> {
        private WeakReference<EntryDetailFragment> mFragment;

        LoadDataAsyncTask(EntryDetailFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected EntryEntity doInBackground(Integer... integers) {
            try {
                EntryDetailFragment frag = mFragment.get();
                if (frag != null && frag.isAdded() && frag.getActivity() != null) {
                    Context context = frag.getActivity().getApplicationContext();
                    DataRepository repo = ((MyApplication) context).getRepository();
                    return repo.loadEntryById(integers[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(EntryEntity entry) {
            try {
                if (entry == null) return;

                EntryDetailFragment frag = mFragment.get();
                if (frag != null && frag.isAdded() && !frag.isRemoving() && frag.getActivity() != null) {
                    Context context = frag.getActivity().getApplicationContext();

                    frag.mTitle = entry.getTitle();
                    String author = entry.getAuthor();
                    String date = entry.getDate();
                    long dateMillis = entry.getDateMillis();
                    frag.mUrl = entry.getUrl();
                    frag.mThumbUrl = entry.getThumbUrl();
                    String content = entry.getContent();

                    frag.mIsBookmarked = entry.isBookmarked();
                    frag.getActivity().invalidateOptionsMenu();

                    try {
                        if (dateMillis != 0) {
                            CharSequence prettyTime = DateUtils.getRelativeTimeSpanString(
                                    dateMillis, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
                            if (!TextUtils.isEmpty(prettyTime)) date = prettyTime.toString();
                        } else {
                            String parsedDate = MyDateUtils.parseTimestampToString(date);
                            if (!TextUtils.isEmpty(parsedDate)) date = parsedDate;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    date = TextUtils.isEmpty(date) ? "" : "<span id=\"entryDate\">" + date + "</span>";

                    String subHeader;
                    if (IS_SHOW_AUTHOR && !TextUtils.isEmpty(author)) {
                        author = "<span id=\"entryAuthor\"><b>" + author + "</b></span>";
                        subHeader = String.format("%s%s%s", author, "<span> · </span>", date);
                    } else {
                        subHeader = String.format("%s", date);
                    }

                    String visitWeb = context.getString(R.string.open_in_browser);
                    String comments = context.getString(R.string.comments);

                    if (frag.mIsSingleLayout) {
                        if (!TextUtils.isEmpty(frag.mThumbUrl)) {
                            Document contentDoc = Jsoup.parse(content, "", Parser.htmlParser());
                            contentDoc.select("img[src*=" + frag.mThumbUrl + "]").remove();
                            content = contentDoc.html();
                        }
                    }

                    // Create docType HTML
                    String _data = ParserHelper.createDocHtml(frag.mTitle, subHeader, frag.mUrl, content,
                            frag.mCssOverride, visitWeb, comments);

                    String baseUrl = null;
                    try {
                        URL _url = new URL(frag.mUrl);
                        baseUrl = _url.getProtocol() + "://" + _url.getHost();
                        frag.mBaseUrl = baseUrl;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (frag.mWebView == null) return;

                    frag.mWebView.loadDataWithBaseURL(baseUrl, _data, MIMETYPE, UTF8, null);

                    showFooter(frag);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void showFooter(EntryDetailFragment frag) {
        new Handler().postDelayed(() -> {
            try {
                if (RemoteConfig.isShowEntryDetailSocialButtons())
                    showSocialButtons(frag);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (RemoteConfig.isShowEntryDetailHorizontalNativeAds())
                    showHorizontalScrollAd(frag);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (RemoteConfig.isShowRelatedPost() && frag != null)
                    new LoadRelatedPostAsyncTask(frag).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, RemoteConfig.getEntryDetailFooterDelayMillis());
    }

    private static void showSocialButtons(EntryDetailFragment frag) {
        if (frag == null || !frag.isAdded() || frag.isRemoving() || frag.getView() == null)
            return;
        frag.getView().findViewById(R.id.socialButtonsContainer).setVisibility(View.GONE);
    }

    private static void showHorizontalScrollAd(EntryDetailFragment frag) { // TODO showHorizontalScrollAd();
        if (frag == null || !frag.isAdded()
                || frag.isRemoving() || frag.getActivity() == null) return;

        Activity activity = frag.getActivity();
        View view = frag.getView();


    }

    private void getShortDynamicLink(Bundle bundle, CustomSocial customSocial) {
        if (getActivity() == null || bundle == null || bundle.isEmpty()) return;

        Uri longDeepLink = AppUtils.buildDeepLink(bundle);
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(longDeepLink)
                .buildShortDynamicLink()
                .addOnFailureListener(getActivity(), e -> {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                })
                .addOnCompleteListener(getActivity(), task -> {
                    String shareSubject = bundle.getString(BUNDLE_SHARE_SUBJECT, "");
                    String shareBody = bundle.getString(BUNDLE_SHARE_BODY, "");

                    if (task.isSuccessful() && task.getResult() != null) {
                        String shortLink = task.getResult().getShortLink().toString();
                        String flowchartLink = task.getResult().getPreviewLink().toString();

                        if (!TextUtils.isEmpty(shortLink)) shareBody = shortLink;

                        if (BuildConfig.DEBUG)
                            Timber.d("\nlongDeepLink= %s \nshortDeepLink= %s \nflowchartLink= %s",
                                    longDeepLink.toString(), shortLink, flowchartLink);

                        Bundle shareBundle = AppUtils
                                .createShareBundle(shareSubject, shareBody, "", "", "");
                       /* if (customSocial.name().equalsIgnoreCase(CustomSocial.FACEBOOK.name())) {
                            AppUtils.facebookLinkContentShare(getActivity(), shareBundle, customSocial.getPackageName());
                        } else */if (customSocial.name().equalsIgnoreCase(CustomSocial.SMS.name())) {
                            AppUtils.smsShare(getActivity(), shareBundle);
                        } else if (customSocial.name().equalsIgnoreCase(CustomSocial.ANDROID.name())) {
                            AppUtils.share(getActivity(), shareBundle);
                        } else {
                            AppUtils.specificPackageShare(getActivity(), shareBundle, customSocial.getPackageName());
                        }
                    } else {
                        if (BuildConfig.DEBUG)
                            Timber.w("onComplete() isSuccessful()= %s", task.isSuccessful());

                        shareBody = longDeepLink.toString();
                        Bundle shareBundle = AppUtils
                                .createShareBundle(shareSubject, shareBody, "", "", "");
                       /* if (customSocial.name().equalsIgnoreCase(CustomSocial.FACEBOOK.name())) {
                            AppUtils.facebookLinkContentShare(getActivity(), shareBundle, customSocial.getPackageName());
                        } else */if (customSocial.name().equalsIgnoreCase(CustomSocial.SMS.name())) {
                            AppUtils.smsShare(getActivity(), shareBundle);
                        } else if (customSocial.name().equalsIgnoreCase(CustomSocial.ANDROID.name())) {
                            AppUtils.share(getActivity(), shareBundle);
                        } else {
                            AppUtils.specificPackageShare(getActivity(), shareBundle, customSocial.getPackageName());
                        }
                    }
                });
    }

    private static class LoadRelatedPostAsyncTask extends AsyncTask<Void, Void, List<EntryEntity>> {
        private WeakReference<EntryDetailFragment> mFragment;

        LoadRelatedPostAsyncTask(EntryDetailFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        protected List<EntryEntity> doInBackground(Void... voids) {
            try {
                EntryDetailFragment frag = mFragment.get();
                if (frag != null && frag.isAdded() && !frag.isRemoving() && frag.getActivity() != null) {
                    Context context = frag.getActivity().getApplicationContext();
                    DataRepository repo = ((MyApplication) context).getRepository();

                    if (frag.mCatId == 0) {
                        int feedId = repo.getDatabase().entryDao().getFeedIdByUrl(frag.mUrl);
                        frag.mCatId = repo.getDatabase().feedDao().getCategoryIdById(feedId);
                    }

                    return repo.getDatabase().entryDao()
                            .getEntriesByCategoryFilterUrl(frag.mCatId, frag.mUrl,
                                    (int) RemoteConfig.getRelatedPostsLimit());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<EntryEntity> entries) {
            try {
                if (entries == null || entries.isEmpty()) return;

                EntryDetailFragment frag = mFragment.get();
                if (frag != null && frag.isAdded() && !frag.isRemoving()
                        && frag.getActivity() != null && frag.getView() != null) {
                    View view = frag.getView();

                    CarouselPagerContainer pagerContainer = view.findViewById(R.id.pager_container);
                    ViewPager viewPager = pagerContainer.getViewPager();

                    Context context = frag.getActivity();
                    RelatedPostPagerAdapter adapter =
                            new RelatedPostPagerAdapter(context, entries, frag);
                    viewPager.setAdapter(adapter);

                    if (adapter.getCount() > 0) {
                        NestedScrollView nsv = view.findViewById(R.id.nested_scroll_view);
                        nsv.setBackgroundColor(Color.TRANSPARENT);
                        view.findViewById(R.id.shadow).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.rootRelatedPost).setVisibility(View.VISIBLE);
                    }
                    // Necessary or the pager will only have one extra page to show
                    // make this at least however many pages you can see
                    viewPager.setOffscreenPageLimit(adapter.getCount());
                    // A little space between pages
                    viewPager.setPageMargin(36);
                    // If hardware acceleration is enabled, you should also remove
                    // clipping on the pager for its children.
                    viewPager.setClipChildren(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
