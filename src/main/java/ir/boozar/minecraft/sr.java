package ir.boozar.minecraft;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by hossein on 2/7/15.
 */
public class sr {
    Context c;
    int sW;
    static DL dl=null;
    final String LINK="http://dl.boozar.ir/sarketab.apk";
    public sr(Context co, int sWidth){
        c=co;
        sW=sWidth;
    }
    public void openIntro(){
        LinearLayout ll=new LinearLayout(c);
        ll.setLayoutParams(new LinearLayout.LayoutParams(m.sW*19/20, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.setPadding(5,5,5,5);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.setBackgroundColor(Color.parseColor("#eeeeec"));
        TextView tv=new TextView(c);
        tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setText("سرکتاب");
        tv.setTextColor(Color.parseColor("#eeeeec"));
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(Color.parseColor("#1b4683"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        //tv.setTypeface(Typeface.createFromAsset(c.getAssets(), m.FONT2), Typeface.BOLD);
        ll.addView(tv);
        final TextView tv2=new TextView(c);
        tv2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv2.setText("سرکتاب و بررسی طالع و مشکلات ان با سرکتاب درویش محمد . بدست اوردن دل طرفتان . بررسی مشکلات مالی و حل آن جذب کردن همه اطرافیان . زیاد شدن رزق و روزی و گشایش بخت و طالع بسته شده و بستن زبان");
        tv2.setTextColor(Color.parseColor("#1b4683"));
        tv2.setGravity(Gravity.CENTER);
        tv2.setLineSpacing(10,1);
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
        //tv2.setTypeface(Typeface.createFromAsset(c.getAssets(), m.FONT2));
        tv2.setMovementMethod(LinkMovementMethod.getInstance());
        ll.addView(tv2);
        final ProgressDialog prog=new ProgressDialog(c);
        prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        if(dl==null)
            dl=new DL(prog);
        else
            dl.prog=prog;
        LinearLayout ll2=new LinearLayout(c);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        ll2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Button ok=new Button(c);
        ok.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ok.setPadding(40,40,40,40);
        ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        ok.setText("اطلاعات بیشتر");
        ok.setBackgroundResource(R.drawable.button_blue);
        //ok.setTypeface(Typeface.createFromAsset(c.getAssets(), m.FONT2));
        ok.setTextColor(Color.BLACK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*c.startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://www.boozar.ir/i/33")));
                */
                tv2.setText(Html.fromHtml("بررسی طالع و بستگی و کشف هر نوع <font color=\"#ff0000\">سحر و جادو و طلسم و همزاد</font> در طالع شما و حل آن.<br>گنجینه ای از دعاهای نایاب شامل محبت، زبانبند، رزق و روزی، دفع سحر، گشایش و ..غیره ...<br>بررسی جفت بودن طالع و ستاره پسر و دختر<br>محاسبه ساعات سعدو نحس برای دعا نویسی و استخاره<br>بررسی طالع روزانه و حالات روحی و مشخصات طالع<br>مشاوره در کلیه امور"));
                v.setVisibility(View.GONE);
            }
        });
        ll2.addView(ok);
        ok=new Button(c);
        ok.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ok.setPadding(40,40,40,40);
        ok.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        ok.setText(dl.downloded ? "نصب برنامه" : "دریافت");
        ok.setBackgroundResource(R.drawable.button_green);
        //ok.setTypeface(Typeface.createFromAsset(c.getAssets(), m.FONT2));
        ok.setTextColor(Color.BLACK);
        ll2.addView(ok);
        ll.addView(ll2);
        ScrollView sv=new ScrollView(c);
        sv.addView(ll);

        final Dialog dialog = new Dialog(c);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(sv);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dl.startSetup()) {
                    if(!isNetworkAvailable()){
                        Toast.makeText(c,"دسترسی به اینترنت وجود ندارد",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(dl.executed)
                        dl=new DL(prog);
                    dl.execute();
                }
            }
        });

        dialog.show();
    }
    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    class DL extends AsyncTask<String, String, Boolean> {
        File out;
        ProgressDialog prog;
        boolean downloded=false;
        public boolean executed=false;
        public DL(ProgressDialog pr){
            prog=pr;
            out=new File(Environment.getExternalStorageDirectory(),"sarketab.apk");
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prog.show();
        }

        @Override
        protected Boolean doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(LINK);
                HttpURLConnection conexion = (HttpURLConnection)url.openConnection();
                conexion.setRequestProperty("Accept-Encoding", "identity");
                conexion.connect();

                /*Map<String, List<String>> list=conexion.getHeaderFields();
                for(Map.Entry<String, List<String>> entry : list.entrySet()) {
                    Log.i("hz","head:"+entry.getKey()+";;"+entry.getValue().toString());
                }*/
                int lenghtOfFile = conexion.getContentLength();
                Log.i("hz", "sr dl: " + conexion.getResponseCode());
                if(conexion.getResponseCode()!=200)
                    return false;

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(out);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.i("hz","sr dl e",e);
                return false;
            }
            return true;
        }
        protected void onProgressUpdate(String... progress) {
            int p=Integer.parseInt(progress[0]);
            prog.setProgress(p);
            try{
                showProgNotify(p);
            }catch (Exception e){

            }
        }

        @Override
        protected void onPostExecute(Boolean ok) {
            Log.i("hz","downloaded");
            executed=true;
            prog.dismiss();
            downloded=ok;
            if(!ok){
                Toast.makeText(c,"یک مشکل در دریافت فایل رخ داد",Toast.LENGTH_SHORT).show();
                return;
            }
            startSetup();
        }
        int lastProg;
        int progID=0;
        void showProgNotify(int prog) throws Exception{
            if(Build.VERSION.SDK_INT<11)
                return;
            if(prog==lastProg)
                return;
            lastProg=prog;

            if(progID==0){
                progID=(int)(System.currentTimeMillis()/10000);
            }

            Notification.Builder nb  = new Notification.Builder(c)
                    .setContentText(String.format(
                            new Locale("fa")
                            ,"در حال دانلود برنامه سرکتاب. %dدرصد دریافت شد"
                            ,prog
                    ))
                    .setContentTitle("در حال دریافت")
                    .setSmallIcon(R.drawable.downloading)
                    .setAutoCancel(false)
                    .setProgress(100,prog,false)
                    .setWhen(0);
            if(prog==100){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(out), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
                PendingIntent pIntent = PendingIntent.getActivity(c, progID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                nb.setContentIntent(pIntent);
                nb.setAutoCancel(true);
            }
            Notification n;
            if(Build.VERSION.SDK_INT<16)
                n=nb.getNotification();
            else
                n=nb.build();

            NotificationManager notificationManager =
                    (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(progID, n);
        }
        public boolean startSetup(){
            if(!downloded)
                return false;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(out), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(intent);
            return true;
        }
    }
}
