package com.rs.game.player.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.utils.Logger;

/**
 * Balance Configuration Manager - Centralized settings for balancing system
 * 
 * @author Zeus
 * @date May 31, 2025
 * @version 2.1 - Configuration Management System
 */
public class BalanceConfig {
    
    private static final String CONFIG_FILE = "data/balance_config.properties";
    private static final Map<String, String> config = new ConcurrentHashMap<String, String>();
    private static boolean loaded = false;
    
    // Default configuration values
    private static final Map<String, String> defaults = new ConcurrentHashMap<String, String>();
    
    static {
        // Initialize default values
        defaults.put("boss.cleanup.interval", "100");
        defaults.put("boss.backup.max", "1000");
        defaults.put("boss.backup.age.hours", "24");
        defaults.put("item.cleanup.interval", "50");
        defaults.put("item.backup.max", "1000");
        defaults.put("item.backup.age.hours", "24");
        defaults.put("system.debug.enabled", "false");
        defaults.put("system.performance.logging", "false");
        defaults.put("balance.validation.strict", "true");
        defaults.put("file.atomic.writes", "true");
        defaults.put("log.max.size.mb", "10");
        defaults.put("batch.max.size", "100");
        defaults.put("tier.min", "1");
        defaults.put("tier.max", "10");
        defaults.put("intensity.min", "0.1");
        defaults.put("intensity.max", "5.0");
    }
    
