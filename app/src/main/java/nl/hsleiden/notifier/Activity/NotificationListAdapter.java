package nl.hsleiden.notifier.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.hsleiden.notifier.Model.Notification;
import nl.hsleiden.notifier.R;

/**
 * Created by Ruben van Til on 21-1-2017.
 */

public class NotificationListAdapter extends ArrayAdapter {

    Context context;

    public NotificationListAdapter(Context context, int resource, List<Notification> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Notification item = (Notification) getItem(position);
        ViewHolder holder;

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


        switch (item.repeatMode) {
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(context, NotificationActivity.class);
                i.putExtra("NotificationID", item.getId());
                Log.d("adapter", "Id = " + item.getId());
                ((OverviewActivity)context).startActivityForResult(i, 1);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(item);
                item.delete();
            }
        });

        holder.Enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.isEnabled = b;
                item.save();
            }
        });


        return convertView;
    }

    static class ViewHolder {

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

        @BindView(R.id.deleteButton)
        ImageButton deleteButton;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}
