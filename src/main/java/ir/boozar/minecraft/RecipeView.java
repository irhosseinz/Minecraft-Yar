package ir.boozar.minecraft;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hossein on 10/29/14.
 */
public class RecipeView extends LinearLayout {
    private Context mContext;
    public int width,height;
    private int productCount=1;
    private boolean isShapeless=false;

    public static final int LOOP_TIME=500;

    public boolean productCouldBeClicked=true;

    private int[][] RECIPE;
    private int[] RECIPE_CURRENT;
    private int[] PRODUCTS;
    private int PRODUCT_CURRENT;
    public static SparseArray<Bitmap> RECIPE_PIC=new SparseArray<Bitmap>();
    public static SparseArray<RecipeView> RECIPE_LOOP=new SparseArray<RecipeView>();
    private boolean addedToLooper=false;
    public static SparseArray<String> RECIPE_NAME=new SparseArray<String>();
    private int MAX_ITEMS=0,CURRENT_STATUS=-1;
    public int getMaxItems(){
        return MAX_ITEMS;
    }
    public boolean shouldBeLooped(){
        return MAX_ITEMS>0;
    }
    public int[] getCurrentRecipe(){
        return RECIPE_CURRENT;
    }

    public static boolean saveItemData(DBD db,int item){
        if (RECIPE_PIC.get(item,null)==null) {
            try{
                DBD.ItemObj i = db.getItem(item);
                if(RECIPE_NAME.get(item,null)==null)
                    RECIPE_NAME.put(item,i.getName());
                //Log.i("hz","setName:"+item+" => "+i.getName());
                RECIPE_PIC.put(
                        item
                        , BitmapFactory
                                .decodeByteArray(
                                        i.pic, 0, i.pic.length)
                );
                return true;
            }catch (Exception e){
                Log.i("hz","can not get item:"+item,e);
            }
        }
        return false;
    }

    public RecipeView(Context c
            ,int width,int proCount
            ,boolean shapeless
            ,boolean looper){
        super(c);
        this.productCount=proCount;
        this.isShapeless=shapeless;
        looperEnable=looper;
        mContext=c;
        this.width=width;
        height=width*5/10;
        LinearLayout.LayoutParams params=
                new LinearLayout.LayoutParams
                        (width,height);
        params.setMargins(15,15,15,15);
        setLayoutParams(params);
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.parseColor("#C6C6C6"));
        setGravity(Gravity.CENTER_VERTICAL);

        RECIPE_CURRENT=new int[9];
        Arrays.fill(RECIPE_CURRENT, 0);

