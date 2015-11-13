package com.example.michael.musicplayer5;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_panel);

        //if the current current song album art uri != null
        //then set as background in some fashion
        if(StaticMediaPlayer.currentSongObect != null && StaticMediaPlayer.currentSongObect.albumArtURI != null){
            //set album art as background in some fashion
            RelativeLayout rl = (RelativeLayout) this.findViewById(R.id.activity_play_panel);
            Bitmap bm1 = BitmapFactory.decodeFile(StaticMediaPlayer.currentSongObect.albumArtURI);
            //blur the bitmap by shrinking it first
            //Bitmap.createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, boolean filter)
            Bitmap bm2 = Bitmap.createScaledBitmap(bm1, 5, 5, true);
            rl.setBackgroundDrawable(new BitmapDrawable(bm2));

            TextView playPanelTitleView = (TextView) findViewById(R.id.playPanelTitleView);
            playPanelTitleView.setText(StaticMediaPlayer.currentSongObect.songTitle);

            ImageView playPanelAlbumView = (ImageView) findViewById(R.id.playPanelAlbumView);
            playPanelAlbumView.setImageBitmap(bm1);
        }


        //TransformBackground(bm);

        //Bitmap bmImg = BitmapFactory.decodeStream(is);
        //BitmapDrawable background = new BitmapDrawable(bmImg);
        //linearLayout.setBackgroundDrawable(background);

        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.my_layout); // add this code
        //LinearLayout ln = (LinearLayout) this.findViewById(R.id.linlay);
        //ln.setBackgroundDrawable(getResources().getDrawable(R.drawable.wallpaper));

        //ImageView mImg;
        //mImg = (ImageView) findViewById(R.id.(your xml img id));
        //mImg.setImageBitmap(img);

    }

    public void TransformBackground(Bitmap bitMap){

        Bitmap bm = BitmapFactory.decodeFile(StaticMediaPlayer.currentSongObect.albumArtURI);
        ResizeBitMap resizer = new ResizeBitMap(this);
        bitMap = resizer.resize(bm);

        ImageView slidingView = (ImageView) findViewById(R.id.slidingView);

        //Get screen dimensions
        ScreenDimensions screenDimensions = new ScreenDimensions(this);

        //Get negative margin
        int negativeMargin = bitMap.getWidth() - screenDimensions.width;

        //Set negative margin as width
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, negativeMargin, 0); // p.setMargins(l, t, r, b);
        slidingView.setLayoutParams(lp);

        Drawable drawable = new BitmapDrawable(getResources(), bitMap); // Convert bitmap to drawable
        slidingView.setBackgroundDrawable(drawable); // Set drawable as background (layouts to not accept bitmaps as backgrounds)
        //imageView.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_image_view));

        int fromX = 0;
        int toX = -(bitMap.getWidth() - screenDimensions.width);

        Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
        animation.setDuration(5000);
        animation.setFillAfter(true);
        slidingView.startAnimation(animation);

        //if (String.valueOf(panel.getPanelState()) == "COLLAPSED") {
        //    panel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        //}
    }
}
