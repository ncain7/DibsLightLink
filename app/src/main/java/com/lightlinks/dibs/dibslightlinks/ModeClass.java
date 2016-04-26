package com.lightlinks.dibs.dibslightlinks;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;

/**
 * Created by NickHome on 4/16/16.
 */
public class ModeClass extends Application{

    public static final int STANDARD_MODE = 0;
    public static final int POWER_SAVE_MODE = 1;
    public static final int PARTY_MODE = 2;
    public static final int CROWD_MODE = 3;
    public static final int SLEEP_MODE = 4;
    public static final int SECURITY_MODE = 5;

    private int cur_mode;
    private Handler modeHandler;

    private int rVal, gVal, bVal, lum; //holds the RGB values and luminosity for color selection
    private boolean isOnPIR, isOnDoor, isTimer;
    private int pattern; // 0-none 1-blink 2-fade 3-alternate
    private int cur_colorset; // 0-Warm 1-Cold 3-Custom
    private Color[] colorset = new Color[4]; //holds the colors for party mode colorset
    private int timer; //holds time in either minutes or seconds, depending on mode
    private String end_time;
    private String[] units = new String[3]; // Used to hold units



    /**
     *
     * Constructor for ModeClass for creating new modes
     */
   // public ModeClass(){

    //}

    public void setMode(int mode){
        cur_mode = mode;
    }

    public int getMode(){
        return cur_mode;
    }

    private void setStandardMode(int red, int green, int blue){

        isOnDoor = true;
        isOnPIR = false;
        isTimer = false;
        rVal = red;
        gVal = green;
        bVal = blue;
        pattern = 0; //No pattern, just on
    }

    private void setPowerSaveMode(int red, int green, int blue, int timer, String endTime){
        rVal = red;
        gVal = green;
        bVal = blue;
        this.timer = timer; // timer in minutes in this mode
        end_time = endTime;
        units = end_time.split(":");
        int hours;
        int minutes;
        int seconds;
        int duration;
        switch (units.length){
            case 3: //Has format HH:MM:SS
                hours = Integer.parseInt(units[0]);
                minutes = Integer.parseInt(units[1]);
                seconds = Integer.parseInt(units[2]);
                duration = 3600 * hours * minutes + seconds;
                break;
            case 2://MM:SS
                hours = 0;
                minutes = Integer.parseInt(units[0]);
                seconds = Integer.parseInt(units[1]);
                duration = 60*minutes + seconds;
                break;
            default:
                break;


        }


    }





}
