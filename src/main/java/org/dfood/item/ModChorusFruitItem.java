package org.dfood.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.block.Block;

public class ModChorusFruitItem extends BlockItem {
    public ModChorusFruitItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        ItemStack itemStack = super.finishUsingItem(stack, world, user);
        if (!world.isClientSide) {
            double d = user.getX();
            double e = user.getY();
            double f = user.getZ();

            for (int i = 0; i < 16; i++) {
                double g = user.getX() + (user.getRandom().nextDouble() - 0.5) * 16.0;
                double h = Mth.clamp(
                        user.getY() + (user.getRandom().nextInt(16) - 8),
                        world.getMinBuildHeight(),
                        world.getMinBuildHeight() + ((ServerLevel)world).getLogicalHeight() - 1
                );
                double j = user.getZ() + (user.getRandom().nextDouble() - 0.5) * 16.0;

                if (user.isPassenger()) {
                    user.stopRiding();
                }

                Vec3 vec3 = user.position();
                if (user.randomTeleport(g, h, j, true)) {
                    world.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(user));
                    SoundEvent soundEvent = user instanceof Fox ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                    world.playSound(null, d, e, f, soundEvent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    user.playSound(soundEvent, 1.0F, 1.0F);
                    break;
                }
            }

            if (user instanceof Player player) {
                player.getCooldowns().addCooldown(this, 20);
            }
        }

        return itemStack;
    }
}