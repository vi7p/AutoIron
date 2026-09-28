package com.example.autoirontools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class AutoIronToolsMod implements ModInitializer {

    // Full iron armor set
    private static final Item[] IRON_ARMOR = {
        Items.IRON_HELMET,
        Items.IRON_CHESTPLATE,
        Items.IRON_LEGGINGS,
        Items.IRON_BOOTS
    };

    private static final EquipmentSlot[] ARMOR_SLOTS = {
        EquipmentSlot.HEAD,
        EquipmentSlot.CHEST,
        EquipmentSlot.LEGS,
        EquipmentSlot.FEET
    };

    // Iron tools strictly excluding the hoe
    private static final Item[] IRON_TOOLS = {
        Items.IRON_SWORD,
        Items.IRON_PICKAXE,
        Items.IRON_AXE,
        Items.IRON_SHOVEL
    };

    @Override
    public void onInitialize() {
        // Runs every tick: continuously checks for missing or broken iron gear
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isAlive()) {
                    ensureIronItems(player);
                }
            }
        });

        // Runs on death/respawn: immediately restores all iron equipment
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            ensureIronItems(newPlayer);
        });
    }

    private static void ensureIronItems(ServerPlayerEntity player) {
        // 1. Maintain Armor
        for (int i = 0; i < IRON_ARMOR.length; i++) {
            Item armorItem = IRON_ARMOR[i];
            EquipmentSlot slot = ARMOR_SLOTS[i];

            // If not worn and not anywhere in inventory, give it back
            if (!player.getEquippedStack(slot).isOf(armorItem) && !player.getInventory().contains(new ItemStack(armorItem))) {
                if (player.getEquippedStack(slot).isEmpty()) {
                    player.equipStack(slot, new ItemStack(armorItem));
                } else {
                    player.getInventory().insertStack(new ItemStack(armorItem));
                }
            }
        }

        // 2. Maintain Tools (Sword, Pickaxe, Axe, Shovel)
        for (Item toolItem : IRON_TOOLS) {
            if (!player.getInventory().contains(new ItemStack(toolItem))) {
                player.getInventory().insertStack(new ItemStack(toolItem));
            }
        }
    }
}
