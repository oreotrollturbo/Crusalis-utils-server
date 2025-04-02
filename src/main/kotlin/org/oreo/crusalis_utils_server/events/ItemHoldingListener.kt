package org.oreo.crusalis_utils_server.events

import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.scheduler.BukkitRunnable
import org.oreo.crusalis_utils_server.CrusalisUtilsServer
import org.oreo.crusalis_utils_server.item.ItemManager
import org.oreo.crusalis_utils_server.utils.GeneralUtils.getNearbyPlayers
import phonon.nodes.Nodes
import phonon.nodes.objects.Nation


class ItemHoldingListener(private val plugin: CrusalisUtilsServer) : Listener {

    @EventHandler
    fun itemHeld(e: PlayerItemHeldEvent) {

        val heldItem = e.player.inventory.getItem(e.newSlot) ?: return

        if (!ItemManager.isCustomItem(heldItem,ItemManager.playerTeamInfo)) {
            return
        }

        e.player.sendMessage("Holding right item")

        e.player.sendMessage(Nodes.getResident(e.player)?.nation.toString())

        val nation = Nodes.getResident(e.player)?.nation ?: return

        object : BukkitRunnable() {
            override fun run() {

                e.player.sendMessage("Looping")

                spawnGreenRedstoneParticles(e.player,getNearbyPlayers(e.player), nation)

                if (e.player.inventory.itemInMainHand != ItemManager.playerTeamInfo) {
                    cancel() // Cancels this task.
                }
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }

    fun spawnGreenRedstoneParticles(player : Player,players: List<Player>,nation: Nation) {
        // Define the dust options with a green color and a size (1.0f)

        players.forEach { player0 ->

            val otherPlayerNation = Nodes.getResident(player0)?.nation ?: return@forEach

            val dustColour = if (otherPlayerNation === nation) {
                Color.GREEN
            } else if (nation.allies.contains(otherPlayerNation)) {
                Color.AQUA
            } else if (nation.enemies.contains(otherPlayerNation)) {
                Color.RED
            } else {
                Color.YELLOW
            }

            val particleLocation = player0.location.clone().add(0.0, player.height + 0.5, 0.0)

            val dustOptions = Particle.DustOptions(dustColour, 1.0f)

            player.spawnParticle(
                Particle.DUST,
                particleLocation,
                1,
                0.0, 0.0, 0.0,
                0.0,
                dustOptions,
                true
            )
        }
    }
}