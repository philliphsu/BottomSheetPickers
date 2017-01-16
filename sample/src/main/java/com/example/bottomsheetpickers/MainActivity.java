package com.example.bottomsheetpickers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.date.BottomSheetDatePickerDialog;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.numberpad.NumberPadTimePickerDialog;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements
        BottomSheetTimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "MainActivity";

    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (TextView) findViewById(R.id.text);

        final RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int checkedId = group.getCheckedRadioButtonId();
                switch (checkedId) {
                    case R.id.choice_number_pad:
                    case R.id.choice_number_pad_dark:
                    case R.id.choice_number_pad_custom:
                    case R.id.choice_number_pad_custom_dark: {
                        NumberPadTimePickerDialog dialog = NumberPadTimePickerDialog.newInstance(
                                MainActivity.this);
                        final boolean custom = checkedId == R.id.choice_number_pad_custom;
                        final boolean customDark = checkedId == R.id.choice_number_pad_custom_dark;
                        dialog.setThemeDark(checkedId == R.id.choice_number_pad_dark || customDark);
                        if (custom || customDark) {
                            dialog.setAccentColor(0xFFE91E63);
                            dialog.setBackgroundColor(custom? 0xFFA5D6A7 : 0xFF43A047);
                            dialog.setHeaderColor(custom? 0xFFA5D6A7 : 0xFF43A047);
                            dialog.setHeaderTextDark(custom);
                        }
                        dialog.show(getSupportFragmentManager(), TAG);
                        break;
                    }
                    case R.id.choice_grid_picker:
                    case R.id.choice_grid_picker_dark: {
                        Calendar now = Calendar.getInstance();
                        GridTimePickerDialog dialog = GridTimePickerDialog.newInstance(
                                MainActivity.this,
                                now.get(Calendar.HOUR_OF_DAY),
                                now.get(Calendar.MINUTE),
                                DateFormat.is24HourFormat(MainActivity.this));
                        dialog.setThemeDark(checkedId == R.id.choice_grid_picker_dark);
                        dialog.show(getSupportFragmentManager(), TAG);
                        break;
                    }
                    case R.id.choice_date_picker:
                    case R.id.choice_date_picker_dark: {
                        Calendar now = Calendar.getInstance();
                        BottomSheetDatePickerDialog dialog = BottomSheetDatePickerDialog.newInstance(
                                MainActivity.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH));
                        dialog.setThemeDark(checkedId == R.id.choice_date_picker_dark);
                        dialog.setMinDate(now);
                        Calendar max = Calendar.getInstance();
                        max.add(Calendar.YEAR, 10);
                        dialog.setMaxDate(max);
                        dialog.setYearRange(1970, 2032);
                        dialog.show(getSupportFragmentManager(), TAG);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {
        Calendar cal = new java.util.GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        mText.setText("Time set: " + DateFormat.getTimeFormat(this).format(cal.getTime()));
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new java.util.GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mText.setText("Date set: " + DateFormat.getDateFormat(this).format(cal.getTime()));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                findViewById(R.id.fab).requestFocus();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                findViewById(R.id.radioGroup).requestFocus();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
