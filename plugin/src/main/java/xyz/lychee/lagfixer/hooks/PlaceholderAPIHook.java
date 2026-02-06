package xyz.lychee.lagfixer.hooks;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.lychee.lagfixer.LagFixer;
import xyz.lychee.lagfixer.managers.HookManager;
import xyz.lychee.lagfixer.managers.ModuleManager;
import xyz.lychee.lagfixer.managers.SupportManager;
import xyz.lychee.lagfixer.modules.WorldCleanerModule;
import xyz.lychee.lagfixer.objects.AbstractHook;
import xyz.lychee.lagfixer.objects.AbstractMonitor;

@Getter
public class PlaceholderAPIHook extends AbstractHook {
    private PapiImplementation papi;

    public PlaceholderAPIHook(LagFixer plugin, HookManager manager) {
        super(plugin, "PlaceholderAPI", manager);
    }

    public String applyPlaceholders(Player p, String text) {
        return PlaceholderAPI.setPlaceholders(p, text);
    }

    @Override
    public void load() {
        this.papi = new PapiImplementation(this.getPlugin());
        this.papi.register();
    }

    @Override
    public void disable() {
        this.papi.unregister();
    }

    public static class PapiImplementation
            extends PlaceholderExpansion {
        private final LagFixer plugin;

        public PapiImplementation(LagFixer plugin) {
            this.plugin = plugin;
        }

        @NotNull
        public String getIdentifier() {
            return "syncboost";
        }

        @NotNull
        public String getAuthor() {
            return "Syncara Host";
        }

        @NotNull
        public String getVersion() {
            return this.plugin.getDescription().getVersion();
        }

        public String onPlaceholderRequest(Player p, @NotNull String id) {
            return this.response(id);
        }

        public String onRequest(OfflinePlayer p, @NotNull String id) {
            return this.response(id);
        }

        public boolean persist() {
            return true;
        }

        public boolean canRegister() {
            return true;
        }

        public String response(String id) {
            AbstractMonitor monitor = SupportManager.getInstance().getMonitor();
            SupportManager support = SupportManager.getInstance();
            WorldCleanerModule worldCleaner = ModuleManager.getInstance().get(WorldCleanerModule.class);
            Runtime runtime = Runtime.getRuntime();
            
            switch (id.toLowerCase()) {
                // ==================== Performance Metrics ====================
                case "tps": {
                    return String.format("%.2f", monitor.getTps());
                }
                case "tps_raw": {
                    return Double.toString(monitor.getTps());
                }
                case "tps_color": {
                    // Returns TPS with color code based on value
                    double tps = monitor.getTps();
                    String color = tps >= 18 ? "&a" : (tps >= 15 ? "&e" : "&c");
                    return color + String.format("%.1f", tps);
                }
                case "mspt": {
                    return String.format("%.2f", monitor.getMspt());
                }
                case "mspt_raw": {
                    return Double.toString(monitor.getMspt());
                }
                case "mspt_color": {
                    double mspt = monitor.getMspt();
                    String color = mspt <= 40 ? "&a" : (mspt <= 50 ? "&e" : "&c");
                    return color + String.format("%.1f", mspt);
                }
                case "cpu":
                case "cpuprocess": {
                    return String.format("%.1f%%", monitor.getCpuProcess());
                }
                case "cpuprocess_raw": {
                    return Double.toString(monitor.getCpuProcess());
                }
                case "cpusystem": {
                    return String.format("%.1f%%", monitor.getCpuSystem());
                }
                case "cpusystem_raw": {
                    return Double.toString(monitor.getCpuSystem());
                }
                
                // ==================== Entity Counts ====================
                case "entities":
                case "entities_total": {
                    return String.valueOf(support.getEntities());
                }
                case "entities_mobs":
                case "mobs": {
                    return String.valueOf(support.getCreatures());
                }
                case "entities_items":
                case "items": {
                    return String.valueOf(support.getItems());
                }
                case "entities_projectiles":
                case "projectiles": {
                    return String.valueOf(support.getProjectiles());
                }
                case "entities_vehicles":
                case "vehicles": {
                    return String.valueOf(support.getVehicles());
                }
                
                // ==================== Memory Stats ====================
                case "memory_used":
                case "ram_used": {
                    long used = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
                    return used + "MB";
                }
                case "memory_used_raw": {
                    return String.valueOf((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
                }
                case "memory_max":
                case "ram_max": {
                    return (runtime.maxMemory() / 1024 / 1024) + "MB";
                }
                case "memory_max_raw": {
                    return String.valueOf(runtime.maxMemory() / 1024 / 1024);
                }
                case "memory_free":
                case "ram_free": {
                    return (runtime.freeMemory() / 1024 / 1024) + "MB";
                }
                case "memory_free_raw": {
                    return String.valueOf(runtime.freeMemory() / 1024 / 1024);
                }
                case "memory_percent":
                case "ram_percent": {
                    long used = runtime.totalMemory() - runtime.freeMemory();
                    long max = runtime.maxMemory();
                    int percent = (int) (used * 100 / max);
                    return percent + "%";
                }
                case "memory_percent_raw": {
                    long used = runtime.totalMemory() - runtime.freeMemory();
                    long max = runtime.maxMemory();
                    return String.valueOf((int) (used * 100 / max));
                }
                case "memory_bar": {
                    // Returns a visual progress bar for memory
                    long used = runtime.totalMemory() - runtime.freeMemory();
                    long max = runtime.maxMemory();
                    int percent = (int) (used * 100 / max);
                    int filled = percent / 10;
                    StringBuilder bar = new StringBuilder("&8[");
                    for (int i = 0; i < 10; i++) {
                        bar.append(i < filled ? "&a■" : "&7■");
                    }
                    bar.append("&8]");
                    return bar.toString();
                }
                case "memory_used_gb": {
                    double used = (runtime.totalMemory() - runtime.freeMemory()) / 1024.0 / 1024.0 / 1024.0;
                    return String.format("%.2fGB", used);
                }
                case "memory_max_gb": {
                    double max = runtime.maxMemory() / 1024.0 / 1024.0 / 1024.0;
                    return String.format("%.2fGB", max);
                }
                
                // ==================== Server Stats ====================
                case "players":
                case "online": {
                    return String.valueOf(org.bukkit.Bukkit.getOnlinePlayers().size());
                }
                case "players_max":
                case "max_players": {
                    return String.valueOf(org.bukkit.Bukkit.getMaxPlayers());
                }
                case "worlds": {
                    return String.valueOf(org.bukkit.Bukkit.getWorlds().size());
                }
                case "chunks":
                case "loaded_chunks": {
                    int chunks = 0;
                    for (org.bukkit.World world : org.bukkit.Bukkit.getWorlds()) {
                        chunks += world.getLoadedChunks().length;
                    }
                    return String.valueOf(chunks);
                }
                case "uptime": {
                    long uptime = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime() / 1000;
                    long hours = uptime / 3600;
                    long minutes = (uptime % 3600) / 60;
                    long seconds = uptime % 60;
                    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
                }
                case "uptime_hours": {
                    return String.valueOf(java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime() / 1000 / 3600);
                }
                case "uptime_minutes": {
                    return String.valueOf(java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime() / 1000 / 60);
                }
                case "uptime_seconds": {
                    return String.valueOf(java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
                }
                
                // ==================== Clearlag Timer ====================
                case "clearlag_timer":
                case "worldcleaner": {
                    return worldCleaner == null || !worldCleaner.isLoaded() ? "0s" : worldCleaner.getSecond() + "s";
                }
                case "clearlag_seconds": {
                    return worldCleaner == null || !worldCleaner.isLoaded() ? "0" : String.valueOf(worldCleaner.getSecond());
                }
                case "clearlag_formatted": {
                    if (worldCleaner == null || !worldCleaner.isLoaded()) return "00:00";
                    int seconds = worldCleaner.getSecond();
                    int mins = seconds / 60;
                    int secs = seconds % 60;
                    return String.format("%02d:%02d", mins, secs);
                }
                case "clearlag_interval": {
                    return worldCleaner == null || !worldCleaner.isLoaded() ? "0" : String.valueOf(worldCleaner.getInterval());
                }
                case "clearlag_enabled": {
                    return worldCleaner != null && worldCleaner.isLoaded() ? "true" : "false";
                }
                case "clearlag_progress": {
                    if (worldCleaner == null || !worldCleaner.isLoaded()) return "0%";
                    int interval = worldCleaner.getInterval();
                    int remaining = worldCleaner.getSecond();
                    int elapsed = interval - remaining;
                    int percent = interval > 0 ? (elapsed * 100 / interval) : 0;
                    return percent + "%";
                }
                case "clearlag_bar": {
                    // Returns a visual progress bar for clearlag timer
                    if (worldCleaner == null || !worldCleaner.isLoaded()) return "&8[&7■■■■■■■■■■&8]";
                    int interval = worldCleaner.getInterval();
                    int remaining = worldCleaner.getSecond();
                    int elapsed = interval - remaining;
                    int percent = interval > 0 ? (elapsed * 100 / interval) : 0;
                    int filled = percent / 10;
                    StringBuilder bar = new StringBuilder("&8[");
                    for (int i = 0; i < 10; i++) {
                        bar.append(i < filled ? "&c■" : "&a■");
                    }
                    bar.append("&8]");
                    return bar.toString();
                }
            }
            return null;
        }
    }
}

