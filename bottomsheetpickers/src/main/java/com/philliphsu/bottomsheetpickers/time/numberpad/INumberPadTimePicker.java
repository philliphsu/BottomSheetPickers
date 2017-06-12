package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.support.annotation.NonNull;

interface INumberPadTimePicker {
    interface View {
        void setNumberKeysEnabled(int start, int end);
        void setBackspaceEnabled(boolean enabled);
        void updateTimeDisplay(CharSequence time);
        void updateAmPmDisplay(CharSequence ampm);
        void setAmPmDisplayVisible(boolean visible);
        void setAmPmDisplayIndex(int index);
        void setLeftAltKeyText(CharSequence text);
        void setRightAltKeyText(CharSequence text);
        void setLeftAltKeyEnabled(boolean enabled);
        void setRightAltKeyEnabled(boolean enabled);
        // TODO: The header is no longer "focusable". Delete this at some point.
        // This will prompt us to delete our tests that test the header display.
        @Deprecated void setHeaderDisplayFocused(boolean focused);
    }

    interface DialogView extends View {
        void setOkButtonEnabled(boolean enabled);
        void setResult(int hour, int minute);
        void cancel();
        void showOkButton();
    }

    interface Presenter {
        void onNumberKeyClick(CharSequence numberKeyText);
        void onAltKeyClick(CharSequence altKeyText);
        void onBackspaceClick();
        boolean onBackspaceLongClick();
        /**
         * @param state The state to initialize the time picker with.
         */
        void onCreate(@NonNull State state);
        void onStop();
        State getState();
    }

    interface DialogPresenter extends Presenter {
        void onCancelClick();
        void onOkButtonClick();
        void onDialogShow();
    }

    interface State {
        int[] getDigits();
        // TODO: Why do we need the count?
        int getCount();
        @AmPmState
        int getAmPmState();
    }
}