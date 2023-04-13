package me.qianyiovo.simplecleanup

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class TrashListener: Listener {
    @EventHandler
    fun onInventoryClickedEvent(event: InventoryClickEvent) {
        if (event.clickedInventory?.holder is Trash) {
            val trash = event.clickedInventory?.holder as Trash
            val player = event.whoClicked as Player
            event.isCancelled = true
            when (event.currentItem) {
                trash.previousPageArrow -> {
                    player.openInventory(Trash(player, trash.page - 1).inventory)
                }
                trash.nextPageArrow -> {
                    player.openInventory(Trash(player, trash.page + 1).inventory)
                }
                trash.nextPageBarrier, trash.previousPageBarrier.itemMeta -> return
                else -> {
                    val item = Global.items.find { it.itemStack == event.currentItem } ?: kotlin.run {
                        player.sendLocalizedMessage("${ChatColor.RED}哎呀，这个物品已经被别人拿走了", "${ChatColor.RED}Oops, this item has already been taken by someone else")
                        return
                    }
                    Global.items.remove(item)
                    player.inventory.addItem(item.itemStack)
                    player.openInventory(Trash(player).inventory)
                }
            }
        }
    }
}