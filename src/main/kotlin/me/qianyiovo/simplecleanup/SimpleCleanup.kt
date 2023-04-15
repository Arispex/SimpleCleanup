package me.qianyiovo.simplecleanup

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class SimpleCleanup : JavaPlugin() {
    lateinit var customConfig: FileConfiguration
    override fun onEnable() {
        saveDefaultConfig()
        Global.config = config
        customConfig = config
        getCommand("sc")?.setExecutor(Sc(this))
        getCommand("sc")?.tabCompleter = Sc(this)
        server.pluginManager.registerEvents(TrashListener(), this)
        scheduleCleanTask()
    }

    override fun onDisable() {
    }

    private fun scheduleCleanTask() {
        val notification = customConfig.getHashMap("Notification")

        // 提示掉落物将在1分钟后清理
        val message = notification["Message"].toString().toBoolean()
        val actionBar = notification["ActionBar"].toString().toBoolean()

        object : BukkitRunnable() {
            override fun run() {
                if (message) {
                    server.onlinePlayers.forEach {
                        it.sendLocalizedMessage(
                            "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.RED}一分钟后清理掉落物",
                            "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.RED}The dropped items will be cleared in 1 minute"
                        )
                    }
                }
                if (actionBar) {
                    server.onlinePlayers.forEach {
                        it.sendLocalizedActionBarMessage(
                            "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.RED}一分钟后清理掉落物",
                            "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.RED}The dropped items will be cleared in 1 minute"
                        )
                    }
                }
            }
        }.runTaskLater(this@SimpleCleanup, customConfig.getLong("CleanupIntervalTickets") - 1200)

        // 实际清理掉落物
        object : BukkitRunnable() {
            override fun run() {
                cleanup()
                // 重新调度清理任务
                scheduleCleanTask()
            }
        }.runTaskLater(this@SimpleCleanup, customConfig.getLong("CleanupIntervalTickets"))
    }

    fun cleanup() {
        val notification = customConfig.getHashMap("Notification")

        val message = notification["Message"].toString().toBoolean()
        val actionBar = notification["ActionBar"].toString().toBoolean()

        val removeItems = server.worlds
            .flatMap { it.entities }
            .filterIsInstance<Item>()
            .onEach { it.remove() }
        Global.items.addAll(removeItems)
        if (message) {
            server.onlinePlayers.forEach {
                it.sendLocalizedMessage(
                    "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.GREEN}已清理${removeItems.count()}个掉落物",
                    "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.GREEN}${removeItems.count()} dropped items have been cleared"
                )
            }
        }
        if (actionBar) {
            server.onlinePlayers.forEach{
                it.sendLocalizedActionBarMessage(
                    "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.GREEN}已清理${removeItems.count()}个掉落物",
                    "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.GREEN}${removeItems.count()} dropped items have been cleared"
                )
            }
        }
    }
}

fun Player.sendLocalizedMessage(zh: String, en: String) {
    if (locale.startsWith("zh")) {
        sendMessage(zh)
    } else {
        sendMessage(en)
    }
}

fun Player.sendLocalizedPermissionErrorMessage() {
    sendLocalizedMessage(
        "${ChatColor.RED}你没有权限访问此命令",
        "${ChatColor.RED}Yon don't have permission to access this command"
    )
}

fun Player.sendLocalizedCommandNotFound() {
    sendLocalizedMessage("${ChatColor.RED}不存在此命令", "${ChatColor.RED}Command not found")
}

fun Player.sendLocalizedActionBarMessage(zhMessage: String, enMessage: String) {
    if (locale.startsWith("zh")) {
        spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(zhMessage))
    } else {
        spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(enMessage))
    }
}

fun Configuration.getHashMap(path: String): HashMap<String, Any> {
    val hashMap = hashMapOf<String, Any>()
    val configuration = getConfigurationSection(path)
    configuration?.getKeys(false)?.forEach { hashMap[it] = configuration.get(it) as Any }
    return hashMap
}