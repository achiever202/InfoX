package org.iith.scitech.infero.infox.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.w3c.dom.Comment;

public class ContentListProvider {

    // Database fields
    private SQLiteDatabase database;
    private ContentListDatabase dbHelper;
    //private String[] allColumns = { MySQLiteHelper.COLUMN_ID,MySQLiteHelper.COLUMN_COMMENT };

    public ContentListProvider(Context context) {
        dbHelper = new ContentListDatabase(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getContentsByRawQuery(String rawQuery)
    {
        Cursor res =  database.rawQuery( rawQuery, null );
        return res;
    }

    public Cursor getAllContents()
    {
        Cursor res =  database.rawQuery( "select * from contents", null );
        return res;
    }

    public ArrayList getContentsListById(int content_id)
    {
        ArrayList array_list = new ArrayList();
        Cursor res =  database.rawQuery( "select * from contents where content_id="+content_id+"", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex("file_path")));
            res.moveToNext();
        }

        return array_list;
    }

    public Cursor getContentsById(int content_id)
    {
        Cursor res =  database.rawQuery( "select * from contents where content_id="+content_id+"", null );
        res.moveToFirst();
        return res;
    }

    public Cursor getContentById(int content_id)
    {
        Cursor res =  database.rawQuery( "select * from contents where content_id="+content_id+"", null );
        return res;
    }

    public Boolean deleteContentById(int content_id)
    {
        //Log.v("DEB", content_id+"");
        return database.delete("contents", "content_id="+content_id, null) > 0;
        //Cursor res =  database.rawQuery( "delete * from contents where content_id="+content_id+"", null );
        //return res;
    }

    public int getContentIdByContent(String content, String time_added, String time_expiry)
    {
        Cursor res =  database.rawQuery( "select * from contents where file_path="+content+" and time_added="+time_added+" and time_expiry="+time_expiry, null );
        res.moveToFirst();
        return res.getInt(res.getColumnIndex("content_id"));
    }

    public Boolean insertContents(String content_id, String file_name, String file_path, String time_added, String time_expiry, String lang_id, String category_id, String content_type_id)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("content_id", content_id);
        contentValues.put("file_name", file_name);
        contentValues.put("file_path", file_path);
        contentValues.put("time_added", time_added);
        contentValues.put("time_expiry", time_expiry);
        contentValues.put("lang_id", lang_id);
        contentValues.put("category_id", category_id);
        contentValues.put("content_type_id", content_type_id);

        database.insert("contents", null, contentValues);
        return true;
    }

    public Boolean insertDownloads(int content_id, String downloaded, int deleted)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("content_id", content_id);
        contentValues.put("downloaded", downloaded);
        contentValues.put("deleted", deleted);

        database.insert("downloads", null, contentValues);
        return true;
    }

    public Cursor getDownloads(String downloaded)
    {
        Cursor res =  database.rawQuery( "select * from downloads where downloaded="+downloaded, null );
        res.moveToFirst();
        return res;
    }

    public ArrayList getDownloadsByStatus(String downloaded)
    {
        ArrayList array_list = new ArrayList();
        Cursor res =  database.rawQuery( "select * from downloads where downloaded="+downloaded+"", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            int content_id = res.getInt(res.getColumnIndex("content_id"));
            Cursor res2 =  database.rawQuery( "select * from contents where content_id="+content_id+"", null );
            res2.moveToFirst();
            array_list.add(res2.getString(res2.getColumnIndex("file_path")));
            res.moveToNext();
        }

        return array_list;
    }

}

