package nl.hsleiden.notifier.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.hsleiden.notifier.Model.Notification;
import nl.hsleiden.notifier.R;

public class NewNotificationActivity extends BaseActivity {

    NewNotificationView view;

    Notification notification;

    List<String> dropdownOptions;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notification);
        toolbar.setTitle(R.string.header_new_notification);
        view = new NewNotificationView(view_stub);

        dropdownOptions = Arrays.asList(getResources().getStringArray(R.array.repeatmode_dropdown));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeatmode_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        view.repeatOptionsDropdown.setAdapter(adapter);

        if( getIntent().hasExtra("Notification")){
            notification = (Notification) getIntent().getSerializableExtra("Notification");
            view.titleField.setText(notification.title);
            view.detailsField.setText(notification.details);
            view.dateField.setText(notification.initialShowTime.toString("dd-MM-YYYY"));
            view.timeField.setText(notification.initialShowTime.toString("HH:mm"));

            switch (notification.repeatMode){
                case NO_REPEAT:
                    view.repeatOptionsDropdown.setSelection(R.string.repeatmode_no_repeat);
                    break;
                case DAILY:
                    view.repeatOptionsDropdown.setSelection(R.string.repeatmode_daily);
                    break;
                case WEEKLY:
                    view.repeatOptionsDropdown.setSelection(R.string.repeatmode_weekly);
                    break;
                case MONTHLY:
                    view.repeatOptionsDropdown.setSelection(R.string.repeatmode_monthly);
                    break;

            }

        }
        else{
            //create dummy object
            notification = new Notification("", "", R.drawable.ic_feedback_black_24dp, DateTime.now(), Notification.RepeatMode.NO_REPEAT, true);
        }


    }

    public void confirmCreation(View v) {
        if(!view.timeField.getText().toString().equals(""))
            notification.title = view.titleField.getText().toString();
        else return;

        notification.details = view.detailsField.getText().toString();

        LocalTime localTime = LocalTime.parse(view.timeField.getText().toString(), DateTimeFormat.forPattern("HH:mm"));
        LocalDate localDate = LocalDate.parse(view.dateField.getText().toString(), DateTimeFormat.forPattern("dd-MM-YYYY"));
        notification.initialShowTime = localDate.toDateTime(localTime);

        if (((String) view.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_no_repeat)))
                notification.repeatMode = Notification.RepeatMode.NO_REPEAT;
        else if (((String) view.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_daily)))
                notification.repeatMode = Notification.RepeatMode.DAILY;
        else if (((String) view.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_weekly)))
                notification.repeatMode = Notification.RepeatMode.WEEKLY;
        else if (((String) view.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_monthly)))
                notification.repeatMode = Notification.RepeatMode.MONTHLY;

        notification.isEnabled = true;

        Intent i = new Intent();
        i.putExtra("Notification", notification);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    public void cancelCreation(View v){
        onBackPressed();
    }


    static class NewNotificationView {

        @BindView(R.id.titleField)
        EditText titleField;
        @BindView(R.id.detailsField)
        EditText detailsField;
        @BindView(R.id.dateField)
        EditText dateField;
        @BindView(R.id.timeField)
        EditText timeField;
        @BindView(R.id.repeatOptionsDropDown)
        Spinner repeatOptionsDropdown;

        NewNotificationView(View v) {
            ButterKnife.bind(this, v);
        }
    }

}
