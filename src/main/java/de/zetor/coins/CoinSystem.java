package de.zetor.coins;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.UnknownHostException;

public class CoinSystem extends JavaPlugin {

    private static CoinSystem instance;

    public static MongoClientURI uri = new MongoClientURI("mongodb+srv://zetor:So88pnSu1IIfwo92@cluster0.fhyc1.mongodb.net/test?retryWrites=true&w=majority");
    public static MongoClient mongoClient;

    @SneakyThrows(UnknownHostException.class)
    @Override
    public void onEnable() {
        instance = this;

        mongoClient = new MongoClient(uri);
    }

    public static CoinSystem getInstance() { return instance; }
}
