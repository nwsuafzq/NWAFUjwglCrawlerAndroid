package cn.nwafulive.zq.jwgl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import cn.nwafulive.zq.jwgl.R;

import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张琼  2017年3月15日 22:37:02
 *
 */
public class MainActivity extends Activity {
    //使用SharedPreferences进行用户的用户名密码以及cookie的保存
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private EditText studentNumber;
    private EditText passWord;
    private EditText idCode;

    private Bitmap bitmap;
    private ImageView IdcodeImage;

    //注意这里Handler使用的是import android.os.Handler;这个包
    private Handler handler;

    private Button logIn;

    String StudentNumber;
    String PassWord;
    String IdCode;

    String groupId="";
    String login="登录";
    //这条是解析出来进行获取验证码的图片的网址
    String url2="http://jwgl.nwsuaf.edu.cn/academic/getCaptcha.do";
    //这条是解析出来进行提交登录信息的网址
    String url3="http://jwgl.nwsuaf.edu.cn/academic/j_acegi_security_check";
    //这里使用HttpClient进行数据的获取和提交
    HttpClient client;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        initEvent();
        //获取验证码
        getIdCode();
        //对我们的验证码绑定一个单击响应事件，这是为了去实现验证码看不清时再更新一张验证码而用
        IdcodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdCode();
            }
        });
        //对登录按钮绑定单击响应事件
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
                loginEvent();
            }
        });
    }
    public void initEvent()
    {
        //实例化HttpClient对象
        client=new DefaultHttpClient();
        //sharedPreferences第一个参数是给你保存的信息起个名字，第二个参数设置为Context.MODE_PRIVATE属性，
        // 这样会避免其他应用可以直接访问我们保存的信息
        sharedPreferences=getSharedPreferences("params", Context.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象
        editor=sharedPreferences.edit();
        studentNumber=(EditText)findViewById(R.id.studentNumber);
        passWord=(EditText)findViewById(R.id.key);
        IdcodeImage=(ImageView)findViewById(R.id.passImage);
        idCode=(EditText)findViewById(R.id.identifyingCode);
        logIn=(Button)findViewById(R.id.login);
        //实例化Handler对象方便线程之间通信
        handler =new Handler();
    }
    public void getIdCode()
    {
        new Thread() {
            @Override
            public void run() {
                //我们需要同步Cookie信息所以从验证码开始就需要获取Cookie
                List<Cookie> cookies1;
                //HttpGet来发送获取验证码请求
                HttpGet httpGet = new HttpGet(url2);
                //声明一个HttpResponse
                HttpResponse httpResponse = null;
                try {
                    //实例化HttpResponse
                    httpResponse = client.execute(httpGet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //如果服务器响应成功
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    try {
                        //使用输入流来接受数据
                        InputStream in = httpResponse.getEntity().getContent();
                        //bitmap来获取数据流中的图片信息
                        bitmap = BitmapFactory.decodeStream(in);
                        //关闭输入流
                        in.close();
                        String Cookies;
                        //获取Cookie
                        cookies1 = ((AbstractHttpClient) client).getCookieStore().getCookies();
                        Cookies = "JSESSIONID="+cookies1.get(0).getValue().toString();
                        //System.out.println(Cookies);
                        //在SharedPreferences中保存cookie
                        editor.putString("Cookies", Cookies);
                        //提交保存数据
                        editor.commit();
                        //通过handler.post方法在线程中更新主线程中的验证码图片信息
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap != null) {
                                    IdcodeImage.setImageBitmap(bitmap);
                                }
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }
    public void saveEvent()
    {
        //获取输入信息，并保存为做记住密码来铺垫
        StudentNumber =studentNumber.getText().toString();
        PassWord =passWord.getText().toString();
        IdCode = idCode.getText().toString();
        //这里写入StudentNumber和PassWord是为了做记住密码登录
        editor.putString("StudentNumber", StudentNumber);
        editor.putString("PassWord", PassWord);
        editor.putString("IdCode", IdCode);
        editor.commit();
    }
    public void loginEvent()
    {

        new Thread() {
            @Override
            public void run() {
                //提交数据用List<NameValuePair>的方式
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //这里的名称不要有多余的符号，因为提交数据时httppost方法会帮你维护数据
                //这里表单的数据顺序要按照刚刚解析所显示的顺序排列
                params.add(new BasicNameValuePair("groupId", groupId));
                params.add(new BasicNameValuePair("j_username", StudentNumber));
                params.add(new BasicNameValuePair("login",login));
                params.add(new BasicNameValuePair("j_password", PassWord));
                params.add(new BasicNameValuePair("j_captcha", IdCode));
                System.out.println(params);
                try {
                    HttpPost httpPost = new HttpPost(url3);
                    String Cookies;
                    //获取到刚刚在获取验证码时得到的Cookie
                    Cookies = sharedPreferences.getString("Cookies", null);
                    //System.out.println(Cookies);
                    //提交数据做准备
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //同步cookie
                    httpPost.setHeader("Cookie", Cookies);
                    //获取返回的信息
                    HttpResponse httpResponse = client.execute(httpPost);
                    String result = EntityUtils.toString(httpResponse.getEntity());
                    //System.out.println(result);
                    //这里我们不仅需要保证服务器正常响应，而且还要知道当我们登陆失败时是什么原因导致的
                    if(!result.contains("错误提示")&&httpResponse.getStatusLine().getStatusCode() == 200)
                    {
                        startActivity(new Intent(MainActivity.this, Score_find.class));
                    }
                    else
                    {
                        if(result.contains("密码不匹配"))
                        {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(MainActivity.this, "密码不匹配或用户名错误!!!请重新输入", Toast.LENGTH_LONG).show();
                                    //当登陆失败时上一张验证码的图片已经失效因此需重新加载
                                    getIdCode();

                                }
                            });
                        }else if(result.contains("验证码错误")||result.contains("验证码不正确"))
                        {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "验证码错误!!!请重新输入", Toast.LENGTH_LONG).show();
                                    getIdCode();
                                }
                            });
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
