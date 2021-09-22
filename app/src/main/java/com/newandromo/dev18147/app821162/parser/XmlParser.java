package com.newandromo.dev18147.app821162.parser;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.newandromo.dev18147.app821162.db.entity.EntryEntity;
import com.newandromo.dev18147.app821162.utils.AppUtils;
import com.newandromo.dev18147.app821162.utils.MyDateUtils;

import static com.newandromo.dev18147.app821162.utils.Constants.Const.HTTP;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.HTTPS;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_A10_UPDATED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_ATOM_PUBLISHED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_ATOM_UPDATED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_AUTHOR;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_AUTHOR_NAME;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_CHANNEL;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_CONTENT;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_CONTENT_ENCODED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_DATE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_DC_CREATOR;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_DC_DATE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_DESCRIPTION;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_ENTRY;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_FEED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_FEEDB_ORIGLINK;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_FULL_TEXT;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_GUID;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_HREF;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_ID;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_IMG;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_ITEM;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_LINK;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_LINK_ALTERNATE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_PUBDATE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_PUBLISHED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_PUBLISHED_DATE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_RDF;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_SUMMARY;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_TITLE;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.TAG_UPDATED;
import static com.newandromo.dev18147.app821162.utils.Constants.Const.URL_PATTERN;

public class XmlParser {
    private static final int EXCERPT_MAX_LENGTH = 156; // 256
    private static final int EXCERPT_MAX_QUERY_LENGTH = 2048;
    private boolean mIsItemElement = true;

    public XmlParser() {
    }