    /**
     * Load configuration from file or create with defaults
     */
    public static synchronized void loadConfig() {
        if (loaded) {
            return;
        }
        
        BufferedReader reader = null;
        try {
            File configFile = new File(CONFIG_FILE);
            
            if (!configFile.exists()) {
                createDefaultConfig();
            }
            
            reader = new BufferedReader(new FileReader(configFile));
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                
                // Parse key=value pairs
                int equalsIndex = line.indexOf('=');
                if (equalsIndex > 0 && equalsIndex < line.length() - 1) {
                    String key = line.substring(0, equalsIndex).trim();
                    String value = line.substring(equalsIndex + 1).trim();
                    config.put(key, value);
                }
            }
            
            // Fill in any missing values with defaults
            for (Map.Entry<String, String> entry : defaults.entrySet()) {
                if (!config.containsKey(entry.getKey())) {
                    config.put(entry.getKey(), entry.getValue());
                }
            }
            
            loaded = true;
            Logger.log("BalanceConfig", "Configuration loaded successfully");
            
        } catch (Exception e) {
            Logger.handle(e);
            // Fallback to defaults
            config.putAll(defaults);
            loaded = true;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Logger.handle(e);
                }
            }
        }
    }
    
    /**
     * Create default configuration file
     */
    private static void createDefaultConfig() {
        BufferedWriter writer = null;
        try {
            File configFile = new File(CONFIG_FILE);
            File parentDir = configFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            writer = new BufferedWriter(new FileWriter(configFile));
            
            writer.write("# Balance System Configuration v2.1");
            writer.newLine();
            writer.write("# Generated: " + new java.util.Date());
            writer.newLine();
            writer.write("# Edit these values to customize the balance system behavior");
            writer.newLine();
            writer.newLine();
            
            writer.write("# Boss System Settings");
            writer.newLine();
            writer.write("boss.cleanup.interval=100");
            writer.newLine();
            writer.write("boss.backup.max=1000");
            writer.newLine();
            writer.write("boss.backup.age.hours=24");
            writer.newLine();
            writer.newLine();
            
            writer.write("# Item System Settings");
            writer.newLine();
            writer.write("item.cleanup.interval=50");
            writer.newLine();
            writer.write("item.backup.max=1000");
            writer.newLine();
            writer.write("item.backup.age.hours=24");
            writer.newLine();
            writer.newLine();
            
            writer.write("# System Settings");
            writer.newLine();
            writer.write("system.debug.enabled=false");
            writer.newLine();
            writer.write("system.performance.logging=false");
            writer.newLine();
            writer.write("balance.validation.strict=true");
            writer.newLine();
            writer.write("file.atomic.writes=true");
            writer.newLine();
            writer.newLine();
            
            writer.write("# Limits and Validation");
            writer.newLine();
            writer.write("log.max.size.mb=10");
            writer.newLine();
            writer.write("batch.max.size=100");
            writer.newLine();
            writer.write("tier.min=1");
            writer.newLine();
            writer.write("tier.max=10");
            writer.newLine();
            writer.write("intensity.min=0.1");
            writer.newLine();
            writer.write("intensity.max=5.0");
            writer.newLine();
            
            Logger.log("BalanceConfig", "Created default configuration file: " + CONFIG_FILE);
            
        } catch (Exception e) {
            Logger.handle(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Logger.handle(e);
                }
            }
        }
    }
    
    /**
     * Get configuration value as string
     */
    public static String getString(String key) {
        if (!loaded) {
            loadConfig();
        }
        return config.containsKey(key) ? config.get(key) : defaults.get(key);
    }
    
    /**
     * Get configuration value as integer
     */
    public static int getInt(String key) {
        try {
            return Integer.parseInt(getString(key));
        } catch (NumberFormatException e) {
            Logger.handle(e);
            String defaultValue = defaults.get(key);
            return defaultValue != null ? Integer.parseInt(defaultValue) : 0;
        }
    }
    
    /**
     * Get configuration value as double
     */
    public static double getDouble(String key) {
        try {
            return Double.parseDouble(getString(key));
        } catch (NumberFormatException e) {
            Logger.handle(e);
            String defaultValue = defaults.get(key);
            return defaultValue != null ? Double.parseDouble(defaultValue) : 0.0;
        }
    }
    
    /**
     * Get configuration value as boolean
     */
    public static boolean getBoolean(String key) {
        return "true".equalsIgnoreCase(getString(key));
    }
    
    /**
     * Set configuration value (runtime only, doesn't save to file)
     */
    public static void set(String key, String value) {
        if (!loaded) {
            loadConfig();
        }
        config.put(key, value);
    }
    
    /**
     * Save current configuration to file
     */
    public static synchronized void saveConfig() {
        BufferedWriter writer = null;
        try {
            File configFile = new File(CONFIG_FILE);
            File tempFile = new File(CONFIG_FILE + ".tmp");
            
            writer = new BufferedWriter(new FileWriter(tempFile));
            
            writer.write("# Balance System Configuration v2.1");
            writer.newLine();
            writer.write("# Last Updated: " + new java.util.Date());
            writer.newLine();
            writer.write("# Edit these values to customize the balance system behavior");
            writer.newLine();
            writer.newLine();
            
            // Write boss settings
            writer.write("# Boss System Settings");
            writer.newLine();
            writeConfigValue(writer, "boss.cleanup.interval");
            writeConfigValue(writer, "boss.backup.max");
            writeConfigValue(writer, "boss.backup.age.hours");
            writer.newLine();
            
            // Write item settings
            writer.write("# Item System Settings");
            writer.newLine();
            writeConfigValue(writer, "item.cleanup.interval");
            writeConfigValue(writer, "item.backup.max");
            writeConfigValue(writer, "item.backup.age.hours");
            writer.newLine();
            
            // Write system settings
            writer.write("# System Settings");
            writer.newLine();
            writeConfigValue(writer, "system.debug.enabled");
            writeConfigValue(writer, "system.performance.logging");
            writeConfigValue(writer, "balance.validation.strict");
            writeConfigValue(writer, "file.atomic.writes");
            writer.newLine();
            
            // Write limits
            writer.write("# Limits and Validation");
            writer.newLine();
            writeConfigValue(writer, "log.max.size.mb");
            writeConfigValue(writer, "batch.max.size");
            writeConfigValue(writer, "tier.min");
            writeConfigValue(writer, "tier.max");
            writeConfigValue(writer, "intensity.min");
            writeConfigValue(writer, "intensity.max");
            
            writer.close();
            writer = null;
            
            // Atomic replacement
            if (configFile.exists()) {
                configFile.delete();
            }
            tempFile.renameTo(configFile);
            
            Logger.log("BalanceConfig", "Configuration saved successfully");
            
        } catch (Exception e) {
            Logger.handle(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Logger.handle(e);
                }
            }
        }
    }
    
    /**
     * Write a single config value to file
     */
    private static void writeConfigValue(BufferedWriter writer, String key) throws IOException {
        String value = config.get(key);
        if (value == null) {
            value = defaults.get(key);
        }
        writer.write(key + "=" + value);
        writer.newLine();
    }
    
    /**
     * Reload configuration from file
     */
    public static synchronized void reloadConfig() {
        loaded = false;
        config.clear();
        loadConfig();
        Logger.log("BalanceConfig", "Configuration reloaded");
    }
    
    /**
     * Reset to default configuration
     */
    public static synchronized void resetToDefaults() {
        config.clear();
        config.putAll(defaults);
        saveConfig();
        Logger.log("BalanceConfig", "Configuration reset to defaults");
    }
    
    /**
     * Validate current configuration
     */
    public static boolean validateConfig() {
        try {
            // Validate numeric ranges
            int tierMin = getInt("tier.min");
            int tierMax = getInt("tier.max");
            if (tierMin < 1 || tierMax > 10 || tierMin >= tierMax) {
                Logger.log("BalanceConfig", "Invalid tier range: " + tierMin + "-" + tierMax);
                return false;
            }
            
            double intensityMin = getDouble("intensity.min");
            double intensityMax = getDouble("intensity.max");
            if (intensityMin <= 0 || intensityMax <= intensityMin) {
                Logger.log("BalanceConfig", "Invalid intensity range: " + intensityMin + "-" + intensityMax);
                return false;
            }
            
            int batchMax = getInt("batch.max.size");
            if (batchMax <= 0 || batchMax > 1000) {
                Logger.log("BalanceConfig", "Invalid batch size: " + batchMax);
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            Logger.handle(e);
            return false;
        }
    }
    
    /**
     * Get all configuration as a formatted string for display
     */
    public static String getConfigSummary() {
        if (!loaded) {
            loadConfig();
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("=== Balance System Configuration v2.1 ===\n");
        summary.append("Boss Cleanup Interval: ").append(getInt("boss.cleanup.interval")).append("\n");
        summary.append("Boss Backup Limit: ").append(getInt("boss.backup.max")).append("\n");
        summary.append("Item Cleanup Interval: ").append(getInt("item.cleanup.interval")).append("\n");
        summary.append("Item Backup Limit: ").append(getInt("item.backup.max")).append("\n");
        summary.append("Debug Enabled: ").append(getBoolean("system.debug.enabled")).append("\n");
        summary.append("Strict Validation: ").append(getBoolean("balance.validation.strict")).append("\n");
        summary.append("Tier Range: ").append(getInt("tier.min")).append("-").append(getInt("tier.max")).append("\n");
        summary.append("Intensity Range: ").append(getDouble("intensity.min")).append("-").append(getDouble("intensity.max")).append("\n");
        summary.append("Max Batch Size: ").append(getInt("batch.max.size")).append("\n");
        summary.append("Configuration Valid: ").append(validateConfig()).append("\n");
        
        return summary.toString();
    }
    
    // Convenience methods for commonly used values
    public static int getBossCleanupInterval() { return getInt("boss.cleanup.interval"); }
    public static int getBossBackupMax() { return getInt("boss.backup.max"); }
    public static int getItemCleanupInterval() { return getInt("item.cleanup.interval"); }
    public static int getItemBackupMax() { return getInt("item.backup.max"); }
    public static boolean isDebugEnabled() { return getBoolean("system.debug.enabled"); }
    public static boolean isStrictValidation() { return getBoolean("balance.validation.strict"); }
    public static int getBatchMaxSize() { return getInt("batch.max.size"); }
    public static int getTierMin() { return getInt("tier.min"); }
    public static int getTierMax() { return getInt("tier.max"); }
    public static double getIntensityMin() { return getDouble("intensity.min"); }
    public static double getIntensityMax() { return getDouble("intensity.max"); }
    public static long getBackupAgeMillis() { return getInt("boss.backup.age.hours") * 60L * 60L * 1000L; }
}