package fr.legendsofxania.dungeon.interactions.dungeon.instances

import com.typewritermc.core.entries.Ref
import fr.legendsofxania.dungeon.entries.manifest.definition.RoomDefinitionEntry
import org.bukkit.util.BoundingBox

data class RoomInstance(
    val id: String,
    val definition: Ref<RoomDefinitionEntry>,
    val boundingBox: BoundingBox
)
