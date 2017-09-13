package com.example.ForMoreNews.bigproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by 金子童 on 2017/9/8.
 */
public class Details extends BaseActivity {

    private String title;
    private String body;
    private String speaker_body;
    private String picture;
    private String source;
    private boolean isLiked;
    private ArrayList<String> name = new ArrayList<>();
    private Speaker speaker = Speaker.getInstance();
    boolean isread = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        body = intent.getStringExtra("body");
        source = intent.getStringExtra("source");
        picture = intent.getStringExtra("picture");
        isLiked = intent.getBooleanExtra("isLiked", false);
        name = intent.getStringArrayListExtra("name");

        TextView titleView = (TextView) findViewById(R.id.title_view);
        ImageView imageView =  (ImageView) findViewById(R.id.pic_view);
        TextView bodyView = (TextView) findViewById(R.id.body_view);
        bodyView.setMovementMethod(ScrollingMovementMethod.getInstance());
        final ImageButton speak = (ImageButton) findViewById(R.id.speaker);

        ImageLoader.getInstance().displayImage(picture, imageView, getDisplayOption());
        titleView.setText(title, TextView.BufferType.EDITABLE);
        titleView.setTextSize(30);

        speaker_body = title;
        speaker_body += body;
        if(name != null)
            for(int i = 0; i < name.size(); i++)
                if(name.get(i) != "")
                body = body.replaceAll(name.get(i), "<a href='https://baike.baidu.com/item/" + name.get(i) + "'>" + name.get(i) + "</a>");
        body = body.replaceAll(" 　　", "<p></p>");

        speaker.setText(speaker_body);
        bodyView.setText(body);

        bodyView.setText(Html.fromHtml(body));
        bodyView.setMovementMethod(LinkMovementMethod.getInstance());
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isread) {
                    speaker.setCur(Details.this);
                    speaker.start();
                    speak.setBackground(getResources().getDrawable(R.drawable.close_speak));
                    isread = true;
                }
                else{
                    speaker.stop();
                    speak.setBackground(getResources().getDrawable(R.drawable.speaker));
                    isread = false;
                }
            }
        });

    }

    public DisplayImageOptions getDisplayOption() {
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
        return options;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(isLiked == false){
            menu.findItem(R.id.like).setIcon(R.drawable.notlove);}
        if(isLiked == true){
            menu.findItem(R.id.like).setIcon(R.drawable.love);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.like) {
            isLiked = !isLiked;
            if(isLiked == false){
                item.setIcon(R.drawable.notlove);
            Toast.makeText(this,"You don't like this news.",Toast.LENGTH_SHORT).show();}
            if(isLiked == true){
                item.setIcon(R.drawable.love);
                Toast.makeText(this,"You like this news.",Toast.LENGTH_SHORT).show();
            }

            SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            String pp = "";
            if(isLiked) pp = String.valueOf(true);
            else pp = "false";
            values.put("like", pp);
            db.update("News", values, "title = ?",
                    new String[] {title});
            Log.i("liketitle", title);
            Cursor cursor = db.query("News", null, "title = ?", new String[]{title}, null, null, null);
            Cursor cursor0 = db.query("News", null, null, null, null, null, null);

            Log.i("likefirst", "" + cursor.moveToFirst());
            if (cursor0.moveToFirst()) {
                do {
                    String like = cursor.getString(cursor
                            .getColumnIndex("title"));
                    Log.i("likebossdetail", like);

                } while (cursor.moveToNext());
            }
            cursor.close();
            return true;
        }

        if (id == R.id.share) {
            Toast.makeText(this,"You clicked the 'share' button.",Toast.LENGTH_SHORT).show();
            shareMsg("Details","Share this news",title+"\nRead this news here : " + source, null);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void shareMsg(String activityTitle, String msgTitle, String msgText,
                         String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain");
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/jpg");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.putExtra("likeData", isLiked);
        setResult(RESULT_OK, intent);
        finish();
    }

}
