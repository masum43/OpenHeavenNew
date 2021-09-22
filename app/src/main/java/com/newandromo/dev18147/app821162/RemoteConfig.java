package com.newandromo.dev18147.app821162;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.newandromo.dev18147.app821162.db.DataGenerator;
import com.newandromo.dev18147.app821162.db.DataRepository;
import com.newandromo.dev18147.app821162.db.entity.CategoryEntity;
import com.newandromo.dev18147.app821162.db.entity.FeedEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;
import com.newandromo.dev18147.app821162.utils.PrefUtils;
import timber.log.Timber;

public class RemoteConfig {
    private static final String APP_NAME = "app_name";
    private static final String SITE_URL = "site_url";
    private static final String DEFAULT_FEEDS_JSON = "default_feeds_json";
    private static final String MENU_HEADER_IMAGE_URL = "menu_header_image_url";
    private static final String IS_SHOW_APP_ICON = "is_show_app_icon";
    private static final String IS_SHOW_HEADER_TITLE = "is_show_header_title";
    private static final String WORDPRESS_SEARCH_RESULTS = "wordpress_search_results";
    private static final String IS_SHOW_FEED_CATEGORIES = "is_show_feed_categories";
    private static final String FACEBOOK_URL = "facebook_url";
    private static final String INSTAGRAM_URL = "instagram_url";
    private static final String TWITTER_URL = "twitter_url";
    private static final String YOUTUBE_URL = "youtube_url";
    private static final String IS_ENABLE_FEED_LANGUAGE_OPTION = "is_enable_feed_language_option";
    private static final String IS_ENABLE_LAYOUT_TOGGLE = "is_enable_layout_toggle";
    private static final String APP_STORE_URL_GOOGLE = "app_store_url_google";
    private static final String APP_DYNAMIC_LINK_DOMAIN = "app_dynamic_link_domain";
    private static final String PROMOTE_MUSIC_URL = "promote_music_url";
    private static final String APP_VERSION_CODE = "app_version_code";
    private static final String APP_REPOSITORY_URL = "app_repository_url";
    private static final String DEVELOPER_NAME = "developer_name";
    private static final String DEVELOPER_EMAIL = "developer_email";
    private static final String DEVELOPER_PHONE = "developer_phone";
    private static final String DEVELOPER_WEBSITE = "developer_website";
    private static final String CLIENT_EMAIL = "client_email";
    private static final String CLIENT_PHONE = "client_phone";
    private static final String ANDROID_DEVELOPER_API_KEY = "android_developer_api_key";
    private static final String ADMOB_BANNER_ID = "admob_banner_id";
    private static final String ADMOB_BANNER_ID_ENTRY_LIST = "admob_banner_id_entry_list";
    private static final String ADMOB_BANNER_ID_ENTRY_DETAIL = "admob_banner_id_entry_detail";
    private static final String ADMOB_INTERSTITIAL_ID = "admob_interstitial_id";
    private static final String ADMOB_INTERSTITIAL_ID_ENTRY_LIST = "admob_interstitial_id_entry_list";
    private static final String MOPUB_BANNER_ID_ENTRY_LIST = "mopub_banner_id_entry_list";
    private static final String MOPUB_BANNER_ID_ENTRY_DETAIL = "mopub_banner_id_entry_detail";
    private static final String MOPUB_INTERSTITIAL_ID_ENTRY_LIST = "mopub_interstitial_id_entry_list";
    private static final String MOPUB_NATIVE_ID_ENTRY_LIST = "mopub_native_id_entry_list";
    private static final String MOPUB_NATIVE_ID_ENTRY_LIST_TINY = "mopub_native_id_entry_list_tiny";
    private static final String FB_BANNER_ID_ENTRY_LIST = "facebook_banner_id_entry_list";
    private static final String FB_BANNER_ID_ENTRY_DETAIL = "facebook_banner_id_entry_detail";
    private static final String FB_BANNER_ID_YOUTUBE = "facebook_banner_id_youtube";
    private static final String FB_INTERSTITIAL_ID_ENTRY_LIST = "facebook_interstitial_id_entry_list";
    private static final String FB_HS_NATIVE_ID_ENTRY_DETAIL = "facebook_hs_native_id_entry_list";
    private static final String SHOW_MOPUB_INTERSTITIAL_ENTRY_LIST = "show_mopub_interstitial_entry_list";
    private static final String SHOW_FB_INTERSTITIAL_ENTRY_LIST = "show_fb_interstitial_entry_list";
    private static final String SHOW_INTERSTITIAL_ENTRY_DETAIL = "show_interstitial_entry_detail";
    private static final String SHOW_INTERSTITIAL_YOUTUBE_PLAYER = "show_interstitial_youtube_player";
    private static final String SHOW_INTERSTITIAL_YOUTUBE_SEARCH = "show_interstitial_youtube_search";
    private static final String SHOW_BANNER_ADS_ENTRY_LIST = "show_banner_ads_entry_list";
    private static final String SHOW_BANNER_ADS_ENTRY_DETAIL = "show_banner_ads_entry_detail";
    private static final String SHOW_BANNER_ADS_YOUTUBE_LIST = "show_banner_ads_youtube_list";
    private static final String SHOW_BANNER_ADS_YOUTUBE_SEARCH = "show_banner_ads_youtube_search";
    private static final String SHOW_MOPUB_BANNER_ENTRY_LIST = "show_mopub_banner_ad_entry_list";
    private static final String SHOW_FB_BANNER_ENTRY_LIST = "show_facebook_banner_ad_entry_list";
    private static final String SHOW_MOPUB_BANNER_ENTRY_DETAIL = "show_mopub_banner_ad_entry_detail";
    private static final String SHOW_FB_BANNER_ENTRY_DETAIL = "show_facebook_banner_ad_entry_detail";
    private static final String SHOW_FB_BANNER_YOUTUBE_LIST = "show_facebook_banner_ad_youtube_list";
    private static final String SHOW_FB_BANNER_YOUTUBE_SEARCH = "show_facebook_banner_ad_youtube_search";
    private static final String SHOW_SINGLE_ENTRY_DETAIL_LAYOUT = "show_single_entry_detail_layout";
    private static final String ENTRY_DETAIL_FOOTER_DELAY_MILLIS = "entry_detail_footer_delay_millis";
    private static final String SHOW_ED_SOCIAL_BUTTONS = "show_entry_detail_social_buttons";
    private static final String SHOW_NATIVES_IN_EL_CATEGORIES = "show_natives_in_entry_list_categories";
    private static final String SHOW_ED_HORIZONTAL_NATIVE_ADS = "show_entry_detail_horizontal_natives";
    private static final String NUMBER_OF_HORIZONTAL_NATIVE_ADS = "number_of_horizontal_native_ads";
    private static final String IS_SHOW_RELATED_POST = "is_show_related_post";
    private static final String RELATED_POSTS_LIMIT = "related_posts_limit";
    private static final String IS_ENABLE_NOTIFICATION_SOUND = "is_enable_notification_sound";
    private static final String IS_ENABLE_NOTIFICATION_VIBRATION = "is_enable_notification_vibration";
    private static final String IS_SHOW_CUSTOM_NOTIFICATION = "is_show_custom_notification";
    private static final String IS_SHOW_HEADS_UP_NOTIFICATION = "is_show_heads_up_notification";
    private static final String IS_SHOW_HEADS_UP_NOTIFICATION_BUNDLE = "is_show_heads_up_notification_bundle";
    private static final String YOUTUBE_DATA_API_VERSION = "youtube_data_api_version";
    private static final String IS_OVERRIDE_YOUTUBE_PLAYER = "is_override_youtube_player";
    private static final String IS_ENABLE_YOUTUBE_SEARCH_SUGGESTIONS = "is_enable_youtube_search_suggestions";
    private static final String IS_USE_IN_APP_DEEP_LINK = "is_use_in_app_deep_link";
    private static final String IS_ACCEPT_COOKIES = "is_accept_cookies";
    private static final String IS_LIMIT_OKHTTP_REQUESTS = "is_limit_okhttp_requests";
    private static final String OKHTTP_MAX_REQUESTS = "okhttp_max_requests";
    private static final String IS_RESTORE_LIST_STATE = "is_restore_list_state";
    private static final String IS_ENABLE_ONLINE_RADIO = "is_enable_online_radio";
    private static final String ONLINE_RADIO_DB = "online_radio_data";

