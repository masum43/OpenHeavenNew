package com.newandromo.dev18147.app821162.utils;

import com.newandromo.dev18147.app821162.ui.enums.Theme;

public final class Constants {

    private Constants() {
    }

    public static class Const {
        static final String APP_THEME = Theme.LIGHT.name();
        static final String DEFAULT_LAYOUT_TYPE = "layout_type_list";

        static final String FEEDS_LANGUAGE_CODE = "sw";
        static final int DEFAULT_FEEDS_VERSION = 1;

        public static final boolean IS_SHOW_DESCRIPTION = true;
        public static final boolean IS_LAYOUT_TYPE_LIST_COMPACT_TIGHT = false;
        public static final boolean IS_LAYOUT_TYPE_LIST_IMG_LEFT = false;

        public static final boolean IS_SHOW_AUTHOR = false;
        public static final boolean IS_CONTENT_CENTERED = false;
        public static final boolean IS_ENABLE_OPEN_IN_BROWSER = false;

        // *****************************************************************************************

        public static final String LAYOUT_TYPE_LIST = "layout_type_list";
        public static final String LAYOUT_TYPE_DOUBLE = "layout_type_double";
        public static final String LAYOUT_TYPE_LARGE = "layout_type_large";

        // *****************************************************************************************

        public static final String URL_PATTERN = "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";

        public static final String PATTERN_YOUTUBE_URL = "^.*((youtu.be\\/)"
                + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";

        public static final String YOUTUBE_PATTERN = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|"
                + "youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";

        public static final String YOUTUBE_PATTERN_EXTRA = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";

        public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

        // RSS/ATOM/XML/JSON document
        public static final String TAG_EMBEDDED = "_embedded";
        public static final String TAG_WP_FEATURE_MEDIA = "wp:featuredmedia";
        public static final String TAG_FEED = "feed";
        public static final String TAG_TITLE = "title";
        public static final String TAG_LINK = "link";
        public static final String TAG_AUTHOR = "author";
        public static final String TAG_POST_SLUG = "slug";
        public static final String TAG_GUID = "guid";
        public static final String TAG_KIND = "kind";
        public static final String TAG_ID = "id";
        public static final String TAG_FEEDB_ORIGLINK = "feedburner|origLink";
        public static final String TAG_CONTENT = "content";
        public static final String TAG_RENDERED = "rendered";
        public static final String TAG_CHANNEL = "channel";
        public static final String TAG_RDF = "rdf|rdf";
        public static final String TAG_ENTRY = "entry";
        public static final String TAG_ITEM = "item";
        public static final String TAG_DESCRIPTION = "description";
        public static final String TAG_CONTENT_ENCODED = "content|encoded";
        public static final String TAG_FULL_TEXT = "fulltext";
        public static final String TAG_DC_CREATOR = "dc|creator";
        public static final String TAG_LINK_ALTERNATE = "link[rel=alternate]";
        public static final String TAG_PUBDATE = "pubDate";
        public static final String TAG_DC_DATE = "dc|date";
        public static final String TAG_ATOM_UPDATED = "atom|updated";
        public static final String TAG_ATOM_PUBLISHED = "atom|published";
        public static final String TAG_UPDATED = "updated";
        public static final String TAG_A10_UPDATED = "a10|updated";
        public static final String TAG_DATE = "date";
        public static final String TAG_MODIFIED = "modified";
        public static final String TAG_PUBLISHED = "published";
        public static final String TAG_PUBLISHED_DATE = "publishedDate";
        public static final String TAG_AUTHOR_NAME = "name";
        public static final String TAG_SUMMARY = "summary";
        public static final String TAG_UNTITLED = "untitled";
        public static final String TAG_IMG = "img";
        public static final String TAG_IMAGE = "image";
        public static final String TAG_NEXTPAGETOKEN = "nextPageToken";
        public static final String TAG_ITEMS = "items";
        public static final String TAG_PLAYLIST = "playlist";
        public static final String TAG_SNIPPET = "snippet";
        public static final String TAG_CONTENT_DETAILS = "contentDetails";
        public static final String TAG_DURATION = "duration";
        public static final String TAG_STATISTICS = "statistics";
        public static final String TAG_VIEW_COUNT = "viewCount";
        public static final String TAG_CHANNEL_TITLE = "channelTitle";
        public static final String TAG_VIDEO_ID = "videoId";
        public static final String TAG_PUBLISHED_AT = "publishedAt";

        // *****************************************************************************************

        public static final String ALL_ENTRIES = "all_entries";
        public static final String UNREAD = "unread";

