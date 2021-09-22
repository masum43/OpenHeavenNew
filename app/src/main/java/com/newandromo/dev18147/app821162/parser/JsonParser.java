package com.newandromo.dev18147.app821162.parser;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.newandromo.dev18147.app821162.RemoteConfig;
import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.MyDateUtils;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.HTTP;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.HTTPS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_AUTHOR;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_AUTHOR_NAME;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_CONTENT;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_DATE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_EMBEDDED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_IMAGE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_IMG;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_ITEMS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_LINK;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_MODIFIED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_POST_SLUG;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_PUBLISHED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_RENDERED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_UPDATED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_URL;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_WP_FEATURE_MEDIA;

public class JsonParser {
    private static final int EXCERPT_MAX_LENGTH = 156; // 256
    private static final int EXCERPT_MAX_QUERY_LENGTH = 2048;

    public JsonParser() {
    }

    public List<EntryEntity> getEntriesFromWordPressJson(int feedId, Document document) throws JSONException {
        JSONArray jsonArray = new JSONArray(document.body().text());

        return getEntriesFromWordPressJson(feedId, jsonArray);
    }

    public List<EntryEntity> getEntriesFromWordPressJson(int feedId, InputStream inputStream)
            throws JSONException, IOException {
        // String jsonString = IOUtils.toString(inputStream, Charset.forName(UTF8));
        String jsonString = AppUtils.getStringFromInputStream(inputStream);
        JSONArray jsonArray = new JSONArray(jsonString);
        return getEntriesFromWordPressJson(feedId, jsonArray);
    }

