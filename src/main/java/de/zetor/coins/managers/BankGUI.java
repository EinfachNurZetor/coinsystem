package de.zetor.coins.managers;

import de.zetor.coins.CoinSystem;
import de.zetor.coins.Messages;
import de.zetor.coins.models.BankAccount;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class BankGUI {

    private CoinSystem coinSystem;
    private BankAccountManager bankAccountManager;

    public BankGUI(CoinSystem coinSystem) {
        this.coinSystem = coinSystem;
        this.bankAccountManager = coinSystem.getBankAccountManager();
    }

    public void openBankGUI(Player p){
        Inventory inventory = Bukkit.createInventory(null, 3*9, Messages.BANKGUI_NAME);
        fillInv(inventory);
        inventory.setItem(10, bankAccountManager.createItem(Material.BARRIER, Messages.BANKGUI_EMPTYSLOT, false, 1, ""));
        inventory.setItem(11, bankAccountManager.createItem(Material.BARRIER, Messages.BANKGUI_EMPTYSLOT, false, 1, ""));
        inventory.setItem(12, bankAccountManager.createItem(Material.BARRIER, Messages.BANKGUI_EMPTYSLOT, false, 1, ""));
        inventory.setItem(13, bankAccountManager.createItem(Material.BARRIER, Messages.BANKGUI_EMPTYSLOT, false, 1, ""));
        inventory.setItem(14, bankAccountManager.createItem(Material.BARRIER, Messages.BANKGUI_EMPTYSLOT, false, 1, ""));
        int bankSlot = 10;
        for (BankAccount bankAccount : bankAccountManager.getPlayerBankAccounts(p)){
            ItemStack itemStack = bankAccountManager.createItem(Material.SUNFLOWER, "§7"+bankAccount.getBankname(), false, 1, "", "§6Coins: "+bankAccount.getCoins());
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(coinSystem, "bankid"), PersistentDataType.STRING, bankAccount.getBankID());
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(bankSlot, itemStack);
            bankSlot++;
        }
        inventory.setItem(16, bankAccountManager.createItem(Material.LIME_DYE, Messages.BANKGUI_NEWACCOUNT, false, 1, ""));
        p.openInventory(inventory);
    }

    public void openBankGUI(Player p, String bankID){
        BankAccount bankAccount = bankAccountManager.getBankAccountByID(bankID);
        if (bankAccount.getUuid().equals(p.getUniqueId().toString())){
            Inventory inventory = Bukkit.createInventory(null, 3*9, Messages.BANKGUI_NAME);
            fillInv(inventory);
            inventory.setItem(10, bankAccountManager.createItem(Material.GREEN_STAINED_GLASS_PANE, "§aDeposit", false, 1, ""));
            inventory.setItem(11, bankAccountManager.createItem(Material.GOLD_NUGGET, "§6Coins: "+bankAccount.getCoins(), false, 1, ""));
            inventory.setItem(12, bankAccountManager.createItem(Material.RED_STAINED_GLASS_PANE, "§cPay off", false, 1, ""));
            inventory.setItem(14, bankAccountManager.createItem(Material.SUNFLOWER, "§6Account Log", false, 1, ""));
            if (bankAccount.getCoins() == 0){
                inventory.setItem(16, bankAccountManager.createItem(Material.RED_STAINED_GLASS_PANE, "§cDelecte Account", false, 1, ""));
            }else {
                inventory.setItem(16, bankAccountManager.createItem(Material.BARRIER, "§cDelecte Account", false, 1, ""));
            }
            p.openInventory(inventory);
        }
    }

    private void fillInv(Inventory inv){
        ItemStack glasspane = bankAccountManager.createItem(Material.GRAY_STAINED_GLASS_PANE, " ", false, 1, "");
        for(int slot = 0; slot < inv.getSize(); slot++){
            inv.setItem(slot, glasspane);
        }
    }
}