        addParts();
    }

    public String getName(){
        String o="";
        for(int i:PRODUCTS){
            if(RECIPE_NAME.get(i,null)!=null)
                o+=RECIPE_NAME.get(i)+"\n";
        }
        return o;
    }

    private ImageView[] RecipeParts;
    private ImageView RecipeProduct;
    public static final int CELL_COLOR=Color.parseColor("#8B8B8B");
    public void addParts(){
        TableLayout tl=new TableLayout(mContext);
        int t1Size=height*4/5;
        int sizeUnit=t1Size/3;
        LinearLayout.LayoutParams paramsM=
                new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsM.setMargins(sizeUnit/4,0,sizeUnit/2,0);
        tl.setLayoutParams(paramsM);
        tl.setBackgroundColor(Color.WHITE);

        TableRow tr=new TableRow(mContext);
        RecipeParts=new ImageView[9];
        for(int i=0;i<RecipeParts.length;i++)
            RecipeParts[i]=new ImageView(mContext);
        TableRow.LayoutParams params=
                new TableRow.LayoutParams
                        (sizeUnit,sizeUnit);
        params.setMargins(2,2,2,2);
        int c=0;
        for(ImageView iv:RecipeParts){
            iv.setLayoutParams(params);
            iv.setBackgroundColor(CELL_COLOR);
            iv.setClickable(true);
            final int current=c++;
            iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAnotherRecipe(RECIPE_CURRENT[current]);
                }
            });
            iv.setLongClickable(true);
            iv.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.i("hz", "click:" + current + "," + RECIPE_CURRENT[current]);
                    if(RECIPE_CURRENT[current]>0){
                        Toast.makeText(mContext,RECIPE_NAME.get(RECIPE_CURRENT[current]),Toast.LENGTH_SHORT).show();
                        /*TextView tv=new TextView(mContext);
                        tv.setText(Html.fromHtml(String.format(
                                mContext.getResources().getString(R.string.item_toast)
                                , RECIPE_NAME.get(RECIPE_CURRENT[current])
                        )));
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextColor(Color.WHITE);
                        tv.setBackgroundColor(Color.BLACK);
                        tv.setPadding(5,5,5,5);
                        Toast t=new Toast(mContext);
                        t.setView(tv);
                        t.setDuration(Toast.LENGTH_LONG);
                        t.show();*/
                        return true;
                    }
                    return false;
                }
            });
        }
        tr.addView(RecipeParts[0]);
        tr.addView(RecipeParts[1]);
        tr.addView(RecipeParts[2]);
        tl.addView(tr);
        tr=new TableRow(mContext);
        tr.addView(RecipeParts[3]);
        tr.addView(RecipeParts[4]);
        tr.addView(RecipeParts[5]);
        tl.addView(tr);
        tr=new TableRow(mContext);
        tr.addView(RecipeParts[6]);
        tr.addView(RecipeParts[7]);
        tr.addView(RecipeParts[8]);
        tl.addView(tr);
        addView(tl);

        LinearLayout ll=new LinearLayout(mContext);
        ll.setOrientation(VERTICAL);
        ll.setPadding(0,height/2-60,0,0);
        paramsM=new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        paramsM.setMargins(sizeUnit/4,0,sizeUnit/4,0);
        ll.setLayoutParams(paramsM);
        ImageView iv=new ImageView(mContext);
        paramsM=new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsM.gravity=Gravity.CENTER_VERTICAL;
        iv.setLayoutParams(paramsM);
        iv.setImageResource(R.drawable.arrow);
        ll.addView(iv);
        if(isShapeless){
            iv=new ImageView(mContext);
            paramsM.setMargins(0,20,0,0);
            iv.setLayoutParams(paramsM);
            iv.setImageResource(R.drawable.shapeless);
            ll.addView(iv);
        }
        addView(ll);

        final RelativeLayout rl=new RelativeLayout(mContext);
        rl.setPadding(sizeUnit / 2
                , 0, sizeUnit / 2, 0);
        //ll.setLayoutParams(paramsM);
        RecipeProduct=new ImageView(mContext);
        RecipeProduct.setClickable(true);
        RecipeProduct.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(productCouldBeClicked){
                    openAnotherRecipe(PRODUCT_CURRENT);
                }
            }
        });
        RecipeProduct.setLongClickable(true);
        RecipeProduct.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(mContext,RECIPE_NAME.get(PRODUCT_CURRENT),Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        RecipeProduct.setBackgroundColor(CELL_COLOR);
        RecipeProduct.setPadding(
                sizeUnit/6,sizeUnit/6
                ,sizeUnit/6,sizeUnit/6
        );
        paramsM=new LinearLayout.LayoutParams(
                sizeUnit*3/2,sizeUnit*3/2);
        paramsM.setMargins(3*sizeUnit/4,3*sizeUnit/4,0,0);
        RecipeProduct.setLayoutParams(paramsM);
        rl.addView(RecipeProduct);
        if(productCount>1){
            TextView tv=new TextView(mContext);
            //paramsM.setMargins(0,-50,10,0);
            //tv.setLayoutParams(paramsM);
            tv.setText(productCount + "");
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextSize(20);
            tv.setTextColor(Color.WHITE);
            rl.addView(tv);
        }
        addView(rl);
        //Log.i("hz","extraAdd:"+productCount+";"+(isShapeless?"y":"n"));
    }

    public void setRECIPE(DBD db,int[][] recipe){
        //MAX_ITEMS=0;
        RECIPE=recipe;

        //RECIPE_PIC=new HashMap<Integer, Bitmap>();
        //RECIPE_NAME=new HashMap<Integer, String>();
        for(int[] box:recipe) {
            MAX_ITEMS=Math.max(MAX_ITEMS,box.length-1);
            for (int item : box) {
                if(item==0)
                    continue;
                saveItemData(db,item);
            }
        }
    }
    public void setProducts(DBD db,int[] products){
        //MAX_ITEMS=0;
        PRODUCTS=products;
        MAX_ITEMS=Math.max(MAX_ITEMS,products.length-1);
        for (int item : products) {
            if(item==0)
                continue;
            saveItemData(db,item);
        }
    }
    private void addToLooper(){
        RECIPE_LOOP.put(RECIPE_LOOP.size(),this);
        addedToLooper=true;
    }
    public void disableLooper(){
        looperEnable=false;
    }
    private boolean looperEnable=true;
    public void changeImage(){
        if(MAX_ITEMS>0 && !addedToLooper && looperEnable)
            addToLooper();
        //Log.i("hz","change:"+CURRENT_STATUS+","+MAX_ITEMS);
        if(CURRENT_STATUS==0 && MAX_ITEMS==0)
            return;
        int next=CURRENT_STATUS+1;
        if(next>MAX_ITEMS)
            next=0;
        int c=-1;
        for(int[] box:RECIPE){
            c++;
            //Log.i("hz","change_:"+box.length+";"+c);
            if((CURRENT_STATUS>-1 && box.length==1)
                    || box.length==0) {
                continue;
            }
            int key=(box.length==1?0:next);
            if(next>=box.length) {
                //Log.i("hz", "change_:" + box.length + ";" + c + "," + next + ";" + MAX_ITEMS);
                break;
            }else{
                //Log.i("hz","change+:"+box[key]);
            }
            RECIPE_CURRENT[c]=box[key];
            //Log.i("hz","change current "+c+":"+box[key]);
            RecipeParts[c]
                    .setImageBitmap(
                            RECIPE_PIC.get(box[key])
                    );
        }

        int key=(PRODUCTS.length==1?0:next);
        PRODUCT_CURRENT=PRODUCTS[key];
        RecipeProduct.setImageBitmap(
                RECIPE_PIC.get(PRODUCTS[key])
        );

        CURRENT_STATUS=next;
    }
    private void openAnotherRecipe(int item){
        if(mContext instanceof ActivityRecipe)
            ActivityRecipe.addRecipe(item);
        else {
            Intent i = new Intent(mContext, ActivityRecipe.class);
            i.putExtra(ActivityRecipe.INTENT_ITEM, item);
            mContext.startActivity(i);
        }
    }
    public static void recycle(){
        for(int i=0;i<RECIPE_PIC.size();i++)
            RECIPE_PIC.get(RECIPE_PIC.keyAt(i)).recycle();
        RECIPE_PIC=new SparseArray<Bitmap>();
    }
}
