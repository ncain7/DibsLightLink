package com.lightlinks.dibs.dibslightlinks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by NickHome on 4/19/16.
 */
public class ColorHelp {

    private static final String MYAPPPREFS = "DibsPrefs";

    static SharedPreferences prefs;
    public static int[] getColors(int RGBcolor){
        int[] RGBvals = new int[3];
        int red, green, blue;
        red = (RGBcolor >> 16) & 0xFF;
        green = (RGBcolor >> 8) & 0xFF;
        blue = (RGBcolor) & 0xFF;
        RGBvals[0] = red;
        RGBvals[1] = green;
        RGBvals[2] = blue;
        return RGBvals;

    }

    public static int[] updateColor(final ColorPicker cp){
        cp.show();
        final int []colors = new int[3];
        Button updateColor = (Button)  cp.findViewById(R.id.button_update_color);
        updateColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int LED_red = cp.getRed();
                int LED_green = cp.getTrueGreen();
                int LED_blue = cp.getTrueBlue();
                colors[0] = LED_red;
                colors[1] = LED_green;
                colors[2] = LED_blue;
                cp.dismiss();
            }
        });
        return colors;
    }

    public static int scaleGreen(int g){
        int trueGreen =(int)(2.3286 * Math.exp((g+0.1)*0.0187));
        if (trueGreen<1)
            trueGreen=0;
        return trueGreen;
    }

    public static int scaleBlue(int b){
       int trueBlue = (int)(0.0055*Math.pow(b,2)-0.6191*b+1.6136);
        if (trueBlue < 1 || b==0)
            trueBlue = 0;
        return trueBlue;
    }

    private ColorPicker getColPreferences(int cur_mode, Activity context) {
        ColorPicker modeColor;
        int mode_color;
        int []color;
        switch (cur_mode){
            case 0:
                mode_color = prefs.getInt("StandardModeColorAPP", 0);
                color = ColorHelp.getColors(mode_color);
                modeColor = new ColorPicker(context,color[0],color[1],color[2]);
                return modeColor;
            case 1:
                mode_color = prefs.getInt("PSModeColorAPP", 0);
                color = ColorHelp.getColors(mode_color);
                modeColor = new ColorPicker(context,color[0],color[1],color[2]);
                return modeColor;
            case 3:
                mode_color = prefs.getInt("CrowdModeColorAPP", 0);
                color = ColorHelp.getColors(mode_color);
                modeColor = new ColorPicker(context,color[0],color[1],color[2]);
                return modeColor;
            case 5:
                mode_color = prefs.getInt("SecurityModeColorAPP", 0);
                color = ColorHelp.getColors(mode_color);
                modeColor = new ColorPicker(context,color[0],color[1],color[2]);
                return modeColor;


        }
        return null; //Was not any case :(
    }

    private void updateColPrefs(int cur_mode, int r, int g, int b, Context context) {
        switch (cur_mode){
            case 0:
                prefs.edit().putInt("StandardModeColorAPP", Color.rgb(r, g, b)).apply();
                g = scaleGreen(g);
                b = scaleBlue(b);
                prefs.edit().putInt("StandardModeColorTrue",Color.rgb(r,g,b)).apply();
                Toast.makeText(context, "Updated Standard Mode Color", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                prefs.edit().putInt("PSModeColorAPP", Color.rgb(r, g, b)).apply();
                g = scaleGreen(g);
                b = scaleBlue(b);
                prefs.edit().putInt("PSModeColorTrue",Color.rgb(r,g,b)).apply();
                Toast.makeText(context,"Updated Power Save Mode Color",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                prefs.edit().putInt("CrowdModeColor", Color.rgb(r, g, b)).apply();
                g = ColorHelp.scaleGreen(g);
                b = ColorHelp.scaleBlue(b);
                prefs.edit().putInt("SecurityModeColorTrue",Color.rgb(r,g,b)).apply();
                Toast.makeText(context,"Crowd Mode Color Updated",Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
