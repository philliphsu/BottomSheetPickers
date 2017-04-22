package com.philliphsu.bottomsheetpickers.view.numberpad;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NumberPadTimePickerPresenterTest {
    private static final int MODE_12HR = 0;
    private static final int MODE_24HR = 1;

    private final INumberPadTimePicker.View[] mViews = new INumberPadTimePicker.View[2];
    private final INumberPadTimePicker.Presenter[] mPresenters = new INumberPadTimePicker.Presenter[2];

    {
        mViews[MODE_12HR] = mock(INumberPadTimePicker.View.class);
        mViews[MODE_24HR] = mock(INumberPadTimePicker.View.class);
    }

    @Mock
    private LocaleModel mLocaleModel;

    @Before
    public void setup() {
        // Inject mocks annotated with the @Mock annotation.
        MockitoAnnotations.initMocks(this);
        setupPresenters();
    }

    @Test
    public void verifyViewEnabledStatesForEmptyState() {
        mPresenters[MODE_12HR].onCreate(NumberPadTimePickerState.EMPTY);
        verify(mViews[MODE_12HR]).setNumberKeysEnabled(1, 10);
        verify(mViews[MODE_12HR]).setBackspaceEnabled(false);
        verify(mViews[MODE_12HR]).setAmPmDisplayVisible(true);
        verify(mViews[MODE_12HR]).setAmPmDisplayIndex(mLocaleModel.isAmPmWrittenBeforeTime() ? 0 : 1);
        /* Verify setLeftAltKeyText() and setRightAltKeyText() manually. */
        verify(mViews[MODE_12HR]).setLeftAltKeyEnabled(false);
        verify(mViews[MODE_12HR]).setRightAltKeyEnabled(false);
        verify(mViews[MODE_12HR]).setHeaderDisplayFocused(true);

        mPresenters[MODE_24HR].onCreate(NumberPadTimePickerState.EMPTY);
        verify(mViews[MODE_24HR]).setNumberKeysEnabled(0, 10);
        verify(mViews[MODE_24HR]).setBackspaceEnabled(false);
        verify(mViews[MODE_24HR]).setAmPmDisplayVisible(false);
        /* Notice setAmPmDisplayIndex() is not called. */
        /* Verify setLeftAltKeyText() and setRightAltKeyText() manually. */
        verify(mViews[MODE_24HR]).setLeftAltKeyEnabled(false);
        verify(mViews[MODE_24HR]).setRightAltKeyEnabled(false);
        verify(mViews[MODE_24HR]).setHeaderDisplayFocused(true);
    }

    @Test
    public void clickOnNumberKey_UpdatesTimeDisplay() {
        mPresenters[MODE_12HR].onNumberKeyClick("1");
        verify(mViews[MODE_12HR]).updateTimeDisplay("1");
    }

    void setupPresenters() {
        initPresenter(MODE_12HR);
        initPresenter(MODE_24HR);
    }

    private void initPresenter(int mode) {
        if (mPresenters[mode] == null) {
            mPresenters[mode] = new NumberPadTimePickerPresenter(
                    mViews[mode], mLocaleModel, mode == MODE_24HR);
        }
    }
}
