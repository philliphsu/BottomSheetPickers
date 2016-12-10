package com.philliphsu.bottomsheetpickers.date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewAnimator;

import com.philliphsu.bottomsheetpickers.R;

import static com.philliphsu.bottomsheetpickers.date.PagingDayPickerView.DAY_PICKER_INDEX;
import static com.philliphsu.bottomsheetpickers.date.PagingDayPickerView.MONTH_PICKER_INDEX;

/**
 * Parent of {@link PagingDayPickerView} and {@link MonthPickerView}.
 */
final class DayPickerViewAnimator extends ViewAnimator {

    /** Used when the PagingDayPickerView slides into view. */
    private final Animation mSlideUpInAnimation;

    /** Used when the PagingDayPickerView slides out of view. */
    private final Animation mSlideDownOutAnimation;

    /** Used when the MonthPickerView fades into view. */
    private final Animation mFadeInAnimation;

    /** Used when the MonthDayPickerView fades out of view. */
    private final Animation mFadeOutAnimation;

    public DayPickerViewAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSlideDownOutAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down);
        mSlideUpInAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        mFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        mFadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
    }

    @Override
    public void setDisplayedChild(int whichChild) {
        switch (whichChild) {
            case DAY_PICKER_INDEX:
                setInAnimation(mSlideUpInAnimation);
                setOutAnimation(mFadeOutAnimation);
                break;
            case MONTH_PICKER_INDEX:
                setInAnimation(mFadeInAnimation);
                setOutAnimation(mSlideDownOutAnimation);
                break;
        }
        super.setDisplayedChild(whichChild);
    }
}
