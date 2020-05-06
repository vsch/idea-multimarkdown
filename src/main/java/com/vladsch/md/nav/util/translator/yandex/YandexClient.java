// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.translator.yandex;

import com.intellij.util.net.HttpConfigurable;
import kotlin.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class YandexClient {
    public static final String HOST = "translate.yandex.net";
    public static final String PATH = "/api/v1.5/tr.json/";
    //private static final String APIKEY = "trnsl.1.1.20150828T222514Z.96c635fa0967005b.781eebb21e0a7b0e9b3b4f2fb62a21a74400189f";

    final private String myApiKey;
    private String myLastError;

    public YandexClient(final String apiKey) {
        myApiKey = apiKey;
    }

    public String getLastError() {
        return myLastError;
    }

    //https://translate.yandex.net/api/v1.5/tr.json/translate?key=APIkey&lang=en-ru&text=To+be,+or+not+to+be%3F&text=That+is+the+question.
    public String translate(String text, String langFrom, String langTo) {
        final String method = "translate";

        String uri = "https://" + HOST + PATH + method;
        uri += "?key=" + myApiKey;
        uri += "&lang=" + langFrom + "-" + langTo;
        try {
            uri += "&text=" + URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return request(uri);
    }

    public String detect(String text, String langFrom, String langTo) {
        final String method = "detect";
        String uri = "https://" + HOST + PATH + method;
        uri += "?key=" + myApiKey;
        //uri += "&hint=" + langFrom;// + "," + langTo;
        try {
            uri += "&text=" + URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return request(uri);
    }

    public String translate(List<String> texts, String langFrom, String langTo) {
        final String method = "translate";

        String uri = "https://" + HOST + PATH + method;
        ArrayList<Pair<String, String>> data = new ArrayList<>();
        data.add(new Pair<>("key", myApiKey));
        data.add(new Pair<>("lang", langFrom + "-" + langTo));
        for (String text : texts) {
            data.add(new Pair<>("text", text));
        }
        return request(uri, data);
    }

    public String getLanguages(String ui) {
        final String method = "getLangs";
        String uri = "https://" + HOST + PATH + method;
        uri += "?key=" + myApiKey;
        uri += "&ui=" + ui;

        return request(uri);
    }

    @Nullable
    private String request(String uri, List<Pair<String, String>> data) {
        try {
            final HttpConfigurable httpConfigurable = HttpConfigurable.getInstance();
            final URLConnection urlConnection = httpConfigurable != null ? httpConfigurable.openConnection(uri) : new URL(uri).openConnection();
            HttpURLConnection http = (HttpURLConnection) urlConnection;
            http.setRequestMethod("POST"); // PUT is another valid option
            urlConnection.setDoOutput(true);

            StringJoiner sj = new StringJoiner("&");
            for (Pair<String, String> entry : data) {
                sj.add(URLEncoder.encode(entry.getFirst(), "UTF-8") + "="
                        + URLEncoder.encode(entry.getSecond(), "UTF-8"));
            }

            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            try (OutputStream os = http.getOutputStream()) {
                os.write(out);
            }

            InputStream inputStream = null;

            inputStream = urlConnection.getInputStream();
            java.util.Scanner s = new java.util.Scanner(inputStream, "UTF-8").useDelimiter("\\A");
            myLastError = null;
            return s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            myLastError = e.getMessage();
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Nullable
    private String request(String uri) {
        try {
            URLConnection connection = new URL(uri).openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            InputStream response = connection.getInputStream();

            java.util.Scanner s = new java.util.Scanner(response, "UTF-8").useDelimiter("\\A");
            myLastError = null;
            return s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            myLastError = e.getMessage();
            return null;
        }
    }
}
