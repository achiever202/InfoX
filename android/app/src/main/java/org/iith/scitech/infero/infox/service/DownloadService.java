package org.iith.scitech.infero.infox.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.iith.scitech.infero.infox.data.ContentListProvider;
import org.iith.scitech.infero.infox.util.HttpServerRequest;
import org.iith.scitech.infero.infox.util.PrefUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

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
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if(networkInfo!=null && networkInfo.isConnected()) {
                        if (PrefUtils.canAutoSync(DownloadService.this)) {
                            new GetNetworkDataTask().run();
                        }
                        res = clp.getDownloads("NO");
                        while (res.isAfterLast() == false) {
                            int content_id = res.getInt(res.getColumnIndex("content_id"));
                            Cursor res2 = clp.getContentsById(content_id);
                            res2.moveToFirst();
                            Boolean isDownloaded = download(res2.getString(res2.getColumnIndex("file_path")), res2.getString(res2.getColumnIndex("file_name")));
                            if(isDownloaded && Environment.getExternalStoragePublicDirectory("InfoX/"+res2.getString(res2.getColumnIndex("file_name"))).exists())
                            {
                                clp.updateContentFilePath(Integer.toString(content_id), Environment.getExternalStoragePublicDirectory("InfoX/"+res2.getString(res2.getColumnIndex("file_name"))).getAbsolutePath());
                                clp.updateDownloads(Integer.toString(content_id), "YES");
                            }
                            //array_list.add(res2.getString(res2.getColumnIndex("file_path")));
                            res.moveToNext();
                        }
                    }
                    try {
                        Thread.sleep(300000);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private class GetNetworkDataTask implements Runnable {
        @Override
        public void run() {
            Log.v("NET", "Sending...");
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(new HttpServerRequest(DownloadService.this).getReply(new String[]{"download.php"}));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(!jsonObject.toString().equals("")) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = (JSONArray) jsonObject.get("data");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ContentListProvider clp = new ContentListProvider(DownloadService.this);
                clp.open();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject indObject = null;
                    try {
                        indObject = (JSONObject) jsonArray.get(i);
                        Cursor cr = clp.getContentById(indObject.getInt("content_id"));
                        if (cr != null)
                            if (!cr.moveToFirst()) {
                                clp.insertContents(indObject.getString("content_id"), indObject.getString("file_name"), indObject.getString("content"), indObject.getString("time_added"), indObject.getString("time_expiry"), indObject.getString("langId"), indObject.getString("category"), indObject.getString("tileType"));
                                if (indObject.getInt("downloadRequired") == 1) {
                                    clp.insertDownloads(clp.getContentIdByContent(indObject.getString("content"), indObject.getString("time_added"), indObject.getString("time_expiry")), "NO", 0);
                                }
                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Boolean download(String downloadPath, String fileName)
    {
        try {
            URL url = new URL(downloadPath);
            URLConnection connection = url.openConnection();
            connection.connect();


            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(PrefUtils.getDownloadDirectory(DownloadService.this));

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                Log.v("progress: ",  ""+(int)(total * 100 / fileLength));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public Boolean downloadAsync(String downloadPath, String fileName)
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
