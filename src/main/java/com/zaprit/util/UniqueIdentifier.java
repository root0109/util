/**
 * 
 */
package com.zaprit.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @author vaibhav.singh
 *
 */
public final class UniqueIdentifier
{
	public UniqueIdentifier()
	{
		throw new IllegalArgumentException("Object cannot be created");
	}

	public long getId()
	{
		final UUID uid = UUID.randomUUID();
		final ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
		buffer.putLong(uid.getLeastSignificantBits());
		buffer.putLong(uid.getMostSignificantBits());
		final BigInteger bigInteger = new BigInteger(buffer.array());
		return bigInteger.longValue() & Long.MAX_VALUE;
	}

	public static byte[] getBytesFromUUID(UUID uuid)
	{
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return bb.array();
	}

	public static UUID getUUIDFromBytes(byte[] bytes)
	{
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		Long high = byteBuffer.getLong();
		Long low = byteBuffer.getLong();
		return new UUID(high, low);
	}
}
