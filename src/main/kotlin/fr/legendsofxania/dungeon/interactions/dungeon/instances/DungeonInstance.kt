package fr.legendsofxania.dungeon.interactions.dungeon.instances

import com.typewritermc.core.entries.Ref
import fr.legendsofxania.dungeon.entries.manifest.definition.DungeonDefinitionEntry
import org.bukkit.Location

data class DungeonInstance(
    val id: String,
    val definition: Ref<DungeonDefinitionEntry>,
    val location: Location,
    val rooms: MutableList<RoomInstance>
)
