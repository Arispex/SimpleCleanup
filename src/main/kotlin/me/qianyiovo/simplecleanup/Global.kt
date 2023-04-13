package me.qianyiovo.simplecleanup
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Item

object Global {
    val items: MutableList<Item> = mutableListOf()
    lateinit var config: FileConfiguration
}