package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.support.annotation.NonNull;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

final class NumberPadTimePickerDialogPresenter extends NumberPadTimePickerPresenter
        implements INumberPadTimePicker.DialogPresenter {

    private final DigitwiseTimeParser mTimeParser = new DigitwiseTimeParser(timeModel);

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
    void updateViewEnabledStates() {
        super.updateViewEnabledStates();
        updateOkButtonState();
    }

    private void updateOkButtonState() {
        mView.setOkButtonEnabled(mTimeParser.checkTimeValid(mAmPmState));
    }
}
