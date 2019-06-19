package ir.boozar.minecraft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hossein on 10/27/14.
 */
public class ActivityVideos extends Activity {
    private static DBD Dbd;
    private static Context context;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_videos);

        context=this;
        Dbd= m.Dbd;

        handler=new Handler();

        TextView tv=(TextView) findViewById(R.id.loadingL);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout rl=(RelativeLayout) findViewById(R.id.loadingBox);
                rl.setVisibility(View.GONE);
            }
        });

        update();
    }
    public void update(){
        DBD.VideoObj[] list=Dbd.getVideos(1);

        LinearLayout ll=(LinearLayout) findViewById(R.id.main);
        ll.removeAllViews();

        for(final DBD.VideoObj o:list){
            TextView tv=new TextView(this);
            tv.setBackgroundResource(R.drawable.recipe_row);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tv.setPadding(6,6,6,6);
            tv.setText(Html.fromHtml(String.format(
                    getResources().getString(R.string.video_text)
                    ,o.title,o.time,o.info
            )));
            tv.setClickable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isNetworkAvailable()){
                        getVideoFail(getResources().getString(R.string.msg_noNet));
                        return;
                    }
                    ImageView iv=(ImageView) findViewById(R.id.loading);
                    iv.setBackgroundResource(R.drawable.loading);
                    AnimationDrawable a=(AnimationDrawable) iv.getBackground();
                    a.start();
                    GetAparat ap=new GetAparat();
                    ap.execute(o.key);
                    RelativeLayout rl=(RelativeLayout) findViewById(R.id.loadingBox);
                    rl.setVisibility(View.VISIBLE);
                }
            });
            ll.addView(tv);
        }
    }
    private void getVideoFail(final String msg){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
                RelativeLayout rl=(RelativeLayout) findViewById(R.id.loadingBox);
                rl.setVisibility(View.GONE);
            }
        });
    }
    private void getVideoDone(final String video){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.parse(video), "video/mp4");
                try{
                    startActivity(i);
                }catch (Exception e){
                    Toast.makeText(context,"هیچ پخش کننده ویدیویی در گوشی شما نصب نیست",Toast.LENGTH_SHORT).show();
                }
                RelativeLayout rl=(RelativeLayout) findViewById(R.id.loadingBox);
                rl.setVisibility(View.GONE);
            }
        });
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private class GetAparat extends AsyncTask<String,Void,String> {
        protected String doInBackground(String... id) {
            try {
                String url = "https://www.aparat.com/v/"+id[0];
                URLConnection conn = new URL(url).openConnection();
                conn.setRequestProperty("User-Agent","Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:56.0) Gecko/20100101 Firefox/56.0");
                InputStream in = conn.getInputStream();
                String data = n.convertStreamToString(in);
//                Log.i("hz","aparatResp("+url+"):"+conn.getHeaderFields().toString()+data);
                return data;
            } catch (Exception e) {
                Log.e("hz", "getNews-E" + e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(String data){
            if(data==null) {
                getVideoFail(getResources().getString(R.string.msg_getVideoError));
                return;
            }
            Matcher matcher= Pattern.compile("\"(http[^\"]+\\.mp4)\"").matcher(data);
            if(!matcher.find()){
                getVideoFail(getResources().getString(R.string.msg_getVideoError));
                return;
            }
            //Log.i("hz","getVideo:"+matcher.group(1));
            getVideoDone(matcher.group(1));
        }
    }
}
