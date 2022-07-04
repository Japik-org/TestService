package com.japik.services.test.shared;

import com.japik.service.IServiceConnection;

public interface ITestServiceConnection extends IServiceConnection {
    long getTickCounter();

    double getTickGroupDtms();
}
