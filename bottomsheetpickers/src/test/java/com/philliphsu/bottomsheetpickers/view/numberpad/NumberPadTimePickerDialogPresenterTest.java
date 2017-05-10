package com.philliphsu.bottomsheetpickers.view.numberpad;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import org.junit.Test;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

public class NumberPadTimePickerDialogPresenterTest extends NumberPadTimePickerPresenterTest {

    @Override
    public void verifyViewEnabledStatesForEmptyState() {
        super.verifyViewEnabledStatesForEmptyState();
        verify(getView(MODE_12HR)).setOkButtonEnabled(false);
        verify(getView(MODE_24HR)).setOkButtonEnabled(false);
    }

    @Test
    @Override
    public void mode12Hr_VerifyOnTimeSetCallback() {
        super.mode12Hr_VerifyOnTimeSetCallback();
    }

    @Test
    @Override
    public void mode12Hr_VerifyOnTimeSetCallback_UsingAltButtons() {
        super.mode12Hr_VerifyOnTimeSetCallback_UsingAltButtons();
    }

    @Test
    @Override
    public void mode24Hr_VerifyOnTimeSetCallback() {
        super.mode24Hr_VerifyOnTimeSetCallback();
    }

    @Test
    @Override
    public void mode24Hr_VerifyOnTimeSetCallback_UsingAltButtons() {
        super.mode24Hr_VerifyOnTimeSetCallback_UsingAltButtons();
    }

    @Override
    void verifyViewEnabledStates(TestCase test, int mode) {
        super.verifyViewEnabledStates(test, mode);
        verify(getView(mode), atLeastOnce()).setOkButtonEnabled(test.okButtonEnabled);
    }

    @Override
    void confirmTimeSelection(INumberPadTimePicker.Presenter presenter, int mode, int hour, int minute) {
        ((INumberPadTimePicker.DialogPresenter) presenter).onOkButtonClick();
        verify(getView(mode)).setResult(hour, minute);
    }

    @Override
    INumberPadTimePicker.DialogView getView(int mode) {
        return (INumberPadTimePicker.DialogView) super.getView(mode);
    }

    @Override
    INumberPadTimePicker.DialogPresenter getPresenter(int mode) {
        return (INumberPadTimePicker.DialogPresenter) super.getPresenter(mode);
    }

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
