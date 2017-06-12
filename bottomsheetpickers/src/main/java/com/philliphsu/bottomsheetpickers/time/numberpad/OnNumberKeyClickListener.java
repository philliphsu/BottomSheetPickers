package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.view.View;
import android.widget.TextView;

/**
 * Created by Phillip Hsu on 4/5/2017.
 */
class OnNumberKeyClickListener implements View.OnClickListener {
    private final INumberPadTimePicker.Presenter mPresenter;

    OnNumberKeyClickListener(INumberPadTimePicker.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        mPresenter.onNumberKeyClick(((TextView) v).getText());
    }
}
