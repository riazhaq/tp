package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MainWindowTest {

    @Test
    public void successUpdate_marksSuccessAndFormatsFeedback() {
        MainWindow.ResultDisplayUpdate update = MainWindow.successUpdate("done");
        assertTrue(update.isSuccess());
        assertEquals("[SUCCESS] done", update.feedback());
    }

    @Test
    public void errorUpdate_marksFailureAndFormatsFeedback() {
        MainWindow.ResultDisplayUpdate update = MainWindow.errorUpdate("bad");
        assertFalse(update.isSuccess());
        assertEquals("[ERROR] bad", update.feedback());
    }

    @Test
    public void applyResultUpdate_updatesSink() {
        FakeSink sink = new FakeSink();
        MainWindow.applyResultUpdate(sink, MainWindow.successUpdate("ok"));
        assertTrue(sink.success);
        assertEquals("[SUCCESS] ok", sink.feedback);
    }

    private static final class FakeSink implements MainWindow.ResultDisplaySink {
        private boolean success;
        private String feedback;

        @Override
        public void setCommandSuccess(boolean isSuccess) {
            success = isSuccess;
        }

        @Override
        public void setFeedbackToUser(String feedbackToUser) {
            feedback = feedbackToUser;
        }
    }

}
