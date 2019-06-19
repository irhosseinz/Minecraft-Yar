package ir.boozar.minecraft;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.net.URLConnection;
import java.util.Locale;

/**
 * Created by hossein on 2/7/15.
 */
public class kj {
    Context c;
    int sW;
    static DL dl=null;
    final String LINK="http://dl.boozar.ir/awax.apk";
    public kj(Context co,int sWidth){
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
        tv.setText("آواکس");
        tv.setTextColor(Color.parseColor("#eeeeec"));
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(Color.parseColor("#1b4683"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        //tv.setTypeface(Typeface.createFromAsset(c.getAssets(), m.FONT2), Typeface.BOLD);
        ll.addView(tv);
        final TextView tv2=new TextView(c);
        tv2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv2.setText("آواکس یک مکانیاب حرفه ای است که می تواند برای کنترل نامحسوس اعضای خانواده، دوستان و... مورد استفاده قرار گیرد.\nبرای نصب برنامه بر روی دریافت کلیک کنید یا برای اطلاعات بیشتر به صفحه برنامه در بوزار مراجعه نمایید");
        tv2.setTextColor(Color.parseColor("#1b4683"));
        tv2.setGravity(Gravity.CENTER);
        tv2.setLineSpacing(10,1);
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
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
                tv2.setText(Html.fromHtml("آیا دوست دارید بدانید:<br>فرزندانتان ، همسر ، دوستان ، پرسنل تان به کجاها رفت و آمد دارند؟<br>آیا از گم شدن یا دزدیده شدن عزیزان خود احساس خطر می کنید؟<br><br>ما در اینجا راهکار تمامی این خواسته ها را پیش پای شما می گذاریم اینک با نصب و استفاده از نرم افزار \"آواکس \" می توانید موقعیت و محل (همسر ، فرزندان ، دوستان ، کارمندان ) خود را در هر لحظه  بدانید. <br><h4>آواکس چیست؟</h4><br>آواکس یک برنامه حرفه ای است که برای مکانیابی نزدیکان و آشنایان طراحی شده است.<br>شیوه کار این برنامه به اینصورت است که شما برنامه را بر روی گوشی خود و فردی که می خواهید او را مکانیابی کنید نصب می کنید و بعد از اعمال تنظیمات لازم از آن به بعد در هر لحظه و موقعیت با فرستادن یک پیامک می توانید آخرین مکان فرد مورد نظر را در قالب یک پیامک دریافت کنید.<br><h4>قابلیت ها</h4><br>- امکان مکانیابی لحظه ای یک شماره با استفاده از پیامک<br>- مکانیابی کاملا نامحسوس و آنی یه مخاطب<br>- وجود سه نوع پیامک مکانیابی (فارسی، انگلیسی و پنهان) جهت شرایط مختلف<br>- تشخیص خودکار آدرس مکان ها<br>- ارائه گزارش دقیق از مکانیابی های خروجی و ورودی<br><h4>نکته ها</h4><br>۱) جواب پیامک مکانیابی به محض اینکه شما مکان را درخواست کنید و بطور خودکار توسط برنامه موجود در گوشی مقابل ارسال می شود<br>۲) مخاطب شما در هر حالتی باشد مهم نیست! چه در حال رانندگی باشد یا حتی از گوشی اش غافل باشد یا دسترسی به آن نداشته باشد تنها کافیست گوشی او آنتن بدهد تا بتوانید او را مکانیابی کنید<br>۳) درخواست مکان و جواب آن از طریق پیامک انجام می گیرد لذا در هر درخواست مکان یک پیامک برای شما و همچنین طرف مقابل حساب خواهد شد<br><h4>راهنمای شروع</h4><b><u> (<a href=\"http://www.aparat.com/v/x4wna\">ویدیوی آموزش اولیه</a>)</u></b><br>۱) برنامه را بر روی گوشی خودتان و دوستتان نصب کنید<br>۲) از روشن بودن مکانیاب (GPS) گوشی دوستتان اطمینان یابید و سپس برنامه را باز کنید و منتظر پیام \"مکان جدید ثبت شد\" بمانید. ممکن است در زیر سقف یا فضای بسته امکان ثبت مکان وجود نداشته باشد<br>۳) در <u>هر دو گوشی</u> در صفحه اول برنامه شماره گوشی دیگر را در قالب یک مخاطب جدید اضافه کنید<br>۴) در گوشی خود نام مخاطبی که اضافه کرده اید را لمس کنید تا به صفحه مکانیابی اختصاصی آن فرد وارد شوید<br>۵) در بالای صفحه باز شده نام مخاطب را لمس کنید<br>۶) شماره ای از مخاطب که می خواهید پیامک درخواست مکان به آن ارسال شود را انتخاب کنید<br>۷) اگر گوشی شما و دوستتان از پیامک فارسی پشتیبانی می کند پیامک فارسی را انتخاب کنید و اگر نه پیامک فینگیلیش را انتخاب کنید<br>۸) شاهد مکانیابی آنی باشید!!<br><br><h3>سوالات متداول</h3><br><b>نقشه کار نمیکنه یا لود نمیشه</b>:<br>برای کار کردن نقشه و همچنین آدرس ها برنامه سرویس های گوگل باید بر روی گوشی شما نصب شده باشد. اگر این برنامه بر روی گوشی شما نصب نباشد یا این برنامه نیاز به بروزرسانی داشته باشد، در هنگام شروع برنامه پیامی به شما نشان داده می شود که راهنمایی های لازم برای نصب آن در آنجا وجود دارد. اگر این برنامه نصب شده باشد با لمس هر مخاطب در صفحه اصلی برنامه به صفحه نقشه وارد خواهید شد. توجه کنید که در شروع اول حتما ارتباط اینترنت شما برقرار باشد. زیرا در شروع اول نقشه گوگل نیاز به اعتبارسنجی دستگاه شما دارد.<br><br><b>آدرس مکان به همراه جواب مکانیابی ارسال نمی شود</b>:<br>آدرس هر مکان باید از اینترنت دریافت شود، لذا اگر در هنگام ثبت هر مکان اتصال به اینترنت برقرار باشد، آدرس آن مکان هم در همان لحظه ثبت خواهد شد و آن آدرس به همراه پیامک ها ارسال خواهد شد. اگر در جواب مکانیابی آدرسی به همراه آن دریافت نکردید با اتصال به اینترنت و باز کردن نقشه آدرس بطور خودکار برای آن مخاطب ثبت خواهد شد.<br><br><b>پیامک پنهان کار نمی کند</b>:<br>پیامک پنهان معمولا در ارتباط های بین شماره هایی که از یک نوع اپراتور نیستند کار نمی کند. یعنی هر دوشماره باید همراه اول باشد، یا هر دو ایرانسل یا ...<br><br><b>برنامه زیاد باتری مصرف میکند</b>:<br>مصرف زیاد باتری در برنامه بدلیل ثبت مکان های شماست که هر چند دقیقه انجام خواهد شد. شما می توانید با ورود به بخش تنظیمات، دوره ثبت مکان ها را زیادتر کنید تا برنامه در بازه های طولانی تر مکان شما را ثبت کند. بعد از تغییر تنظیمات ممکن است همان لحظه تغییرات اعمال نشود!"));
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
            out=new File(Environment.getExternalStorageDirectory(),"awax.apk");
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
                if(conexion.getResponseCode()!=200)
                    return false;
                Log.i("hz", "Lenght of file: " + lenghtOfFile);

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
                return false;
            }
            return true;
        }
        protected void onProgressUpdate(String... progress) {
            //Log.i("hz",progress[0]);
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
                            ,"در حال دانلود برنامه مکانیاب. %dدرصد دریافت شد"
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
