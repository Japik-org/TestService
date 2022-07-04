package com.japik.services.test;

import com.japik.logger.ILogger;
import com.japik.services.test.shared.ITestServiceConnection;
import com.japik.tick.AServiceTickRunnable;
import lombok.Getter;

public class TestServiceTickRunnable extends AServiceTickRunnable<TestService, ITestServiceConnection> {
    private final ITickRunnableCallback callback;
    @Getter
    private double dtmsMedium = 0;
    private double dtmsMediumTemp = 0;
    @Getter
    private int dtmsCounter = 0;
    @Getter
    private static final int dtmsCounterMax = 100;

    public TestServiceTickRunnable(TestService service, ILogger logger, ITickRunnableCallback callback) {
        super(service, logger);
        this.callback = callback;
    }

    @Override
    public void tick(long dtms) throws InterruptedException {
        logger.info("dtms = " + dtms);
        if ((++dtmsCounter) == dtmsCounterMax) {
            dtmsMedium = dtmsMediumTemp;
            dtmsMediumTemp = 0;
        }
        dtmsMediumTemp += dtms / (double) dtmsCounterMax;

        Thread.sleep(1500);
    }

}
