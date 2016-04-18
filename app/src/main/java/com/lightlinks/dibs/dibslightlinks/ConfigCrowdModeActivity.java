package com.lightlinks.dibs.dibslightlinks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by NickHome on 4/17/16.
 */
public class ConfigCrowdModeActivity extends AppCompatActivity{
    private String TAG = "ConfigCrowdModeActivity";
    ColorPicker cp;
    private int LEDred, LEDgreen, LEDblue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_crowd);

        EditText light_speed = (EditText) findViewById(R.id.editText_crowd_speed);
        EditText end_time = (EditText) findViewById(R.id.editText_crowd_end_time);
        View color_view = findViewById(R.id.crowd_color_view);
        Button update = (Button) findViewById(R.id.button_crowd_update);
        Button cancel = (Button) findViewById(R.id.button_crowd_cancel);
        cp = new ColorPicker(this,0,0,0);

        if (color_view !=null) {
            //set listener for when view is clicked
            color_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cp.show();
                    Button color_update = (Button) cp.findViewById(R.id.button_update_color);
                    color_update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LEDred = cp.getRed();
                            LEDgreen = cp.getGreen();
                            LEDblue = cp.getBlue();
                            cp.dismiss();
                        }
                    });
                }
            });
        }else{
            Log.d(TAG, "onCreate: Something is not right here...");
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMode();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void updateMode(){
        finish();
    }
}
