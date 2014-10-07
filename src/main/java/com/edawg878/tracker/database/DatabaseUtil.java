package com.edawg878.tracker.database;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @author EDawg878 <EDawg878@gmail.com>
 */
public class DatabaseUtil {

    public static byte[] toBinary(UUID uuid) {
        ByteBuffer bb = ByteBuffer.allocate(16);
        bb.putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public static UUID fromBinary(byte[] id) {
        ByteBuffer bb = ByteBuffer.wrap(id);
        return new UUID(bb.getLong(), bb.getLong());
    }

}
