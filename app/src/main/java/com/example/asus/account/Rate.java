package com.example.asus.account;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class Rate extends AppCompatActivity implements Runnable {//开启子线程接口
    private static final String TAG = "Rate";
    TextView rate;
    EditText rmb;
    Handler handler;
    String updateDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rate = (TextView) findViewById(R.id.textView1);
        rmb = (EditText) findViewById(R.id.editText);

        final SharedPreferences sharedPerferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        dollar = sharedPerferences.getFloat("dollar_rate", 0.0f);
        euro = sharedPerferences.getFloat("euro_rate", 0.0f);
        won = sharedPerferences.getFloat("won_rate", 0.0f);
        updateDate = sharedPerferences.getString("update_date", "");

        final Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        Log.i(TAG, "onCreate:sp dollar=" + dollar);
        Log.i(TAG, "onCreate:sp euro=" + euro);
        Log.i(TAG, "onCreate:sp won" + won);
        Log.i(TAG, "onCreate:sp update_date" + updateDate);
        Log.i(TAG, "onCreate:sp todayStr=" + todayStr);

        if (!todayStr.equals(updateDate)) {
            Log.i(TAG, "onCreate:需要更新");
            Thread t = new Thread(this); //开启子线程
            t.start();//启动线程
        } else {
            Log.i(TAG, "onCreate:不需要更新");
        }

        //借用handler
        handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 5) {//根据参数查看是哪个线程的内容
                    Bundle bdl = (Bundle) msg.obj;
                    dollar = bdl.getFloat("dollarrate");
                    euro = bdl.getFloat("eurorate");
                    won = bdl.getFloat("wonrate");

                    Log.i(TAG, "handleMessage:dollar:" + dollar);
                    Log.i(TAG, "handleMessage:euro:" + euro);
                    Log.i(TAG, "handleMessage:won:" + won);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date", todayStr);
                    editor.commit();//editor.apply();
                    Toast.makeText(Rate.this, "汇率已更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }

    float dollar = 6.72f, euro = 7.54f, won = 0.0059f;

    public void btnAdd_d(View btn) {
        count(dollar);
        //View btn 用于标记响应按钮调用的对象
    }

    public void btnAdd_e(View btn) {
        count(euro);
    }

    public void btnAdd_w(View btn) {
        count(won);
    }

    public void count(double number) {
        Log.i("show", "inc=");
        String str = rmb.getText().toString();
        double show = 0;
        if (str.length() > 0) {
            show = Double.parseDouble(str);
            show = show / number;
            float result = (float) show;
            rate.setText("转换结果是：" + result);
        } else
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
    }

    public void openMain(View btn) {
        Intent main = new Intent(this, MainActivity.class);
        main.putExtra("dollar_rate_key", dollar);
        main.putExtra("euro_rate_key", euro);
        main.putExtra("won_rate_key", won);

        Log.i(TAG, "openOne:dollar=" + dollar);
        Log.i(TAG, "openOne:euro=" + euro);
        Log.i(TAG, "openOne:won=" + won);

        startActivityForResult(main, 1);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_set) {
            openMain();
        } else if (item.getItemId() == R.id.open_list) {
            //打开列表窗口
            Intent list = new Intent(this, MyList2Activity.class);
            startActivity(list);
        }
        return super.onOptionsItemSelected(item);
    }

    private void openMain() {
        Intent main = new Intent(this, MainActivity.class);
        main.putExtra("dollar_rate_key", dollar);
        main.putExtra("euro_rate_key", euro);
        main.putExtra("won_rate_key", won);

        Log.i(TAG, "openOne:dollar=" + dollar);
        Log.i(TAG, "openOne:euro=" + euro);
        Log.i(TAG, "openOne:won=" + won);

        startActivityForResult(main, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == 2) {
            Bundle bundle = data.getExtras();
            dollar = bundle.getFloat("keydollar", 0.1f);
            euro = bundle.getFloat("keyeuro", 0.1f);
            won = bundle.getFloat("keywon", 0.1f);
            Log.i(TAG, "onActivityResult:dollar=" + dollar);
            Log.i(TAG, "onActivityResult:euro=" + euro);
            Log.i(TAG, "onActivityResult:won=" + won);

            SharedPreferences sharedPerferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPerferences.edit();
            editor.putFloat("dollar_rate", dollar);
            editor.putFloat("euro_rate", euro);
            editor.putFloat("won_rate", won);
            editor.commit();
            Log.i(TAG, "onCreateResult:数据已保存到sharedPreferences");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void run() {
        Log.i(TAG, "run:run()....");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //用户保存获取的汇率
        Bundle bundle = new Bundle();


        //获取网络数据
       /* URL url = null;
        try {
            url = new URL("http://www.usd-cny.com/bankofChina.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();

            String html = inputStream2String(in);
            Log.i(TAG, "run:html=" + html);
            Document doc =Jsoup.parse(html);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        bundle = getFormBOC();

        //bundle中保存所获取的汇率
        //获取msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what=5;
        //msg.obj = "Hello from run()";
        msg.obj = bundle;
        handler.sendMessage(msg);

    }

    private Bundle getFormBOC() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            //doc =Jsoup.parse(html);
            Log.i(TAG, "run:" + doc.title());
            Elements tables = doc.getElementsByTag("table");

            /*for(Element table :tables){
                Log.i(TAG,"run:table["+i+"]="+table);
                i++;
            }*/
            Element table2 = tables.get(1);//定位table
            //获取TD中的数据
            Elements tds = table2.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 8) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);

                String str1 = td1.text();
                String val = td2.text();
                Log.i(TAG, "run:" + str1 + "==>" + val);

                if ("美元".equals(str1)) {
                    bundle.putFloat("dollarrate", 100f / Float.parseFloat(val));
                } else if ("欧元".equals(str1)) {
                    bundle.putFloat("eurorate", 100f / Float.parseFloat(val));
                } else if ("韩国元".equals(str1)) {
                    bundle.putFloat("wonrate", 100f / Float.parseFloat(val));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private Bundle getFormUsdCny() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofChina.htm").get();
            //doc =Jsoup.parse(html);
            Log.i(TAG, "run:" + doc.title());
            Elements tables = doc.getElementsByTag("table");

            /*for(Element table :tables){
                Log.i(TAG,"run:table["+i+"]="+table);
                i++;
            }*/
            Element table6 = tables.get(5);
            // Log.i(TAG,"run:table6="+table6);
            //获取TD中的数据
            Elements tds = table6.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i += 8) {
                Element td1 = tds.get(i);
                Element td2 = tds.get(i + 5);
                Log.i(TAG, "run:" + td1.text() + "==>" + td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if ("美元".equals(str1)) {
                    bundle.putFloat("dollarrate", 100f / Float.parseFloat(val));
                } else if ("欧元".equals(str1)) {
                    bundle.putFloat("eurorate", 100f / Float.parseFloat(val));
                } else if ("韩国元".equals(str1)) {
                    bundle.putFloat("wonrate", 100f / Float.parseFloat(val));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "UTF-8");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}



