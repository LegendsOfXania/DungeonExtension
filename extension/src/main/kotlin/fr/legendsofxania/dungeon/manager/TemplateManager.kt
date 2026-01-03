package fr.legendsofxania.dungeon.manager

import com.typewritermc.engine.paper.entry.entries.binaryData
import com.typewritermc.engine.paper.entry.entries.hasData
import com.typewritermc.engine.paper.utils.server
import fr.legendsofxania.dungeon.entry.static.template.RoomTemplateEntry
import org.bukkit.Location
import org.bukkit.structure.Structure
import java.io.ByteArrayOutputStream

object TemplateManager {
    suspend fun saveTemplate(
        corner1: Location,
        corner2: Location,
        entry: RoomTemplateEntry
    ): Result<Unit> {
        val structureManager = server.structureManager
        val structure = structureManager.createStructure().also { it.fill(corner1, corner2, true) }

        val bytes = ByteArrayOutputStream().use { out ->
            structureManager.saveStructure(out, structure)
            out.toByteArray()
        }

        entry.binaryData(bytes)
        return Result.success(Unit)
    }

    suspend fun loadTemplate(entry: RoomTemplateEntry): Structure? {
        if (entry.hasData()) {
            val inputStream = entry.binaryData()?.inputStream() ?: return null
            return server.structureManager.loadStructure(inputStream)
        }

        return null
    }
}