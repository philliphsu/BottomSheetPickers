package com.philliphsu.bottomsheetpickers.view.numberpad;

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
    }
    interface Presenter {
        void onNumberKeyClick(CharSequence numberKeyText);
        void onAltKeyClick(CharSequence altKeyText);
        void onBackspaceClick();
        boolean onBackspaceLongClick();
        void onShowTimePicker();
        // TODO: If we don't need the State interface, just change the return type to int[].
        State getState();
        // TODO: If we don't need the State interface, just change the parameter type to int[].
        void onRestoreInstanceState(State savedInstanceState);
    }
    // TODO: If we don't need the count, why do we need this interface?
    interface State {
        int[] getDigits();
        // TODO: Why do we need the count?
        int getCount();
        // TODO: If we rename the annotation to HalfDay, rename method to getHalfDay().
        @AmPmStates.AmPmState
        int getAmPmState();
    }
}