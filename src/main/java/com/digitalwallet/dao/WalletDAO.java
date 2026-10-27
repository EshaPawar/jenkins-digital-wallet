package com.digitalwallet.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WalletDAO {

    private static final String URL =
            "jdbc:mysql://localhost:3306/digital_wallet?useSSL=false&serverTimezone=Asia/Kolkata";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    /*
     * CREATE DATABASE CONNECTION
     *
     * Explicitly load the MySQL JDBC driver before
     * creating the connection.
     */
    private Connection getConnection() throws SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC Driver not found. " +
                    "Make sure mysql-connector-j is included in the Maven project.",
                    e
            );
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }

    // REGISTER USER
    public int registerUser(
            String name,
            String email,
            String mobile,
            String password)
            throws SQLException {

        String sql =
                "INSERT INTO users " +
                "(name, email, mobile, password, balance) " +
                "VALUES (?, ?, ?, ?, 0.00)";

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, mobile);
            statement.setString(4, password);

            statement.executeUpdate();

            try (ResultSet rs =
                         statement.getGeneratedKeys()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    // LOGIN
    public int loginUser(
            String email,
            String password)
            throws SQLException {

        String sql =
                "SELECT id FROM users " +
                "WHERE email = ? AND password = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return -1;
    }

    // GET USER
    public User getUser(int userId)
            throws SQLException {

        String sql =
                "SELECT id, name, email, mobile, balance " +
                "FROM users WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (rs.next()) {

                    User user = new User();

                    user.setId(
                            rs.getInt("id")
                    );

                    user.setName(
                            rs.getString("name")
                    );

                    user.setEmail(
                            rs.getString("email")
                    );

                    user.setMobile(
                            rs.getString("mobile")
                    );

                    user.setBalance(
                            rs.getDouble("balance")
                    );

                    return user;
                }
            }
        }

        return null;
    }

    // ADD MONEY
    public boolean addMoney(
            int userId,
            double amount)
            throws SQLException {

        if (amount <= 0) {
            return false;
        }

        String updateBalance =
                "UPDATE users " +
                "SET balance = balance + ? " +
                "WHERE id = ?";

        String transaction =
                "INSERT INTO transactions " +
                "(user_id, recipient, type, amount, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection connection = null;

        try {

            connection = getConnection();

            connection.setAutoCommit(false);

            // Update wallet balance
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 updateBalance)) {

                statement.setDouble(1, amount);
                statement.setInt(2, userId);

                int updated =
                        statement.executeUpdate();

                if (updated == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // Add transaction record
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 transaction)) {

                statement.setInt(1, userId);
                statement.setString(2, "Wallet");
                statement.setString(3, "ADD_MONEY");
                statement.setDouble(4, amount);
                statement.setString(5, "SUCCESS");

                statement.executeUpdate();
            }

            connection.commit();

            return true;

        } catch (SQLException e) {

            if (connection != null) {
                connection.rollback();
            }

            throw e;

        } finally {

            if (connection != null) {
                connection.close();
            }
        }
    }

    // SEND MONEY
    public boolean sendMoney(
            int userId,
            String recipient,
            double amount)
            throws SQLException {

        if (amount <= 0) {
            return false;
        }

        String balanceSql =
                "SELECT balance FROM users WHERE id = ?";

        String updateBalance =
                "UPDATE users " +
                "SET balance = balance - ? " +
                "WHERE id = ? AND balance >= ?";

        String transaction =
                "INSERT INTO transactions " +
                "(user_id, recipient, type, amount, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        Connection connection = null;

        try {

            connection = getConnection();

            connection.setAutoCommit(false);

            double balance;

            // Get current balance
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 balanceSql)) {

                statement.setInt(1, userId);

                try (ResultSet rs =
                             statement.executeQuery()) {

                    if (!rs.next()) {

                        connection.rollback();

                        return false;
                    }

                    balance =
                            rs.getDouble("balance");
                }
            }

            // Check sufficient balance
            if (balance < amount) {

                connection.rollback();

                return false;
            }

            // Deduct amount
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 updateBalance)) {

                statement.setDouble(1, amount);
                statement.setInt(2, userId);
                statement.setDouble(3, amount);

                int updated =
                        statement.executeUpdate();

                if (updated == 0) {

                    connection.rollback();

                    return false;
                }
            }

            // Record transaction
            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 transaction)) {

                statement.setInt(1, userId);
                statement.setString(2, recipient);
                statement.setString(3, "SEND");
                statement.setDouble(4, amount);
                statement.setString(5, "SUCCESS");

                statement.executeUpdate();
            }

            connection.commit();

            return true;

        } catch (SQLException e) {

            if (connection != null) {
                connection.rollback();
            }

            throw e;

        } finally {

            if (connection != null) {
                connection.close();
            }
        }
    }

    // TRANSACTION HISTORY
    public List<Transaction> getTransactions(
            int userId)
            throws SQLException {

        List<Transaction> transactions =
                new ArrayList<>();

        String sql =
                "SELECT id, recipient, type, amount, " +
                "status, transaction_date " +
                "FROM transactions " +
                "WHERE user_id = ? " +
                "ORDER BY transaction_date DESC";

        try (Connection connection =
                     getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                while (rs.next()) {

                    Transaction transaction =
                            new Transaction();

                    transaction.setId(
                            rs.getInt("id")
                    );

                    transaction.setRecipient(
                            rs.getString("recipient")
                    );

                    transaction.setType(
                            rs.getString("type")
                    );

                    transaction.setAmount(
                            rs.getDouble("amount")
                    );

                    transaction.setStatus(
                            rs.getString("status")
                    );

                    transaction.setTransactionDate(
                            rs.getTimestamp(
                                    "transaction_date"
                            )
                    );

                    transactions.add(
                            transaction
                    );
                }
            }
        }

        return transactions;
    }
}