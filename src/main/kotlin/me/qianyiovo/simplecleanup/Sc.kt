package me.qianyiovo.simplecleanup

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Sc(val plugin: SimpleCleanup) : CommandExecutor, TabCompleter {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        if (p0 !is Player) {
            p0.sendMessage("You can only use this command in the game")
            return true
        }
        if (p3.isEmpty()) {
            p0.sendLocalizedCommandNotFound()
            return true
        }
        if (p3[0] == "cleanup") {
            if (!p0.hasPermission("sc.command.cleanup")) {
                p0.sendLocalizedPermissionErrorMessage()
                return true
            }
            plugin.cleanup()
            return true
        }
        if (p3[0] == "reload") {
            if (!p0.hasPermission("sc.command.reload")) {
                p0.sendLocalizedPermissionErrorMessage()
                return true
            }
            plugin.reloadConfig()
            plugin.customConfig = plugin.config
            Global.config = plugin.config
            p0.sendLocalizedMessage("${ChatColor.WHITE}[${plugin.customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.GREEN}已重载配置文件",
                "${ChatColor.WHITE}[${plugin.customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.GREEN}Reloaded config")
            return true
        }
        if (p3[0] == "trash") {
            if (!p0.hasPermission("sc.command.trash")) {
                p0.sendLocalizedPermissionErrorMessage()
                return true
            }
            p0.openInventory(Trash(p0).inventory)
            return true
        }
        p0.sendLocalizedCommandNotFound()
        return true
    }

    override fun onTabComplete(
        p0: CommandSender,
        p1: Command,
        p2: String,
        p3: Array<out String>
    ): MutableList<String> {
        val list = mutableListOf<String>()
        if (p3.size == 1) {
            if (p0.hasPermission("sc.command.cleanup"))
            {
                list.add("cleanup")
            }
            if (p0.hasPermission("sc.command.reload"))
            {
                list.add("reload")
            }
            if (p0.hasPermission("sc.command.trash"))
            {
                list.add("trash")
            }
            return list
        }
        return list
    }
}