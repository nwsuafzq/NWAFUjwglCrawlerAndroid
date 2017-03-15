package cn.nwafulive.zq.jwgl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
/**
 * @author ’≈«Ì
 *
 */
public class HttpConnection extends Thread{
    private Handler handler;
    private String url;
    private ImageView passImage;
    private String cookie;

    public HttpConnection(String url,ImageView imageView,Handler handler)
    {
        this.url=url;
        this.passImage=imageView;
        this.handler=handler;
    }
    @Override
    public void run() {
        try {
            URL httpUrl=new URL(url);
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoInput(true);
                InputStream inputStream = httpURLConnection.getInputStream();
                FileOutputStream outputStream = null;
                File downloadFile = null;
                String path = null;
                String fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    path = Environment.getExternalStorageDirectory().toString() + "/DJTU";
                    downloadFile = new File(path, fileName);
                    outputStream = new FileOutputStream(downloadFile);
                }
                final byte[] b = new byte[5*1024];
                int len;
                if (outputStream != null) {
                    while ((len = inputStream.read(b)) != -1) {
                        outputStream.write(b, 0, len);
                    }

                }
                System.out.println(downloadFile.getAbsoluteFile().toString());
                final Bitmap bitmap = BitmapFactory.decodeFile(downloadFile.getAbsolutePath().toString());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(bitmap!=null)
                        {
                            passImage.setImageBitmap(bitmap);
                        }
                    }
                });
            }catch (IOException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
