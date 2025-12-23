package fr.legendsofxania.dungeon.entries.manifest.definition

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.core.extension.annotations.Tags
import com.typewritermc.engine.paper.entry.ManifestEntry
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import fr.legendsofxania.dungeon.entries.static.template.RoomTemplateEntry
import fr.legendsofxania.dungeon.enums.Direction

@Entry(
    "room_definition",
    "The definition of a room.",
    Colors.YELLOW,
    "tabler:building-arch"
)
@Tags("room_definition")
/**
 * The `Room Definition` entry is used to define a room in a dungeon.
 *
 * ## How could this be used?
 *
 * This could be used to define a room in a dungeon, including its next room(s), template, and direction.
 */
class RoomDefinitionEntry(
    override val id: String = "",
    override val name: String = "",
    @Help("The next room(s) of the dungeon. Leave empty if this is the last one.")
    val children: List<Ref<RoomDefinitionEntry>> = emptyList(),
    @Help("The RoomTemplate to use for this room.")
    val template: Var<Ref<RoomTemplateEntry>> = ConstVar(emptyRef()),
    @Help("The direction in which the RoomInstance will be generated.")
    val direction: Var<Direction> = ConstVar(Direction.NORTH)
) : ManifestEntry