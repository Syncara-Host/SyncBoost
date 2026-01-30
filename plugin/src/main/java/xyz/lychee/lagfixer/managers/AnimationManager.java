package xyz.lychee.lagfixer.managers;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.lychee.lagfixer.LagFixer;
import xyz.lychee.lagfixer.objects.AbstractManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Manager for GUI animations and visual effects
 */
public class AnimationManager extends AbstractManager {
    private static @Getter AnimationManager instance;
    
    private final Map<String, Animation> animations = new HashMap<>();
    private int globalTick = 0;
    
    public AnimationManager(LagFixer plugin) {
        super(plugin);
        instance = this;
    }
    
    @Override
    public void load() {
        // Start animation tick task
        SupportManager.getInstance().getFork().runTimer(true, () -> {
            globalTick++;
            // Update all registered animations
            animations.values().forEach(anim -> anim.tick(globalTick));
        }, 1, 1, java.util.concurrent.TimeUnit.SECONDS);
    }
    
    @Override
    public void disable() {
        animations.clear();
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    /**
     * Register a new animation
     */
    public void registerAnimation(String id, AnimationType type, int speed) {
        animations.put(id, new Animation(type, speed));
    }
    
    /**
     * Get current animation frame
     */
    public ItemStack getAnimationFrame(String id) {
        Animation anim = animations.get(id);
        if (anim == null) return new ItemStack(Material.BARRIER);
        return anim.getCurrentFrame();
    }
    
    /**
     * Get material for animation frame
     */
    public Material getAnimationMaterial(String id) {
        Animation anim = animations.get(id);
        if (anim == null) return Material.BARRIER;
        return anim.getCurrentMaterial();
    }
    
    /**
     * Remove animation
     */
    public void unregisterAnimation(String id) {
        animations.remove(id);
    }
    
    /**
     * Animation types
     */
    public enum AnimationType {
        BREATHING,      // Pulsing opacity (different glass types)
        ROTATING,       // Color wheel rotation
        FLOWING,        // Flowing effect
        GLOWING,        // Glow pulse
        PULSE,          // Size pulse simulation
        RAINBOW_BORDER  // Rainbow border effect
    }
    
    /**
     * Internal animation class
     */
    private static class Animation {
        private final AnimationType type;
        private final int speed;
        private int currentFrame = 0;
        
        public Animation(AnimationType type, int speed) {
            this.type = type;
            this.speed = speed;
        }
        
        public void tick(int globalTick) {
            if (globalTick % speed == 0) {
                currentFrame++;
            }
        }
        
        public ItemStack getCurrentFrame() {
            return new ItemStack(getCurrentMaterial());
        }
        
        public Material getCurrentMaterial() {
            return switch (type) {
                case BREATHING -> getBreathingMaterial();
                case ROTATING, RAINBOW_BORDER -> getRainbowMaterial();
                case FLOWING -> getFlowingMaterial();
                case GLOWING -> getGlowingMaterial();
                case PULSE -> getPulseMaterial();
            };
        }
        
        private Material getBreathingMaterial() {
            // Cycle between glass types for "breathing" effect
            Material[] materials = {
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.CYAN_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.BLUE_STAINED_GLASS_PANE
            };
            return materials[currentFrame % materials.length];
        }
        
        private Material getRainbowMaterial() {
            // Rainbow color cycle
            Material[] materials = {
                Material.RED_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.BLUE_STAINED_GLASS_PANE,
                Material.PURPLE_STAINED_GLASS_PANE,
                Material.MAGENTA_STAINED_GLASS_PANE
            };
            return materials[currentFrame % materials.length];
        }
        
        private Material getFlowingMaterial() {
            // Water-like flowing effect
            Material[] materials = {
                Material.CYAN_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                Material.BLUE_STAINED_GLASS_PANE,
                Material.LIGHT_BLUE_STAINED_GLASS_PANE
            };
            return materials[currentFrame % materials.length];
        }
        
        private Material getGlowingMaterial() {
            // Pulsing glow effect
            Material[] materials = {
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE
            };
            return materials[currentFrame % materials.length];
        }
        
        private Material getPulseMaterial() {
            // Pulse effect
            Material[] materials = {
                Material.WHITE_STAINED_GLASS_PANE,
                Material.LIGHT_GRAY_STAINED_GLASS_PANE,
                Material.GRAY_STAINED_GLASS_PANE,
                Material.LIGHT_GRAY_STAINED_GLASS_PANE
            };
            return materials[currentFrame % materials.length];
        }
    }
    
    /**
     * Helper: Get rainbow border materials for a given tick
     */
    public static Material[] getRainbowBorderMaterials(int tick) {
        Material[] base = {
            Material.RED_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE
        };
        
        // Rotate array based on tick
        Material[] result = new Material[base.length];
        int offset = (tick / 5) % base.length;
        for (int i = 0; i < base.length; i++) {
            result[i] = base[(i + offset) % base.length];
        }
        return result;
    }
    
    /**
     * Helper: Get health-based pulse materials
     */
    public static Material[] getHealthPulseMaterials(double tps) {
        if (tps >= 19.0) {
            return new Material[]{
                Material.LIME_STAINED_GLASS_PANE,
                Material.GREEN_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE,
                Material.GREEN_STAINED_GLASS_PANE
            };
        } else if (tps >= 17.0) {
            return new Material[]{
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE
            };
        } else if (tps >= 15.0) {
            return new Material[]{
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.YELLOW_STAINED_GLASS_PANE
            };
        } else {
            return new Material[]{
                Material.RED_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE,
                Material.RED_STAINED_GLASS_PANE,
                Material.ORANGE_STAINED_GLASS_PANE
            };
        }
    }
}
