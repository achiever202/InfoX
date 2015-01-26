package org.iith.scitech.infero.infox.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import org.iith.scitech.infero.infox.data.ContentListProvider;

/**
 * Created by shashank on 26/1/15.
 */
public class DownloadService extends Service {

    Boolean isDownloadingSomething = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread()
        {
            ContentListProvider clp = new ContentListProvider(DownloadService.this);
            Cursor res = null;
            public void run()
            {
                while(true)
                {
                    res = clp.getDownloads("NO");
                    while(res.isAfterLast() == false){
                        int content_id = res.getInt(res.getColumnIndex("content_id"));
                        Cursor res2 =  clp.getContentsById(content_id);
                        res2.moveToFirst();
                        Boolean isDownloaded = download(res2.getString(res2.getColumnIndex("file_path")), res2.getString(res2.getColumnIndex("file_name")));
                        //array_list.add(res2.getString(res2.getColumnIndex("file_path")));
                        res.moveToNext();
                    }
                }
            }
        }.start();
    }

    public Boolean download(String downloadPath, String fileName)
    {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadPath));
        request.setDescription(fileName);
        request.setTitle("InfoX");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        return true;
    }

}
