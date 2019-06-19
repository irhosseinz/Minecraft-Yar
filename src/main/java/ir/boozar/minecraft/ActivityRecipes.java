package ir.boozar.minecraft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hossein on 10/27/14.
 */
public class ActivityRecipes extends Activity {

    private DBD Dbd;
    private Context context;

    private int CATEGORY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recipes);

        Bundle extras=getIntent().getExtras();
        if(extras!=null)
            CATEGORY=extras.getInt("CAT");
        else
            CATEGORY=1;

        context=this;

        Dbd= m.Dbd;
        updateList();
    }
    private void updateList(){
        TableLayout tl=(TableLayout) findViewById(R.id.mainMenu);
        tl.removeAllViews();
        int[] items=Dbd.getCategoryItems(CATEGORY);
        TableRow.LayoutParams params1=
                new TableRow.LayoutParams(0,150,4);
        TableRow.LayoutParams params2=
                new TableRow.LayoutParams(0,150,1);
        //params2.setMargins(10,2,40,2);
        for(final int item:items){
            if(RecipeView.RECIPE_PIC.get(item,null)==null){
                if(!RecipeView.saveItemData(Dbd,item)){
                    Toast.makeText(context, context.getResources().getString(R.string.items_no_memory), Toast.LENGTH_LONG).show();
                    return;
                }
            }
            TableRow tr=new TableRow(this);
            tr.setBackgroundResource(R.drawable.recipe_row);
            //tr.setBackgroundColor(Color.MAGENTA);
            TextView tv=new TextView(this);
            tv.setText(RecipeView.RECIPE_NAME.get(item));
            tv.setTextSize(25);
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            tv.setLayoutParams(params1);
            tr.addView(tv);
            ImageView iv=new ImageView(this);
            iv.setImageBitmap(RecipeView.RECIPE_PIC.get(item));
            iv.setLayoutParams(params2);
            iv.setScaleType(ImageView.ScaleType.FIT_START);
            tr.addView(iv);
            tr.setClickable(true);
            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(context,ActivityRecipe.class);
                    i.putExtra(ActivityRecipe.INTENT_ITEM,item);
                    i.putExtra(ActivityRecipe.INTENT_FORCE_SHOW,true);
                    finish();
                    startActivity(i);
                }
            });
            tl.addView(tr);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
