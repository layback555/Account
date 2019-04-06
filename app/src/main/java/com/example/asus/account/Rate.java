package com.example.asus.account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Rate extends AppCompatActivity{
     TextView rate;
     EditText rmb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rate=(TextView)findViewById(R.id.textView1);
        rmb=(EditText)findViewById(R.id.editText);
        }
     public void btnAdd_d(View btn){count(6.72);
     }
     public void btnAdd_e(View btn){ count(7.54);
     }
    public void btnAdd_w(View btn){count(0.0059);
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
     }


