package com.rs.game.npc.combat;

import java.util.HashMap;
import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class CombatScriptsHandler {
    
    public static final HashMap<Object, CombatScript> cachedCombatScripts = new HashMap<Object, CombatScript>();
    private static final CombatScript DEFAULT_SCRIPT = new Default();
    
    @SuppressWarnings("rawtypes")
    public static final void init() {
        try {
            Class[] classes = Utils.getClasses("com.rs.game.npc.combat.impl");
            for (Class c : classes) {
                if (c.isAnonymousClass()) {
                    continue;
                }
                
                // SIMPLE FIX: Skip inner classes (anything with $ in the name)
                if (c.getName().contains("$")) {
                    System.out.println("Skipping inner class: " + c.getName());
                    continue;
                }
                
                try {
                    Object o = c.newInstance();
                    if (!(o instanceof CombatScript)) {
                        continue;
                    }
                    CombatScript script = (CombatScript) o;
                    for (Object key : script.getKeys()) {
                        cachedCombatScripts.put(key, script);
                    }
                    System.out.println("Loaded combat script: " + c.getSimpleName());
                } catch (Exception e) {
                    // SIMPLE FIX: Catch ALL exceptions and skip problematic classes
                    System.out.println("Skipped problematic class: " + c.getSimpleName() + " - " + e.getMessage());
                    continue;
                }
            }
            Logger.log(cachedCombatScripts.size() + " combat scripts initiated..");
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
    
    public static int specialAttack(final NPC npc, final Entity target) {
        CombatScript script = cachedCombatScripts.get(npc.getId());
        if (script == null) {
            script = cachedCombatScripts.get(npc.getDefinitions().name);
            if (script == null)
                script = DEFAULT_SCRIPT;
        }
        return script.attack(npc, target);
    }
}