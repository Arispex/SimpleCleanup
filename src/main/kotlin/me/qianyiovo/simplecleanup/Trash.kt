package me.qianyiovo.simplecleanup

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import kotlin.math.max


class Trash(private val player: Player, var page: Int = 1) : InventoryHolder {
    private val inv: Inventory = Bukkit.createInventory(this, 9 * 6, "${Global.config.getString("Title")} - ${if (player.locale.startsWith("zh")) "第${page}页" else "page $page"}")
    private val totalPage = max(1, (Global.items.size + 44) / 45)

    var previousPageArrow: ItemStack
    var nextPageArrow: ItemStack
    var previousPageBarrier: ItemStack
    var nextPageBarrier: ItemStack

    init {
        val itemStacks = Global.items.map { it.itemStack }.take(45 * page).drop(45 * (page - 1))
        for (i in itemStacks.indices) {
            inv.setItem(i, itemStacks[i])
        }
        previousPageArrow = setLore(
            setDisplayName(
                ItemStack(Material.ARROW),
                "${ChatColor.GREEN}上一页",
                "${ChatColor.GREEN}Previous Page"
            ),
            listOf("${ChatColor.GREEN}点击切换到上一页"),
            listOf("${ChatColor.GREEN}Click to switch to the previous Page")
        )
        nextPageArrow = setLore(
            setDisplayName(
                ItemStack(Material.ARROW),
                "${ChatColor.GREEN}下一页",
                "${ChatColor.GREEN}Next Page"
            ),
            listOf("${ChatColor.GREEN}点击切换到下一页"),
            listOf("${ChatColor.GREEN}Click to switch to the next Page")
        )

        previousPageBarrier = setLore(
            setDisplayName(
                ItemStack(Material.BARRIER),
                "${ChatColor.RED}上一页",
                "${ChatColor.RED}Previous Page"
            ), listOf("${ChatColor.RED}已经是第一页了"), listOf("${ChatColor.RED}This is the first page")
        )
        nextPageBarrier = setLore(
            setDisplayName(ItemStack(Material.BARRIER), "${ChatColor.RED}下一页", "${ChatColor.RED}Next Page"),
            listOf("${ChatColor.RED}已经是最后一页了"),
            listOf("${ChatColor.RED}This is the last page")
        )

        inv.setItem(48, if (page == 1) previousPageBarrier else previousPageArrow)
        inv.setItem(50, if (page == totalPage) nextPageBarrier else nextPageArrow)
    }

    override fun getInventory(): Inventory {
        return inv
    }

    private fun setLore(itemStack: ItemStack, zhList: List<String>, enList: List<String>): ItemStack {
        val meta = itemStack.itemMeta
        meta?.lore = if (player.locale.startsWith("zh")) zhList else enList
        itemStack.itemMeta = meta
        return itemStack
    }

    private fun setDisplayName(itemStack: ItemStack, zhName: String, enName: String): ItemStack {
        val meta = itemStack.itemMeta
        meta?.setDisplayName(if (player.locale.startsWith("zh")) zhName else enName)
        itemStack.itemMeta = meta
        return itemStack
    }
}
