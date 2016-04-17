package com.lightlinks.dibs.dibslightlinks;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by NickHome on 4/16/16.
 */
public class ConfigStandActivity extends AppCompatActivity {
    private View color_view;
    private ColorPicker cp;
    private int LEDred, LEDgreen, LEDblue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_standard_mode);

        cp = new ColorPicker(this,0,0,0);
        color_view = findViewById(R.id.color_view_standard);

        color_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cp.show();
                Button update = (Button) cp.findViewById(R.id.button_update_color);
                if (update!=null){
                    LEDred = cp.getRed();
                    LEDgreen = cp.getGreen();
                    LEDblue = cp.getBlue();
                    color_view.setBackgroundColor(Color.rgb(LEDred,LEDgreen,LEDblue));
                    cp.dismiss();
                }
            }
        });
    }
}
