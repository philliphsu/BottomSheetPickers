package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.support.annotation.NonNull;

final class NumberPadTimePickerDialogPresenter extends NumberPadTimePickerPresenter
        implements INumberPadTimePicker.DialogPresenter {

    private final DigitwiseTimeParser mTimeParser = new DigitwiseTimeParser(mTimeModel);

    private INumberPadTimePicker.DialogView mView;

    NumberPadTimePickerDialogPresenter(@NonNull INumberPadTimePicker.DialogView view,
                                       @NonNull LocaleModel localeModel,
                                       boolean is24HourMode) {
        super(view, localeModel, is24HourMode);
        mView = view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mView = null;
    }

    @Override
    public void onCancelClick() {
        mView.cancel();
    }

    @Override
    public void onOkButtonClick() {
        mView.setResult(mTimeParser.getHour(mAmPmState), mTimeParser.getMinute(mAmPmState));
        mView.cancel();
    }

    @Override
    public void onDialogShow() {
        mView.showOkButton();
    }

    @Override
    void updateViewEnabledStates() {
        super.updateViewEnabledStates();
        updateOkButtonState();
    }

    private void updateOkButtonState() {
        mView.setOkButtonEnabled(mTimeParser.checkTimeValid(mAmPmState));
    }
}
