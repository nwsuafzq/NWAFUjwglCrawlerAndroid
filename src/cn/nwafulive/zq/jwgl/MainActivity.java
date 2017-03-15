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
 * @author ����  2017��3��15�� 22:37:02
 *
 */
public class MainActivity extends Activity {
    //ʹ��SharedPreferences�����û����û��������Լ�cookie�ı���
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private EditText studentNumber;
    private EditText passWord;
    private EditText idCode;

    private Bitmap bitmap;
    private ImageView IdcodeImage;

    //ע������Handlerʹ�õ���import android.os.Handler;�����
    private Handler handler;

    private Button logIn;

    String StudentNumber;
    String PassWord;
    String IdCode;

    String groupId="";
    String login="��¼";
    //�����ǽ����������л�ȡ��֤���ͼƬ����ַ
    String url2="http://jwgl.nwsuaf.edu.cn/academic/getCaptcha.do";
    //�����ǽ������������ύ��¼��Ϣ����ַ
    String url3="http://jwgl.nwsuaf.edu.cn/academic/j_acegi_security_check";
    //����ʹ��HttpClient�������ݵĻ�ȡ���ύ
    HttpClient client;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //��ʼ���ؼ�
        initEvent();
        //��ȡ��֤��
        getIdCode();
        //�����ǵ���֤���һ��������Ӧ�¼�������Ϊ��ȥʵ����֤�뿴����ʱ�ٸ���һ����֤�����
        IdcodeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIdCode();
            }
        });
        //�Ե�¼��ť�󶨵�����Ӧ�¼�
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
        //ʵ����HttpClient����
        client=new DefaultHttpClient();
        //sharedPreferences��һ�������Ǹ��㱣�����Ϣ������֣��ڶ�����������ΪContext.MODE_PRIVATE���ԣ�
        // �������������Ӧ�ÿ���ֱ�ӷ������Ǳ������Ϣ
        sharedPreferences=getSharedPreferences("params", Context.MODE_PRIVATE);
        //ʵ����SharedPreferences.Editor����
        editor=sharedPreferences.edit();
        studentNumber=(EditText)findViewById(R.id.studentNumber);
        passWord=(EditText)findViewById(R.id.key);
        IdcodeImage=(ImageView)findViewById(R.id.passImage);
        idCode=(EditText)findViewById(R.id.identifyingCode);
        logIn=(Button)findViewById(R.id.login);
        //ʵ����Handler���󷽱��߳�֮��ͨ��
        handler =new Handler();
    }
    public void getIdCode()
    {
        new Thread() {
            @Override
            public void run() {
                //������Ҫͬ��Cookie��Ϣ���Դ���֤�뿪ʼ����Ҫ��ȡCookie
                List<Cookie> cookies1;
                //HttpGet�����ͻ�ȡ��֤������
                HttpGet httpGet = new HttpGet(url2);
                //����һ��HttpResponse
                HttpResponse httpResponse = null;
                try {
                    //ʵ����HttpResponse
                    httpResponse = client.execute(httpGet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //�����������Ӧ�ɹ�
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    try {
                        //ʹ������������������
                        InputStream in = httpResponse.getEntity().getContent();
                        //bitmap����ȡ�������е�ͼƬ��Ϣ
                        bitmap = BitmapFactory.decodeStream(in);
                        //�ر�������
                        in.close();
                        String Cookies;
                        //��ȡCookie
                        cookies1 = ((AbstractHttpClient) client).getCookieStore().getCookies();
                        Cookies = "JSESSIONID="+cookies1.get(0).getValue().toString();
                        //System.out.println(Cookies);
                        //��SharedPreferences�б���cookie
                        editor.putString("Cookies", Cookies);
                        //�ύ��������
                        editor.commit();
                        //ͨ��handler.post�������߳��и������߳��е���֤��ͼƬ��Ϣ
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
        //��ȡ������Ϣ��������Ϊ����ס�������̵�
        StudentNumber =studentNumber.getText().toString();
        PassWord =passWord.getText().toString();
        IdCode = idCode.getText().toString();
        //����д��StudentNumber��PassWord��Ϊ������ס�����¼
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
                //�ύ������List<NameValuePair>�ķ�ʽ
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //��������Ʋ�Ҫ�ж���ķ��ţ���Ϊ�ύ����ʱhttppost���������ά������
                //�����������˳��Ҫ���ոոս�������ʾ��˳������
                params.add(new BasicNameValuePair("groupId", groupId));
                params.add(new BasicNameValuePair("j_username", StudentNumber));
                params.add(new BasicNameValuePair("login",login));
                params.add(new BasicNameValuePair("j_password", PassWord));
                params.add(new BasicNameValuePair("j_captcha", IdCode));
                System.out.println(params);
                try {
                    HttpPost httpPost = new HttpPost(url3);
                    String Cookies;
                    //��ȡ���ո��ڻ�ȡ��֤��ʱ�õ���Cookie
                    Cookies = sharedPreferences.getString("Cookies", null);
                    //System.out.println(Cookies);
                    //�ύ������׼��
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    //ͬ��cookie
                    httpPost.setHeader("Cookie", Cookies);
                    //��ȡ���ص���Ϣ
                    HttpResponse httpResponse = client.execute(httpPost);
                    String result = EntityUtils.toString(httpResponse.getEntity());
                    //System.out.println(result);
                    //�������ǲ�����Ҫ��֤������������Ӧ�����һ�Ҫ֪�������ǵ�½ʧ��ʱ��ʲôԭ���µ�
                    if(!result.contains("������ʾ")&&httpResponse.getStatusLine().getStatusCode() == 200)
                    {
                        startActivity(new Intent(MainActivity.this, Score_find.class));
                    }
                    else
                    {
                        if(result.contains("���벻ƥ��"))
                        {

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(MainActivity.this, "���벻ƥ����û�������!!!����������", Toast.LENGTH_LONG).show();
                                    //����½ʧ��ʱ��һ����֤���ͼƬ�Ѿ�ʧЧ��������¼���
                                    getIdCode();

                                }
                            });
                        }else if(result.contains("��֤�����")||result.contains("��֤�벻��ȷ"))
                        {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "��֤�����!!!����������", Toast.LENGTH_LONG).show();
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
