package me.twodee.dowcsttp;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

import static org.apache.tomcat.util.codec.binary.Base64.encodeBase64URLSafeString;

public class Helper {
    public static <K, V> void addNotNull(Map<K, V> map, K key, V value) {
        if(value != null)
            map.put(key, value);
    }

    public static byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }
    public static String generateUniqueId() {
        return encodeBase64URLSafeString(getBytesFromUUID(UUID.randomUUID()));
    }
}
