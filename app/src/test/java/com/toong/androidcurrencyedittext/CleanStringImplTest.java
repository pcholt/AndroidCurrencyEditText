package com.toong.androidcurrencyedittext;

import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.burst.BurstJUnit4;
import com.toong.androidcurrencyedittext.behaviour.CursorBehaviourImpl;
import com.toong.androidcurrencyedittext.string.CleanString;
import com.toong.androidcurrencyedittext.string.CleanStringImpl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

import static org.junit.Assert.*;

@RunWith(BurstJUnit4.class)
public class CleanStringImplTest{

    @BeforeClass
    public static void setupLogging() {
        Timber.uprootAll();
        Timber.plant(new Timber.Tree() {
            @Override
            protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
                if (priority >= Log.DEBUG)
                    System.out.println(message);
            }
        });
    }

    @Test
    public void update_VALUES_EQUAL(TestCase testCase) {
        CleanString cleanString = getCleanString(testCase);
        cleanString.update(testCase.selectionStart, testCase.selectionEnd, testCase.inputText, testCase.changeText);
        assertEquals(testCase.outputText, cleanString.getDisplayText());
    }

    @NonNull
    private CleanStringImpl getCleanString(TestCase testCase) {
        return new CleanStringImpl(Locale.forLanguageTag(testCase.languageTag), new CursorBehaviourImpl());
    }

    @Test
    public void update_CARET_POS(TestCase testCase) {
        CleanString cleanString = getCleanString(testCase);
        cleanString.update(testCase.selectionStart, testCase.selectionEnd, testCase.inputText, testCase.changeText);
        assertEquals(testCase.specifiedOutput, TestCase.displayWithCursorPosition(cleanString.getDisplayText(), cleanString.getSelection()));
    }

    @Test
    public void update_INITIAL_DIGITS(TestCase testCase) {
        CleanString cleanString = getCleanString(testCase);
        if (cleanString.getDigitCountUntilSelection() >= 0 && testCase.expectedInitialDigitCount >= 0) {
            cleanString.update(testCase.selectionStart, testCase.selectionEnd, testCase.inputText, testCase.changeText);
            assertEquals(testCase.expectedInitialDigitCount, cleanString.getDigitCountUntilSelection());
        }
    }

    @Test
    @Ignore("Not needed, POSITION1 does an admirable job")
    public void update_POSITION2(TestCase testCase) {
        CleanString cleanString = getCleanString(testCase);
        cleanString.update(testCase.selectionStart, testCase.selectionEnd, testCase.inputText, testCase.changeText);
        assertEquals(testCase.newSelectionPosition, cleanString.getSelection());
    }

    enum TestCase {
                CASE1a("fr-FR", "1,^ €", "1,^00 €"),
        CASE1b("en-IE", "€1^", "€1^.00"),
        CASE1c("en-IE", "€1.^.00", "€1.^00"),
        CASE1d("en-IE", "€1.0^00", "€1.0^0"),
        CASE2a("en-GB", "12,921.13^", "£12,921.13^"),
        CASE3A("en-US", "$^.00", "$0^.00"),
        CASE3B("en-US", "$0.^.00", "$0.^00"),
        CASE3C1("en-US", "$^.00", "$0^.00", 0),
        CASE3C2("en-US", "$01^.00", "$1^.00", 1),
        CASE3D("en-US", "^$.00", "$0^.00"),
        CASE3E("en-US", "$2^00", "$2^.00", "."),
        CASE3F("fr-FR", "1^00 €", "1^,00 €", ","),
        CASE3G("en-US", "$01^.00", "$1^.00"),
        CASE4Ea("vi-VN", "1229^ đ", "1.229 đ^"),
        CASE4Eb("vi-VN", "1229^.345.678 đ", "1.229.^345.678 đ"),
        CASE4a("vi-VN", "1^229.345.678 đ", "1.^229.345.678 đ"),
        CASE4b("vi-VN", "1229^.345.678 đ", "1.229.^345.678 đ"),
        CASE4c("vi-VN", "1229^.345.678 đ", "1.229.^345.678 đ"),
        CASE4d("vi-VN", "1229^.345.678 đ", "1.229.^345.678 đ"),
        CASE4e("vi-VN", "122.345.3^678 đ", "1.223.453.^678 đ"),
        case5a("be-BY", "Руб0^", "Руб0^"),
        case5b("en-ZA", "^R 0.00", "R 0^.00"),
        ;

        final int selectionStart;
        final int selectionEnd;
        private int expectedInitialDigitCount = -1;
        public String specifiedOutput;
        public String specifiedInput;
        private String languageTag;
        final String inputText;
        final String outputText;
        final int newSelectionPosition;
        String changeText;

        TestCase(String languageTag, String inputText, String outputText, String changeText) {
            this(languageTag, inputText, outputText);
            this.changeText = changeText;
        }

        TestCase(String languageTag, String inputText, String outputText) {
            this.languageTag = languageTag;
            selectionStart = inputText.indexOf('^');
            selectionEnd = selectionStart;
            newSelectionPosition = outputText.indexOf('^');
            this.inputText = inputText.substring(0, selectionStart) + inputText.substring(selectionStart + 1);
            this.outputText = outputText.substring(0, newSelectionPosition) + outputText.substring(newSelectionPosition + 1);
            this.specifiedOutput = outputText;
            this.specifiedInput = inputText;
            changeText = "";
        }

        TestCase(String languageTag, String inputText, String outputText, int expectedInitialDigitCount) {
            this(languageTag, inputText, outputText);
            this.expectedInitialDigitCount = expectedInitialDigitCount;
        }

        @Override
        public String toString() {
            return this.name()+": "+specifiedInput + " -> " + specifiedOutput + ((Objects.equals(changeText, "")) ? "" : ", changeText=[" + changeText + "]");
        }

        public static String displayWithCursorPosition(String displayText, int selection) {
            return displayText.substring(0, selection) + "^" + displayText.substring(selection);
        }
    }
}