package fr.legendsofxania.dungeon.enums

import org.bukkit.util.Vector

enum class Direction {
    NORTH {
        override fun getOffset(size: Vector) = Vector(0.0, 0.0, -size.z)
    },
    SOUTH {
        override fun getOffset(size: Vector) = Vector(0.0, 0.0, size.z)
    },
    EAST {
        override fun getOffset(size: Vector) = Vector(size.x, 0.0, 0.0)
    },
    WEST {
        override fun getOffset(size: Vector) = Vector(-size.x, 0.0, 0.0)
    },
    UP {
        override fun getOffset(size: Vector) = Vector(0.0, size.y, 0.0)
    },
    DOWN {
        override fun getOffset(size: Vector) = Vector(0.0, -size.y, 0.0)
    };

    abstract fun getOffset(size: Vector): Vector
}