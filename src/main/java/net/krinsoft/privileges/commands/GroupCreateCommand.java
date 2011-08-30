package net.krinsoft.privileges.commands;

import java.util.ArrayList;
import java.util.List;
import net.krinsoft.privileges.Privileges;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 *
 * @author krinsdeath
 */
public class GroupCreateCommand extends PrivilegesCommand {

    public GroupCreateCommand(Privileges plugin) {
        super(plugin);
        this.plugin = (Privileges) plugin;
        this.setName("privileges group create");
        this.setCommandUsage("/privileges group create [name] [rank]");
        this.addCommandExample("/pgc ? -- show command help");
        this.addCommandExample("/pgc admin 10 -- creates the 'admin' group at rank 10");
        this.setArgRange(2, 2);
        this.addKey("privileges group create");
        this.addKey("priv group create");
        this.addKey("pg create");
        this.addKey("pgc");
        this.setPermission("privileges.group.create", "Allows the user to create new groups.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Configuration groups = plugin.getGroups();
        if (groups.getKeys("groups").contains(args.get(0))) {
            // already have a group called that!
            sender.sendMessage("Groups can have any name you want EXCEPT ones that already exist.");
            return;
        }
        int rank = 0;
        try {
            rank = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
            // sender provided an invalid argument
            sender.sendMessage("Numbers are fun.");
            return;
        }
        for (String key : groups.getKeys("groups")) {
            if (groups.getInt("groups." + key + ".rank", 0) == rank) {
                // duplicate rank found
                sender.sendMessage("A group with that rank already exists.");
                return;
            }
        }
        // check that the user can create a group of this rank
        if (rank > plugin.getGroupManager().getHighestRank(sender)) {
            // sender is trying to be a bad boy!
            sender.sendMessage("That rank is too high for you.");
            return;
        }
        // alright, group name is unique and rank isn't used!
        groups.setProperty("groups." + args.get(0) + ".rank", rank);
        groups.setProperty("groups." + args.get(0) + ".permissions", null);
        groups.setProperty("groups." + args.get(0) + ".worlds", null);
        groups.setProperty("groups." + args.get(0) + ".inheritance", null);
        groups.save();
    }

}
