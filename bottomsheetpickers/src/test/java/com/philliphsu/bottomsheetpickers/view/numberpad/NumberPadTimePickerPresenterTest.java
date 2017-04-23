package com.philliphsu.bottomsheetpickers.view.numberpad;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.philliphsu.bottomsheetpickers.view.numberpad.ButtonTextModel.text;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class NumberPadTimePickerPresenterTest {
    static final int MODE_12HR = 0;
    static final int MODE_24HR = 1;

    private final INumberPadTimePicker.View[] mViews = new INumberPadTimePicker.View[2];
    private final INumberPadTimePicker.Presenter[] mPresenters = new INumberPadTimePicker.Presenter[2];
    private final ButtonTextModel[] mButtonTextModels = new ButtonTextModel[2];

    @Mock
    private LocaleModel mLocaleModel;

    @Before
    public final void setup() {
        // Inject mocks annotated with the @Mock annotation.
        MockitoAnnotations.initMocks(this);
        mViews[MODE_12HR] = mock(getViewClass());
        mPresenters[MODE_12HR] = createPresenter(mViews[MODE_12HR], mLocaleModel, false);
        mButtonTextModels[MODE_12HR] = new ButtonTextModel(mLocaleModel, false);

        mViews[MODE_24HR] = mock(getViewClass());
        mPresenters[MODE_24HR] = createPresenter(mViews[MODE_24HR], mLocaleModel, true);
        mButtonTextModels[MODE_24HR] = new ButtonTextModel(mLocaleModel, true);
    }

    @Test
    public void verifyViewEnabledStatesForEmptyState() {
        mPresenters[MODE_12HR].onCreate(NumberPadTimePickerState.EMPTY);
        verify(mViews[MODE_12HR]).setNumberKeysEnabled(1, 10);
        verify(mViews[MODE_12HR]).setBackspaceEnabled(false);
        // Assuming no initial text for the time display, there is no need to have to call this.
        verify(mViews[MODE_12HR], never()).updateTimeDisplay(null /* value doesn't matter */);
        verify(mViews[MODE_12HR]).updateAmPmDisplay(null);
        verify(mViews[MODE_12HR]).setAmPmDisplayVisible(true);
        verify(mViews[MODE_12HR]).setAmPmDisplayIndex(mLocaleModel.isAmPmWrittenBeforeTime() ? 0 : 1);
        verify(mViews[MODE_12HR]).setLeftAltKeyText(altText(0, MODE_12HR));
        verify(mViews[MODE_12HR]).setRightAltKeyText(altText(1, MODE_12HR));
        verify(mViews[MODE_12HR]).setLeftAltKeyEnabled(false);
        verify(mViews[MODE_12HR]).setRightAltKeyEnabled(false);
        verify(mViews[MODE_12HR]).setHeaderDisplayFocused(true);

        mPresenters[MODE_24HR].onCreate(NumberPadTimePickerState.EMPTY);
        verify(mViews[MODE_24HR]).setNumberKeysEnabled(0, 10);
        verify(mViews[MODE_24HR]).setBackspaceEnabled(false);
        verify(mViews[MODE_24HR], never()).updateTimeDisplay(null /* value doesn't matter */);
        verify(mViews[MODE_24HR], never()).updateAmPmDisplay(null /* value doesn't matter */);
        verify(mViews[MODE_24HR]).setAmPmDisplayVisible(false);
        verify(mViews[MODE_24HR], never()).setAmPmDisplayIndex(0 /* value doesn't matter */);
        verify(mViews[MODE_24HR]).setLeftAltKeyText(altText(0, MODE_24HR));
        verify(mViews[MODE_24HR]).setRightAltKeyText(altText(1, MODE_24HR));
        verify(mViews[MODE_24HR]).setLeftAltKeyEnabled(false);
        verify(mViews[MODE_24HR]).setRightAltKeyEnabled(false);
        verify(mViews[MODE_24HR]).setHeaderDisplayFocused(true);
    }

    @Test
    public void clickOnNumberKey_UpdatesTimeDisplay() {
        // Number texts are the same for both 12-hour and 24-hour modes.
        
        mPresenters[MODE_12HR].onNumberKeyClick(text(1));
        verify(mViews[MODE_12HR]).updateTimeDisplay(text(1));

        mPresenters[MODE_24HR].onNumberKeyClick(text(1));
        verify(mViews[MODE_24HR]).updateTimeDisplay(text(1));
    }

    INumberPadTimePicker.View getView(int mode) {
        return mViews[mode];
    }

    INumberPadTimePicker.Presenter getPresenter(int mode) {
        return mPresenters[mode];
    }

    Class<? extends INumberPadTimePicker.View> getViewClass() {
        return INumberPadTimePicker.View.class;
    }

    INumberPadTimePicker.Presenter createPresenter(INumberPadTimePicker.View view,
                                                   LocaleModel localeModel,
                                                   boolean is24HourMode) {
        return new NumberPadTimePickerPresenter(view, localeModel, is24HourMode);
    }

    private String altText(int leftOrRight, int mode) {
        return mButtonTextModels[mode].altText(leftOrRight);
    }
}
