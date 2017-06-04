package com.philliphsu.bottomsheetpickers;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private static final String KEY_DARK_THEME = "dark_theme";
    private static final String KEY_THEME_SET_AT_RUNTIME = "theme_set_at_runtime";
    private static final String KEY_ACCENT_COLOR = "accent_color";
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_HEADER_COLOR = "header_color";
    private static final String KEY_HEADER_TEXT_DARK = "header_text_dark";

    // TODO: Remove duplicates in time picker classes.
    protected boolean mThemeDark;
    protected boolean mThemeSetAtRuntime;

    // TODO: Remove retrievals of these values in time picker classes.
    protected int mDarkGray;
    protected int mLightGray;
    protected int mWhite;
    protected int mWhiteTextDisabled;
    protected int mBlackText;
    protected int mBlackTextDisabled;

    protected int mAccentColor;
    protected int mBackgroundColor;
    protected int mHeaderColor;
    protected boolean mHeaderTextDark;

    @LayoutRes
    protected abstract int contentLayout();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mThemeDark = savedInstanceState.getBoolean(KEY_DARK_THEME);
            mThemeSetAtRuntime = savedInstanceState.getBoolean(KEY_THEME_SET_AT_RUNTIME);
            mAccentColor = savedInstanceState.getInt(KEY_ACCENT_COLOR);
            mBackgroundColor = savedInstanceState.getInt(KEY_BACKGROUND_COLOR);
            mHeaderColor = savedInstanceState.getInt(KEY_HEADER_COLOR);
            mHeaderTextDark = savedInstanceState.getBoolean(KEY_HEADER_TEXT_DARK);
        }
        // Prepare common colors.
        final Context ctx = getActivity();
        mDarkGray = getColor(ctx, R.color.bsp_dark_gray);
        mLightGray = getColor(ctx, R.color.bsp_light_gray);
        mWhite = getColor(ctx, android.R.color.white);
        mWhiteTextDisabled = getColor(ctx, R.color.bsp_text_color_disabled_dark);
        mBlackText = getColor(ctx, R.color.bsp_text_color_primary_light);
        mBlackTextDisabled = getColor(ctx, R.color.bsp_text_color_disabled_light);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!mThemeSetAtRuntime) {
            mThemeDark = Utils.isDarkTheme(getActivity(), mThemeDark);
        }
        if (mAccentColor == 0) {
            mAccentColor = Utils.getThemeAccentColor(getActivity());
        }
        if (mBackgroundColor == 0) {
            mBackgroundColor = mThemeDark ? mDarkGray : mWhite;
        }
        if (mHeaderColor == 0) {
            mHeaderColor = mThemeDark ? mLightGray : mAccentColor;
        }

        if (contentLayout() != 0) {
            View view = inflater.inflate(contentLayout(), container, false);
            // Set background color of entire view
            // TODO: Remove setting of this in subclasses.
            view.setBackgroundColor(mBackgroundColor);
            return view;
        }

        return null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // TODO: Pass in a dark/light BottomSheetDialog theme depending on the theme set.
        // Verify this changes colors for text, selectableBackground, etc. appropriately.
        return new CustomWidthBottomSheetDialog(getContext(), R.style.BSP_BottomSheetDialogTheme);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_DARK_THEME, mThemeDark);
        outState.putBoolean(KEY_THEME_SET_AT_RUNTIME, mThemeSetAtRuntime);
        outState.putInt(KEY_ACCENT_COLOR, mAccentColor);
        outState.putInt(KEY_BACKGROUND_COLOR, mBackgroundColor);
        outState.putInt(KEY_HEADER_COLOR, mHeaderColor);
        outState.putBoolean(KEY_HEADER_TEXT_DARK, mHeaderTextDark);
    }

    /**
     * Set a dark or light theme. NOTE: this will only take effect for the next onCreateView.
     */
    public void setThemeDark(boolean dark) {
        mThemeDark = dark;
        mThemeSetAtRuntime = true;
    }

    public boolean isThemeDark() {
        return mThemeDark;
    }

    /**
     * Set the accent color. This color is primarily used to tint views in the picker.
     * If this picker is using the light theme and you did not call {@link #setHeaderColor(int)},
     * this color will also be applied to the dialog's header.
     */
    public final void setAccentColor(@ColorInt int color) {
        mAccentColor = color;
    }

    /**
     * Set the background color. If this color is dark, consider
     * setting the theme dark to ensure text in the picker has enough contrast.
     */
    public final void setBackgroundColor(@ColorInt int color) {
        mBackgroundColor = color;
    }

    /**
     * Set the header color. If this color is light, consider
     * setting the header text dark to ensure it has enough contrast.
     * <p>
     * If this picker is using the light theme, this will normally be your Activity's
     * {@code colorAccent} or the accent color set with {@link #setAccentColor(int)}.
     * </p>
     */
    public final void setHeaderColor(@ColorInt int color) {
        mHeaderColor = color;
    }

    /**
     * Set the header text to use a light or dark color.
     * The default is false, so a light color is applied.
     */
    public final void setHeaderTextDark(boolean dark) {
        mHeaderTextDark = dark;
    }

    /**
     * @return The default color for header texts when they are in the selected state, and when
     *         no custom color has been set. Not all pickers support selectable/toggleable
     *         header texts, so you should only call this for those that do.
     */
    protected final int getDefaultHeaderTextColorSelected() {
        return mHeaderTextDark ? mBlackText : mWhite;
    }

    /**
     * @return The default color for header texts when they are in the unselected state, and
     *         when no custom color has been set. Not all pickers support selectable/toggleable
     *         header texts, so you should only call this for those that do.
     */
    protected final int getDefaultHeaderTextColorUnselected() {
        return mHeaderTextDark ? mBlackTextDisabled : mWhiteTextDisabled;
    }
    
    public static abstract class Builder {
        private int mAccentColor;
        private int mBackgroundColor;
        private int mHeaderColor;
        private boolean mHeaderTextDark;
        private boolean mThemeDark;
        private boolean mThemeSetAtRuntime;

        /**
         * Set the accent color. This color is primarily used to tint views in the picker.
         * If this picker is using the light theme and you did not call {@link #setHeaderColor(int)},
         * this color will also be applied to the dialog's header.
         */
        public Builder setAccentColor(int accentColor) {
            mAccentColor = accentColor;
            return this;
        }

        /**
         * Set the background color. If this color is dark, consider
         * setting the theme dark to ensure text in the picker has enough contrast.
         */
        public Builder setBackgroundColor(int backgroundColor) {
            mBackgroundColor = backgroundColor;
            return this;
        }

        /**
         * Set the header color. If this color is light, consider
         * setting the header text dark to ensure it has enough contrast.
         * <p>
         * If this picker is using the light theme, this will normally be your Activity's
         * {@code colorAccent} or the accent color set with {@link #setAccentColor(int)}.
         * </p>
         */
        public Builder setHeaderColor(int headerColor) {
            mHeaderColor = headerColor;
            return this;
        }

        /**
         * Set the header text to use a light or dark color.
         * The default is false, so a light color is applied.
         */
        public Builder setHeaderTextDark(boolean headerTextDark) {
            mHeaderTextDark = headerTextDark;
            return this;
        }

        /**
         * Set a dark or light theme.
         */
        public Builder setThemeDark(boolean themeDark) {
            mThemeDark = themeDark;
            mThemeSetAtRuntime = true;
            return this;
        }

        /**
         * Hacky workaround to mimic a {@code super.build()} call, which would otherwise be 
         * impossible to make because we can't possibly know what explicit type of 
         * {@code BottomSheetPickerDialog} should be instantiated.
         * 
         * @param dialog A dialog already built by one of our subclasses.
         */
        protected void super_build(@NonNull BottomSheetPickerDialog dialog) {
            dialog.setAccentColor(mAccentColor);
            dialog.setBackgroundColor(mBackgroundColor);
            dialog.setHeaderColor(mHeaderColor);
            dialog.setHeaderTextDark(mHeaderTextDark);
            if (mThemeSetAtRuntime) {
                dialog.setThemeDark(mThemeDark);
            }
        }
        
        public abstract BottomSheetPickerDialog build();
    }
}
