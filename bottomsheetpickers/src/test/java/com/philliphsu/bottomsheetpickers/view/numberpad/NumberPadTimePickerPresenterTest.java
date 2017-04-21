package com.philliphsu.bottomsheetpickers.view.numberpad;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class NumberPadTimePickerPresenterTest {

    @Mock
    private INumberPadTimePicker.View mView;

    @Mock
    private LocaleModel mLocaleModel;

    private NumberPadTimePickerPresenter mPresenter;

    @Before
    public void setupPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        mPresenter = new NumberPadTimePickerPresenter(mView, mLocaleModel, false);
    }

    @Test
    public void clickOnNumberKey_UpdatesTimeDisplay() {
        mPresenter.onNumberKeyClick("1");
        Mockito.verify(mView).updateTimeDisplay("1");
    }
}
