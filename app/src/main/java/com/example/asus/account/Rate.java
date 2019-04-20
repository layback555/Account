package com.example.asus.account;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.LogRecord;

public class Rate extends AppCompatActivity implements Runnable{//开启子线程接口
    private static final String TAG = "Rate";
    TextView rate;
     EditText rmb;
     Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rate=(TextView)findViewById(R.id.textView1);
        rmb=(EditText)findViewById(R.id.editText);

        SharedPreferences sharedPerferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
        dollar=sharedPerferences.getFloat("dollar_rate",0.0f);
        euro=sharedPerferences.getFloat("euro_rate",0.0f);
        won=sharedPerferences.getFloat("won_rate",0.0f);
        Log.i(TAG,"onCreate:sp dollar_rate="+dollar);
        Log.i(TAG,"onCreate:sp euro_rate="+euro);
        Log.i(TAG,"onCreate:sp won_rate"+won);

        Thread t=new Thread(this); //开启子线程
        t.start();//启动线程
        //借用handler
       handler = new Handler(){
           public void handleMessage(Message msg){
             if(msg.what==5){//根据参数查看是哪个线程的内容
                 String str=(String) msg.obj;
                 Log.i(TAG,"handlerMessage:getMessage msg="+str);
                 rate.setText(str);
             }
             super.handleMessage(msg);

         }

         };

    }
        float dollar=6.72f,euro=7.54f,won=0.0059f;

     public void btnAdd_d(View btn){ count(dollar);
     //View btn 用于标记响应按钮调用的对象
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

           SharedPreferences sharedPerferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
           SharedPreferences.Editor editor=sharedPerferences.edit();
           editor.putFloat("dollar_rate",dollar);
           editor.putFloat("euro_rate",euro);
           editor.putFloat("won_rate",won);
           editor.commit();
           Log.i(TAG,"onCreateResult:数据已保存到sharedPreferences");
       }
       super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
        Log.i(TAG,"run:run()....");
        for(int i=1;i<6;i++){
            Log.i(TAG,"run:i="+i);
            //当前停止两秒钟//
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //获取message对象，返回主线程
        Message msg=handler.obtainMessage(5);
        msg.obj="Hello from run()";
        handler.sendMessage(msg);

        //获取网络数据
        URL url= null;
        try {
            url = new URL("http://www.boc.cn/sourcedb/whpj/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection http= (HttpURLConnection) url.openConnection();
            InputStream in =http.getInputStream();//获得一个输入流
            String html=inputStream2String(in);
            Log.i(TAG,"run:html:"+html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String inputStream2String(InputStream inputStream) throws IOException {
         final int bufferSize=1024;
         final char[] buffer = new char[bufferSize];
         final StringBuilder out =new StringBuilder();
         Reader in=new InputStreamReader(inputStream,"UTF-8");
         for(;;){
             int rsz=in.read(buffer,0,buffer.length);
             if(rsz<0)
                 break;
             out.append(buffer,0,rsz);
         }
         return out.toString();
    }
}


