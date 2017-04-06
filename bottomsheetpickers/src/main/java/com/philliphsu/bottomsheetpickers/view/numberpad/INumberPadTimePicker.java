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
    }
    interface Presenter {
        void onNumberKeyClick(CharSequence numberKeyText);
        void onBackspaceClick();
        void onShowTimePicker();
    }
}