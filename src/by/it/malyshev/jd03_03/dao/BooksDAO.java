package by.it.malyshev.jd03_03.dao;

import by.it.malyshev.jd03_03.beans.Books;
import by.it.malyshev.jd03_03.connection.ConnectionCreator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BooksDAO extends AbstractDAO implements InterfaceDAO<Books> {

    @Override
    public boolean create(Books book) throws SQLException {
        String createBookSQL = String.format(
                "insert into books(`title`,`pubyear`,`isbn`, `img`,`price`) values('%s', %d, '%s', '%s', '%s');",
                book.getTitle(), book.getPubyear(), book.getIsbn(), book.getImg(), book.getPrice()
        );
        int id = executeCreate(createBookSQL);
        if (id > 0) book.setId(id);
        return (id > 0);
    }

    @Override
    public boolean update(Books book) throws SQLException {
        String updateBookSQL = String.format(
                "UPDATE books SET title = '%s', pubyear = %d, isbn = '%s', img = '%s',price='%s' WHERE books.ID = %d",
                book.getTitle(), book.getPubyear(), book.getIsbn(), book.getImg(), book.getPrice(), book.getId()
        );
        return executeUpdate(updateBookSQL);
    }

    @Override
    public boolean delete(Books book) throws SQLException {
        String deleteBookSQL = String.format("DELETE FROM books WHERE books.ID = %d", book.getId());
        return executeUpdate(deleteBookSQL);
    }

    @Override
    public Books read(Books book) throws SQLException {
        return getAll("where ID=" + book.getId()).get(0);
    }

    @Override
    public Books read(int id) throws SQLException {
        List<Books> book = getAll("WHERE ID=" + id + " LIMIT 0,1");
        if (book.size() > 0) {
            return book.get(0);
        } else
            return null;
    }

    @Override
    public List<Books> getAll() throws SQLException {
        return getAll("");
    }

    @Override
    public List<Books> getAll(String WhereAndOrder) throws SQLException {
        List<Books> books = new ArrayList<>();
        String readUserSQL = "SELECT * FROM books " + WhereAndOrder;
        try (
                Connection connection = ConnectionCreator.getConnection();
                Statement statement = connection.createStatement()
        ) {
            final ResultSet resultSet = statement.executeQuery(readUserSQL);
            while (resultSet.next()) {
                Books tmpBook = new Books(
                        resultSet.getInt("ID"),
                        resultSet.getString("title"),
                        resultSet.getInt("pubyear"),
                        resultSet.getString("isbn"),
                        resultSet.getString("img"),
                        resultSet.getDouble("price"));
                books.add(tmpBook);
            }
        }
        return books;
    }
}

