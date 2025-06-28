package com.rs.game.npc.combat;

import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import com.rs.utils.Logger;

public class NPCCombatDefinitionsManager {
    
    private static Map<Integer, NPCCombatDefinitions> combatDefinitions = new HashMap<>();
    private static Map<Integer, NPCCombatDefinitions> originalDefinitions = new HashMap<>();
    
    public static void loadCombatDefinitions() {
        // TODO: Load your existing combat definitions here
        // This depends on how your server currently stores NPC combat data
        Logger.log("NPCCombatDefinitionsManager", "Combat definitions loaded");
    }
    
    public static NPCCombatDefinitions getCombatDefinitions(int npcId) {
        return combatDefinitions.get(npcId);
    }
    
    public static void setCombatDefinitions(int npcId, NPCCombatDefinitions definitions) {
        if (!originalDefinitions.containsKey(npcId) && combatDefinitions.containsKey(npcId)) {
            originalDefinitions.put(npcId, combatDefinitions.get(npcId).copy());
        }
        combatDefinitions.put(npcId, definitions);
    }
    
    public static NPCCombatDefinitions getOriginalCombatDefinitions(int npcId) {
        return originalDefinitions.get(npcId);
    }
    
    public static void loadCustomBossStats() {
        File bossDir = new File("data/npcs/bosses/");
        if (!bossDir.exists()) {
            return;
        }
        
        File[] bossFiles = bossDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (bossFiles == null) return;
        
        for (File file : bossFiles) {
            try {
                String fileName = file.getName();
                int npcId = Integer.parseInt(fileName.substring(0, fileName.length() - 4));
                loadBossStatsFromFile(npcId, file);
            } catch (Exception e) {
                System.err.println("Failed to load boss stats from " + file.getName());
            }
        }
    }
    
    private static void loadBossStatsFromFile(int npcId, File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            NPCCombatDefinitions currentDef = getCombatDefinitions(npcId);
            
            if (currentDef == null) {
                reader.close();
                return;
            }
            
            NPCCombatDefinitions newDef = currentDef.copy();
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//") || line.trim().isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    
                    switch (key) {
                        case "hitpoints":
                            newDef.setHitpoints(Integer.parseInt(value));
                            break;
                        case "maxHit":
                            newDef.setMaxHit(Integer.parseInt(value));
                            break;
                        case "attackStyle":
                            newDef.setAttackStyle(Integer.parseInt(value));
                            break;
                    }
                }
            }
            
            setCombatDefinitions(npcId, newDef);
            reader.close();
            
        } catch (Exception e) {
            System.err.println("Error loading boss stats for NPC " + npcId);
        }
    }
}