package com.philliphsu.bottomsheetpickers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.support.v4.content.ContextCompat.getColor;

/**
 * Created by Phillip Hsu on 10/4/2016.
 */
// TODO: Consider renaming this to BasePickerDialog(Fragment?) and extend from AppCompatDialogFragment.
public abstract class BottomSheetPickerDialog extends BottomSheetDialogFragment {

    // TODO: Other classes in this package may need these? If not, make private.
    protected static final String KEY_DARK_THEME = "dark_theme";
    protected static final String KEY_THEME_SET_AT_RUNTIME = "theme_set_at_runtime";

    // TODO: Other classes in this package may need this? If not, make private.
    // TODO: Remove duplicates in time picker classes.
    protected boolean mThemeDark;
    protected boolean mThemeSetAtRuntime;

    // TODO: Remove retrievals of these values in time picker classes.
    protected int mDarkGray;
    protected int mLightGray;
    protected int mAccentColor;

    @LayoutRes
    protected abstract int contentLayout();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!mThemeSetAtRuntime) {
            mThemeDark = Utils.isDarkTheme(getActivity(), mThemeDark);
        }

        final Context ctx = getActivity();
        mDarkGray = getColor(ctx, R.color.dark_gray);
        mLightGray = getColor(ctx, R.color.light_gray);
        mAccentColor = Utils.getThemeAccentColor(getActivity());

        return inflater.inflate(contentLayout(), container, false);
    }

    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
    }

    // TODO: Time picker subclasses need to call up to super!
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_DARK_THEME, mThemeDark);
        outState.putBoolean(KEY_THEME_SET_AT_RUNTIME, mThemeSetAtRuntime);
    }

    // TODO: Delete time picker subclasses' implementation.
    /**
     * Set a dark or light theme. NOTE: this will only take effect for the next onCreateView.
     */
    public void setThemeDark(boolean dark) {
        mThemeDark = dark;
        mThemeSetAtRuntime = true;
    }

    // TODO: Delete time picker subclasses' implementation.
    public boolean isThemeDark() {
        return mThemeDark;
    }
}
