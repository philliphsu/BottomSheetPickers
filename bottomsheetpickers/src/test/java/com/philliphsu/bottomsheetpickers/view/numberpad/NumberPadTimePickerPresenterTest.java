package com.philliphsu.bottomsheetpickers.view.numberpad;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static com.philliphsu.bottomsheetpickers.view.numberpad.ButtonTextModel.text;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
        mButtonTextModels[MODE_12HR] = new ButtonTextModel(mLocaleModel, false);
        mButtonTextModels[MODE_24HR] = new ButtonTextModel(mLocaleModel, true);
    }

    @Test
    public void verifyViewEnabledStatesForEmptyState() {
        createNewViewAndPresenter(MODE_12HR);
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

        createNewViewAndPresenter(MODE_24HR);
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
    public final void mode12Hr_VerifyViewEnabledStates_Input_1_to_9() {
        verifyViewEnabledStates(TestSuite.MODE_12HR_TESTS_1_TO_9, MODE_12HR);
    }

    @Test
    public final void mode24Hr_VerifyViewEnabledStates_Input_0_To_9() {
        verifyViewEnabledStates(TestSuite.MODE_24HR_TESTS_0_TO_9, MODE_24HR);
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

    private void verifyViewEnabledStates(List<TestCase> testSuite, int mode) {
        for (TestCase test : testSuite) {
            verifyViewEnabledStates(test, mode);
        }
    }

    void verifyViewEnabledStates(TestCase test, int mode) {
        createNewViewAndPresenter(mode);
        for (int digit : test.sequence) {
            mPresenters[mode].onNumberKeyClick(text(digit));
        }
        verify(mViews[mode]).setNumberKeysEnabled(test.numberKeysEnabledStart, test.numberKeysEnabledEnd);
        verify(mViews[mode]).setBackspaceEnabled(test.backspaceEnabled);
        verify(mViews[mode], times(test.timeDisplay == null ? 0 : 1)).updateTimeDisplay(test.timeDisplay);
        verify(mViews[mode], times(test.ampmDisplay == null ? 0 : 1)).updateAmPmDisplay(test.ampmDisplay);
        verify(mViews[mode]).setLeftAltKeyEnabled(test.leftAltKeyEnabled);
        verify(mViews[mode]).setRightAltKeyEnabled(test.rightAltKeyEnabled);
        verify(mViews[mode]).setHeaderDisplayFocused(test.headerDisplayFocused);
    }

    private String altText(int leftOrRight, int mode) {
        return mButtonTextModels[mode].altText(leftOrRight);
    }
    
    private void createNewViewAndPresenter(int mode) {
        mViews[mode] = mock(getViewClass());
        mPresenters[mode] = createPresenter(mViews[mode], mLocaleModel, mode == MODE_24HR);
    }
}
