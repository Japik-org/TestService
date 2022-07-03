package com.japik.services.test;

import com.japik.settings.Settings;

public final class TestServiceSettingKeys {
    public static String KEY_CREATE_TICK_GROUP;
    
    public static boolean isCreateTickGroupEnabled(Settings settings){
        return settings.getBooleanOrDefault(KEY_CREATE_TICK_GROUP, true);
    }
}
