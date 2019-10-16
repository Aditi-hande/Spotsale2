package com.example.ecommerce.spotsale2;

import android.app.AlertDialog;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if(remoteMessage.getNotification() != null) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage(remoteMessage.getFrom() + ": " + remoteMessage.getNotification().getBody());
            alertBuilder.create().show();
        }
    }
}
