package org.iith.scitech.infero.infox.util;

import android.content.Context;

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
        /* create a httpClient and a new post request. */
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPostRequest = new HttpPost(PrefUtils.getServerIP(context)+arguments[0]);

		/* extracting the number of arguments in the post requests. */
        int numberOfArguments = Integer.parseInt(arguments[1]);

		/* creating the name-value pairs for the post request. */
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(numberOfArguments);
        for(int i=2; i<(numberOfArguments*2)+2; i=i+2)
            nameValuePairs.add(new BasicNameValuePair(arguments[i], arguments[i+1]));

        try
        {
			/* sending the post request. */
            httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPostRequest);

            HttpEntity httpEntity = httpResponse.getEntity();
            reply = EntityUtils.toString(httpEntity);

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return reply;
    }
}
