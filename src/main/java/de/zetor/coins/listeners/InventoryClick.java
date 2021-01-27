package de.zetor.coins.listeners;

import de.zetor.coins.CoinSystem;
import de.zetor.coins.Messages;
import de.zetor.coins.managers.BankAccountManager;
import de.zetor.coins.managers.BankGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

public class InventoryClick implements Listener {

    private BankAccountManager bankAccountManager;
    private CoinSystem coinSystem;
    private BankGUI bankGUI;

    public InventoryClick(CoinSystem coinSystem) {
        this.bankAccountManager = coinSystem.getBankAccountManager();
        this.coinSystem = coinSystem;
        this.bankGUI = coinSystem.getBankGUI();
        coinSystem.getServer().getPluginManager().registerEvents(this, coinSystem);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getCurrentItem() != null){
            if (event.getView().getTitle().equals(Messages.BANKGUI_NAME)){
                Player p = (Player) event.getWhoClicked();
                if (event.getCurrentItem().getType().equals(Material.SUNFLOWER)){
                    String bankID = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(coinSystem, "bankid"), PersistentDataType.STRING);
                    bankGUI.openBankGUI(p, bankID);
                }else if (event.getCurrentItem().getType().equals(Material.LIME_DYE)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Messages.BANKGUI_NEWACCOUNT)){
                        bankAccountManager.createBankAccount(p);
                        bankGUI.openBankGUI(p);
                    }
                }
                event.setCancelled(true);
            }
        }
    }
}
