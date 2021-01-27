package de.zetor.coins.managers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.zetor.coins.CoinSystem;
import org.bson.Document;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class InterestManager {

    private static double interestPercentage = 1.01;
    private CoinSystem coinSystem;
    private Timer timer = new Timer();

    public InterestManager(CoinSystem coinSystem) {
        this.coinSystem = coinSystem;
        registerTimes();
    }

    private void registerTimes() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                giveInterests();
            }
        }, new Date(), 3600000);
    }

    public void giveInterests() {
        MongoDatabase db = coinSystem.getMongoClient().getDatabase("coins");
        MongoCollection<Document> collection = db.getCollection("bank_accounts");
        for (Document document : collection.find()){
            int coins = document.getInteger("coins");
            int interestCoins = (int) Math.round(coins * interestPercentage);
            collection.updateOne(Filters.eq("bankID", document.getString("bankID")), Updates.set("coins", interestCoins));
        }
    }
}
