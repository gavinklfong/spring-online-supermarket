package space.gavinklfong.supermarket.utils;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class CommonUtils {

    static public final String EMPTY_UUID_STRING = "00000000-0000-0000-0000-000000000000";

    public UUID emptyUUID() {
        return UUID.fromString(EMPTY_UUID_STRING);
    }
}
