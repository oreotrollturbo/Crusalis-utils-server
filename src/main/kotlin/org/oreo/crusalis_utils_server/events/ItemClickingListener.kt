package org.oreo.crusalis_utils_server.events


import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable
import org.oreo.crusalis_utils_server.Crusalis_utils_server
import org.oreo.crusalis_utils_server.item.ItemManager


class ItemClickingListener(private val plugin: Crusalis_utils_server) : Listener {

    @EventHandler
    fun itemHeld(e: PlayerInteractEvent) {

        if (e.player.inventory.itemInMainHand != ItemManager.teleComunicationSctick) {
            return
        } //TODO send this to other players
            //TODO track where the player clicks
        object : BukkitRunnable() {
            override fun run() {

                if (e.player.inventory.itemInMainHand != ItemManager.playerTeamInfo) {
                    cancel() // Cancels this task.
                }

            }
        }.runTaskTimer(plugin, 0L, 1L)
    }


    fun getNearbyPlayers(player: Player): List<Player> {
        return player.trackedBy.toList()
    }
}