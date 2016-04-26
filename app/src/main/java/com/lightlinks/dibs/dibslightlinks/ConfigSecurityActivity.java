package com.lightlinks.dibs.dibslightlinks;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by NickHome on 4/18/16.
 */
public class ConfigSecurityActivity extends AppCompatActivity {
    private static final String MYAPPPREFS = "DibsPrefs";
    boolean colorSelected = false;
    ColorPicker cp;
    int LEDr, LEDg, LEDb, mode_color;
    EditText end_time;
    Switch timeOfDay;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_security);

        prefs = getSharedPreferences(MYAPPPREFS,0);
        end_time = (EditText) findViewById(R.id.editText_sec_end_time);
        final View colorView = findViewById(R.id.security_color_view);
        Switch time_of_day = (Switch) findViewById(R.id.switch_sec_switch);
        cp = getColPreferences();
        Button update = (Button) findViewById(R.id.button_sec_update);
        Button cancel = (Button) findViewById(R.id.button_sec_cancel);

        if (colorView != null) {
            colorView.setBackgroundColor(mode_color);
            colorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cp.show();
                    Button update_color = (Button) cp.findViewById(R.id.button_update_color);
                    update_color.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LEDr = cp.getRed();
                            LEDg = cp.getTrueGreen();
                            LEDb = cp.getTrueBlue();
                            colorView.setBackgroundColor(cp.getColor());
                            colorSelected = true;
                            cp.dismiss();
                        }
                    });
                }
            });
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMode();
            }
        });
    }

    private void updateMode(){
        if (!colorSelected && end_time.getText().toString().isEmpty()){
            Toast.makeText(this, "You have not made any changes, if this was intentional, hit cancel instead",
                    Toast.LENGTH_SHORT).show();
        }
        else if (!colorSelected){
            Toast.makeText(this,"Please select a color",Toast.LENGTH_SHORT).show();
        }else if (end_time.getText().toString().isEmpty()){
            Toast.makeText(this,"You need to enter a time",Toast.LENGTH_SHORT).show();
        }else {
            String command = "LED "+LEDr+" "+LEDg+" "+LEDb+" 5|PIR ON|TIRS ON|PAT ALT 255 0 0 0 0 0 5|";
            prefs.edit().putInt("SecurityModeColorAPP", cp.getColor()).apply();
            prefs.edit().putInt("SecurityModeColorTrue", Color.rgb(LEDr,LEDg,LEDb)).apply();

            Toast.makeText(this,"Security Mode Updated",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private ColorPicker getColPreferences() {
        ColorPicker modeColor;
        int[] color;

        mode_color = prefs.getInt("SecurityModeColorAPP", 0);
        color = ColorHelp.getColors(mode_color);
        modeColor = new ColorPicker(this,color[0],color[1],color[2]);
        return modeColor;

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
