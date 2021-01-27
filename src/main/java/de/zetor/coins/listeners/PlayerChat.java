package de.zetor.coins.listeners;

import de.zetor.coins.CoinSystem;
import de.zetor.coins.managers.BankAccountManager;
import de.zetor.coins.managers.BankGUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
public class PlayerChat implements Listener {

    private BankAccountManager bankAccountManager;

    private HashMap<Player, String> chatPlayer = new HashMap<>();

    public PlayerChat(CoinSystem coinSystem) {
        this.bankAccountManager = coinSystem.getBankAccountManager();
        coinSystem.getServer().getPluginManager().registerEvents(this, coinSystem);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        Player p = event.getPlayer();
        if (chatPlayer.containsKey(p)){
            bankAccountManager.chatResult(p, event.getMessage());
            chatPlayer.remove(p);
            event.setCancelled(true);
        }
    }

    public void addChatPlayer(Player p, String bankID){
        chatPlayer.put(p, bankID);
    }


}
