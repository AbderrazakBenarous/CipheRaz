package com.cryptolab.core.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Embedded SQLite Database Manager for CipheRaz.
 * Stores local experiment session metadata and student exercise attempts completely offline.
 *
 * Developed by Abderrazak BENAROUS
 */
public final class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_DIR = System.getProperty("user.home") + File.separator + ".cipheraz";
    private static final String DB_FILE = DB_DIR + File.separator + "cipheraz_history.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_FILE;

    private static boolean initialized = false;

    private DatabaseManager() {}

    public static synchronized Connection getConnection() throws SQLException {
        ensureDatabaseInitialized();
        return DriverManager.getConnection(JDBC_URL);
    }

    private static synchronized void ensureDatabaseInitialized() throws SQLException {
        if (initialized) return;

        File dir = new File(DB_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement()) {

            // Create experiments table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS experiments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    algorithm_name TEXT NOT NULL,
                    operation_type TEXT NOT NULL,
                    parameters_summary TEXT,
                    input_size_bytes INTEGER,
                    output_size_bytes INTEGER,
                    duration_millis REAL,
                    success INTEGER NOT NULL,
                    notes TEXT
                );
            """);

            // Create exercises progress table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS exercise_progress (
                    exercise_id TEXT PRIMARY KEY,
                    title TEXT NOT NULL,
                    category TEXT NOT NULL,
                    completed INTEGER DEFAULT 0,
                    score INTEGER DEFAULT 0,
                    last_attempt_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    submitted_answer TEXT
                );
            """);

            initialized = true;
            logger.info("CipheRaz SQLite Database initialized successfully at: {}", DB_FILE);
        }
    }

    public static String getDatabasePath() {
        return DB_FILE;
    }
}
