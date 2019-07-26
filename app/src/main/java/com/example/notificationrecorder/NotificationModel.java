package com.example.notificationrecorder;

import lombok.Data;

@Data
public class NotificationModel {
  private String appName;
  private String packageName;
  private String title;
  private String text;
  private String time;
  private Long epochSecond;
}
