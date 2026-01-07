package fr.legendsofxania.dungeon.interaction.content

import com.typewritermc.core.entries.Query
import com.typewritermc.core.utils.UntickedAsync
import com.typewritermc.core.utils.failure
import com.typewritermc.core.utils.launch
import com.typewritermc.core.utils.ok
import com.typewritermc.engine.paper.content.ContentComponent
import com.typewritermc.engine.paper.content.ContentContext
import com.typewritermc.engine.paper.content.ContentMode
import com.typewritermc.engine.paper.content.components.*
import com.typewritermc.engine.paper.content.entryId
import com.typewritermc.engine.paper.utils.asMini
import com.typewritermc.engine.paper.utils.msg
import fr.legendsofxania.dungeon.entry.static.template.RoomTemplateEntry
import fr.legendsofxania.dungeon.manager.TemplateManager
import fr.legendsofxania.dungeon.util.BoundingBoxViewer
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BoundingBox
import java.time.Duration

class RoomTemplateContentMode(
    context: ContentContext,
    player: Player
) : ContentMode(context, player) {
    private var boundingBoxViewer: BoundingBoxViewer? = null
    private var tickCounter = 0

    override suspend fun setup(): Result<Unit> {
        bossBar {
            title = "Saving RoomTemplate..."
            color = BossBar.Color.PINK
            progress = 1f
        }

        val entryId = context.entryId
            ?: return failure("Entry ID not found in context.")
        val entry = Query.findById<RoomTemplateEntry>(entryId)
            ?: return failure("RoomTemplateEntry not found for ID: $entryId")

        val selectionTool = SelectionTool(entry) { corner1, corner2 ->
            updateBoundingBox(corner1, corner2)
        }
        +selectionTool

        exit()
        return ok(Unit)
    }

    override suspend fun tick(deltaTime: Duration) {
        tickCounter++

        if (tickCounter == 10) {
            boundingBoxViewer?.drawnBox()
            tickCounter = 0
        }
    }

    private fun updateBoundingBox(corner1: Location?, corner2: Location?) {
        if (corner1 != null && corner2 != null) {
            val minX = minOf(corner1.x, corner2.x)
            val minY = minOf(corner1.y, corner2.y)
            val minZ = minOf(corner1.z, corner2.z)
            val maxX = maxOf(corner1.x, corner2.x) + 1
            val maxY = maxOf(corner1.y, corner2.y) + 1
            val maxZ = maxOf(corner1.z, corner2.z) + 1

            val boundingBox = BoundingBox(minX, minY, minZ, maxX, maxY, maxZ)
            boundingBoxViewer = BoundingBoxViewer(player, boundingBox)
        } else {
            boundingBoxViewer = null
        }
    }
}

@Suppress("UnstableApiUsage")
private class SelectionTool(
    private val entry: RoomTemplateEntry,
    private val onSelectionChanged: (Location?, Location?) -> Unit
) : ContentComponent, ItemComponent {
    private var corner1: Location? = null
    private var corner2: Location? = null

    override fun item(player: Player): Pair<Int, IntractableItem> {
        val item = ItemStack(Material.BREEZE_ROD).apply {
            setData(DataComponentTypes.ITEM_NAME, "<aqua>RoomTemplate Selection</aqua>".asMini())
            setData(
                DataComponentTypes.LORE, ItemLore.lore().addLines(
                    """
                    <!i><gray><white>Left-click</white> to select the first corner.</gray>
                    <!i><gray><white>Right-click</white> to select the second corner.</gray>
                    <!i><gray><white>Shift + Left-click</white> to save the room.</gray>
                """.trimIndent().lines().map { it.asMini() }
                ))
        } onInteract { event ->
            val location = event.clickedBlock?.location ?: player.location
            handleInteraction(player, location, event.type)
        }

        return 4 to item
    }

    private fun handleInteraction(player: Player, location: Location, type: ItemInteractionType) {
        when (type) {
            ItemInteractionType.LEFT_CLICK -> {
                corner1 = location
                player.msg("First corner selected at <blue>${location.blockX}</blue>, <blue>${location.blockY}</blue>, <blue>${location.blockZ}</blue>.")
                onSelectionChanged(corner1, corner2)
            }

            ItemInteractionType.RIGHT_CLICK -> {
                corner2 = location
                player.msg("Second corner selected at <blue>${location.blockX}</blue>, <blue>${location.blockY}</blue>, <blue>${location.blockZ}</blue>.")
                onSelectionChanged(corner1, corner2)
            }

            ItemInteractionType.SHIFT_LEFT_CLICK -> {
                val c1 = corner1 ?: run {
                    player.msg("<red>You must select both corners before saving the room.</red>")
                    return
                }
                val c2 = corner2 ?: run {
                    player.msg("<red>You must select both corners before saving the room.</red>")
                    return
                }

                Dispatchers.UntickedAsync.launch {
                    TemplateManager.saveTemplate(c1, c2, entry)
                        .onSuccess {
                            player.msg("RoomTemplate saved successfully!")
                        }
                        .onFailure {
                            player.msg("<red>Failed to save RoomTemplate: ${it.message}</red>")
                        }
                }
            }

            else -> return
        }
    }
}