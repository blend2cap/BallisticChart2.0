//This file establish a connection with SQLite, doesn't define the Bullet class

package sample;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.String.valueOf;

public class CartridgeConnection {

    private static Connection con;

    static Bullet selectedBullet(String name) throws SQLException, ClassNotFoundException {
        if (con == null) getConnection();
        PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM BulletTable WHERE name=?;");
        preparedStatement.setString(1,name);
        ResultSet resultSet=preparedStatement.executeQuery();
        return new Bullet(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("mass"),
                resultSet.getDouble("bc"), resultSet.getDouble("caliber"),resultSet.getDouble("muzzleVelocity"));
    }

    private static void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:CartridgeDB.db");
    }

    //generates list of GFunctions
    static ArrayList<GFunction> GetGFunctions() throws SQLException, ClassNotFoundException {
        ArrayList<GFunction> GFunctions = new ArrayList<>();
        if(con==null) getConnection();
        Statement state = con.createStatement();
        ResultSet resultSet = state.executeQuery("SELECT * from GFunction");
        while ( resultSet.next()){
            GFunction gf = new GFunction();
            gf.Speed = resultSet.getDouble("m/s");
            gf.G1 = resultSet.getDouble("G1");
            GFunctions.add(gf);
        }
        return GFunctions;
    }

    static ArrayList<String> GetBulletNames() throws SQLException, ClassNotFoundException {
        ArrayList<String> bulletNames=new ArrayList<>();
        if(con==null)
            getConnection();
        Statement state = con.createStatement();
        ResultSet resultSet = state.executeQuery("SELECT name from BulletTable");
        String temp;
        while ( resultSet.next() ){
            temp=resultSet.getString("name");
            bulletNames.add(temp);
        }
        return bulletNames;
    }

    static void addBulletToDB(Bullet bullet) throws SQLException, ClassNotFoundException {
        if(con==null) getConnection();
        PreparedStatement prep= con.prepareStatement("INSERT INTO BulletTable values(?,?,?,?,?,?);");
        prep.setString(2, valueOf(String.valueOf(bullet.getName())));
        prep.setString(3, valueOf(String.valueOf(bullet.getMass())));
        prep.setString(4, valueOf(String.valueOf(bullet.getMuzzleVelocity())));
        prep.setString(5, valueOf(String.valueOf(bullet.getCaliber())));
        prep.setString(6, valueOf(String.valueOf(bullet.getBC())));
        prep.execute();
    }


    public  static ArrayList<Bullet> CreateBulletList() throws SQLException, ClassNotFoundException {
        ArrayList<Bullet> Bullets = new ArrayList<>();
        if (con==null) getConnection();
        Statement state = con.createStatement();
        ResultSet resultSet = state.executeQuery("SELECT * from BulletTable");
        while ( resultSet.next()){
            Bullets.add(new Bullet(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("mass"),
                    resultSet.getDouble("bc"), resultSet.getDouble("caliber"),resultSet.getDouble("muzzleVelocity")));
        }
        return Bullets;
    }



}
