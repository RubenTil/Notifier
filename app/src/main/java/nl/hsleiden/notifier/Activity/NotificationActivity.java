package nl.hsleiden.notifier.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.hsleiden.notifier.Model.Notification;
import nl.hsleiden.notifier.R;
import nl.hsleiden.notifier.Service.NotificationService;

public class NotificationActivity extends BaseActivity {

    static NotificationView notificationView;

    Notification notification;

    List<String> dropdownOptions;

    static DateTime selectedTime;
    static boolean timeIsSelected;

    boolean isNewNotification; //Not editing a notification, but creating one.

    static FragmentManager fragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notification);
        toolbar.setTitle(R.string.header_new_notification);
        notificationView = new NotificationView(view_stub);

        fragmentManager = getFragmentManager();


        dropdownOptions = Arrays.asList(getResources().getStringArray(R.array.repeatmode_dropdown));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeatmode_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationView.repeatOptionsDropdown.setAdapter(adapter);

        if (getIntent().hasExtra("NotificationID")) {
            isNewNotification = false;
            notification = Notification.load(Notification.class, getIntent().getLongExtra("NotificationID", 0));
//            notification = (Notification) getIntent().getSerializableExtra("Notification");
            Log.d("adapter", "Id = " + notification.getId());
            notificationView.titleField.setText(notification.title);
            notificationView.detailsField.setText(notification.details);
            selectedTime = notification.initialShowTime;
            timeIsSelected = true;
            notificationView.isEnabled.setChecked(notification.isEnabled);


            switch (notification.repeatMode) {
                case NO_REPEAT:
                    notificationView.repeatOptionsDropdown.setSelection(dropdownOptions.indexOf(getResources().getString(R.string.repeatmode_no_repeat)));
                    break;
                case DAILY:
                    notificationView.repeatOptionsDropdown.setSelection(dropdownOptions.indexOf(getResources().getString(R.string.repeatmode_daily)));
                    break;
                case WEEKLY:
                    notificationView.repeatOptionsDropdown.setSelection(dropdownOptions.indexOf(getResources().getString(R.string.repeatmode_weekly)));
                    break;

            }

        } else {
            isNewNotification = true;
            //create dummy object
            notification = new Notification("", "", DateTime.now(), Notification.RepeatMode.NO_REPEAT, true);
            selectedTime = notification.initialShowTime;
        }
        notificationView.dateTimeField.setText(selectedTime.toString("dd-MM-YYYY HH:mm"));

    }

    public void confirmCreation(View v) {
        if (notificationView.titleField.getText().toString().trim().isEmpty() || !timeIsSelected) {
            Toast.makeText(getApplicationContext(), "Please select a time and/or enter a title", Toast.LENGTH_LONG).show();
            return;
        }
        notification.title = notificationView.titleField.getText().toString();
        notification.details = notificationView.detailsField.getText().toString();
        notification.initialShowTime = selectedTime;

        if (((String) notificationView.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_no_repeat)))
            notification.repeatMode = Notification.RepeatMode.NO_REPEAT;
        else if (((String) notificationView.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_daily)))
            notification.repeatMode = Notification.RepeatMode.DAILY;
        else if (((String) notificationView.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_weekly)))
            notification.repeatMode = Notification.RepeatMode.WEEKLY;

        notification.isEnabled = notificationView.isEnabled.isChecked();

        notification.save();

        if(notification.isEnabled) setAlarm();
        else if(!isNewNotification) stopAlarm();

        Intent i = new Intent();
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    private void setAlarm() {
        Intent i = new Intent(getApplicationContext(), NotificationService.class);
        i.putExtra("NotificationID", notification.getId());
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int pendingID = new BigDecimal(notification.getId()).intValueExact();
        PendingIntent pi = PendingIntent.getService(getBaseContext(), pendingID, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        switch(notification.repeatMode){
            case NO_REPEAT:
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, notification.initialShowTime.getMillis(), pi);
                break;
            case DAILY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notification.initialShowTime.getMillis(),AlarmManager.INTERVAL_DAY ,  pi);
                break;
            case WEEKLY:
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notification.initialShowTime.getMillis(),AlarmManager.INTERVAL_DAY * 7,  pi);
                break;
        }

        Log.d("Notificationactivity", "Alarm set at " + notification.initialShowTime.toString("HH:mm:ss"));
    }

    private void stopAlarm(){
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.putExtra("NotificationID", notification.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(this.getApplicationContext(), new BigDecimal(notification.getId()).intValueExact() , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        Log.d("Notificationactivity", "Alarm stopped");
    }


    public void cancelCreation(View v) {
        onBackPressed();
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(fragmentManager, "datePicker");
    }

    static void showTimePickerDialog() {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(fragmentManager, "timePicker");
    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            int hour = selectedTime.getHourOfDay();
            int minute = selectedTime.getMinuteOfHour();

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            selectedTime = selectedTime.hourOfDay().setCopy(hourOfDay);
            selectedTime = selectedTime.minuteOfHour().setCopy(minute);
            timeIsSelected = true;
            notificationView.dateTimeField.setText(selectedTime.toString("dd-MM-YYYY HH:mm"));
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = selectedTime.getYear();
            int month = selectedTime.getMonthOfYear() - 1; // -1 because different indexes
            int day = selectedTime.getDayOfMonth();

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            selectedTime = selectedTime.year().setCopy(year);
            selectedTime = selectedTime.monthOfYear().setCopy(month+1); // -1 because different indexes
            selectedTime = selectedTime.dayOfMonth().setCopy(day);
            showTimePickerDialog();
        }
    }


    static class NotificationView {

        @BindView(R.id.titleField)
        EditText titleField;
        @BindView(R.id.detailsField)
        EditText detailsField;
        @BindView(R.id.dateTimeField)
        TextView dateTimeField;
        @BindView(R.id.repeatOptionsDropDown)
        Spinner repeatOptionsDropdown;
        @BindView(R.id.isEnabled)
        Switch isEnabled;

        NotificationView(View v) {
            ButterKnife.bind(this, v);
        }
    }

}
