package com.rs.game.player.content.interfaces.potionTimer;

import java.io.Serializable;

import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * @author Era || Feb 15, 2021 
 *
 */
public class PotionTimers implements Serializable{

    private static final long serialVersionUID = -8464793901060403125L;

    Player player;

    public PotionTimers(Player player){
        this.player = player;
    }
    public boolean hideSlots;
    public long vengTimer,freezeTimer,ovlTimer,poisonTimer,antifireTimer,renewalTimer,tbTimer,newtimer1,newtime2,newtimer3,newtimer4,newtimer5;
    public long[] slotTimerArray = {vengTimer,freezeTimer,ovlTimer,renewalTimer,poisonTimer,antifireTimer,tbTimer,newtimer1,newtime2,newtimer3,newtimer4,newtimer5};


}
