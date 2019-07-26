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

  private TableLayout tableLayout;

  private Button clearButton;

  //  private RecyclerView recyclerView;
  //  private RecyclerView.Adapter adapter;
  //  private RecyclerView.LayoutManager layoutManager;

  private static final String TAG = "MainActivity";
  private ObjectMapper objectMapper;

  //  private List<NotificationModel> notificationModels;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    tableLayout = findViewById(R.id.table_layout);

    //    recyclerView = findViewById(R.id.main_recycler_view);
    //    recyclerView.setHasFixedSize(true);
    // use a linear layout manager
    //    layoutManager = new LinearLayoutManager(this);
    //    recyclerView.setLayoutManager(layoutManager);
    //    notificationModels = new ArrayList<>();
    //
    //    NotificationModel notificationModel = new NotificationModel();
    //    notificationModel.setAppName("app 1");
    //    notificationModel.setPackageName("qwerqwerf");
    //    notificationModel.setText("qwerwfssdfasdfww");
    //    notificationModel.setTitle("title");
    //    notificationModel.setTime("time");
    //    notificationModels.add(notificationModel);

    //    adapter = new NotificationAdapter(notificationModels);
    //    recyclerView.setAdapter(adapter);

    clearButton = findViewById(R.id.clear_btn);

    clearButton.setOnClickListener(
        v -> {
          getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE).edit().clear().apply();
          //          recyclerView.removeAllViews();
          tableLayout.removeAllViews();
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

    //    recyclerView.removeAllViews();
    tableLayout.removeAllViews();
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

    tableLayout.removeAllViews();

    Map<String, ?> allEntries =
        getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE).getAll();

    List<NotificationModel> notificationModels =
        allEntries.values().stream()
            .map(
                v -> {
                  try {
                    return objectMapper.readValue((String) v, NotificationModel.class);
                  } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                  }
                  return null;
                })
            // filter out null
            .filter(Objects::nonNull)
            // sort
            .sorted(Comparator.comparingLong(NotificationModel::getEpochSecond))
            .collect(Collectors.toList());

    notificationModels.forEach(
        notificationModel -> {
          TableRow tableRow = new TableRow(getApplicationContext());

          tableRow.setPadding(0, 10, 0, 10);

          tableRow.setLayoutParams(
              new TableRow.LayoutParams(
                  TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
          TextView textview = new TextView(getApplicationContext());
          textview.setLayoutParams(
              new TableRow.LayoutParams(
                  TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
          textview.setTextSize(16);
          textview.setTextColor(Color.parseColor("#0B0719"));
          textview.setText(
              Html.fromHtml(generatePrettyHtml(notificationModel), Html.FROM_HTML_MODE_LEGACY));
          tableRow.addView(textview);

          tableLayout.addView(tableRow);
        });
  }

  private String generatePrettyHtml(NotificationModel notificationModel) {
    String appName = notificationModel.getAppName();
    String title = notificationModel.getTitle();
    String text = notificationModel.getText();
    String time = notificationModel.getTime();

    String appNameText = "<b><big>" + appName + "</b>";
    String titleText = "<b>" + title + ": </b>";

    if (appName.equals("LINE")) {
      appNameText = "<font color='green'>" + appNameText + "</font>";
    } else if (appName.equals("FACEBOOK")) {
      appNameText = "<font color='blue'>" + appNameText + "</font>";
    } else {
      appNameText = appName;
    }

    return appNameText + "<br>" + titleText + text + "<br>" + time;
  }
}
