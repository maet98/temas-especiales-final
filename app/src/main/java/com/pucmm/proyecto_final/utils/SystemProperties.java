package com.pucmm.proyecto_final.utils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public final class SystemProperties {

    public static CharSequence[] getOptions() {
        final CharSequence[] options = new CharSequence[PhotoOptions.values().length];
        final AtomicInteger atomic = new AtomicInteger(0);

        for (PhotoOptions obj : Arrays.asList(PhotoOptions.values())) {
            options[atomic.getAndIncrement()] = obj.getValue();
        }

        return options;
    }

}
