package fr.legendsofxania.dungeon.interaction.dungeon

import com.typewritermc.core.interaction.Interaction
import com.typewritermc.core.interaction.InteractionContext
import com.typewritermc.core.utils.UntickedAsync
import com.typewritermc.core.utils.ok
import com.typewritermc.engine.paper.entry.entries.EventTrigger
import com.typewritermc.engine.paper.entry.triggerFor
import com.typewritermc.engine.paper.plugin
import com.typewritermc.engine.paper.utils.msg
import com.typewritermc.engine.paper.utils.toBukkitLocation
import fr.legendsofxania.dungeon.entry.action.StartDungeonInstanceActionEntry
import fr.legendsofxania.dungeon.event.*
import fr.legendsofxania.dungeon.interaction.dungeon.trigger.DungeonStopTrigger
import fr.legendsofxania.dungeon.manager.InstanceManager
import fr.legendsofxania.dungeon.manager.PlayerManager
import fr.legendsofxania.dungeon.manager.StructureManager
import fr.legendsofxania.dungeon.manager.WorldManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lirand.api.extensions.events.unregister
import lirand.api.extensions.server.registerSuspendingEvents
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Duration

class DungeonInteraction(
    private val player: Player,
    override val context: InteractionContext,
    override val priority: Int,
    val eventTriggers: List<EventTrigger>,
    val entry: StartDungeonInstanceActionEntry
) : Interaction, Listener, KoinComponent {
    private val instanceManager: InstanceManager by inject()
    private val playerManager by lazy { PlayerManager(instanceManager) }
    private val structureManager by lazy { StructureManager(instanceManager) }

    private val dungeonDefinition = entry.dungeon
    private val dungeonLocation = WorldManager.startDungeon()
    private val dungeonInstance = instanceManager.startDungeonInstance(dungeonDefinition, dungeonLocation)

    private var lastPosition: Triple<Int, Int, Int>? = null

    override suspend fun initialize(): Result<Unit> {
        structureManager.placeRooms(player, dungeonInstance)
        playerManager.setDungeonInstance(player, dungeonInstance)

        player.teleportAsync(getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN)

        plugin.registerSuspendingEvents(this)
        AsyncPlayerJoinDungeonInstanceEvent(player, dungeonDefinition).callEvent()

        player.msg("Starting")
        return ok(Unit)
    }

    override suspend fun tick(deltaTime: Duration) {
        player.msg("Ticking...")
    }

    override suspend fun teardown() {
        playerManager.removeDungeonInstance(player)
        playerManager.removeRoomInstance(player)

        structureManager.deleteRooms(dungeonInstance)
        WorldManager.stopDungeon(dungeonLocation)
        instanceManager.stopDungeonInstance(dungeonInstance)

        unregister()
        AsyncPlayerLeaveDungeonInstanceEvent(player, dungeonDefinition).callEvent()
        player.msg("Stopped")
    }

    @EventHandler
    suspend fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.player != player) return

        val to = event.to
        val pos = Triple(to.blockX, to.blockY, to.blockZ)
        if (pos == lastPosition) return
        lastPosition = pos

        val prevRoom = playerManager.getRoomInstance(player)
        val newRoom = instanceManager.getRoomInstance(dungeonInstance, to)

        withContext(Dispatchers.UntickedAsync) {
            if (newRoom != null) {
                playerManager.setRoomInstance(player, newRoom)
                prevRoom?.let { AsyncPlayerLeaveRoomInstanceEvent(player, it.definition).callEvent() }
                AsyncPlayerJoinRoomInstanceEvent(player, newRoom.definition).callEvent()
            } else {
                DungeonStopTrigger.triggerFor(player, context)
            }
        }
    }

    // Some dark magic to handle respawn on Folia #2
    @EventHandler
    fun onPlayerRespawn(event: InventoryCloseEvent) {
        val eventPlayer = event.player as? Player ?: return
        if (eventPlayer != player) return
        if (event.inventory.type != InventoryType.CRAFTING) return
        if (!player.isDead || !player.isOnline || player.health > 0) return

        player.teleportAsync(getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN)
    }

    private fun getSpawnLocation(): Location {
        val world = dungeonLocation.world
            ?: error("Dungeon world not found")
        val location = dungeonDefinition.entry?.respawnLocation?.get(player, context)?.toBukkitLocation(world)
            ?: error("Spawn location not found for DungeonDefinitionEntry: ${dungeonDefinition.id}")

        return location
    }
}
