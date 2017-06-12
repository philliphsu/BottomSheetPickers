package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.view.View;

/**
 * Handles both regular clicks and long clicks on the backspace button.
 */
class OnBackspaceClickHandler implements View.OnClickListener, View.OnLongClickListener {
    private final INumberPadTimePicker.Presenter mPresenter;

    OnBackspaceClickHandler(INumberPadTimePicker.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(View v) {
        mPresenter.onBackspaceClick();
    }

    @Override
    public boolean onLongClick(View v) {
        return mPresenter.onBackspaceLongClick();
    }
}
