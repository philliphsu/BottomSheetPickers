package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// TODO: Declare an attribute with format="reference" to allow a style resource to be specified.

// TODO: Animate FAB elevation in BottomSheet subtype.

// TODO: If you backspace repeatedly quick enough, you see that the FAB color animation plays
// over and over again. E.g. enter a valid time then double tap on the backspace. This is happening
// because you are waiting until the animation ends before setting the FAB's enabled state.
// You can try (1) immediately setting the enabled state or (2) do some trickery to prevent the
// animation from playing if it is already playing or (3) decrease the animation duration.
// The problem with (1) could be the elevation shadow would immediately show up as well;
// however, this could be mitigated when we also animate the elevation property--or would the
// elevation be immediately set to 100% as a result and then the animation's calculations are
// thrown off? The problem with (2) is it's going to require significant effort. Lastly, the problem
// with (3) is people can no doubt successively click the backspace button faster than any reasonably
// small duration for the animation, so the animation will end up being so short that it may as well
// not be an animation.
class NumberPadTimePicker extends LinearLayout implements INumberPadTimePicker.View {
    /** Option to layout this view for use in an alert dialog. */
    static final int LAYOUT_ALERT = 1;
    /** Option to layout this view for use in a bottom sheet dialog. */
    static final int LAYOUT_BOTTOM_SHEET = 2;

    @IntDef({LAYOUT_ALERT, LAYOUT_BOTTOM_SHEET})
    @Retention(RetentionPolicy.SOURCE)
    @interface NumberPadTimePickerLayout {}

    private NumberPadView mNumberPad;
    private LinearLayout mInputTimeContainer;
    private TextView mTimeDisplay;
    private TextView mAmPmDisplay;
    private ImageButton mBackspace;

    private NumberPadTimePickerBaseComponent mTimePickerComponent;

    private @NumberPadTimePickerLayout int mLayout;

    public NumberPadTimePicker(Context context) {
        this(context, null);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /*TODO: Pass our attribute here. This contains a reference to a style resource.*/);
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

    // TODO: Apply the style resource, either the one contained in defStyleAttr or defStyleRes itself.
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

        mNumberPad = (NumberPadView) findViewById(R.id.bsp_numberpad_time_picker_view);
        mInputTimeContainer = (LinearLayout) findViewById(R.id.bsp_input_time_container);
        mTimeDisplay = (TextView) findViewById(R.id.bsp_input_time);
        mAmPmDisplay = (TextView) findViewById(R.id.bsp_input_ampm);
        mBackspace = (ImageButton) findViewById(R.id.bsp_backspace);
    }

    @Override
    public void setNumberKeysEnabled(int start, int end) {
        mNumberPad.setNumberKeysEnabled(start, end);
    }

    @Override
    public void setBackspaceEnabled(boolean enabled) {
        mBackspace.setEnabled(enabled);
    }

    @Override
    public void updateTimeDisplay(CharSequence time) {
        mTimeDisplay.setText(time);
    }

    @Override
    public void updateAmPmDisplay(CharSequence ampm) {
        mAmPmDisplay.setText(ampm);
    }

    @Override
    public void setAmPmDisplayVisible(boolean visible) {
        mAmPmDisplay.setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    public void setAmPmDisplayIndex(int index) {
        if (index != 0 && index != 1) {
            throw new IllegalArgumentException("Index of AM/PM display must be 0 or 1. index == " + index);
        }
        if (index == 1) return;
        mInputTimeContainer.removeViewAt(1);
        mInputTimeContainer.addView(mAmPmDisplay, 0);
    }

    @Override
    public void setLeftAltKeyText(CharSequence text) {
        mNumberPad.setLeftAltKeyText(text);
    }

    @Override
    public void setRightAltKeyText(CharSequence text) {
        mNumberPad.setRightAltKeyText(text);
    }

    @Override
    public void setLeftAltKeyEnabled(boolean enabled) {
        mNumberPad.setLeftAltKeyEnabled(enabled);
    }

    @Override
    public void setRightAltKeyEnabled(boolean enabled) {
        mNumberPad.setRightAltKeyEnabled(enabled);
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
        mBackspace.setOnClickListener(l);
    }

    void setOnBackspaceLongClickListener(OnLongClickListener l) {
        mBackspace.setOnLongClickListener(l);
    }

    void setOnNumberKeyClickListener(OnClickListener l) {
        mNumberPad.setOnNumberKeyClickListener(l);
    }

    void setOnAltKeyClickListener(OnClickListener l) {
        mNumberPad.setOnAltKeyClickListener(l);
    }

    @Nullable
    View getOkButton() {
        checkComponentIsBottomSheet(mTimePickerComponent);
        return ((NumberPadTimePickerBottomSheetComponent) mTimePickerComponent).getOkButton();
    }

    boolean isAnimateFabIn() {
        checkComponentIsBottomSheet(mTimePickerComponent);
        return ((NumberPadTimePickerBottomSheetComponent) mTimePickerComponent).isAnimateFabIn();
    }

    @NumberPadTimePickerBottomSheetComponent.ShowFabPolicy
    int getShowFabPolicy() {
        checkComponentIsBottomSheet(mTimePickerComponent);
        return ((NumberPadTimePickerBottomSheetComponent) mTimePickerComponent).getShowFabPolicy();
    }

    boolean isAnimateFabBackgroundColor() {
        checkComponentIsBottomSheet(mTimePickerComponent);
        return ((NumberPadTimePickerBottomSheetComponent) mTimePickerComponent).isAnimateFabBackgroundColor();
    }

    void setOkButtonEnabled(boolean enabled) {
        checkComponentIsBottomSheet(mTimePickerComponent);
        ((NumberPadTimePickerBottomSheetComponent) mTimePickerComponent).setOkButtonEnabled(enabled);
    }

    private static void checkComponentIsBottomSheet(NumberPadTimePickerBaseComponent component) {
        if (!(component instanceof NumberPadTimePickerBottomSheetComponent)) {
            throw new UnsupportedOperationException("Method can only be called for LAYOUT_BOTTOM_SHEET");
        }
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
    abstract static class NumberPadTimePickerBaseComponent {
        NumberPadTimePickerBaseComponent(NumberPadTimePicker timePicker, Context context,
                AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            final View root = inflate(context, timePicker);
            final NumberPadView numberPad = (NumberPadView) root.findViewById(R.id.bsp_numberpad_time_picker_view);
            final TextView timeDisplay = (TextView) root.findViewById(R.id.bsp_input_time);
            final TextView amPmDisplay = (TextView) root.findViewById(R.id.bsp_input_ampm);
            final ImageButton backspace = (ImageButton) root.findViewById(R.id.bsp_backspace);

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
                timeDisplay.setTextColor(inputTimeTextColor);
            }
            if (inputAmPmTextColor != 0) {
                amPmDisplay.setTextColor(inputAmPmTextColor);
            }
            if (backspaceTint != null) {
                DrawableCompat.setTintList(backspace.getDrawable(), backspaceTint);
            }
            if (numberKeysTextColor != null) {
                numberPad.setNumberKeysTextColor(numberKeysTextColor);
            }
            if (altKeysTextColor != null) {
                numberPad.setAltKeysTextColor(altKeysTextColor);
            }
            if (headerBackground != null) {
                setBackground(root.findViewById(R.id.bsp_header), headerBackground);
            }
            if (divider != null) {
                setBackground(root.findViewById(R.id.bsp_divider), divider);
            }
            if (numberPadBackground != null) {
                setBackground(numberPad, numberPadBackground);
            }
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
