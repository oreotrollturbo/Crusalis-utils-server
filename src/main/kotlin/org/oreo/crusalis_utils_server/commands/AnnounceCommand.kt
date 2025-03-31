package org.oreo.crusalis_utils_server.commands

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.oreo.crusalis_utils_server.Crusalis_utils_server
import phonon.nodes.Nodes.getResident
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.*

class AnnounceCommand(plugin: JavaPlugin, plugin1: Crusalis_utils_server) : CommandExecutor, TabCompleter {
    private var nationPlayersMap: MutableMap<String, MutableList<String>> = HashMap()

    // Use the plugin's data folder to store the JSON file
    private val saveFile = File(plugin.dataFolder, "nationPlayersMap.json")
    private val gson = Gson()
    private val plugin = plugin1

    init {
        loadNationPlayersMap()
    }

    override fun onCommand(commandSender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (commandSender !is Player) {
            commandSender.sendMessage(ChatColor.RED.toString() + "You can't use this command in the console!")
            return false
        }

        val sender = commandSender
        val resident = getResident(sender)

        if (resident?.nation == null) {
            sender.sendMessage(ChatColor.RED.toString() + "You are not part of any nation!")
            return false
        }

        val nation = resident.nation!!

        // Check if the sender is the nation leader
        if (nation.capital.leader!!.name != resident.name) {
            sender.sendMessage(ChatColor.RED.toString() + "You aren't a nation leader.")
            return false
        }

        if (args.size < 1) {
            sender.sendMessage(ChatColor.RED.toString() + "You need to provide a subcommand (announce/add/remove/clear/list).")
            return false
        }

        val subCommand = args[0].lowercase(Locale.getDefault())
        val nationName = nation.name // Use the nation name as the key

        when (subCommand) {
            "announce" -> {
                if (args.size < 2) {
                    sender.sendMessage(ChatColor.RED.toString() + "Please provide a message to announce.")
                    return false
                }

                val message = java.lang.String.join(" ", *Arrays.copyOfRange(args, 1, args.size))
                for (playerName in nationPlayersMap.getOrDefault(nationName, emptyList())) {
                    val player = sender.server.getPlayer(playerName)
                    if (player != null && player.isOnline) {
                        player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 2.0f, 0.5f)
                        sendActionBar(player, message)
                    }
                }
            }

            "add" -> {
                if (args.size < 2) {
                    sender.sendMessage(ChatColor.RED.toString() + "Please provide a player name to add.")
                    return false
                }

                val playerNameToAdd = args[1]
                val playerToAdd = commandSender.getServer().getPlayer(playerNameToAdd)

                if (playerToAdd == null) {
                    sender.sendMessage(ChatColor.RED.toString() + "Player not found or not online.")
                    return false
                }

                val playersListToAdd = nationPlayersMap.computeIfAbsent(nationName) { k: String? -> ArrayList() }

                if (playersListToAdd.contains(playerNameToAdd)) {
                    sender.sendMessage(ChatColor.RED.toString() + "Player is already added to the list.")
                } else {
                    playersListToAdd.add(playerNameToAdd)
                    saveNationPlayersMap() // Save after adding
                    sender.sendMessage(ChatColor.GREEN.toString() + playerToAdd.name + " has been added to your nation's announcement list.")
                }
            }

            "remove" -> {
                if (args.size < 2) {
                    sender.sendMessage(ChatColor.RED.toString() + "Please provide a player name to remove.")
                    return false
                }

                val playerNameToRemove = args[1]
                val playersListToRemove = nationPlayersMap[nationName]

                if (playersListToRemove == null || !playersListToRemove.contains(playerNameToRemove)) {
                    sender.sendMessage(ChatColor.RED.toString() + "Player is not in the list.")
                } else {
                    playersListToRemove.remove(playerNameToRemove)
                    saveNationPlayersMap() // Save after removing
                    sender.sendMessage(ChatColor.GREEN.toString() + playerNameToRemove + " has been removed from your nation's announcement list.")
                }
            }

            "clear" -> {
                // Clear the entire list for this nation
                val playersListToClear = nationPlayersMap[nationName]

                if (playersListToClear != null) {
                    playersListToClear.clear()
                    saveNationPlayersMap() // Save after clearing
                    sender.sendMessage(ChatColor.GREEN.toString() + "All players have been removed from your nation's announcement list.")
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + "There are no players in the list to clear.")
                }
            }

