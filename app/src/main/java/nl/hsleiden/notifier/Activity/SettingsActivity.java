package nl.hsleiden.notifier.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.hsleiden.notifier.Installation;
import nl.hsleiden.notifier.Model.Notification;
import nl.hsleiden.notifier.Model.gsonDateTimeSerializer;
import nl.hsleiden.notifier.R;

public class SettingsActivity extends BaseActivity {

    SettingsView view;

    RequestQueue requestQueue;
    Gson gson;

    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        view = new SettingsView(view_stub);


        requestQueue = Volley.newRequestQueue(getApplicationContext());
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(DateTime.class, new gsonDateTimeSerializer())
                .serializeNulls()
                .create();
        user_id = Installation.id(this);
    }

    public void importItems(View v) {
        final String baseurl = view.importField.getText().toString();
        final String url = "http://" + baseurl + "/fetchdata.php?user_id="+ user_id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    ArrayList<Notification> notifications = gson.fromJson(response, new TypeToken<List<Notification>>(){}.getType());
                    ActiveAndroid.beginTransaction();
                    try {
                        for (Notification n: notifications) {
                            n.save();
                        }
                        ActiveAndroid.setTransactionSuccessful();
                        Toast.makeText(getApplicationContext(), "Succesfully imported "+ notifications.size() + " notifications", Toast.LENGTH_LONG).show();

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {
                        ActiveAndroid.endTransaction();
                    }
                }catch(Exception e)
                {
                    Toast.makeText(getApplicationContext(), "The provided URL did not contain valid import data", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.d("url", url);
                    Log.d("response", response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error:" + error.getMessage() , Toast.LENGTH_LONG).show();

                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);


    }

    public void exportItems(View v) {

        final String baseurl = view.importField.getText().toString();
        String url = "http://" + baseurl + "/savedata.php?user_id="+ user_id;

        List<Notification> notifications = new Select()
                .from(Notification.class)
                .execute();
        final String NotificationJSON = gson.toJson(notifications,  new TypeToken<List<Notification>>(){}.getType());


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Export succesvol", Toast.LENGTH_LONG).show();
                Log.d("Response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error:" + error.getMessage() , Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }){
            @Override
            public byte[] getBody() throws AuthFailureError {
                return NotificationJSON.getBytes();
            }
        };
        requestQueue.add(stringRequest);
    }

    static class SettingsView {

        @BindView(R.id.importButton)
        Button importButton;

        @BindView(R.id.exportButton)
        Button exportButton;

        @BindView(R.id.importField)
        EditText importField;

        SettingsView(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
