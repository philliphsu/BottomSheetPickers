package com.philliphsu.bottomsheetpickers.view.numberpad;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

public class NumberPadTimePickerDialogPresenterTest extends NumberPadTimePickerPresenterTest {
    @Override
    Class<? extends INumberPadTimePicker.DialogView> getViewClass() {
        return INumberPadTimePicker.DialogView.class;
    }

    @Override
    INumberPadTimePicker.DialogPresenter createPresenter(INumberPadTimePicker.View view,
                                                   LocaleModel localeModel,
                                                   boolean is24HourMode) {
        return new NumberPadTimePickerDialogPresenter((INumberPadTimePicker.DialogView) view,
                localeModel, is24HourMode);
    }
}
