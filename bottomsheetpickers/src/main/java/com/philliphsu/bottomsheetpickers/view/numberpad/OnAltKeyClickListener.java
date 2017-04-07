package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.view.View;
import android.widget.TextView;

/**
 * Created by Phillip Hsu on 4/6/2017.
 */

class OnAltKeyClickListener implements View.OnClickListener {
    private final INumberPadTimePicker.Presenter presenter;

    OnAltKeyClickListener(INumberPadTimePicker.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View v) {
        presenter.onAltKeyClick(((TextView) v).getText());
    }
}
