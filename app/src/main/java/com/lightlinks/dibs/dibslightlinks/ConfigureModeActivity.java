package com.lightlinks.dibs.dibslightlinks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by NickHome on 4/15/16.
 */
public class ConfigureModeActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configure_modes);

        //Setup toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        //Setup buttons and their listeners
        ImageButton ib_standard_mode = (ImageButton) findViewById(R.id.imageButton_standard_mode);
        ib_standard_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureStandardMode();
            }
        });
        
        ImageButton ib_ps_mode = (ImageButton) findViewById(R.id.imageButton_powerSave_mode);
        ib_ps_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configurePowerSaveMode();
            }
        });
        
        ImageButton ib_party_mode = (ImageButton) findViewById(R.id.imageButton_party_mode);
        ib_party_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configurePartyMode();
            }
        });

        ImageButton ib_crowd_mode = (ImageButton) findViewById(R.id.imageButton_crowd_mode);
        ib_crowd_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureCrowdMode();
            }
        });
        
        ImageButton ib_sleep_mode = (ImageButton) findViewById(R.id.imageButton_sleep_mode);
        ib_sleep_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureSleepMode();
            }
        });
        ImageButton ib_security_mode = (ImageButton) findViewById(R.id.imageButton_security_mode);
        ib_security_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureSecurityMode();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void configureSecurityMode() {
    }

    private void configureSleepMode() {
        
    }

    private void configureCrowdMode() {
        
    }

    private void configurePartyMode() {
        Intent config_party_intent = new Intent(this, ConfigPartyActivity.class);
        startActivity(config_party_intent);
    }

    private void configurePowerSaveMode() {
        Intent config_ps_mode_intent = new Intent(this, ConfigPowerSaveActivity.class);
        startActivity(config_ps_mode_intent);
    }

    private void configureStandardMode() {
        //Create and intent to go to standard modes configure page
        //Intent stnd_mode_config = new Intent(this, StandardConfigActivity.class);
       // startActivity(stnd_mode_config);
        
    }
}
