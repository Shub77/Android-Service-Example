package com.javatechig.serviceexample;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class HelloService extends Service {

    private static final String TAG = "HelloService";

    private boolean isRunning  = false;

    private BroadcastReceiver br;
    private IntentFilter mIntentFilter;
    private String chargingMode = "";
    private boolean firstrun = true;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyMgr;

    @Override
    public void onCreate() {
        Log.d(TAG, "Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Service onStartCommand");


        br = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {


                //e lancia il thread con il wait

                //new NotifyWaiter().execute(mNmanager);

                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;


                if (isCharging) {


                    Log.d(TAG, "Battery is charging");


                    // How are we charging?
                    int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                    boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
                    boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

                    if (usbCharge) {
                        Log.d(TAG, "USB battery charging");
                        if (!chargingMode.contains("USB")) {
                            chargingMode = "USB";
                            if (!firstrun) {
                                PrepareLedNotification();
                            }
                            firstrun = false;
                        }

                        //synchronized (mNmanager) {
                        //mNmanager.notifyAll();
                        //}


                    } else if (acCharge) {
                        Log.d(TAG, "AC battery charging");
                        if (!chargingMode.contains("AC")) {
                            chargingMode = "AC";
                            if (!firstrun) {
                                PrepareLedNotification();
                            }
                            firstrun = false;
                        }


                    } else {
                        Log.d(TAG, "Unknown battery charging");
                        Log.d(TAG, "AC battery charging");
                        if (!chargingMode.contains("Unknown")) {
                            chargingMode = "Unknown";
                            if (!firstrun) {
                                PrepareLedNotification();
                            }
                            firstrun = false;
                        }
                    }
                } else {
                    Log.d(TAG, "Battery is not charging");
                    Log.d(TAG, "AC battery charging");
                    if (!chargingMode.contains("Not charging")) {
                        chargingMode = "Not charging";
                        if (!firstrun) {
                            PrepareLedNotification();
                        }
                        firstrun = false;
                    }
                }


            }
        };


        mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(br, mIntentFilter);






        /*

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {


                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }

                    if(isRunning){
                        Log.d(TAG, "Service running");
                    }
                }

                //Stop service once it finishes its task
                stopSelf();
            }
        }).start();

        */

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;
        unregisterReceiver(br);

        Log.d(TAG, "Service onDestroy");

    }

    private void PrepareLedNotification() {

        /*

        mNmanager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
        mNotification = new Notification();
        mNotification.flags = Notification.FLAG_SHOW_LIGHTS;
        mNotification.ledOnMS = 100000;
        mNotification.ledOffMS = 1;
        mNotification.defaults |= Notification.DEFAULT_VIBRATE;

        */

        long[] vibrate = new long[]{10, 100, 500};

        mBuilder = new NotificationCompat.Builder(this)
                //.setSmallIcon(R.drawable.notification_icon)
                //.setContentTitle("My notification")
                //.setContentText("Hello World!")
                //.setLights(Color.GRAY , 1000, 100)
                //.setAutoCancel(true)
                .setVibrate(vibrate);
        //Notification mNotification = mBuilder.build();


        // Sets an ID for the notification
        int mNotificationId = 1;
        // Gets an instance of the NotificationManager service

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        Log.d(TAG, "Vibrate");
        mNotifyMgr.notify(mNotificationId, mBuilder.build());




    }
}