package ir.boozar.minecraft;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class m extends Activity {

    public static DBD Dbd;
    public static DB db;
    private Context context;

    Resources re;
    AssetManager as;
    SharedPreferences pref;

    public static final String P_RUN="run";
    public static final String P_RATE="rate";
    public static final String FONT="naz.ttf";

    private boolean isWaiting,minWaiting;

    int runCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setScreenSize();

        (new n(this)).getNews();

        re=getResources();
        as=getAssets();
        pref= PreferenceManager.getDefaultSharedPreferences(this);

        runCount=pref.getInt(P_RUN,0);
        SharedPreferences.Editor e=pref.edit();
        e.putInt(P_RUN,runCount+1);
        e.commit();

        context=this;

        final Handler h=new Handler();
        isWaiting=true;
        minWaiting=false;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isWaiting){
                    ImageView iv=(ImageView) findViewById(R.id.wait);
                    iv.setVisibility(View.GONE);
                    upgrade();
                }
                minWaiting=true;
            }
        },6000);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                DBD.DBD_TEMP Dbd0=new DBD.DBD_TEMP(context);
                Dbd0.getWritableDatabase();
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        if(minWaiting){
                            ImageView iv=(ImageView) findViewById(R.id.wait);
                            iv.setVisibility(View.GONE);
                        }
                        isWaiting=false;
                    }
                });
            }
        });
        thread.start();

        Dbd=new DBD(this);
        db=new DB(this);
        //List<DBD.RecipeObj> list=Dbd.getRecipe(18);
        //int size=list.size();
        LinearLayout ll=(LinearLayout) findViewById(R.id.mainMenu);

        Button btn=(Button) findViewById(R.id.recipes);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecipeCategories();
            }
        });
        btn=(Button) findViewById(R.id.videos);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, ActivityVideos.class));
            }
        });
        btn=(Button) findViewById(R.id.brewing);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context,ActivityTourial.class));
            }
        });
        btn=(Button) findViewById(R.id.rating);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(
                            Intent.ACTION_EDIT,
                            Uri.parse("bazaar://details?id=ir.boozar.minecraft")));
                } catch (Exception e) {
                    Toast.makeText(context,getResources().getString(R.string.msg_noCFB), Toast.LENGTH_LONG).show();
                }
            }
        });
        btn=(Button) findViewById(R.id.exit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!pref.getBoolean(P_RATE,false)){
//                    openRate(true);
//                    return;
//                }
                finish();
            }
        });

        ImageButton ib=(ImageButton) findViewById(R.id.back);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                showMainMenu();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        System.gc();

        changeAD();
    }


    void changeAD(){
        final n.NewsObj[] l=db.getNews(1,true);
        if(l==null || l.length==0)
            return;
        Bitmap b= l[0].getPoster();
        if(b==null)
            return;
        if(n.isPackageInstalled(this,l[0].pack)){
            db.delNewsPack(l[0].pack);
        }

        ImageView iv=(ImageView) findViewById(R.id.ad);
        iv.setImageBitmap(b);
        RelativeLayout.LayoutParams p=(RelativeLayout.LayoutParams) iv.getLayoutParams();
        p.width=sW*3/4;
        p.height=b.getHeight()*3*sW/(4*b.getWidth());
        iv.setClickable(true);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAds(l[0].pack,l[0].link);
            }
        });

    }
    void openAds(String pack,String link){
        switch (pack){
            case "ir.boozar.kojayi":
                (new kj(m.this,sW)).openIntro();
                break;
            case "ir.boozar.sarketab":
                (new sr(m.this,sW)).openIntro();
                break;
            case "ir.boozar.youtube":
                if(link.startsWith("http")){
                    (new yt(m.this,sW)).openIntro();
                    break;
                }
            default:
                if(link!=null && link.startsWith("http"))
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(link)));
                else
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("bazaar://details?id=" + pack)));
        }
    }

    public static int sW,sH;
    public void setScreenSize(){
        Display display=getWindowManager().getDefaultDisplay();
        if(sW!=0 && sH!=0)
            return;
        if(Build.VERSION.SDK_INT>12){
            Point size = new Point();
            display.getSize(size);
            sW = size.x;
            sH = size.y;
        }else{
            sW = display.getWidth();
            sH = display.getHeight();
        }
    }


    private boolean isShowingMenu=true;
    private void showMainMenu(){
        isShowingMenu=true;
        ImageButton ib=(ImageButton)findViewById(R.id.back);
        ib.setVisibility(View.GONE);
        LinearLayout ll=(LinearLayout) findViewById(R.id.mainMenu);
        ll.setVisibility(View.VISIBLE);
        ScrollView sv=(ScrollView) findViewById(R.id.categories);
        sv.setVisibility(View.GONE);
    }
    private void showRecipeCategories(){
        isShowingMenu=false;
        LinearLayout ll=(LinearLayout) findViewById(R.id.mainMenu);
        ll.setVisibility(View.GONE);
        ll=(LinearLayout) findViewById(R.id.categoriesC);
        List<DBD.CategoryObj> list=Dbd.getCategories();
        ll.removeAllViews();
        Button btn;
        for(final DBD.CategoryObj c:list){
            btn=new Button(this);
            btn.setTextColor(Color.WHITE);
            btn.setText(c.getName());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,ActivityRecipes.class);
                    i.putExtra("CAT",c.id);
                    startActivity(i);
                }
            });
            ll.addView(btn);
        }
        ScrollView sv=(ScrollView) findViewById(R.id.categories);
        sv.setVisibility(View.VISIBLE);

        ImageButton ib=(ImageButton) findViewById(R.id.back);
        ib.setVisibility(View.VISIBLE);
    }
    private boolean exiting=false;
    @Override
    public void onBackPressed(){
        if(isShowingMenu){
            if(!exiting){
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exiting=false;
                    }
                },2000);
                exiting=true;
                Toast.makeText(this,getResources().getString(R.string.msg_exit),Toast.LENGTH_SHORT).show();
                return;
            }
