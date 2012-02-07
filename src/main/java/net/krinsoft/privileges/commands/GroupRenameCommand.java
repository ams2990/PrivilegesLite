package net.krinsoft.privileges.commands;

import net.krinsoft.privileges.Privileges;
import net.krinsoft.privileges.groups.GroupManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class GroupRenameCommand extends GroupCommand {
    
    public GroupRenameCommand(Privileges plugin) {
        super(plugin);
        setName("Privileges: Group Rename");
        setCommandUsage("/priv group rename [group] [new name]");
        setArgRange(2, 2);
        addKey("privileges group rename");
        addKey("priv group rename");
        addKey("pg rename");
        addKey("pg ren");
        setPermission("privileges.group.rename", "Allows this user to rename groups.", PermissionDefault.OP);
    }
    
    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (groupManager.getGroup(args.get(0)) == null || groupManager.getGroup(args.get(1)) != null) {
            sender.sendMessage(ChatColor.RED + "Invalid group(s).");
            return;
        }
        if (groupManager.getRank(sender) <= groupManager.getGroup(args.get(0)).getRank() && !sender.hasPermission("privileges.self.edit")) {
            sender.sendMessage(ChatColor.RED + "That group's rank is too high.");
            return;
        }
        String o = args.get(0);
        String n = args.get(1);
        for (String group : plugin.getGroups().getConfigurationSection("groups").getKeys(false)) {
            List<String> inherit = plugin.getGroupNode(group).getStringList("inheritance");
            if (inherit.contains(o)) {
                inherit.remove(o);
                inherit.add(n);
                plugin.getGroupNode(group).set("inheritance", inherit);
            }
        }
        plugin.saveGroups();
        for (String user : plugin.getUsers().getConfigurationSection("users").getKeys(false)) {
            String group = plugin.getUserNode(user).getString("group");
            if (group.equalsIgnoreCase(o)) {
                plugin.getUserNode(user).set("group", n);
            }
        }
        plugin.saveUsers();
        if (groupManager.getGroup(o).equals(groupManager.getDefaultGroup())) {
            plugin.getConfig().set("default_group", n);
            plugin.saveConfig();
        }
        plugin.registerPermissions();
        sender.sendMessage("'" + colorize(ChatColor.GREEN, o) + "' has been renamed to '" + colorize(ChatColor.GREEN, n) + "'");
        plugin.log(sender.getName() + " has renamed the group '" + o + "' to '" + n + "'");
    }
}