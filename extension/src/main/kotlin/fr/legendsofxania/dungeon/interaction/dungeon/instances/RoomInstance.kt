package fr.legendsofxania.dungeon.interaction.dungeon.instances

import com.typewritermc.core.entries.Ref
import fr.legendsofxania.dungeon.entry.manifest.definition.RoomDefinitionEntry
import org.bukkit.util.BoundingBox
import java.util.*

data class RoomInstance(
    val id: UUID,
    val definition: Ref<RoomDefinitionEntry>,
    val boundingBox: BoundingBox
)