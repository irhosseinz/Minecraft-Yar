package ir.boozar.minecraft;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hossein on 10/27/14.
 */
public class ActivityRecipe extends Activity {

    public static final String INTENT_ITEM="ITEM";
    public static final String INTENT_FORCE_SHOW="FORCE";

    private int ITEM;
    private boolean FORCE_SHOW;

    private static double WIDTH;
    private static final double RECIPE_WIDTH_RATIO=0.6;

    private static DBD Dbd;
    private static Context context;

    private static int RECIPE_INDEX=0;
    private static LinearLayout RECIPE_CONTAINER;
    private static ScrollView RECIPE_SCROLL;
    private static Map<Integer,Integer> RECIPE_POSITION;
    public static void addRecipe(int item){
        if(RECIPE_POSITION.containsKey(item)){
            RECIPE_SCROLL.smoothScrollTo(0,RECIPE_POSITION.get(item)-50);
            return;
        }
        List<DBD.RecipeObj> list;
        try{
            list=Dbd.getItemRecipe(item, false);
        }catch (Exception e){
            return;
        }
        if(list.size()==0){
            Toast.makeText(context,context.getResources().getString(R.string.items_no_recipe),Toast.LENGTH_LONG).show();
        }else{
            Animation anim= AnimationUtils.loadAnimation(context, R.anim.scale_down_open);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0,10,20,10);
            //int position=-1;
            for(DBD.RecipeObj r:list){
                LinearLayout ll2=new LinearLayout(context);
                ll2.setOrientation(LinearLayout.HORIZONTAL);
                RecipeView rv;
                try{
                    rv=r.getRecipeView(context,Dbd,(int)(WIDTH*RECIPE_WIDTH_RATIO),true);
                    rv.productCouldBeClicked=false;
                }catch (Exception e){
                    Log.e("hz", "error on add recipe", e);
                    break;
                }
                ll2.addView(rv);
                TextView tv=new TextView(context);
                tv.setText(rv.getName());
                tv.setTextColor(Color.parseColor("#babdb6"));
                tv.setTextSize(17);
                tv.setGravity(Gravity.CENTER);
                tv.setLayoutParams(params);
                ll2.addView(tv);
                RECIPE_CONTAINER.addView(ll2,RECIPE_INDEX);
                if(!RECIPE_POSITION.containsKey(item)){
                    int pos=RECIPE_CONTAINER.getChildAt(RECIPE_INDEX-1).getBottom();
                    RECIPE_POSITION.put(item,pos);
                    RECIPE_SCROLL.smoothScrollTo(0,pos-50);
                    //Log.i("hz","got top:"+pos);
                }
                if(anim!=null)
                    ll2.startAnimation(anim);
            }
            RECIPE_INDEX+=list.size();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recipe);

        Bundle extras=getIntent().getExtras();
        if(extras!=null) {
            ITEM = extras.getInt(INTENT_ITEM);
            FORCE_SHOW=extras.getBoolean(INTENT_FORCE_SHOW);
        }else {
            ITEM = 1;
            FORCE_SHOW=false;
        }

        context=this;
        setWidth();

        Dbd= m.Dbd;

        DBD.ItemObj i=Dbd.getItem(ITEM);
        TextView tv=(TextView) findViewById(R.id.name);
        tv.setText(i.getName());
        ImageView iv=(ImageView) findViewById(R.id.photo);
        iv.setImageBitmap(BitmapFactory.decodeByteArray(i.pic,0,i.pic.length));

        update();
    }
    public void update(){
        LinearLayout ll=(LinearLayout) findViewById(R.id.mainMenu);
        ll.removeAllViews();

        RECIPE_CONTAINER=ll;
        RECIPE_SCROLL=(ScrollView) findViewById(R.id.mainScroll);
        RECIPE_POSITION=new HashMap<Integer, Integer>();

        TextView tv;

        List<DBD.RecipeObj> list=Dbd.getItemRecipe(ITEM, false);
        if(list.size()==0){
            Toast.makeText(this,getResources().getString(R.string.items_no_recipe),Toast.LENGTH_LONG).show();
            if(!FORCE_SHOW)
                finish();
        }else{
            tv=new TextView(this);
            tv.setText(getResources().getString(R.string.items_produstion));
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(20);
            tv.setGravity(Gravity.RIGHT);
            tv.setPadding(0,10,30,5);
            ll.addView(tv);
            for(DBD.RecipeObj r:list){
                RecipeView rv;
                try{
                    rv=r.getRecipeView(this,Dbd,(int)(WIDTH*RECIPE_WIDTH_RATIO),true);
                }catch (Exception e){
                    break;
                }
                rv.productCouldBeClicked=false;
                ll.addView(rv);
                if(!RECIPE_POSITION.containsKey(ITEM))
                    RECIPE_POSITION.put(ITEM,rv.getTop());
            }
            RECIPE_INDEX=list.size()+1;
        }

        list=Dbd.getItemRecipe(ITEM,true);
        if(list.size()>0){
            tv=new TextView(this);
            tv.setText(getResources().getString(R.string.items_uses));
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(20);
            tv.setGravity(Gravity.RIGHT);
            tv.setPadding(0,10,30,5);
            ll.addView(tv);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0,10,20,10);
            for(DBD.RecipeObj r:list){
                LinearLayout ll2=new LinearLayout(this);
                ll2.setOrientation(LinearLayout.HORIZONTAL);
                RecipeView rv;
                try{
                    rv=r.getRecipeView(this,Dbd,(int)(WIDTH*RECIPE_WIDTH_RATIO),false);
                }catch (Exception e){
                    break;
                }
                loop1:for(int i=0;i<rv.getMaxItems();i++){
                    for(int it:rv.getCurrentRecipe())
                        if(it==ITEM)
                            break loop1;
                    rv.changeImage();
                }
                ll2.addView(rv);
                tv=new TextView(this);
                //tv.setText(rv.getName());
                tv.setTextColor(Color.parseColor("#babdb6"));
                tv.setTextSize(15);
                tv.setGravity(Gravity.CENTER);
                tv.setLayoutParams(params);
                ll2.addView(tv);
                ll.addView(ll2);
            }
        }
        doLoop();
    }
    private void setWidth(){
        Display display = getWindowManager().getDefaultDisplay();

        if(Build.VERSION.SDK_INT>12){
            Point size = new Point();
            display.getSize(size);
            WIDTH = size.x;
            //height = size.y;
        }else{
            WIDTH = display.getWidth();
            //height = display.getHeight();
        }
    }
    private boolean pageIsActive=true;
    private Handler looper=null;
    private void doLoop(){
        if(!pageIsActive)
            return;
        if(looper==null)
            looper=new Handler();
        for(int i=0;i<RecipeView.RECIPE_LOOP.size();i++)
            RecipeView.RECIPE_LOOP.get(RecipeView.RECIPE_LOOP.keyAt(i)).changeImage();
        looper.postDelayed(new Runnable() {
            @Override
            public void run() {
                doLoop();
            }
        },RecipeView.LOOP_TIME);
    }
    @Override
    public void onDestroy(){
        RecipeView.recycle();
        pageIsActive=false;
        super.onDestroy();
    }
}
