package github.hywinapi.ClassUtils;

import github.hywinapi.HywinAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;

public class CommandAPI implements CommandExecutor, TabCompleter {

    private Map<String, CommandAPI> commandRegistred = new HashMap<>();
    private Map<String, CommandAPI> commands = new HashMap<>();
    private static Map<String, String> replacers = new LinkedHashMap<>();
    private Plugin plugin = HywinAPI.getHywinInstance();
    private String name;
    private String permission;
    private String defaultPermission;
    private String permissionMessage;
    private String description;
    private String usage;
    private List<String> aliases = new ArrayList<>();
    private CommandAPI parent;
    private Boolean playerOnly;
    private String playerOnlyMessage;

    public Map<String, CommandAPI> getCommands() {
        return commands;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String labela, String[] args) {
        CommandAPI commandAPI = this;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toLowerCase();
            CommandAPI sub = null;
            for (CommandAPI subcommand : commandAPI.getCommands().values()) {
                if (subcommand.getName().equalsIgnoreCase(arg)) {
                    sub = subcommand;
                }
                for (String alias : subcommand.getAliases()) {
                    if (alias.equalsIgnoreCase(arg)) {
                        sub = subcommand;
                    }
                }
            }
            if (sub != null) {
                commandAPI = sub;
            }
        }
        if(commandAPI.getPlayerOnly() && sender instanceof ConsoleCommandSender){
            sender.sendMessage(getPlayerOnlyMessage());
            return true;
        }
        if(commandAPI == this){
            return true;
        }
        if (!sender.hasPermission(commandAPI.getPermission()) && !sender.hasPermission(commandAPI.getDefaultPermission())) {
            sender.sendMessage(commandAPI.getPermissionMessage());
            return true;
        }
        commandAPI.onCommand(sender, command, labela, args);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        CommandAPI cmd = this;
        List<String> vars = null;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toLowerCase();
            CommandAPI sub = null;
            vars = new ArrayList<>();
            for (CommandAPI subcommand : cmd.getCommands().values()) {
                if (sender.hasPermission(subcommand.getPermission()) && sender.hasPermission(subcommand.getDefaultPermission())) {
                    if (subcommand.toString().toLowerCase().startsWith(arg)) {
                        vars.add(subcommand.getName());
                    }
                    if (subcommand.getName().equalsIgnoreCase(arg)) {
                        sub = subcommand;
                    }
                    for (String alias : subcommand.getAliases()) {
                        if (alias.toLowerCase().startsWith(arg)) {
                            vars.add(alias);
                        }
                        if (alias.equalsIgnoreCase(arg)) {
                            sub = subcommand;
                        }
                    }
                }
            }
            if (sub != null) {
                cmd = sub;
            }
        }
        List<String> finalVars = vars;
        this.aliases.forEach(finalVars::remove);
        return finalVars;
    }

    public void register(){
        PluginCommand command = Bukkit.getPluginCommand(getName());
        if(command == null){
            Bukkit.getConsoleSender().sendMessage("§a[Command] - §3The command §f" + getName() + " §fwas not registered in any plugin");
        }
        setPlugin(command.getPlugin());
        command.setAliases(getAliases());
        command.setName(getName());
        command.setPermission(getPermission());
        command.setPermissionMessage(getPermissionMessage());
        command.setDescription(getDescription());
        //command.setUsage(usage);
        command.setLabel(getName());
        command.setExecutor(this);
        Bukkit.getConsoleSender().sendMessage("§a[Command] - §3The command §f" + getName() + " §3has been registered to the plugin §f" + command.getPlugin().getName());
        commandRegistred.put(getName().toLowerCase(), this);
        updateSubs();
    }

    public void registerCommand(Plugin plugin){
        Command command = new Command(getName()) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                if (!plugin.isEnabled()) {
                    return false;
                }
                if (!sender.hasPermission(getPermission()) && !sender.hasPermission(getDefaultPermission())) {
                    sender.sendMessage(getPermissionMessage());
                    return true;
                }
                return onCommand(sender, this, commandLabel, args);
            }
        };
        command.setAliases(getAliases());
        command.setDescription(getDescription());
        command.setLabel(getName());
        command.setName(getName());
        command.setPermission(getPermission());
        command.setPermissionMessage(getPermissionMessage());
        command.setUsage(getUsage());
        createCommand(plugin, command);
        Bukkit.getConsoleSender().sendMessage("§a[Command] - §3The command §f" + getName() + " §3has been registered");
    }

    public void createCommand(Plugin plugin, Command... commands) {
        try {
            Class<?> serverClass = getClassFrom(Bukkit.getServer());
            Field field = serverClass.getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap map = (CommandMap) field.get(Bukkit.getServer());
            for (Command cmd : commands) {
                map.register(plugin.getName(), cmd);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Class<?> getClassFrom(Object object) throws Exception {
        if (object instanceof Class) {
            return (Class<?>) object;
        }
        if (object instanceof String) {
            String string = (String) object;
            if (string.startsWith("#")) {
                for (Map.Entry<String, String> entry : replacers.entrySet()) {
                    string = string.replace(entry.getKey(), entry.getValue());
                }
                return Class.forName(string);
            }
        }
        try {
            return (Class<?>) object.getClass().getField("TYPE").get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object.getClass();
    }

    public boolean addSub(CommandAPI sub) {
        commands.put(sub.name, sub);
        sub.setParent(this);
        return true;
    }

    public void updateSubs(){
        for (CommandAPI sub : commands.values()) {
            Bukkit.getConsoleSender().sendMessage("§a[Command] - §3Subcommand §f" + sub.name + " §3has been registered for command §f" + name);
            if (!sub.commands.isEmpty())
                sub.updateSubs();
        }
    }

    public String getCommandName() {
        return getClass().getSimpleName().toLowerCase().replace("sub", "").replace("subcommand", "")
                .replace("comando", "").replace("command", "").replace("cmd", "");
    }

    public Boolean getPlayerOnly() {
        if(playerOnly == null) return false;
        return playerOnly;
    }

    public String getPlayerOnlyMessage(){
        if(playerOnlyMessage == null){
            return "This command can only be executed by one player.";
        }
        return playerOnlyMessage;
    }

    public String getName() {
        if(name == null){
            name = getCommandName();
        }
        return name;
    }

    public String getPermission() {
        if(permission == null) {
            permission = name + ".use";
        }
        return permission;
    }

    public String getDefaultPermission(){
        if(defaultPermission == null) {
            defaultPermission = name + ".use";
        }
        return defaultPermission;
    }

    public String getPermissionMessage() {
        if(permissionMessage == null){
            permissionMessage = "§cYou do not have the §e" + permission + " §cpermission to run this command.";
        }
        return permissionMessage;
    }

    public String getDescription() {
        if(description == null){
            description = "Default of command server.";
        }
        return description;
    }

    public String getUsage() {
        if(usage == null){
            usage = "Use /" + name;
        }
        return usage;
    }

    public List<String> getAliases() {
        return aliases;
    }

    private CommandAPI getParent() {
        return parent;
    }

    private Plugin getPlugin() {
        return plugin;
    }

    public void setPlayer_only(Boolean player_only) {
        this.playerOnly = player_only;
    }

    public void setPlayerOnlyMessage(String player_onlyMessage){
        this.playerOnlyMessage = player_onlyMessage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setDefaultPermission(String defaultPermission) {
        this.defaultPermission = defaultPermission;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setAliases(String... aliases) {
        if(aliases != null){
            this.aliases = Arrays.asList(aliases);
        }
    }

    public void setPermissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
    }
    private void setParent(CommandAPI parent) {
        this.parent = parent;
    }

    private void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

}