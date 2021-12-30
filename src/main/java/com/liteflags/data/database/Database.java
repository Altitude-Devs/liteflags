package com.liteflags.data.database;

import com.liteflags.config.Config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class Database {
    public static void createTables() {
        String player_flags = "CREATE TABLE IF NOT EXISTS player_flags (\n id INTEGER NOT NULL AUTO_INCREMENT,\n uuid VARCHAR(36) NOT NULL,\n  expire_time INTEGER(12),\n reason VARCHAR(256) NOT NULL,\n flagged_by VARCHAR(16) NOT NULL,\n time_flagged INTEGER(16) NOT NULL,\n flag_length VARCHAR(36) NOT NULL,\n PRIMARY KEY(ID)\n);";
        String player_cache = "CREATE TABLE IF NOT EXISTS player_cache (\n uuid VARCHAR(36) NOT NULL,\n player_name VARCHAR(16),\n PRIMARY KEY(uuid)\n);";

        try {
            Statement statement = DatabaseConnection.getConnection().createStatement();
            statement.execute(player_flags);
            statement.execute(player_cache);
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

    }

    public static void addFlag(UUID uuid, long expireTime, String reason, String flaggedBy, String flagLength) {
        int timeFlagged = (int) (System.currentTimeMillis() / 1000L);
        String sql = "INSERT INTO player_flags (uuid, expire_time, reason, flagged_by, time_flagged, flag_length) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);

            statement.setString(1, uuid.toString());
            statement.setLong(2, expireTime);
            statement.setString(3, reason);
            statement.setString(4, flaggedBy);
            statement.setInt(5, timeFlagged);
            statement.setString(6, flagLength);
            statement.execute();
        } catch (SQLException var21) {
            var21.printStackTrace();
        }

    }

    public static boolean removeFlag(UUID uuid, int id) {
        String sql = "DELETE FROM player_flags WHERE uuid = ? AND id = ?";

        try {
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            statement.setString(1, uuid.toString());
            statement.setInt(2, id);
            if (statement.executeUpdate() > 0)
                return true;
        } catch (SQLException var16) {
            var16.printStackTrace();
        }
        return false;
    }

    public static void removePlayerCache(UUID uuid) {
        String sql = "DELETE FROM player_cache WHERE uuid = ?";

        try {
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);
            Throwable var3 = null;

            try {
                statement.setString(1, uuid.toString());
                statement.executeUpdate();
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (statement != null) {
                    if (var3 != null) {
                        try {
                            statement.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        statement.close();
                    }
                }

            }
        } catch (SQLException var15) {
            var15.printStackTrace();
        }

    }
//
//    public static boolean hasFlag(UUID uuid, int id) {
//        try {
//            if (getStringResult("SELECT * FROM player_flags WHERE uuid = ?", uuid.toString()).next()) {
//                return true;
//            }
//        } catch (SQLException var2) {
//            var2.printStackTrace();
//        }
//
//        return false;
//    }

    public static void addPlayerCache(UUID uuid, String playerName) {
        String sql = "INSERT INTO player_cache (uuid, player_name) VALUES (?, ?) ON DUPLICATE KEY UPDATE player_name = ?";

        try {
            PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(sql);

            statement.setString(1, uuid.toString());
            statement.setString(2, playerName);
            statement.setString(3, playerName);
            statement.execute();
        } catch (SQLException var16) {
            var16.printStackTrace();
        }

    }

    public static boolean inPlayerCache(UUID uuid) {
        try {
            if (getStringResult("SELECT * FROM player_cache WHERE uuid = ?", uuid.toString()).next()) {
                return true;
            }
        } catch (SQLException var2) {
            var2.printStackTrace();
        }

        return false;
    }

    public static String getFlagReason(UUID uuid, int id) {
        try {
            ResultSet resultSet = getStringResult("SELECT reason FROM player_flags WHERE uuid = ? AND id = " + id, uuid.toString());
            if (resultSet.next()) {
                return resultSet.getString("reason");
            }
        } catch (SQLException var3) {
            var3.printStackTrace();
        }

        return null;
    }

    public static ResultSet getPlayerFlags(UUID uuid) throws SQLException {
        return getStringResult("SELECT player_flags.*, count(*)over() AS total_flags FROM player_flags WHERE uuid = ? ORDER BY id DESC LIMIT " + Config.MAX_FLAGS_LISTED, uuid.toString());
    }

    public static ResultSet getActiveTime(UUID uuid) throws SQLException {
        return getStringResult("SELECT expire_time, flagged_by FROM player_flags WHERE uuid = ?", uuid.toString());
    }

    private static ResultSet getStringResult(String query, String... parameters) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(query);

        for (int i = 1; i < parameters.length + 1; ++i) {
            statement.setString(i, parameters[i - 1]);
        }

        return statement.executeQuery();
    }
}
