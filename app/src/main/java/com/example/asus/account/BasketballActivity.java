package com.example.asus.account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class BasketballActivity extends AppCompatActivity {
  TextView score,score1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basketball);
        score = (TextView)findViewById(R.id.textView2);
        score1 = (TextView)findViewById(R.id.textView4);
        score.setText("0");
        score1.setText("0");
    }

     public void btnAdd1(View btn){
        showScore(1);
    }
    public void btnAdd2(View btn){
        showScore(2);
    }
    public void btnAdd3(View btn){
        showScore(3);
    }
    public void btnAdd11(View btn){
        showScore1(1);
    }
    public void btnAdd22(View btn){
        showScore1(2);
    }
    public void btnAdd33(View btn){
        showScore1(3);
    }
    public void btnReset(View btn){
        score.setText("0");
        score1.setText("0");
    }

    private void showScore(int inc){
        Log.i("show","inc1="+inc);
        String oldScore=(String) score.getText();
        int newScore=Integer.parseInt(oldScore)+inc;
        score.setText(""+newScore);
    }
    private void showScore1(int inc1){
        Log.i("show","inc="+inc1);
        String oldScore1=(String) score1.getText();
        int newScore1=Integer.parseInt(oldScore1)+inc1;
        score1.setText(""+newScore1);
    }
}