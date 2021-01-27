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
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataType;

public class InventoryClick implements Listener {

    private BankAccountManager bankAccountManager;
    private CoinSystem coinSystem;
    private BankGUI bankGUI;
    private PlayerChat playerChat;

    public InventoryClick(CoinSystem coinSystem) {
        this.bankAccountManager = coinSystem.getBankAccountManager();
        this.coinSystem = coinSystem;
        this.bankGUI = coinSystem.getBankGUI();
        this.playerChat = coinSystem.getPlayerChat();
        coinSystem.getServer().getPluginManager().registerEvents(this, coinSystem);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getCurrentItem() != null){
            if (event.getView().getTitle().equals(Messages.BANKGUI_NAME)){
                Player p = (Player) event.getWhoClicked();
                if (event.getCurrentItem().getType().equals(Material.SUNFLOWER)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Messages.BANKGUI_ACCOUNTLOG)){
                        String bankID = event.getInventory().getItem(10).getItemMeta().getPersistentDataContainer().get(new NamespacedKey(coinSystem, "bankid"), PersistentDataType.STRING);
                        bankAccountManager.sendLogToPlayer(p, bankID);
                    }else {
                        if (event.isShiftClick()){
                            String bankID = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(coinSystem, "bankid"), PersistentDataType.STRING);
                            p.closeInventory();
                            p.sendMessage(Messages.CHAT_DEACTIVATED);
                            playerChat.addChatPlayer(p, bankID);
                        }else {
                            String bankID = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(coinSystem, "bankid"), PersistentDataType.STRING);
                            bankGUI.openBankGUI(p, bankID);
                        }
                    }
                }else if (event.getCurrentItem().getType().equals(Material.LIME_DYE)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Messages.BANKGUI_NEWACCOUNT)){
                        bankAccountManager.createBankAccount(p);
                        bankGUI.openBankGUI(p);
                    }
                }else if (event.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS_PANE)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Messages.BANKGUI_DEPOSIT)){
                        String bankID = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(coinSystem, "bankid"), PersistentDataType.STRING);
                        bankGUI.openDepositGUI(p, bankID);
                    }
                }else if (event.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS_PANE)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Messages.BANKGUI_PAYOFF)){
                        String bankID = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(coinSystem, "bankid"), PersistentDataType.STRING);
                        bankGUI.openPayOffGUI(p, bankID);
                    }else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Messages.BANKGUI_DELETEACCOUNT)){
                        String bankID = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(coinSystem, "bankid"), PersistentDataType.STRING);
                        bankAccountManager.deleteBankAccountByID(bankID);
                        bankGUI.openBankGUI(p);
                    }
                }else if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Messages.BANKGUI_BACK)){
                        bankGUI.openBankGUI(p);
                    }
                }
                event.setCancelled(true);
            }else if (event.getView().getTitle().equals("§a§lDesposit")){
                Player p = (Player) event.getWhoClicked();
                if (event.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS_PANE)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§a+10")){
                        int value = Integer.parseInt(event.getInventory().getItem(3).getItemMeta().getDisplayName().replace("§aAmount: §6",""));
                        int newValue = value + 10;
                        event.getInventory().setItem(3, bankAccountManager.createItem(Material.LIGHT_BLUE_WOOL, "§aAmount: §6"+newValue, false,1, ""));
                    }
                }else if (event.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS_PANE)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§c-10")){
                        int value = Integer.parseInt(event.getInventory().getItem(3).getItemMeta().getDisplayName().replace("§aAmount: §6",""));
                        int newValue = value - 10;
                        if (newValue >= 0){
                            event.getInventory().setItem(3, bankAccountManager.createItem(Material.LIGHT_BLUE_WOOL, "§aAmount: §6"+newValue, false,1, ""));
                        }
                    }
                }else if (event.getCurrentItem().getType().equals(Material.LIME_DYE)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§aSubmit")){
                        //Submit
                        int value = Integer.parseInt(event.getInventory().getItem(3).getItemMeta().getDisplayName().replace("§aAmount: §6",""));
                        int coins = bankAccountManager.getPlayerCoins(p);
                        if (value <= coins){
                            String bankID = event.getInventory().getItem(4).getItemMeta().getPersistentDataContainer().get(new NamespacedKey(coinSystem, "bankid"), PersistentDataType.STRING);
                            bankAccountManager.payAmountToBankAccount(bankID, p, value);
                        }
                        p.closeInventory();
                    }
                }else if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Messages.BANKGUI_BACK)){
                        bankGUI.openBankGUI(p);
                    }
                }
                event.setCancelled(true);
            }else if (event.getView().getTitle().equals("§c§lPay off")){
                Player p = (Player) event.getWhoClicked();
                if (event.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS_PANE)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§a+10")){
                        int value = Integer.parseInt(event.getInventory().getItem(3).getItemMeta().getDisplayName().replace("§aAmount: §6",""));
                        int newValue = value + 10;
                        event.getInventory().setItem(3, bankAccountManager.createItem(Material.LIGHT_BLUE_WOOL, "§aAmount: §6"+newValue, false,1, ""));
                    }
                }else if (event.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS_PANE)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§c-10")){
                        int value = Integer.parseInt(event.getInventory().getItem(3).getItemMeta().getDisplayName().replace("§aAmount: §6",""));
                        int newValue = value - 10;
                        if (newValue >= 0){
                            event.getInventory().setItem(3, bankAccountManager.createItem(Material.LIGHT_BLUE_WOOL, "§aAmount: §6"+newValue, false,1, ""));
                        }
                    }
                }else if (event.getCurrentItem().getType().equals(Material.LIME_DYE)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§aSubmit")){
                        //Submit
                        String bankID = event.getInventory().getItem(4).getItemMeta().getPersistentDataContainer().get(new NamespacedKey(coinSystem, "bankid"), PersistentDataType.STRING);
                        int value = Integer.parseInt(event.getInventory().getItem(3).getItemMeta().getDisplayName().replace("§aAmount: §6",""));
                        int coins = bankAccountManager.getPlayerCoinsOnBank(bankID);
                        if (value <= coins){
                            bankAccountManager.removeAmountFromBankAccount(bankID, p, value);
                            p.getInventory().addItem(bankAccountManager.createItem(Material.GOLD_NUGGET, Messages.COIN_NAME, false, value, ""));
                        }
                        p.closeInventory();
                    }
                }else if (event.getCurrentItem().getType().equals(Material.BARRIER)){
                    if (event.getCurrentItem().getItemMeta().getDisplayName().equals(Messages.BANKGUI_BACK)){
                        bankGUI.openBankGUI(p);
                    }
                }
                event.setCancelled(true);
            }
        }
    }
}
