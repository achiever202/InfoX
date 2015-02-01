package org.iith.scitech.infero.infox.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashank on 25/1/15.
 */
public class HttpServerRequest {
    protected Context context;
    protected String reply;

    public HttpServerRequest(Context ctx)
    {
		/* context of the caller. */
        context = ctx;
        reply = "";
    }

    public String getReply(String... arguments)
    {
        Log.v("NET", PrefUtils.getServerIP(context)+arguments[0]);
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

//        if(networkInfo!=null && networkInfo.isConnected()) {
            /* create a httpClient and a new post request. */
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(PrefUtils.getServerIP(context) + arguments[0]);

            if ((arguments.length & 1) == 0)
                return "";

            Log.v("NET", "Sending...2");

            /* creating the name-value pairs for the post request. */
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            for (int i = 1; i < arguments.length; i = i + 2)
                nameValuePairs.add(new BasicNameValuePair(arguments[i], arguments[i + 1]));

            try {
                /* sending the post request. */
                httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                Log.v("NET", "executing");
                HttpResponse httpResponse = httpClient.execute(httpPostRequest);
                Log.v("NET", "executed");

                HttpEntity httpEntity = httpResponse.getEntity();
                reply = EntityUtils.toString(httpEntity);
                Log.v("NET", "Got reply: " + reply);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}
        return reply;
    }
}
