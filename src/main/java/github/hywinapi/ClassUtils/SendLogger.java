package github.hywinapi.ClassUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SendLogger {

    public static void sendConsole(LoggerType typeLogger, String message){
        Bukkit.getConsoleSender().sendMessage(typeLogger.name + message.replaceAll("&", "§"));
    }

    public static void sendPlayer(LoggerType typeLogger, String message, Player getPlayer){
        getPlayer.sendMessage(typeLogger.name + message.replaceAll("&", "§"));
    }

    public enum LoggerType{
        WARN("§6[Hywin] §r"),
        ERROR("§4[Hywin] §r"),
        SUCCESS("§a[Hywin] §r");

        final String name;


        LoggerType(String type) {
            this.name = type;
        }
    }

}
