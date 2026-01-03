package fr.legendsofxania.dungeon.utils

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import org.jspecify.annotations.NullMarked
import java.nio.ByteBuffer
import java.util.*

@NullMarked
class UUIDDataType private constructor() : PersistentDataType<ByteArray, UUID> {

    override fun getPrimitiveType(): Class<ByteArray> {
        return ByteArray::class.java
    }

    override fun getComplexType(): Class<UUID> {
        return UUID::class.java
    }

    override fun toPrimitive(
        complex: UUID,
        context: PersistentDataAdapterContext
    ): ByteArray {
        val buffer = ByteBuffer.allocate(Long.SIZE_BYTES * 2)
        buffer.putLong(complex.mostSignificantBits)
        buffer.putLong(complex.leastSignificantBits)
        return buffer.array()
    }

    override fun fromPrimitive(
        primitive: ByteArray,
        context: PersistentDataAdapterContext
    ): UUID {
        val buffer = ByteBuffer.wrap(primitive)
        return UUID(buffer.long, buffer.long)
    }

    companion object {
        val INSTANCE: UUIDDataType = UUIDDataType()
    }
}
