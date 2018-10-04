package com.toong.androidcurrencyedittext.behaviour;

public interface CursorBehaviour {
    int digitCount(String string, int selectionEnd);

    int cursorPositionAfterCount(int digitCountUntilSelection, String displayText);

    void setDecimalSeparator(char decimalSeparator);
}
