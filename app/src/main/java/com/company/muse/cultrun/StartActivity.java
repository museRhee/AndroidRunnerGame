package com.company.muse.cultrun;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * StartActivity connected with MainActivity(GameView)
 */

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_start);

        // hide statusBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        // Click Listener of button
        findViewById(R.id.BtnStart).setOnClickListener(onButtonClick);
        findViewById(R.id.BtnQuit).setOnClickListener(onButtonClick);
    }

    //-----------------------------
    // ButtonClick
    //-----------------------------
    View.OnClickListener onButtonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch ( v.getId() ) {
                case R.id.BtnQuit :     // game quit
                    android.os.Process.killProcess( android.os.Process.myPid() );
                    break;
                case R.id.BtnStart :    // game start
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    //-----------------------------
    // Back Key
    //-----------------------------
    @Override
    public void onBackPressed() {
        android.os.Process.killProcess( android.os.Process.myPid() );
    }

}
