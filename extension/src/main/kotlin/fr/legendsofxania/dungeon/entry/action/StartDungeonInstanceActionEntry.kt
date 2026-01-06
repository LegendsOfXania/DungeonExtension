package fr.legendsofxania.dungeon.entry.action

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.entries.emptyRef
import com.typewritermc.core.entries.priority
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.engine.paper.entry.Criteria
import com.typewritermc.engine.paper.entry.Modifier
import com.typewritermc.engine.paper.entry.TriggerableEntry
import com.typewritermc.engine.paper.entry.entries.ActionEntry
import com.typewritermc.engine.paper.entry.entries.ActionTrigger
import com.typewritermc.engine.paper.entry.entries.EventTrigger
import com.typewritermc.engine.paper.entry.entries.GroupEntry
import fr.legendsofxania.dungeon.entry.manifest.definition.DungeonDefinitionEntry
import fr.legendsofxania.dungeon.interaction.dungeon.trigger.DungeonStartTrigger

@Entry(
    "start_dungeon_instance_action",
    "Starts a dungeon instance for a player.",
    Colors.RED,
    "carbon:build-image"
)
/**
 * The `Start Dungeon Instance Action` entry is used to start a dungeon.
 *
 * ## How could this be used?
 *
 * This could be used to start a DungeonInstance for a Group
 */
class StartDungeonInstanceActionEntry(
    override val id: String = "",
    override val name: String = "",
    override val criteria: List<Criteria> = emptyList(),
    override val modifiers: List<Modifier> = emptyList(),
    override val triggers: List<Ref<TriggerableEntry>> = emptyList(),
    @Help("The group for which to start the DungeonInstance.")
    val group: Ref<GroupEntry> = emptyRef(),
    @Help("The DungeonDefinition to use for the DungeonInstance.")
    val dungeon: Ref<DungeonDefinitionEntry> = emptyRef(),
    @Help("Only activate if you are creating the dungeon. This will give you information to help you.")
    val debug: Boolean = false
) : ActionEntry {
    override val eventTriggers: List<EventTrigger>
        get() = listOf(
            DungeonStartTrigger(
                this.priority,
                super.eventTriggers,
                this
            )
        )

    override fun ActionTrigger.execute() {}
}