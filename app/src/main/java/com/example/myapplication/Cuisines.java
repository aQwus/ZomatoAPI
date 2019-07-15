package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class Cuisines extends AppCompatActivity {

    private RecyclerView recyclerView;

    private CuisineAdapter adapter;
    private int id;
    private ArrayList<String> nameList = new ArrayList<>();

    //Declare a private RequestQueue variable
    private RequestQueue requestQueue;
    private static Cuisines mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisines);

        recyclerView = findViewById(R.id.recyclerView);
        mInstance = this;

        id = getIntent().getIntExtra("id",1);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Cuisines.this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //obtaining list of cuisines using the Zomato api
        String url = "https://developers.zomato.com/api/v2.1/cuisines?city_id=" + id;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Success callback

                        try {
                            JSONArray results = (JSONArray) response.get("cuisines");
                            for(int i=0;i<results.length();i++){
                                JSONObject cuisineObj = (JSONObject) results.get(i);
                                JSONObject obj = (JSONObject) cuisineObj.get("cuisine");
                                String name = (String) obj.get("cuisine_name");
                                nameList.add(name);
                            }

                            adapter = new CuisineAdapter(nameList);
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Cuisines.this, "sorry, failed to retrieve cities", Toast.LENGTH_SHORT).show();
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
        Cuisines.getInstance().addToRequestQueue(jsonObjReq, "getRequest");
    }

    public static synchronized Cuisines getInstance() {
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
