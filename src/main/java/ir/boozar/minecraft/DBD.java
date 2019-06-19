package ir.boozar.minecraft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.sql.Blob;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DBD extends SQLiteOpenHelper {

    private static String DB_NAME="db_data";
    private static final int DB_VERSION=1;

    private Context context;

    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID="id";
    public static final String COLUMN_KEY="ukey";
    public static final String COLUMN_PIC = "pic";
    public static final String COLUMN_NAME_EN = "name_en";
    public static final String COLUMN_NAME_FA = "name_fa";

    public static final String TABLE_RECIPE = "recipe";
    public static final String COLUMN_CATEGORY="cat";
    public static final String COLUMN_PRODUCT="pro";
    public static final String COLUMN_PRODUCT_COUNT="pro_count";
    public static final String COLUMN_SHAPELESS = "shapeless";
    public static final String COLUMN_RECIPE = "recipe";

    public static final String TABLE_CATEGORY = "category";

    public static final String TABLE_VIDEOS = "videos";
    public static final String COLUMN_EPISODE="ep";
    public static final String COLUMN_TITLE="title";
    public static final String COLUMN_TIME="time";
    public static final String COLUMN_INFO="info";

    public DBD(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
        //Log.i("hz", "open db:" + DB_NAME);
        context=c;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //throw new Exception("database not created");
        //Log.i("hz","oncreate db:"+DB_NAME);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV){
        //Log.i("hz","onupgrade db:"+DB_NAME);
    }

    public List<CategoryObj> getCategories(){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.query(
                TABLE_CATEGORY
                ,null
                ,null
                ,null,null,null,null
        );
        //Log.i("hz", "getPhoto:" + id + "," + c.getCount());
        List<CategoryObj> out=new LinkedList<CategoryObj>();
        CategoryObj o;
        if(c.moveToFirst()){
            do {
                o=new CategoryObj();
                o.id = c.getInt(0);
                o.nameEN=c.getString(1);
                o.nameFA=c.getString(2);
                out.add(o);
            }while (c.moveToNext());
        }
        c.close();
        //db.close();
        return out;
    }
    public int[] getCategoryItems(int cat){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.query(
                TABLE_RECIPE
                ,null
                ,COLUMN_CATEGORY+"=?"
                ,new String[]{cat+""}
                ,COLUMN_PRODUCT,null,null
        );
        //Log.i("hz", "getPhoto:" + id + "," + c.getCount());
        int[] out=new int[c.getCount()];
        int i=0;
        if(c.moveToFirst()){
            do {
                String[] items=c.getString(2).split(RecipeObj.ITEM_SEP);
                out[i++]=Integer.parseInt(items[1]);
            }while (c.moveToNext());
        }
        c.close();
        //db.close();
        return out;
    }

    public List<RecipeObj> getItemRecipe(int productID,boolean itemUses){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.query(
                TABLE_RECIPE
                ,null
                ,(itemUses?COLUMN_RECIPE:COLUMN_PRODUCT)+" like ?"
                ,new String[]{
                        //productID+""
                        "%"+RecipeObj.ITEM_SEP+productID+RecipeObj.ITEM_SEP+"%"
                },null,null,null
        );
        //Log.i("hz", "getPhoto:" + id + "," + c.getCount());
        List<RecipeObj> out=new LinkedList<RecipeObj>();
        RecipeObj o;
        if(c.moveToFirst()){
            do {
                o=new RecipeObj();
                o.id = c.getInt(0);
                o.category = c.getInt(1);
                o.setProducts(c.getString(2));
                o.productCount = c.getInt(3);
                o.isShapeless = c.getInt(4)==1;
                o.setRecipe(c.getString(5));
                out.add(o);
            }while (c.moveToNext());
        }
        return out;
    }

    public int ITEMS_COUNT=0;
    public ItemObj getItem(int id){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.query(
                TABLE_ITEMS
                ,null
                ,COLUMN_ID+"=?"
                ,new String[]{id+""},null,null,null
        );
        //Log.i("hz", "getItem:" + id);
        ItemObj o=new ItemObj();
        if(ITEMS_COUNT==0)
            ITEMS_COUNT=c.getCount();
        if(c.moveToFirst()){
            o.id=c.getInt(0);
            o.key=c.getString(1);
            o.pic=c.getBlob(2);
            o.nameEN=c.getString(3);
            o.nameFA=c.getString(4);
        }
        //Log.i("hz","getItem:"+id+","+o.id);
        return o;
    }
    public VideoObj[] getVideos(int cat){
        SQLiteDatabase db=getReadableDatabase();
        Cursor c=db.query(
                TABLE_VIDEOS
                ,null
                ,COLUMN_CATEGORY+"=?"
                ,new String[]{cat+""},null,null,COLUMN_EPISODE
        );
        VideoObj[] out=new VideoObj[c.getCount()];
        int i=0;
        if(c.moveToFirst()){
            do {
                out[i]=new VideoObj();
                out[i].key = c.getString(0);
                out[i].cat = c.getInt(1);
                out[i].episode = c.getInt(2);
                out[i].title=c.getString(3);
                out[i].time=c.getString(4);
                out[i].info=c.getString(5);
                i++;
            }while (c.moveToNext());
        }
        return out;
    }


    public static class CategoryObj{
        public int id=0;
        private String nameEN,nameFA;
        public String getName(){
            return nameFA;
        }
    }
    public static class ItemObj{
        public int id=0;
        public String key;
        private String nameEN,nameFA;
        public String getName(){
            return nameFA;
        }
        public byte[] pic;
    }
    public static class VideoObj{
        public int cat,episode;
        public String key,title,time,info;
    }
    public static class RecipeObj{
        private DBD Dbd;
        public RecipeObj(){
        }
        public int id=0
                ,category
                ,productCount;
        public boolean isShapeless;
        public int[] products;
        public int[][] recipe;

        private static final String EMPTY="0,0,0";
        public static final String ITEM_SEP=",";
        public static final String BOX_SEP=";";
        public void setProducts(String data){
            String[] d=data.split(ITEM_SEP);
            products=new int[d.length-2];
            for(int i=1;i<d.length-1;i++)
                products[i-1]=Integer.parseInt(d[i]);
        }
        public void setRecipe(String data){
            String[] boxes=data.split(BOX_SEP);
            recipe=new int[9][];
            if(boxes.length<11)
                return;
            for(int i=1;i<10;i++){
                String[] items=boxes[i].split(ITEM_SEP);
                recipe[i-1]=new int[items.length-2];
                for(int j=1;j<items.length-1;j++)
                    recipe[i-1][j-1]=Integer.parseInt(items[j]);
            }
        }
        public RecipeView getRecipeView(Context c,DBD db,int width,boolean looperEnabled){
            RecipeView o=new RecipeView(c
                    ,width,productCount,isShapeless,looperEnabled);
            o.setRECIPE(db,recipe);
            o.setProducts(db,products);
            o.changeImage();
            return o;
        }
    }










    public static class DBD_TEMP extends SQLiteOpenHelper {

        private static String DB_TEMP_NAME = "db";

        private Context context;

        public DBD_TEMP(Context c) {
            super(c, DB_TEMP_NAME, null, DB_VERSION);
            //Log.i("hz","open db:"+DB_TEMP_NAME);
            context = c;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Log.i("hz","on create db:"+DB_TEMP_NAME);
            try {
                copyFromAssets(DB_ORIG_NAME);
            } catch (Exception e) {
                Log.e("hz", "create database error:" + e.getMessage(), e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            try {
                copyFromAssets(DB_ORIG_NAME);
            } catch (Exception e) {
                Log.e("hz", "upgrade database error:" + e.getMessage(), e);
            }
        }

        public static final String DB_ORIG_NAME = "data";

        public void copyFromAssets(String input) throws Exception {
            int resId = context.getResources()
                    .getIdentifier("raw/" + input, null, context.getPackageName());
            //Log.i("hz","start copy:"+input+","+resId);
            InputStream myInput = context
                    .getResources().openRawResource(resId);
            String out = context.getDatabasePath(DB_TEMP_NAME).toString() + "_data";
            File outF = new File(out);
            if (outF.exists())
                outF.delete();
            OutputStream myOutput = new FileOutputStream(out);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
            //Log.i("hz","db copied:"+out);
        }
    }
}