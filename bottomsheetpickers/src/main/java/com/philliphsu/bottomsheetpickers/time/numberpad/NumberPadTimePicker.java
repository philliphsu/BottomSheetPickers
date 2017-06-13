package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NumberPadTimePicker extends LinearLayout implements INumberPadTimePicker.View {

    /** Option to layout this view for use in an alert dialog. */
    static final int LAYOUT_ALERT = 1;
    /** Option to layout this view for use in a bottom sheet dialog. */
    static final int LAYOUT_BOTTOM_SHEET = 2;

    @IntDef({LAYOUT_ALERT, LAYOUT_BOTTOM_SHEET})
    @Retention(RetentionPolicy.SOURCE)
    @interface NumberPadTimePickerLayout {}

    private NumberPadTimePickerComponent mTimePickerComponent;
    private LinearLayout mInputTimeContainer;

    private @NumberPadTimePickerLayout int mLayout;

    public NumberPadTimePicker(Context context) {
        this(context, null);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.bsp_numberPadTimePickerStyle);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public NumberPadTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setOrientation(VERTICAL);

        final TypedArray timePickerAttrs = context.obtainStyledAttributes(attrs,
                R.styleable.BSP_NumberPadTimePicker, defStyleAttr, defStyleRes);
        mLayout = retrieveLayout(timePickerAttrs);
        timePickerAttrs.recycle();

        switch (mLayout) {
            case LAYOUT_BOTTOM_SHEET:
                mTimePickerComponent = new NumberPadTimePickerBottomSheetComponent(
                        this, context, attrs, defStyleAttr, defStyleRes);
                break;
            case LAYOUT_ALERT:
            default:
                mTimePickerComponent = new NumberPadTimePickerAlertComponent(
                        this, context, attrs, defStyleAttr, defStyleRes);
                break;
        }

        mInputTimeContainer = (LinearLayout) findViewById(R.id.bsp_input_time_container);
    }

    @Override
    public void setNumberKeysEnabled(int start, int end) {
        mTimePickerComponent.mNumberPad.setNumberKeysEnabled(start, end);
    }

    @Override
    public void setBackspaceEnabled(boolean enabled) {
        mTimePickerComponent.mBackspace.setEnabled(enabled);
    }

    @Override
    public void updateTimeDisplay(CharSequence time) {
        mTimePickerComponent.mTimeDisplay.setText(time);
    }

    @Override
    public void updateAmPmDisplay(CharSequence ampm) {
        mTimePickerComponent.mAmPmDisplay.setText(ampm);
    }

    @Override
    public void setAmPmDisplayVisible(boolean visible) {
        mTimePickerComponent.mAmPmDisplay.setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    public void setAmPmDisplayIndex(int index) {
        if (index != 0 && index != 1) {
            throw new IllegalArgumentException("Index of AM/PM display must be 0 or 1. index == " + index);
        }
        if (index == 1) return;
        mInputTimeContainer.removeViewAt(1);
        mInputTimeContainer.addView(mTimePickerComponent.mAmPmDisplay, 0);
    }

    @Override
    public void setLeftAltKeyText(CharSequence text) {
        mTimePickerComponent.mNumberPad.setLeftAltKeyText(text);
    }

    @Override
    public void setRightAltKeyText(CharSequence text) {
        mTimePickerComponent.mNumberPad.setRightAltKeyText(text);
    }

    @Override
    public void setLeftAltKeyEnabled(boolean enabled) {
        mTimePickerComponent.mNumberPad.setLeftAltKeyEnabled(enabled);
    }

    @Override
    public void setRightAltKeyEnabled(boolean enabled) {
        mTimePickerComponent.mNumberPad.setRightAltKeyEnabled(enabled);
    }

    @Deprecated
    @Override
    public void setHeaderDisplayFocused(boolean focused) {
        // Do nothing.
    }

    @NumberPadTimePickerLayout
    int getLayout() {
        return mLayout;
    }

    void setOnBackspaceClickListener(OnClickListener l) {
        mTimePickerComponent.mBackspace.setOnClickListener(l);
    }

    void setOnBackspaceLongClickListener(OnLongClickListener l) {
        mTimePickerComponent.mBackspace.setOnLongClickListener(l);
    }

    void setOnNumberKeyClickListener(OnClickListener l) {
        mTimePickerComponent.mNumberPad.setOnNumberKeyClickListener(l);
    }

    void setOnAltKeyClickListener(OnClickListener l) {
        mTimePickerComponent.mNumberPad.setOnAltKeyClickListener(l);
    }

    NumberPadTimePickerComponent getComponent() {
        return mTimePickerComponent;
    }

    @NumberPadTimePickerLayout
    private static int retrieveLayout(TypedArray timePickerAttrs) {
        final int layout = timePickerAttrs.getInt(
                R.styleable.BSP_NumberPadTimePicker_bsp_numberPadTimePickerLayout, LAYOUT_ALERT);
        switch (layout) {
            case LAYOUT_ALERT:
            case LAYOUT_BOTTOM_SHEET:
                return layout;
            default:
                return LAYOUT_ALERT;
        }
    }

    /**
     * Component that installs the base functionality of a {@link NumberPadTimePicker}. 
     */
    abstract static class NumberPadTimePickerComponent implements NumberPadTimePickerThemer {
        final NumberPadView mNumberPad;
        final TextView mTimeDisplay;
        final TextView mAmPmDisplay;
        final ImageButton mBackspace;
        final ImageView mDivider;
        final View mHeader;

        NumberPadTimePickerComponent(NumberPadTimePicker timePicker, Context context,
                AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            final View root = inflate(context, timePicker);
            mNumberPad = (NumberPadView) root.findViewById(R.id.bsp_numberpad_time_picker_view);
            mTimeDisplay = (TextView) root.findViewById(R.id.bsp_input_time);
            mAmPmDisplay = (TextView) root.findViewById(R.id.bsp_input_ampm);
            mBackspace = (ImageButton) root.findViewById(R.id.bsp_backspace);
            mDivider = (ImageView) root.findViewById(R.id.bsp_divider);
            mHeader = root.findViewById(R.id.bsp_header);

            final TypedArray timePickerAttrs = context.obtainStyledAttributes(attrs,
                    R.styleable.BSP_NumberPadTimePicker, defStyleAttr, defStyleRes);
            final int inputTimeTextColor = timePickerAttrs.getColor(
                    R.styleable.BSP_NumberPadTimePicker_bsp_inputTimeTextColor, 0);
            final int inputAmPmTextColor = timePickerAttrs.getColor(
                    R.styleable.BSP_NumberPadTimePicker_bsp_inputAmPmTextColor, 0);
            final ColorStateList backspaceTint = timePickerAttrs.getColorStateList(
                    R.styleable.BSP_NumberPadTimePicker_bsp_backspaceTint);
            final ColorStateList numberKeysTextColor = timePickerAttrs.getColorStateList(
                    R.styleable.BSP_NumberPadTimePicker_bsp_numberKeysTextColor);
            final ColorStateList altKeysTextColor = timePickerAttrs.getColorStateList(
                    R.styleable.BSP_NumberPadTimePicker_bsp_altKeysTextColor);
            final Drawable headerBackground = timePickerAttrs.getDrawable(
                    R.styleable.BSP_NumberPadTimePicker_bsp_headerBackground);
            final Drawable divider = timePickerAttrs.getDrawable(
                    R.styleable.BSP_NumberPadTimePicker_bsp_divider);
            final Drawable numberPadBackground = timePickerAttrs.getDrawable(
                    R.styleable.BSP_NumberPadTimePicker_bsp_numberPadBackground);
            timePickerAttrs.recycle();

            if (inputTimeTextColor != 0) {
                setInputTimeTextColor(inputTimeTextColor);
            }
            if (inputAmPmTextColor != 0) {
                setInputAmPmTextColor(inputAmPmTextColor);
            }
            if (backspaceTint != null) {
                setBackspaceTint(backspaceTint);
            }
            if (numberKeysTextColor != null) {
                setNumberKeysTextColor(numberKeysTextColor);
            }
            if (altKeysTextColor != null) {
                setAltKeysTextColor(altKeysTextColor);
            }
            if (headerBackground != null) {
                setHeaderBackground(headerBackground);
            }
            if (divider != null) {
                setDivider(divider);
            }
            if (numberPadBackground != null) {
                setNumberPadBackground(numberPadBackground);
            }
        }

        @Override
        public final NumberPadTimePickerThemer setInputTimeTextColor(@ColorInt int color) {
            mTimeDisplay.setTextColor(color);
            return this;
        }

        @Override
        public final NumberPadTimePickerThemer setInputAmPmTextColor(@ColorInt int color) {
            mAmPmDisplay.setTextColor(color);
            return this;
        }

        @Override
        public final NumberPadTimePickerThemer setBackspaceTint(ColorStateList colors) {
            DrawableCompat.setTintList(mBackspace.getDrawable(), colors);
            return this;
        }

        @Override
        public final NumberPadTimePickerThemer setNumberKeysTextColor(ColorStateList colors) {
            mNumberPad.setNumberKeysTextColor(colors);
            return this;
        }

        @Override
        public final NumberPadTimePickerThemer setAltKeysTextColor(ColorStateList colors) {
            mNumberPad.setAltKeysTextColor(colors);
            return this;
        }

        @Override
        public final NumberPadTimePickerThemer setHeaderBackground(Drawable background) {
            setBackground(mHeader, background);
            return this;
        }

        @Override
        public final NumberPadTimePickerThemer setNumberPadBackground(Drawable background) {
            setBackground(mNumberPad, background);
            return this;
        }

        @Override
        public final NumberPadTimePickerThemer setDivider(Drawable divider) {
            mDivider.setImageDrawable(divider);
            if (Build.VERSION.SDK_INT >= 21) {
                // Clear the tint set in the header's layout resource.
                // This is not necessary for pre-21, because the tint
                // doesn't show up when the divider is changed.
                mDivider.setImageTintList(null);
            }
            return this;
        }

        abstract View inflate(Context context, NumberPadTimePicker root);

        private static void setBackground(View view, Drawable background) {
            if (Build.VERSION.SDK_INT < 16) {
                view.setBackgroundDrawable(background);
            } else {
                view.setBackground(background);
            }
        }
    }
}
