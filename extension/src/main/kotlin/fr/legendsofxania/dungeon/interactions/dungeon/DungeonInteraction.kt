package fr.legendsofxania.dungeon.interactions.dungeon

import com.typewritermc.core.interaction.Interaction
import com.typewritermc.core.interaction.InteractionContext
import com.typewritermc.core.utils.UntickedAsync
import com.typewritermc.core.utils.ok
import com.typewritermc.engine.paper.entry.entries.EventTrigger
import com.typewritermc.engine.paper.entry.triggerFor
import com.typewritermc.engine.paper.plugin
import com.typewritermc.engine.paper.utils.msg
import com.typewritermc.engine.paper.utils.toBukkitLocation
import fr.legendsofxania.dungeon.entries.action.StartDungeonInstanceActionEntry
import fr.legendsofxania.dungeon.events.AsyncPlayerJoinDungeonInstanceEvent
import fr.legendsofxania.dungeon.events.AsyncPlayerJoinRoomInstanceEvent
import fr.legendsofxania.dungeon.events.AsyncPlayerLeaveDungeonInstanceEvent
import fr.legendsofxania.dungeon.events.AsyncPlayerLeaveRoomInstanceEvent
import fr.legendsofxania.dungeon.interactions.dungeon.trigger.DungeonStopTrigger
import fr.legendsofxania.dungeon.managers.InstanceManager
import fr.legendsofxania.dungeon.managers.PlayerManager
import fr.legendsofxania.dungeon.managers.StructureManager
import fr.legendsofxania.dungeon.managers.WorldManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lirand.api.extensions.events.unregister
import lirand.api.extensions.server.registerSuspendingEvents
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration

class DungeonInteraction(
    val player: Player,
    override val context: InteractionContext,
    override val priority: Int,
    val eventTriggers: List<EventTrigger>,
    val entry: StartDungeonInstanceActionEntry
) : Interaction, Listener, KoinComponent {
    private val instanceManager: InstanceManager by inject()

    private val dungeonEntry = entry.dungeon
    private val dungeonLocation = WorldManager.startDungeon()
    private val dungeonInstance = instanceManager.startDungeonInstance(dungeonEntry, dungeonLocation)

    private var lastCheckedPosition: Triple<Int, Int, Int>? = null

    override suspend fun initialize(): Result<Unit> {
        StructureManager(instanceManager).placeRooms(player, dungeonInstance)
        PlayerManager(instanceManager).setDungeonInstance(player, dungeonInstance)

        val world = dungeonLocation.world
            ?: error("Dungeon world not found")
        val coordinate = dungeonEntry.entry?.respawnLocation?.get(player, context)?.toBukkitLocation(world)
            ?: error("Spawn location not found for DungeonDefinitionEntry: ${dungeonEntry.id}")

        player.teleportAsync(coordinate, PlayerTeleportEvent.TeleportCause.PLUGIN)

        plugin.registerSuspendingEvents(this)
        AsyncPlayerJoinDungeonInstanceEvent(player, dungeonEntry).callEvent()

        player.msg("Starting")
        return ok(Unit)
    }

    override suspend fun tick(deltaTime: Duration) {
        player.msg("Ticking...")
    }

    override suspend fun teardown() {
        PlayerManager(instanceManager).removeDungeonInstance(player)
        PlayerManager(instanceManager).removeRoomInstance(player)

        StructureManager(instanceManager).deleteRooms(dungeonInstance)
        WorldManager.stopDungeon(dungeonLocation)
        instanceManager.stopDungeonInstance(dungeonInstance)

        unregister()
        AsyncPlayerLeaveDungeonInstanceEvent(player, dungeonEntry).callEvent()
        player.msg("Stopped")
    }

    @EventHandler
    suspend fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.player != player) return

        val to = event.to
        val position = Triple(to.blockX, to.blockY, to.blockZ)

        if (position == lastCheckedPosition) return
        lastCheckedPosition = position

        val previousRoomInstance = PlayerManager(instanceManager).getRoomInstance(player)
        val newRoomInstance = instanceManager.getRoomInstance(dungeonInstance, event.to)

        withContext(Dispatchers.UntickedAsync) {
            if (newRoomInstance != null) {
                PlayerManager(instanceManager).setRoomInstance(player, newRoomInstance)
                previousRoomInstance?.let {
                    AsyncPlayerLeaveRoomInstanceEvent(player, it.definition).callEvent()
                }
                AsyncPlayerJoinRoomInstanceEvent(player, newRoomInstance.definition).callEvent()
            } else {
                DungeonStopTrigger.triggerFor(player, context)
            }
        }
    }
}