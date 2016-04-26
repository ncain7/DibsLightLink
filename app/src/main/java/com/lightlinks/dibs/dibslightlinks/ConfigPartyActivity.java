package com.lightlinks.dibs.dibslightlinks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


/**
 * Created by NickHome on 4/15/16.
 */
public class ConfigPartyActivity extends AppCompatActivity {
    private static final String MYAPPPREFS = "DibsPrefs";
    private Spinner spinner_colorset, spinner_modes, spinner_pattern;
    private Button update, cancel;
    private String def_mode, pattern, colorSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_party);
        update = (Button) findViewById(R.id.button_party_update);
        cancel = (Button) findViewById(R.id.button_party_cancel);

        initSpinners();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

        spinner_pattern.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String pat = spinner_pattern.getSelectedItem().toString();
                pattern = pat;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String pat = spinner_pattern.getItemAtPosition(0).toString();
                pattern = pat;

            }
        });

        spinner_colorset.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cS = spinner_colorset.getSelectedItem().toString();
                colorSet = cS;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String cS = spinner_colorset.getItemAtPosition(0).toString();
                colorSet = cS;
            }
        });

    }
}
