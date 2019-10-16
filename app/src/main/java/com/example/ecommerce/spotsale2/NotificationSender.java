package com.example.ecommerce.spotsale2;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ecommerce.spotsale2.DatabaseClasses.Users;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationSender {

    public static void sendNotification(String body, Users user) throws JSONException {

        JSONObject obj = new JSONObject();
        obj.put("to", "/topic/" + user.getEmail() );
        //Another object inside our created obj
        JSONObject notifObj = new JSONObject();
        notifObj.put("title", "New Buyer");
        notifObj.put("body", body);
        notifObj.put("click_action", ".CatalogActivity");
        obj.put("notification", notifObj);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.MESSAGING_URL, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


    }

}
