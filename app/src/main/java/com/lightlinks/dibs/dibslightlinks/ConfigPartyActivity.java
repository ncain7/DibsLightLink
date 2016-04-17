package com.lightlinks.dibs.dibslightlinks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


/**
 * Created by NickHome on 4/15/16.
 */
public class ConfigPartyActivity extends AppCompatActivity {

    private Spinner spinner_colorset, spinner_modes, spinner_pattern;
    private Button update, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_party);

        initSpinners();
    }

    public void initSpinners() {
        // Initialize spinner
        spinner_colorset = (Spinner) findViewById(R.id.spinner_colorset);
        spinner_modes = (Spinner) findViewById(R.id.spinner_modes);
        spinner_pattern = (Spinner) findViewById(R.id.spinner_pattern);

        ArrayAdapter<CharSequence> colorset_adapter = ArrayAdapter.createFromResource(this, R.array.colorset_array, R.layout.colorset_spinner_item);
        colorset_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> mode_adapter = ArrayAdapter.createFromResource(this, R.array.modes_array, R.layout.spinner_item_modes);
        mode_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> pattern_adapter = ArrayAdapter.createFromResource(this, R.array.patterns_array, R.layout.spinner_item_patterns);
        pattern_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set adapter to spinner
        spinner_colorset.setAdapter(colorset_adapter);
        spinner_modes.setAdapter(mode_adapter);
        spinner_pattern.setAdapter(pattern_adapter);


    }
}
