package com.abdallaadelessa.android.dynamictext.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.abdallaadelessa.android.dynamictext.DynamicTextManager;
import com.abdallaadelessa.android.dynamictext.ui_components.DynamicEditText;
import com.abdallaadelessa.android.dynamictext.ui_components.DynamicTextView;

public class MainActivity extends AppCompatActivity {
    DynamicTextView tvLabel;
    DynamicEditText etext;
    Button btnLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLabel = (DynamicTextView) findViewById(R.id.tvLabel);
        etext = (DynamicEditText) findViewById(R.id.etext);
        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String key = "Loaded From Code";
                tvLabel.setDynamicText(key);
                etext.setDynamicText(key);
                //DynamicTextManager.getInstance().stopAll();
            }
        });
    }
}
