package com.philliphsu.bottomsheetpickers.view.numberpad;

final class TestCase {
    final int[] sequence;
    final @AmPmState int ampmState;

    final int numberKeysEnabledStart;
    final int numberKeysEnabledEnd;
    final boolean backspaceEnabled;
    final boolean headerDisplayFocused;
    final boolean leftAltKeyEnabled;
    final boolean rightAltKeyEnabled;
    final boolean okButtonEnabled;
    final CharSequence timeDisplay;
    final CharSequence ampmDisplay;

    public TestCase(int[] sequence, int ampmState, int numberKeysEnabledStart, int numberKeysEnabledEnd, boolean backspaceEnabled, boolean headerDisplayFocused, boolean leftAltKeyEnabled, boolean rightAltKeyEnabled, boolean okButtonEnabled, CharSequence timeDisplay, CharSequence ampmDisplay) {
        this.sequence = sequence;
        this.ampmState = ampmState;
        this.numberKeysEnabledStart = numberKeysEnabledStart;
        this.numberKeysEnabledEnd = numberKeysEnabledEnd;
        this.backspaceEnabled = backspaceEnabled;
        this.headerDisplayFocused = headerDisplayFocused;
        this.leftAltKeyEnabled = leftAltKeyEnabled;
        this.rightAltKeyEnabled = rightAltKeyEnabled;
        this.okButtonEnabled = okButtonEnabled;
        this.timeDisplay = timeDisplay;
        this.ampmDisplay = ampmDisplay;
    }

    static class Builder {
        private final int[] sequence;
        private final @AmPmState int ampmState;

        private int numberKeysEnabledStart;
        private int numberKeysEnabledEnd;
        private boolean backspaceEnabled;
        private boolean headerDisplayFocused;
        private boolean leftAltKeyEnabled;
        private boolean rightAltKeyEnabled;
        private boolean okButtonEnabled;
        private CharSequence timeDisplay;
        private CharSequence ampmDisplay;

        public Builder(int[] sequence, @AmPmState int ampmState) {
            this.sequence = sequence;
            this.ampmState = ampmState;
        }

        public Builder numberKeysEnabled(int numberKeysEnabledStart, int numberKeysEnabledEnd) {
            this.numberKeysEnabledStart = numberKeysEnabledStart;
            this.numberKeysEnabledEnd = numberKeysEnabledEnd;
            return this;
		}

        public Builder backspaceEnabled(boolean backspaceEnabled) {
            this.backspaceEnabled = backspaceEnabled;
            return this;
        }

        public Builder altKeysEnabled(boolean enabled) {
            leftAltKeyEnabled = rightAltKeyEnabled = enabled;
            return this;
		}

        public Builder headerDisplayFocused(boolean headerDisplayFocused) {
            this.headerDisplayFocused = headerDisplayFocused;
            return this;
		}

        public Builder timeDisplay(CharSequence timeDisplay) {
            this.timeDisplay = timeDisplay;
            return this;
		}

        public Builder ampmDisplay(CharSequence ampmDisplay) {
            this.ampmDisplay = ampmDisplay;
            return this;
		}

        public Builder okButtonEnabled(boolean okButtonEnabled) {
            this.okButtonEnabled = okButtonEnabled;
            return this;
        }

        public TestCase build() {
            return new TestCase(sequence, ampmState, numberKeysEnabledStart, numberKeysEnabledEnd,
                    backspaceEnabled, headerDisplayFocused, leftAltKeyEnabled,
                    rightAltKeyEnabled, okButtonEnabled, timeDisplay, ampmDisplay);
        }
    }
}
