package de.zetor.coins;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import de.zetor.coins.commands.BankCommand;
import de.zetor.coins.listeners.InventoryClick;
import de.zetor.coins.listeners.PlayerChat;
import de.zetor.coins.managers.BankAccountManager;
import de.zetor.coins.managers.BankGUI;
import de.zetor.coins.managers.InterestManager;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.UnknownHostException;

@Getter
public class CoinSystem extends JavaPlugin {

    private static MongoClientURI uri = new MongoClientURI("mongodb+srv://zetor:So88pnSu1IIfwo92@cluster0.fhyc1.mongodb.net/test?retryWrites=true&w=majority");

    private MongoClient mongoClient;
    private Gson gson;

    private BankAccountManager bankAccountManager;
    private BankGUI bankGUI;
    private InventoryClick inventoryClick;
    private PlayerChat playerChat;
    private InterestManager interestManager;

    @Override
    public void onEnable() {
        mongoClient = new MongoClient(uri);
        this.gson = new Gson();

        this.bankAccountManager = new BankAccountManager(this);
        this.bankGUI = new BankGUI(this);
        this.playerChat = new PlayerChat(this);
        this.inventoryClick = new InventoryClick(this);
        this.interestManager = new InterestManager(this);

        new BankCommand(this);
    }
}
