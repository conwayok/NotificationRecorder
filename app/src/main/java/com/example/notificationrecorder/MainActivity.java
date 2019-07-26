package com.example.notificationrecorder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

  private TableLayout tab;
  private Button clearButton;
  private static final String TAG = "MainActivity";
  private ObjectMapper objectMapper;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    tab = findViewById(R.id.tab);
    clearButton = findViewById(R.id.clear_btn);

    clearButton.setOnClickListener(
        v -> {
          getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE).edit().clear().apply();
          tab.removeAllViews();
        });

    objectMapper = new ObjectMapper();
    LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));

    ComponentName cn =
        new ComponentName(getApplicationContext(), CustomNotificationListenerService.class);
    String flat =
        Settings.Secure.getString(
            getApplicationContext().getContentResolver(), "enabled_notification_listeners");
    boolean enabled = flat != null && flat.contains(cn.flattenToString());

    if (!enabled) {
      startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    tab.removeAllViews();
    loadContent();
  }

  private BroadcastReceiver onNotice =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          Log.i(TAG, "receive");
          loadContent();
        }
      };

  private void loadContent() {

    tab.removeAllViews();

    Map<String, ?> allEntries =
        getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE).getAll();

    List<MessageModel> messageModels =
        allEntries.values().stream()
            .map(
                v -> {
                  try {
                    return objectMapper.readValue((String) v, MessageModel.class);
                  } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                  }
                  return null;
                })
            // filter out null
            .filter(Objects::nonNull)
            // sort
            .sorted(Comparator.comparingLong(MessageModel::getEpochSecond))
            .collect(Collectors.toList());

    messageModels.forEach(
        messageModel -> {
          String pack = messageModel.getPackageName();
          String title = messageModel.getTitle();
          String text = messageModel.getText();
          String time = messageModel.getTime();
          TableRow tr = new TableRow(getApplicationContext());
          tr.setLayoutParams(
              new TableRow.LayoutParams(
                  TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
          TextView textview = new TextView(getApplicationContext());
          textview.setLayoutParams(
              new TableRow.LayoutParams(
                  TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
          textview.setTextSize(16);
          textview.setTextColor(Color.parseColor("#0B0719"));
          textview.setText(
              Html.fromHtml(
                  pack + "<br><b>" + title + " : </b>" + text + " " + time,
                  Html.FROM_HTML_MODE_LEGACY));
          tr.addView(textview);
          tab.addView(tr);
        });
  }
}
