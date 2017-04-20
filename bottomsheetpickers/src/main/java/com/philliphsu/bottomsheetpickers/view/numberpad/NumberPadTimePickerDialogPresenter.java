package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.support.annotation.NonNull;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

final class NumberPadTimePickerDialogPresenter extends NumberPadTimePickerPresenter
        implements INumberPadTimePicker.DialogPresenter {

    private final DigitwiseTimeParser timeParser = new DigitwiseTimeParser(timeModel);

    private INumberPadTimePicker.DialogView view;

    NumberPadTimePickerDialogPresenter(@NonNull INumberPadTimePicker.DialogView view,
                                       @NonNull LocaleModel localeModel,
                                       boolean is24HourMode) {
        super(view, localeModel, is24HourMode);
        this.view = view;
    }

    @Override
    public void onStop() {
        super.onStop();
        view = null;
    }

    @Override
    public void onCancelClick() {
        view.cancel();
    }

    @Override
    public void onOkButtonClick() {
        view.setResult(timeParser.getHour(mAmPmState), timeParser.getMinute(mAmPmState));
        view.cancel();
    }

    @Override
    void updateViewEnabledStates() {
        super.updateViewEnabledStates();
        updateOkButtonState();
    }

    private void updateOkButtonState() {
        view.setOkButtonEnabled(timeParser.checkTimeValid(mAmPmState));
    }
}
