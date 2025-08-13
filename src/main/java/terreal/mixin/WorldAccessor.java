package terreal.mixin;

import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
public interface WorldAccessor {

    @Accessor("biomeAccess")
    BiomeAccess terreal$getBiomeAccess();

}
