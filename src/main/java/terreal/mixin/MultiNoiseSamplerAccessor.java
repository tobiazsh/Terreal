package terreal.mixin;

import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiNoiseUtil.MultiNoiseSampler.class)
public interface MultiNoiseSamplerAccessor {

    @Accessor("temperature")
    DensityFunction terreal$getTemperatureFunction();

    @Accessor("humidity")
    DensityFunction terreal$getHumidityFunction();

    @Accessor("continentalness")
    DensityFunction terreal$getContinentalnessFunction();

    @Accessor("erosion")
    DensityFunction terreal$getErosionFunction();

}
