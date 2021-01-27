package de.zetor.coins.commands;

import de.zetor.coins.CoinSystem;
import de.zetor.coins.Messages;
import de.zetor.coins.managers.BankAccountManager;
import de.zetor.coins.managers.BankGUI;
import de.zetor.coins.managers.InterestManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class BankCommand implements CommandExecutor {

    private BankAccountManager bankAccountManager;
    private BankGUI bankGUI;
    private InterestManager interestManager;

    public BankCommand(CoinSystem coinSystem) {
        this.bankAccountManager = coinSystem.getBankAccountManager();
        this.bankGUI = coinSystem.getBankGUI();
        this.interestManager = coinSystem.getInterestManager();
        coinSystem.getCommand("bank").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            if (args.length == 1){
                if (args[0].equalsIgnoreCase("open")){
                    bankGUI.openBankGUI(p);
                }
            }else if (args.length == 2){
                if (args[0].equalsIgnoreCase("give")){
                    if (p.isOp()){
                        p.getInventory().addItem(bankAccountManager.createItem(Material.GOLD_NUGGET, Messages.COIN_NAME, false, Integer.parseInt(args[1]), ""));
                    }
                }
            }
        }
        return false;
    }
}
