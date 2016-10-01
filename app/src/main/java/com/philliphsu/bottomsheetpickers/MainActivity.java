package com.philliphsu.bottomsheetpickers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.philliphsu.bottomsheettimepickers.timepickers.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheettimepickers.timepickers.NumpadTimePickerDialog;

public class MainActivity extends AppCompatActivity implements BottomSheetTimePickerDialog.OnTimeSetListener {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumpadTimePickerDialog.newInstance(MainActivity.this).show(getSupportFragmentManager(), TAG);
            }
        });
    }

    @Override
    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {
        Log.d(TAG, "Time set: " + String.format("%02d:%02d", hourOfDay, minute));
    }
}
