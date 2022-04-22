package com.sigmundgranaas.forgero.mixins;

import com.sigmundgranaas.forgero.core.property.Property;
import com.sigmundgranaas.forgero.core.property.TargetTypes;
import com.sigmundgranaas.forgero.core.property.active.PatternBreaking;
import com.sigmundgranaas.forgero.core.property.attribute.SingleTarget;
import com.sigmundgranaas.forgero.core.tool.ForgeroTool;
import com.sigmundgranaas.forgero.item.ForgeroToolItem;
import com.sigmundgranaas.forgero.toolhandler.PatternBlockBreakingHandler;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

    @Inject(at = @At("HEAD"), method = "calcBlockBreakingDelta", cancellable = true)
    public void calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (player.getMainHandStack().getItem() instanceof ForgeroToolItem toolItem) {
            ForgeroTool tool = toolItem.convertItemStack(player.getMainHandStack(), toolItem.getTool());
            var activeProperties = Property.stream(tool.getProperties(new SingleTarget(TargetTypes.BLOCK, Collections.emptySet()))).getActiveProperties().toList();
            if (!activeProperties.isEmpty()) {
                float f = new PatternBlockBreakingHandler((PatternBreaking) activeProperties.get(0)).getHardness(state, pos, world, player);
                if (f == -1.0F) {
                    cir.setReturnValue(0.0F);
                } else {
                    int i = player.canHarvest(state) ? 30 : 100;
                    cir.setReturnValue(player.getBlockBreakingSpeed(state) / f / (float) i);
                }

            }
        }
    }
}
