package com.greg.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.greg.go4lunch.MainActivity;
import com.greg.go4lunch.R;
import com.greg.go4lunch.api.WorkmateHelper;
import com.greg.go4lunch.model.Workmate;
import com.greg.go4lunch.viewmodel.SharedViewModel;

import java.util.ArrayList;

public class NotificationsService extends FirebaseMessagingService {

    final int NOTIFICATION_ID = 218;
    final String NOTIFICATION_TAG = "FIRE_BASE_GO4LUNCH";
    public static final String TAG = "NotificationsService";
    private SharedViewModel mSharedViewModel;
    private String mMessage;
    public static final String NOTIFICATIONS_PREF = "Notifications preferences";
    //private Context context;
    //String[] listOfJoiningWorkmates;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        SharedPreferences sharedPreferences = getSharedPreferences(NOTIFICATIONS_PREF, MODE_PRIVATE);
        boolean receiveNotification = sharedPreferences.getBoolean("silentMode", false);

        if (receiveNotification){
            if (remoteMessage.getNotification() != null){
                mMessage = remoteMessage.getNotification().getBody();
                //----------------------------- Show notification after received message ---------------
                notificationData();
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------- Data to send into notification ---------------------------------
    //----------------------------------------------------------------------------------------------

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    private void notificationData(){
        WorkmateHelper.getWorkmate(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Workmate currentUser = documentSnapshot.toObject(Workmate.class);
                String idRestaurant = currentUser.getIdPickedRestaurant();
                String restaurant = currentUser.getPickedRestaurant();
                String address = currentUser.getAddressRestaurant();

                String notificationMessage = "Today you eat at " + restaurant + ": " + address;
                mMessage = notificationMessage;
                sendVisualNotification(mMessage);
                //-------------------------- Ok until this part ------------------------------------
                //Context who doesn't work:
                //getApplication()
                //getApplicationContext()
                //NotificationsService.this
                //context
                //getBaseContext()

                //mSharedViewModel = new ViewModelProvider().get(SharedViewModel.class);
                //mSharedViewModel.initJoiningWorkmates(/*NotificationsService.this*/, idRestaurant);
                //mSharedViewModel.getJoiningWorkmatesData().observe(/*(LifecycleOwner) getApplication()*/, new Observer<ArrayList<Workmate>>() {
                //    @Override
                //    public void onChanged(ArrayList<Workmate> workmates) {
                //        if (!workmates.isEmpty()){
                //            String item = "";
                //            for(int i = 0; i < workmates.size(); i++){
                //                item = item + workmates.get(i);
//
                //                if (i != workmates.size() - 1){
                //                    item = item + ", ";
                //                    String notificationMessage = "Today you eat at" + restaurant + ": " + address + " with " + workmates;
                //                    mMessage = notificationMessage;
                //                    sendVisualNotification(mMessage);
                //                }
                //            }
                //        }
                //        else{
                //            String notificationMessage = "Today you eat at " + restaurant + ": " + address;
                //            mMessage = notificationMessage;
                //            sendVisualNotification(mMessage);
                //        }
                //    }
                //});

            }
        });
    }

    //mSharedViewModel.initJoiningWorkmates(NotificationsService.this, idRestaurant);
    //mSharedViewModel.getJoiningWorkmatesData().observe((LifecycleOwner) getApplication(), new Observer<ArrayList<Workmate>>() {
    //    @Override
    //    public void onChanged(ArrayList<Workmate> workmates) {
    //        if (!workmates.isEmpty()){
    //            String notificationMessage = "Today you eat at" + restaurant; //+ ": " + /*Add adresse in workamtes ???*/
    //            //" with " +
    //           mMessage = notificationMessage;
    //            sendVisualNotification(mMessage);
    //        }
    //    }
    //});


    //----------------------------------------------------------------------------------------------
    //----------------------------- Send notification ----------------------------------------------
    //----------------------------------------------------------------------------------------------

    public void sendVisualNotification(String messageBody){

        //----------------------------- Create an Intent that will be shown when user will click on the Notification ------
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        //----------------------------- Create a style for the notification ------------------------
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.notification_title));
        inboxStyle.addLine(messageBody);

        //----------------------------- Create a Channel -------------------------------------------
        String channelId = getString(R.string.default_notification_channel_id);

        //----------------------------- Build a Notification object --------------------------------
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo_go4lunch)
                        .setContentTitle(getString(R.string.app_name)) ///<----
                        .setContentText(getString(R.string.notification_title)) //<-- May be change this for notificationMessage
                        //.setContentText(mNotificationInfo)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        //----------------------------- Add the Notification to the Notification Manager and show it -------------
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //----------------------------- Support Version >= Android 8 -------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "Message from Go4Lunch";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        //----------------------------- Show notification ------------------------------------------
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}
