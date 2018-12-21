package net.elise1886.globalcoins;
import java.sql.*;

public class SQLConnection {
    private Connection connection;

    public SQLConnection(Connection connection) {
        this.connection = connection;

    }
    //Gives the user coins
    public void insert(String username, int coins){
        try
        {
        String sql = "INSERT INTO Coins (Username, Balance) VALUES (?, ?)";
        PreparedStatement prepedStatement = connection.prepareStatement(sql);
        prepedStatement.setString(1,username);
        prepedStatement.setInt(2,coins);
        prepedStatement.execute();
    }
        catch (Exception e)
        {
            System.err.println("Got an exception in Insert!");
            System.err.println(e.getMessage());
        }
    }


    //Updates the users balance
    public void updateBuy(String username, int coinsToAdd){
        try
        {
            if(!check(username)){
                insert(username,0 );
            }
            else {
                int coins = getBalance(username);
                int totalCoins = coins + coinsToAdd;
                String sql = "UPDATE Coins SET Balance=? WHERE Username=?";
                PreparedStatement prepedStatement = connection.prepareStatement(sql);
                prepedStatement.setInt(1, totalCoins);
                prepedStatement.setString(2, username);
                prepedStatement.execute();
            }

        }
        catch (Exception e)
        {
            System.err.println("Got an exception in Update!");
            System.err.println(e.getMessage());
        }
    }
    public void updateSell(String username, int coinsToSubtract){
        try
        {
            if(!check(username)){
                insert(username,0 );
            }
            else{
                String sql = "UPDATE Coins SET Balance=? WHERE Username=?";
                PreparedStatement prepedStatement = connection.prepareStatement(sql);
                prepedStatement.setInt(1, coinsToSubtract);
                prepedStatement.setString(2, username);
                prepedStatement.execute();
            }

        }
        catch (Exception e)
        {
            System.err.println("Got an exception in Update!");
            System.err.println(e.getMessage());
        }
    }

    public boolean check(String username){
        try{

            String sql = "SELECT Username FROM Coins WHERE Username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,username);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        }
        catch (Exception e){
            System.err.println("Got an excpetion in the Check Method");
            System.err.println(e);
        }
    return false;
    }



    //Gets the balance out of the database
    public int getBalance(String username){
        try
        {
            if(!check(username)){
                insert(username, 0);
            }
            else{
                String sql = "SELECT Balance FROM Coins WHERE Username = ?";
                PreparedStatement prepedStatement = connection.prepareStatement(sql);
                prepedStatement.setString(1,username);
                ResultSet rs = prepedStatement.executeQuery();
                   while(rs.next()){
                    return rs.getInt("Balance");}
            }
        }
        catch (Exception e)
        {
            System.err.println("Got an exception Balance!");
            System.err.println(e.getMessage());
        }
        return 0;
    }






}
