package com.pickle.googleservicetester;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class GCMFirebaseService extends FirebaseMessagingService {
    private static String TAG = "FIREBASE_MESSAGING_SERVICE";

    public GCMFirebaseService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From : " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0){
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if(remoteMessage.getNotification() != null)
        {
            Log.d(TAG, "Message Notification body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d(TAG, "Refreshed Token: " + s);
    }
}
