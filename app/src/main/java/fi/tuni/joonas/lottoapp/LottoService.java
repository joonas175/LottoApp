package fi.tuni.joonas.lottoapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Arrays;

public class LottoService extends Service {

    private boolean running;
    int interval;

    int[] lottoNumbers;

    int weeks;

    int difficulty;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        interval = 0;
        difficulty = 5;
        weeks = 0;


        lottoNumbers = intent.getExtras().getIntArray("lottoNumbers");
        printNumbers();
        if(!(running)){
            LottoThread thread = new LottoThread();
            new Thread(thread).start();
        }


        return START_STICKY;
    }

    private void printNumbers() {
        String printtaus = "";
        for(Integer i : lottoNumbers){
            printtaus += i + " ";
        }
        Debug.print("LottoService","Received numbers: " + printtaus, 1);
    }

    class LottoThread implements Runnable{

        @Override
        public void run() {
            running = true;
            while(running){
                int[] rng = lottoRNGMachine();
                if(intArraysEqual(lottoNumbers, rng)){
                    running = false;
                    showNotification();

                    stopSelf();
                } else {
                    weeks++;

                }
                Bundle bundle = new Bundle();
                bundle.putInt("weeks", weeks);
                bundle.putBoolean("running", running);
                bundle.putIntArray("rngNumbers", rng);
                sendBroadcastToMain(bundle);

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public int[] lottoRNGMachine(){
            int[] random = new int[7];
            for(int i = 0; i < random.length; i++){
                random[i] = (int) (Math.random() * 39) + 1;
            }

            Arrays.sort(random);
            return random;

        }

        public boolean intArraysEqual(int[] a, int[] b){
            int amountOfSame = 0;
            for(int i = 0; i < a.length; i++){
                if(a[i] == b[i]){
                    amountOfSame++;
                }
            }

            if(amountOfSame >= difficulty){
                Debug.print("LottoService", "Player numbers: " + LottoService.intArrayToString(a), 1);
                Debug.print("LottoService", "Random numbers: " + LottoService.intArrayToString(b), 1);
                return true;
            }
            return false;
        }


    }

    @Override
    public void onCreate(){
        running = false;
    }

    public static String intArrayToString(int[] a){
        String aAsString = "";
        for(int i : a){
            aAsString += i + " ";
        }
        return aAsString;
    }

    public void showNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Lottery");
        mBuilder.setContentText("Lottery won!");

        Intent targetIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);


        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.setShowBadge(false);
            manager.createNotificationChannel(mChannel);
            mBuilder.setChannelId(CHANNEL_ID);
        }

        Notification noti = mBuilder.build();

        int mId = 1;
        manager.notify(mId, noti);
    }

    public void sendBroadcastToMain(Bundle bundle){

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        Intent i = new Intent("asd");
        i.putExtra("bundle", bundle);
        manager.sendBroadcast(i);
    }



}
