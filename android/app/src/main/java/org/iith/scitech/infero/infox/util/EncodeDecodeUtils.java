package org.iith.scitech.infero.infox.util;

import android.content.Context;
import android.util.Base64;

import org.iith.scitech.infero.infox.ui.DataTransferDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by shashank on 28/1/15.
 */
public class EncodeDecodeUtils {
    public static String Encode(Context context, String fileName)
    {
        File file = new File(PrefUtils.getDownloadDirectory(context)+"/"+fileName);
        FileInputStream objFileIS = null;
        ByteArrayOutputStream objByteArrayOS = null;

        try {
            objFileIS = new FileInputStream(file);
            objByteArrayOS   = new ByteArrayOutputStream();
            byte[] byteBufferString = new byte[1024];
            for (int readNum; (readNum = objFileIS.read(byteBufferString)) != -1;) {
                objByteArrayOS.write(byteBufferString, 0, readNum);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return Base64.encodeToString(objByteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static void Decode(Context context, String contentToBeDecoded, String fileName) {
        byte[] valueDecoded = Base64.decode(contentToBeDecoded, Base64.DEFAULT);
        File file2 = new File(PrefUtils.getDownloadDirectory(context) + "/" + fileName);
        try {
            FileOutputStream os = new FileOutputStream(file2, true);
            os.write(valueDecoded);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
