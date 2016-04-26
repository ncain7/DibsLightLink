package com.lightlinks.dibs.dibslightlinks;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by NickHome on 4/17/16.
 */
public class ConfigCrowdModeActivity extends AppCompatActivity{
    private static final String MYAPPPREFS = "DibsPrefs";
    private String TAG = "ConfigCrowdModeActivity";
    ColorPicker cp;
    private int LEDr, LEDg, LEDb;
    SharedPreferences prefs;
    private String end_time_string;
    private String[] times;
    private int duration;
    private boolean color_selected = false;
    EditText light_speed;
    EditText end_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_crowd);

        light_speed = (EditText) findViewById(R.id.editText_crowd_speed);
        end_time = (EditText) findViewById(R.id.editText_crowd_end_time);
        final View color_view = findViewById(R.id.crowd_color_view);
        //int[] colors = ColorHelp.getColors(color);

        Button update = (Button) findViewById(R.id.button_crowd_update);
        Button cancel = (Button) findViewById(R.id.button_crowd_cancel);
        prefs = getSharedPreferences(MYAPPPREFS,0);
        int color = prefs.getInt("CrowdModeColorAPP", 0);
        cp = getColPreferences(3) ;

        if (color_view !=null) {
            color_view.setBackgroundColor(color);
            //set listener for when view is clicked
            color_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cp.show();
                    Button color_update = (Button) cp.findViewById(R.id.button_update_color);
                    color_update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LEDr = cp.getRed();
                            LEDg = cp.getTrueGreen();
                            LEDb = cp.getTrueBlue();
                            color_selected = true;
                            color_view.setBackgroundColor(cp.getColor());
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
        //LED 255 0 0 127 0 0 5|PIR OFF|TIRS ON|TIME 500|PAT ALT|;

        String command;
        long duration;
        long end_time_Millis;
        long light_speed_millis;
        end_time_string = end_time.getText().toString();
        String light_speed_string = light_speed.getText().toString();


        if (!(color_selected) || end_time_string.isEmpty() || light_speed_string.isEmpty()) {//User did not edit these fields
            //command = command + "500|PAT ALT 255 0 0 127 0 0 5|END USER|";
            Toast.makeText(this,"Please make sure all fields have been filled, including the color",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            times = end_time_string.split(":");
            if (times.length==2) {
                long hours = Long.parseLong(times[0]);
                long minutes = Long.parseLong(times[1]);
                duration = (hours + (minutes/60)) * 3600; // Convert to seconds
            }else{
                long hours = Long.parseLong(end_time_string);
                duration = hours * 3600; // convert to seconds
            }

            end_time_Millis = TimeUnit.SECONDS.toMillis(duration); // get time in milliseconds
            duration = Long.parseLong(light_speed_string);
            light_speed_millis = TimeUnit.SECONDS.toMillis(duration);
            command = "LED "+LEDr+" "+LEDg+" "+LEDb+" "+(LEDr*0.5)+" "+(LEDg*0.5)+
                    " "+(LEDb*0.5)+" 5|PIR OFF|TIRS ON|TIME "+light_speed_millis+"|PAT ALT|";
            prefs.edit().putString("CrowdModePref",command).apply();
            updateColPrefs(3,cp.getRed(),cp.getGreen(),cp.getBlue());
            Toast.makeText(this,"Crowd Mode has been updated",Toast.LENGTH_SHORT).show();
        }

        finish();
    }
    private ColorPicker getColPreferences(int cur_mode) {
        ColorPicker modeColor;
        int mode_color;
        int[] color;
        switch (cur_mode){
            case 0:
                mode_color = prefs.getInt("StandardModeColorAPP", 0);
                color = ColorHelp.getColors(mode_color);
                modeColor = new ColorPicker(this,color[0],color[1],color[2]);
                return modeColor;
            case 1:
                mode_color = prefs.getInt("PSModeColorAPP", 0);
                color = ColorHelp.getColors(mode_color);
                modeColor = new ColorPicker(this,color[0],color[1],color[2]);
                return modeColor;
            case 3:
                mode_color = prefs.getInt("CrowdModeColorAPP", 0);
                color = ColorHelp.getColors(mode_color);
                modeColor = new ColorPicker(this,color[0],color[1],color[2]);
                return modeColor;
            case 5:
                mode_color = prefs.getInt("SecurityModeColorAPP", 0);
                color = ColorHelp.getColors(mode_color);
                modeColor = new ColorPicker(this,color[0],color[1],color[2]);
                return modeColor;


        }
        return null; //Was not any case :(
    }

    private void updateColPrefs(int cur_mode, int r, int g, int b) {
        switch (cur_mode){
            case 0:
                prefs.edit().putInt("StandardModeColorAPP", Color.rgb(r, g, b)).apply();
                g = ColorHelp.scaleGreen(g);
                b = ColorHelp.scaleBlue(b);
                prefs.edit().putInt("StandardModeColorTrue",Color.rgb(r,g,b)).apply();
                Toast.makeText(this,"Updated Standard Mode Color",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                prefs.edit().putInt("PSModeColorAPP", Color.rgb(r, g, b)).apply();
                g = ColorHelp.scaleGreen(g);
                b = ColorHelp.scaleBlue(b);
                prefs.edit().putInt("PSModeColorTrue",Color.rgb(r,g,b)).apply();
                Toast.makeText(this,"Updated Power Save Mode Color",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                prefs.edit().putInt("CrowdModeColorAPP", Color.rgb(r, g, b)).apply();
                g = ColorHelp.scaleGreen(g);
                b = ColorHelp.scaleBlue(b);
                prefs.edit().putInt("CrowdModeColorTrue",Color.rgb(r,g,b)).apply();
                Toast.makeText(this,"Crowd Mode Color Updated",Toast.LENGTH_SHORT).show();
                break;
            case 5:
                prefs.edit().putInt("SecurityModeColorAPP", Color.rgb(r, g, b)).apply();
                g = ColorHelp.scaleGreen(g);
                b = ColorHelp.scaleBlue(b);
                prefs.edit().putInt("SecurityModeColorTrue",Color.rgb(r,g,b)).apply();
                Toast.makeText(this,"Security Mode Color Updated",Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