    public List<EntryEntity> getEntriesFromXml(int feedId, Document document) {
        List<EntryEntity> entries = new ArrayList<>();
        Elements elements = getXmlInnerElements(document);

        for (Element parentElement : elements) {
            try {
                String title = getText(parentElement, TAG_TITLE);

                String author = getXmlAuthor(parentElement, mIsItemElement);

                String date = getXmlDate(parentElement);

                long dateMillis = 0;
                if (!TextUtils.isEmpty(date)) {
                    dateMillis = MyDateUtils.parseTimestampToMillis(date, false);
                }

                String url = getXmlLink(parentElement, mIsItemElement);

                String content = getXmlContent(parentElement, mIsItemElement);
                Document contentDoc = Jsoup.parse(content, "", Parser.htmlParser());

                // Parse content and do some cleanup & modifications
                Element cleanBodyElement = ParserHelper.parseDocHTML(contentDoc.body());
                content = cleanBodyElement.html();

                // Extract Images from content for ThumbNail
                Elements images = cleanBodyElement.select(TAG_IMG);
                String thumbUrl = ParserHelper.imageParser(images, url);

                if (TextUtils.isEmpty(thumbUrl)) {
                    try {
                        thumbUrl = ParserHelper.getImageFromRssAtomXml(parentElement, url);
                        if (!TextUtils.isEmpty(thumbUrl)) {
                            content = ParserHelper.imageTag(thumbUrl) + content;
                        }
                    } catch (Exception ignored) {
                    }
                }

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

    private Elements getXmlInnerElements(Document document) {
        // Document document = Jsoup.parse(doc.outerHtml(), "", Parser.xmlParser());
        Elements elements = document.select(TAG_CHANNEL).select(TAG_ITEM);
        mIsItemElement = true;

        if (elements.isEmpty()) {
            elements = document.select(TAG_FEED).select(TAG_ENTRY);
            if (elements.isEmpty()) {
                elements = document.select(TAG_RDF).select(TAG_ITEM);
                if (elements.isEmpty()) {
                    elements = document.select(TAG_FEED).select(TAG_ITEM);
                }
            } else mIsItemElement = false;
        }
        return elements;
    }

    public List<EntryEntity> getEntriesFromXml(int feedId, InputStream stream) throws IOException {
        List<EntryEntity> entries = new ArrayList<>();
        Elements elements = getXmlInnerElements(stream);

        for (Element parentElement : elements) {
            try {
                String title = getText(parentElement, TAG_TITLE);

                String author = getXmlAuthor(parentElement, mIsItemElement);

                String date = getXmlDate(parentElement);

                long dateMillis = 0;
                if (!TextUtils.isEmpty(date)) {
                    dateMillis = MyDateUtils.parseTimestampToMillis(date, false);
                }

                String url = getXmlLink(parentElement, mIsItemElement);

                String content = getXmlContent(parentElement, mIsItemElement);
                Document contentDoc = Jsoup.parse(content, "", Parser.htmlParser());

                // Parse content and do some cleanup & modifications
                Element cleanBodyElement = ParserHelper.parseDocHTML(contentDoc.body());
                content = cleanBodyElement.html();

                // Extract Images from content for ThumbNail
                Elements images = cleanBodyElement.select(TAG_IMG);
                String thumbUrl = ParserHelper.imageParser(images, url);

                if (TextUtils.isEmpty(thumbUrl)) {
                    try {
                        thumbUrl = ParserHelper.getImageFromRssAtomXml(parentElement, url);
                        if (!TextUtils.isEmpty(thumbUrl)) {
                            content = ParserHelper.imageTag(thumbUrl) + content;
                        }
                    } catch (Exception ignored) {
                    }
                }

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

    private Elements getXmlInnerElements(InputStream stream) throws IOException {
        String xml = AppUtils.getStringFromInputStream(stream);
        Document document = Jsoup.parse(xml, "", Parser.xmlParser());

        Elements elements = document.select(TAG_CHANNEL).select(TAG_ITEM);
        mIsItemElement = true;

        if (elements.isEmpty()) {
            elements = document.select(TAG_FEED).select(TAG_ENTRY);
            if (elements.isEmpty()) {
                elements = document.select(TAG_RDF).select(TAG_ITEM);
                if (elements.isEmpty()) {
                    elements = document.select(TAG_FEED).select(TAG_ITEM);
                }
            } else mIsItemElement = false;
        }
        return elements;
    }

    private String getXmlDate(Element parentElement) {
        String date = getText(parentElement, TAG_PUBDATE);
        if (TextUtils.isEmpty(date)) {
            date = getText(parentElement, TAG_DC_DATE);
            if (TextUtils.isEmpty(date)) {
                date = getText(parentElement, TAG_PUBLISHED);
                if (TextUtils.isEmpty(date)) {
                    date = getText(parentElement, TAG_UPDATED);
                    if (TextUtils.isEmpty(date)) {
                        date = getText(parentElement, TAG_ATOM_UPDATED);
                        if (TextUtils.isEmpty(date)) {
                            date = getText(parentElement, TAG_ATOM_PUBLISHED);
                            if (TextUtils.isEmpty(date)) {
                                date = getText(parentElement, TAG_A10_UPDATED);
                                if (TextUtils.isEmpty(date)) {
                                    date = getText(parentElement, TAG_DATE);
                                    if (TextUtils.isEmpty(date)) {
                                        date = getText(parentElement, TAG_PUBLISHED_DATE);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return date;
    }

    private String getXmlAuthor(Element parentElement, boolean isItemElement) {
        String author = "";
        if (isItemElement) {
            author = getText(parentElement, TAG_DC_CREATOR);
        } else {
            Element elementAuthor = parentElement.getElementsByTag(TAG_AUTHOR).first();
            if (elementAuthor != null) {
                Element name = elementAuthor.getElementsByTag(TAG_AUTHOR_NAME).first();
                if (name != null) {
                    author = name.text();
                }
            }
        }

        if (TextUtils.isEmpty(author)) {
            author = getText(parentElement, TAG_AUTHOR);
        }
        return author;
    }

    private String getXmlLink(Element parentElement, boolean isItemElement) {
        String link = "";
        if (isItemElement) {
            link = getText(parentElement, TAG_LINK);
        } else {
            Elements elementsLink = parentElement.select(TAG_LINK_ALTERNATE);
            if (elementsLink.isEmpty()) {
                elementsLink = parentElement.select("link[type=text/html]");
                if (elementsLink.isEmpty()) {
                    elementsLink = parentElement.select("link[" + TAG_HREF + "]");
                }
            }

            if (!elementsLink.isEmpty()) {
                for (Element eLink : elementsLink) {
                    if (eLink != null && eLink.parent().tagName().equals(parentElement.tagName())) {
                        link = eLink.attr(TAG_HREF);
                        break;
                    }
                }
            }
        }

        if (TextUtils.isEmpty(link)) {
            link = getText(parentElement, TAG_FEEDB_ORIGLINK);
            if (TextUtils.isEmpty(link)) {
                try {
                    String tempLink = getText(parentElement, TAG_GUID);
                    if (TextUtils.isEmpty(tempLink)) {
                        tempLink = getText(parentElement, TAG_ID);
                    }

                    if (tempLink.matches(URL_PATTERN))
                        link = tempLink;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!TextUtils.isEmpty(link)) {
            if (link.startsWith(HTTPS)) {
                link = HTTP + link.substring(HTTPS.length());
            }
        }
        return link;
    }

    private String getXmlContent(Element parentElement, boolean isItemElement) {
        String content = getText(parentElement, TAG_FULL_TEXT);
        if (TextUtils.isEmpty(content)) {
            content = getText(parentElement, isItemElement ? TAG_CONTENT_ENCODED : TAG_CONTENT);
            if (TextUtils.isEmpty(content)) {
                content = getText(parentElement, isItemElement ? TAG_DESCRIPTION : TAG_SUMMARY);
            }
        }
        // Find PodCasts
        content += ParserHelper.getPodCasts(parentElement);
        return content;
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return excerpt;
    }

    private String getText(Element parentElement, String cssQuery) {
        try {
            Elements elements = parentElement.select(cssQuery);
            if (!elements.isEmpty()) {
                for (Element element : elements) {
                    if (element != null && element.parent().tagName().equals(parentElement.tagName())) {
                        return element.text();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
