package com.toong.androidcurrencyedittext.behaviour;

import android.util.Log;

import com.squareup.burst.BurstJUnit4;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.MessageFormat;

import timber.log.Timber;

import static org.junit.Assert.assertEquals;

@RunWith(BurstJUnit4.class)
public class CursorBehaviourImplTest {

    private CursorBehaviourImpl cursorBehaviour;

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

    @Before
    public void setup() {
        cursorBehaviour = new CursorBehaviourImpl();
        cursorBehaviour.setDecimalSeparator('.');
    }

    @Test
    public void digitCount(DigitCountTestCase d) {
        assertEquals(d.expectedDigitCount, cursorBehaviour.digitCount(d.string, d.selection));
    }

    private enum DigitCountTestCase {

        CASE1("120^456", 3),
        CASE1a("^", 0),
        CASE2a("Br 0^.99", 0),
        CASE2b("Br ^0.99", 0),
        CASE2c("Br 0.^99", 1),
        CASE2("^120456", 0),
        CASE3("12.^456", 3),
        CASE4a("ab0^.56", 0),
        CASE4b("ab1^.56", 1),
        CASE5("220^456", 3);

        private final int selection;
        private final String inputText;
        public String string;
        private final int expectedDigitCount;

        DigitCountTestCase(String string, int expectedDigitCount) {
            this.string = string;
            this.expectedDigitCount = expectedDigitCount;

            selection = string.indexOf('^');

            this.inputText = string.substring(0, selection) + string.substring(selection + 1);

        }

        @Override
        public String toString() {
            return MessageFormat.format("{0} -> {1}", string, selection);
        }
    }

    @Test
    public void cursorPositionAfterCount(CursorPositionTestCase d) {
        int i = cursorBehaviour.cursorPositionAfterCount(d.count, d.string);
        assertEquals(d.expectedCursorPosition, i);
    }

    private enum CursorPositionTestCase {

        CASE1(0, "^1234"),
        CASE2(1, "1^234"),
        CASE3(2, "12^34"),
        CASE4(3, "123^4"),
        CASE5(4, "1234^"),
        CASE6(5, "1234^"),
        CASE7(60, "1234^"),

        CASE_A(0, "$ ^5.60"),
        CASE_B(0, "$ 0^.60"),
        CASE_C(1, "$ 0.^60"),
        CASE_D(3, "Br 12.^4"),
        CASE_E(2, "Br 12^.4"),
        ;

        private final String display;
        int expectedCursorPosition;
        public int count;
        public String string;

        CursorPositionTestCase(int count, String string) {
            this.count = count;
            this.display = count+" -> "+string;
            this.string = string.replaceAll("\\^","");
            this.expectedCursorPosition = string.indexOf('^');
        }

        @Override
        public String toString() {
            return display;
        }
    }

    @Test
    public void setDecimalSeparator() {
    }

}