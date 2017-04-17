package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.support.annotation.NonNull;

interface INumberPadTimePicker {
    interface View {
        void setNumberKeysEnabled(int start, int end);
        void setBackspaceEnabled(boolean enabled);
        void updateTimeDisplay(CharSequence time);
        void updateAmPmDisplay(CharSequence ampm);
        void setOkButtonEnabled(boolean enabled);
        void setAmPmDisplayVisible(boolean visible);
        void setAmPmDisplayIndex(int index);
        void setLeftAltKeyText(CharSequence text);
        void setRightAltKeyText(CharSequence text);
        void setLeftAltKeyEnabled(boolean enabled);
        void setRightAltKeyEnabled(boolean enabled);
        void setHeaderDisplayFocused(boolean focused);
        void setResult(int hour, int minute);
        void cancel();
    }

    interface Presenter {
        void onNumberKeyClick(CharSequence numberKeyText);
        void onAltKeyClick(CharSequence altKeyText);
        void onBackspaceClick();
        boolean onBackspaceLongClick();
        void onCancelClick();
        void onOkButtonClick();
        /**
         * @param state The state to initialize the time picker with.
         */
        void onCreate(@NonNull State state);
        void onStop();
        State getState();
    }

    interface State {
        int[] getDigits();
        // TODO: Why do we need the count?
        int getCount();
        // TODO: If we rename the annotation to HalfDay, rename method to getHalfDay().
        @AmPmStates.AmPmState
        int getAmPmState();
    }
}