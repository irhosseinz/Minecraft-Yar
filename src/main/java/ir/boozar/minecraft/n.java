package ir.boozar.minecraft;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URL;

/**
 * Created by hossein on 8/23/14.
 */
public class n {

    public static final int MARKET=0;
    static final String[] MARKETS=new String[]{
            "cfb"
            ,"myk"
            ,"cnd"
            ,"irap"
            ,"plaz"
    };
    public static Intent getRateIntent(Context c){
        switch (MARKET){
            case 0:
                return new Intent(
                        Intent.ACTION_EDIT,
                        Uri.parse("bazaar://details?id="+c.getPackageName()));
            case 2:
                return new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("cando://leave-review?id="+c.getPackageName()));
            default:
                return new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("bazaar://details?id="+c.getPackageName()));
        }
    }

    private Context context;

    private SharedPreferences pref;

    private boolean dialogShown=false;

    private SharedPreferences Pref;

    public static final String PREF_LASTCHECK="lastCheck";
    public static final String PREF_LASTVER="lastVer";
    public static final String PREF_LASTVERC="lastVerC";
    public static final String PREF_LAST_DATA="lastData";

    public n(Context c){
        context=c;

        Pref=PreferenceManager.getDefaultSharedPreferences(c);
        //Db=d;

        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void getNews(){
        SharedPreferences.Editor edit= pref.edit();
        long time=System.currentTimeMillis();
        long f=pref.getLong("first", 0L);
        if(f<10){
            edit.putLong("first",time);
        }
        edit.commit();
        long l=pref.getLong(PREF_LASTCHECK, 0L);
        if(time-l>86400000L){
            if(isNetworkAvailable()){
                String id= Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                getNews n=new getNews();
                n.execute(id);
            }
        }
    }
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    void showNewsDialog(final NewsObj n){
        if(dialogShown)
            return;
        dialogShown=true;
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(n.link));

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.news);
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(n.text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                context.startActivity(browserIntent);
            }

        });
        Button btn=(Button) dialog.findViewById(R.id.close);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn=(Button) dialog.findViewById(R.id.view);
        //btn.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m.db.readNews(n.id);
                dialog.dismiss();
                context.startActivity(browserIntent);
            }
        });
        dialog.show();
    }
    void showNewsNotify(final NewsObj no) throws Exception{
        if(Build.VERSION.SDK_INT<11 || no.type>5)
            return;
        int id=(int)(System.currentTimeMillis()/1000);
        Intent intent = new Intent(
                Intent.ACTION_VIEW, Uri.parse(no.link));
        PendingIntent pIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder nb  = new Notification.Builder(context)
                .setContentText(no.text)
                .setContentTitle(no.title)
                .setSmallIcon(no.iconID>0?no.iconID:R.drawable.no5)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis()+(5*60*1000));
        Notification n;
        if(no.icon!=null){
            nb.setLargeIcon(BitmapFactory.decodeByteArray(no.icon, 0, no.icon.length));
        }
        if(Build.VERSION.SDK_INT<16)
            n=nb.getNotification();
        else
            n=nb.build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, n);
    }
    private class getNews extends AsyncTask<String,Void,String> {
        protected String doInBackground(String... id) {
            try {
                System.setProperty("http.keepAlive", "false");
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                //  site | cfb | myk | irap | plaz
                String url = "http://ads.boozar.ir/ads.php" +
                        "?app=7&r="+MARKETS[MARKET]+"&i="+
                        id[0]+"&ver="+info.versionCode+
                        "&sdk="+ Build.VERSION.SDK_INT+"&up=0";
                //String url = "http://www.boozar.ir/01.php?r=site&i="+id[0]+"&ver="+info.versionCode+"&sdk="+ Build.VERSION.SDK_INT;
                URLConnection conn = new URL(url).openConnection();
                InputStream in = conn.getInputStream();
                return convertStreamToString(in);
            } catch (Exception e) {
                Log.e("hz", "getNews-E");
            }
            return "";
        }
        protected void onPostExecute(String data){
            String[] resp=data.split(";");
            int size=resp.length;
            //Log.i("hz", "News-Resp:" + data+" size:"+size);

            boolean save=true;
            if(size>2){
                try{
                    SharedPreferences.Editor edit= Pref.edit();
                    edit.putInt(PREF_LASTVER,Integer.parseInt(resp[0]));
                    edit.commit();
                    ads(resp[2]);
                }catch (Exception e){

                }
            }
        }
        void ads(String data){
            //Log.i("hz","packs news:"+data);
            String[] d=data.split("\\|");
            int ok0=0,ok=0;
            DOWNLOADED=0;
            DOWNLOAD_COUNT=d.length;
            for (String dd:d){
                //Log.i("hz","pack news:"+dd);
                String[] ddd=dd.split(",");
                if(ddd.length<3)
                    continue;
                ok0++;
                try{
                    NewsObj n=new NewsObj();
                    n.id=Integer.parseInt(ddd[0]);
                    n.setType(context,Integer.parseInt(ddd[1]));
                    n.pack=ddd[2];
                    n.title=ddd[3];
                    n.text=ddd[4];
                    n.link=ddd[5];
                    if(!m.db.newsExists(n.id) && !isPackageInstalled(context,n.pack)) {
                        IMG i=new IMG(n,ddd[6],ddd[7]);
                        i.execute();
                        if (n.type==1) {
                            try{
                                showNewsDialog(n);
                            }catch(Exception e){

                            }
                        }
                    }//else Log.i("hz","pack ref");
                    ok++;
                }catch (Exception e){
                    Log.i("hz","parseN-E",e);
                }
            }
            //Log.i("hz","chhh:"+ok0+","+ok);
            if(ok0==ok || true){
                SharedPreferences.Editor edit= Pref.edit();
                edit.putLong(PREF_LASTCHECK,System.currentTimeMillis());
                edit.commit();
            }
        }
    }
    int DOWNLOADED,DOWNLOAD_COUNT;
    class IMG extends AsyncTask<Void,Void,Boolean> {
        NewsObj no;
        String url_i,url_p;
        public IMG(NewsObj o,String icon,String picture){
            no=o;
            url_i=icon;
            url_p=picture;
            //Log.i("hz", "startIMG:"+url_i+","+url_p);
        }
        protected Boolean doInBackground(Void... id) {
            try {
                System.setProperty("http.keepAlive", "false");
                if(url_i!=null && !url_i.equals("-"))
                    no.icon=getImg(url_i);
                if(url_p!=null && !url_p.equals("-"))
                    no.picture=getImg(url_p);
                return m.db.saveNews(no)>0;
            } catch (Exception e) {
                Log.i("hz", "getImgs-E",e);
            }
            return false;
        }
        protected void onPostExecute(Boolean ok) {
            //Log.i("hz","getIMG:"+(ok?1:0)+":"+url_p);
            if(ok) {
                DOWNLOADED++;
            }else{
                return;
            }
            try{
                showNewsNotify(no);
                //Log.i("hz","img down:"+DOWNLOADED+","+DOWNLOAD_COUNT);
                //if(DOWNLOADED==1)((m)context).changeAD();
            }catch (Exception e){
                Log.i("hz","notify e",e);
            }
        }
        byte[] getImg(String url) throws Exception{
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            int imageLength = (int)(entity.getContentLength());
            InputStream is = entity.getContent();

            byte[] imageBlob = new byte[imageLength];
            int bytesRead = 0;
            while (bytesRead < imageLength) {
                int n = is.read(imageBlob, bytesRead, imageLength - bytesRead);
                if (n <= 0)
                    throw new Exception("dowload error");
                bytesRead += n;
            }
            return imageBlob;
        }
    }
    public static boolean isPackageInstalled(Context c,String packagename) {
        PackageManager pm = c.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static class NewsObj{
        public int id,code,status,type,iconID;
        public long date;
        public String pack,title,text,link;
        public void setType(Context co,int t){
            code=t;
            type=t%10;
            int i=t/10;
            iconID=co.getResources().getIdentifier("no"+i,"drawable",co.getPackageName());
            //Log.i("hz","icon("+id+"):"+type+","+iconID+"("+i+");"+code);
        }
        public byte[] icon,picture;
        public Bitmap poster=null;
        public Bitmap getPoster(){
            if(poster!=null)
                return poster;
            if(picture==null)
                return null;
            poster=BitmapFactory.decodeByteArray(picture,0,picture.length);
            //picture=null;
            System.gc();
            return poster;
        }
    }


    public static String convertStreamToString(InputStream is) throws Exception {

        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            Boolean first=true;
            String l;
            while ((line = reader.readLine()) != null) {
                if(first){
                    first=false;
                    l=line;
                }else
                    l="\n"+line;
                sb.append(l);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static class Net extends BroadcastReceiver {
        public void onReceive(final Context context, Intent intent) {
            //super.onReceive(context, intent);
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    if(m.db==null)
                        m.db=new DB(context);
                    (new n(context)).getNews();
                }
            }
        }
    }
}
