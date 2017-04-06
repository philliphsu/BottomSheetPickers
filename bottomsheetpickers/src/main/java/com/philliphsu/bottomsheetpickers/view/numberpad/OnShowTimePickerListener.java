package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.DialogInterface;

/**
 * Created by Phillip Hsu on 4/5/2017.
 */
class OnShowTimePickerListener implements DialogInterface.OnShowListener {
    private final INumberPadTimePicker.Presenter mPresenter;

    OnShowTimePickerListener(INumberPadTimePicker.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        mPresenter.onShowTimePicker();
    }
}