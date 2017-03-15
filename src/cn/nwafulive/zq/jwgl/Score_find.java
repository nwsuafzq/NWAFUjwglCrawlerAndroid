package cn.nwafulive.zq.jwgl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.nwafulive.zq.jwgl.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.widget.Toast;


/**
 * @author 张琼
 *
 */
public class Score_find extends Activity{
    private String Cookies;
    HttpClient client;
    private String url="http://jwgl.nwsuaf.edu.cn/academic/manager/score/studentOwnScore.do?groupId=&moduleId=2021";
    private String year=null;
    private String trem=null;
    private String para="0";
    private String sortColumn="";
    private String Submit="查询";

    private TextView showScore;

    private EditText InputYear;
    private EditText InputTrem;

    SharedPreferences sharedPreferences;
    StringBuffer sb=new StringBuffer();

    private Handler handler=null;

    private Button searchButton;
    //这两个标记是用于判断用户输入的数据是否合法
    private int mark1=0;
    private int mark2=0;
    @Override
    protected void onCreate(final Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.score_find);
        initEvent();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String years=null,trems=null;
                years=InputYear.getText().toString();
                trems=InputTrem.getText().toString();
                System.out.println(years+trems);
                //输入信息的判断
                if("春".equals(trems))
                {
                    trem="1";
                    mark1=1;
                    System.out.println(trems+"\t"+trem);
                }else if("秋".equals(trems))
                {
                    trem="3";
                    mark1=1;
                }else
                {
                    mark1=0;
                    Toast.makeText(Score_find.this,"输入学期有误请重新输入！",Toast.LENGTH_SHORT).show();
                }
                if("2014".equals(years))
                {
                    year="34";
                    mark2=1;
                    System.out.println(years+"\t"+year);
                }
                else if("2015".equals(years))
                {
                    year="35";
                    mark2=1;
                }
                else if("2016".equals(years))
                {
                	year="36";
                	mark2=1;
                	System.out.println(years+"\t"+year);
                }else if("2017".equals(years))
                {
                	year="37";
                	mark2=1;
                	System.out.println(years+"\t"+year);
                }
                else
                {
                    mark2=0;
                    Toast.makeText(Score_find.this,"输入年份有误请重新输入！",Toast.LENGTH_SHORT).show();
                }
                //如果两个信息都输入合法则提交请求
                if(mark1==1&&mark2==1) {
                    //是耗时操作都要放到新线程里执行
                    getScore();
                }
            }
        });

    }
    public void initEvent()
    {
        InputTrem=(EditText)findViewById(R.id.InputTrem);
        InputYear=(EditText)findViewById(R.id.InputYear);
        searchButton=(Button)findViewById(R.id.searchButton);
        showScore=(TextView)findViewById(R.id.show_score);
        //设置showScore可以滚动
        showScore.setMovementMethod(ScrollingMovementMethod.getInstance());
        handler=new Handler()
        {
            @Override
            public void handleMessage(Message message)
            {
                //加载信息
                showScore.setText(sb.toString());
            }
        };
        sharedPreferences = getSharedPreferences("params", Context.MODE_PRIVATE);
        Cookies=sharedPreferences.getString("Cookies", null);
        showScore=(TextView)findViewById(R.id.show_score);
        client=new DefaultHttpClient();
    }
    public void analysisText(String results)
    {
    	 //这里使用jsoup开源的解析包进行html源码的解析
        //获取要解析的网址或者文档或者网址
        Document document = Jsoup.parse(results);
        //经过分析成绩保存在datalist这个Class中因此要定位到这个类中
        Elements elements = document.getElementsByClass("datalist");
        System.out.println(elements.text().toString()+"啊啊啊啊啊啊");
        
        if(elements.text().toString()==null||elements.text().toString()=="")
        {
        	sb.append("无成绩\n");
        }
        else
        {
        //获取他的第一个元素集合
        Element element = elements.get(0);
        System.out.println(element.text().toString()+"--------------------");
        //再分析可以看到在tr标签下有成绩的详细信息
        Elements elements1 = element.getElementsByTag("tr");
        Element element2;
        Elements elements3;
        Element element3;
        Element element4;
        for (int i = 0; i < elements1.size(); i++) {
        	 //剥离每一个标签
            element2 = elements1.get(i);
            //再重新定位td标签下的内容
            elements3 = element2.getElementsByTag("td");
            for (int j = 0; j < elements3.size(); j++) {
            	//这里为了获取td标签中的子元素要进行一个循环
                if (j == 0) {
                	 //我发现我要的课程名和成绩分别在elements3集合中的第4个元素和第11个元素
                    element3 = elements3.get(3);
                    element4 = elements3.get(10);
                    sb.append(element3.text()).append(":").append("\t\t").append(element4.text()).append("\n");
                } else {
                    break;
                }

            }
        }
        
        }
        //数据获取完成通知组件重绘信息
        handler.sendEmptyMessage(0);

    }
    public void getScore()
    {
        new Thread() {
            @Override
            public void run()
            {
                HttpResponse httpResponse;
                HttpPost httpPost = new HttpPost(url);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("year", year));
                params.add(new BasicNameValuePair("term", trem));
                params.add(new BasicNameValuePair("para", para));
                params.add(new BasicNameValuePair("sortColumn", sortColumn));
                params.add(new BasicNameValuePair("Submit", Submit));
                System.out.println(params);
                httpPost.setHeader("Cookie", Cookies);
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    httpResponse = client.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {

                        StringBuffer stringBuffer = new StringBuffer();
                        String result;
                        InputStream inputStream = httpResponse.getEntity().getContent();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        String data = "";
                      //读取得到的数据
                        while ((data = bufferedReader.readLine()) != null) {
                            stringBuffer.append(data);
                            stringBuffer.append("\n");
                        }
                        result = stringBuffer.toString();
                        
                        
                        System.out.println(result);
                        
                        //判断是否获取到数据
                        if (result == null) {
                            System.out.println("NULL!!!!");
                        } else {
                            analysisText(result);
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
