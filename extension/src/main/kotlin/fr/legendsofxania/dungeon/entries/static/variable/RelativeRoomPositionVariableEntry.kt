package fr.legendsofxania.dungeon.entries.static.variable

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.VariableData
import com.typewritermc.core.extension.annotations.WithRotation
import com.typewritermc.core.utils.point.Coordinate
import com.typewritermc.core.utils.point.toCoordinate
import com.typewritermc.core.utils.point.toPosition
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import com.typewritermc.engine.paper.entry.entries.VarContext
import com.typewritermc.engine.paper.entry.entries.VariableEntry
import com.typewritermc.engine.paper.entry.entries.getData
import com.typewritermc.engine.paper.utils.toPosition
import com.typewritermc.engine.paper.utils.toWorld
import fr.legendsofxania.dungeon.entries.manifest.definition.RoomDefinitionEntry
import fr.legendsofxania.dungeon.managers.InstanceManager
import fr.legendsofxania.dungeon.managers.PlayerManager
import fr.legendsofxania.dungeon.managers.WorldManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Entry(
    "relative_room_position_variable",
    "A variable representing a position relative to a room",
    Colors.GREEN,
    "tabler:arrows-diagonal"
)
/** * The `Relative Room Position Variable Entry` is used to define a variable
 * that represents a position relative to a room in a dungeon.
 *
 * ## How could this be used?
 *
 * This could be used to get a position within a specific room of a dungeon,
 * for example to spawn entities or place objects relative to that room.
 */

@VariableData(RelativeRoomPositionVariableData::class)
class RelativeRoomPositionVariableEntry(
    override val id: String = "",
    override val name: String = ""
) : VariableEntry, KoinComponent {
    private val instanceManager: InstanceManager by inject()

    override fun <T : Any> get(context: VarContext<T>): T {
        val data = context.getData<RelativeRoomPositionVariableData>()
            ?: error("RelativeRoomPositionVariableData not found in context")

        val world = WorldManager.getWorld()
            ?: error("Dungeon world not found")

        val dungeonInstance = PlayerManager(instanceManager).getDungeonInstance(context.player)
            ?: error("DungeonInstance not found for player ${context.player.name}")

        val roomRef = data.room.get(context.player, context.interactionContext)
        val roomInstance = instanceManager.getRoomInstance(dungeonInstance, roomRef)
            ?: error("RoomInstance not found for room $roomRef in DungeonInstance ${dungeonInstance.id}")

        val coordinate = data.coordinate.get(context.player, context.interactionContext)
        val position = coordinate.toPosition(world.toWorld())
        val origin = roomInstance.boundingBox.min.toLocation(world).toPosition()

        @Suppress("UNCHECKED_CAST")
        return origin.add(position).toCoordinate() as T
    }
}

data class RelativeRoomPositionVariableData(
    @WithRotation
    val coordinate: Var<Coordinate> = ConstVar(Coordinate.ORIGIN),
    val room: Var<Ref<RoomDefinitionEntry>> = ConstVar(emptyRef())
)