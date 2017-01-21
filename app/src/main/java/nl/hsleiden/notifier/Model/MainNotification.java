package nl.hsleiden.notifier.Model;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Created by Ruben van Til on 21-1-2017.
 */

public class MainNotification {
    public String title;
    public String details;
    public int icon;
    public DateTime initialShowTime;
    public RepeatMode repeatMode;
    public int[] repeatDaysPerWeek; //1 = monday, 7 = sunday
    public int[] repeatDaysPerMonth; // 1 = first day of month
    public Boolean isEnabled;

    public enum RepeatMode{ DAILY, WEEKLY, MONTHLY, NO_REPEAT }

    public MainNotification(String title, String details, int icon, DateTime initialShowTime, RepeatMode repeatMode, int[] repeatDaysPerWeek, int[] repeatDaysPerMonth, Boolean isEnabled) {
        this.title = title;
        this.details = details;
        this.icon = icon;
        this.initialShowTime = initialShowTime;
        this.repeatMode = repeatMode;
        this.repeatDaysPerWeek = repeatDaysPerWeek;
        this.repeatDaysPerMonth = repeatDaysPerMonth;
        this.isEnabled = isEnabled;
    }
}
