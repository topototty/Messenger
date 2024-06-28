package com.example.messengerapp;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

public class NotificationSender {

    private static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "BHSYjMa5OinOCOkNb-AZVZrFID7Dz5limmCN5htFCX5af1aD_NVwnDomDfpulg-ZmoZfVTWrCoVCQHw0Lu5dwRA"; // Замените YOUR_SERVER_KEY на ваш ключ сервера FCM

    public static void sendNotification(String token, String title, String body) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();

        try {
            notificationBody.put("title", title);
            notificationBody.put("body", body);

            notification.put("to", token);
            notification.put("notification", notificationBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(notification.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(FCM_API_URL)
                .post(requestBody)
                .addHeader("Authorization", "key=" + SERVER_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        System.out.println(response.body().string());
    }
}
