package by.it.ali.project.java.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends AbstractDAO implements InDAO<User> {
    private static final String URL_DB = "jdbc:mysql://127.0.0.1:2016/ali";
    private static final String USER_DB = "root";
    private static final String PASSWORD_DB = "";
    static {
        try { //регистрируем драйвер
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean create(User user) throws SQLException {
        String createUserSQL = String.format(
                "insert into users(Login,Password,Email,FKroles) values('%s','%s','%s',%d);",
                user.getLogin(), user.getPassword(), user.getEmail(), user.getFk_Roles()
        );
        int id=executeCreate(createUserSQL);
        if (id>0) user.setId(id);
        return (id>0);
    }

    @Override
    public boolean update(User user) throws SQLException {
        String updateUserSQL = String.format(
                "UPDATE `users` SET Login = '%s', Password = '%s', Email = '%s', FKroles=%d WHERE users.ID = %d",
                user.getLogin(), user.getPassword(), user.getEmail(), user.getFk_Roles(), user.getId()
        );
        return (0<(executeUpdate(updateUserSQL)));
    }

    @Override
    public boolean delete(User user) throws SQLException {
        String deleteUserSQL = String.format("DELETE FROM users WHERE users.login = %s", user.getLogin());
        return (0<executeUpdate(deleteUserSQL));
    }

    @Override
    public User read(User user) throws SQLException {
        return getAll("where id="+user.getId()).get(0);
    }



    @Override
    public List<User> getAll(String WhereAndOreder) throws SQLException {
        List<User> users = new ArrayList<>();
        String readUserSQL = "SELECT * FROM users " + WhereAndOreder;
        try (
                Connection connection = DriverManager.getConnection(URL_DB, USER_DB, PASSWORD_DB);
                Statement statement = connection.createStatement()
        ) {
            final ResultSet resultSet = statement.executeQuery(readUserSQL);
            while (resultSet.next()) {
                User tmpUser = new User(
                        resultSet.getInt("ID"),
                        resultSet.getString("Login"),
                        resultSet.getString("Password"),
                        resultSet.getString("Email"),
                        resultSet.getInt("FKroles"));
                users.add(tmpUser);
            }
        }
        return users;
    }
}
