package com.pro100kryto.server.services.test;

import com.pro100kryto.server.settings.Settings;

public final class TestServiceSettingKeys {
    public static String KEY_CREATE_TICK_GROUP;
    
    public static boolean isCreateTickGroupEnabled(Settings settings){
        return settings.getBooleanOrDefault(KEY_CREATE_TICK_GROUP, true);
    }
}
