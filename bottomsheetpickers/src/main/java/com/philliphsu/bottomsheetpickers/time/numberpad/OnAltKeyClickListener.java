package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.view.View;
import android.widget.TextView;

/**
 * Created by Phillip Hsu on 4/6/2017.
 */

class OnAltKeyClickListener implements View.OnClickListener {
    private final INumberPadTimePicker.Presenter mPresenter;

    OnAltKeyClickListener(INumberPadTimePicker.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        mPresenter.onAltKeyClick(((TextView) v).getText());
    }
}
