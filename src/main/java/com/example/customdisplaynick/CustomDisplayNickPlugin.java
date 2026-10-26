package com.example.customdisplaynick;

import org.bukkit.plugin.java.JavaPlugin;

public final class CustomDisplayNickPlugin extends JavaPlugin {

    private static CustomDisplayNickPlugin instance;
    private DisplayNickManager nickManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.nickManager = new DisplayNickManager(this);

        DisplayNickCommand commandExecutor = new DisplayNickCommand(this);
        if (getCommand("displaynick") != null) {
            getCommand("displaynick").setExecutor(commandExecutor);
            getCommand("displaynick").setTabCompleter(commandExecutor);
        }

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        getLogger().info("CustomDisplayNick успешно включен!");
    }

    @Override
    public void onDisable() {
        if (nickManager != null) {
            nickManager.cleanupAll();
        }
        getLogger().info("CustomDisplayNick выключен.");
    }

    public static CustomDisplayNickPlugin getInstance() {
        return instance;
    }

    public DisplayNickManager getNickManager() {
        return nickManager;
    }
}
