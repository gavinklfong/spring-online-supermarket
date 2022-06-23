package space.gavinklfong.supermarket.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class CommonUtils {
    static public final String EMPTY_UUID_STRING = "00000000-0000-0000-0000-000000000000";
    static public final UUID EMPTY_UUID = UUID.fromString(EMPTY_UUID_STRING);
}

