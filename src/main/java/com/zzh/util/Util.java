package com.zzh.util;

import java.util.UUID;

/**
 * Created by zzh on 2017/4/12.
 */
public class Util {

    public static String UUID() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
