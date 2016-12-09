package com.philliphsu.bottomsheetpickers.date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.ViewAnimator;

import static com.philliphsu.bottomsheetpickers.date.PagingDayPickerView.DAY_PICKER_INDEX;
import static com.philliphsu.bottomsheetpickers.date.PagingDayPickerView.MONTH_PICKER_INDEX;

/**
 * Parent of {@link PagingDayPickerView} and {@link MonthPickerView}.
 */
final class DayPickerViewAnimator extends ViewAnimator {

    private final Animation mInAnimation;
    private final Animation mOutAnimation;

    public DayPickerViewAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInAnimation = getInAnimation();
        mOutAnimation = getOutAnimation();
    }

    @Override
    public void setDisplayedChild(int whichChild) {
        if (mInAnimation != null && mOutAnimation != null) {
            switch (whichChild) {
                case DAY_PICKER_INDEX:
                    setInAnimation(mInAnimation);
                    setOutAnimation(null);
                    break;
                case MONTH_PICKER_INDEX:
                    setInAnimation(null);
                    setOutAnimation(mOutAnimation);
                    break;
            }
        }
        super.setDisplayedChild(whichChild);
    }
}
