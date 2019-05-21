package com.example.asus.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RateManager {
    private DBHelper dbHelper;
    private String TBNAME;
    public RateManager(Context context) {
        dbHelper = new DBHelper(context);
        TBNAME = DBHelper.TB_NAME;//通过类直接访问
    }
    public void add(RateItem item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();  //获得数据库
        ContentValues values = new ContentValues();
        values.put("curname", item.getCurName());  //把数据放到对象里边去
        values.put("currate", item.getCurRate());
        db.insert(TBNAME, null, values);
        db.close();
    }
    public void deleteAll(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME,null,null);
        db.close();
    }
    public void delete(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TBNAME, "ID=?", new String[]{String.valueOf(id)});
        db.close();
    }
    public void update(RateItem item){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("curname", item.getCurName());
        values.put("currate", item.getCurRate());
        db.update(TBNAME, values, "ID=?", new String[]{String.valueOf(item.getId())});
        db.close();
    }
    public void addAll(List<RateItem> list){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (RateItem item : list) {
            ContentValues values = new ContentValues();
            values.put("curname", item.getCurName());
            values.put("currate", item.getCurRate());
            db.insert(TBNAME, null, values);
        }
        db.close();
    }
        public List<RateItem> listAll(){
        List<RateItem> rateList = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase(); //获得只读数据库
        Cursor cursor = db.query(TBNAME, null, null, null, null, null, null);  //查询表里所有数据
        if(cursor!=null){  //当游标不为空
            rateList = new ArrayList<RateItem>();  //将其实例化
            while(cursor.moveToNext()){  //当光标移到下一有数据行
                RateItem item = new RateItem();
                item.setId(cursor.getInt(cursor.getColumnIndex("ID"))); //获取ID索引值
                item.setCurName(cursor.getString(cursor.getColumnIndex("CURNAME")));
                item.setCurRate(cursor.getString(cursor.getColumnIndex("CURRATE")));
                rateList.add(item);  //将每一行的item数据，放到列表里边
            }
            cursor.close(); //关闭光标
        }
        db.close();  //关闭数据库
        return rateList;     }//返回列表
}
