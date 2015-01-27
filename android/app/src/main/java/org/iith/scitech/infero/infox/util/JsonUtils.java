package org.iith.scitech.infero.infox.util;

import android.content.Context;

import org.json.JSONObject;

/**
 * Created by shashank on 27/1/15.
 */
public class JsonUtils
{
    public static String getData(String data, String key)
    {
        JSONObject jsonObject = null;
        String ret = "";
        try
        {
            jsonObject = new JSONObject(data);
            ret = jsonObject.getString(key);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
