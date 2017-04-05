package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.view.View;

/**
 * Created by Phillip Hsu on 4/5/2017.
 */
class OnBackspaceClickListener implements View.OnClickListener {
    private final INumberPadTimePicker.Presenter mPresenter;

    OnBackspaceClickListener(INumberPadTimePicker.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        mPresenter.onBackspaceClick();
    }
}
