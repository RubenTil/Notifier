package nl.hsleiden.notifier.Activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.hsleiden.notifier.R;
import nl.hsleiden.notifier.Model.MainNotification;

/**
 * Created by Ruben van Til on 21-1-2017.
 */

public class NotificationListAdapter extends ArrayAdapter {

    public NotificationListAdapter(Context context, int resource, List<MainNotification> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MainNotification item = (MainNotification) getItem(position);
        final ViewHolder holder;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.title.setText(item.title);
        holder.details.setText(item.details);
        holder.showTime.setText(item.initialShowTime.toString("dd-MM HH:mm"));
        holder.Enabled.setChecked(item.isEnabled);


        switch (item.repeatMode){
            case NO_REPEAT:
                holder.repeatMode.setText(R.string.repeatmode_no_repeat);
                break;
            case DAILY:
                holder.repeatMode.setText(R.string.repeatmode_daily);
                break;
            case WEEKLY:
                holder.repeatMode.setText(R.string.repeatmode_weekly);
                break;
            case MONTHLY:
                holder.repeatMode.setText(R.string.repeatmode_monthly);
                break;

        }


        return convertView;
    }

    static class ViewHolder{

        @BindView(R.id.listItemTitle)
        TextView title;

        @BindView(R.id.listItemDetails)
        TextView details;

        @BindView(R.id.listItemShowTime)
        TextView showTime;

        @BindView(R.id.listItemRepeat)
        TextView repeatMode;

        @BindView(R.id.listItemEnabled)
        Switch Enabled;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}
