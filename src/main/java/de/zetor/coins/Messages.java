package de.zetor.coins;

public interface Messages {

    String PREFIX = "§6Bank §7>> ";

    String NAME_CHANGED = PREFIX + "The name of your Bank Account was §asuccessfully §7changed!";
    String CHAT_DEACTIVATED = PREFIX + "Write the name for your bank account in the chat. §cThe message will not be sent.";
    String ACCOUNT_CREATED = PREFIX + "A new bank account has been §acreated§7!";
    String ACCOUNT_LIMIT = PREFIX + "§cYou already have 5 bank accounts!";

    String COIN_NAME = "§6Coin";

    String BANKGUI_NAME = "§6§lBank";
    String BANKGUI_EMPTYSLOT = "§cEmpty Bank Slot";
    String BANKGUI_NEWACCOUNT = "§aCreate new Bank-Account";
    String BANKGUI_DEPOSIT = "§aDeposit";
    String BANKGUI_PAYOFF = "§cPay off";
    String BANKGUI_ACCOUNTLOG = "§6Account Log";
    String BANKGUI_DELETEACCOUNT = "§cDelete Account";
    String BANKGUI_NOTEMPTY = "§cAccount is not empty";
    String BANKGUI_BACK = "§cBack";
}
