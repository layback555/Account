package com.example.asus.account;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements Runnable,AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "MyList2Activity";
    Handler handler;
    private ArrayList<HashMap<String, String>> listItems; // 存放文字、图片信息
    private SimpleAdapter listItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();

        //MyAdapter myAdapter = new MyAdapter(this, R.layout.list_item, listItems);
        //this.setListAdapter(myAdapter);
        this.setListAdapter(listItemAdapter);

        Thread t=new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    listItems= (ArrayList<HashMap<String, String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MyList2Activity.this, listItems,//listItems数据源
                            R.layout.list_item,//ListItem的XML布局实现
                            new String[]{"ItemTitle", "ItemDetail"},
                            new int[]{R.id.itemTitle, R.id.itemDetail}
                    );
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);
    }

    private void initListView() {
        listItems = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", "Rate： " + i); // 标题文字
            map.put("ItemDetail", "detail:" + i); // 详情描述
            listItems.add(map);
        }  // 生成适配器的 Item 和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItems, // listItems 数据源
                R.layout.list_item, // ListItem 的 XML 布局实现
                new String[]{"ItemTitle", "ItemDetail"},
                new int[]{R.id.itemTitle, R.id.itemDetail});
    }
    @Override
    public void run() {
        List<HashMap<String,String>> retList=new ArrayList<HashMap<String, String>>();
        Document doc = null;
        try {
            Thread.sleep(1000);
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            Log.i(TAG, "run:" + doc.title());
            Elements tables = doc.getElementsByTag("table");
            Element table2 = tables.get(1);//需要运行后获得第几个
            Log.i(TAG, "run:table6=" + table2);
            //获取TD中的数据
            Elements tds = table2.getElementsByTag("td");
            for (int r = 0; r < tds.size(); r += 8) {
                Element td1 = tds.get(r);//获取第一列，即所有的币种
                Element td2 = tds.get(r + 5);//获取第六列,即一个币种的汇率

                String str1 = td1.text();
                String val = td2.text();
                Log.i(TAG,"run:"+str1+"==>"+val);
                HashMap<String,String>map=new HashMap<String,String>();
                map.put("ItemTitle",str1);
                map.put("ItemDetil",val);
                retList.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message msg=handler.obtainMessage(7);
    msg.obj=retList;
    handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG,"OnItemClick:parent="+parent);
        Log.i(TAG,"OnItemClick:view="+view);
        Log.i(TAG,"OnItemClick:position="+position);
        Log.i(TAG,"OnItemClick:id="+id);
        HashMap<String,String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr=map.get("ItemTitle");
        String detailStr=map.get("ItemDetail");
        Log.i(TAG,"OnItemClick:titleStr"+titleStr);
        Log.i(TAG,"OnItemClick:DetailStr"+detailStr);

        TextView title=(TextView)view.findViewById(R.id.itemTitle);
        TextView detail=(TextView)view.findViewById(R.id.itemDetail);
        String title2=String.valueOf(title.getText());
        String detail2=String.valueOf(detail.getText());
        Log.i(TAG,"OnItemClick:title2"+title2);
        Log.i(TAG,"OnItemClick:detail2"+detail2);
     //打开新的列表，传入参数
        Intent rateCalc=new Intent(this,RateCalcActivity.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalc);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.i(TAG,"onItemLongClick:长按列表项：position"+position);

        AlertDialog.Builder bulider=new AlertDialog.Builder(this);
        bulider.setTitle("提示").setMessage("请确认是否要删除数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,"onClick:对话框事件处理：");
                listItems.remove(position);
                listItemAdapter.notifyDataSetChanged();
            }
        })
                .setNegativeButton("否",null);
        bulider.create().show();
        Log.i(TAG,"onItemLongClick:size="+listItems.size());
        return false;
    }
}
