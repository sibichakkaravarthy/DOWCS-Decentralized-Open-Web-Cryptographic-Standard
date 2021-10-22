package me.twodee.dowcspws;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

public class Helper {
    public static <K, V> void addNotNull(Map<K, V> map, K key, V value) {
        if(value != null)
            map.put(key, value);
    }
    private static String uuidToBase64(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return Base64.getUrlEncoder().encodeToString(bb.array());
    }

    public static String generateUniqueId() {
        return uuidToBase64(UUID.randomUUID());
    }
}
