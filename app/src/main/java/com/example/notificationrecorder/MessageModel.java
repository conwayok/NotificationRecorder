package com.example.notificationrecorder;

import lombok.Data;

@Data
public class MessageModel {
  private String packageName;
  private String title;
  private String text;
  private String time;
  private Long epochSecond;
}
