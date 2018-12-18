package net.elise1886.globalcoins;
import java.sql.*;

public class SQLConnection {
    private Connection connection;

    String host;
    int port;
    String username;
    String password;
    String database;

    public SQLConnection(String host, int port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }



    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        }
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
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
    }





    //Updates the users balance
    public void update(String username, int coinsToAdd){
        try
        {
            int coins = getBalance(username);
            int totalCoins = coins + coinsToAdd;
            String sql = "UPDATE Coins SET Balance=? WHERE Username=?";
            PreparedStatement prepedStatement = connection.prepareStatement(sql);
            prepedStatement.setInt(1, totalCoins);
            prepedStatement.setString(2, username);
            prepedStatement.execute();

        }
        catch (Exception e)
        {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
    }





    //Gets the balance out of the database
    public int getBalance(String username){
        try
        {
            String sql = "SELECT Balance FROM Coins WHERE Username = ?";
                PreparedStatement prepedStatement = connection.prepareStatement(sql);
                prepedStatement.setString(1,username);
                ResultSet rs = prepedStatement.executeQuery();
                    return rs.getInt("Balance");

        }
        catch (Exception e)
        {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
        }
        return 0;
    }






}
