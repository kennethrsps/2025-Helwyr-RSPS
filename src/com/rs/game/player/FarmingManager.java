package com.rs.game.player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.NatureSpiritNPC;
import com.rs.game.player.actions.Action;
import com.rs.game.player.actions.Woodcutting.HatchetDefinitions;
import com.rs.game.player.actions.Woodcutting.TreeDefinitions;
import com.rs.game.player.actions.firemaking.Firemaking;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;
/**
 * @author Zeus
 * @date 05.27.2025
 */
/**
 * Manages farming activities for players including planting, harvesting, and maintaining crops.
 * Handles various patch types: allotments, trees, herbs, flowers, fruit trees, bushes, hops, compost bins.
 */
public class FarmingManager implements Serializable {

    private static final long serialVersionUID = -6487741852718632170L;

    // Constants
    private static final int REGENERATION_CONSTANT = 120000;
    private static final int EMPTY_BUCKET = 1925;
    private static final int MAGIC_SECATEURS = 7409;
    
    // Patch type constants
    private static final int ALLOTMENT = 0, TREES = 1, HOPS = 2, FLOWERS = 3, 
                           FRUIT_TREES = 4, BUSHES = 5, HERBS = 6, COMPOST = 7, 
                           MUSHROOMS = 8, BELLADONNA = 9;
    
    private static final String[] PATCH_NAMES = { 
        "allotment", "tree", "hops", "flower", "fruit tree", 
        "bush", "herb", "compost", "mushroom", "belladonna" 
    };
    
    private static final int[][] HARVEST_AMOUNTS = { 
        { 3, 10 }, { 1, 1 }, { 3, 7 }, { 1, 3 }, 
        { 3, 5 }, { 3, 5 }, { 3, 6 } 
    };
    
    // Compost organic items
    public static final int[] COMPOST_ORGANIC = { 
        6055, 1942, 1957, 1965, 5986, 5504, 5982, 249, 251, 253, 255, 257, 
        2998, 259, 261, 263, 3000, 265, 2481, 267, 269, 1951, 753, 2126, 247, 239, 6018 
    };
    
    public static final int[] SUPER_COMPOST_ORGANIC = { 
        2114, 5978, 5980, 5982, 6004, 247, 6469 
    };
    
    // Animation constants
    private static final class Animations {
        static final Animation RAKING = new Animation(2273);
        static final Animation WATERING = new Animation(2293);
        static final Animation SEED_DIPPING = new Animation(2291);
        static final Animation SPADE = new Animation(830);
        static final Animation HERB_PICKING = new Animation(2282);
        static final Animation MAGIC_PICKING = new Animation(2286);
        static final Animation CURE_PLANT = new Animation(2288);
        static final Animation CHECK_TREE = new Animation(832);
        static final Animation PRUNING = new Animation(2275);
        static final Animation FLOWER_PICKING = new Animation(2292);
        static final Animation FRUIT_PICKING = new Animation(2280);
        static final Animation COMPOST = new Animation(2283);
        static final Animation BUSH_PICKING = new Animation(2281);
        static final Animation FILL_COMPOST = new Animation(832);
    }
    
    // Item IDs for products
    public static final int[] PRODUCES = { 
        199, 201, 203, 205, 207, 3049, 209, 211, 213, 3051, 215, 2485, 217, 219, 21626, // herbs
        1942, 1957, 1965, 1982, 5986, 5504, 5982, // allotments
        6010, 6014, 6012, 1793, 255, 6055, 21622, 6016, // flowers
        6006, 5994, 5996, 5931, 5998, 6000, 6002, 1955, 1963, 2108, 5970, 2114, 5972, 5974 // fruit and hops
    };

    private final List<FarmingSpot> spots;
    private transient Player player;

