package com.rs.game.activities.instances;

import java.util.ArrayList;
import java.util.TimerTask;

import com.rs.cores.CoresManager;
import com.rs.cores.FixedLengthRunnable;
import com.rs.game.MapBuilder;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * Enhanced Instance System with improved cleanup and Java 1.7 compatibility
 * Fixed memory leaks and ghost player issues
 * 
 * @author Zeus (Enhanced)
 * @date June 06, 2025
 * @version 2.0 - Fixed ghost players, memory leaks, Java 1.7 compatibility
 * @original @ausky Kris 
 * {@link https://www.rune-server.ee/members/kris/ } 
 */
public abstract class Instance {
	
	public ArrayList<Player> players;
	public final Player owner;
	public final int instanceDuration, respawnSpeed, playersLimit, bossId, password;
	public int totalSeconds;
	public int[] boundChunks, sizes, chunksToBind;
	public final boolean hardMode;
	public boolean isStable, finished;
	public NPC boss;
	
	public Instance(Player owner, int instanceDuration, int respawnSpeed, int playersLimit, int password, int bossId, boolean hardMode) {
		players = new ArrayList<Player>();
		this.owner = owner;
		this.instanceDuration = getTime(owner, instanceDuration);
		this.respawnSpeed = respawnSpeed;
		this.playersLimit = playersLimit;
		this.password = password;
		this.bossId = bossId;
		this.hardMode = hardMode;
		isStable = true;
		addPlayer(owner);
		World.getInstances().add(this);
	}
	
	public static final int getTime(Player player, int time) {
		if (player.isSponsor())
			return (int) (time * 1.3);
		if (player.isDiamond())
			return (int) (time * 1.25);
		if (player.isPlatinum())
			return (int) (time * 1.2);
		if (player.isGold())
			return (int) (time * 1.15);
		if (player.isSilver())
			return (int) (time * 1.1);
		if (player.isBronze())
			return (int) (time * 1.05);
		return time;
	}
	
	public abstract NPC getBossNPC();
	
	public abstract WorldTile getWaitingRoomCoords();
	
	public abstract WorldTile getOutsideCoordinates();
	
	/**
	 * Enhanced instance destruction with proper cleanup
	 */
	public final void destroyInstance() {
		try {
			// Enhanced player cleanup (Java 1.7 compatible)
			if (players != null) {
				ArrayList<Player> playersCopy = new ArrayList<Player>(players);
				for (Player player : playersCopy) {
					if (player != null) {
						try {
							// Clear player's instance reference
							player.setCurrentInstance(null);
							
							// Teleport to outside coordinates (safe location)
							WorldTile safeLocation = getOutsideCoordinates();
							if (safeLocation != null) {
								player.setNextWorldTile(safeLocation);
							} else {
								// Fallback to Lumbridge if outside coordinates are null
								player.setNextWorldTile(new WorldTile(3222, 3218, 0));
							}
							
							// Send notification
							player.sendMessage("The instance has been destroyed. You have been teleported to safety.");
							
						} catch (Exception e) {
							System.err.println("Error cleaning up player " + player.getUsername() + " from instance: " + e.getMessage());
						}
					}
				}
				
				// Clear the players list
				players.clear();
			}
			
			// Destroy map
			if (boundChunks != null && sizes != null) {
				MapBuilder.destroyMap(boundChunks[0], boundChunks[1], sizes[0], sizes[1]);
			}
			
			// Remove from world instances list
			World.getInstances().remove(this);
			
			// Set flags
			isStable = false;
			finished = true;
			
			// Clean up boss
			if (boss != null && !boss.hasFinished()) {
				boss.finish();
				boss = null; // Clear reference to prevent memory leak
			}
			
		} catch (Exception e) {
			System.err.println("Error in destroyInstance(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public final void constructInstance() {
		try {
			MapBuilder.copyAllPlanesMap(chunksToBind[0], chunksToBind[1], boundChunks[0], boundChunks[1], sizes[0], sizes[1]);
			
			if (owner != null) {
				WorldTile waitingRoom = getWaitingRoomCoords();
				if (waitingRoom != null) {
					owner.setNextWorldTile(waitingRoom);
				}
			}
			
			initiateSpawningSequence();
		} catch (Exception e) {
			System.err.println("Error constructing instance: " + e.getMessage());
			destroyInstance(); // Clean up on failure
		}
	}
	
	public final void enterInstance(Player player) {
		if (player == null) return;
		
		if (playersLimit != 0 && players.size() >= playersLimit) {
			player.sendMessage("This instance is currently full.");
			return;
		}
		if (!isStable) {
			player.sendMessage("The instance isn't stable enough to enter it.");
			return;
		}
		if (password != -1 && !player.getDisplayName().equalsIgnoreCase(owner.getDisplayName())) {
			player.getPackets().sendRunScript(108, "Enter password:");
			player.getTemporaryAttributtes().put("instancepasswordenter", this);
			return;
		}
		addPlayer(player);
		
		WorldTile waitingRoom = getWaitingRoomCoords();
		if (waitingRoom != null) {
			player.setNextWorldTile(waitingRoom);
		}
	}
	
	public final void enterInstance(Player player, int password) {
		if (player == null) return;
		
		if (playersLimit != 0 && players.size() >= playersLimit) {
			player.sendMessage("This instance is currently full.");
			return;
		}
		if (!isStable) {
			player.sendMessage("The instance isn't stable enough to enter it.");
			return;
		}
		if (password != this.password) {
			player.sendMessage("The password you entered is incorrect.");
			return;
		}
		addPlayer(player);
		
		WorldTile waitingRoom = getWaitingRoomCoords();
		if (waitingRoom != null) {
			player.setNextWorldTile(waitingRoom);
		}
	}
	
	public abstract void performOnSpawn();
	
	/**
	 * Enhanced spawning sequence with better error handling
	 */
	public void initiateSpawningSequence() {
		final Instance instance = this;
		
		try {
			CoresManager.getServiceProvider().scheduleFixedLengthTask(new FixedLengthRunnable() {
				private int seconds;
				private boolean resetSeconds;
				
				@Override
				public boolean repeat() {
					try {
						// Check if instance should be destroyed
						if ((!isStable && players.size() == 0) || (totalSeconds % 60 == 0 && (totalSeconds / 60) == instanceDuration + 5)) {
							if (players.size() > 0) {
								// Java 1.7 compatible player evacuation
								for (Player player : players) {
									if (player != null && player.getCurrentInstance() == instance) {
										WorldTile outside = getOutsideCoordinates();
										if (outside != null) {
											player.setNextWorldTile(outside);
										} else {
											player.setNextWorldTile(new WorldTile(3222, 3218, 0)); // Lumbridge fallback
										}
									}
								}
							}
							destroyInstance();
							return false; // Stop the task
						}
						
						// Boss spawning logic
						if (seconds == 0 && !finished) {
							resetSeconds = false;
							if (boss == null || boss.hasFinished()) {
								try {
									boss = getBossNPC();
									if (boss != null) {
										boss.setForceMultiArea(true);
										performOnSpawn();
									}
								} catch (Exception e) {
									System.err.println("Error spawning boss in instance: " + e.getMessage());
								}
							}
						}
						
						// Boss respawn logic
						if (boss != null && boss.hasFinished() && !resetSeconds) {
							seconds = 0 - respawnSpeed;
							resetSeconds = true;
						}
						
						// Instance ending warnings
						if (totalSeconds % 60 == 0 && (totalSeconds / 60) == instanceDuration) {
							finished = true;
							// Java 1.7 compatible message sending
							for (Player player : players) {
								if (player != null) {
									player.sendMessage("The instance has ended. No more monsters will be spawned in this instance.");
								}
							}
						}
						
						if (totalSeconds % 60 == 0 && (totalSeconds / 60) == instanceDuration - 2) {
							// Java 1.7 compatible message sending
							for (Player player : players) {
								if (player != null) {
									player.sendMessage("The instance will remain open for two more minutes.");
								}
							}
							isStable = false;
						}
						
						seconds++;
						totalSeconds++;
						return true; // Continue the task
						
					} catch (Exception e) {
						System.err.println("Error in instance spawning sequence: " + e.getMessage());
						e.printStackTrace();
						destroyInstance(); // Clean up on error
						return false; // Stop the task
					}
				}
			}, 0, 1);
		} catch (Exception e) {
			System.err.println("Error initiating spawning sequence: " + e.getMessage());
			destroyInstance(); // Clean up on failure
		}
	}
	
	public WorldTile getWorldTile(int x, int y) {
		if (boundChunks != null && boundChunks.length >= 2) {
			return new WorldTile((boundChunks[0] * 8) + x, (boundChunks[1] * 8) + y, 0);
		}
		return new WorldTile(3222, 3218, 0); // Fallback to Lumbridge
	}
	
	/**
	 * Enhanced player addition with better safety checks
	 */
	public void addPlayer(Player player) {
		if (player == null) return;
		
		try {
			// Remove from previous instance if any
			if (player.getCurrentInstance() != null && player.getCurrentInstance() != this) {
				player.getCurrentInstance().removePlayer(player);
			}
			
			// Add to this instance
			if (!players.contains(player)) {
				players.add(player);
			}
			
			player.setForceMultiArea(true);
			player.setCurrentInstance(this);
			
			String ownerName = (owner != null && owner.getDisplayName() != null) 
				? Utils.formatPlayerNameForDisplay(owner.getDisplayName()) 
				: "Unknown";
			
			player.sendMessage("You've joined " + ownerName + "'s " + (hardMode ? "hard " : "normal ") + 
				"instance. This instance will remain active for approximately " + getMinutesRemaining() + " minutes.");
				
		} catch (Exception e) {
			System.err.println("Error adding player to instance: " + e.getMessage());
		}
	}
	
	/**
	 * Enhanced player removal with proper cleanup
	 */
	public final boolean removePlayer(Player player) {
		if (player == null) return false;
		
		try {
			player.setCurrentInstance(null);
			player.setForceMultiArea(false); // Reset multi area
			return players.remove(player);
		} catch (Exception e) {
			System.err.println("Error removing player from instance: " + e.getMessage());
			return false;
		}
	}
	
	public final int getMinutesRemaining() {
		try {
			return (int) Math.floor(instanceDuration - (totalSeconds / 60));
		} catch (Exception e) {
			return 0;
		}
	}
	
	public final boolean isHardMode() {
		return hardMode;
	}
	
	public final int getBoss() {
		return bossId;
	}
	
	public final int getPassword() {
		return password;
	}
	
	public final int getPlayersLimit() {
		return playersLimit;
	}
	
	public final int getRespawnSpeed() {
		return respawnSpeed;
	}
	
	public final int getInstanceDuration() {
		return instanceDuration;
	}
	
	public final ArrayList<Player> getPlayers() {
		return players;
	}
	
	public final Player getOwner() {
		return owner;
	}
	
	public String getInstanceCoords() {
		try {
			if (owner != null && boundChunks != null && boundChunks.length >= 2) {
				return "x: " + (owner.getX() - (boundChunks[0] * 8)) + " y: " + (owner.getY() - (boundChunks[1] * 8));
			}
		} catch (Exception e) {
			System.err.println("Error getting instance coords: " + e.getMessage());
		}
		return "x: 0 y: 0";
	}
	
	/**
	 * Get unique instance ID for debugging
	 */
	public String getId() {
		return "Instance-" + (owner != null ? owner.getUsername() : "Unknown") + "-" + bossId;
	}
}