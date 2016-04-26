package com.lightlinks.dibs.dibslightlinks;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.graphics.Color;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by NickHome on 4/15/16.
 */
public class ConfigPowerSaveActivity extends AppCompatActivity{
    private static final String MYAPPPREFS = "DibsPrefs";
    private String TAG = "ConfigPowerSaveActivity";
    private Button update, cancel;
    private Spinner spinner_modes;
    private View color_view;
    private ColorPicker cp;
    private int LED_red, LED_green, LED_blue;
    private int rgbColor;
    private EditText end_time;
    private String def_mode;
    int mode_color;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_ps_mode);
        prefs = getSharedPreferences(MYAPPPREFS,0);


        initSpinner();
        color_view = findViewById(R.id.color_view);
        cp = getColPreferences(1);
        color_view.setBackgroundColor(mode_color);
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
                            LED_green = cp.getTrueGreen();
                            LED_blue = cp.getTrueBlue();
                            color_view.setBackgroundColor(cp.getColor());
                            cp.dismiss();
                        }
                    });

                }
            }
        });

        end_time = (EditText) findViewById(R.id.editText_end_time);


        //Find buttons in xml activity
        update = (Button) findViewById(R.id.Button_update);
        cancel = (Button) findViewById(R.id.Button_cancel);

        //Set listeners
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** TODO
                 * make update function to get all data from activity
                 */
                updateMode();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //simply close this activity
            }
        });



    }

    public void initSpinner() {
        spinner_modes = (Spinner) findViewById(R.id.spinner_modes);
        ArrayAdapter<CharSequence> adapter_modes = ArrayAdapter.createFromResource(this, R.array.modes_array, R.layout.spinner_item_modes);
        adapter_modes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_modes.setAdapter(adapter_modes);
        spinner_modes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String mode = spinner_modes.getSelectedItem().toString();
                def_mode = mode;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                String mode = spinner_modes.getItemAtPosition(0).toString();
                def_mode = mode;
            }
        });
    }

    private ColorPicker getColPreferences(int cur_mode) {
        ColorPicker modeColor;
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
                //Toast.makeText(this,"Updated Standard Mode Color",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                prefs.edit().putInt("PSModeColorAPP", Color.rgb(r, g, b)).apply();
                g = ColorHelp.scaleGreen(g);
                b = ColorHelp.scaleBlue(b);
                prefs.edit().putInt("PSModeColorTrue",Color.rgb(r,g,b)).apply();
                //Toast.makeText(this,"Updated Power Save Mode Color",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                prefs.edit().putInt("CrowdModeColor", Color.rgb(r, g, b)).apply();
                g = ColorHelp.scaleGreen(g);
                b = ColorHelp.scaleBlue(b);
                prefs.edit().putInt("CrowdModeColorTrue",Color.rgb(r,g,b)).apply();
                //Toast.makeText(this,"Crowd Mode Color Updated",Toast.LENGTH_SHORT).show();
                break;
            case 5:
                prefs.edit().putInt("SecurityModeColor", Color.rgb(r, g, b)).apply();
                g = ColorHelp.scaleGreen(g);
                b = ColorHelp.scaleBlue(b);
                prefs.edit().putInt("SecurityModeColorTrue",Color.rgb(r,g,b)).apply();
                //Toast.makeText(this,"Security Mode Color Updated",Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void updateMode(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String command = "LED " + LED_red + " " + LED_green + " " + LED_green + "5|PIR ON|TIRS OFF|" +
                "PAT ALT "+LED_red+" "+LED_green+" "+LED_blue+" 0 0 0 5|";
        prefs.edit().putString("PSModePref",command).apply(); //Update system preferences
        String time = end_time.getText().toString();
        Switch time_of_day = (Switch) findViewById(R.id.switch1);
        String[] times = time.split(":");
        if (time.length()==2) {
            int hours = Integer.parseInt(times[0]);
            int mins = Integer.parseInt(times[1]);
        }
        updateColPrefs(1,cp.getRed(),cp.getGreen(),cp.getBlue());
        Toast.makeText(this, "Power Save Mode updated",Toast.LENGTH_SHORT).show();

        finish();


    }
}
