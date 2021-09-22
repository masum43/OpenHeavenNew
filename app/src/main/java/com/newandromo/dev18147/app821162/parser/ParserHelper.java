package com.newandromo.dev18147.app821162.parser;

import android.text.TextUtils;
import android.webkit.URLUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.newandromo.dev18147.app821162.BuildConfig;
import com.newandromo.dev18147.app821162.utils.AppUtils;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.EMPTY_STRING;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.HTTP;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.HTTPS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.IS_ENABLE_OPEN_IN_BROWSER;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.PATTERN_YOUTUBE_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_IMG;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_LINK;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.URL_PATTERN;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.YOUTUBE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.YOUTUBE_PATTERN;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.YOUTUBE_PATTERN_EXTRA;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.YOUTUBE_SHORT;

public class ParserHelper {
    /*private static final String[] IMG_SRC_CONTAINS = {"file:///", "smilies", "feedburner", "feedsportal", "ad20.net",
            "/comments/", "stats.wordpress", "/emoji/"};*/
    private static final String BASE_PATH = "file:///android_asset/";
    private static final String[] FRAME_SRC_CONTAINS = {"video", "tv", "apple", "google", "ustream", "wsj", "vevo", "bloomberg",
            "vimeo", "vine", "worldstarhiphop", "embed.reembed.com", "icon.gif"};
    private static final String[] IMG_SRC_CONTAINS = {"file:///", "smilies", "feedburner", "feedsportal", "note_small_", "ad20.net",
            "/comments/", "stats.wordpress", "/emoji/", "flattr-badge", "photobucket", "ads.gif"};
    private static final String[] IMG_SRC_ENDS_WITH = {"_icon_large.png", "gplus-16.png", "_small_25.png", "mf.gif", "unilag-ad.gif",
            "headline_bullet.jpg", "_collapsed.gif", ".xml", "glossify/info.png", "share-email.png", "pdf.png", "print.gif",
            "srnathan_0.jpg", "FBLOGO_0.png", "logo_huff.png", "TWITTERBIRD.png", "instagram-logo.png", "/original.jpg", "ads.gif"};
    private static final String TAG_SRC = "src";
    private static final String TAG_CLASS = "class";
    private static final String TAG_SCRIPT = "script";
    private static final String TAG_AUDIO = "audio";
    private static final String TAG_VIDEO = "video";
    private static final String TAG_IFRAME = "iframe";
    private static final String TAG_EMBED = "embed";
    private static final String TAG_OBJECT = "object";
    private static final String TAG_DATA = "data";
    private static final String TAG_STYLE = "style";
    private static final String TAG_VIDEOSERIES = "videoseries";
    private static final String TAG_ALLOWFULLSCREEN = "allowfullscreen";
    private static final String TAG_WEBKITALLOWFULLSCREEN = "webkitallowfullscreen";
    private static final String TAG_MOZALLOWFULLSCREEN = "mozallowfullscreen";
    private static final String TAG_VINE = "vine";
    private static final String TAG_MEDIA_THUMBNAIL = "media:thumbnail";
    private static final String TAG_M_THUMBNAIL = "media|thumbnail";
    private static final String TAG_THUMBNAIL = "thumbnail";
    private static final String TAG_M_CONTENT = "media|content";
    private static final String TAG_MEDIA_CONTENT = "media:content";
    private static final String TAG_ENCLOSURE = "enclosure";
    private static final String TAG_ENCLOSURE_AUDIO = "enclosure[type*=audio]";
    private static final String TAG_ENCLOSURE_VIDEO = "enclosure[type*=video]";
    private static final String TAG_MEDIA_T_AUDIO = "media|content[type*=audio]";
    private static final String TAG_MEDIA_M_AUDIO = "media|content[medium*=audio]";
    private static final String TAG_MEDIA_T_VIDEO = "media|content[type*=video]";
    private static final String TAG_MEDIA_M_VIDEO = "media|content[medium*=video]";
    private static final String TAG_MEDIUM = "medium";
    private static final String TAG_TYPE = "type";
    private static final String TAG_IMAGE_BIG = "image_big";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_IMAGE_URL = "imageurl";
    private static final String TAG_HREF = "href";
    private static final String TAG_AVATAR = "avatar";
    private static final String TAG_WIDTH = "width";
    private static final String TAG_HEIGHT = "height";
    private static final String TAG_NAME = "name";
    private static final String TAG_W = "w=";
    private static final String TAG_QM = "?";
    private static final String JPG = ".jpg";
    private static final String JPEG = ".jpeg";
    private static final String PNG = ".png";
    private static final String GIF = ".gif";

