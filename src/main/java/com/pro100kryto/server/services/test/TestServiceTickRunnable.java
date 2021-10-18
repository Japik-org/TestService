package com.pro100kryto.server.services.test;

import com.pro100kryto.server.logger.ILogger;
import com.pro100kryto.server.services.TestService;
import com.pro100kryto.server.tick.AServiceTickRunnable;
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
    public void tick(long dtms) {
        logger.info("dtms = " + dtms);
        if ((++dtmsCounter) == dtmsCounterMax) {
            dtmsMedium = dtmsMediumTemp;
            dtmsMediumTemp = 0;
        }
        dtmsMediumTemp += dtms / (double) dtmsCounterMax;
    }

}
