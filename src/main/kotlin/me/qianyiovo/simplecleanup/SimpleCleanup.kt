package me.qianyiovo.simplecleanup
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class SimpleCleanup : JavaPlugin(){
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
        // 提示掉落物将在1分钟后清理
        object : BukkitRunnable() {
            override fun run() {
                server.onlinePlayers.forEach {
                    it.sendLocalizedMessage(
                        "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.RED}一分钟后清理掉落物",
                        "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.RED}The dropped items will be cleared in 1 minute"
                    )
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
        val removeItems = server.worlds
            .flatMap { it.entities }
            .filterIsInstance<Item>()
            .onEach { it.remove() }
        Global.items.addAll(removeItems)
        server.onlinePlayers.forEach{it.sendLocalizedMessage("${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.GREEN}已清理${removeItems.count()}个掉落物",
           "${ChatColor.WHITE}[${customConfig.getString("Prefix")}${ChatColor.WHITE}]${ChatColor.GREEN}${removeItems.count()} dropped items have been cleared")}
    }
}
fun Player.sendLocalizedMessage(zh: String, en: String) {
    if (locale.startsWith("zh")) {
        sendMessage(zh)
    }
    else {
        sendMessage(en)
    }
}
fun Player.sendLocalizedPermissionErrorMessage() {
    sendLocalizedMessage("${ChatColor.RED}你没有权限访问此命令", "${ChatColor.RED}Yon don't have permission to access this command")
}
fun Player.sendLocalizedCommandNotFound() {
    sendLocalizedMessage("${ChatColor.RED}不存在此命令", "${ChatColor.RED}Command not found")
}