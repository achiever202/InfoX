package adarsh.awesomeapps.androidtracker;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/*
 *  This class is used to send HTTP POST requests to the server.
 */
public class ServerRequest extends AsyncTask<String, Void, String>
{
	/* reply stores the reply from the server. */
	String reply;
	Context context;
	
	/* constructor for the object. */
	public ServerRequest(Context ctx)
	{
		/* context of the caller. */
		context = ctx;
		reply = "";
	}
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
	}
	
	protected String doInBackground(String... arguments)
	{
		/* create a httpClient and a new post request. */
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPostRequest = new HttpPost("http://172.16.0.43/AndroidTracker/"+arguments[0]);
		
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
	
	protected void onPostExecute(String reply)
	{
		super.onPostExecute(reply);
	}
	
	/* This function returns the reply string. */
	public String getReply()
	{
		return reply;
	}
}
