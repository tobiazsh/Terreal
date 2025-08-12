package terreal.mixin;

import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiNoiseUtil.MultiNoiseSampler.class)
public interface MultiNoiseSamplerAccessor {

    @Accessor("temperature")
    DensityFunction getTemperatureFunction();

    @Accessor("humidity")
    DensityFunction getHumidityFunction();

    @Accessor("continentalness")
    DensityFunction getContinentalnessFunction();

    @Accessor("erosion")
    DensityFunction getErosionFunction();

}
