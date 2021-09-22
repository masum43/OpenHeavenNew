package com.newandromo.dev18147.app821162.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.newandromo.dev18147.app821162.R;
import com.newandromo.dev18147.app821162.ui.enums.Theme;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.ALL_ENTRIES;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.APP_THEME;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.DEFAULT_FEEDS_VERSION;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.DEFAULT_LAYOUT_TYPE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.FEEDS_LANGUAGE_CODE;

/**
 * Utilities and constants related to app preferences.
 */
public class PrefUtils {
    public static final String PREF_APP_THEME = "theme_app";
    public static final String PREF_GLOBAL_REFRESH = "global_refresh";
    public static final String PREF_TOOLBAR_CLICKED = "toolbar_clicked";
    public static final String PREF_ITEMS_STORAGE_LIMIT = "items_storage_limit";
    public static final String PREF_LAYOUT_TYPE = "layout_type";
    public static final String PREF_VIEW_MODE = "view_mode";
    public static final String PREF_SORT_BY_DATE = "sort_by_date";
    public static final String PREF_ENTRY_DETAIL_FONT_SIZE = "entry_detail_font_size";
    public static final String PREF_YOUTUBE_PLAYER_OPTIONS = "youtube_player_options";
    public static final String PREF_FEEDS_LANGUAGE_CODE = "feeds_language_code";
    private static final String PREF_DEFAULT_FEEDS_VERSION = "default_feeds_version";
    public static final String PREF_YOUTUBE_TERMS_AND_PRIVACY = "youtube_terms_and_privacy";

    // ---------------------------------------------------------------------------------------------

    private static final String PREF_IS_SEARCH = "is_search";
    private static final String PREF_SEARCH_QUERY = "search_query";
    private static final String PREF_IS_YOUTUBE_SEARCHING = "is_youtube_searching";
    private static final String PREF_YOUTUBE_NEXTPAGETOKEN = "youtube_next_page_token";
    private static final String PREF_IS_CATEGORY_REFRESHED = "is_category_refreshed_";
    private static final String PREF_CURRENT_LIST_POSITION = "current_list_position";
    private static final String PREF_SCROLLED_POSITION = "scrolled_position";
    private static final String PREF_IS_YOUTUBE_TYPES_UPDATED = "is_youtube_types_updated";
    private static final String PREF_NAV_DRAWER_SELECTED_ITEM = "nav_drawer_selected_item";
    private static final String PREF_APP_RATER_DATE_FIRST_LAUNCH = "app_rater_date_first_launch";
    private static final String PREF_IS_DISABLE_APP_RATER = "is_disable_app_rater";
    private static final String PREF_APP_RATER_LAUNCH_COUNT = "app_rater_launch_count";
    private static final String PREF_POPUP_APP_RATER = "is_popup_app_rater";
    private static final String PREF_USE_VOLUME_KEYS = "use_volume_keys";
    private static final String PREF_HARDWARE_ACCELERATION = "hardware_acceleration";
    // private static final String PREF_IS_NOTIFY = "is_notify";


