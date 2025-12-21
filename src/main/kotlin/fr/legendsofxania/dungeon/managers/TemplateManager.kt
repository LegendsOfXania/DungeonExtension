package fr.legendsofxania.dungeon.managers

import com.typewritermc.core.entries.Query
import com.typewritermc.engine.paper.entry.entries.binaryData
import com.typewritermc.engine.paper.utils.server
import fr.legendsofxania.dungeon.entries.static.template.RoomTemplateEntry
import org.bukkit.Location
import java.io.ByteArrayOutputStream

object TemplateManager {
    suspend fun saveTemplate(
        corner1: Location,
        corner2: Location,
        entryId: String
    ): Result<Unit> {
        val structureManager = server.structureManager
        val structure = structureManager.createStructure().also { it.fill(corner1, corner2, true) }

        val bytes = ByteArrayOutputStream().use { out ->
            structureManager.saveStructure(out, structure)
            out.toByteArray()
        }

        val entry = Query.findById<RoomTemplateEntry>(entryId)
            ?: return Result.failure(NullPointerException("RoomTemplate entry with id $entryId not found"))

        entry.binaryData(bytes)
        return Result.success(Unit)
    }

    // TODO: loadTemplate function
}