package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.content.DialogInterface;

/**
 * Handles clicks on the dialog's positive and negative buttons.
 */
class OnDialogButtonClickListener implements DialogInterface.OnClickListener {
    private final INumberPadTimePicker.DialogPresenter mPresenter;

    OnDialogButtonClickListener(INumberPadTimePicker.DialogPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                mPresenter.onOkButtonClick();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                mPresenter.onCancelClick();
                break;
        }
    }
}