        public static final String EMPTY_STRING = "";
        public static final String YOUTUBE_PACKAGE_NAME = "com.google.android.youtube";
        public static final String VENDOR_YOUTUBE = "vnd.youtube:";
        public static final String YOUTUBE_SHORT = "youtu.be";
        public static final String YOUTUBE = "youtube";
        public static final String YOUTUBE_GET_VIDEO_INFO = "youtube.com/get_video_info";
        public static final String YOUTUBE_VIDEO_ID = "video_id=";
        //public static final String ENCODING = "encoding=\"";
        //public static final String ENCODING2 = "encoding='";
        public static final String HTTP = "http://";
        public static final String HTTPS = "https://";
        public static final String MIMETYPE = "text/html";
        public static final String TAG_URL = "url";
        public static final String ALLOWED_URI_CHARACTERS = "@=&*+-_.,:!?()/~'%";
        public static final String UTF8 = "UTF-8";
        public static final String ABOUT_BLANK = "about:blank";
        public static final String TAG_HREF = "href";
    }

    public static class AppUrls {
        public static final String APP_MARKET_URL_GOOGLE = "market://details?id=%s";
        public static final String MAIL_TO = "mailto:%s";
        public static final String TEL = "tel:%s";
        public static final String INSTAGRAM_URL = "instagram.com";
        public static final String TWITTER_URL = "twitter.com";
        public static final String YOUTUBE_URL = "youtube.com";
        public static final String YOUTUBE_URL_SHORT = "youtu.be";
        public static final String YOUTUBE_WATCH_URL = "http://www.youtube.com/watch?v=";
        public static final String VIMEO_URL = "vimeo.com";
        public static final String YOUTUBE_SHORT_URL = "https://youtu.be/%s";
        // public static final String GOOGLE_SHORT_URL = "http://goo.gl/";
        // public static final String BITLY_SHORT_URL = "http://bit.ly/";
    }

    public static class AppBundles {
        public static final String BUNDLE_ENTRY_DETAIL_TITLE = "entry_detail_title";
        public static final String BUNDLE_ENTRY_DETAIL_SUBTITLE = "entry_detail_sub_title";
        public static final String BUNDLE_ENTRY_ID = "entry_id";
        public static final String BUNDLE_ENTRY_IMAGE_URL = "image_url";
        public static final String BUNDLE_POSITION = "position";
        public static final String BUNDLE_SEARCH_QUERY = "search_query";
        public static final String BUNDLE_ID = "id";
        public static final String BUNDLE_IS_CATEGORY = "is_category";
        public static final String BUNDLE_TYPE_ID = "type_id";
        public static final String BUNDLE_TYPE_UNIQUE_ID = "unique_id";
        public static final String BUNDLE_IS_CHANNEL = "is_channel";
        public static final String BUNDLE_SHARE_SUBJECT = "shareSubject";
        public static final String BUNDLE_SHARE_BODY = "shareBody";
        public static final String BUNDLE_SHARE_IMAGE = "shareImage";
        public static final String BUNDLE_IS_GO_HOME = "is_go_home";
    }

    public static class RequestCodesAndIds {
        // EntryListAndDetail
        public static final int REQ_ENTRY_DETAIL_ACTIVITY = 101;

        // YouTube
        public static final int REQ_START_STANDALONE_PLAYER = 1000;
        public static final int REQ_START_EXTERNAL_PLAYER = 1500;
        public static final int REQ_RECOVERY_DIALOG = 2000;

        public static final int REQ_YOUTUBE_TYPE_MANAGER = 3000;

        // Permissions
        public static final int REQ_WRITE_EXTERNAL_STORAGE = 400;
    }

    public static class AppIntents {
        public static final String INTENT_EXTRA_POSITION = "position";
        public static final String INTENT_EXTRA_ID = "id";
        public static final String INTENT_EXTRA_IS_CATEGORY = "is_category";
        public static final String INTENT_EXTRA_ENTRY_ID = "entry_id";
        public static final String INTENT_EXTRA_ENTRY_TITLE = "entry_title";
        public static final String INTENT_EXTRA_ENTRY_IMAGE_URL = "entry_image_url";
        public static final String INTENT_EXTRA_ENTRY_URL = "entry_url";
        public static final String INTENT_EXTRA_IS_SEARCH = "is_search";
        public static final String INTENT_EXTRA_SEARCH_QUERY = "search_query";
        public static final String INTENT_EXTRA_UNIQUE_ID = "unique_id";
        public static final String INTENT_EXTRA_IS_CHANNEL = "is_channel";
        public static final String INTENT_EXTRA_IS_TYPE_VIDEO = "is_type_video";
        public static final String INTENT_EXTRA_IS_LOAD_MORE = "is_load_more";
        public static final String INTENT_EXTRA_IS_SINGLE_LAYOUT = "is_single_layout";
        public static final String INTENT_EXTRA_IS_GO_HOME = "is_go_home";
        public static final String INTENT_EXTRA_IS_REFRESH_ALL = "is_refresh_all";
        public static final String INTENT_EXTRA_IS_ONETIME_WORK = "is_onetime_work";
    }
}
