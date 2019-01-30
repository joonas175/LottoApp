package fi.tuni.joonas.lottoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends MyBaseActivity {

    TreeSet<Integer> lottoNumbers;
    MyReceiver myReceiver;
    int interval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Debug.loadDebug(this);

        interval = 100;

        setButtons();

        lottoNumbers = new TreeSet<Integer>();

        myReceiver = new MyReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter("asd"));
    }

    public void setButtons(){
        TableLayout lottoTable = findViewById(R.id.lottoNumbers);

        int tableChildrenCount = lottoTable.getChildCount();
        int lottoNumber = 1;
        for(int i = 0; i < tableChildrenCount; i++){
            TableRow row = (TableRow) lottoTable.getChildAt(i);
            for(int j = 0; j < row.getChildCount(); j++){
                Button butt = (Button) row.getChildAt(j);
                butt.setText(lottoNumber + "");
                lottoNumber++;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void lottoNumberClicked(View v){
        Button button = (Button) v;
        Debug.print(TAG, button.getText().toString(), 1);

        int numberSelected = Integer.parseInt(button.getText().toString());

        if(lottoNumbers.size() < 7 && lottoNumbers.add(numberSelected)){
            button.setTextColor(Color.MAGENTA);
        } else if(lottoNumbers.removeIf((t) -> intIsEqual(t, numberSelected))){
            button.setTextColor(Color.BLACK);
        }

    }

    public boolean intIsEqual(int a, int b){
        if(a == b){
            return true;
        } else {
            return false;
        }
    }


    public void feelingLucky(View v){


        Intent intent = new Intent(this, LottoService.class);


        intent.putExtra("lottoNumbers", IntegerSetToIntArray(lottoNumbers));
        startService(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case(R.id.incDiff):
                interval+=100;
                Debug.print(TAG, "Interval set to " + interval,1);
                Toast.makeText(this, "Interval set to " + interval, Toast.LENGTH_LONG).show();
                break;
            case(R.id.decDiff):
                if(interval >= 100){
                    interval-=100;
                }
                Debug.print(TAG, "Interval set to " + interval ,1);
                Toast.makeText(this, "Interval set to " + interval, Toast.LENGTH_LONG).show();
            break;
        }

        return true;

    }

    public int[] IntegerSetToIntArray(Set<Integer> set){
        int[] lottoNumbersAsInt = new int[7];
        Object[] lottoNumbersTemp = set.toArray();

        for(int i = 0; i < lottoNumbersTemp.length; i++){
            lottoNumbersAsInt[i] = (int) lottoNumbersTemp[i];
        }

        return lottoNumbersAsInt;
    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras().getBundle("bundle");
            int weeks = bundle.getInt("weeks");
            boolean won = !(bundle.getBoolean("running"));
            if(won){
                Toast.makeText(MainActivity.this, "Lottery won!", Toast.LENGTH_LONG).show();
            }
            //Debug.print(TAG, "Weeks passed: " + weeks, 1);
        }
    }

    public void onDestroy(){
        Debug.print(TAG, "onDestroy()", 1);
        if(myReceiver != null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        }
        super.onDestroy();
    }

}