    private PrefUtils() {
        // No instances
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void globalRefresh(Context context) {
        setGlobalRefresh(context, !PrefUtils.isGlobalRefresh(context));
    }

    /**
     * Workaround: (used in OnSharedPreferenceChangeListener)
     * Any listener that listens to this key change, will do some necessary refresh
     */
    private static boolean isGlobalRefresh(Context context) {
        try {
            return getSharedPreferences(context).getBoolean(PREF_GLOBAL_REFRESH, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Workaround: (used in OnSharedPreferenceChangeListener)
     * Any listener that listens to this key change, will do some necessary refresh
     */
    private static void setGlobalRefresh(Context context, boolean value) {
        try {
            getSharedPreferences(context).edit().putBoolean(PREF_GLOBAL_REFRESH, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isPopupAppRater(Context context) {
        try {
            return getSharedPreferences(context).getBoolean(PREF_POPUP_APP_RATER, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void setIsPopupAppRater(Context context, boolean isPopupAppRater) {
        try {
            getSharedPreferences(context).edit().putBoolean(PREF_POPUP_APP_RATER, isPopupAppRater).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isDisableAppRater(Context context) {
        try {
            return getSharedPreferences(context).getBoolean(PREF_IS_DISABLE_APP_RATER, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void setIsDisableAppRater(Context context, boolean disable) {
        try {
            getSharedPreferences(context).edit().putBoolean(PREF_IS_DISABLE_APP_RATER, disable).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getAppRaterLaunchCount(Context context) {
        try {
            return getSharedPreferences(context).getLong(PREF_APP_RATER_LAUNCH_COUNT, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setAppRaterLaunchCount(Context context, long count) {
        try {
            getSharedPreferences(context).edit().putLong(PREF_APP_RATER_LAUNCH_COUNT, count).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getAppRaterDateFirstLaunch(Context context) {
        try {
            return getSharedPreferences(context).getLong(PREF_APP_RATER_DATE_FIRST_LAUNCH, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setAppRaterDateFirstLaunch(Context context, long dateMillis) {
        try {
            getSharedPreferences(context).edit().putLong(PREF_APP_RATER_DATE_FIRST_LAUNCH, dateMillis).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isYoutubeTermsAccepted(Context context) {
        try {
            return getSharedPreferences(context).getBoolean(PREF_YOUTUBE_TERMS_AND_PRIVACY, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void setAcceptYoutubeTerms(Context context, boolean value) {
        try {
            getSharedPreferences(context).edit().putBoolean(PREF_YOUTUBE_TERMS_AND_PRIVACY, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getCurrentListPosition(Context context, int id) {
        try {
            return getSharedPreferences(context).getInt(PREF_CURRENT_LIST_POSITION + id, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public synchronized static void setCurrentListPosition(Context context, int id, int position) {
        try {
            getSharedPreferences(context).edit().putInt(PREF_CURRENT_LIST_POSITION + id, position).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getScrolledPosition(Context context, int id) {
        try {
            return getSharedPreferences(context).getInt(PREF_SCROLLED_POSITION + id, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public synchronized static void setScrolledPosition(Context context, int id, int position) {
        try {
            getSharedPreferences(context).edit().putInt(PREF_SCROLLED_POSITION + id, position).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getNavDrawerSelectedItem(Context context) {
        try {
            return getSharedPreferences(context).getInt(PREF_NAV_DRAWER_SELECTED_ITEM, R.id.nav_all_items);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.id.nav_all_items;
    }

    public static void setNavDrawerSelectedItem(Context context, int itemId) {
        try {
            getSharedPreferences(context).edit().putInt(PREF_NAV_DRAWER_SELECTED_ITEM, itemId).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAppTheme(Context context) {
        try {
            return getSharedPreferences(context).getString(PREF_APP_THEME, APP_THEME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return APP_THEME;
    }

    public static void setAppTheme(Context context, Theme theme) {
        try {
            getSharedPreferences(context).edit().putString(PREF_APP_THEME, theme.name()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isCategoryRefreshed(Context context, int categoryId) {
        try {
            return getSharedPreferences(context)
                    .getBoolean(PREF_IS_CATEGORY_REFRESHED + categoryId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void setIsCategoryRefreshed(Context context, int categoryId, boolean value) {
        try {
            getSharedPreferences(context).edit()
                    .putBoolean(PREF_IS_CATEGORY_REFRESHED + categoryId, value)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isYoutubeTypesUpdated(Context context) {
        try {
            return getSharedPreferences(context).getBoolean(PREF_IS_YOUTUBE_TYPES_UPDATED, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setYoutubeTypesUpdated(Context context, boolean value) {
        try {
            getSharedPreferences(context).edit().putBoolean(PREF_IS_YOUTUBE_TYPES_UPDATED, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isToolbarClicked(Context context) {
        try {
            return getSharedPreferences(context).getBoolean(PREF_TOOLBAR_CLICKED, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setToolbarClicked(Context context, boolean value) {
        try {
            getSharedPreferences(context).edit().putBoolean(PREF_TOOLBAR_CLICKED, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isSearch(Context context) {
        try {
            return getSharedPreferences(context)
                    .getBoolean(PREF_IS_SEARCH, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setIsSearch(Context context, boolean value) {
        try {
            getSharedPreferences(context).edit()
                    .putBoolean(PREF_IS_SEARCH, value)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSearchQuery(Context context) {
        try {
            return getSharedPreferences(context)
                    .getString(PREF_SEARCH_QUERY, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setSearchQuery(Context context, String value) {
        try {
            getSharedPreferences(context).edit()
                    .putString(PREF_SEARCH_QUERY, value)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isYoutubeSearching(Context context, int typeId) {
        try {
            return getSharedPreferences(context)
                    .getBoolean(PREF_IS_YOUTUBE_SEARCHING + typeId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setIsYoutubeSearching(Context context, int typeId, boolean value) {
        try {
            getSharedPreferences(context).edit()
                    .putBoolean(PREF_IS_YOUTUBE_SEARCHING + typeId, value)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isYoutubeSearching(Context context) {
        try {
            return getSharedPreferences(context).getBoolean(PREF_IS_YOUTUBE_SEARCHING, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setIsYoutubeSearching(Context context, boolean value) {
        try {
            getSharedPreferences(context).edit().putBoolean(PREF_IS_YOUTUBE_SEARCHING, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getYoutubeNextPageToken(Context context, int typeId) {
        try {
            return getSharedPreferences(context)
                    .getString(PREF_YOUTUBE_NEXTPAGETOKEN + typeId, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setYoutubeNextPageToken(Context context, int typeId, String value) {
        try {
            getSharedPreferences(context).edit()
                    .putString(PREF_YOUTUBE_NEXTPAGETOKEN + typeId, value)
                    .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getYoutubeNextPageToken(Context context) {
        try {
            return getSharedPreferences(context).getString(PREF_YOUTUBE_NEXTPAGETOKEN, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void setYoutubeNextPageToken(Context context, String value) {
        try {
            getSharedPreferences(context).edit().putString(PREF_YOUTUBE_NEXTPAGETOKEN, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isOldestFirst(Context context) {
        try {
            return getSharedPreferences(context).getBoolean(PREF_SORT_BY_DATE, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setIsOldestFirst(Context context, boolean value) {
        try {
            getSharedPreferences(context).edit().putBoolean(PREF_SORT_BY_DATE, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean useVolumeKeysToNavigate(Context context) {
        try {
            return getSharedPreferences(context).getBoolean(PREF_USE_VOLUME_KEYS, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getLayoutType(Context context) {
        try {
            return getSharedPreferences(context).getString(PREF_LAYOUT_TYPE, DEFAULT_LAYOUT_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_LAYOUT_TYPE;
    }

    public static void setLayoutType(Context context, String value) {
        try {
            getSharedPreferences(context).edit().putString(PREF_LAYOUT_TYPE, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getViewMode(Context context) {
        try {
            return getSharedPreferences(context).getString(PREF_VIEW_MODE, ALL_ENTRIES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ALL_ENTRIES;
    }

    public static void setViewMode(Context context, String value) {
        try {
            getSharedPreferences(context).edit().putString(PREF_VIEW_MODE, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getItemsStorageLimit(Context context) {
        try {
            return getSharedPreferences(context).getString(PREF_ITEMS_STORAGE_LIMIT, "500");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "500";
    }

    public static String getEntryDetailFontSize(Context context) {
        try {
            return getSharedPreferences(context).getString(PREF_ENTRY_DETAIL_FONT_SIZE, "3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "3";
    }

    public static void setEntryDetailFontSize(Context context, String value) {
        try {
            getSharedPreferences(context).edit().putString(PREF_ENTRY_DETAIL_FONT_SIZE, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFeedsLanguageCode(Context context) {
        try {
            return getSharedPreferences(context).getString(PREF_FEEDS_LANGUAGE_CODE, FEEDS_LANGUAGE_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FEEDS_LANGUAGE_CODE;
    }

    public static void setFeedsLanguageCode(Context context, String value) {
        try {
            getSharedPreferences(context).edit().putString(PREF_FEEDS_LANGUAGE_CODE, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getDefaultFeedsVersion(Context context) {
        try {
            return getSharedPreferences(context).getInt(PREF_DEFAULT_FEEDS_VERSION, DEFAULT_FEEDS_VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_FEEDS_VERSION;
    }

    public static void setDefaultFeedsVersion(Context context, int version) {
        try {
            getSharedPreferences(context).edit().putInt(PREF_DEFAULT_FEEDS_VERSION, version).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getYouTubePlayerOption(Context context) {
        try {
            return getSharedPreferences(context).getString(PREF_YOUTUBE_PLAYER_OPTIONS, "0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static void setYouTubePlayerOption(Context context, String value) {
        try {
            getSharedPreferences(context).edit().putString(PREF_YOUTUBE_PLAYER_OPTIONS, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isHardwareAccelerated(Context context) {
        try {
            return getSharedPreferences(context).getBoolean(PREF_HARDWARE_ACCELERATION, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void registerOnSharedPreferenceChangeListener(
            Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        try {
            getSharedPreferences(context).registerOnSharedPreferenceChangeListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static void removePrefKey(Context context, String key) {
        try {
            getSharedPreferences(context).edit().remove(key).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*public static void unregisterOnSharedPreferenceChangeListener(
            Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        try {
            getSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*private static String getString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }

    private static void putString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    private static int getInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }

    private static void putInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }

    private static long getLong(String key, long defValue) {
        return getSharedPreferences().getLong(key, defValue);
    }

    private static void putLong(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).apply();
    }

    private static boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }

    private static void putBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }*/
}
