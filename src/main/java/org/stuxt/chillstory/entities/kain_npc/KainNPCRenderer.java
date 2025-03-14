package org.stuxt.chillstory.entities.kain_npc;


import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.stuxt.chillstory.MainClass;

public class KainNPCRenderer extends HumanoidMobRenderer<KainNPC, PlayerModel<KainNPC>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MainClass.MODID, "textures/entity/kain_npc.png");

    public KainNPCRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(KainNPC entity) {
        return TEXTURE;
    }
}