package me.papaseca;

import lombok.SneakyThrows;
import me.papaseca.objects.MySQLCreeds;
import me.papaseca.objects.Person;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Database {

    private Connection con;

    @SneakyThrows
    public Database() {

        establishConnection();

    }

    @SneakyThrows
    public ResultSet getUserFromDB(String dni) {
        ResultSet resultSet;

        String querry = "SELECT `name`,`age`,`job`,`dni` FROM `users` WHERE `dni`=?";

        PreparedStatement preparedStatement = null;
        preparedStatement = con.prepareStatement(querry);
        preparedStatement.setString(1, dni);

        resultSet = preparedStatement.executeQuery();

        return resultSet;

    }


    @SneakyThrows
    public void addUserToDB(Person person) {
        String querry = "INSERT INTO `users` VALUE(?,?,?,?,?)";

        PreparedStatement preparedStatement = null;

        preparedStatement = con.prepareStatement(querry);
        preparedStatement.setString(1, String.valueOf(UUID.randomUUID()));
        preparedStatement.setString(2, person.getName());
        preparedStatement.setString(3, String.valueOf(person.getAge()));
        preparedStatement.setString(4, person.getJob());
        preparedStatement.setString(5, person.getDni());

        preparedStatement.executeUpdate();
    }

    @SneakyThrows
    private void establishConnection() {
        System.out.println(logger() + "Connecting to database...");

        Class.forName("com.mysql.cj.jdbc.Driver");

        con = DriverManager.getConnection("jdbc:mysql://" + MySQLCreeds.ip + ":3306/" + MySQLCreeds.database + "", MySQLCreeds.username, MySQLCreeds.password);

        System.out.println(logger() + "Connected to database succesfull!");
    }

    public String logger() {
        String dateFormated = "";

        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SS");

        dateFormated += "[" + simpleDateFormat.format(date) + "] ";

        return dateFormated;
    }

}