    public FarmingManager() {
        spots = new CopyOnWriteArrayList<>();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void init() {
        spots.forEach(FarmingSpot::refresh);
    }

    public void process() {
        spots.forEach(FarmingSpot::process);
    }

    /**
     * Product information enum containing all farming products with their properties
     */
    private enum ProductInfo {
        // Allotments
        Potato(5318, 1, 1942, 0, 8, 9, 10, ALLOTMENT),
        Onion(5319, 5, 1957, 1, 9.5, 10.5, 10, ALLOTMENT),
        Cabbage(5324, 7, 1965, 2, 10, 11.5, 10, ALLOTMENT),
        Tomato(5322, 12, 1982, 3, 12.5, 14, 10, ALLOTMENT),
        Sweetcorn(5320, 20, 5986, 4, 17, 19, 10, 6, ALLOTMENT),
        Strawberry(5323, 31, 5504, 5, 26, 29, 10, 6, 2, ALLOTMENT),
        Watermelon(5321, 47, 5982, 6, 48.5, 54.5, 10, 8, 4, ALLOTMENT),

        // Herbs
        Guam(5291, 9, 199, 0, 11, 12.5, 20, HERBS),
        Marrentill(5292, 14, 201, 1, 13.5, 15, 20, HERBS),
        Tarromin(5293, 19, 203, 2, 16, 18, 20, HERBS),
        Harralander(5294, 26, 205, 3, 21.5, 24, 20, HERBS),
        Rannar(5295, 32, 207, 4, 27, 30.5, 20, HERBS),
        Toadflax(5296, 38, 3049, 5, 34, 38.5, 20, HERBS),
        Irit(5297, 44, 209, 6, 43, 48.5, 20, HERBS),
        Avantoe(5298, 50, 211, 7, 54.4, 61.5, 20, HERBS),
        Kwuarm(5299, 56, 213, 6, 69, 78, 20, HERBS),
        Snapdragon(5300, 62, 3051, 6, 87.5, 98.5, 20, HERBS),
        Cadantine(5301, 67, 215, 6, 106.5, 120, 20, HERBS),
        Lantadyme(5302, 73, 2485, 6, 134.5, 151.5, 20, HERBS),
        Dwarf(5303, 79, 217, 6, 170.5, 192, 20, HERBS),
        Torstol(5304, 85, 219, 6, 199.5, 224.5, 20, HERBS),
        Fellstalk(21621, 91, 21626, 6, 225, 315.6, 20, HERBS),
        Wergali(14870, 46, 213, 8, 52.8, 52.8, 20, HERBS),
        Gout(6311, 65, 3261, 27, 105, 45, 20, HERBS),

        // Flowers
        Marigold(5096, 2, 6010, 0, 8.5, 47, 5, FLOWERS),
        Rosemary(5097, 11, 6014, 1, 12, 66.5, 5, FLOWERS),
        Nasturtium(5098, 24, 6012, 2, 19.5, 111, 5, FLOWERS),
        Woad(5099, 25, 1793, 3, 20.5, 115.5, 5, FLOWERS),
        Limpwurt(5100, 26, 225, 4, 21.5, 120, 5, FLOWERS),
        White_lily(14589, 52, 14583, 6, 70, 250, 20, 4, -1, FLOWERS),

        // Hops
        Barley(5305, 3, 6006, 9, 8.5, 9.5, 10, 4, 1, HOPS),
        Hammerstone(5307, 4, 5994, 0, 9, 10, 10, 4, 1, HOPS),
        Asgarnian(5308, 8, 5996, 1, 10.9, 12, 10, 5, 3, HOPS),
        Jute(5306, 13, 5931, 10, 13, 14.5, 10, 5, 3, HOPS),
        Yanillian(5309, 16, 5998, 3, 14.5, 16, 10, 6, 1, HOPS),
        Krandorian(5310, 21, 6000, 5, 17.5, 19.5, 10, 7, HOPS),
        Wildbood(5311, 28, 6002, 7, 23, 26, 10, 7, 1, HOPS),

        // Trees
        Oak(5370, 15, 6043, 1, 150, 14, 40, TREES),
        Willow(5371, 30, 6045, 6, 497.25, 25, 40, 6, TREES),
        Maple(5372, 45, 6047, 17, 1107.91, 45, 40, 8, TREES),
        Yew(5373, 60, 6049, 26, 1994.56, 81, 40, 10, TREES),
        Magic(5374, 75, 6051, 41, 3201.15, 145.5, 40, 12, TREES),

        // Fruit Trees
        Apple(5496, 27, 1955, 1, 600, 22, 160, 6, FRUIT_TREES),
        Banana(5497, 33, 1963, 26, 800, 28, 160, 6, FRUIT_TREES),
        Orange(5498, 39, 2108, 65, 1000, 35.5, 160, 6, FRUIT_TREES),
        Curry(5499, 42, 5970, 90, 1200, 40, 160, 6, FRUIT_TREES),
        Pineapple(5500, 51, 2114, 129, 1600, 57, 160, 6, FRUIT_TREES),
        Papaya(5501, 57, 5972, 154, 1800, 72, 160, 6, FRUIT_TREES),
        Palm(5502, 68, 5974, 193, 2000, 110.5, 160, 6, FRUIT_TREES),

        // Bushes
        Redberry(5101, 10, 1951, -4, 64, 11.5, 20, 5, BUSHES),
        Cadavaberry(5102, 22, 753, 6, 102.5, 18, 20, 6, BUSHES),
        Dwellberry(5103, 36, 2126, 19, 177.5, 31.5, 20, 7, BUSHES),
        Jangerberry(5104, 48, 247, 31, 284.5, 50.5, 20, 8, BUSHES),
        Whiteberry(5105, 59, 239, 42, 437.5, 78, 20, 8, BUSHES),
        Poison_ivy(5106, 70, 6018, 188, 675, 120, 20, 8, BUSHES),

        // Compost
        Compost_Bin(7836, 1, -1, 0, 8, 14, 2, 15, COMPOST);

        private static final Map<Short, ProductInfo> PRODUCTS = new HashMap<>();

        static {
            for (ProductInfo product : ProductInfo.values()) {
                PRODUCTS.put((short) product.seedId, product);
            }
        }

        public static ProductInfo getProduct(int itemId) {
            return PRODUCTS.get((short) itemId);
        }

        private final int seedId;
        private final int level;
        private final int productId;
        private final int configIndex;
        private final int type;
        private final int maxStage;
        private final int stageSkip;
        private final double experience;
        private final double plantingExperience;
        private final int cycleTime;

        ProductInfo(int seedId, int level, int productId, int configIndex, 
                   double plantingExperience, double experience, int cycleTime, 
                   int maxStage, int stageSkip, int type) {
            this.seedId = seedId;
            this.level = level;
            this.productId = productId;
            this.configIndex = configIndex;
            this.plantingExperience = plantingExperience;
            this.experience = experience;
            this.cycleTime = cycleTime;
            this.maxStage = maxStage;
            this.stageSkip = stageSkip;
            this.type = type;
        }

        ProductInfo(int seedId, int level, int productId, int configIndex, 
                   double plantingExperience, double experience, int cycleTime, int type) {
            this(seedId, level, productId, configIndex, plantingExperience, 
                 experience, cycleTime, 4, 0, type);
        }

        ProductInfo(int seedId, int level, int productId, int configIndex, 
                   double plantingExperience, double experience, int cycleTime, 
                   int maxStage, int type) {
            this(seedId, level, productId, configIndex, plantingExperience, 
                 experience, cycleTime, maxStage, 0, type);
        }

        // Getters
        public int getSeedId() { return seedId; }
        public int getLevel() { return level; }
        public int getProductId() { return productId; }
        public int getConfigIndex() { return configIndex; }
        public int getType() { return type; }
        public int getMaxStage() { return maxStage; }
        public int getStageSkip() { return stageSkip; }
        public double getExperience() { return experience; }
        public double getPlantingExperience() { return plantingExperience; }
        public int getCycleTime() { return cycleTime; }
    }

    /**
     * Spot information enum containing all farming locations
     */
    public enum SpotInfo {
        // Trees
        Talvery_Tree(8388, TREES),
        Falador_Garden_Tree(8389, TREES),
        Varrock_Tree(8390, TREES),
        Lumbridge_Tree(8391, TREES),
        Gnome_Tree(19147, TREES),

        // Fruit Trees
        Gnome_Strong_Fruit_Tree(7962, FRUIT_TREES),
        Gnome_Fruit_Tree(7963, FRUIT_TREES),
        Brimhaven_Fruit_Tree(7964, FRUIT_TREES),
        Catherby_Fruit_Tree(7965, FRUIT_TREES),
        Lletya_Fruit_Tree(28919, FRUIT_TREES),

        // Allotments
        Falador_Allotment_North(8550, ALLOTMENT),
        Falador_Allotment_South(8551, ALLOTMENT),
        Catherby_Allotment_North(8552, ALLOTMENT),
        Catherby_Allotment_South(8553, ALLOTMENT),
        Ardougne_Allotment_North(8554, ALLOTMENT),
        Ardougne_Allotment_South(8555, ALLOTMENT),
        Canfis_Allotment_North(8556, ALLOTMENT),
        Canfis_Allotment_South(8557, ALLOTMENT),

        // Hops
        Yannile_Hops_Patch(8173, HOPS),
        Talvery_Hops_Patch(8174, HOPS),
        Lumbridge_Hops_Patch(8175, HOPS),
        McGrubor_Hops_Patch(8176, HOPS),

        // Flowers
        Falador_Flower(7847, FLOWERS),
        Catherby_Flower(7848, FLOWERS),
        Ardougne_Flower(7849, FLOWERS),
        Canfis_Flower(7850, FLOWERS),

        // Bushes
        Champions_Bush(7577, BUSHES),
        Rimmington_Bush(7578, BUSHES),
        Etceteria_Bush(7579, BUSHES),
        South_Arddougne_Bush(7580, BUSHES),

        // Herbs
        Falador_Herb_Patch(8150, HERBS),
        Catherby_Herb_Patch(8151, HERBS),
        Ardougne_Herb_Patch(8152, HERBS),
        Canfis_Herb_Patch(8153, HERBS),

        // Compost
        Falador_Compost_Bin(7836, COMPOST),
        Catherby_Bin(7837, COMPOST),
        Port_Phasymatis_Bin(7838, COMPOST),
        Ardougn_Bin(7839, COMPOST),
        Taverly_Bin(66577, COMPOST),

        // Special
        Mushroom_Special(8337, MUSHROOMS),
        Belladonna(7572, BELLADONNA);

        private static final Map<Short, SpotInfo> INFORMATIONS = new HashMap<>();

        static {
            for (SpotInfo information : SpotInfo.values()) {
                INFORMATIONS.put((short) information.objectId, information);
            }
        }

        public static SpotInfo getInfo(int objectId) {
            return INFORMATIONS.get((short) objectId);
        }

        private final int objectId;
        private final int configFileId;
        private final int type;

        SpotInfo(int objectId, int type) {
            this.objectId = objectId;
            this.configFileId = ObjectDefinitions.getObjectDefinitions(objectId).configFileId;
            this.type = type;
        }

        public int getObjectId() { return objectId; }
        public int getConfigFileId() { return configFileId; }
        public int getType() { return type; }
    }

    public FarmingSpot getSpot(SpotInfo info) {
        return spots.stream()
                .filter(spot -> spot.getSpotInfo().equals(info))
                .findFirst()
                .orElse(null);
    }

    public boolean isFarming(int objectId, Item item, int optionId) {
        SpotInfo info = SpotInfo.getInfo(objectId);
        if (info != null) {
            handleFarming(info, item, optionId);
            return true;
        }
        return false;
    }

    public void handleFarming(SpotInfo info, Item item, int optionId) {
        FarmingSpot spot = getSpot(info);
        if (spot == null) {
            spot = new FarmingSpot(info);
        }
        
        if (!spot.isCleared()) {
            handleUnclearedSpot(spot, item, optionId, info);
        } else {
            handleClearedSpot(spot, item, optionId, info);
        }
    }

    private void handleUnclearedSpot(FarmingSpot spot, Item item, int optionId, SpotInfo info) {
        if (item != null && info.getType() == COMPOST) {
            fillCompostBin(spot, item);
        } else if (item == null) {
            switch (optionId) {
                case 1: // rake
                    if (info.getType() == COMPOST) {
                        handleCompostBinClose(spot);
                    } else {
                        startRakeAction(spot);
                    }
                    break;
                case 2: // inspect
                    sendNeedsWeeding(false);
                    break;
                case 4: // guide
                    openGuide();
                    break;
            }
        }
    }

    private void handleClearedSpot(FarmingSpot spot, Item item, int optionId, SpotInfo info) {
        if (item != null) {
            handleItemOnSpot(spot, item);
        } else if (spot.getProductInfo() != null) {
            handleSpotInteraction(spot, optionId, info);
        }
    }

    private void handleCompostBinClose(FarmingSpot spot) {
        if (spot.getHarvestAmount() == 15) {
            spot.setCleared(true);
            spot.setActive(ProductInfo.Compost_Bin);
            spot.setHarvestAmount(15);
            spot.refresh();
            player.sendMessage("You close the compost bin.");
            player.sendMessage("The vegetation begins to decompose.");
        }
    }

    private void handleItemOnSpot(FarmingSpot spot, Item item) {
        String itemName = item.getName().toLowerCase();
        
        if (itemName.startsWith("watering can (")) {
            startWateringAction(spot, item);
        } else if (itemName.contains("compost")) {
            startCompostAction(spot, item, itemName.equals("supercompost"));
        } else if (item.getId() == 6036) {
            startCureAction(spot, item);
        } else {
            startFarmingCycle(spot, item);
        }
    }

    private void handleSpotInteraction(FarmingSpot spot, int optionId, SpotInfo info) {
        switch (optionId) {
            case 1:
                handlePrimaryAction(spot, info);
                break;
            case 2: // inspect
                player.sendMessage("There's something growing in this patch..");
                break;
            case 3: // clear & guide
                handleSecondaryAction(spot, info);
                break;
        }
    }

    private void handlePrimaryAction(FarmingSpot spot, SpotInfo info) {
        int type = info.getType();
        
        if (type == TREES) {
            handleTreeAction(spot);
        } else if (type == FRUIT_TREES) {
            handleFruitTreeAction(spot);
        } else if (type == BUSHES) {
            handleBushAction(spot);
        } else if (type == COMPOST) {
            handleCompostAction(spot);
        } else {
            handleGenericPlantAction(spot);
        }
    }

    private void handleTreeAction(FarmingSpot spot) {
        if (spot.reachedMaxStage() && !spot.hasChecked()) {
            checkHealth(spot);
        } else if (spot.reachedMaxStage() && !spot.isEmpty()) {
            collectTreeProducts(spot, TreeDefinitions.valueOf(spot.getProductInfo().name().toUpperCase()));
        } else if (spot.reachedMaxStage() && spot.isEmpty()) {
            startHarvestingAction(spot);
        } else if (spot.isDead()) {
            clearFarmingPatch(spot);
        } else if (spot.isDiseased()) {
            startCureAction(spot, null);
        }
    }

    private void handleFruitTreeAction(FarmingSpot spot) {
        if (spot.reachedMaxStage() && !spot.hasChecked()) {
            checkHealth(spot);
        } else if (spot.reachedMaxStage() && !spot.hasEmptyHarvestAmount()) {
            startPickingAction(spot);
        } else if (spot.reachedMaxStage() && !spot.isEmpty()) {
            collectTreeProducts(spot, TreeDefinitions.FRUIT_TREES);
        } else if (spot.reachedMaxStage() && spot.isEmpty() || spot.isDead()) {
            clearFarmingPatch(spot);
        } else if (spot.isDiseased()) {
            startCureAction(spot, null);
        }
    }

    private void handleBushAction(FarmingSpot spot) {
        if (spot.reachedMaxStage() && !spot.hasChecked()) {
            checkHealth(spot);
        } else if (spot.reachedMaxStage() && !spot.hasEmptyHarvestAmount()) {
            startPickingAction(spot);
        } else if (spot.isDead()) {
            clearFarmingPatch(spot);
        } else if (spot.isDiseased()) {
            startCureAction(spot, null);
        }
    }

    private void handleCompostAction(FarmingSpot spot) {
        if (spot.reachedMaxStage() && !spot.hasChecked()) {
            spot.setChecked(true);
            spot.refresh();
            player.sendMessage("You open the compost bin.");
        } else if (!spot.reachedMaxStage()) {
            player.sendMessage("The weeds haven't finished decomposing yet.");
        } else {
            clearCompostAction(spot);
        }
    }

    private void handleGenericPlantAction(FarmingSpot spot) {
        if (spot.reachedMaxStage() && !spot.isDead()) {
            startHarvestingAction(spot);
        } else if (spot.isDead()) {
            clearFarmingPatch(spot);
        }
    }

    private void handleSecondaryAction(FarmingSpot spot, SpotInfo info) {
        if (spot.isDiseased() || spot.reachedMaxStage()) {
            clearFarmingPatch(spot);
        } else if (info.getType() == FRUIT_TREES) {
            if (!spot.reachedMaxStage()) {
                clearFarmingPatch(spot);
            }
        } else {
            openGuide();
        }
    }

    private void startRakeAction(final FarmingSpot spot) {
        player.getActionManager().setAction(new Action() {
            @Override
            public boolean start(Player player) {
                if (!player.getInventory().containsOneItem(5341)) {
                    player.sendMessage("You need a rake to do this.");
                    return false;
                }
                return true;
            }

            @Override
            public boolean process(Player player) {
                return spot.getStage() != 3;
            }

            @Override
            public int processWithDelay(Player player) {
                player.setNextAnimation(Animations.RAKING);
                if (Utils.random(1) == 0) {
                    spot.increaseStage();
                    if (spot.getStage() == 3) {
                        spot.setCleared(true);
                    }
                    player.getInventory().addItem(6055, 1);
                    player.getSkills().addXp(Skills.FARMING, 8);
                }
                return 1;
            }

            @Override
            public void stop(Player player) {
                setActionDelay(player, 1);
            }
        });
    }

    public void startHarvestingAction(final FarmingSpot spot) {
        final String patchName = getPatchName(spot.getProductInfo().getType());
        
        player.getActionManager().setAction(new Action() {
            @Override
            public boolean start(Player player) {
                if (!player.getInventory().hasFreeSlots()) {
                    player.sendMessage("You do not have enough inventory space to do that.");
                    return false;
                }
                
                if (spot.hasEmptyHarvestAmount() && !spot.hasGivenAmount()) {
                    spot.setHarvestAmount(getRandomHarvestAmount(spot.getProductInfo().getType()));
                    spot.setHasGivenAmount(true);
                } else if (spot.getHarvestAmount() <= 0) {
                    player.sendMessage("You have successfully harvested this patch for new crops.");
                    player.setNextAnimation(new Animation(-1));
                    spot.setIdle();
                    return false;
                }
                
                player.sendMessage("You begin to harvest the " + patchName + " patch.");
                setActionDelay(player, 1);
                return true;
            }

            @Override
            public boolean process(Player player) {
                if (spot.getHarvestAmount() > 0) {
                    return true;
                } else {
                    player.sendMessage("You harvest the produce in the " + patchName
                            + " patch; produce harvested: " + Colors.red
                            + Utils.getFormattedNumber(player.produceGathered) + "</col>.", true);
                    player.setNextAnimation(new Animation(-1));
                    spot.setIdle();
                    return false;
                }
            }

            @Override
            public int processWithDelay(Player player) {
                spot.decreaseHarvestAmount();
                player.setNextAnimation(getHarvestAnimation(spot.getProductInfo().getType()));
                
                double experience = spot.getProductInfo().getExperience();
                if (player.getEquipment().getWeaponId() == MAGIC_SECATEURS || player.getPerkManager().greenThumb) {
                    experience += Utils.random(1, 7);
                } else {
                    experience += Utils.random(1, 4);
                }
                
                player.getSkills().addXp(Skills.FARMING, experience);
                player.addProduceGathered();
                player.getInventory().addItem(spot.getProductInfo().getProductId(), 1);
                Pets.checkSkillingPet(player, 38087);
                
                if (player.hasCleansingActivated() && Utils.random(10) == 0) {
                    player.getInventory().addItem(spot.getProductInfo().getSeedId(), 1);
                    player.sendMessage(Colors.green + "<shad=000000>Your Scroll of Life "
                            + "saves you a seed!", true);
                }
                return 2;
            }

            @Override
            public void stop(Player player) {
                setActionDelay(player, 3);
            }
        });
    }

    private void startPickingAction(final FarmingSpot spot) {
        player.getActionManager().setAction(new Action() {
            @Override
            public boolean start(Player player) {
                return true;
            }

            @Override
            public boolean process(Player player) {
                if (spot.getHarvestAmount() > 0) {
                    return true;
                } else {
                    String produceType = (spot.getProductInfo().getType() == FRUIT_TREES) ? "fruits" : "berries";
                    player.sendMessage("You pick all of the " + produceType + " from the "
                            + getPatchName(spot.getProductInfo().getType()) + " patch; produce harvested: " + Colors.red
                            + Utils.getFormattedNumber(player.produceGathered) + "</col>.", true);
                    player.setNextAnimation(new Animation(-1));
                    return false;
                }
            }

            @Override
            public int processWithDelay(Player player) {
                String itemName = ItemDefinitions.getItemDefinitions(spot.getProductInfo().getProductId())
                        .getName().toLowerCase();
                player.sendMessage("You pick a " + itemName + ".");
                player.setNextAnimation(getHarvestAnimation(spot.getProductInfo().getType()));
                player.getProduceGathered();
                player.getSkills().addXp(Skills.FARMING, spot.getProductInfo().getExperience());
                player.getInventory().addItem(spot.getProductInfo().getProductId(), 1);
                spot.decreaseHarvestAmount();
                spot.refresh();
                
                if (spot.getCycleTime() < Utils.currentTimeMillis()) {
                    spot.setCycleTime(REGENERATION_CONSTANT);
                }
                return 2;
            }

            @Override
            public void stop(Player player) {
                setActionDelay(player, 1);
            }
        });
    }

    public void clearCompostAction(final FarmingSpot spot) {
        player.getActionManager().setAction(new Action() {
            @Override
            public boolean start(Player player) {
                if (spot == null) {
                    return false;
                } else if (!player.getInventory().containsItem(EMPTY_BUCKET, 1)) {
                    player.sendMessage("You need an empty bucket to clear the compost.");
                    return false;
                }
                return true;
            }

            @Override
            public boolean process(Player player) {
                if (!player.getInventory().containsItem(EMPTY_BUCKET, 1)) {
                    player.sendMessage("You need an empty bucket to clear the compost.");
                    return false;
                } else if (spot.getHarvestAmount() > 0) {
                    return true;
                } else {
                    spot.setCleared(false);
                    spot.refresh();
                    spot.setProductInfo(null);
                    spot.remove();
                    player.setNextAnimation(new Animation(-1));
                    return false;
                }
            }

            @Override
            public int processWithDelay(Player player) {
                player.setNextAnimation(Animations.FILL_COMPOST);
                player.getSkills().addXp(Skills.FARMING, 5);
                player.getInventory().deleteItem(EMPTY_BUCKET, 1);
                player.getInventory().addItem(spot.getCompost() ? 6032 : 6034, 1);
                spot.decreaseHarvestAmount();
                spot.refresh();
                return 2;
            }

            @Override
            public void stop(Player player) {
                setActionDelay(player, 1);
            }
        });
    }

    public void clearFarmingPatch(final FarmingSpot spot) {
        final String patchName = getPatchName(spot.getProductInfo().getType());
        
        player.getActionManager().setAction(new Action() {
            private int stage;

            @Override
            public boolean start(Player player) {
                if (!player.getInventory().containsOneItem(952)) {
                    player.sendMessage("You need a spade to do this.");
                    return false;
                }
                player.sendMessage("You start digging up the produce in the " + patchName + " patch.");
                return true;
            }

            @Override
            public boolean process(Player player) {
                if (stage != 2) {
                    return true;
                } else {
                    player.sendMessage("You clear the " + patchName + " and get it ready for new crops.");
                    player.setNextAnimation(new Animation(-1));
                    spot.setIdle();
                    return false;
                }
            }

            @Override
            public int processWithDelay(Player player) {
                player.setNextAnimation(Animations.SPADE);
                if (Utils.random(3) == 0) {
                    stage++;
                }
                return 2;
            }

            @Override
            public void stop(Player player) {
                setActionDelay(player, 3);
            }
        });
    }

    public boolean startFarmingCycle(FarmingSpot spot, Item item) {
        ProductInfo productInfo = ProductInfo.getProduct(item.getId());
        if (spot == null || productInfo == null
                || spot.getSpotInfo().getType() != productInfo.getType() || !spot.isCleared()
                || spot.getProductInfo() != null || spot.getSpotInfo().getType() == COMPOST) {
            return false;
        }
        
        String patchName = getPatchName(productInfo.getType());
        String itemName = item.getDefinitions().getName().toLowerCase();
        int requiredAmount = (productInfo.getType() == ALLOTMENT || productInfo.getType() == HOPS) ? 3 : 1;
        boolean isTree = productInfo.getType() == TREES || productInfo.getType() == FRUIT_TREES;
        int level = productInfo.getLevel();
        
        if (!player.getInventory().containsItem(item.getId(), requiredAmount)) {
            player.sendMessage("You don't have enough " + itemName + " to plant "
                    + (patchName.startsWith("(?i)[^aeiou]") ? "an" : "a") + " " + patchName + " patch.");
            return true;
        } else if (player.getSkills().getLevel(Skills.FARMING) < level) {
            player.sendMessage("You need a farming level of " + level
                    + " to plant this " + (isTree ? "sapling" : "seed") + ".");
            return true;
        }
        
        if (!player.getInventory().containsOneItem(5343)) {
            player.sendMessage("You need a seed dibber to do this.");
            return false;
        }
        
        if (!player.getInventory().containsOneItem(5325)) {
            player.sendMessage("You need a gardening trowel to do this.");
            return false;
        }
        
        player.sendMessage("You plant the " + itemName + " in the " + patchName + " patch.");
        player.setNextAnimation(isTree ? Animations.SPADE : Animations.SEED_DIPPING);
        player.getSkills().addXp(Skills.FARMING, 
                isTree ? productInfo.getExperience() : productInfo.getPlantingExperience());
        player.getInventory().deleteItem(new Item(item.getId(), requiredAmount));
        
        if (isTree) {
            if (player.getInventory().hasFreeSlots()) {
                player.getInventory().addItem(new Item(5350));
            } else {
                World.updateGroundItem(new Item(5350), player, player, 60, 0);
            }
        }
        
        spot.setActive(productInfo);
        return true;
    }

    public boolean startWateringAction(final FarmingSpot spot, Item item) {
        if (spot == null || spot.getProductInfo() == null) {
            return false;
        }
        
        if (item.getName().toLowerCase().startsWith("watering can(")) {
            player.sendMessage("You can't water your plants with an empty watering can!");
            return true;
        } else if (spot.isWatered()) {
            player.sendMessage("This patch has already been watered.");
            return true;
        } else if (spot.reachedMaxStage() || spot.getProductInfo().getType() == HERBS
                || spot.getProductInfo().getType() == COMPOST
                || spot.getProductInfo().getType() == TREES
                || spot.getProductInfo().getType() == FRUIT_TREES
                || spot.getProductInfo() == ProductInfo.White_lily
                || spot.getProductInfo().getType() == BUSHES) {
            player.sendMessage("This patch doesn't need watering.");
            return true;
        } else if (spot.isDiseased()) {
            player.sendMessage("This crop is diseased and needs to be cured.");
            return true;
        } else if (spot.isDead()) {
            player.sendMessage("You can't water dead produce!");
            return true;
        }
        
        player.getInventory().deleteItem(item.getId(), 1);
        if (item.getId() == 5333) {
            player.getInventory().addItem(5331, 1);
        } else {
            player.getInventory().addItem(item.getId() - 1, 1);
        }
        
        player.sendMessage("You water the " + spot.getProductInfo() + ".");
        player.setNextAnimation(Animations.WATERING);
        spot.setWatered(true);

        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                spot.refresh();
            }
        }, 2);
        return true;
    }

    public boolean startCureAction(final FarmingSpot spot, final Item item) {
        if (spot == null || spot.getProductInfo() == null
                || spot.getProductInfo().getType() == COMPOST) {
            return false;
        }
        
        final boolean isTree = spot.getProductInfo().getType() == TREES
                || spot.getProductInfo().getType() == FRUIT_TREES;
        final boolean isBush = spot.getProductInfo().getType() == BUSHES;
        
        if (!spot.isDiseased()) {
            player.sendMessage("The produce in this patch isn't diseased and it doesn't need to be cured.");
            return true;
        }
        
        String message = isTree ? "You prune the " + spot.getProductInfo().name().toLowerCase()
                + " tree's diseased branches." 
                : isBush ? "You prune the " + spot.getProductInfo().name().toLowerCase()
                + " bush's diseased leaves." 
                : "You treat the " + getPatchName(spot.getSpotInfo().getType())
                + " patch with the plant cure.";
        
        player.sendMessage(message);
        player.setNextAnimation((isTree || isBush) ? Animations.PRUNING : Animations.CURE_PLANT);
        spot.setDiseased(false);
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                if (!isTree && !isBush) {
                    player.getInventory().deleteItem(item);
                    player.getInventory().addItem(new Item(229, 1));
                } else {
                    player.setNextAnimation(new Animation(-1));
                }
                player.sendMessage("The produce in this patch has been restored to its natural health.");
                spot.refresh();
            }
        }, 2);
        return true;
    }

    public boolean startCompostAction(final FarmingSpot spot, final Item item, boolean superCompost) {
        if (spot == null || spot.getSpotInfo().getType() == COMPOST) {
            return false;
        }
        
        if (spot.hasCompost()) {
            player.sendMessage("This patch is saturated by "
                    + (superCompost ? "supercompost" : "compost") + ".");
            return true;
        } else if (!spot.isCleared()) {
            player.sendMessage("The patch needs to be cleared in order to saturate it with compost.");
            return true;
        }
        
        player.sendMessage("You saturate the patch with "
                + (superCompost ? "supercompost" : "compost") + ".");
        player.setNextAnimation(Animations.COMPOST);
        
        if (superCompost) {
            spot.setSuperCompost(true);
        } else {
            spot.setCompost(true);
        }
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                player.getInventory().deleteItem(item);
                player.getInventory().addItem(EMPTY_BUCKET, 1);
                player.getSkills().addXp(Skills.FARMING, 8);
                spot.refresh();
            }
        }, 1);
        return true;
    }

    private void collectTreeProducts(final FarmingSpot spot, final TreeDefinitions definitions) {
        player.getActionManager().setAction(new Action() {
            private HatchetDefinitions hatchet;

            @Override
            public boolean start(Player player) {
                if (!checkAll(player)) {
                    return false;
                }
                player.sendMessage("You swing your hatchet at the tree...");
                setActionDelay(player, getWoodcuttingDelay(player));
                return true;
            }

            private int getWoodcuttingDelay(Player player) {
                int summoningBonus = player.getFamiliar() != null 
                        ? (player.getFamiliar().getId() == 6808 || player.getFamiliar().getId() == 6807) ? 10 : 0 
                        : 0;
                int wcTimer = definitions.getLogBaseTime()
                        - (player.getSkills().getLevel(8) + summoningBonus)
                        - Utils.getRandom(hatchet.getAxeTime());
                if (wcTimer < 1 + definitions.getLogRandomTime()) {
                    wcTimer = 1 + Utils.getRandom(definitions.getLogRandomTime());
                }
                wcTimer /= player.getAuraManager().getWoodcuttingAccurayMultiplier();
                return wcTimer;
            }

            private boolean checkAll(Player player) {
                for (HatchetDefinitions def : HatchetDefinitions.values()) {
                    if (player.getInventory().containsOneItem(def.getItemId())) {
                        hatchet = def;
                        if (player.getSkills().getLevel(Skills.WOODCUTTING) >= hatchet.getLevelRequried()) {
                            break;
                        } else {
                            hatchet = null;
                        }
                    }
                }
                
                if (hatchet == null) {
                    player.sendMessage("You dont have the required level to use that axe or you don't have a hatchet.");
                    return false;
                }
                
                if (!hasWoodcuttingLevel(player)) {
                    return false;
                }
                
                if (!player.getInventory().hasFreeSlots()) {
                    player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
                    return false;
                }
                return true;
            }

            private boolean hasWoodcuttingLevel(Player player) {
                if (definitions.getLevel() > player.getSkills().getLevel(8)) {
                    player.sendMessage("You need a woodcutting level of "
                            + definitions.getLevel() + " to chop down this tree.");
                    return false;
                }
                return true;
            }

            private double getWoodcuttingSetBonus(Player player) {
                double xpBoost = 1.0;
                if (player.getEquipment().getChestId() == 10939) xpBoost *= 1.01;
                if (player.getEquipment().getLegsId() == 10940) xpBoost *= 1.01;
                if (player.getEquipment().getHatId() == 10941) xpBoost *= 1.01;
                if (player.getEquipment().getBootsId() == 10933) xpBoost *= 1.01;
                
                if (player.getEquipment().getChestId() == 10939
                        && player.getEquipment().getLegsId() == 10940
                        && player.getEquipment().getHatId() == 10941
                        && player.getEquipment().getBootsId() == 10933) {
                    xpBoost *= 1.01;
                }
                
                if (Wilderness.isAtWild(player) && player.getEquipment().getGlovesId() == 13850) {
                    xpBoost *= 1.01;
                }
                return xpBoost;
            }

            private void addLog(Player player) {
                player.getSkills().addXp(Skills.WOODCUTTING, definitions.getXp() * getWoodcuttingSetBonus(player));
                
                if (definitions == TreeDefinitions.IVY) {
                    player.getPackets().sendGameMessage("You succesfully cut an ivy vine.", true);
                } else {
                    String logName = ItemDefinitions.getItemDefinitions(definitions.getLogsId())
                            .getName().toLowerCase();
                    player.addLogsChopped();
                    player.getPackets().sendGameMessage("You get some " + logName
                            + "; total chopped: " + Utils.getFormattedNumber(player.getLogsChopped()) + ".", true);

                    boolean infAdze = Utils.random(3) == 1 && player.getEquipment().getWeaponId() == 13661
                            && definitions != TreeDefinitions.IVY;
                    
                    if (infAdze) {
                        player.addLogsBurned();
                        player.getSkills().addXp(Skills.FIREMAKING, 
                                Firemaking.increasedExperience(player, definitions.getXp()));
                        player.getPackets().sendGameMessage("The adze's heat instantly incinerates the " + logName
                                + "; logs burned: " + Utils.getFormattedNumber(player.getLogsBurned()) + ".", true);
                        World.sendProjectile(player, player, 
                                new WorldTile(player.getX(), player.getY() - 3, 0), 1776, 30, 0, 15, 0, 0, 0);
                    } else {
                        player.getInventory().addItem(definitions.getLogsId(), 1);
                    }
                }
            }

            @Override
            public boolean process(Player player) {
                player.setNextAnimation(new Animation(hatchet.getEmoteId()));
                if (Utils.random(250) == 0) {
                    new NatureSpiritNPC(player, player);
                    player.sendMessage("<col=ff0000>A Nature Spirit emerges from the tree.");
                }
                return checkTree();
            }

            private boolean usedDeplateAurora;

            @Override
            public int processWithDelay(Player player) {
                addLog(player);
                
                if (!usedDeplateAurora && (1 + Math.random()) < player.getAuraManager().getChanceNotDepleteMN_WC()) {
                    usedDeplateAurora = true;
                } else if (Utils.getRandom(definitions.getRandomLifeProbability()) == 0) {
                    int time = definitions.getRespawnDelay();
                    spot.setEmpty(true);
                    spot.refresh();
                    spot.setCycleTime(true, time * 1000L);
                    player.setNextAnimation(new Animation(-1));
                    return -1;
                }
                
                if (!player.getInventory().hasFreeSlots()) {
                    player.setNextAnimation(new Animation(-1));
                    player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
                    return -1;
                }
                return getWoodcuttingDelay(player);
            }

            private boolean checkTree() {
                return spot != null && !spot.isEmpty();
            }

            @Override
            public void stop(Player player) {
                setActionDelay(player, 3);
            }
        });
    }

    private void fillCompostBin(final FarmingSpot spot, final Item item) {
        final boolean[] attributes = isOrganicItem(item.getId());
        
        player.getActionManager().setAction(new Action() {
            @Override
            public boolean start(Player player) {
                if (item == null || !player.getInventory().containsItem(item.getId(), 1) || spot.isCleared()) {
                    return false;
                } else if (!attributes[0]) {
                    player.sendMessage("You cannot add that to the compost bin.");
                    return false;
                }
                return true;
            }

            @Override
            public boolean process(Player player) {
                return spot.getHarvestAmount() != 15
                        && player.getInventory().containsItem(item.getId(), 1);
            }

            @Override
            public int processWithDelay(Player player) {
                player.setNextAnimation(Animations.FILL_COMPOST);
                player.getInventory().deleteItem(item.getId(), 1);
                spot.increaseHarvestAmount();
                spot.refresh();
                return 1;
            }

            @Override
            public void stop(Player player) {
                setActionDelay(player, 2);
            }
        });
    }

    private void checkHealth(final FarmingSpot spot) {
        String plantType = (spot.getProductInfo().getType() == TREES || spot.getProductInfo().getType() == FRUIT_TREES) 
                ? "tree" : "bush";
        player.sendMessage("You examine the " + plantType
                + " for signs of disease, and quickly realise that it is at full health.");
        player.getSkills().addXp(Skills.FARMING, spot.getProductInfo().getPlantingExperience());
        player.setNextAnimation(Animations.CHECK_TREE);
        spot.setChecked(true);
        spot.refresh();
    }

    private String getPatchName(int type) {
        return PATCH_NAMES[type];
    }

    private int getRandomHarvestAmount(int type) {
        int baseAmount = HARVEST_AMOUNTS[type][0];
        int maximumAmount = HARVEST_AMOUNTS[type][1];
        int totalAmount = Utils.random(baseAmount, maximumAmount);
        
        if (player.getEquipment().getWeaponId() == MAGIC_SECATEURS) {
            totalAmount = (int) (totalAmount * 1.15);
        }
        if (player.getPerkManager().greenThumb) {
            totalAmount = (int) (totalAmount * 1.15);
        }
        return totalAmount;
    }

    private Animation getHarvestAnimation(int type) {
        switch (type) {
            case ALLOTMENT:
            case HOPS:
            case TREES:
            case MUSHROOMS:
            case BELLADONNA:
                return Animations.SPADE;
            case HERBS:
                return (player.getEquipment().getWeaponId() == MAGIC_SECATEURS) 
                        ? Animations.MAGIC_PICKING : Animations.HERB_PICKING;
            case FLOWERS:
                return (player.getEquipment().getWeaponId() == MAGIC_SECATEURS) 
                        ? Animations.MAGIC_PICKING : Animations.FLOWER_PICKING;
            case FRUIT_TREES:
                return Animations.FRUIT_PICKING;
            case BUSHES:
                return Animations.BUSH_PICKING;
            default:
                return Animations.SPADE;
        }
    }

    private void sendNeedsWeeding(boolean cleared) {
        player.sendMessage(cleared ? "The patch is ready for planting." : "The patch needs weeding.");
    }

    private void openGuide() {
        player.sendMessage("Google 'RS Wiki Farming' if you need help with training Farming.");
    }

    private boolean[] isOrganicItem(int itemId) {
        boolean[] bools = new boolean[2];
        
        for (int organicId : COMPOST_ORGANIC) {
            if (itemId == organicId) {
                bools[0] = true;
                bools[1] = false;
                return bools;
            }
        }
        
        for (int organicId : SUPER_COMPOST_ORGANIC) {
            if (itemId == organicId) {
                bools[0] = true;
                bools[1] = true;
                return bools;
            }
        }
        
        return bools;
    }

    public void resetSpots() {
        spots.clear();
    }

    public void resetTreeTrunks() {
        spots.stream()
                .filter(spot -> spot.getSpotInfo().getType() == TREES || spot.getSpotInfo().getType() == FRUIT_TREES)
                .filter(FarmingSpot::isEmpty)
                .forEach(spot -> {
                    spot.setEmpty(false);
                    spot.refresh();
                });
    }

    public boolean checkWaterCan(Player player) {
        int[] wateringCanIds = {5333, 5334, 5335, 5336, 5337, 5338, 5339, 5340};
        
        if (!player.getInventory().containsOneItem(wateringCanIds)) {
            player.sendMessage("You will need a watering can with water in it to do this!");
            return true;
        }
        
        // Find which watering can the player has and replace with emptier version
        for (int i = wateringCanIds.length - 1; i >= 0; i--) {
            if (player.getInventory().containsItem(wateringCanIds[i], 1)) {
                player.getInventory().deleteItem(wateringCanIds[i], 1);
                if (i == 0) {
                    player.getInventory().addItem(5331, 1); // Empty can
                } else {
                    player.getInventory().addItem(wateringCanIds[i - 1], 1);
                }
                break;
            }
        }
        return false;
    }

    /**
     * Inner class representing a farming spot with all its states and properties
     */
    public class FarmingSpot implements Serializable {
        private static final long serialVersionUID = -732322970478931771L;

        private final SpotInfo spotInfo;
        private ProductInfo productInfo;
        private int stage;
        private long cycleTime;
        private int harvestAmount;
        private final boolean[] attributes;

        public FarmingSpot(SpotInfo spotInfo) {
            this.spotInfo = spotInfo;
            this.cycleTime = Utils.currentTimeMillis();
            this.stage = 0;
            this.harvestAmount = 0;
            this.attributes = new boolean[10]; // diseased, watered, dead, firstCycle, cleared, checked, empty, compost, superCompost, givenAmount
            renewCycle();
            spots.add(this);
        }

        public void setActive(ProductInfo productInfo) {
            setProductInfo(productInfo);
            stage = -1;
            resetCycle();
        }

        private void resetCycle() {
            cycleTime = Utils.currentTimeMillis();
            harvestAmount = 0;
            for (int index = 0; index < attributes.length; index++) {
                if (index != 4) { // Don't reset cleared status
                    attributes[index] = false;
                }
            }
        }

        public void setCycleTime(long cycleTime) {
            setCycleTime(false, cycleTime);
        }

        public void setCycleTime(boolean reset, long cycleTime) {
            if (reset) {
                this.cycleTime = 0;
            }
            if (this.cycleTime == 0) {
                this.cycleTime = Utils.currentTimeMillis();
            }
            this.cycleTime += cycleTime;
        }

        public void setIdle() {
            stage = 3;
            setProductInfo(null);
            refresh();
            resetCycle();
        }

        public void process() {
            if (cycleTime == 0) {
                return;
            }
            
            while (cycleTime < Utils.currentTimeMillis()) {
                if (productInfo != null) {
                    if (hasChecked() && (isEmpty() || !hasMaximumRegeneration())) {
                        handleRegeneration();
                        return;
                    } else {
                        increaseStage();
                        if (reachedMaxStage() || isDead()) {
                            cycleTime = 0;
                            break;
                        }
                    }
                } else {
                    if (spotInfo.getType() != COMPOST) {
                        decreaseStage();
                        if (stage <= 0) {
                            remove();
                            break;
                        }
                    }
                }
                renewCycle();
            }
        }

        private void handleRegeneration() {
            if (isEmpty()) {
                setEmpty(false);
                if (productInfo.getType() == FRUIT_TREES) {
                    setCycleTime(REGENERATION_CONSTANT);
                } else {
                    cycleTime = 0;
                }
            } else if (!hasMaximumRegeneration()) {
                if (harvestAmount == 5) {
                    cycleTime = 0;
                } else {
                    cycleTime += REGENERATION_CONSTANT;
                }
                harvestAmount++;
            } else {
                cycleTime = 0;
            }
            refresh();
        }

        public int getConfigBaseValue() {
            if (productInfo != null) {
                switch (productInfo.getType()) {
                    case ALLOTMENT:
                        return 6 + (productInfo.getConfigIndex() * 7);
                    case HERBS:
                        return 4 + (productInfo.getConfigIndex() * 7);
                    case FLOWERS:
                        return 8 + (productInfo.getConfigIndex() * 5);
                    case HOPS:
                        return 3 + (productInfo.getConfigIndex() * 5);
                    case TREES:
                    case FRUIT_TREES:
                    case BUSHES:
                        return 8 + (productInfo.getConfigIndex() ^ 2 - 1);
                    default:
                        return stage;
                }
            }
            return stage;
        }

        private int getConfigValue(int type) {
            switch (type) {
                case HERBS:
                    return isDead() ? stage + 169 : getConfigBaseValue()
                            + ((isDiseased() && stage != 0) ? stage + 127 : stage);
                case TREES:
                    int baseValue = getConfigBaseValue()
                            + (isDead() ? stage + 128
                            : (isDiseased() && stage != 0) ? stage + 64 : stage);
                    if (hasChecked()) {
                        baseValue += 2;
                        if (!isEmpty()) {
                            baseValue--;
                        }
                    }
                    return baseValue;
                case FRUIT_TREES:
                    baseValue = stage + getConfigBaseValue();
                    if (hasChecked()) {
                        baseValue += getHarvestAmount();
                    } else if (isDead()) {
                        baseValue += 20;
                    } else if (isDiseased()) {
                        baseValue += 12;
                    } else if (!hasChecked() && reachedMaxStage()) {
                        baseValue += 20;
                    }
                    if (isEmpty()) {
                        baseValue += 19;
                    }
                    return baseValue;
                case BUSHES:
                    baseValue = stage + getConfigBaseValue();
                    if (hasChecked()) {
                        baseValue += getHarvestAmount();
                    } else if (isDead()) {
                        baseValue += 128;
                    } else if (isDiseased()) {
                        baseValue += 65;
                    } else if (!hasChecked() && reachedMaxStage()) {
                        baseValue += 240;
                    }
                    return baseValue;
                case COMPOST:
                    return isCleared() ? harvestAmount + 16 + (hasChecked() ? -1 : 0)
                            : productInfo != null && reachedMaxStage() ? 0 : harvestAmount - stage;
                case FLOWERS:
                case HOPS:
                case ALLOTMENT:
                    return getConfigBaseValue()
                            + (isDead() ? stage + 192
                            : (isDiseased() && stage != 0) ? stage + 128
                            : isWatered() ? 64 + stage : stage);
                default:
                    return stage + getConfigBaseValue();
            }
        }

        private void checkFactors() {
            if (isDiseased()) {
                if (reachedMaxStage()) {
                    setDead(false);
                    setDiseased(false);
                } else {
                    if (isFirstCycle()) {
                        setFirstCycle(false);
                    } else {
                        setDead(true);
                    }
                }
            }
            
            if (productInfo.getType() == FRUIT_TREES || productInfo.getType() == BUSHES) {
                if (reachedMaxStage()) {
                    setHarvestAmount(productInfo.getType() == BUSHES
                            || productInfo == ProductInfo.Palm ? 4 : 6);
                }
            }
            
            setWatered(false);
            checkDisease();
        }

        public boolean reachedMaxStage() {
            return stage == productInfo.getMaxStage();
        }

        private boolean hasMaximumRegeneration() {
            if (spotInfo.getType() != FRUIT_TREES && spotInfo.getType() != BUSHES) {
                return true;
            } else {
                return getHarvestAmount() == HARVEST_AMOUNTS[productInfo.getType()][1];
            }
        }

        public void renewCycle() {
            long constant = 30000L;
            if (productInfo != null) {
                cycleTime += (stage == 0) ? 5000 : constant * productInfo.getCycleTime();
            } else {
                cycleTime += constant * 3;
            }
        }

        public boolean canBeDiseased() {
            return !(stage == 0 && productInfo.getType() != BUSHES || reachedMaxStage()
                    || isDiseased() || productInfo == ProductInfo.White_lily
                    || productInfo == ProductInfo.Poison_ivy
                    || productInfo.getType() == COMPOST);
        }

        private void checkDisease() {
            if (canBeDiseased()) {
                int baseValue = 35;
                if (isWatered()) baseValue += 10;
                if (getCompost()) baseValue += 10;
                else if (getSuperCompost()) baseValue += 20;
                if (player.getPerkManager().greenThumb) baseValue += 25;
                
                if (Utils.getRandom(baseValue) == 0) {
                    setDiseased(true);
                    refresh();
                }
            }
        }

        public void increaseStage() {
            stage++;
            if (productInfo != null) {
                checkFactors();
            }
            refresh();
        }

        public void decreaseStage() {
            setCleared(false);
            stage--;
            refresh();
        }

        private void remove() {
            spots.remove(this);
        }

        public void refresh() {
            int value = spotInfo.getType() == COMPOST ? getConfigValue(spotInfo.getType())
                    : productInfo != null ? getConfigValue(productInfo.getType()) + productInfo.getStageSkip() : stage;
            player.getPackets().sendConfigByFile(spotInfo.getConfigFileId(), value);
        }

        // Getters and Setters
        public SpotInfo getSpotInfo() { return spotInfo; }
        public ProductInfo getProductInfo() { return productInfo; }
        public void setProductInfo(ProductInfo productInfo) { this.productInfo = productInfo; }
        public int getStage() { return stage; }
        public long getCycleTime() { return cycleTime; }
        public int getHarvestAmount() { return harvestAmount; }
        public void setHarvestAmount(int harvestAmount) { this.harvestAmount = harvestAmount; }
        public void increaseHarvestAmount() { this.harvestAmount++; }
        public void decreaseHarvestAmount() { this.harvestAmount--; }

        // Attribute methods
        public boolean isDiseased() { return attributes[0]; }
        public void setDiseased(boolean diseased) { this.attributes[0] = diseased; }

        public boolean isWatered() { return attributes[1]; }
        public void setWatered(boolean watered) { this.attributes[1] = watered; }

        public boolean isDead() { return attributes[2]; }
        public void setDead(boolean dead) {
            this.attributes[2] = dead;
            if (dead) setDiseased(false);
        }

        public boolean isFirstCycle() { return attributes[3]; }
        public void setFirstCycle(boolean firstCycle) { this.attributes[3] = firstCycle; }

        public boolean isCleared() { return attributes[4]; }
        public void setCleared(boolean cleared) { this.attributes[4] = cleared; }

        public boolean hasChecked() { return attributes[5]; }
        public void setChecked(boolean checked) { this.attributes[5] = checked; }

        public boolean isEmpty() { return attributes[6]; }
        public void setEmpty(boolean empty) { this.attributes[6] = empty; }

        public boolean hasCompost() { return attributes[7] || attributes[8]; }
        public boolean getCompost() { return attributes[7]; }
        public void setCompost(boolean compost) { this.attributes[7] = compost; }

        public boolean getSuperCompost() { return attributes[8]; }
        public void setSuperCompost(boolean superCompost) { this.attributes[8] = superCompost; }

        public boolean hasGivenAmount() { return attributes[9]; }
        public void setHasGivenAmount(boolean amount) { this.attributes[9] = amount; }

        public boolean hasEmptyHarvestAmount() { return harvestAmount == 0; }
    }
}