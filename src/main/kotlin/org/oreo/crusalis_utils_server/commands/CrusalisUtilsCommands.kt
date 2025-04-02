package org.oreo.crusalis_utils_server.commands


import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.oreo.crusalis_utils_server.item.ItemManager


class CrusalisUtilsCommands : CommandExecutor, TabCompleter {

    private val SUBCOMMANDS = listOf(
        "help"
    )

    private val OP_SUBCOMMANDS = listOf(
        "commstick",
        "teamInfo",
    )


    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {

        val arg = args[0].lowercase()

        when (arg){
            "help" -> {
                helpCommand(sender, args)
                return true
            }
        }

        if (sender.isOp) {
            when (arg){
                "commstick" -> {
                    if (sender is Player) {
                        sender.inventory.addItem(ItemManager.teleComunicationSctick!!)
                    }
                    return true
                }
                "teaminfo" -> {
                    if (sender is Player) {
                        sender.inventory.addItem(ItemManager.playerTeamInfo!!)
                        return true
                    }
                }
            }
        }

        sender.sendMessage("§cErroneous subcommand")
        return true
    }



    private fun helpCommand(sender: CommandSender, args: Array<String>) {

    }

    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<out String>?
    ): MutableList<String> {
        return if (p0.isOp){
            (SUBCOMMANDS + OP_SUBCOMMANDS).toMutableList()
        } else {
            SUBCOMMANDS.toMutableList()
        }
    }
}