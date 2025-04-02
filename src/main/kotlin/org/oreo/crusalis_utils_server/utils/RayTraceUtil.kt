package org.oreo.crusalis_utils_server.utils

import org.bukkit.Location
import org.bukkit.entity.Player

object RayTraceUtil {
    /**
     * Returns the last non-solid location along the player's line of sight up to a given distance.
     * It checks every half block in the direction the player is facing.
     *
     * @param player the player whose view to trace
     * @param maxDistance the maximum distance to check (in blocks)
     * @return the last Location that did not contain a solid block
     */
    fun getLastNonSolidLocation(player: Player, maxDistance: Double, resolution: Double): Location? {
        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction.normalize()

        var lastNonSolidLocation = eyeLocation.clone()

        var d = 0.0
        while (d <= maxDistance) {
            val currentLocation = eyeLocation.clone().add(direction.clone().multiply(d))
            val block = currentLocation.block
            if (block.type.isSolid && !block.type.toString().contains("GLASS") ) {
                return lastNonSolidLocation
            }
            lastNonSolidLocation = currentLocation
            d += resolution
        }


        // If no solid block is found, return null
        return null
    }
}
