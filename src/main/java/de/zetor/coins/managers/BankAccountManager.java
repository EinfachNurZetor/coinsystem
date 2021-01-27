package de.zetor.coins.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.zetor.coins.CoinSystem;
import de.zetor.coins.Messages;
import de.zetor.coins.models.BankAccount;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Filter;


public class BankAccountManager {

    private CoinSystem coinSystem;

    public BankAccountManager(CoinSystem coinSystem) {
        this.coinSystem = coinSystem;
    }

    /**
     * @param player
     */
    public void createBankAccount(Player player) {
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");

        BankAccount bankAccount = new BankAccount(UUID.randomUUID().toString(), player.getUniqueId().toString(), player.getName()+"'s Bank-Account", 0);
        collection.insertOne(Document.parse(coinSystem.getGson().toJson(bankAccount)));
    }

    /**
     * @param accountName
     * @param p
     */
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
