package fr.legendsofxania.dungeon.interactions.content

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
import fr.legendsofxania.dungeon.entries.static.template.RoomTemplateEntry
import fr.legendsofxania.dungeon.managers.TemplateManager
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemLore
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.bossbar.BossBar
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RoomTemplateContentMode(
    context: ContentContext,
    player: Player
) : ContentMode(context, player) {

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
        +SelectionTool(entry)

        exit()
        return ok(Unit)
    }
}

@Suppress("UnstableApiUsage")
private class SelectionTool(
    private val entry: RoomTemplateEntry
) : ContentComponent, ItemComponent {

    private var corner1: Location? = null
    private var corner2: Location? = null

    override fun item(player: Player): Pair<Int, IntractableItem> {
        val item = ItemStack(Material.BREEZE_ROD).apply {
            setData(DataComponentTypes.ITEM_NAME, "<aqua>RoomTemplate Selection</aqua>".asMini())
            setData(
                DataComponentTypes.LORE, ItemLore.lore().addLines(
                """
                    </i><gray><white>Left-click</white> to select the first corner.</gray>
                    </i><gray><white>Right-click</white> to select the second corner.</gray>
                    </i><gray><white>Shift + Left-click</white> to save the room.</gray>
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
                player.msg("First corner selected at <blue>${location.blockX}, ${location.blockY}, ${location.blockZ}.</blue>")
            }

            ItemInteractionType.RIGHT_CLICK -> {
                corner2 = location
                player.msg("Second corner selected at <blue>${location.blockX}, ${location.blockY}, ${location.blockZ}.</blue>")
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

                if (c1.world != c2.world) {
                    player.msg("<red>The two corners must be in the same world.</red>")
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
