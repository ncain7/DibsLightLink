package com.lightlinks.dibs.dibslightlinks;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import java.lang.Math;

/**
 * Created by NickHome on 3/19/16.
 */
public class ColorPicker extends Dialog implements SeekBar.OnSeekBarChangeListener {
    public Activity mActivity;
    public Dialog dialog;
    View colorView;
    SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    TextView redColorHint, greenColorHint, blueColorHint;
    EditText hexColor;

    private String TAG = "ColorPicker";
    private int red, green, blue, trueRed, trueGreen, trueBlue;
    int seekBarLeft;
    Rect thumbRect;

    /**
     * Constructor for color picker, if only given the activity, sets colors to 0
     * @param a
     * The activity the color picker is used in
     */
    ColorPicker(Activity a){
        super(a);
        this.mActivity = a;
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }


    /**
     * Constuctor for the given color picker
     * @param a The activity that uses the color picker
     * @param r Red color value that must be between 0-255 inclusive
     * @param g Green color value that must be between 0-255 inclusive
     * @param b Blue color vlaue that must be between 0-255 inclusive
     */
    ColorPicker(Activity a, int r, int g, int b){
        super(a);
        this.mActivity = a;

        if (r<=255 && r>=0){
            this.red = r;
        } else{
            this.red = 0;
        }

        if (g<=255 && g>=0){
            this.green = g;
        }else{
            this.green = 0;
        }

        if (b<=255 && b>=0){
            this.blue = b;
        }else{
            this.blue = 0;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_color_picker_dialog);

        // Initialize each UI component for use in code
        colorView = findViewById(R.id.colorView);

        redSeekBar = (SeekBar) findViewById(R.id.redSeekBar);
        greenSeekBar = (SeekBar) findViewById(R.id.greenSeekBar);
        blueSeekBar = (SeekBar) findViewById(R.id.blueSeekBar);

        seekBarLeft = redSeekBar.getPaddingLeft();

        redColorHint = (TextView) findViewById(R.id.redColorHint);
        greenColorHint = (TextView) findViewById(R.id.greenColorHint);
        blueColorHint = (TextView) findViewById(R.id.blueColorHint);

        hexColor = (EditText) findViewById(R.id.hexColor);

        redSeekBar.setOnSeekBarChangeListener(this);
        greenSeekBar.setOnSeekBarChangeListener(this);
        blueSeekBar.setOnSeekBarChangeListener(this);

        redSeekBar.setProgress(red);
        greenSeekBar.setProgress(green);
        blueSeekBar.setProgress(blue);

        colorView.setBackgroundColor(Color.rgb(red, green, blue));

        hexColor.setOnEditorActionListener( new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH
                        || event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    updateColorView(v.getText().toString());
                    InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(hexColor.getWindowToken(), 0);
                    return true;
                }

                return false;
            }
        });

    }

    private void updateColorView(String s) {
        Log.d(TAG, "updateColorView: BEGINNING FUNCTION");

        if (s.matches("-?[0-9a-fA-F]+")){
            int color = (int) Long.parseLong(s, 16);
            red = (color >> 16) & 0xFF;
            green = (color >> 8) & 0xFF;
            blue = (color >> 0) & 0xFF;
            trueRed = red;
            trueGreen =(int)(2.3286 * Math.exp((green+0.1)*0.0187));
            trueBlue = (int)(0.0055*Math.pow(blue,2)-0.6191*blue+1.6136);
            Log.d(TAG, "TRUEGREEN: "+trueGreen+" TRUEBLUE: "+trueBlue);
            if (trueBlue<0)
                trueBlue = 0;
            if (trueGreen<0)
                trueGreen = 0;
            //colorView.setBackgroundColor(Color.rgb(red, trueGreen, trueBlue));
            colorView.setBackgroundColor(Color.rgb(red, green, blue));
            redSeekBar.setProgress(red);
            greenSeekBar.setProgress(green);
            blueSeekBar.setProgress(blue);

        } else{
            hexColor.setError(mActivity.getResources().getText(R.string.hexErr));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //super.onWindowFocusChanged(hasFocus);

        thumbRect = redSeekBar.getThumb().getBounds();

        redColorHint.setX(seekBarLeft + thumbRect.left);

        if (red < 10)
            redColorHint.setText(" " + red);
        else if (red < 100)
            redColorHint.setText(" "+red);
        else
            redColorHint.setText(red + "   ");

        thumbRect = greenSeekBar.getThumb().getBounds();
        greenColorHint.setX(seekBarLeft + thumbRect.left);

        if (green<10)
            greenColorHint.setText(" "+green);
        else if (green<100)
            greenColorHint.setText(" "+green);
        else
            greenColorHint.setText(green + "   ");

        thumbRect = blueSeekBar.getThumb().getBounds();
        blueColorHint.setX(seekBarLeft + thumbRect.left);

        if (blue<10)
            blueColorHint.setText(" "+blue);
        else if (blue<100)
            blueColorHint.setText(" "+blue);
        else
            blueColorHint.setText(blue+"   ");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (seekBar.getId() == R.id.redSeekBar){
            red = progress;
            thumbRect = seekBar.getThumb().getBounds();
            redColorHint.setX(seekBarLeft + thumbRect.left);
            if (red < 10)
                redColorHint.setText(" " + red);
            else if (red < 100)
                redColorHint.setText(" "+red);
            else
                redColorHint.setText(red + "");
        }
        else if (seekBar.getId() == R.id.greenSeekBar){
            green = progress;
            if (green == 0)
                trueGreen = 0;
            else
                trueGreen =(int)(2.3286 * Math.exp((green+0.1)*0.0187));
            if (trueGreen<1)
                trueGreen=0;
            thumbRect = greenSeekBar.getThumb().getBounds();
            greenColorHint.setX(seekBarLeft + thumbRect.left);

            if (green<10)
                greenColorHint.setText(" "+green);
            else if (green<100)
                greenColorHint.setText(" "+green);
            else
                greenColorHint.setText(green + "");
        }

        else if (seekBar.getId() == R.id.blueSeekBar){
            blue = progress;
            trueBlue = (int)(0.0055*Math.pow(blue,2)-0.6191*blue+1.6136);
            if (trueBlue < 1 || blue == 0)
                trueBlue = 0;
            thumbRect = blueSeekBar.getThumb().getBounds();
            blueColorHint.setX(seekBarLeft + thumbRect.left);

            if (blue<10)
                blueColorHint.setText(" "+blue);
            else if (blue<100)
                blueColorHint.setText(" "+blue);
            else
                blueColorHint.setText(blue+"");

        }

        colorView.setBackgroundColor(Color.rgb(red,green,blue));

        hexColor.setText(String.format("%02x%02x%02x", red, green, blue));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public int getRed(){
        return red;
    }

    public int getGreen(){
        return green;
    }

    public int getBlue(){
        return blue;
    }

    public int getTrueRed(){
        return trueRed;
    }

    public int getTrueGreen(){
        return trueGreen;
    }

    public int getTrueBlue(){
        return trueBlue;
    }

    public int getColor(){
        return Color.rgb(red,green,blue);
    }


}
