package me.nikolchev98.toggledeath;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ToggleDeath extends JavaPlugin implements Listener {
    private List<Player> playersWithDisabledDeathMessages;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        playersWithDisabledDeathMessages = new ArrayList<>();
        getCommand("dm").setExecutor(new ToggleDeathCommand(this));
    }

    public boolean areDeathMessagesEnabledForPlayer(Player player) {
        return !playersWithDisabledDeathMessages.contains(player);
    }

    public void setDeathMessagesEnabledForPlayer(Player player, boolean enabled) {
        if (enabled) {
            playersWithDisabledDeathMessages.remove(player);
        } else {
            playersWithDisabledDeathMessages.add(player);
        }
    }

    public class ToggleDeathCommand implements org.bukkit.command.CommandExecutor {
        private ToggleDeath plugin;

        public ToggleDeathCommand(ToggleDeath plugin) {
            this.plugin = plugin;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                boolean areDeathMessagesEnabled = plugin.areDeathMessagesEnabledForPlayer(player);
                plugin.setDeathMessagesEnabledForPlayer(player, !areDeathMessagesEnabled);
                player.sendMessage(ChatColor.GOLD + "Death messages " + (areDeathMessagesEnabled ? ChatColor.GREEN + "disabled" : ChatColor.RED + "enabled") + ".");
                return true;
            }
            return false;
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Get the player who died
        Player deadPlayer = event.getEntity();

        // Get the death message
        String deathMessage = event.getDeathMessage();

        // Set the death message to null
        event.setDeathMessage(null);

        // Send a message to all other players that the player died
        for (Player player : getServer().getOnlinePlayers()) {

            if (areDeathMessagesEnabledForPlayer(player)) {
                player.sendMessage(deathMessage);
            }
        }
    }
}