//            if(!pref.getBoolean(P_RATE,false)){
//                openRate(true);
//                return;
//            }
            super.onBackPressed();
        }else
            showMainMenu();
    }


    void openRate(final boolean exit){
        final Dialog d=new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout ll=new LinearLayout(this);
        d.setContentView(ll);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.setBackgroundColor(Color.WHITE);
        ImageView iv=new ImageView(this);
        iv.setImageResource(R.drawable.gags);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setLayoutParams(new LinearLayout.LayoutParams(sW*4/5,sW*26/100));
        ll.addView(iv);
        TextView tv=new TextView(this);
        tv.setTypeface(Typeface.createFromAsset(as, FONT));
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setText(Html.fromHtml(
                re.getString(R.string.rate_desc)
        ));
        tv.setGravity(Gravity.CENTER);
        ll.addView(tv);
        LinearLayout ll2=new LinearLayout(this);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        ll2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.addView(ll2);
        Button btn=new Button(this);
        btn.setText("فعلا نه");
        btn.setBackgroundResource(R.drawable.button_red);
        btn.setTypeface(Typeface.createFromAsset(as, FONT));
        btn.setTextColor(Color.BLACK);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        btn.setPadding(10, 10, 10, 10);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                if(exit) {
                    finish();
                }
            }
        });
        ll2.addView(btn);
        btn=new Button(this);
        btn.setText("میخوام نظر بدم");
        btn.setBackgroundResource(R.drawable.button_green);
        btn.setTypeface(Typeface.createFromAsset(as, FONT));
        btn.setTextColor(Color.BLACK);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        btn.setPadding(10, 10, 10, 10);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    startActivity(n.getRateIntent(m.this));
                    SharedPreferences.Editor e=pref.edit();
                    e.putBoolean(P_RATE,true);
                    e.commit();
                }catch (Exception e){

                }
                d.dismiss();
                if(exit)
                    finish();
            }
        });
        ll2.addView(btn);
        d.show();
    }

    void upgrade(){
        int v0,v=pref.getInt(n.PREF_LASTVER,0);
        try{
            v0=getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        }catch (Exception e){
            v0=0;
        }
        if(v==0 || v0==0 || v0>=v)
            return;
        final Dialog d=new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout ll=new LinearLayout(this);
        d.setContentView(ll);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.setBackgroundColor(Color.parseColor("#981F24"));
        ImageView iv=new ImageView(this);
        iv.setImageResource(R.drawable.update);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setLayoutParams(new LinearLayout.LayoutParams(sW*4/5,sW*4/10));
        ll.addView(iv);
        TextView tv=new TextView(this);
        tv.setTypeface(Typeface.createFromAsset(as,FONT));
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setText(re.getString(R.string.update));
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(5,5,5,15);
        ll.addView(tv);
        LinearLayout ll2=new LinearLayout(this);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
        ll2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.addView(ll2);
        Button btn=new Button(this);
        btn.setText("فعلا نه");
        btn.setBackgroundResource(R.drawable.button_red);
        btn.setTypeface(Typeface.createFromAsset(as, FONT));
        btn.setTextColor(Color.BLACK);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        btn.setPadding(10, 10, 10, 10);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        ll2.addView(btn);
        btn=new Button(this);
        btn.setText("ارتقا می دهم");
        btn.setBackgroundResource(R.drawable.button_green);
        btn.setTypeface(Typeface.createFromAsset(as, FONT));
        btn.setTextColor(Color.BLACK);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        btn.setPadding(10, 10, 10, 10);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("bazaar://details?id="+getPackageName())));
                }catch (Exception e){
                    Toast.makeText(m.this,"برنامه کافه بازار در گوشی شما نصب نیست",Toast.LENGTH_SHORT).show();
                }
                d.dismiss();
            }
        });
        ll2.addView(btn);
        d.show();
    }
}
