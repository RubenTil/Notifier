package nl.hsleiden.notifier.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.hsleiden.notifier.Model.MainNotification;
import nl.hsleiden.notifier.R;


public class OverviewActivity extends BaseActivity {

    OverviewView view;
    ArrayList<MainNotification> mainNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        toolbar.setTitle(R.string.header_overview);
        view = new OverviewView(view_stub);

        mainNotifications = new ArrayList<>();

        mainNotifications.add(new MainNotification("titles", "detail blabla", R.drawable.ic_feedback_black_24dp, DateTime.now(), MainNotification.RepeatMode.NO_REPEAT, null, null, true  ));
        mainNotifications.add(new MainNotification("titles", "detail blabla", R.drawable.ic_feedback_black_24dp, DateTime.now(), MainNotification.RepeatMode.NO_REPEAT, null, null, false  ));
        mainNotifications.add(new MainNotification("titles", "detail blabla", R.drawable.ic_feedback_black_24dp, DateTime.now(), MainNotification.RepeatMode.NO_REPEAT, null, null, true));

        NotificationListAdapter adapter = new NotificationListAdapter(this, R.id.notificationList, mainNotifications);
        view.notificationList.setAdapter(adapter);


    }

    static class OverviewView {

        @BindView(R.id.notificationList)
        ListView notificationList;

        public OverviewView(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
