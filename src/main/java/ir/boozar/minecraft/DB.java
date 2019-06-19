package ir.boozar.minecraft;


import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hossein on 6/20/14.
 */
public class DB extends SQLiteOpenHelper {

    static final String TABLE_NEWS = "n";
    static final String COLUMN_ID = "id";
    static final String COLUMN_TYPE = "a";
    static final String COLUMN_PACKAGE = "b";
    static final String COLUMN_DATE = "c";
    static final String COLUMN_LINK = "d";
    static final String COLUMN_TITLE = "e";
    static final String COLUMN_ICON = "f";
    static final String COLUMN_DATA = "g";
    static final String COLUMN_TEXT = "h";
    static final String COLUMN_STATUS = "i";

    private static final String DATABASE_NAME = "data_n";
    private static final int DATABASE_VERSION = 1;

    private static final String[] DATABASE_TABLES =new String[]{
            TABLE_NEWS
    };
    private static final String[] DATABASE_CREATE =new String[]{
            "create table "
                + TABLE_NEWS + "("+ COLUMN_ID
                + " integer primary key, "
                + COLUMN_TYPE + " integer not null, "
                + COLUMN_DATE + " integer not null, "
                + COLUMN_PACKAGE + " text not null,"
                + COLUMN_TITLE + " text not null,"
                + COLUMN_TEXT + " text not null,"
                + COLUMN_LINK + " text null,"
                + COLUMN_ICON + " blob null,"
                + COLUMN_DATA + " blob null,"
                + COLUMN_STATUS + " integer default 0"
                + " );"
    };

    Context context;
    public DB(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        context=c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(String query:DATABASE_CREATE)
            db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(String table:DATABASE_TABLES)
            db.execSQL("DROP TABLE IF EXISTS " + table);
        //db.execSQL(DATABASE_CREATE[0]);
        onCreate(db);
    }


    public long saveNews(n.NewsObj o){
        SQLiteDatabase db = this.getWritableDatabase();

        String q="INSERT INTO "+TABLE_NEWS+"(" +
                COLUMN_ID+"," +
                COLUMN_TYPE+"," +
                COLUMN_DATE+"," +
                COLUMN_PACKAGE+"," +
                COLUMN_TITLE+"," +
                COLUMN_TEXT+"," +
                COLUMN_LINK+","+
                COLUMN_ICON+","+
                COLUMN_DATA+
                ") VALUES(?,?,?,?,?,?,?,?,?)";
        //Log.i("hz","insertN:"+q);
        SQLiteStatement i=db.compileStatement(q);
        i.clearBindings();
        i.bindLong(1,o.id);
        i.bindLong(2,o.code);
        i.bindLong(3,System.currentTimeMillis());
        i.bindString(4, o.pack);
        i.bindString(5, o.title);
        i.bindString(6, o.text);
        i.bindString(7, o.link);
        if(o.icon!=null)
            i.bindBlob(8, o.icon);
        if(o.picture!=null)
            i.bindBlob(9, o.picture);

        return i.executeInsert();
    }
    public boolean newsExists(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =
                db.query(TABLE_NEWS, // a. table
                        null, // b. column names
                        COLUMN_ID+"=?", // c. selections
                        new String[]{id+""}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null);
        boolean o=c.getCount()>0;
        c.close();
        return o;
    }
    public long readNews(int id){
        //Log.i("hz","readNews("+id+")");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, 1);

        return db.update(TABLE_NEWS, // table
                values
                , COLUMN_ID + "=?"
                , new String[]{id + ""}
        );
    }
    public n.NewsObj[] getNews(int count,boolean unread) {
        //Log.i("hz","getNews("+count+")");
        String sq="";
        String[] sa=new String[]{};
        if(unread){
            sq=COLUMN_STATUS+"=?";
            sa=new String[]{"0"};
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_NEWS, // a. table
                        null, // b. column names
                        sq, // c. selections
                        sa, // d. selections args
                        null, // e. group by
                        null, // f. having
                        "random()", // g. order by
                        count+"");
        if (cursor.moveToFirst()) {
            n.NewsObj[] na = new n.NewsObj[cursor.getCount()];
            int c=0;
            do {
                na[c]=new n.NewsObj();
                na[c].id=cursor.getInt(0);
                na[c].setType(context,cursor.getInt(1));
                na[c].date=cursor.getLong(2);
                na[c].pack=cursor.getString(3);
                na[c].title=cursor.getString(4);
                na[c].text=cursor.getString(5);
                na[c].link=cursor.getString(6);
                na[c].icon=cursor.getBlob(7);
                na[c].picture=cursor.getBlob(8);
                na[c].status=cursor.getInt(9);
                //Log.i("hz","getN:"+na[c].title+","+na[c].picture.length);
                c++;
            } while (cursor.moveToNext());
            cursor.close();
            return na;
        }else
            return null;
    }
    public void delNewsPack(String pack){
        getWritableDatabase().delete(
                TABLE_NEWS
                ,COLUMN_PACKAGE+"=?"
                ,new String[]{pack}
        );
    }
}
