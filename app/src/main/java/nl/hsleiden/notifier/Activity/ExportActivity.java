package nl.hsleiden.notifier.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.hsleiden.notifier.Model.Notification;
import nl.hsleiden.notifier.Model.gsonDateTimeSerializer;
import nl.hsleiden.notifier.R;

public class ExportActivity extends BaseActivity {

    ExportView view;

    RequestQueue requestQueue;
    Gson gson;

    private String PasteBinAPIKey = "0cfd15b41bed612f5e32895fee77d22b";
    private String PasteBinURL = "http://pastebin.com/api/api_post.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        view = new ExportView(view_stub);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(DateTime.class, new gsonDateTimeSerializer())
                .serializeNulls()
                .create();
    }

    public void exportItems(View v) {

        List<Notification> notifications = new Select()
                .from(Notification.class)
                .execute();
        final String NotificationJSON = gson.toJson(notifications,  new TypeToken<List<Notification>>(){}.getType());

        String url = PasteBinURL + "?api_option=paste&api_dev_key=" + PasteBinAPIKey + "&api_paste_code=" + NotificationJSON;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, PasteBinURL , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                view.exportField.setText(response);
                view.exportField.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("api_option","paste");
                params.put("api_dev_key",PasteBinAPIKey);
                params.put("api_paste_code",NotificationJSON);
                params.put("api_paste_private","1");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    static class ExportView {

        @BindView(R.id.exportField)
        EditText exportField;

        ExportView(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