    private List<EntryEntity> getEntriesFromWordPressJson(int feedId, JSONArray jsonArray) {
        List<EntryEntity> entries = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject post = jsonArray.getJSONObject(i);

                // -----------------------------------------------------------------------------

                String postSlug = "";
                if (!post.isNull(TAG_POST_SLUG)) {
                    postSlug = post.getString(TAG_POST_SLUG);
                }

                // -----------------------------------------------------------------------------

                String title = "";
                if (!post.isNull(TAG_TITLE)) {
                    title = post.getJSONObject(TAG_TITLE).getString(TAG_RENDERED);
                }

                // -----------------------------------------------------------------------------

                String date = "";
                long dateMillis = 0;
                if (!post.isNull(TAG_DATE)) {
                    date = post.getString(TAG_DATE);
                }

                if (TextUtils.isEmpty(date) && !post.isNull(TAG_MODIFIED)) {
                    date = post.getString(TAG_MODIFIED);
                }

                if (!TextUtils.isEmpty(date)) {
                    dateMillis = MyDateUtils.parseTimestampToMillis(date, false);
                }

                // -----------------------------------------------------------------------------

                String url = "";
                if (!post.isNull(TAG_LINK)) {
                    url = post.getString(TAG_LINK);
                }

                if (TextUtils.isEmpty(url)) {
                    url = String.format(RemoteConfig.getSiteUrl() + "%s", postSlug);
                }

                if (!TextUtils.isEmpty(url)) {
                    if (url.startsWith(HTTPS)) {
                        url = HTTP + url.substring(HTTPS.length());
                    }
                }

                // -----------------------------------------------------------------------------

                String content = "";
                if (!post.isNull(TAG_CONTENT)) {
                    content = post.getJSONObject(TAG_CONTENT).getString(TAG_RENDERED);
                }

                Document contentDoc = Jsoup.parse(content, "", Parser.htmlParser());
                // Parse content and do some cleanup & modifications
                Element cleanBodyElement = ParserHelper.parseDocHTML(contentDoc.body());
                content = cleanBodyElement.html();

                // -----------------------------------------------------------------------------

                String author = "";
                String thumbUrl = "";
                String imageFile = "";

                if (!post.isNull(TAG_EMBEDDED)) {
                    JSONObject jObjEmbedded = post.getJSONObject(TAG_EMBEDDED);

                    if (!jObjEmbedded.isNull(TAG_AUTHOR)) {
                        JSONArray jArrayAuthor = jObjEmbedded.getJSONArray(TAG_AUTHOR);
                        if (jArrayAuthor.length() > 0) {
                            JSONObject jObjAuthor = jArrayAuthor.getJSONObject(0);
                            if (!jObjAuthor.isNull(TAG_AUTHOR_NAME)) {
                                author = jObjAuthor.getString(TAG_AUTHOR_NAME);
                            }
                        }
                    }

                    // -----------------------------------------------------------------------------

                    if (!jObjEmbedded.isNull(TAG_WP_FEATURE_MEDIA)) {
                        JSONArray jArrayFeatureMedia = jObjEmbedded.getJSONArray(TAG_WP_FEATURE_MEDIA);
                        if (jArrayFeatureMedia.length() > 0) {
                            JSONObject jObjFeatureMedia = jArrayFeatureMedia.getJSONObject(0);

                            if (!jObjFeatureMedia.isNull("media_type")) {
                                String mediaType = jObjFeatureMedia.getString("media_type");
                                if (mediaType.equalsIgnoreCase(TAG_IMAGE)) {

                                    if (!jObjFeatureMedia.isNull("media_details")) {
                                        JSONObject jObjMediaDetails = jObjFeatureMedia.getJSONObject("media_details");

                                        if (!jObjMediaDetails.isNull("file")) {
                                            String jStringFile = jObjMediaDetails.getString("file");
                                            if (!TextUtils.isEmpty(jStringFile)) {
                                                imageFile = jStringFile
                                                        .replaceAll(".jpg", "")
                                                        .replaceAll(".JPG", "")
                                                        .replaceAll(".PNG", "")
                                                        .replaceAll(".png", "")
                                                        .replaceAll(".gif", "")
                                                        .replaceAll(".GIF", "");
                                            }
                                        }

                                        if (!jObjMediaDetails.isNull("sizes")) {
                                            JSONObject jObjSizes = jObjMediaDetails.getJSONObject("sizes");

                                            if (!jObjSizes.isNull("medium_large")) {
                                                thumbUrl = jObjSizes.getJSONObject("medium_large").getString("source_url");
                                            } else if (!jObjSizes.isNull("full")) {
                                                thumbUrl = jObjSizes.getJSONObject("full").getString("source_url");
                                            } else if (!jObjSizes.isNull("large")) {
                                                thumbUrl = jObjSizes.getJSONObject("large").getString("source_url");
                                            } else if (!jObjSizes.isNull("medium-thumb")) {
                                                thumbUrl = jObjSizes.getJSONObject("medium-thumb").getString("source_url");
                                            } else if (!jObjSizes.isNull("medium")) {
                                                thumbUrl = jObjSizes.getJSONObject("medium").getString("source_url");
                                            } else if (!jObjSizes.isNull("post-thumbnail")) {
                                                thumbUrl = jObjSizes.getJSONObject("post-thumbnail").getString("source_url");
                                            }
                                        }

                                        if (TextUtils.isEmpty(thumbUrl) && !jObjFeatureMedia.isNull("source_url")) {
                                            thumbUrl = jObjFeatureMedia.getString("source_url");
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(thumbUrl) && thumbUrl.startsWith("//")) {
                        thumbUrl = "http:" + thumbUrl;
                    }

                    // Extract Images from content for thumbnail
                    Elements allImages = cleanBodyElement.select(TAG_IMG);

                    if (TextUtils.isEmpty(thumbUrl)) {
                        thumbUrl = ParserHelper.imageParser(allImages, url);
                    } else {
                        Elements tagImages = cleanBodyElement.select("img[src*=" + thumbUrl + "]");
                        if (tagImages.isEmpty() && !TextUtils.isEmpty(imageFile)) {
                            tagImages = cleanBodyElement.select("img[src*=" + imageFile + "]");
                        }

                        if (allImages.isEmpty() || tagImages.isEmpty()) {
                            content = ParserHelper.imageTag(thumbUrl) + content;
                        }
                    }
                }

                // -----------------------------------------------------------------------------

                if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(content)) {
                    EntryEntity entry = new EntryEntity(
                            feedId, title, author, date, dateMillis, url, thumbUrl,
                            content, extractExcerpt(content), 1, 0, 0);

                    entries.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entries;
    }

    public List<EntryEntity> getEntriesFromGoogleJson(int feedId, Document document) throws JSONException {
        JSONObject jsonObject = new JSONObject(document.body().text());
        return getEntriesFromGoogleJson(feedId, jsonObject);
    }

    public List<EntryEntity> getEntriesFromGoogleJson(int feedId, InputStream inputStream)
            throws JSONException, IOException {
        String jsonString = AppUtils.getStringFromInputStream(inputStream);
        // JSONObject jsonObject = new JSONObject(IOUtils.toString(inputStream, Charset.forName(UTF8)));
        JSONObject jsonObject = new JSONObject(jsonString);
        return getEntriesFromGoogleJson(feedId, jsonObject);
    }

    private List<EntryEntity> getEntriesFromGoogleJson(int feedId, JSONObject jsonObject) throws JSONException {
        List<EntryEntity> entries = new ArrayList<>();
        JSONArray items = jsonObject.getJSONArray(TAG_ITEMS);

        for (int i = 0; i < items.length(); i++) {
            try {
                JSONObject item = items.getJSONObject(i);
                // -----------------------------------------------------------------------------

                String title = "";
                if (!item.isNull(TAG_TITLE)) {
                    title = item.getString(TAG_TITLE);
                }

                // -----------------------------------------------------------------------------

                String date = "";
                long dateMillis = 0;
                if (!item.isNull(TAG_UPDATED)) {
                    date = item.getString(TAG_UPDATED);
                }

                if (TextUtils.isEmpty(date) && !item.isNull(TAG_PUBLISHED)) {
                    date = item.getString(TAG_PUBLISHED);
                }

                if (!TextUtils.isEmpty(date)) {
                    dateMillis = MyDateUtils.parseTimestampToMillis(date, false);
                }

                // -----------------------------------------------------------------------------

                String author = "";
                if (!item.isNull(TAG_AUTHOR)) {
                    JSONObject objectAuthor = item.getJSONObject(TAG_AUTHOR);
                    if (!objectAuthor.isNull("displayName"))
                        author = objectAuthor.getString("displayName");
                }

                // -----------------------------------------------------------------------------

                String url = "";
                if (!item.isNull(TAG_URL)) {
                    url = item.getString(TAG_URL);
                }

                if (!TextUtils.isEmpty(url)) {
                    if (url.startsWith(HTTPS)) {
                        url = HTTP + url.substring(HTTPS.length());
                    }
                }

                // -----------------------------------------------------------------------------

                String content = "";
                if (!item.isNull(TAG_CONTENT)) {
                    content = item.getString(TAG_CONTENT);
                }

                Document contentDoc = Jsoup.parse(content, "", Parser.htmlParser());

                // Parse content and do some cleanup & modifications
                Element cleanBodyElement = ParserHelper.parseDocHTML(contentDoc.body());
                content = cleanBodyElement.html();

                // -----------------------------------------------------------------------------

                // Extract Images from content for ThumbNail
                Elements images = cleanBodyElement.select(TAG_IMG);
                String thumbUrl = ParserHelper.imageParser(images, url);

                if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(content)) {
                    EntryEntity entry = new EntryEntity(
                            feedId, title, author, date, dateMillis, url, thumbUrl,
                            content, extractExcerpt(content), 1, 0, 0);

                    entries.add(entry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entries;
    }

    private String extractExcerpt(String content) {
        String excerpt = "";
        if (!TextUtils.isEmpty(content)) {
            try {
                String mTmp = content.length() > EXCERPT_MAX_QUERY_LENGTH ?
                        content.substring(0, EXCERPT_MAX_QUERY_LENGTH) : content;

                excerpt = Jsoup.parse(mTmp, "", Parser.htmlParser()).text();

                if (excerpt.length() > EXCERPT_MAX_LENGTH)
                    excerpt = excerpt.substring(0, EXCERPT_MAX_LENGTH) + "…";
            } catch (Exception ignored) {
            }
        }
        return excerpt;
    }
}
