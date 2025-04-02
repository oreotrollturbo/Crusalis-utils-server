package org.oreo.crusalis_utils_server.events


import org.bukkit.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable
import org.oreo.crusalis_utils_server.CrusalisUtilsServer
import org.oreo.crusalis_utils_server.item.ItemManager
import org.oreo.crusalis_utils_server.utils.GeneralUtils
import org.oreo.crusalis_utils_server.utils.RayTraceUtil
import phonon.nodes.Nodes


class ItemClickingListener(private val plugin: CrusalisUtilsServer) : Listener {

    @EventHandler
    fun itemHeld(e: PlayerInteractEvent) {

        if (!ItemManager.isHoldingCustomItem(e.player,ItemManager.teleComunicationSctick)) {
            return
        }

        e.isCancelled = true

        if (e.player.hasCooldown(Material.STICK)) return

        e.player.setCooldown(Material.STICK,100) //5 sec cooldown
        e.player.playSound(e.player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2.0F)
        val serverRender = Bukkit.getServer().viewDistance * 16.0
        val raytraceLocation = RayTraceUtil.getLastNonSolidLocation(e.player,serverRender,0.3) ?: return

        val nation = Nodes.getResident(e.player)?.nation

        var timer = 8 * 20 //8 seconds
        var firstLoop = true
        object : BukkitRunnable() {
            override fun run() {

                val dustOptions = Particle.DustOptions(Color.RED, 5.0f)

                val offsetRange = 0.3
                val offsetX = (Math.random() - 0.5) * offsetRange
                val offsetY = (Math.random() - 0.5) * offsetRange
                val offsetZ = (Math.random() - 0.5) * offsetRange

                e.player.spawnParticle(
                    Particle.DUST,
                    raytraceLocation,
                    2,
                    offsetX, offsetY, offsetZ,
                    0.0,
                    dustOptions,
                    true
                )

                for (player in GeneralUtils.getNearbyPlayers(e.player)) {

                    if ( Nodes.getResident(player)?.nation !== nation ) continue

                    if (firstLoop){
                        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5F, 2.0F)
                    }

                    player.spawnParticle(
                        Particle.DUST,
                        raytraceLocation,
                        2,
                        offsetX, offsetY, offsetZ,
                        0.0,
                        dustOptions,
                        true
                    )
                }

                firstLoop = false

                if (timer <= 0) {
                    cancel() // Cancels this task.
                }

                timer--
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }
}