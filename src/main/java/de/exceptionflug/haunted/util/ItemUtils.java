package de.exceptionflug.haunted.util;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.google.common.collect.ImmutableMultimap;
import org.bukkit.craftbukkit.profile.CraftPlayerProfile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

/**
 * Created on 22.10.2025
 *
 * @author DerMistkaefer
 */
public class ItemUtils {

    public static ItemStack setSkullTexture(final ItemStack stack, final String textureHash) {
        Preconditions.checkNotNull(stack, "The stack cannot be null!");
        Preconditions.checkNotNull(textureHash, "The textureHash cannot be null!");
        Preconditions.checkArgument(!textureHash.isEmpty(), "The textureHash cannot be empty!");
        final ItemMeta meta = stack.getItemMeta();
        Preconditions.checkState(meta instanceof SkullMeta, "Meta must be a skull meta");
        final PropertyMap properties = new PropertyMap(ImmutableMultimap.of("textures", new Property("textures", textureHash)));
        final GameProfile profile = new GameProfile(UUID.randomUUID(), textureHash.substring(0, Math.min(textureHash.length(), 16)), properties);
        SkullMeta skullMeta = (SkullMeta) meta;
        skullMeta.setPlayerProfile(new CraftPlayerProfile(profile));
        stack.setItemMeta(skullMeta);
        return stack;
    }
}
