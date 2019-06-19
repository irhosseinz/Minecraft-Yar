package ir.boozar.minecraft;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.Window;
import android.widget.TextView;

import org.xml.sax.XMLReader;

/**
 * Created by hossein on 10/27/14.
 */
public class ActivityTourial extends Activity {

    private DBD Dbd;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tourial);

        context=this;

        Dbd= m.Dbd;

        TextView tv=(TextView) findViewById(R.id.txt);
        tv.setText(Html.fromHtml(
                "<b>hi</b><img src=\"i:222\"/>how are you<u>done</u><img src=\"i:230\" style=\"width:100;height:100\"/> how are u?"
                ,new HtmlImg()
                ,new HtmlTag()
        ));
    }
    public class HtmlTag implements Html.TagHandler{
        @Override
        public void handleTag(boolean b, String s, Editable editable, XMLReader xmlReader) {
            //Log.i("hz", "getTag:" + s);
        }
    }
    public class HtmlImg implements Html.ImageGetter{
        @Override
        public Drawable getDrawable(String s) {
            String[] d=s.split(":");
            if(d[0].equals("i")){
                int item=Integer.parseInt(d[1]);
                if(RecipeView.RECIPE_PIC.get(item,null)==null){
                    if(!RecipeView.saveItemData(Dbd,item)){
                        return null;
                    }
                }
                Drawable dr=new BitmapDrawable(context.getResources()
                        ,Bitmap.createScaledBitmap(RecipeView.RECIPE_PIC.get(item), 120, 120, false));
                dr.setBounds(0,0,dr.getIntrinsicWidth(),dr.getIntrinsicHeight());
                return dr;
            }
            return null;
        }
    }
}
