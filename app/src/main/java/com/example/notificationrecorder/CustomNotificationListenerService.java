package com.example.notificationrecorder;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.LocalDateTime;

public class CustomNotificationListenerService extends NotificationListenerService {

  private static final String TAG = "nnn";

  private static final String LINE_PACKAGE = "jp.naver.line.android";
  private static final String FACEBOOK_PACKAGE = "com.facebook";
  //  private static final String WHATSAPP_PACKAGE = "com.whatsapp";
  //  private static final String INSTAGRAM_PACKAGE = "com.instagram";

  private Context context;
  private ObjectMapper objectMapper;

  @Override
  public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
    objectMapper = new ObjectMapper();
  }

  @Override
  public void onListenerConnected() {
    super.onListenerConnected();
  }

  @Override
  public void onNotificationPosted(StatusBarNotification sbn) {
    String packageName = sbn.getPackageName();

    if (packageName.contains(FACEBOOK_PACKAGE) || packageName.contains(LINE_PACKAGE)) {
      Bundle extras = sbn.getNotification().extras;
      try {
        String title = extras.getCharSequence(Notification.EXTRA_TITLE).toString();
        String text = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
        Log.i(TAG, "Package " + packageName);
        Log.i(TAG, "Title " + title);
        Log.i(TAG, "Text " + text);

        LocalDateTime current = LocalDateTime.now().withNano(0);
        String timeString = current.toString();
        long second = current.atZone(Clock.systemDefaultZone().getZone()).toEpochSecond();

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setPackageName(packageName);

        if (packageName.contains(LINE_PACKAGE)) notificationModel.setAppName("LINE");
        else if (packageName.contains(FACEBOOK_PACKAGE)) notificationModel.setAppName("FACEBOOK");

        notificationModel.setTitle(title);
        notificationModel.setText(text);
        notificationModel.setTime(timeString);
        notificationModel.setEpochSecond(second);
        String json = objectMapper.writeValueAsString(notificationModel);
        SharedPreferences.Editor editor =
            getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE).edit();
        editor.putString(packageName + " " + timeString, json);
        editor.apply();
        Intent intent = new Intent("Msg");
        intent.putExtra("payload", json);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
      } catch (NullPointerException | JsonProcessingException e) {
        Log.e(TAG, e.getMessage());
      }
    }
  }
}
