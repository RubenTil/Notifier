package nl.hsleiden.notifier.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.hsleiden.notifier.Model.Notification;
import nl.hsleiden.notifier.R;


public class OverviewActivity extends BaseActivity {

    OverviewView view;
    List<Notification> notifications;
    NotificationListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        toolbar.setTitle(R.string.header_overview);
        view = new OverviewView(view_stub);

        notifications = new Select()
                .from(Notification.class)
                .execute();

        adapter = new NotificationListAdapter(this, R.id.notificationList, notifications);
        view.notificationList.setAdapter(adapter);

        view.notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification notification = (Notification) adapter.getItem(position);

                Intent i = new Intent();
                i.putExtra("Notification", notification);
                i.setClass(getBaseContext(), NotificationActivity.class);
                startActivityForResult(i, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            notifications = new Select()
                    .from(Notification.class)
                    .execute();

            adapter = new NotificationListAdapter(this, R.id.notificationList, notifications);
            view.notificationList.setAdapter(adapter);
        }
    }

    public void openAddNotificationView(View v) {
        Intent i = new Intent();
        i.setClass(this, NotificationActivity.class);
        this.startActivityForResult(i, 1);
    }

    static class OverviewView {

        @BindView(R.id.notificationList)
        ListView notificationList;

        @BindView(R.id.addNotificationFab)
        FloatingActionButton addNotificationFab;

        OverviewView(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
