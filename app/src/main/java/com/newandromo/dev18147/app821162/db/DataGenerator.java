package com.newandromo.dev18147.app821162.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.newandromo.dev18147.app821162.db.entity.CategoryEntity;
import com.newandromo.dev18147.app821162.db.entity.FeedEntity;
import com.newandromo.dev18147.app821162.db.entity.YoutubeTypeEntity;

public class DataGenerator {

    public static List<FeedEntity> populateFeedsByCategory(AppDatabase database, JSONObject defaultObj) throws JSONException {
        List<FeedEntity> feeds = new ArrayList<>();

        JSONArray catsArray = defaultObj.getJSONArray("category_feeds");

        for (int i = 0; i < catsArray.length(); i++) {
            JSONObject catObj = catsArray.getJSONObject(i);
            String title = catObj.getString("category");
            CategoryEntity category = new CategoryEntity();
            category.setTitle(title);

            int rowId = (int) database.categoryDao().insertCategory(category);

            JSONArray feedsArray = catObj.getJSONArray("feeds");

            for (int j = 0; j < feedsArray.length(); j++) {
                FeedEntity feed = new FeedEntity();
                JSONObject feedObj = feedsArray.getJSONObject(j);
                boolean isJson = feedObj.getBoolean("is_json");
                boolean isGoogleJson = feedObj.getBoolean("is_google_json");
                boolean isWordPressJson = feedObj.getBoolean("is_wordpress_json");

                feed.setCategoryId(rowId);
                feed.setTitle(feedObj.getString("name"));
                feed.setFeedUrl(feedObj.getString("url"));
                feed.setSiteUrl(feedObj.getString("web"));
                feed.setGoogleBlogId(feedObj.getString("google_blog_id"));
                feed.setIsJson(isJson ? 1 : 0);
                feed.setIsGoogleJson(isGoogleJson ? 1 : 0);
                feed.setIsWordPressJson(isWordPressJson ? 1 : 0);

                feeds.add(feed);
            }
        }

        return feeds;
    }

    public static List<YoutubeTypeEntity> populateYoutubeTypes(JSONObject defaultObj) throws JSONException {
        List<YoutubeTypeEntity> youtubeTypes = new ArrayList<>();

        JSONArray youtubeArray = defaultObj.getJSONArray("youtube");

        for (int i = 0; i < youtubeArray.length(); i++) {
            JSONObject youtubeObj = youtubeArray.getJSONObject(i);
            String id = youtubeObj.getString("id");
            String name = youtubeObj.getString("name");
            boolean isChannel = !id.startsWith("PL");

            YoutubeTypeEntity youtubeType =
                    new YoutubeTypeEntity(id, name, isChannel ? 1 : 0, 0);
            youtubeTypes.add(youtubeType);
        }

        return youtubeTypes;
    }
}
