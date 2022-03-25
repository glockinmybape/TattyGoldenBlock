package org.glockinmybape.tattygoldenblock;

import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.Map.Entry;

public class Main extends JavaPlugin implements Listener {
    public static Main inst;
    public static Boolean isspawn;
    public static Boolean ischest;
    public static Location loc;
    public static Boolean isend;
    public static Inventory chest;
    private Hologram holo;
    private CMIHologram cmiholo;
    public static Economy economy;
    public static Map<String, Integer> ext = new HashMap();

    public static Main getInstance() {
        return inst;
    }

    public void onEnable() {
        this.setupEconomy();
        inst = this;
        this.saveDefaultConfig();
        this.getCommand("goldblock").setExecutor(new CommandBlock());
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        isspawn = false;
        ischest = false;
        isend = false;
        Cooldowns.tryCooldown("MGOLDENBLOCK", 60000L);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            public void run() {
                if (Cooldowns.containsCooldown("MGOLDENBLOCK") && Cooldowns.getCooldown("MGOLDENBLOCK") <= 0L && !Main.isspawn) {
                    Cooldowns.removeCooldown("MGOLDENBLOCK");
                    Cooldowns.tryCooldown("MGOLDENBLOCK", (long)Main.inst.getConfig().getInt("cooldown-spawn"));
                    Main.isspawn = true;
                    Main.this.start();
                    Cooldowns.tryCooldown("MGOLDENBLOCK2", 600000L);
                    Cooldowns.tryCooldown("MGOLDENBLOCKMONEY", 600000L);
                }

                Player allx;
                if (Cooldowns.containsCooldown("MGOLDENBLOCKMONEY") && Cooldowns.getCooldown("MGOLDENBLOCKMONEY") > 0L && Main.isspawn && !Main.ischest) {
                    Iterator var2 = Main.loc.getWorld().getNearbyEntities(Main.loc, 3.0D, 3.0D, 3.0D).iterator();

                    label137:
                    while(true) {
                        Entity ent;
                        do {
                            if (!var2.hasNext()) {
                                break label137;
                            }

                            ent = (Entity)var2.next();
                        } while(!(ent instanceof Player));

                        allx = (Player)ent;
                        Iterator var5 = Bukkit.getOnlinePlayers().iterator();

                        while(var5.hasNext()) {
                            Player allxx = (Player)var5.next();
                            if (allxx == allx) {
                                if (Main.ext.containsKey(allxx.getName())) {
                                    Main.ext.put(allxx.getName(), (Integer)Main.ext.get(allxx.getName()) + 10);
                                } else {
                                    Main.ext.put(allxx.getName(), 3);
                                }

                                Econom.deposit(allxx, Main.inst.getConfig().getInt("money"));
                                allxx.sendMessage(Main.inst.getConfig().getString("message-add-money"));
                            }
                        }
                    }
                }

                String sx;
                Iterator var19;
                if (Cooldowns.containsCooldown("MGOLDENBLOCK2") && Cooldowns.getCooldown("MGOLDENBLOCK2") <= 0L && Main.isspawn && !Main.ischest) {
                    Cooldowns.removeCooldown("MGOLDENBLOCKMONEY");
                    Cooldowns.removeCooldown("MGOLDENBLOCK2");
                    Inventory chest = Bukkit.createInventory((InventoryHolder)null, 27, "НОВОГОДНИЙ БЛОК");
                    List<String> l1 = new ArrayList();
                    var19 = Main.inst.getConfig().getConfigurationSection("Items").getKeys(false).iterator();

                    while(var19.hasNext()) {
                        sx = (String)var19.next();
                        l1.add(sx);
                    }

                    int size = l1.size();
                    ItemStack i12 = Main.createItem(size, l1);
                    ItemStack i13 = Main.createItem(size, l1);
                    ItemStack i14 = Main.createItem(size, l1);
                    ItemStack i15 = Main.createItem(size, l1);
                    ItemStack i16 = Main.createItem(size, l1);
                    chest.setItem(Main.getRandomInt(1, 26), i12);
                    chest.setItem(Main.getRandomInt(1, 26), i13);
                    chest.setItem(Main.getRandomInt(1, 26), i14);
                    chest.setItem(Main.getRandomInt(1, 26), i15);
                    chest.setItem(Main.getRandomInt(1, 26), i16);
                    Main.chest = chest;
                    Main.ischest = true;
                    if (Main.this.getConfig().getString("hologram-api").equals("cmi")) {
                        Main.this.cmiholo.setLine(1, "§fТеперь это сундук с вещами!");
                    } else {
                        Main.this.holo.removeLine(1);
                        Main.this.holo.insertTextLine(1, "§fТеперь это сундук с вещами!");
                    }

                    Iterator var10 = Main.this.getConfig().getStringList("broadcast-chest").iterator();

                    while(var10.hasNext()) {
                        String s = (String)var10.next();
                        Iterator var12 = Bukkit.getOnlinePlayers().iterator();

                        while(var12.hasNext()) {
                            Player allxxx = (Player)var12.next();
                            allxxx.sendMessage(s.replace("{x}", "" + Main.loc.getBlockX()).replace("{y}", "" + Main.loc.getBlockY()).replace("{z}", "" + Main.loc.getBlockZ()));
                        }
                    }

                    Cooldowns.tryCooldown("MGOLDENBLOCK3", 300000L);
                }

                if (Cooldowns.containsCooldown("MGOLDENBLOCK3") && Cooldowns.getCooldown("MGOLDENBLOCK3") <= 0L && Main.isspawn && Main.ischest) {
                    Cooldowns.removeCooldown("MGOLDENBLOCK3");
                    Main.ischest = false;
                    if (Main.this.getConfig().getString("hologram-api").equals("cmi")) {
                        Main.this.cmiholo.setLine(1, "§fПриносит монеты, если стоишь рядом");
                    } else {
                        Main.this.holo.removeLine(1);
                        Main.this.holo.insertTextLine(1, "§fПриносит монеты, если стоишь рядом");
                    }

                    Cooldowns.tryCooldown("MGOLDENBLOCKMONEY", 300000L);
                    Cooldowns.tryCooldown("MGOLDENBLOCK4", 300000L);
                }

                if (Cooldowns.containsCooldown("MGOLDENBLOCK4") && Cooldowns.getCooldown("MGOLDENBLOCK4") <= 0L && Main.isspawn && !Main.ischest) {
                    Cooldowns.removeCooldown("MGOLDENBLOCK4");
                    Cooldowns.removeCooldown("MGOLDENBLOCKMONEY");
                    Block b = Main.loc.getBlock();
                    b.setType(Material.AIR);
                    Main.removeRegion("gold_block", b.getWorld());
                    if (Main.this.getConfig().getString("hologram-api").equals("cmi")) {
                        Main.this.cmiholo.disable();
                    } else {
                        Main.this.holo.delete();
                    }

                    Main.isspawn = false;
                    Main.isend = true;
                    String[] title = Main.this.getConfig().getString("title-deleted").split("%nl");
                    var19 = Bukkit.getOnlinePlayers().iterator();

                    while(var19.hasNext()) {
                        allx = (Player)var19.next();
                        allx.sendTitle(title[0], title[1]);
                    }

                    var19 = Main.this.getConfig().getStringList("broadcast-deleted").iterator();

                    while(var19.hasNext()) {
                        sx = (String)var19.next();
                        Iterator var23 = Bukkit.getOnlinePlayers().iterator();

                        while(var23.hasNext()) {
                            Player all = (Player)var23.next();
                            if (Main.getTopStats1().isEmpty()) {
                                all.sendMessage(sx.replace("{winner_money}", "0").replace("{winner}", "нету"));
                            } else {
                                all.sendMessage(sx.replace("{winner_money}", "" + Main.ext.get(Main.getTopStats1().get(1))).replace("{winner}", (CharSequence)Main.getTopStats1().get(1)));
                            }
                        }
                    }
                }

            }
        }, 0L, 20L);
    }

    public void start() {
        int minx = inst.getConfig().getInt("Coords.min_x");
        int maxx = inst.getConfig().getInt("Coords.max_x");
        int minz = inst.getConfig().getInt("Coords.min_z");
        int maxz = inst.getConfig().getInt("Coords.max_z");
        int X = getRandomInt(minx, maxx);
        int Z = getRandomInt(minz, maxz);
        World W = Bukkit.getWorld("world");
        int Y = W.getHighestBlockYAt(X, Z);
        String[] title = this.getConfig().getString("title-spawned").split("%nl");
        Iterator var11 = Bukkit.getOnlinePlayers().iterator();

        while(var11.hasNext()) {
            Player all = (Player)var11.next();
            all.sendTitle(title[0], title[1]);
        }

        var11 = this.getConfig().getStringList("broadcast-spawned").iterator();

        while(var11.hasNext()) {
            String s = (String)var11.next();
            Iterator var13 = Bukkit.getOnlinePlayers().iterator();

            while(var13.hasNext()) {
                Player all = (Player)var13.next();
                all.sendMessage(s.replace("{x}", "" + X).replace("{y}", "" + Y).replace("{z}", "" + Z));
            }
        }

        Location loc2 = new Location(W, (double)X, (double)Y, (double)Z);
        loc = loc2;
        Block b = loc2.getBlock();
        b.setType(Material.GOLD_BLOCK);
        b.setData((byte)1);
        b.getChunk().unload(true);
        b.getChunk().load(true);
        Location loc11 = new Location(W, (double)X, (double)Y, (double)Z);
        Location loc21 = new Location(W, (double)X, (double)Y, (double)Z);
        loc11.setX(loc11.getX() + 5.0D);
        loc21.setX(loc21.getX() - 5.0D);
        loc11.setY(255.0D);
        loc21.setY(0.0D);
        loc11.setZ(loc11.getZ() + 5.0D);
        loc21.setZ(loc21.getZ() - 5.0D);
        createRegion(loc11, loc21);
        Location holo = new Location(W, (double)X, (double)Y, (double)Z);
        holo.setY(holo.getY() + 2.0D);
        holo.setX(holo.getX() + 0.5D);
        holo.setZ(holo.getZ() + 0.5D);
        Hologram hologram = HologramsAPI.createHologram(this, holo);
        hologram.appendTextLine("§6Золотой блок");
        hologram.appendTextLine("§fПриносит монеты, если стоишь рядом");
        this.holo = hologram;
    }

    public static int getRandomInt(int min, int max) {
        int x = (int)(Math.random() * (double)(max - min + 1) + (double)min);
        return x;
    }

    public static BlockVector convertToSk89qBV(Location location) {
        return new BlockVector(location.getX(), location.getY(), location.getZ());
    }

    public static void createRegion(Location loc, Location loc2) {
        WorldGuardPlugin wg = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin("WorldGuard");
        ProtectedCuboidRegion pr = new ProtectedCuboidRegion("gold_block", convertToSk89qBV(loc), convertToSk89qBV(loc2));
        wg.getRegionManager(loc2.getWorld()).addRegion(pr);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "region flag -w " + loc2.getWorld().getName() + " gold_block pistons deny");
    }

    public static void removeRegion(String name, World w) {
        WorldGuardPlugin wg = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin("WorldGuard");
        wg.getRegionManager(w).removeRegion(name);
    }

    public static ItemStack createItem(int size, List<String> l1) {
        int item5 = getRandomInt(1, size);
        String name3 = (String)l1.get(item5 - 1);
        ItemStack item6 = inst.getConfig().getItemStack("Items." + name3);
        return item6;
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                economy = (Economy)rsp.getProvider();
                return economy != null;
            }
        }
    }

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order) {
        List<Entry<String, Integer>> list = new LinkedList(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Entry<String, Integer>>() {
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return order ? ((Integer)o1.getValue()).compareTo((Integer)o2.getValue()) : ((Integer)o2.getValue()).compareTo((Integer)o1.getValue());
            }

            // $FF: synthetic method
            // $FF: bridge method
            public int compare(Object var1, Object var2) {
                return this.compare((Entry)var1, (Entry)var2);
            }
        });
        Map<String, Integer> sortedMap = new LinkedHashMap();
        Iterator var5 = list.iterator();

        while(var5.hasNext()) {
            Entry<String, Integer> entry = (Entry)var5.next();
            sortedMap.put((String)entry.getKey(), (Integer)entry.getValue());
        }

        return sortedMap;
    }

    public static List<String> getTopStats1() {
        List<String> stats = new ArrayList();
        int id = 0;

        for(Iterator var3 = sortByComparator(ext, false).entrySet().iterator(); var3.hasNext(); ++id) {
            Entry<String, Integer> sortedMap = (Entry)var3.next();
            if (id <= 5) {
                stats.add((String)sortedMap.getKey());
            }
        }

        return stats;
    }
}
