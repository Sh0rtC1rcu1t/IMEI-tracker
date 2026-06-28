package imei.track;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Applies additive schema migrations to the phones table.
 * Safe to call on every login — skips columns that already exist.
 */
public class SchemaManager {

    public static void migrate(Connection conn) {
        try {
            addColumnIfAbsent(conn, "created_at",
                    "DATETIME DEFAULT CURRENT_TIMESTAMP");
            addColumnIfAbsent(conn, "updated_at",
                    "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP");
            addColumnIfAbsent(conn, "last_seen_cell",
                    "VARCHAR(50) DEFAULT NULL");
            addColumnIfAbsent(conn, "status",
                    "ENUM('ACTIVE','STOLEN','FOUND') NOT NULL DEFAULT 'ACTIVE'");
        } catch (SQLException e) {
            System.err.println("Schema migration failed: " + e.getMessage());
        }
    }

    private static void addColumnIfAbsent(Connection conn, String column, String definition)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                   + "WHERE TABLE_SCHEMA = DATABASE() "
                   + "AND TABLE_NAME = 'phones' "
                   + "AND COLUMN_NAME = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, column);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    try (Statement st = conn.createStatement()) {
                        st.executeUpdate(
                            "ALTER TABLE phones ADD COLUMN " + column + " " + definition);
                    }
                    System.out.println("Schema: added column '" + column + "'");
                }
            }
        }
    }
}
