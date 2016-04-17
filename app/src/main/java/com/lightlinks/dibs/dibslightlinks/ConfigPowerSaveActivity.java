package com.lightlinks.dibs.dibslightlinks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.graphics.Color;
import android.widget.TimePicker;

/**
 * Created by NickHome on 4/15/16.
 */
public class ConfigPowerSaveActivity extends AppCompatActivity{

    private Button update, cancel;
    private Spinner spinner_modes;
    private View color_view;
    private ColorPicker cp;
    private int LED_red, LED_green, LED_blue;
    private int rgbColor;
    private TimePicker end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_ps_mode);


        initSpinner();
        color_view = findViewById(R.id.color_view);
        cp = new ColorPicker(this, 0, 0, 0);
        color_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cp.show();
                Button updateColor = (Button) cp.findViewById(R.id.button_update_color);
                if (updateColor != null) {
                    updateColor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LED_red = cp.getRed();
                            LED_green = cp.getGreen();
                            LED_blue = cp.getBlue();
                            color_view.setBackgroundColor(Color.rgb(LED_red, LED_green, LED_blue));
                            cp.dismiss();
                        }
                    });

                }
            }
        });

        //end_time = (TimePicker) findViewById()

    }

    public void initSpinner(){
        spinner_modes = (Spinner) findViewById(R.id.spinner_modes);
        ArrayAdapter<CharSequence> adapter_modes = ArrayAdapter.createFromResource(this, R.array.modes_array, R.layout.spinner_item_modes);
        adapter_modes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_modes.setAdapter(adapter_modes);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
