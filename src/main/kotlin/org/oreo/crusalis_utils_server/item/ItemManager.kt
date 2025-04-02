package org.oreo.crusalis_utils_server.item

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

object ItemManager {
    private var plugin: JavaPlugin? = null

    const val CRUSALIS_UTILS_KEY = "Crusalis_utils"

    var teleComunicationSctick: ItemStack? = null
    var playerTeamInfo: ItemStack? = null


    /**
     * Item initialisation
     */
    fun init(pluginInstance: JavaPlugin?) {
        plugin = pluginInstance
        createItems()
    }

    /**
     * Creates all the custom items
     * Keep in mind all device items have an odd number for custom model data and every controller has an even number
     */
    private fun createItems() {
        teleComunicationSctick = createCustomItem("§e§lTelecomunication Shtick",20,Material.STICK)
        playerTeamInfo = createCustomItem("§e§lPlayer info",20, Material.ORANGE_DYE)
    }


    /**
     * Creates a controller item
     * @param name The controller item name
     * @param deviceName used for the unique namespaceKey and other things
     * @return the finalised item
     */
    private fun createCustomItem(name : String , modelData: Int, material:Material) : ItemStack{

        val item = ItemStack(material, 1)
        val meta = item.itemMeta


        if (meta != null) {
            meta.setDisplayName(name) // Should start with §7

            meta.setCustomModelData(modelData)

            val lore: MutableList<String> = ArrayList()
            lore.add("§5Basically a laser pointer :3") //The funni

            meta.lore = lore

            meta.addEnchant(Enchantment.MENDING, 1, false)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

            val data = meta.persistentDataContainer
            val key = NamespacedKey(plugin!!, CRUSALIS_UTILS_KEY) //deviceName_control
            data.set(key, PersistentDataType.STRING, UUID.randomUUID().toString())

            item.setItemMeta(meta)
        }
        return item
    }



    /**
     * A few simple checks to see if a player is holding a custom item
     * These methods are in the ItemManager because it simply makes more sense to me
     */

    /**
     * Checks if the player is holding a specific custom item.
     *
     * @param player the player whose held item is to be checked
     * @param item the custom item to check against
     * @return true if the player is holding the specified custom item, false otherwise
     */
    fun isHoldingCustomItem(player: Player, item: ItemStack? ): Boolean {

        if (item == null) return false

        val itemInHand: ItemStack = player.inventory.itemInMainHand

        return isCustomItem(item = itemInHand , itemToCheck = item)
    }


    /**
     * Checks if a given item is a specific custom item.
     *
     * @param item the item to be checked
     * @param itemToCheck the custom item to check against
     * @return true if the given item matches the custom item, false otherwise
     */
    public fun isCustomItem(item: ItemStack , itemToCheck : ItemStack?): Boolean {

        if (itemToCheck == null) {
            return false
        }

        if (item.type != itemToCheck.type) {
            return false
        }

        val itemMeta = item.itemMeta
        val customItemMeta = itemToCheck.itemMeta

        if (itemMeta == null || customItemMeta == null) {
            return false
        }

        if (itemMeta.displayName != customItemMeta.displayName) {
            return false
        }

        if (itemMeta.enchants != customItemMeta.enchants) {
            return false
        }

        return true
    }
}
