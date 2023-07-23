package github.hywinapi;

import github.hywinapi.ClassUtils.SendLogger;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class HywinAPI extends JavaPlugin {


    @Getter
    private static HywinAPI hywinInstance;

    @Override
    public void onEnable() {
        hywinInstance = this;
        SendLogger.sendConsole(SendLogger.LoggerType.SUCCESS, "§aAPI Successfully enabled.");
    }

    @Override
    public void onDisable() {
        SendLogger.sendConsole(SendLogger.LoggerType.SUCCESS, "§cAPI Successfully disable.");
    }
}
