package cn.nwafulive.zq.jwgl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.params.HttpParams;

import cn.nwafulive.zq.jwgl.R;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.CookieManager;

/**
 * @author ’≈«Ì
 *
 */
//http://jw.djtu.edu.cn/academic/manager/studentinfo/showStudentImage.jsp
public class ChooseCentre extends Activity{
    private Handler handler;
    private Button course;
    private Button exam;
    private ImageView photo;
    private WebView webView;
    private String Cookies;
    SharedPreferences sharedPreferences;
    private String url="http://jwgl.nwsuaf.edu.cn/academic/index_new.jsp";
    private String url2="http://jwgl.nwsuaf.edu.cn/academic/manager/coursearrange/showTimetable.do?id=290552&yearid=36&termid=1&timetableType=STUDENT&sectionType=BASE";
    String url3="http://jwgl.nwsuaf.edu.cn/academic/manager/coursearrange/showTimetable.do?id=290552&yearid=36&termid=1&timetableType=STUDENT&sectionType=BASE";
    String url4="http://jwgl.nwsuaf.edu.cn";
    String url5="http://jwgl.nwsuaf.edu.cn/academic/accessModule.do?moduleId=2000&groupId=";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_centre);
        sharedPreferences = getSharedPreferences("params", Context.MODE_PRIVATE);
        Cookies=sharedPreferences.getString("Cookies", null);
        System.out.println("ChooseCentre Cookies: " + Cookies);
        course=(Button)findViewById(R.id.course_List);
        exam=(Button)findViewById(R.id.examination_Results);
        course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent=new Intent(ChooseCentre.this,Score_find.class);
              //  intent.putExtra("cookie",Cookies);
                startActivity(new Intent(ChooseCentre.this, Course_list.class));
            }
        });
        exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseCentre.this,Score_find.class));


            }
        });
    }
}
