package com.young.myresultfix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String DEX_DIR = "patch";
    Button btnTest;
    Button btnRepair;
    Button btnTest2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTest = findViewById(R.id.btn_test);
        btnRepair = findViewById(R.id.btn_repair);
        btnRepair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("young","btnRepair");
                new HotFixUtil().doHotFix(MainActivity.this);
            }
        });

        btnTest2 = findViewById(R.id.btn_test2);
        init();
    }

    private void init() {
        File dexFile = new File(this.getExternalFilesDir(null), DEX_DIR);
        if (!dexFile.exists()) {
            dexFile.mkdirs();
        }
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("young","BtnTest");
                new Test().toastBug(getApplicationContext());
            }
        });
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Test().toastBug2(MainActivity.this);
            }
        });
    }
}
