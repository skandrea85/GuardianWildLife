package com.example.andrea.guardian;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class WildLifeUtilis {

    // Keys used for the JSON response
    private static final String response = "response";

    private static final String results = "results";

    private static final String section = "sectionName";

    private static final String date = "webPublicationDate";

    private static final String title = "webTitle";

    private static final String url = "webUrl";

    private static final String tags = "tags";

    private static final String author = "webTitle";

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = WildLifeUtilis.class.getSimpleName();

    /**
     * Create a private constructor.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name NewsUtils.
     */

    private WildLifeUtilis() {
    }

    // Query the Guardian data and return a list of News objects.

    public static List<wildlife> fetchNewsData(String requestUrl) throws InterruptedException {

        /*
         * Create URL object
         */

        URL url = returnUrl(requestUrl);

        // Perform HTTP request to URL and receive a JSON response
        String jsonResponse = null;
        try {
            // Try to create a HTTP request with the request URL by makeHttpRequest
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            // In case that request failed, print the error message into log
            Log.e(LOG_TAG, "HTTP request failed.", e);
        }

        // Extract relevant fields from the JSON response and create a list of Education News
        List<wildlife> wild = extractFeatureFromJson(jsonResponse);
        // Return the list of news
        return wild;
    }

    /*
     *Return new URL object from the given string URL.
     */
    private static URL returnUrl(String stringUrl) {
        URL url = null;
        try {
            // Try to create an URL from String
            url = new URL(stringUrl);

        } catch (MalformedURLException e) {

            // In case that request failed, print the error message into log
            Log.e(LOG_TAG, "URL building problem.", e);
        }
        return url;
    }


    /*
     * Make an HTTP request to the given URL and return a String as the response.
     */

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null then return early.
        if (url == null) {
            return jsonResponse;
        }
        // Initialize variables for the HTTP connection and for the InputStream
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            // Try to establish a HTTP connection with the request URL and set up the properties of the request
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            // Send a request to connect
            urlConnection.connect();
            // If the request was successful, then read the Input Stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                // If the response failed, print it to the Log
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {

            // If the connection was not established, print it to the log
            Log.e(LOG_TAG, "Connection was not established. Problem retrieving JSON News results.", e);
        } finally {
            // Disconnect the HTTP connection if it is not disconnected yet
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            // Close the Input Stream if it is not closed yet
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

       /*
    / Convert the InputStream into a String which contains the whole JSON response from the server.
    */

    private static String readFromStream(InputStream inputStream) throws IOException {
        // Create a new StringBuilder
        StringBuilder output = new StringBuilder();

        // If the InputStream exists, create an InputStreamReader from it and a BufferedReader from the InputStreamReader
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Append the data of the BufferedReader line by line to the StringBuilder
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        // Convert the output into String and return it
        return output.toString();
    }

    /**
     * Return list of {@link wildlife} objects that has been built up from parsing the given JSON response.
     */

    private static List<wildlife> extractFeatureFromJson(String newsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<wildlife> wildlifeList = new ArrayList<>();

        // Try to parse the JSON response string.
        // In case of any problem with the way the JSON is formatted,
        // a JSONException exception object will be thrown.
        // To avoid app is crashed error message is printed into logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonNewsResponse = new JSONObject(newsJSON);

            // Extract the JSONObject associated with the key called "response",
            JSONObject responseJsonNews = baseJsonNewsResponse.getJSONObject(response);

            // Extract the JSONArray associated with the key called "results"
            JSONArray newsArray = responseJsonNews.getJSONArray(results);

            // For each news in the JsonNewsArray create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news article at position i within the list of news
                JSONObject currentNews = newsArray.getJSONObject(i);

                // Extract the section name for the key called "sectionName"
                String newsSection = currentNews.getString(section);

                // Check if newsDate exist and than extract the date for the key called "webPublicationDate"
                String newsDate = "N/A";

                if (currentNews.has(date)) {
                    newsDate = currentNews.getString(date);
                }

                // Extract the article name for the key called "webTitle"
                String newsTitle = currentNews.getString(title);

                // Extract the value for the key called "webUrl"
                String newsUrl = currentNews.getString(url);

                //Extract the JSONArray associated with the key called "tags",
                JSONArray currentNewsAuthorArray = currentNews.getJSONArray(tags);

                String newsAuthor = "N/A";

                //Check if "tags" array contains data
                int tagsLenght = currentNewsAuthorArray.length();


                if (tagsLenght == 1) {
                    // Create a JSONObject for author
                    JSONObject currentNewsAuthor = currentNewsAuthorArray.getJSONObject(0);

                    String newsAuthor1 = currentNewsAuthor.getString(author);

                    newsAuthor = "written by: " + newsAuthor1;

                }
                // Create a new News object with the title, category, author, date, url ,
                // from the JSON response.
                wildlife NewsWild = new wildlife(newsTitle, newsSection, newsAuthor, newsDate, newsUrl);
                // Add the new {@link News} to the list of News.
                wildlifeList.add(NewsWild);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("NewsUtils", "JSON results parsing problem.");
        }

        // Return the list of earthquakes
        return wildlifeList;
    }


}
