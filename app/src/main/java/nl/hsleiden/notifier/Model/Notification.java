package nl.hsleiden.notifier.Model;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.Serializable;

/**
 * Created by Ruben van Til on 21-1-2017.
 */

@Table(name="Notifications")
public class Notification extends Model implements Serializable{

    @Expose
    @Column(name ="title")
    public String title;

    @Expose
    @Column(name ="details")
    public String details;

    @Expose
    @Column(name ="icon")
    public int icon;

    @Expose
    @Column(name ="initial_show_time")
    public DateTime initialShowTime;

    @Expose
    @Column(name ="repeat_mode")
    public RepeatMode repeatMode;

    @Expose
    @Column(name ="is_enabled")
    public Boolean isEnabled;

    public enum RepeatMode{ DAILY, WEEKLY, NO_REPEAT }

    public Notification(){
        super();
    }

    public Notification(String title, String details, int icon, DateTime initialShowTime, RepeatMode repeatMode, Boolean isEnabled) {
        super();
        this.title = title;
        this.details = details;
        this.icon = icon;
        this.initialShowTime = initialShowTime;
        this.repeatMode = repeatMode;
        this.isEnabled = isEnabled;
    }
}
