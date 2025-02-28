package de.netzkronehd.hitboxutils.paper.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("deprecation")
public class ItemBuilder {

    public final static ItemStack FORWARD =
            new ItemBuilder(Material.TIPPED_ARROW, 1)
                    .setName("§7Forward »")
                    .setArrowPotionType(PotionType.JUMP)
                    .addFlags(ItemFlag.HIDE_POTION_EFFECTS)
                    .build();


    public final static ItemStack GLASS = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE, 1).setName(" ").build();

    public final static ItemStack BACKWARD =
            new ItemBuilder(Material.TIPPED_ARROW, 1)
                    .setName("§7« Back")
                    .setArrowPotionType(PotionType.INSTANT_HEAL)
                    .addFlags(ItemFlag.HIDE_POTION_EFFECTS)
                    .build();

    private final ItemStack item;

    public static ItemBuilder builder(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder builder(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder builder(Material material, int amount, short data) {
        return new ItemBuilder(material, amount, data);
    }

    public static ItemBuilder builder(ItemStack item) {
        return new ItemBuilder(item);
    }

    public static ItemBuilder builder(ItemStack item, boolean newItem) {
        return new ItemBuilder(item, newItem);
    }

    public ItemBuilder(Material material) {
        item = new ItemStack(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        item = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, int amount, short data) {
        item = new ItemStack(material, amount, data);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(ItemStack item, boolean newItem) {
        if (newItem) {
            this.item = new ItemStack(item);
        } else this.item = item;
    }

    public ItemBuilder setDurability(short data) {
        item.setDurability(data);
        return this;
    }

    public ItemBuilder setType(Material m) {
        item.setType(m);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemBuilder setName(String name) {
        final ItemMeta m = item.getItemMeta();
        assert m != null;
        m.setDisplayName(name);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setArrowPotionType(PotionData data) {
        final PotionMeta m = (PotionMeta) item.getItemMeta();
        assert m != null;
        m.setBasePotionData(data);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setArrowPotionType(PotionType type) {
        return setArrowPotionType(new PotionData(type));
    }

    public ItemBuilder setLore(String... lore) {
        final ItemMeta m = item.getItemMeta();
        assert m != null;
        m.setLore(Arrays.asList(lore));
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setLore(int index, String value) {
        final ItemMeta m = item.getItemMeta();

        assert m != null;
        if (m.hasLore()) {
            final List<String> lore = m.getLore();
            assert lore != null;
            lore.set(index, value);
            m.setLore(lore);
        } else {
            final List<String> lore = new ArrayList<>();
            for (int i = 0; i < index + 1; i++) {
                lore.add("");
            }
            lore.set(index, value);
            m.setLore(lore);
        }
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        final ItemMeta m = item.getItemMeta();
        assert m != null;
        m.setLore(lore);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        final ItemMeta m = item.getItemMeta();
        assert m != null;
        if (m.hasLore()) {
            List<String> itemLore = m.getLore();
            assert itemLore != null;
            Collections.addAll(itemLore, lore);
            m.setLore(itemLore);
        } else m.setLore(Arrays.asList(lore));
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setCustomModelData(Integer data) {
        final ItemMeta m = item.getItemMeta();
        assert m != null;
        m.setCustomModelData(data);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder enchant(Enchantment ench, int lvl) {
        final ItemMeta m = item.getItemMeta();
        assert m != null;
        m.addEnchant(ench, lvl, true);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean type) {
        final ItemMeta m = item.getItemMeta();
        assert m != null;
        m.setUnbreakable(type);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flag) {
        final ItemMeta m = item.getItemMeta();
        assert m != null;
        m.addItemFlags(flag);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setEggType(EntityType type) {
        item.setType(Material.valueOf(type.toString() + "_SPAWN_EGG"));
        return this;
    }

    public ItemBuilder setBannerVersion(int version) {
        if (!item.getType().toString().endsWith("_BANNER")) {
            setBannerColor(Color.WHITE);
        }
        final BannerMeta bm = (BannerMeta) item.getItemMeta();
        assert bm != null;
        bm.setVersion(version);
        item.setItemMeta(bm);
        return this;
    }

    public ItemBuilder setBannerBaseColor(DyeColor color) {
        if (!item.getType().toString().endsWith("_BANNER")) {
            setBannerColor(Color.WHITE);
        }
        final BannerMeta bm = (BannerMeta) item.getItemMeta();
        assert bm != null;
        bm.setBaseColor(color);
        item.setItemMeta(bm);
        return this;
    }

    public ItemBuilder setBannerColor(Color color) {
        item.setType(Material.valueOf(color.toString() + "_BANNER"));
        return this;
    }

    public ItemBuilder setLeatherColor(Color color) {
        final LeatherArmorMeta m = (LeatherArmorMeta) item.getItemMeta();
        assert m != null;
        m.setColor(color);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        item.setType(Material.PLAYER_HEAD);
        final SkullMeta m = (SkullMeta) item.getItemMeta();
        assert m != null;
        m.setOwner(owner);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setPotionColor(Color color) {
        final PotionMeta m = (PotionMeta) item.getItemMeta();
        assert m != null;
        m.setColor(color);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setPotionData(PotionData data) {
        final PotionMeta m = (PotionMeta) item.getItemMeta();
        assert m != null;
        m.setBasePotionData(data);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setPotionVersion(int version) {
        final PotionMeta m = (PotionMeta) item.getItemMeta();
        assert m != null;
        m.setVersion(version);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setPotionType(PotionEffectType type) {
        final PotionMeta m = (PotionMeta) item.getItemMeta();
        assert m != null;
        m.setMainEffect(type);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setBasePotionType(PotionType potionType) {
        final PotionMeta m = (PotionMeta) item.getItemMeta();
        assert m != null;
        m.setBasePotionType(potionType);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setBookAuthor(String author) {
        final BookMeta m = (BookMeta) item.getItemMeta();
        assert m != null;
        m.setAuthor(author);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setBookContent(String... pages) {
        final BookMeta m = (BookMeta) item.getItemMeta();
        assert m != null;
        m.setPages(pages);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder addBookPage(String... pages) {
        final BookMeta m = (BookMeta) item.getItemMeta();
        assert m != null;
        m.addPage(pages);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setBookTitle(String title) {
        final BookMeta m = (BookMeta) item.getItemMeta();
        assert m != null;
        m.setTitle(title);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setBookMeta(String title, String author, String... pages) {
        final BookMeta m = (BookMeta) item.getItemMeta();
        assert m != null;
        m.setTitle(title);
        m.setAuthor(author);
        m.setPages(pages);
        item.setItemMeta(m);
        return this;
    }

    public ItemBuilder setSkullTexture(String base64) {
        final ItemMeta m = item.getItemMeta();
        final GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));
        Field profileField;
        try {
            assert m != null;
            profileField = m.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(m, profile);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        item.setItemMeta(m);
        return this;
    }

    public ItemStack build() {
        return item;
    }

}