    // constructor
    private ParserHelper() {
        // No instances
    }

    /**
     * Create HTML docType string
     *
     * @param link      entry link
     * @param title     entry title
     * @param subHeader feedTitle | author | date
     * @param content   entry content
     * @return HTML string
     */
    public static String createDocHtml(String title,
                                       String subHeader,
                                       String link,
                                       String content,
                                       String cssOverride, String visitWeb, String comments) {

        if (!IS_ENABLE_OPEN_IN_BROWSER) link = "#";
        String style = "@font-face{font-family:'Lato Bold';src:url('file:///android_asset/font/lato_bold.ttf')}" +
                "@font-face{font-family:'Roboto Regular';src:url('file:///android_asset/font/roboto_regular.ttf')}";
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
                "<div class=\"header_x\"><a href=\"" + link + "\">" + title + "</a></div>" +
                "<div class=\"subheader_x\">" + subHeader + "</div>" +
                "<div class=\"content_x\">" + content + "</div>" +
                "<div style=\"clear:both;\"></div>" +
                "<div id=\"visitWeb\">" +
                "<a href=\"" + link + "\" class=\"buttonVisitWeb\">" + visitWeb + "</a>" +
                "<a href=\"comments://\" class=\"buttonComments\">" + comments + "</a>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * This is the method to parse docHTML and do some modifications like
     * YoutubeVideo iframe replacement, img centering etc.
     *
     * @param body element to parse
     */
    public static Element parseDocHTML(Element body) {
        Elements elements = body.select("*");
        if (!elements.isEmpty()) {
            for (Element el : elements) {
                try {
                    try {
                        if (el.hasAttr(TAG_STYLE)) {
                            el.removeAttr(TAG_STYLE);
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        if (el.hasAttr(TAG_SRC)) {
                            String src = el.attr(TAG_SRC);
                            if (src.startsWith("//www") || src.startsWith("//")) {
                                src = "http:" + src;
                                el.attr(TAG_SRC, src);
                            } else if (src.startsWith("www")) {
                                src = "http://" + src;
                                el.attr(TAG_SRC, src);
                            }
                        }
                    } catch (Exception e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    if (el.tagName().equals(TAG_IFRAME)
                            || el.tagName().equals(TAG_OBJECT)
                            || el.tagName().equals(TAG_EMBED)) {
                        parseMedia(el);
                    } else if (el.tagName().equals(TAG_VIDEO)) {
                        parseVideoTag(el);
                    } else if (el.tagName().equals("biembeddedobject")) {
                        parseBiEmbeddedObject(el);
                    } else if (el.tagName().equals("table")) {
                        el.attr("width", "100%");
                    } else if (el.tagName().equals("tbody")) {
                        Elements tds = el.select("tr > td");
                        for (Element td : tds) {
                            Elements fonts = td.select("font");
                            for (Element font : fonts) {
                                font.removeAttr("color");
                            }
                        }
                    } else if (el.tagName().equals(TAG_SCRIPT)) {
                        el.remove();
                    } else if (el.tagName().equals(TAG_IMG) && el.hasAttr(TAG_SRC)) {
                        String src = el.attr(TAG_SRC);
                        if (AppUtils.stringContainsItemFromList(src, IMG_SRC_CONTAINS)) {
                            el.remove();
                        }
                    } else if (el.className().contains("wpInsertInPostAd")) {
                        el.remove();
                    } else if (el.ownText().contains("The post appeared first on")) {
                        el.remove();
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return body;
    }

    private static void parseMedia(Element element) {
        try {
            boolean youtubeMatch = false;
            String src = element.attr(TAG_SRC);

            if (element.tagName().equals(TAG_OBJECT)) {
                src = element.attr(TAG_DATA);
            }

            try {
                Pattern pattern = Pattern.compile(YOUTUBE_PATTERN, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(src);
                youtubeMatch = matcher.matches();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // check if URL matches YoutubeVideo URL pattern
            if (youtubeMatch || src.contains(YOUTUBE) || src.contains(YOUTUBE_SHORT)) {
                String videoId = getYoutubeVideoId(src);
                if (videoId.trim().length() == 11 && !videoId.contains(TAG_VIDEOSERIES)) {
                    element.after(insertYouTubeThumbnail(videoId));
                    element.wrap(wrapMedia());
                    element.before(insertOverlayImage(src));
                    element.after(insertOverlayButton(src));
                } else {
                    element.wrap(wrapMedia());
                    element.before(insertOverlayImage(src));
                    element.after(insertOverlayButton(src));
                }
            } else if (element.hasAttr(TAG_ALLOWFULLSCREEN)
                    || element.hasAttr(TAG_WEBKITALLOWFULLSCREEN)
                    || element.hasAttr(TAG_MOZALLOWFULLSCREEN)
                    || element.attr("id").contains(TAG_VIDEO)
                    || AppUtils.stringContainsItemFromList(src, FRAME_SRC_CONTAINS)
                    || elementAttributesHaveValue(element)) {

                element.wrap(wrapMedia());
                element.before(insertOverlayImage(src));
                element.after(insertOverlayButton(src));
            } else {
                element.wrap(wrapUnknownElement());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean elementAttributesHaveValue(Element element) {
        try {
            for (Attribute attribute : element.attributes()) {
                String attrValue = attribute.getValue();
                if (attrValue.equalsIgnoreCase(TAG_ALLOWFULLSCREEN) ||
                        attrValue.equalsIgnoreCase(TAG_WEBKITALLOWFULLSCREEN) ||
                        attrValue.equalsIgnoreCase(TAG_MOZALLOWFULLSCREEN)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void parseVideoTag(Element video) {
        try {
            Document videoInner = Jsoup.parse(video.outerHtml());
            String src1 = videoInner.select("source").attr(TAG_SRC);
            String src2 = videoInner.select("a").attr("abs:href");

            if (src1.trim().length() > 0) {
                video.wrap(podCastDiv(src1, TAG_VIDEO));
                video.remove();
            } else if (src2.trim().length() > 0) {
                video.wrap(podCastDiv(src2, TAG_VIDEO));
                video.remove();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseBiEmbeddedObject(Element el) {
        try {
            List<TextNode> textNodes = el.textNodes();
            if (textNodes != null && !textNodes.isEmpty()) {
                for (TextNode textNode : textNodes)
                    try {
                        String text = textNode.text();
                        if (text.matches(URL_PATTERN)) {
                            if (text.contains(TAG_VINE)) {
                                textNode.wrap(wrapMedia());
                                textNode.before(insertOverlayImage(text));
                                textNode.after(insertOverlayButton(text));
                                textNode.wrap(addVineIframe(text));
                            } else if (text.contains(YOUTUBE) || text.contains(YOUTUBE_SHORT)) {
                                textNode.wrap(wrapMedia());
                                textNode.before(insertOverlayImage(text));
                                textNode.after(insertOverlayButton(text));
                                textNode.wrap(addYouTubeIframe(text));
                            } else {
                                textNode.wrap(addLink(text));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String addLink(String url) {
        return "<a href=\"" + url + "\"></a>";
    }

    private static String addYouTubeIframe(String url) {
        return "<iframe width=\"655\" height=\"368\" src=\"" + url
                + "\" frameborder=\"0\" allowfullscreen></iframe>";
    }

    private static String addVineIframe(String url) {
        return "<iframe width=\"600\" height=\"600\" src=\"" + url + "\" ></iframe>";
    }

    /*private static String wrapMedia(String url) {
        return "<div class='videoBox widescreen vimeo'>"
                + "<a href='" + url + "' target='_top'>"
                + " <img style='display:none;' src='" + BASE_PATH + "media.png'></a>"
                + " <a href='" + url + "' target='_top'>"
                + " <img id='overlayBtn' src='" + BASE_PATH + "media.png'></a>"
                + "</div>";
    }*/

    private static String wrapMedia() {
        return "<div class='videoBox widescreen vimeo'></div>";
    }

    private static String insertOverlayImage(String url) {
        return "<a href='" + url + "' target='_top'>"
                + "<img style='display:none;' src='" + BASE_PATH + "media.png'></a>";
    }

    private static String insertOverlayButton(String url) {
        return " <a href='" + url + "' target='_top'>"
                + "<img id='overlayBtn' src='" + BASE_PATH + "media.png'></a>";
    }

    private static String insertYouTubeThumbnail(String url) {
        return "<div class='thumbnail'>" +
                "<img style='display:none;' src='http://img.youtube.com/vi/" + url + "/0.jpg'></div>";
    }

    private static String wrapUnknownElement() {
        return "<div style='overflow:auto;'></div>";
    }

    /**
     * getYoutubeVideoId gets YoutubeVideo video ID from URL. If this fails it tries
     * also getYoutubeVideo2
     *
     * @param youtubeUrl YoutubeVideo video url
     * @return videoId
     */
    public static String getYoutubeVideoId(String youtubeUrl) {
        try {
            Pattern pattern = Pattern.compile(PATTERN_YOUTUBE_URL, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(youtubeUrl);
            if (matcher.matches()) {
                String groupIndex = matcher.group(7);
                if (groupIndex != null && groupIndex.trim().length() == 11) {
                    return groupIndex.trim();
                } else {
                    return getYoutubeVideoId2(youtubeUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY_STRING;
    }

    private static String getYoutubeVideoId2(String youtubeUrl) {
        try {
            Pattern pattern = Pattern.compile(YOUTUBE_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(youtubeUrl);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(1);
                if (groupIndex1 != null && groupIndex1.trim().length() == 11) {
                    return groupIndex1.trim();
                } else {
                    return getYoutubeVideoId3(youtubeUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY_STRING;
    }

    private static String getYoutubeVideoId3(String youtubeUrl) {
        try {
            Pattern pattern = Pattern.compile(YOUTUBE_PATTERN_EXTRA, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(youtubeUrl);
            if (matcher.find()) {
                String groupIndex1 = matcher.group(1);
                if (groupIndex1 != null && groupIndex1.trim().length() == 11)
                    return groupIndex1.trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY_STRING;
    }

    // **************************************************************
    // TODO ********************** podCasts HANDLING *****************
    public static String getPodCasts(Element element) {
        String podCasts = "";
        // PodCast Audio
        try {
            String audio = findPodCasts(element, TAG_AUDIO);
            if (audio != null && audio.trim().length() > 0) {
                podCasts += audio;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // PodCast Videos
        try {
            String video = findPodCasts(element, TAG_VIDEO);
            if (video != null && video.trim().length() > 0) {
                podCasts += video;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return podCasts;
    }

    private static String findPodCasts(Element element, String type) {
        String podCasts = "";
        try {
            if (type.equals(TAG_AUDIO)) {
                Elements elements = element.select(TAG_ENCLOSURE_AUDIO);
                if (elements.isEmpty()) {
                    elements = element.select(TAG_MEDIA_T_AUDIO);
                    if (elements.isEmpty()) {
                        elements = element.select(TAG_MEDIA_M_AUDIO);
                        if (elements.isEmpty()) {
                            elements = element.select("enclosure[type*=mp3]");
                            if (elements.isEmpty()) {
                                elements = element.select("enclosure[url~=(?i)\\.(mp3)]");
                                if (elements.isEmpty()) {
                                    elements = element.select("media|content[url~=(?i)\\.(mp3)]");
                                }
                            }
                        }
                    }
                }

                if (!elements.isEmpty()) {
                    podCasts = getPodCastsForLoop(elements, TAG_AUDIO);
                }

            } else if (type.equals(TAG_VIDEO)) {

                Elements elements = element.select(TAG_ENCLOSURE_VIDEO);
                if (elements.isEmpty()) {
                    elements = element.select(TAG_MEDIA_T_VIDEO);
                    if (elements.isEmpty()) {
                        elements = element.select(TAG_MEDIA_M_VIDEO);
                        if (elements.isEmpty()) {
                            elements = element.select("enclosure[type*=mp4]");
                            if (elements.isEmpty()) {
                                elements = element.select("enclosure[url~=(?i)\\.(mp4)]");
                                if (elements.isEmpty()) {
                                    elements = element.select("media|content[url~=(?i)\\.(mp4)]");
                                }
                            }
                        }
                    }
                }

                if (!elements.isEmpty()) {
                    podCasts = getPodCastsForLoop(elements, TAG_VIDEO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return podCasts;
    }

    private static String getPodCastsForLoop(Elements elements, String type) {
        StringBuilder podCasts = new StringBuilder();
        try {
            for (Element el : elements) {
                if (el != null && el.attr(TAG_URL).trim().length() > 0) {
                    String url = el.attr(TAG_URL);
                    podCasts.append(podCastDiv(url, type));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return podCasts.toString();
    }

    private static String podCastDiv(String url, String type) {
        String name = url;
        try {
            name = URLUtil.guessFileName(url, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "<div class='podCast_x'>"
                + "<div class='podIcon_x " + type + "PodCast'></div>"
                + "<div class='podName_x'>"
                + "<a style='display:block;' href='" + url + "'>" + name + "</a></div>"
                + "</div>";
    }

    // **************************************************************
    // TODO ***************** THUMB IMAGE HANDLING ******************
    public static String imageTag(String src) {
        return "<p><img src='" + src + "' alt=''></p>";
    }

    public static String imageParser(Elements images, String baseUri) {
        String imgSrc = "";
        try {
            for (Element img : images) {
                try {
                    try {
                        if (!TextUtils.isEmpty(img.attr(TAG_WIDTH))) {
                            int width = Integer.parseInt(img.attr(TAG_WIDTH));
                            if (width < 100) continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (!TextUtils.isEmpty(img.attr(TAG_HEIGHT))) {
                            int height = Integer.parseInt(img.attr(TAG_HEIGHT));
                            if (height < 100) continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!AppUtils.stringEndsWithItemFromList(img.attr(TAG_SRC), IMG_SRC_ENDS_WITH)
                            && !AppUtils.stringContainsItemFromList(img.attr(TAG_SRC), IMG_SRC_CONTAINS)
                            && !img.attr(TAG_CLASS).contains("wpml_ico")
                            && !img.attr(TAG_CLASS).contains("attachment-medium")
                            && !img.parent().attr(TAG_NAME).contains("share")
                            && !img.parent().attr(TAG_HREF).contains("com/share")) {
                        imgSrc = img.attr(TAG_SRC);
                        break;
                    } else if (images.size() == 1 && (images.attr(TAG_CLASS).contains("attachment-medium"))) {
                        // Try again in this condition: this code allows images with class
                        // that contains "attachment-medium"
                        imgSrc = img.attr(TAG_SRC);
                        break;
                    } else if (images.size() == 3 && images.is("[src*=file:///]") &&
                            (images.attr(TAG_CLASS).contains("attachment-medium"))) {
                        // Try again in this condition: this code allows images with class
                        // that contains "attachment-medium"
                        imgSrc = images.attr(TAG_SRC);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (!TextUtils.isEmpty(imgSrc)
                    && (!imgSrc.startsWith(HTTP))
                    && (!imgSrc.startsWith(HTTPS))) {

                if (imgSrc.startsWith("//")) {
                    imgSrc = "http:" + imgSrc;
                } else {
                    try {
                        URL url = new URL(baseUri);
                        String baseUrl = url.getProtocol() + "://" + url.getHost();
                        if (imgSrc.startsWith("/")) {
                            imgSrc = baseUrl + imgSrc;
                        } else {
                            imgSrc = baseUrl + "/" + imgSrc;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!TextUtils.isEmpty(imgSrc) && imgSrc.contains("width=")) {
                try {
                    String[] separated = imgSrc.split("width=");
                    String str1 = separated[0];

                    if (str1.endsWith(TAG_QM)) {
                        imgSrc = str1 + "width=145";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgSrc;
    }

    public static String getImageFromRssAtomXml(Element element, String baseUri) {
        String imgSrc = "";
        try {
            Elements elements = element.select(TAG_M_THUMBNAIL);
            if (elements.isEmpty()) {
                elements = element.select(TAG_M_CONTENT);
                if (elements.isEmpty()) {
                    elements = element.select(TAG_THUMBNAIL);
                    if (elements.isEmpty()) {
                        elements = element.select(TAG_ENCLOSURE);
                        if (elements.isEmpty()) {
                            elements = element.select("link[type*=image]");
                            if (elements.isEmpty()) {
                                elements = element.select(TAG_IMAGE).select(TAG_URL);
                                if (elements.isEmpty()) {
                                    elements = element.select(TAG_IMAGE_BIG);
                                    if (elements.isEmpty()) {
                                        elements = element.select(TAG_IMAGE);
                                        if (elements.isEmpty()) {
                                            elements = element.select(TAG_IMAGE_URL);
                                            if (elements.isEmpty()) {
                                                elements = element.select("mash|thumbnail");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!elements.isEmpty()) {
                imgSrc = getImgElementsForLoop(elements);
            }

            if (!TextUtils.isEmpty(imgSrc)
                    && (!imgSrc.startsWith(HTTP))
                    && (!imgSrc.startsWith(HTTPS))) {

                if (imgSrc.startsWith("//")) {
                    imgSrc = "http:" + imgSrc;
                } else {
                    try {
                        URL url = new URL(baseUri);
                        String baseUrl = url.getProtocol() + "://" + url.getHost();
                        if (imgSrc.startsWith("/")) {
                            imgSrc = baseUrl + imgSrc;
                        } else {
                            imgSrc = baseUrl + "/" + imgSrc;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgSrc;
    }

    private static String getImgElementsForLoop(Elements elements) {
        String url = "";
        for (Element el : elements) {
            switch (el.tagName()) {
                case TAG_MEDIA_THUMBNAIL:
                    if (el.hasAttr(TAG_WIDTH)) {
                        try {
                            if (!TextUtils.isEmpty(el.attr(TAG_WIDTH))) {
                                int width = Integer.parseInt(el.attr(TAG_WIDTH));
                                if (width >= 70) {
                                    url = el.attr(TAG_URL);
                                }
                            }
                        } catch (Exception e) {
                            url = el.attr(TAG_URL);
                        }
                    } else {
                        url = el.attr(TAG_URL);
                    }

                    break;
                case TAG_ENCLOSURE:
                    if (el.hasAttr(TAG_TYPE) && (el.attr(TAG_TYPE).contains(TAG_IMAGE) || el.attr(TAG_TYPE).contains(TAG_IMG))) {
                        url = el.attr(TAG_URL);
                    } else if (el.hasAttr(TAG_MEDIUM) && el.attr(TAG_MEDIUM).contains(TAG_IMAGE)) {
                        url = el.attr(TAG_URL);
                    } else if (el.attributes().size() == 0) {
                        try {
                            Elements images = el.getElementsByTag(TAG_IMG);
                            url = images.first() != null ? images.first().attr(TAG_SRC) : "";
                            if (TextUtils.isEmpty(url) && !TextUtils.isEmpty(el.html())) {
                                String u = el.html();
                                try {
                                    u = u.toLowerCase(Locale.getDefault());
                                } catch (Exception ignore) {
                                }
                                if (u.endsWith(JPG)
                                        || u.endsWith(JPEG)
                                        || u.endsWith(PNG)
                                        || u.endsWith(GIF)) {
                                    url = el.html();
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    } else {
                        String u = el.attr(TAG_URL);
                        try {
                            u = u.toLowerCase(Locale.getDefault());
                        } catch (Exception ignored) {
                        }
                        if (u.endsWith(JPG)
                                || u.endsWith(JPEG)
                                || u.endsWith(PNG)
                                || u.endsWith(GIF)) {
                            url = el.attr(TAG_URL);
                        }
                    }

                    break;
                case TAG_MEDIA_CONTENT:
                    if (el.hasAttr(TAG_MEDIUM) && el.attr(TAG_MEDIUM).contains(TAG_IMAGE)) {
                        url = el.attr(TAG_URL);
                    } else if (el.hasAttr(TAG_TYPE) && el.attr(TAG_TYPE).contains(TAG_IMAGE)) {
                        url = el.attr(TAG_URL);
                    } else {
                        String u = el.attr(TAG_URL);
                        try {
                            u = u.toLowerCase(Locale.getDefault());
                        } catch (Exception ignored) {
                        }
                        if (u.endsWith(JPG)
                                || u.endsWith(JPEG)
                                || u.endsWith(PNG)
                                || u.endsWith(GIF)) {
                            url = el.attr(TAG_URL);
                        }
                    }

                    break;
                case TAG_LINK:

                    url = el.absUrl(TAG_HREF);

                    break;
                case TAG_IMAGE_BIG:
                case TAG_IMAGE:
                case TAG_IMAGE_URL:
                    try {
                        String unescapedHtml = el.text();
                        url = Jsoup.parse(unescapedHtml).select(TAG_IMG).first().attr(TAG_SRC);
                    } catch (Exception ignored) {
                        try {
                            if (!TextUtils.isEmpty(el.html())) {
                                String u = el.html();
                                try {
                                    u = u.toLowerCase(Locale.getDefault());
                                } catch (Exception ignore) {
                                }
                                if (u.endsWith(JPG)
                                        || u.endsWith(JPEG)
                                        || u.endsWith(PNG)
                                        || u.endsWith(GIF)) {
                                    url = el.html();
                                }
                            }
                        } catch (Exception ignore) {
                        }
                    }

                    break;
                case TAG_THUMBNAIL:
                case TAG_URL:
                    url = el.text();
                    if (url.trim().length() == 0) {
                        url = el.attr(TAG_URL);
                    }

                    break;
                case "mash:thumbnail":
                    Document doc = Jsoup.parse(el.text());
                    url = doc.select("img").attr(TAG_SRC);
                    break;
            }

            if ((url.trim().length() > 0) && (!url.contains(TAG_AVATAR))) {
                if (removeThumbWidth(url).trim().length() > 0) {
                    return removeThumbWidth(url);
                } else if (url.contains("huffpost") && url.contains("-mini.jpg")) {
                    url = url.replace("-mini.jpg", "-large.jpg");
                }
                return url;
            }
        }
        return "";
    }

    private static String removeThumbWidth(String url) {
        if (url.contains(TAG_W)) {
            String[] separated = url.split(TAG_W);
            url = separated[0];
            if (url.endsWith(TAG_QM)) {
                url = url.substring(0, url.length() - 1);
                return url;
            }
        }
        return "";
    }
}
