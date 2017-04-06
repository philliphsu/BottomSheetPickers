package com.philliphsu.bottomsheetpickers.view.numberpad;

/**
 * Created by Phillip Hsu on 4/5/2017.
 */

final class NumberPadTimePickerPresenter implements INumberPadTimePicker.Presenter {

    private final DigitwiseTimeModel timeModel = new DigitwiseTimeModel();

    private final INumberPadTimePicker.View view;

    NumberPadTimePickerPresenter(INumberPadTimePicker.View view) {
        this.view = view;
    }

    @Override
    public void onNumberKeyClick(CharSequence numberKeyText) {

    }

    @Override
    public void onBackspaceClick() {
        // TODO: Check if we're removing the AM/PM label before removing any digits.
        timeModel.removeDigit();
        // TODO: Update time display.
        view.setBackspaceEnabled(timeModel.count() > 0);
        // TODO: Update number key states.
        // TODO: Update ok button state.
    }

    @Override
    public void onShowTimePicker(/*TODO: Require is24HourMode param*/) {
        view.setOkButtonEnabled(false);
        view.setBackspaceEnabled(false);
        view.updateTimeDisplay(null);
        view.updateAmPmDisplay(null);
        // TODO: Update number key states.
        // TODO: Set the alt button texts according to is24HourMode.
    }
}