    private RemoteConfig() {
    }

    public static void initiateRemoteConfig(Context context) {
        try {
            FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

            long cacheExpiration = TimeUnit.MINUTES.toSeconds(20); // 20 minutes in seconds.

            // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
            // the server.
            if (BuildConfig.DEBUG) {
                cacheExpiration = 0;
            }

            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(cacheExpiration)
                    .build();

            // firebaseRemoteConfig.setConfigSettings(configSettings);
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);

            // firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
            firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

            // [START fetch_config_with_callback]
            // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
            // fetched and cached config would be considered expired because it would have been fetched
            // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
            // throttling is in progress. The default expiration duration is 43200 (12 hours).
            AppExecutors executors = new AppExecutors();
            firebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(executors.networkIO(), task -> {
                        if (task.isSuccessful()) {
                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            if (BuildConfig.DEBUG) Timber.d("onComplete() Fetch Succeeded");

                            updateOnRemoteConfigFetchSuccess(context, firebaseRemoteConfig);

                        } else if (BuildConfig.DEBUG) Timber.e("onComplete() Fetch failed");
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // TODO CONFIG
    // ---------------------------------------------------------------------------------------------

    private static void updateOnRemoteConfigFetchSuccess(Context context, FirebaseRemoteConfig firebaseRemoteConfig) {
        try {
            String jsonString = firebaseRemoteConfig.getString(DEFAULT_FEEDS_JSON);

            if (!TextUtils.isEmpty(jsonString)) {
                JSONObject jsonObject = new JSONObject(jsonString);

                int currentVersion = PrefUtils.getDefaultFeedsVersion(context);
                int remoteVersion = jsonObject.getInt("version");

                if (remoteVersion > currentVersion) {
                    PrefUtils.setDefaultFeedsVersion(context, remoteVersion);

                    DataRepository repo = ((MyApplication) context).getRepository();

                    repo.deleteAllFeeds();
                    repo.deleteAllCategories();
                    repo.deleteAllYoutubeTypes();

                    JSONArray defaultsArray = jsonObject.getJSONArray("defaults");

                    List<String> languageCodes = new ArrayList<>();
                    for (int i = 0; i < defaultsArray.length(); i++) {
                        languageCodes.add(defaultsArray.getJSONObject(i).getString("id"));
                    }

                    String languageCode = PrefUtils.getFeedsLanguageCode(context);
                    int index = languageCodes.contains(languageCode) ?
                            languageCodes.indexOf(languageCode) : 0;

                    JSONObject defaultObj = defaultsArray.getJSONObject(index);

                    List<FeedEntity> feeds = DataGenerator.populateFeedsByCategory(repo.getDatabase(), defaultObj);

                    List<YoutubeTypeEntity> youtubeTypes = DataGenerator.populateYoutubeTypes(defaultObj);

                    repo.insertFeeds(feeds);
                    repo.insertYoutubeTypes(youtubeTypes);

                    try {
                        List<CategoryEntity> categories = repo.getAllCategories();
                        if (categories != null && !categories.isEmpty()) {
                            for (CategoryEntity category : categories) {
                                int id = category.getId();
                                PrefUtils.setIsCategoryRefreshed(context, id, false);
                                PrefUtils.setCurrentListPosition(context, id, 0);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    PrefUtils.globalRefresh(context);
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
                Timber.e("initUpdateDefaultFeeds() error= %s", e.getMessage());
            }
        }
    }

    public static String getAppStoreUrl() {
        return getGoogleAppStoreUrl();
    }

    public static String getAppName() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(APP_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSiteUrl() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(SITE_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDefaultFeedsJson() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(DEFAULT_FEEDS_JSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getMenuHeaderImageUrl() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(MENU_HEADER_IMAGE_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isShowAppIcon() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_SHOW_APP_ICON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowHeaderTitle() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_SHOW_HEADER_TITLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getWordPressSearchResults() {
        try {
            long remote = FirebaseRemoteConfig.getInstance().getLong(WORDPRESS_SEARCH_RESULTS);
            return remote >= 10 ? remote : 10;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 10;
    }

    public static boolean isEnableFeedLanguageOption() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_ENABLE_FEED_LANGUAGE_OPTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isEnableLayoutToggle() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_ENABLE_LAYOUT_TOGGLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShowFeedCategories() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_SHOW_FEED_CATEGORIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static String getGoogleAppStoreUrl() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(APP_STORE_URL_GOOGLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAppDynamicLinkDomain() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(APP_DYNAMIC_LINK_DOMAIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPromoteMusicUrl() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(PROMOTE_MUSIC_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getAppVersionCode() {
        try {
            return FirebaseRemoteConfig.getInstance().getLong(APP_VERSION_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getAppRepositoryUrl() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(APP_REPOSITORY_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDeveloperName() {
        return FirebaseRemoteConfig.getInstance().getString(DEVELOPER_NAME);
    }

    public static String getDeveloperEmail() {
        return FirebaseRemoteConfig.getInstance().getString(DEVELOPER_EMAIL);
    }

    public static String getDeveloperPhone() {
        return FirebaseRemoteConfig.getInstance().getString(DEVELOPER_PHONE);
    }

    public static String getDeveloperWebsite() {
        return FirebaseRemoteConfig.getInstance().getString(DEVELOPER_WEBSITE);
    }

    public static String getClientEmail() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(CLIENT_EMAIL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getClientPhone() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(CLIENT_PHONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFacebookUrl() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(FACEBOOK_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getInstagramUrl() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(INSTAGRAM_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getTwitterUrl() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(TWITTER_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getYoutubeUrl() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(YOUTUBE_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAndroidDeveloperApiKey() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(ANDROID_DEVELOPER_API_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    // ---------------------------------------------------------------------------------------------

    /*public static String getMoPubBannerIdEntryList() {
        try {
            String remote = FirebaseRemoteConfig.getInstance().getString(MOPUB_BANNER_ID_ENTRY_LIST);
            return !TextUtils.isEmpty(remote.trim()) ? remote : AdUtils.MOPUB_BANNER_ENTRY_LIST;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AdUtils.MOPUB_BANNER_ENTRY_LIST;
    }*/

   /* public static String getMoPubBannerIdEntryDetail() {
        try {
            String remote = FirebaseRemoteConfig.getInstance().getString(MOPUB_BANNER_ID_ENTRY_DETAIL);
            return !TextUtils.isEmpty(remote.trim()) ? remote : AdUtils.MOPUB_BANNER_ENTRY_DETAIL;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AdUtils.MOPUB_BANNER_ENTRY_DETAIL;
    }*/

    /*public static String getMoPubInterstitialIdEntryList() {
        try {
            String remote = FirebaseRemoteConfig.getInstance().getString(MOPUB_INTERSTITIAL_ID_ENTRY_LIST);
            return !TextUtils.isEmpty(remote.trim()) ? remote : AdUtils.MOPUB_INTERSTITIAL_ENTRY_LIST;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AdUtils.MOPUB_INTERSTITIAL_ENTRY_LIST;
    }

    public static String getMoPubNativeIdEntryList() {
        try {
            String remote = FirebaseRemoteConfig.getInstance().getString(MOPUB_NATIVE_ID_ENTRY_LIST);
            return !TextUtils.isEmpty(remote.trim()) ? remote : AdUtils.MOPUB_NATIVE_ENTRY_LIST;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AdUtils.MOPUB_NATIVE_ENTRY_LIST;
    }*/

  /*  public static String getMoPubNativeIdEntryListTiny() {
        try {
            String remote = FirebaseRemoteConfig.getInstance().getString(MOPUB_NATIVE_ID_ENTRY_LIST_TINY);
            return !TextUtils.isEmpty(remote.trim()) ? remote : AdUtils.MOPUB_NATIVE_ENTRY_LIST_TINY;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AdUtils.MOPUB_NATIVE_ENTRY_LIST_TINY;
    }
*/
    // ---------------------------------------------------------------------------------------------

    public static boolean isShowEntryDetailSocialButtons() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_ED_SOCIAL_BUTTONS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShowNativesInEntryListCategories() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_NATIVES_IN_EL_CATEGORIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowEntryDetailHorizontalNativeAds() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_ED_HORIZONTAL_NATIVE_ADS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static int getNumberOfHorizontalNativeAds() {
        try {
            int remote = (int) FirebaseRemoteConfig.getInstance().getLong(NUMBER_OF_HORIZONTAL_NATIVE_ADS);
            return remote >= 1 ? remote : 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    // ---------------------------------------------------------------------------------------------

    public static boolean isShowMoPubInterstitialEntryList() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_MOPUB_INTERSTITIAL_ENTRY_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowFBInterstitialEntryList() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_FB_INTERSTITIAL_ENTRY_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowInterstitialEntryDetail() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_INTERSTITIAL_ENTRY_DETAIL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowInterstitialYouTubePlayer() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_INTERSTITIAL_YOUTUBE_PLAYER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShowInterstitialYouTubeSearch() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_INTERSTITIAL_YOUTUBE_SEARCH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // -----------------------------------------------------------------------------------------------------

    public static boolean isShowBannerAdsEntryList() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_BANNER_ADS_ENTRY_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShowBannerAdsEntryDetail() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_BANNER_ADS_ENTRY_DETAIL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShowBannerAdsYoutubeList() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_BANNER_ADS_YOUTUBE_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShowBannerAdsYoutubeSearch() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_BANNER_ADS_YOUTUBE_SEARCH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShowMoPubBannerAdEntryList() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_MOPUB_BANNER_ENTRY_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShowFacebookBannerAdEntryList() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_FB_BANNER_ENTRY_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShowMoPubBannerAdEntryDetail() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_MOPUB_BANNER_ENTRY_DETAIL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowFacebookBannerAdEntryDetail() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_FB_BANNER_ENTRY_DETAIL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowFacebookBannerAdYouTubeList() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_FB_BANNER_YOUTUBE_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isShowFacebookBannerAdYouTubeSearch() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_FB_BANNER_YOUTUBE_SEARCH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // ---------------------------------------------------------------------------------------------

    public static boolean isShowSingleEntryDetailLayout() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(SHOW_SINGLE_ENTRY_DETAIL_LAYOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getEntryDetailFooterDelayMillis() {
        try {
            long remote = FirebaseRemoteConfig.getInstance().getLong(ENTRY_DETAIL_FOOTER_DELAY_MILLIS);
            return remote >= 0 ? remote : 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isShowRelatedPost() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_SHOW_RELATED_POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static long getRelatedPostsLimit() {
        try {
            long remote = FirebaseRemoteConfig.getInstance().getLong(RELATED_POSTS_LIMIT);
            return remote >= 1 ? remote : 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static boolean isEnableNotificationSound() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_ENABLE_NOTIFICATION_SOUND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isEnableNotificationVibration() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_ENABLE_NOTIFICATION_VIBRATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowCustomNotification() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_SHOW_CUSTOM_NOTIFICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowHeadsUpNotification() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_SHOW_HEADS_UP_NOTIFICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isShowHeadsUpNotificationBundle() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_SHOW_HEADS_UP_NOTIFICATION_BUNDLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String youtubeDataAPIVersion() {
        try {
            String remote = FirebaseRemoteConfig.getInstance().getString(YOUTUBE_DATA_API_VERSION);
            return !TextUtils.isEmpty(remote.trim()) ? remote : "v3";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "v3";
    }

    public static boolean isOverrideYoutubePlayer() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_OVERRIDE_YOUTUBE_PLAYER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isEnableYoutubeSearchSuggestions() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_ENABLE_YOUTUBE_SEARCH_SUGGESTIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isUseInAppDeepLink() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_USE_IN_APP_DEEP_LINK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isAcceptCookies() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_ACCEPT_COOKIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean isLimitOkHttpMaxRequests() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_LIMIT_OKHTTP_REQUESTS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static int getOkHttpMaxRequests() {
        try {
            int remote = (int) FirebaseRemoteConfig.getInstance().getLong(OKHTTP_MAX_REQUESTS);
            return remote >= 3 ? remote : 3; // My Default OkHttp Dispatcher Maximum Requests = 3
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 3;
    }

    public static boolean isRestoreListState() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_RESTORE_LIST_STATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isEnableOnlineRadio() {
        try {
            return FirebaseRemoteConfig.getInstance().getBoolean(IS_ENABLE_ONLINE_RADIO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getOnlineRadioData() {
        try {
            return FirebaseRemoteConfig.getInstance().getString(ONLINE_RADIO_DB);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
