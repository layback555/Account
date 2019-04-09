package com.example.asus.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class  MainActivity extends AppCompatActivity {
    public final String TAG="MainActivity";

    EditText dollartext;
    EditText eurotext;
    EditText wontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       Intent intent= getIntent();
       float dollar2=intent.getFloatExtra("dollar_rate_key",0.0f);
        float euro2=intent.getFloatExtra("euro_rate_key",0.0f);
        float won2=intent.getFloatExtra("won_rate_key",0.0f);

        Log.i(TAG,"onCreate:dollar2="+dollar2);
        Log.i(TAG,"onCreate:euro2="+euro2);
        Log.i(TAG,"onCreate:won2="+won2);

        dollartext=(EditText)findViewById(R.id.dollar_rate);
        eurotext=(EditText)findViewById(R.id.euro_rate);
        wontext=(EditText)findViewById(R.id.won_rate);

        dollartext.setText(String.valueOf(dollar2));
        eurotext.setText(String.valueOf(euro2));
        wontext.setText(String.valueOf(won2));
    }
    public void save(View btn){
        Log.i(TAG,"save:");
        float newdollar=Float.parseFloat(dollartext.getText().toString());
        float neweuro=Float.parseFloat(eurotext.getText().toString());
        float newwon=Float.parseFloat(wontext.getText().toString());
        Log.i(TAG,"save:获取到新的值");
        Log.i(TAG,"save:newdollar="+newdollar);
        Log.i(TAG,"save:neweuro="+neweuro);
        Log.i(TAG,"save:newwon="+newwon);

        Intent intent=getIntent();
        Bundle bdl=new Bundle();
        bdl.putFloat("keydollar",newdollar);
        bdl.putFloat("keyeuro",neweuro);
        bdl.putFloat("keywon",newwon);
        intent.putExtras(bdl);
        setResult(2,intent);
        finish();
    }

}
