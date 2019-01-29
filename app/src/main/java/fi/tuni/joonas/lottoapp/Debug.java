package fi.tuni.joonas.lottoapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;




public class Debug{
    public static int DEBUG_LEVEL;
    public static boolean DEBUG_TOAST;
    public static Context host;

    public static void print(String tag, String msg, int level){
        if(BuildConfig.DEBUG && level <= DEBUG_LEVEL){
            Log.d(tag, msg);
        }
        if(BuildConfig.DEBUG && level <= DEBUG_LEVEL && DEBUG_TOAST){
            Toast.makeText(host.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }

    }

    public static void loadDebug(Context host){
        Debug.host = host;
        DEBUG_LEVEL = host.getResources().getInteger(R.integer.debugLevel);
        DEBUG_TOAST = host.getResources().getBoolean(R.bool.debugToast);
        Log.d("DEBUG", "Debug Level: " + DEBUG_LEVEL);

    }
}

