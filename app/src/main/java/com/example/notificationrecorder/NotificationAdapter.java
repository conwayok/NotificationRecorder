//package com.example.notificationrecorder;
//
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.text.Html;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import java.util.List;
//
//import lombok.Data;
//
//public class NotificationAdapter
//    extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
//
//  private List<NotificationModel> notificationModels;
//
//  public NotificationAdapter(List<NotificationModel> notificationModels) {
//    this.notificationModels = notificationModels;
//  }
//
//  @NonNull
//  @Override
//  public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//    View view =
//        LayoutInflater.from(viewGroup.getContext())
//            .inflate(R.layout.notification_model_card, viewGroup, false);
//
//    return new NotificationViewHolder(view);
//  }
//
//  @Override
//  public void onBindViewHolder(
//      @NonNull NotificationViewHolder notificationViewHolder, int position) {
//    NotificationModel notificationModel = notificationModels.get(position);
//
//    notificationViewHolder.getAppName().setText(notificationModel.getAppName());
//    notificationViewHolder.getTitle().setText(notificationModel.getTitle());
//    notificationViewHolder
//        .getContent()
//        .setText(Html.fromHtml(notificationModel.getText(), Html.FROM_HTML_MODE_LEGACY));
//    notificationViewHolder.getTime().setText(notificationModel.getTime());
//  }
//
//  @Override
//  public int getItemCount() {
//    return notificationModels.size();
//  }
//
//  @Data
//  public static class NotificationViewHolder extends RecyclerView.ViewHolder {
//    private TextView appName;
//    private TextView title;
//    private TextView content;
//    private TextView time;
//
//    public NotificationViewHolder(View itemView) {
//      super(itemView);
//
//      appName = itemView.findViewById(R.id.notification_app_name);
//      title = itemView.findViewById(R.id.notification_title);
//      content = itemView.findViewById(R.id.notification_content);
//      time = itemView.findViewById(R.id.notification_time);
//    }
//  }
//}
