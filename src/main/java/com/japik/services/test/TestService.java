package com.japik.services.test;

import com.japik.livecycle.AShortLiveCycleImpl;
import com.japik.livecycle.StartException;
import com.japik.livecycle.StopSlowException;
import com.japik.livecycle.controller.ILiveCycleImplId;
import com.japik.livecycle.controller.LiveCycleController;
import com.japik.service.AService;
import com.japik.service.ServiceConnectionParams;
import com.japik.service.ServiceParams;
import com.japik.services.test.shared.ITestServiceConnection;
import com.japik.settings.SettingListenerEventMask;
import com.japik.settings.Settings;
import com.japik.settings.SettingsManager;
import com.japik.tick.ITick;
import com.japik.tick.ITickGroup;
import com.japik.tick.Ticks;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static com.japik.services.test.TestServiceSettingKeys.isCreateTickGroupEnabled;

public class TestService extends AService<ITestServiceConnection> {
    @Getter
    private ITickGroup tickGroup;
    @Getter
    private ITick tick;
    @Getter
    private TestServiceTickRunnable tickRunnable;

    public TestService(ServiceParams serviceParams) {
        super(serviceParams);
    }

    @Override
    protected ITestServiceConnection createServiceConnection(ServiceConnectionParams params) {
        return new TestServiceConnection(this, params);
    }

    @Override
    protected void initLiveCycleController(LiveCycleController liveCycleController) {
        super.initLiveCycleController(liveCycleController);
        liveCycleController.putImplAll(new TestLiveCycleImpl(this));
    }

    private final class TestLiveCycleImpl extends AShortLiveCycleImpl implements ILiveCycleImplId {
        private final TestService service;

        @Getter
        private final String name = "TestLiveCycleImpl";
        @Getter @Setter
        private int priority = LiveCycleController.PRIORITY_NORMAL;

        private TestLiveCycleImpl(TestService service) {
            this.service = service;
        }

        @Override
        public void init() throws Throwable{
            settingsManager.setCommonSettingsListener(new SettingsListener(service));
            settingsManager.apply();
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
            tickGroup = null;
        }
    }

    @RequiredArgsConstructor
    public final class SettingsListener implements SettingsManager.ICommonSettingsListener {
        private final TestService service;

        @Override
        public void apply(Settings settings, SettingListenerEventMask eventMask) throws Throwable {
            if (eventMask.containsPartially(SettingListenerEventMask.ON_APPLY)) {
                if (isCreateTickGroupEnabled(settings) && tickGroup==null) {
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
        }
    }

}
