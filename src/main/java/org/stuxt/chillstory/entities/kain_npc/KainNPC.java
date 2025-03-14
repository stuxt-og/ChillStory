package org.stuxt.chillstory.entities.kain_npc;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.stuxt.chillstory.network.channel.EntityKillChannel;
import org.stuxt.chillstory.network.packet.EntityKillPacket;

public class KainNPC extends Mob {
    public KainNPC(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 0.1D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.GENERIC_KILL)) {
            teleportTo(getX(), -100, getZ());
        } else if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return super.hurt(source, amount);
        }

        return false;
    }

    @Override
    public void knockback(double strength, double x, double z) {}

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypes.GENERIC_KILL)) {
            return false;
        }

        return true;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof KainNPC) {
            event.getEntity().invulnerableTime = 0;
        }
    }

    public static void onEnd(Entity ent) {
        EntityKillPacket packet = new EntityKillPacket(ent.getId(), ent.level().dimension().location().toString());

        EntityKillChannel.INSTANCE.sendToServer(packet);
    }
}
