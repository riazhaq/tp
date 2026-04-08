package seedu.address.ui;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assumptions;

import javafx.application.Platform;

/**
 * Minimal JavaFX test helpers for UI unit tests.
 */
public final class FxTestUtil {

    private static final long FX_TIMEOUT_SECONDS = isLinuxOs() ? 30 : 5;
    private static boolean isFxToolkitAvailable = true;

    private FxTestUtil() {}

    /**
     * Starts the JavaFX toolkit once for UI tests.
     */
    public static void setUpFxToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            Platform.setImplicitExit(false);
        } catch (UnsupportedOperationException unsupportedEnvironment) {
            isFxToolkitAvailable = false;
            return;
        } catch (IllegalStateException alreadyStarted) {
            Platform.setImplicitExit(false);
            latch.countDown();
        }
        if (!latch.await(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw new AssertionError("Timed out starting JavaFX toolkit");
        }
    }

    /**
     * Runs the supplied action on the JavaFX thread and returns its result.
     */
    public static <T> T onFx(ThrowingSupplier<T> supplier) {
        Assumptions.assumeTrue(isFxToolkitAvailable,
                "Skipping JavaFX-dependent test because toolkit is unavailable in this environment");

        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                result.set(supplier.get());
            } catch (Throwable throwable) {
                error.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        try {
            if (!latch.await(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new AssertionError("Timed out waiting for JavaFX operation");
            }
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new AssertionError(interruptedException);
        }

        if (error.get() != null) {
            throw new AssertionError(error.get());
        }
        return result.get();
    }

    private static boolean isLinuxOs() {
        return System.getProperty("os.name", "").toLowerCase().contains("linux");
    }

    /**
     * Runs the supplied action on the JavaFX thread.
     */
    public static void onFxRun(ThrowingRunnable runnable) {
        onFx(() -> {
            runnable.run();
            return null;
        });
    }

    /**
     * Functional interface for JavaFX test code that returns a value.
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    /**
     * Functional interface for JavaFX test code that does not return a value.
     */
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
