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

public class ItemView extends LinearLayout {
    private Context mContext;
    public int width,height;
    public ItemView(Context c
            ,DBD db, int width, int itemID){
        super(c);
        mContext=c;
        this.width=width;
        height=width/2;
        LayoutParams params=
                new LayoutParams
                        (width,height);
        params.setMargins(15,15,15,15);
        setLayoutParams(params);
        setOrientation(HORIZONTAL);
        setBackgroundColor(Color.parseColor("#C6C6C6"));
        setGravity(Gravity.CENTER_VERTICAL);

        addParts(db,itemID);
    }
    private void addParts(DBD db,int item){
        if(RecipeView.RECIPE_PIC.get(item,null)==null){
            if(!RecipeView.saveItemData(db,item)){
                return;
            }
        }
        TextView tv=new TextView(mContext);
        tv.setText(RecipeView.RECIPE_NAME.get(item));
        addView(tv);
        ImageView iv=new ImageView(mContext);
        iv.setImageBitmap(RecipeView.RECIPE_PIC.get(item));
        iv.setLayoutParams(new LayoutParams(height*9/10,height*9/10));
        addView(iv);
    }
}
