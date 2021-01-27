package de.zetor.coins.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.zetor.coins.CoinSystem;
import de.zetor.coins.Messages;
import de.zetor.coins.models.BankAccount;
import de.zetor.coins.models.BankLog;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Filter;


public class BankAccountManager {

    private CoinSystem coinSystem;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");

    public BankAccountManager(CoinSystem coinSystem) {
        this.coinSystem = coinSystem;
    }

    /**
     * @param player
     */
    public void createBankAccount(Player player) {
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");
        if (collection.count(Filters.eq("uuid", player.getUniqueId().toString())) >= 5){
            BankAccount bankAccount = new BankAccount(UUID.randomUUID().toString(), player.getUniqueId().toString(), player.getName()+"'s Bank-Account", 0);
            collection.insertOne(Document.parse(coinSystem.getGson().toJson(bankAccount)));
            player.sendMessage(Messages.ACCOUNT_CREATED);
        }else {
            player.sendMessage(Messages.ACCOUNT_LIMIT);
        }
    }

    /**
     * @param accountName
     * @param p
     */
    @Deprecated
    public void createBankAccount(String accountName, Player p) {
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");

        BankAccount bankAccount = new BankAccount(UUID.randomUUID().toString(), p.getUniqueId().toString(), accountName, 0);
        collection.insertOne(Document.parse(coinSystem.getGson().toJson(bankAccount)));
    }

    /**
     * @param p
     * @return
     */
    public int getPlayerCoins(Player p){
        AtomicInteger coins = new AtomicInteger();
        p.getInventory().forEach( itemStack -> {
            if (itemStack !=  null){
                if (itemStack.getType().equals(Material.GOLD_NUGGET)){
                    if (itemStack.getItemMeta().getDisplayName().equals(Messages.COIN_NAME)){
                        coins.addAndGet(itemStack.getAmount());
                    }
                }
            }
        });
        return coins.get();
    }

    /**
     * @param p
     * @return
     */
    public int getPlayerCoinsAndRemove(Player p){
        AtomicInteger coins = new AtomicInteger();
        p.getInventory().forEach( itemStack -> {
            if (itemStack !=  null){
                if (itemStack.getType().equals(Material.GOLD_NUGGET)){
                    if (itemStack.getItemMeta().getDisplayName().equals(Messages.COIN_NAME)){
                        coins.addAndGet(itemStack.getAmount());
                        p.getInventory().remove(itemStack);
                    }
                }
            }
        });
        return coins.get();
    }

    /**
     * @param bankID
     * @param p
     */
    public void payAllToBankAccount(String bankID, Player p){
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");
        collection.updateOne(Filters.eq("bankID", bankID), Updates.set("coins", getPlayerCoins(p) + getPlayerCoinsOnBank(bankID)));
    }

    /**
     * @param bankID
     * @param p
     * @param amount
     */
    public void removeAmountFromBankAccount(String bankID, Player p, int amount){
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");
        collection.updateOne(Filters.eq("bankID", bankID), Updates.set("coins", getPlayerCoinsOnBank(bankID) - amount));
        logActivity(new BankLog(bankID, p.getUniqueId().toString(), amount+" coins have been debited from your account.", System.currentTimeMillis()));
        p.sendMessage(Messages.PREFIX + amount +" coins have been debited from your account!");
    }

    /**
     * @param bankID
     * @param p
     * @param amount
     */
    public void payAmountToBankAccount(String bankID, Player p, int amount){
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");
        int payBack = getPlayerCoins(p) - amount;
        p.getInventory().forEach( itemStack -> {
            if (itemStack !=  null){
                if (itemStack.getType().equals(Material.GOLD_NUGGET)){
                    if (itemStack.getItemMeta().getDisplayName().equals(Messages.COIN_NAME)){
                        p.getInventory().remove(itemStack);
                    }
                }
            }
        });
        collection.updateOne(Filters.eq("bankID", bankID), Updates.set("coins", amount + getPlayerCoinsOnBank(bankID)));
        p.getInventory().addItem(createItem(Material.GOLD_NUGGET, Messages.COIN_NAME, false, payBack, ""));
        logActivity(new BankLog(bankID, p.getUniqueId().toString(), amount+" coins have been added to your account.", System.currentTimeMillis()));
        p.sendMessage(Messages.PREFIX + amount + " coins have been added to your account!");
    }

    /**
     * @param bankID
     * @return
     */
    public int getPlayerCoinsOnBank(String bankID){
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");
        return collection.find(Filters.eq("bankID", bankID)).first().getInteger("coins");
    }

    /**
     * @param bankID
     * @return
     */
    public BankAccount getBankAccountByID(String bankID){
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");
        Document document = collection.find(Filters.eq("bankID", bankID)).first();
        return new BankAccount(document.getString("bankID"), document.getString("uuid"), document.getString("bankname"), document.getInteger("coins"));
    }

    /**
     * @param bankID
     */
    public void deleteBankAccountByID(String bankID){
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");
        collection.deleteOne(Filters.eq("bankID", bankID));
    }

    /**
     * @param p
     * @return
     */
    public ArrayList<BankAccount> getPlayerBankAccounts(Player p){
        ArrayList<BankAccount> bankAccounts = new ArrayList<BankAccount>();
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");
        for (Document document : collection.find(Filters.eq("uuid", p.getUniqueId().toString()))){
            bankAccounts.add(new BankAccount(document.getString("bankID"), document.getString("uuid"), document.getString("bankname"), document.getInteger("coins")));
        }
        return bankAccounts;
    }

    /**
     * @param p 
     * @param message
     */
    public void chatResult(Player p, String message){
        if (message.length() < 25){
            String bankid = coinSystem.getPlayerChat().getChatPlayer().get(p);
            coinSystem.getPlayerChat().getChatPlayer().remove(p);
            MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
            MongoCollection<Document> collection = db.getCollection("bank_accounts");
            collection.updateOne(Filters.eq("bankID", bankid), Updates.set("bankname", message));
            p.sendMessage(Messages.NAME_CHANGED);

        }
    }

    /**
     * @param bankLog
     */
    public void logActivity(BankLog bankLog){
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_log");
        collection.insertOne(Document.parse(coinSystem.getGson().toJson(bankLog)));
    }

    /**
     * @param p
     * @param bankID
     */
    public void sendLogToPlayer(Player p, String bankID){
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_log");
        collection.find(Filters.eq("bankID", bankID)).limit(10).forEach((Consumer<? super Document>) document -> {
            p.sendMessage(Messages.PREFIX+"["+dateFormat.format(new Date(document.getLong("time")))+"] "+ document.getString("logMSG"));
        });
    }

    /**
     * @param material
     * @param name
     * @param enchant
     * @param amount
     * @param lore
     * @return
     */
    public ItemStack createItem(final Material material, final String name, final boolean enchant, final int amount, final String... lore){
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));

        if (enchant){
            meta.addEnchant(Enchantment.DAMAGE_ARTHROPODS, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        item.setItemMeta(meta);

        return item;
    }


}