            "list" -> {
                // List all players currently added
                val playersList: List<String>? = nationPlayersMap[nationName]

                if (playersList == null || playersList.isEmpty()) {
                    sender.sendMessage(ChatColor.RED.toString() + "No players are currently added to your nation's announcement list.")
                } else {
                    sender.sendMessage(ChatColor.GREEN.toString() + "Players in your nation's announcement list:")
                    for (playerName in playersList) {
                        sender.sendMessage(ChatColor.AQUA.toString() + "- " + playerName)
                    }
                }
            }

            else -> {
                sender.sendMessage(ChatColor.RED.toString() + "Unknown subcommand. Use 'announce', 'add', 'remove', 'clear', or 'list'.")
                return false
            }
        }

        return true
    }

    override fun onTabComplete(
        commandSender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String>? {
        val completions: MutableList<String> = ArrayList()

        if (commandSender !is Player) {
            return completions
        }

        val resident = getResident(commandSender)

        if (resident?.nation == null) {
            return completions
        }

        val nation = resident.nation!!
        val nationName = nation.name // Use the nation name as the key

        if (nation.capital.leader!!.name != resident.name) {
            return completions
        }

        if (args.size == 1) {
            completions.add("announce")
            completions.add("add")
            completions.add("remove")
            completions.add("clear")
            completions.add("list")
        } else if (args.size == 2) {
            if (args[0].equals("add", ignoreCase = true)) {
                // Autocomplete player names within the nation who are online
                for (player in nation.playersOnline) {
                    completions.add(player.name)
                }
            } else if (args[0].equals("remove", ignoreCase = true)) {
                // Autocomplete only players that are already in the nation's list
                val playersList: List<String>? = nationPlayersMap[nationName]
                if (playersList != null) {
                    completions.addAll(playersList)
                }
            }
        }

        return completions
    }

    // Method to send a message above the player's hotbar
    fun sendActionBar(player: Player, message: String) {
        val formattedMessage = ChatColor.AQUA.toString() + "" + ChatColor.BOLD + message.uppercase(Locale.getDefault())
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(formattedMessage))
    }

    private fun loadNationPlayersMap() {
        if (!saveFile.exists()) {
            initializeSaveFile() // Create the file with default content if it does not exist
        }

        try {
            FileReader(saveFile).use { reader ->
                val mapType = object : TypeToken<Map<String?, List<String?>?>?>() {}.type
                val loadedMap = gson.fromJson<MutableMap<String, MutableList<String>>>(reader, mapType)
                if (loadedMap != null) {
                    nationPlayersMap = loadedMap
                }
            }
        } catch (e: IOException) {
            plugin.logger.info("File not found creating save file")
            initializeSaveFile()
            // Handle file read errors, such as file not being found or inaccessible
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            // Handle JSON syntax errors, possibly reset file or log error
        }
    }

    // Save the nationPlayersMap to file
    private fun saveNationPlayersMap() {
        try {
            FileWriter(saveFile).use { writer ->
                gson.toJson(nationPlayersMap, writer)
                plugin.logger.info("File saved successfully")
            }
        } catch (e: IOException) {
            plugin.logger.info("Save file does not exist")
            e.printStackTrace()
        }
    }

    private fun initializeSaveFile() {
        if (!saveFile.exists()) {
            try {
                if (saveFile.createNewFile()) {
                    plugin.logger.info("Created new file at: " + saveFile.absolutePath)
                    FileWriter(saveFile).use { writer ->
                        writer.write("{}") // Write an empty JSON object to the file
                        loadNationPlayersMap()
                    }
                }
            } catch (e: IOException) {
                plugin.logger.info("Cant create save file")
                e.printStackTrace()
                // Handle file creation errors
            }
        } else {
            plugin.logger.info("Save file found")
        }
    }
}
