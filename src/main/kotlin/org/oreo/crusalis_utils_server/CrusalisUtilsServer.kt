package org.oreo.crusalis_utils_server

import org.bukkit.plugin.java.JavaPlugin
import org.oreo.crusalis_utils_server.commands.AnnounceCommand
import org.oreo.crusalis_utils_server.commands.CrusalisUtilsCommands
import org.oreo.crusalis_utils_server.commands.IncomeSummary
import org.oreo.crusalis_utils_server.events.ItemClickingListener
import org.oreo.crusalis_utils_server.events.ItemHoldingListener
import org.oreo.crusalis_utils_server.item.ItemManager


class CrusalisUtilsServer : JavaPlugin() {

    override fun onEnable() {

        logger.info("Crusalis utils enabled")
        logger.info("Millions must download crusalis utils")

        getCommand("nation-announcements")!!.setExecutor(AnnounceCommand())
        getCommand("income")!!.setExecutor(IncomeSummary())
        getCommand("cu")!!.setExecutor(CrusalisUtilsCommands())

        ItemManager.init( this )

        enableListeners()

        saveDefaultConfig()
    }

    private fun enableListeners(){
        server.pluginManager.registerEvents(ItemHoldingListener(this), this)
        server.pluginManager.registerEvents(ItemClickingListener(this), this)
    }


    override fun onDisable() {
        logger.info("Crusalis utils disabled")
        logger.info("Oreo's code is terrible anyway")
    }
}
