package fi.tuni.joonas.lottoapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Debug.loadDebug(this);

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

        lottoNumbers = new TreeSet<Integer>();
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
                Debug.print(TAG, "Increasing difficulty" ,1);
                Toast.makeText(this, "Increasing difficulty", Toast.LENGTH_LONG).show();
                break;
            case(R.id.decDiff):
                Debug.print(TAG, "Decreasing difficulty" ,1);
                Toast.makeText(this, "Decreasing difficulty", Toast.LENGTH_LONG).show();
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

}
