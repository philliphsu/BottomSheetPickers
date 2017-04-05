package com.philliphsu.bottomsheetpickers.view;

public final class Preconditions {
    private Preconditions() {}

    public static <T> T checkNotNull(T t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return t;
    }
}
