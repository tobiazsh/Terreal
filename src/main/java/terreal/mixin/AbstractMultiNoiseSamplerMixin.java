package terreal.mixin;

import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseUtil.MultiNoiseSampler.class)
abstract public class AbstractMultiNoiseSamplerMixin {

    @Inject(at = @At("HEAD"), method = "sample")
    public void sample$Terreal(int x, int y, int z, CallbackInfoReturnable<MultiNoiseUtil.NoiseValuePoint> cir) {
        MultiNoiseSamplerAccessor multiNoiseSamplerAccessor = (MultiNoiseSamplerAccessor)this;
    }

}
