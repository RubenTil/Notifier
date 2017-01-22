package nl.hsleiden.notifier.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.hsleiden.notifier.Model.Notification;
import nl.hsleiden.notifier.R;

public class NewNotificationActivity extends BaseActivity {

    static NewNotificationView newNotificationView;

    Notification notification;

    List<String> dropdownOptions;

    static DateTime selectedTime;
    static boolean timeIsSelected;

    static FragmentManager fragmentManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notification);
        toolbar.setTitle(R.string.header_new_notification);
        newNotificationView = new NewNotificationView(view_stub);

        fragmentManager = getFragmentManager();


        dropdownOptions = Arrays.asList(getResources().getStringArray(R.array.repeatmode_dropdown));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repeatmode_dropdown, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newNotificationView.repeatOptionsDropdown.setAdapter(adapter);

        if( getIntent().hasExtra("Notification")){
            notification = (Notification) getIntent().getSerializableExtra("Notification");
            newNotificationView.titleField.setText(notification.title);
            newNotificationView.detailsField.setText(notification.details);
            selectedTime = notification.initialShowTime;

            switch (notification.repeatMode){
                case NO_REPEAT:
                    newNotificationView.repeatOptionsDropdown.setSelection(R.string.repeatmode_no_repeat);
                    break;
                case DAILY:
                    newNotificationView.repeatOptionsDropdown.setSelection(R.string.repeatmode_daily);
                    break;
                case WEEKLY:
                    newNotificationView.repeatOptionsDropdown.setSelection(R.string.repeatmode_weekly);
                    break;
                case MONTHLY:
                    newNotificationView.repeatOptionsDropdown.setSelection(R.string.repeatmode_monthly);
                    break;

            }

        }
        else{
            //create dummy object
            notification = new Notification("", "", R.drawable.ic_feedback_black_24dp, DateTime.now(), Notification.RepeatMode.NO_REPEAT, true);
            selectedTime = notification.initialShowTime;
        }
        newNotificationView.dateTimeField.setText(selectedTime.toString("dd-MM-YYYY HH:mm"));

    }

    public void confirmCreation(View v) {
        if(newNotificationView.titleField.getText().toString().equals("") && !timeIsSelected){
            Toast.makeText(this, "Please select a time and/or enter a title", Toast.LENGTH_LONG);
            return;
        }
        notification.title = newNotificationView.titleField.getText().toString();
        notification.details = newNotificationView.detailsField.getText().toString();
        notification.initialShowTime = selectedTime;

        if (((String) newNotificationView.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_no_repeat)))
                notification.repeatMode = Notification.RepeatMode.NO_REPEAT;
        else if (((String) newNotificationView.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_daily)))
                notification.repeatMode = Notification.RepeatMode.DAILY;
        else if (((String) newNotificationView.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_weekly)))
                notification.repeatMode = Notification.RepeatMode.WEEKLY;
        else if (((String) newNotificationView.repeatOptionsDropdown.getSelectedItem()).equals(getResources().getString(R.string.repeatmode_monthly)))
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
            newNotificationView.dateTimeField.setText(selectedTime.toString("dd-MM-YYYY HH:mm"));
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = selectedTime.getYear();
            int month = selectedTime.getMonthOfYear();
            int day = selectedTime.getDayOfMonth();

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            selectedTime = selectedTime.year().setCopy(year);
            selectedTime = selectedTime.monthOfYear().setCopy(month);
            selectedTime = selectedTime.dayOfMonth().setCopy(day);
            showTimePickerDialog();
        }
    }


    static class NewNotificationView {

        @BindView(R.id.titleField)
        EditText titleField;
        @BindView(R.id.detailsField)
        EditText detailsField;
        @BindView(R.id.dateTimeField)
        TextView dateTimeField;
        @BindView(R.id.repeatOptionsDropDown)
        Spinner repeatOptionsDropdown;

        NewNotificationView(View v) {
            ButterKnife.bind(this, v);
        }
    }

}
