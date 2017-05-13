package com.example.bottomsheetpickers;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static com.example.bottomsheetpickers.NumberPadTimePickerDialogFragment.MODE_ALERT;
import static com.example.bottomsheetpickers.NumberPadTimePickerDialogFragment.MODE_BOTTOM_SHEET;

public class NumberPadTimePickersActivity extends AppCompatActivity {
    private static final String TAG = NumberPadTimePickersActivity.class.getSimpleName();
    private static final String TAG_ALERT = "alert";
    private static final String TAG_BOTTOM_SHEET = "bottom_sheet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_pad_time_pickers);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPadTimePickerDialogFragment.newInstance(mListener, MODE_ALERT)
                        .show(getSupportFragmentManager(), TAG_ALERT);
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPadTimePickerDialogFragment.newInstance(mListener, MODE_BOTTOM_SHEET)
                        .show(getSupportFragmentManager(), TAG_BOTTOM_SHEET);
            }
        });
    }

    private TimePickerDialog.OnTimeSetListener mListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            Context context = NumberPadTimePickersActivity.this;
            String text = "Time set: " + DateFormat.getTimeFormat(context).format(calendar.getTime());
            Log.d(TAG, text);
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
    };
}
