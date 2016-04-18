package com.lightlinks.dibs.dibslightlinks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by NickHome on 4/17/16.
 */
public class ConfigSleepModeActivity extends AppCompatActivity {

    private Button updateButton;
    private Button cancelButton;
    private EditText end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_sleep_mode);

        updateButton = (Button) findViewById(R.id.Sleep_Button_update);
        cancelButton = (Button) findViewById(R.id.Sleep_Button_cancel);
        end_time = (EditText) findViewById(R.id.editText_sleep_end_time);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMode();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateMode(){
        String time = end_time.getText().toString();
        Toast.makeText(this,"Sleep Mode Updated", Toast.LENGTH_SHORT).show();
        finish();
    }
}