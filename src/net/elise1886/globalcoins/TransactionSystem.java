package net.elise1886.globalcoins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.milkbowl.vault.economy.*;




public class TransactionSystem{

    String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Coins" + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE;

    SQLConnection connection;
    CommandSender sender;
    double TransactionRate;
    String username;
    Economy Eco;

    public TransactionSystem(SQLConnection connection, CommandSender sender, double TransactionRate, String username, Economy economy) {
        this.connection = connection;
        this.sender = sender;
        this.TransactionRate = TransactionRate;
        this.username = username;
        this.Eco = economy;

    }



    public int buyCoins(int amountOfCoinsToBuy, int supply){
        Player player = (Player) sender;
        if(amountOfCoinsToBuy > supply){
            sender.sendMessage(prefix + "Sorry but " + amountOfCoinsToBuy + " exceeds the current supply. Please try a lower number!");
        }
        else{
            double price = TransactionRate * amountOfCoinsToBuy;
            TransactionRate += (0.0005 * price);
            Eco.withdrawPlayer(player, price);
            connection.updateBuy(username, amountOfCoinsToBuy);
            sender.sendMessage(prefix + "You just bought " + ChatColor.GREEN + amountOfCoinsToBuy + ChatColor.WHITE +" for " + ChatColor.RED + price);
            return  supply - amountOfCoinsToBuy;
        }
        return supply;
    }

    public int sellCoins(int amountOfCoinsToSell, int supply){
        Player player = (Player) sender;
        double price = TransactionRate * amountOfCoinsToSell;
        TransactionRate -= (0.0005 * price);
        Eco.depositPlayer(player, price);
        int userBalance = connection.getBalance(username);
        int updatedBalance = userBalance - amountOfCoinsToSell;
        connection.updateSell(username, updatedBalance);
        sender.sendMessage(prefix + "You just sold " + ChatColor.GREEN + amountOfCoinsToSell + ChatColor.WHITE +" for " + ChatColor.RED + price);
        return  supply + amountOfCoinsToSell;
    }

    public void playerBalance(){
        int playerBal =  connection.getBalance(username);
        sender.sendMessage(prefix + "Your current balance is " + ChatColor.GREEN + playerBal);
    }





}
