package org.oreo.crusalis_utils_server.utils

import org.bukkit.entity.Player

object GeneralUtils {
    fun getNearbyPlayers(player: Player): List<Player> {
        return player.trackedBy.toList()
    }
}