package nl.hsleiden.notifier.Activity;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.hsleiden.notifier.Model.Notification;
import nl.hsleiden.notifier.Model.gsonDateTimeSerializer;
import nl.hsleiden.notifier.R;

public class ImportActivity extends BaseActivity {

    ImportView view;

    RequestQueue requestQueue;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        view = new ImportView(view_stub);


        requestQueue = Volley.newRequestQueue(getApplicationContext());
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(DateTime.class, new gsonDateTimeSerializer())
                .serializeNulls()
                .create();;
    }

    public void importItems(View v) {
        final String url = view.importField.getText().toString();
//        if (Patterns.WEB_URL.matcher(url).matches()) {
//            Toast.makeText(getApplicationContext(), "Please enter a valid URL to import from", Toast.LENGTH_LONG).show();
//            return;
//        }
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
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);


    }

    static class ImportView {

        @BindView(R.id.importButton)
        Button importButton;

        @BindView(R.id.importField)
        EditText importField;

        ImportView(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
