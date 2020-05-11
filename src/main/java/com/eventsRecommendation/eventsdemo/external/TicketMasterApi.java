package com.eventsRecommendation.eventsdemo.external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.eventsRecommendation.eventsdemo.entity.Category;
import com.eventsRecommendation.eventsdemo.entity.Item;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TicketMasterApi
{
    private static final String URL = "https://app.ticketmaster.com/discovery/v2";
    private static final String DEFAULT_KEYWORD = "";
    private static final String API_KEY = "6W7owaMD79IalzZzjGxOfPjd7KrVtos3";
    private static final int PRECISION = 8;
    private static final int RADIUS = 100;
    private static final int RESPONSE_SIZE = 40;

    public static List<Item> search(double lat, double lon, String keyword) {
        if (keyword == null) {
            keyword = DEFAULT_KEYWORD;
        }
        try {
            keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
            // Encode keyword in url since it may contain special characters
        } catch (Exception e) {
            e.printStackTrace();
        }

        String geoHash = GeoHash.encodeGeohash(lat, lon, PRECISION);
        ///Radius of the area in which we want to search for events.
        String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s&size=%s", API_KEY, geoHash, keyword, RADIUS, RESPONSE_SIZE);
        return fetchItemList(URL + "/events.json?" + query);

    }

    public static Item searchItem(String itemId) {
        try {
            String query = URL + "/events/" + itemId + "?apikey=" + API_KEY;

            JSONObject event = fetchItems(query);
            if (event == null) return null;

            return getItemFromJsonObject(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Item> fetchItemList(String query) {
        try {
            JSONObject obj = fetchItems(query);
            if (obj == null) return new ArrayList<>();

            if (obj.isNull("_embedded")) {
                return new ArrayList<>();
            }
            JSONObject embedded = obj.getJSONObject("_embedded");
            JSONArray events = embedded.getJSONArray("events");
            return getItemList(events);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static JSONObject fetchItems(String query) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(query).openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        System.out.println("responseCode" + responseCode);///should be under 400

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //Being able to read a file line by line gives us the ability to seek only the relevant information and
        // stop the search once we have found what we're looking for.
        // It also allows us to break up the data into logical pieces, like if the file was CSV-formatted.

        JSONObject obj = new JSONObject(response.toString());
        return obj;

    }

    public static List<Item> getItemList(JSONArray events) throws JSONException {
        List<Item> itemList = new ArrayList<>();

        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);

            Item item = getItemFromJsonObject(event);
            itemList.add(item);
        }
        return itemList;
    }

    private static Item getItemFromJsonObject(JSONObject event) throws JSONException {
        Item.ItemBuilder itemBuilder = Item.builder();

        if (!event.isNull("name")) {
            itemBuilder.name(event.getString("name"));
        }
        if (!event.isNull("id")) {
            itemBuilder.itemId(event.getString("id"));
        }
        if (!event.isNull("url")) {
            itemBuilder.url(event.getString("url"));
        }
        if (!event.isNull("rate")) {
            itemBuilder.rating(event.getDouble("rate"));
        }
        if (!event.isNull("distance")) {
            itemBuilder.distance(event.getDouble("distance"));
        }
        if (!event.isNull("dates")) {
            JSONObject date = event.getJSONObject("dates");
            if (!date.isNull("status")) {
                itemBuilder.status(date.getJSONObject("status").getString("code"));
            }
            if (!date.isNull("start")) {
                JSONObject start = date.getJSONObject("start");
                if (!start.isNull("localDate")) {
                    itemBuilder.localDate(start.getString("localDate"));
                    if (!start.isNull("localTime")) {
                        itemBuilder.time(start.getString("localTime"));
                    }
                }
            }
        }

        itemBuilder.address(getAddress(event));
        itemBuilder.imageUrl(getImageUrl(event));
        itemBuilder.genres(getGenres(event));
        Item item = itemBuilder.build();

        Set<String> categoryTypes = getCategories(event);
        Set<Category> categories = categoryTypes.stream()
                .map(category-> new Category(item,category))
                .collect(Collectors.toSet());
        item.setCategories(categories);
        return item;
    }

    private static String getAddress(JSONObject event) throws JSONException {
        if (!event.isNull("_embedded")) {
            JSONObject embedded = event.getJSONObject("_embedded");
            StringBuilder stringBuilder = new StringBuilder();
            if (!embedded.isNull("venues")) {
                JSONArray venues = embedded.getJSONArray("venues");
                for (int i = 0; i < venues.length(); i++) {
                    JSONObject venue = venues.getJSONObject(i);
                    if (!venue.isNull("address")) {
                        JSONObject address = venue.getJSONObject("address");
                        if (!address.isNull("line1")) {
                            stringBuilder.append(address.getString("line1"));
                        }
                        if (!address.isNull("line2")) {
                            stringBuilder.append(" ");
                            stringBuilder.append(address.getString("line2"));
                        }
                        if (!address.isNull("line3")) {
                            stringBuilder.append(" ");
                            stringBuilder.append(address.getString("line3"));
                        }
                    }
                    if (!venue.isNull("city")) {
                        JSONObject city = venue.getJSONObject("city");
                        if (!city.isNull("name")) {
                            stringBuilder.append(" ");
                            stringBuilder.append(city.getString("name"));
                        }
                    }
                    if (stringBuilder.toString().length() > 0) {
                        return stringBuilder.toString();
                    }
                }
            }
        }
        return "";
    }

    private static String getImageUrl(JSONObject event) throws JSONException {
        String url = "";
        if (!event.isNull("images")) {
            JSONArray images = event.getJSONArray("images");
            for (int i = 0; i < images.length(); i++) {
                JSONObject image = images.getJSONObject(i);
                if (!image.isNull("url")) {
                    url = image.getString("url");
                }
                if (url.length() > 0) {
                    return url;
                }
            }
        }
        return "";
    }

    private static Set<String> getCategories(JSONObject event) throws JSONException {
        Set<String> categories = new HashSet<>();
        if (!event.isNull("classifications")) {
            JSONArray classifications = event.getJSONArray("classifications");
            for (int i = 0; i < classifications.length(); i++) {
                JSONObject classification = classifications.getJSONObject(i);
                if (!classification.isNull("segment")) {
                    JSONObject segment = classification.getJSONObject("segment");
                    if (!segment.isNull("name")) {
                        categories.add(segment.getString("name"));
                    }
                }

            }
        }
        return categories;
    }

    private static String getGenres(JSONObject event) throws JSONException {
        List<String> genres = new ArrayList<>();
        if (!event.isNull("classifications")) {
            JSONArray classifications = event.getJSONArray("classifications");
            for (int i = 0; i < classifications.length(); i++) {
                JSONObject classification = classifications.getJSONObject(i);
                if (!classification.isNull("genre")) {
                    JSONObject segment = classification.getJSONObject("genre");
                    if (!segment.isNull("name")) {
                        genres.add(segment.getString("name"));
                    }
                }

            }
        }
        return Strings.join(genres, ' ');
    }
}


//      "classifications": [
//    {
//        "primary": true,
//            "segment": {
//        "id": "KZFzniwnSyZfZ7v7nJ",
//                "name": "Music"
//    },
//        "genre": {
//        "id": "KnvZfZ7vAeA",
//                "name": "Rock"
//    },
//        "subGenre": {
//        "id": "KZazBEonSMnZfZ7v6dt",
//                "name": "Alternative Rock"
//    }
//    }
//  ],
