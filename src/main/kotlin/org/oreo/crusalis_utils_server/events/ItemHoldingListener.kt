package org.oreo.crusalis_utils_server.events

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.scheduler.BukkitRunnable
import org.oreo.crusalis_utils_server.Crusalis_utils_server
import org.oreo.crusalis_utils_server.item.ItemManager


class ItemHoldingListener(private val plugin: Crusalis_utils_server) : Listener {

    @EventHandler
    fun itemHeld(e: PlayerItemHeldEvent) {

        if (e.player.inventory.itemInMainHand != ItemManager.playerTeamInfo) {
            return
        }

        object : BukkitRunnable() {
            override fun run() {

                spawnGreenRedstoneParticles(e.player,getNearbyPlayers(e.player))

                if (e.player.inventory.itemInMainHand != ItemManager.playerTeamInfo) {
                    cancel() // Cancels this task.
                }

            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun spawnGreenRedstoneParticles(player : Player,players: List<Player>) {
        // Define the dust options with a green color and a size (1.0f)

        players.forEach { player0 -> //TODO compare nations and change colour
            val particleLocation = player0.location.clone().add(0.0, player.height + 0.5, 0.0)

            val dustOptions = Particle.DustOptions(Color.GREEN, 1.0f)

            player.spawnParticle(
                Particle.DUST,
                particleLocation,
                1,
                0.0, 0.0, 0.0,
                0.0,
                dustOptions
            )
        }
    }


    fun getNearbyPlayers(player: Player): List<Player> {
        return player.trackedBy.toList()
    }
}