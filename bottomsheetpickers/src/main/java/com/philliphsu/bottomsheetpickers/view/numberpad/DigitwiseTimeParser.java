package com.philliphsu.bottomsheetpickers.view.numberpad;

/**
 * Helper class that parses the hour and minute from {@link DigitwiseTimeModel}.
 */
final class DigitwiseTimeParser {
    private final DigitwiseTimeModel model;

    public DigitwiseTimeParser(DigitwiseTimeModel model) {
        this.model = model;
    }

    int getHour() {
        // TODO: Not a comprehensive implementation!
        return model.getDigit(0) * 10 + model.getDigit(1);
    }

    int getMinute() {
        // TODO: Not a comprehensive implementation!
        return model.getDigit(2) * 10 + model.getDigit(3);
    }
}
