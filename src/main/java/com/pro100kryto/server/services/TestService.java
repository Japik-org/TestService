package com.pro100kryto.server.services;

import com.pro100kryto.server.service.AServiceType;
import com.pro100kryto.server.service.Service;
import com.pro100kryto.server.services.test.ITestServiceConnection;

import java.rmi.RemoteException;

public class TestService extends AServiceType<ITestServiceConnection> {


    public TestService(Service service) {
        super(service);
    }

    @Override
    protected ITestServiceConnection createServiceConnection() {
        return new TestServiceConnection();
    }


    private final class TestServiceConnection implements ITestServiceConnection {

        @Override
        public boolean ping() throws RemoteException {
            return true;
        }
    }
}
