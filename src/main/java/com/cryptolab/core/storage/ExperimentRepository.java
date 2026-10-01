package com.cryptolab.core.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository performing SQLite persistence operations for laboratory experiments.
 *
 * Developed by Abderrazak BENAROUS
 */
public class ExperimentRepository {

    private static final Logger logger = LoggerFactory.getLogger(ExperimentRepository.class);

    public void record(String algorithm, String opType, String paramsSummary, int inBytes, int outBytes, double durationMs, boolean success, String notes) {
        String sql = """
            INSERT INTO experiments (algorithm_name, operation_type, parameters_summary, input_size_bytes, output_size_bytes, duration_millis, success, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, algorithm);
            ps.setString(2, opType);
            ps.setString(3, paramsSummary);
            ps.setInt(4, inBytes);
            ps.setInt(5, outBytes);
            ps.setDouble(6, durationMs);
            ps.setInt(7, success ? 1 : 0);
            ps.setString(8, notes);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed recording experiment to SQLite: {}", e.getMessage());
        }
    }

    public List<ExperimentLog> findAll(int limit) {
        List<ExperimentLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM experiments ORDER BY id DESC LIMIT ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit > 0 ? limit : 100);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(new ExperimentLog(
                            rs.getLong("id"),
                            rs.getString("created_at"),
                            rs.getString("algorithm_name"),
                            rs.getString("operation_type"),
                            rs.getString("parameters_summary"),
                            rs.getInt("input_size_bytes"),
                            rs.getInt("output_size_bytes"),
                            rs.getDouble("duration_millis"),
                            rs.getInt("success") == 1,
                            rs.getString("notes")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed querying experiments from SQLite: {}", e.getMessage());
        }
        return logs;
    }

    public void delete(long id) {
        String sql = "DELETE FROM experiments WHERE id = ?;";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed deleting experiment {}: {}", id, e.getMessage());
        }
    }

    public void clearAll() {
        String sql = "DELETE FROM experiments;";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error("Failed clearing experiments: {}", e.getMessage());
        }
    }

    public static String generateHtmlReport(List<ExperimentLog> logs, String studentName) {
        StringBuilder html = new StringBuilder();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        html.append("<!DOCTYPE html>\n<html>\n<head>\n<meta charset=\"UTF-8\">\n")
                .append("<title>CipheRaz Experiment Report</title>\n")
                .append("<style>\n")
                .append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #0B0F19; color: #F9FAFB; padding: 30px; }\n")
                .append("h1 { color: #38BDF8; font-size: 24px; margin-bottom: 4px; }\n")
                .append(".meta { color: #94A3B8; font-size: 13px; margin-bottom: 24px; }\n")
                .append("table { width: 100%; border-collapse: collapse; margin-top: 16px; background-color: #111827; border-radius: 8px; overflow: hidden; }\n")
                .append("th, td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #1F2937; font-size: 13px; }\n")
                .append("th { background-color: #1F2937; color: #38BDF8; font-weight: bold; }\n")
                .append(".success { color: #10B981; font-weight: bold; }\n")
                .append(".failure { color: #EF4444; font-weight: bold; }\n")
                .append(".footer { margin-top: 40px; font-size: 12px; color: #64748B; border-top: 1px solid #1F2937; padding-top: 12px; }\n")
                .append("</style>\n</head>\n<body>\n")
                .append("<h1>CipheRaz — Laboratory Experiment Report</h1>\n")
                .append("<div class=\"meta\">Student: <strong>").append(studentName != null ? studentName : "Cybersecurity Student").append("</strong> • Date: ").append(dateStr).append("</div>\n")
                .append("<table>\n<thead>\n<tr>\n")
                .append("<th>ID</th><th>Timestamp</th><th>Algorithm</th><th>Operation</th><th>Parameters</th><th>Input (B)</th><th>Output (B)</th><th>Duration</th><th>Status</th>\n")
                .append("</tr>\n</thead>\n<tbody>\n");

        for (ExperimentLog log : logs) {
            html.append("<tr>\n")
                    .append("<td>").append(log.id()).append("</td>\n")
                    .append("<td>").append(log.createdAt()).append("</td>\n")
                    .append("<td><strong>").append(log.algorithmName()).append("</strong></td>\n")
                    .append("<td>").append(log.operationType()).append("</td>\n")
                    .append("<td>").append(log.parametersSummary() != null ? log.parametersSummary() : "-").append("</td>\n")
                    .append("<td>").append(log.inputSizeBytes()).append("</td>\n")
                    .append("<td>").append(log.outputSizeBytes()).append("</td>\n")
                    .append("<td>").append(String.format("%.2f ms", log.durationMillis())).append("</td>\n")
                    .append("<td class=\"").append(log.success() ? "success" : "failure").append("\">").append(log.success() ? "PASS" : "FAIL").append("</td>\n")
                    .append("</tr>\n");
        }

        html.append("</tbody>\n</table>\n")
                .append("<div class=\"footer\">CipheRaz Educational Workstation • Developed by Abderrazak BENAROUS</div>\n")
                .append("</body>\n</html>");

        return html.toString();
    }
}
