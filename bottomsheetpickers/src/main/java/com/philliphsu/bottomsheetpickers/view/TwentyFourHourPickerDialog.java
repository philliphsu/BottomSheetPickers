package com.philliphsu.bottomsheetpickers.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.philliphsu.bottomsheetpickers.R;

/**
 * Created by Phillip Hsu on 4/4/2017.
 */

public class TwentyFourHourPickerDialog extends AlertDialog {
    public TwentyFourHourPickerDialog(Context context) {
        super(context);
        final Context themeContext = getContext();
        final LayoutInflater inflater = LayoutInflater.from(themeContext);
        final View view = inflater.inflate(R.layout.bsp_twentyfour_hour_picker_dialog, null);
        setView(view);
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }
}
