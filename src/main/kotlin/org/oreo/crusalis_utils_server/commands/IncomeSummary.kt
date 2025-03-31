package org.oreo.crusalis_utils_server.commands

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import phonon.nodes.Nodes.getResident

class IncomeSummary : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (commandSender !is Player) {
            commandSender.sendMessage("Only players can use this command")
            return true // Exit early if the sender is not a player
        }

        val sender = commandSender
        val resident = getResident(sender)

        if (resident!!.town == null) {
            commandSender.sendMessage(ChatColor.RED.toString() + "You need to be part of a town to use this command")
            return true
        }

        // Make sure the player is officer or above
        if (!resident.town!!.officers.contains(resident)) {
            checkNotNull(resident.town!!.leader)
            if (resident.town!!.leader != resident) {
                sender.sendMessage(ChatColor.RED.toString() + "Only officers and above can use this command")
                return true // Exit early if the player is not an officer or leader
            }
        }

        val inventory = resident.town!!.income.inventory.storageContents

        sender.sendMessage(ChatColor.AQUA.toString() + "------Income summary------")

        var prevStackMaterial: Material? = null
        var prevStackAmount = 0

        for (stack in inventory) {
            if (stack != null && stack.type != Material.AIR) { // Check for valid stack

                if (prevStackMaterial == null || prevStackMaterial != stack.type) {
                    // If we're moving to a new item type, display the previous one
                    if (prevStackMaterial != null) {
                        sender.sendMessage(ChatColor.AQUA.toString() + prevStackMaterial.toString() + " - " + prevStackAmount)
                    }
                    // Start counting the new item type
                    prevStackMaterial = stack.type
                    prevStackAmount = stack.amount
                } else {
                    // If it's the same item type, just add the amounts
                    prevStackAmount += stack.amount
                }
            }
        }

        // Output the last item stack summary if it exists
        if (prevStackMaterial != null) {
            sender.sendMessage(ChatColor.AQUA.toString() + prevStackMaterial.toString() + " - " + prevStackAmount)
        } else {
            sender.sendMessage(ChatColor.AQUA.toString() + "No items found in the income inventory.")
        }

        return true
    }
}
