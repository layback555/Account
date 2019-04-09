package com.example.asus.account;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Rate extends AppCompatActivity{
    private static final String TAG = "Rate";
    TextView rate;
     EditText rmb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rate=(TextView)findViewById(R.id.textView1);
        rmb=(EditText)findViewById(R.id.editText);
        }
        float dollar=6.72f,euro=7.54f,won=0.0059f;

     public void btnAdd_d(View btn){ count(dollar);
     }
     public void btnAdd_e(View btn){ count(euro);
     }
    public void btnAdd_w(View btn){count(won);
    }
    public void count(double number){
        Log.i("show","inc=");
        String str=rmb.getText().toString();
        double show=0;
        if(str.length()>0) {
            show =Double.parseDouble(str);
            show=show/number;
            float result=(float)show;
            rate.setText("转换结果是："+result);
        }
        else
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
    }
    public void openOne(View btn){
         Intent main=new Intent(this,MainActivity.class);
         main.putExtra("dollar_rate_key",dollar);
        main.putExtra("euro_rate_key",euro);
        main.putExtra("won_rate_key",won);

        Log.i(TAG,"openOne:dollar="+dollar);
        Log.i(TAG,"openOne:euro="+euro);
        Log.i(TAG,"openOne:won="+won);

        startActivityForResult(main,1);
    }

    public boolean onCreateOptionsMenu(Menu menu){
         getMenuInflater().inflate(R.menu.rate,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         if(item.getItemId()==R.id.menu_set){
             Intent main=new Intent(this,MainActivity.class);
             main.putExtra("dollar_rate_key",dollar);
             main.putExtra("euro_rate_key",euro);
             main.putExtra("won_rate_key",won);

             Log.i(TAG,"openOne:dollar="+dollar);
             Log.i(TAG,"openOne:euro="+euro);
             Log.i(TAG,"openOne:won="+won);

             startActivityForResult(main,1);

         }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(requestCode==1&& resultCode==2){
          Bundle bundle=data.getExtras();
          dollar=bundle.getFloat("keydollar",0.1f);
          euro=bundle.getFloat("keyeuro",0.1f);
          won=bundle.getFloat("keywon",0.1f);
          Log.i(TAG,"onActivityResult:dollar="+dollar);
           Log.i(TAG,"onActivityResult:euro="+euro);
           Log.i(TAG,"onActivityResult:won="+won);
       }
       super.onActivityResult(requestCode, resultCode, data);
    }
}


