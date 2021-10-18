package com.pro100kryto.server.services;

import com.pro100kryto.server.livecycle.AShortLiveCycleImpl;
import com.pro100kryto.server.livecycle.ILiveCycleImpl;
import com.pro100kryto.server.livecycle.StartException;
import com.pro100kryto.server.livecycle.StopSlowException;
import com.pro100kryto.server.logger.ILogger;
import com.pro100kryto.server.service.AService;
import com.pro100kryto.server.service.AServiceConnectionImpl;
import com.pro100kryto.server.service.ServiceParams;
import com.pro100kryto.server.services.test.ITestServiceConnection;
import com.pro100kryto.server.services.test.TestServiceTickRunnable;
import com.pro100kryto.server.settings.SettingListenerEventMask;
import com.pro100kryto.server.tick.ITick;
import com.pro100kryto.server.tick.ITickGroup;
import com.pro100kryto.server.tick.Ticks;
import org.jetbrains.annotations.NotNull;

import static com.pro100kryto.server.services.test.TestServiceSettingKeys.isCreateTickGroupEnabled;

public class TestService extends AService<ITestServiceConnection> {
    private ITickGroup tickGroup;
    private ITick tick;
    private TestServiceTickRunnable tickRunnable;

    public TestService(ServiceParams serviceParams) {
        super(serviceParams);
    }

    @Override
    public ITestServiceConnection createServiceConnection() {
        return new TestServiceConnection(this, logger);
    }

    @NotNull
    @Override
    protected ILiveCycleImpl getDefaultLiveCycleImpl() {
        return new LiveCycleImpl(this);
    }

    private final class LiveCycleImpl extends AShortLiveCycleImpl {
        private final TestService service;

        private LiveCycleImpl(TestService service) {
            this.service = service;
        }

        @Override
        public void init() {
            settingsManager.setCommonSettingsListener((settings, eventMask) -> {
                if (eventMask.containsPartially(SettingListenerEventMask.ON_APPLY)) {
                    if (isCreateTickGroupEnabled(settings)) {
                        tickGroup = Ticks.newTickGroupFreeMod(settings);
                        tickGroup.getLiveCycle().init();

                        tickRunnable = new TestServiceTickRunnable(service, logger, null);
                        tick = tickGroup.createTick(tickRunnable);

                        if (getLiveCycle().getStatus().isStarted()) {
                            tickGroup.getLiveCycle().start();
                            tick.activate();
                        }
                    }
                }
            });
        }

        @Override
        public void start() throws StartException {
            tickGroup.getLiveCycle().start();
            tick.activate();
        }

        @Override
        public void stopForce() {
            tick.inactivate();

            try {
                tickGroup.getLiveCycle().stopSlow();
            } catch (StopSlowException e) {
                logger.exception(e);
                tickGroup.getLiveCycle().stopForce();
            }
        }

        @Override
        public void destroy() {
            settingsManager.removeAllListeners();

            tick.destroy();
            tickGroup.deleteTickGroup();
        }
    }

    private final class TestServiceConnection extends AServiceConnectionImpl<TestService, ITestServiceConnection>
            implements ITestServiceConnection {

        public TestServiceConnection(@NotNull TestService service, ILogger logger) {
            super(service, logger);
        }

        @Override
        public long getTickCounter() {
            return tickRunnable.getDtmsCounter();
        }

        @Override
        public double getTickGroupDtms() {
            return tickRunnable.getDtmsMedium();
        }

        @Override
        protected void onClose() {
            logger.info("TestServiceConnection closed");
        }
    }
}
