package com.japik.services.test;

import com.japik.service.AServiceConnection;
import com.japik.service.ServiceConnectionParams;
import com.japik.services.test.shared.ITestServiceConnection;
import org.jetbrains.annotations.NotNull;

final class TestServiceConnection extends AServiceConnection<TestService, ITestServiceConnection>
        implements ITestServiceConnection {

    public TestServiceConnection(@NotNull TestService service, ServiceConnectionParams params) {
        super(service, params);
    }

    @Override
    public long getTickCounter() {
        return service.getTickRunnable().getDtmsCounter();
    }

    @Override
    public double getTickGroupDtms() {
        return service.getTickRunnable().getDtmsMedium();
    }

    @Override
    protected void onClose() {
        logger.info("TestServiceConnection closed");
    }
}
