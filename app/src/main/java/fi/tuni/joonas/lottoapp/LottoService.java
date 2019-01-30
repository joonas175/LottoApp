package fi.tuni.joonas.lottoapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Arrays;

public class LottoService extends Service {

    private boolean running;
    int interval;

    int[] lottoNumbers;

    int difficulty;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        interval = 0;
        difficulty = 7;

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

                if(intArraysEqual(lottoNumbers, lottoRNGMachine())){
                    running = false;

                }

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
}
