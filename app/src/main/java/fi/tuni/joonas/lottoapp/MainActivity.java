package fi.tuni.joonas.lottoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.Group;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class MainActivity extends MyBaseActivity {

    TreeSet<Integer> lottoNumbers;
    int[] lottoNumbersInt;
    MyReceiver myReceiver;
    int interval;
    ArrayList<Button> buttons;
    int difficulty;
    MenuItem checkedDiff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Debug.loadDebug(this);

        interval = 100;
        buttons = new ArrayList<Button>();
        setButtons();

        lottoNumbers = new TreeSet<Integer>();
        difficulty = 7;


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
                butt.setTextColor(Color.BLACK);
                buttons.add(butt);
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

        if(lottoNumbers.size() == 7){
            Intent intent = new Intent(this, LottoService.class);


            intent.putExtra("lottoNumbers", IntegerSetToIntArray(lottoNumbers));
            intent.putExtra("interval", interval);
            intent.putExtra("diff", difficulty);
            lottoNumbersInt = IntegerSetToIntArray(lottoNumbers);
            startService(intent);
        } else {
            Toast.makeText(this, "Pick 7 numbers!", Toast.LENGTH_LONG).show();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        checkedDiff = menu.findItem(R.id.diff7);
        checkedDiff.setChecked(true);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case(R.id.incDiff):
                interval+=20;
                Debug.print(TAG, "Interval set to " + interval,1);
                Toast.makeText(this, "Interval set to " + interval, Toast.LENGTH_LONG).show();
                break;
            case(R.id.decDiff):
                if(interval >= 40){
                    interval-=20;
                }
                Debug.print(TAG, "Interval set to " + interval ,1);
                Toast.makeText(this, "Interval set to " + interval, Toast.LENGTH_LONG).show();
                break;
            case(R.id.diff5): difficulty = 5; checkedDiff.setChecked(false); item.setChecked(true); checkedDiff = item; break;
            case(R.id.diff6): difficulty = 6; checkedDiff.setChecked(false); item.setChecked(true); checkedDiff = item; break;
            case(R.id.diff7): difficulty = 7; checkedDiff.setChecked(false); item.setChecked(true); checkedDiff = item; break;

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
            int[] rngNumbers = bundle.getIntArray("rngNumbers");
            flashButtons(rngNumbers);
            ((TextView) findViewById(R.id.weeks)).setText("Weeks passed: " + weeks);
            if(won){
                Toast.makeText(MainActivity.this, "Lottery won!", Toast.LENGTH_LONG).show();
            }
            //Debug.print(TAG, "Weeks passed: " + weeks, 1);
        }
    }

    private void flashButtons(int[] rngNumbers) {
        for(Button butt : buttons){
            butt.setTextColor(Color.BLACK);
        }
        for(Button butt : buttons){
            int buttonNumber = Integer.parseInt(butt.getText().toString());
            for(int num : lottoNumbersInt){
                if (buttonNumber == num) {
                    butt.setTextColor(Color.MAGENTA);
                }
            }
            for(int num : rngNumbers){
                if(buttonNumber == num){
                    butt.setTextColor(Color.BLUE);
                }
            }
        }
    }

    public void onDestroy(){
        Debug.print(TAG, "onDestroy()", 1);
        if(myReceiver != null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        }
        super.onDestroy();
    }

    public void stopLottoService(View v){
        Intent intent = new Intent(this, LottoService.class);
        stopService(intent);
    }

}
