package com.example.bottomsheetpickers;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.BottomSheetPickerDialog;
import com.philliphsu.bottomsheetpickers.date.BottomSheetDatePickerDialog;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.numberpad.NumberPadTimePickerDialog;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements
        BottomSheetTimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "MainActivity";
    private static final boolean USE_BUILDERS = false;

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
                DialogFragment dialog = createDialog(group.getCheckedRadioButtonId());
                dialog.show(getSupportFragmentManager(), TAG);
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

    private DialogFragment createDialog(int checkedId) {
        if (USE_BUILDERS) {
            return createDialogWithBuilders(checkedId);
        } else {
            return createDialogWithSetters(checkedId);
        }
    }

    private DialogFragment createDialogWithBuilders(int checkedId) {
        BottomSheetPickerDialog.Builder builder = null;
        boolean custom = false;
        boolean customDark = false;
        boolean themeDark = false;

        switch (checkedId) {
            case R.id.choice_number_pad:
            case R.id.choice_number_pad_dark:
            case R.id.choice_number_pad_custom:
            case R.id.choice_number_pad_custom_dark: {
                custom = checkedId == R.id.choice_number_pad_custom;
                customDark = checkedId == R.id.choice_number_pad_custom_dark;
                themeDark = checkedId == R.id.choice_number_pad_dark || customDark;
                builder = new NumberPadTimePickerDialog.Builder(MainActivity.this);
                if (custom || customDark) {
                    ((NumberPadTimePickerDialog.Builder) builder).setHeaderTextColor(0xFFFF4081);
                }
                break;
            }
            case R.id.choice_grid_picker:
            case R.id.choice_grid_picker_dark:
            case R.id.choice_grid_picker_custom:
            case R.id.choice_grid_picker_custom_dark: {
                custom = checkedId == R.id.choice_grid_picker_custom;
                customDark = checkedId == R.id.choice_grid_picker_custom_dark;
                themeDark = checkedId == R.id.choice_grid_picker_dark || customDark;

                Calendar now = Calendar.getInstance();
                builder = new GridTimePickerDialog.Builder(
                        MainActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(MainActivity.this));
                GridTimePickerDialog.Builder gridDialogBuilder =
                        (GridTimePickerDialog.Builder) builder;
                if (custom || customDark) {
                    gridDialogBuilder.setHeaderTextColorSelected(0xFFFF4081)
                            .setHeaderTextColorUnselected(0x4AFF4081)
                            .setTimeSeparatorColor(0xFF000000)
                            .setHalfDayButtonColorSelected(0xFFFF4081)
                            .setHalfDayButtonColorUnselected(0x4AFF4081);
                }
                break;
            }
            case R.id.choice_date_picker:
            case R.id.choice_date_picker_dark:
            case R.id.choice_date_picker_custom:
            case R.id.choice_date_picker_custom_dark: {
                custom = checkedId == R.id.choice_date_picker_custom;
                customDark = checkedId == R.id.choice_date_picker_custom_dark;
                themeDark = checkedId == R.id.choice_date_picker_dark || customDark;

                Calendar now = Calendar.getInstance();
                Calendar max = Calendar.getInstance();
                max.add(Calendar.YEAR, 10);
                builder = new BottomSheetDatePickerDialog.Builder(
                        MainActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                BottomSheetDatePickerDialog.Builder dateDialogBuilder =
                        (BottomSheetDatePickerDialog.Builder) builder;
                dateDialogBuilder.setMaxDate(max)
                        .setMinDate(now)
                        .setYearRange(1970, 2032);
                if (custom || customDark) {
                    dateDialogBuilder.setHeaderTextColorSelected(0xFFFF4081)
                            .setHeaderTextColorUnselected(0x4AFF4081)
                            .setDayOfWeekHeaderTextColorSelected(0xFFFF4081)
                            .setDayOfWeekHeaderTextColorUnselected(0x4AFF4081);
                }
                break;
            }
        }

        builder.setThemeDark(themeDark);
        if (custom || customDark) {
            builder.setAccentColor(0xFFFF4081)
                    .setBackgroundColor(custom? 0xFF90CAF9 : 0xFF2196F3)
                    .setHeaderColor(custom? 0xFF90CAF9 : 0xFF2196F3)
                    .setHeaderTextDark(custom);
        }
        return builder.build();
    }

    private DialogFragment createDialogWithSetters(int checkedId) {
        BottomSheetPickerDialog dialog = null;
        boolean custom = false;
        boolean customDark = false;
        boolean themeDark = false;

        switch (checkedId) {
            case R.id.choice_number_pad:
            case R.id.choice_number_pad_dark:
            case R.id.choice_number_pad_custom:
            case R.id.choice_number_pad_custom_dark: {
                dialog = NumberPadTimePickerDialog.newInstance(MainActivity.this);
                custom = checkedId == R.id.choice_number_pad_custom;
                customDark = checkedId == R.id.choice_number_pad_custom_dark;
                themeDark = checkedId == R.id.choice_number_pad_dark || customDark;
                if (custom || customDark) {
                    ((NumberPadTimePickerDialog) dialog).setHeaderTextColor(0xFFFF4081);
                }
                break;
            }
            case R.id.choice_grid_picker:
            case R.id.choice_grid_picker_dark:
            case R.id.choice_grid_picker_custom:
            case R.id.choice_grid_picker_custom_dark: {
                Calendar now = Calendar.getInstance();
                dialog = GridTimePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(MainActivity.this));
                custom = checkedId == R.id.choice_grid_picker_custom;
                customDark = checkedId == R.id.choice_grid_picker_custom_dark;
                themeDark = checkedId == R.id.choice_grid_picker_dark || customDark;
                GridTimePickerDialog gridDialog = (GridTimePickerDialog) dialog;
                if (custom || customDark) {
                    gridDialog.setHeaderTextColorSelected(0xFFFF4081);
                    gridDialog.setHeaderTextColorUnselected(0x4AFF4081);
                    gridDialog.setTimeSeparatorColor(0xFF000000);
                    gridDialog.setHalfDayButtonColorSelected(0xFFFF4081);
                    gridDialog.setHalfDayButtonColorUnselected(0x4AFF4081);
                }
                break;
            }
            case R.id.choice_date_picker:
            case R.id.choice_date_picker_dark:
            case R.id.choice_date_picker_custom:
            case R.id.choice_date_picker_custom_dark: {
                Calendar now = Calendar.getInstance();
                dialog = BottomSheetDatePickerDialog.newInstance(
                        MainActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                custom = checkedId == R.id.choice_date_picker_custom;
                customDark = checkedId == R.id.choice_date_picker_custom_dark;
                themeDark = checkedId == R.id.choice_date_picker_dark || customDark;
                BottomSheetDatePickerDialog dateDialog = (BottomSheetDatePickerDialog) dialog;
                dateDialog.setMinDate(now);
                Calendar max = Calendar.getInstance();
                max.add(Calendar.YEAR, 10);
                dateDialog.setMaxDate(max);
                dateDialog.setYearRange(1970, 2032);
                if (custom || customDark) {
                    dateDialog.setHeaderTextColorSelected(0xFFFF4081);
                    dateDialog.setHeaderTextColorUnselected(0x4AFF4081);
                    dateDialog.setDayOfWeekHeaderTextColorSelected(0xFFFF4081);
                    dateDialog.setDayOfWeekHeaderTextColorUnselected(0x4AFF4081);
                }
                break;
            }
        }

        dialog.setThemeDark(themeDark);
        if (custom || customDark) {
            dialog.setAccentColor(0xFFFF4081);
            dialog.setBackgroundColor(custom? 0xFF90CAF9 : 0xFF2196F3);
            dialog.setHeaderColor(custom? 0xFF90CAF9 : 0xFF2196F3);
            dialog.setHeaderTextDark(custom);
        }

        return dialog;
    }
}
