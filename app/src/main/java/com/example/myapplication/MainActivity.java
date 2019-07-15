package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText text;
    ImageButton searchButton;
    private RecyclerView cityList;
    private TextView no_city;

    private ArrayList<Integer> idList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();

    //Declare a private RequestQueue variable
    private RequestQueue requestQueue;
    private static MainActivity mInstance;

    private CityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);
        searchButton = findViewById(R.id.searchButton);
        cityList = findViewById(R.id.cityList);
        no_city = findViewById(R.id.no_city);
        mInstance = this;

        no_city.setVisibility(View.GONE);
        cityList.setVisibility(View.VISIBLE);

        cityList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = text.getText().toString().trim();
                nameList.clear();
                idList.clear();
                if(!str.equals("")){

                    String url = "https://developers.zomato.com/api/v2.1/cities?q=" + str;

                    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                            url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    //Success callback

                                    try {
                                        JSONArray results = (JSONArray) response.get("location_suggestions");
                                        if(results.length() > 0) {
                                            no_city.setVisibility(View.GONE);
                                            cityList.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < results.length(); i++) {
                                                JSONObject locationObj = (JSONObject) results.get(i);
                                                int id = (int) locationObj.get("id");
                                                String city = (String) locationObj.get("name") + ", " + locationObj.get("country_name");

                                                idList.add(id);
                                                nameList.add(city);
                                            }

                                            adapter = new CityAdapter(nameList, idList);
                                            cityList.setAdapter(adapter);
                                        } else {
                                            no_city.setVisibility(View.VISIBLE);
                                            cityList.setVisibility(View.GONE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, "sorry, failed to retrieve cities", Toast.LENGTH_SHORT).show();
                                }
                            })
                        {
                            /** Passing some request headers* */
                            @Override
                            public Map getHeaders() throws AuthFailureError {
                                HashMap headers = new HashMap();
                                headers.put("user-key", "b20128649787be0b59037747f547e6b9");
                                return headers;
                            }
                        };

                    // Adding the request to the queue along with a unique string tag
                    MainActivity.getInstance().addToRequestQueue(jsonObjReq, "getRequest");

                } else Toast.makeText(MainActivity.this, "Please enter a City name first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static synchronized MainActivity getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        return requestQueue;
    }

    public void addToRequestQueue(Request request, String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public void cancelAllRequests(String tag) {
        getRequestQueue().cancelAll(tag);
    }
}
