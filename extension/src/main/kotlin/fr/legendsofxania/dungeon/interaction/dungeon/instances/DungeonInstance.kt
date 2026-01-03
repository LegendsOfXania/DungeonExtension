package fr.legendsofxania.dungeon.interaction.dungeon.instances

import com.typewritermc.core.entries.Ref
import fr.legendsofxania.dungeon.entry.manifest.definition.DungeonDefinitionEntry
import org.bukkit.Location
import java.util.*

data class DungeonInstance(
    val id: UUID,
    val definition: Ref<DungeonDefinitionEntry>,
    val location: Location,
    val rooms: MutableMap<UUID, RoomInstance>
)
