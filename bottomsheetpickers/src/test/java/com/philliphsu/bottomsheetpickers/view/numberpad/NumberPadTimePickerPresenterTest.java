package com.philliphsu.bottomsheetpickers.view.numberpad;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static com.philliphsu.bottomsheetpickers.view.numberpad.ButtonTextModel.text;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(mLocaleModel.getTimeSeparator(anyBoolean())).thenReturn(":");

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

    @Test
    public final void mode12Hr_VerifyViewEnabledStates_Input_10_to_95() {
        verifyViewEnabledStates(TestSuite.MODE_12HR_TESTS_10_TO_95, MODE_12HR);
    }

    // We probably don't want this test to run in this class, so leave off the @Test annotation.
    // Override this method in subclasses and add the @Test annotation, then call up to super.
    public void mode12Hr_VerifyOnTimeSetCallback() {
        for (int time = 100; time <= 1259; time++) {
            if (time % 100 > 59) {
                System.out.println("Skipping invalid time " + time);
                continue;
            }
            System.out.println("Testing time " + time);
            for (int amOrPm = 0; amOrPm < 2; amOrPm++) {
                createNewViewAndPresenter(MODE_12HR);
                if (time <= 959) {
                    mPresenters[MODE_12HR].onNumberKeyClick(text(time / 100));
                    mPresenters[MODE_12HR].onNumberKeyClick(text((time % 100) / 10));
                    mPresenters[MODE_12HR].onNumberKeyClick(text(time % 10));
                } else {
                    mPresenters[MODE_12HR].onNumberKeyClick(text(time / 1000));
                    mPresenters[MODE_12HR].onNumberKeyClick(text((time % 1000) / 100));
                    mPresenters[MODE_12HR].onNumberKeyClick(text((time % 100) / 10));
                    mPresenters[MODE_12HR].onNumberKeyClick(text(time % 10));
                }
                mPresenters[MODE_12HR].onAltKeyClick(altText(amOrPm, MODE_12HR));
                final int hour = (time >= 1200 ? 0 : time / 100) + (amOrPm == 1 ? 12 : 0);
                final int minute = time % 100;
                confirmTimeSelection(mPresenters[MODE_12HR], MODE_12HR, hour, minute);
            }
        }
    }

    @Test
    public void rotateDevice_savesAndRestoresInstanceState() {

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

    /**
     * Subclasses should perform their logic to confirm the selected time.
     * This class is not responsible for this behavior because there is no
     * 'ok' button the {@link com.philliphsu.bottomsheetpickers.view.numberpad.INumberPadTimePicker.Presenter
     * base presenter} is aware of.
     */
    void confirmTimeSelection(INumberPadTimePicker.Presenter presenter, int mode, int hour, int minute) {
        throw new UnsupportedOperationException();
    }

    private void verifyViewEnabledStates(List<TestCase> testSuite, int mode) {
        for (TestCase test : testSuite) {
            System.out.println("Testing sequence: " + Arrays.toString(test.sequence));
            verifyViewEnabledStates(test, mode);
        }
    }

    void verifyViewEnabledStates(TestCase test, int mode) {
        createNewViewAndPresenter(mode);
        for (int digit : test.sequence) {
            mPresenters[mode].onNumberKeyClick(text(digit));
        }
        // There could legitimately be multiple calls to these methods with the same arguments.
        // E.g. in MODE_12HR, inputting a sequence of [1, 0, 0] will result in two calls of
        // setNumberKeysEnabled(0, 10). This is fine because we're just interested in verifying
        // the final states specified by the TestCase.
        //
        // Note that we are not verifying all multiple calls to these methods; specifically,
        // we are not verifying intermediate states. This is only verifying the final states
        // specified by the TestCase.
        verify(mViews[mode], atLeastOnce()).setNumberKeysEnabled(test.numberKeysEnabledStart,
                test.numberKeysEnabledEnd);
        verify(mViews[mode], atLeastOnce()).setBackspaceEnabled(test.backspaceEnabled);
        verify(mViews[mode], atLeastOnce()).setLeftAltKeyEnabled(test.leftAltKeyEnabled);
        verify(mViews[mode], atLeastOnce()).setRightAltKeyEnabled(test.rightAltKeyEnabled);
        verify(mViews[mode], atLeastOnce()).setHeaderDisplayFocused(test.headerDisplayFocused);

        // Formatting of the header display is currently not the main concern.
//        verify(mViews[mode], times(test.timeDisplay == null ? 0 : 1)).updateTimeDisplay(test.timeDisplay);
//        verify(mViews[mode], times(test.ampmDisplay == null ? 0 : 1)).updateAmPmDisplay(test.ampmDisplay);
    }

    private String altText(int leftOrRight, int mode) {
        return mButtonTextModels[mode].altText(leftOrRight);
    }
    
    private void createNewViewAndPresenter(int mode) {
        mViews[mode] = mock(getViewClass());
        mPresenters[mode] = createPresenter(mViews[mode], mLocaleModel, mode == MODE_24HR);
    }
}
