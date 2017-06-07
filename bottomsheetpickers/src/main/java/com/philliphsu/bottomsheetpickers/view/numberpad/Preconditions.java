package com.philliphsu.bottomsheetpickers.view.numberpad;

final class Preconditions {
    private Preconditions() {}

    static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }
}
