package com.philliphsu.bottomsheetpickers.view.numberpad;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class NumberPadTimePickerPresenterTest {

    @Mock
    private INumberPadTimePicker.View mView;

    @Mock
    private LocaleModel mLocaleModel;

    private NumberPadTimePickerPresenter mPresenter;
    private boolean mIs24HourMode;

    @Before
    public void setupPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mIs24HourMode = false;
        mPresenter = new NumberPadTimePickerPresenter(mView, mLocaleModel, mIs24HourMode);
    }

    @Test
    public void verifyViewEnabledStatesForEmptyState() {
        mPresenter.onCreate(NumberPadTimePickerState.EMPTY);
        verify(mView).setNumberKeysEnabled(mIs24HourMode ? 0 : 1, 10);
        verify(mView).setBackspaceEnabled(false);
        verify(mView).setAmPmDisplayVisible(!mIs24HourMode);
        if (!mIs24HourMode) {
            verify(mView).setAmPmDisplayIndex(mLocaleModel.isAmPmWrittenBeforeTime() ? 0 : 1);
        }
        /* Verify setLeftAltKeyText() and setRightAltKeyText() manually. */
        verify(mView).setLeftAltKeyEnabled(false);
        verify(mView).setRightAltKeyEnabled(false);
        verify(mView).setHeaderDisplayFocused(true);
    }

    @Test
    public void clickOnNumberKey_UpdatesTimeDisplay() {
        mPresenter.onNumberKeyClick("1");
        verify(mView).updateTimeDisplay("1");
    }
}
