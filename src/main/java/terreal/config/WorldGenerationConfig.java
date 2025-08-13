package terreal.config;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorldGenerationConfig {

    // FIELDS -------------------------------------------------------------------------------------------------------------------------------------

                        // Looping latitude: total pole-to-pole distance in blocks (N -> S)
    /* CONFIGURABLE */  public final int    POLE_TO_POLE_DISTANCE;

                        // Equator offset (shift climate band north/south without moving world origin)
    /* CONFIGURABLE */  public final double EQUATOR_OFFSET_BLOCKS;

                        // Temperature shaping
    /* CONFIGURABLE */  public final double TEMP_NOISE_FREQ; // larger -> more frequent temp ripples
    /* CONFIGURABLE */  public final double TEMP_NOISE_STRENGTH; // contribution of temp noise
    /* CONFIGURABLE */  public final double TEMP_LAT_SHARPNESS; // >1 sharpens cold poles, <1 flattens

                        // Humidity shaping
    /* CONFIGURABLE */  public final double HUM_NOISE_FREQ;
    /* CONFIGURABLE */  public final double HUM_NOISE_STRENGTH;

                        // Equatorial bell (jungles near the equator)
    /* CONFIGURABLE */  public final double HUM_EQUATOR_BELL_WIDTH; // half-width of the wet equatorial band (normalized latitude)

    // Subtropical desert belts (dry just off the equator)
    /* CONFIGURABLE */  public final double DESERT_CENTER_LATITUDE; // ~10-20 degrees in normalized units (0..0.5 maps to pole)
    /* CONFIGURABLE */  public final double DESERT_WIDTH; // width of desert dip
    /* CONFIGURABLE */  public final double DESERT_DEPTH; // how strong the dryness is

                        // Continentalness
    /* CONFIGURABLE */  public final double CONTINENT_WAVELENGTH_BLOCKS;
    /* CONFIGURABLE */  public final double CONT_FREQ;
    /* CONFIGURABLE */  public final int    CONT_OCTAVES;
    /* CONFIGURABLE */  public final double CONT_LACUNARITY; // frequency multiplier per octave
    /* CONFIGURABLE */  public final double CONT_GAIN; // amplitude multiplier per octave
    /* CONFIGURABLE */  public final double CONT_RIDGE_SHAPE; // 0..1; lower makes deeper oceans, higher makes broader land
    /* CONFIGURABLE */  public final double SHELF_PUSH; // continental shelf “ledge” shaping

                        // Mountains / Erosion / Depth
    /* CONFIGURABLE */  public final double MNT_FREQ;
    /* CONFIGURABLE */  public final int    MNT_OCTAVES;
    /* CONFIGURABLE */  public final double MNT_GAIN;
    /* CONFIGURABLE */  public final double MNT_STRENGTH;
    /* CONFIGURABLE */  public final double EROSION_FROM_MNT; // invert mountains into erosion
    /* CONFIGURABLE */  public final double DEPTH_FROM_CONT; // tie depth to continentalness

                        // Weirdness (pure flavour)
    /* CONFIGURABLE */  public final double WEIRD_FREQ;
    /* CONFIGURABLE */  public final double WEIRD_STRENGTH;

                        // Seeds
                        private final long SEED_BASE;
                        private final OctavePerlinNoiseSampler CONT_NOISE;
                        private final OctavePerlinNoiseSampler MNT_NOISE;
                        private final PerlinNoiseSampler TEMP_NOISE;
                        private final PerlinNoiseSampler HUM_NOISE;
                        private final PerlinNoiseSampler WEIRD_NOISE;


    // CONSTRUCTOR -------------------------------------------------------------------------------------------------------------------------------

    public WorldGenerationConfig (
        int poleToPoleDistance,
        double equatorOffsetBlocks,
        double tempNoiseFreq, double tempNoiseStrength, double tempLatSharpness,
        double humNoiseFreq, double humNoiseStrength,
        double humEquatorBellWidth,
        double desertCenterLatitude, double desertWidth, double desertDepth,
        double continentWavelengthBlocks, double contFreq, int contOctaves, double contLacunarity, double contGain, double contRidgeShape, double shelfPush,
        double mntFreq, int mntOctaves, double mntGain, double mntStrength, double erosionFromMnt, double depthFromCont,
        double weirdFreq, double weirdStrength,
        long seedBase
    ) {
        this.POLE_TO_POLE_DISTANCE = poleToPoleDistance;

        this.EQUATOR_OFFSET_BLOCKS = equatorOffsetBlocks;

        this.TEMP_NOISE_FREQ = tempNoiseFreq;
        this.TEMP_NOISE_STRENGTH = tempNoiseStrength;
        this.TEMP_LAT_SHARPNESS = tempLatSharpness;

        this.HUM_NOISE_FREQ = humNoiseFreq;
        this.HUM_NOISE_STRENGTH = humNoiseStrength;

        this.HUM_EQUATOR_BELL_WIDTH = humEquatorBellWidth;

        this.DESERT_CENTER_LATITUDE = desertCenterLatitude;
        this.DESERT_WIDTH = desertWidth;
        this.DESERT_DEPTH = desertDepth;

        this.CONTINENT_WAVELENGTH_BLOCKS = continentWavelengthBlocks;
        this.CONT_FREQ = contFreq;
        this.CONT_OCTAVES = contOctaves;
        this.CONT_LACUNARITY = contLacunarity;
        this.CONT_GAIN = contGain;
        this.CONT_RIDGE_SHAPE = contRidgeShape;
        this.SHELF_PUSH = shelfPush;

        this.MNT_FREQ = mntFreq;
        this.MNT_OCTAVES = mntOctaves;
        this.MNT_GAIN = mntGain;
        this.MNT_STRENGTH = mntStrength;
        this.EROSION_FROM_MNT = erosionFromMnt;
        this.DEPTH_FROM_CONT = depthFromCont;

        this.WEIRD_FREQ = weirdFreq;
        this.WEIRD_STRENGTH = weirdStrength;

        this.SEED_BASE = seedBase;

        DoubleList contAmplitudes = new DoubleArrayList(CONT_OCTAVES);
        double gain = CONT_GAIN; // amplitude multiplier
        for (int i = 0; i < CONT_OCTAVES; i++) {
            contAmplitudes.add(Math.pow(gain, i)); // classic fractal sum
        }
        this.CONT_NOISE = new OctavePerlinNoiseSampler(Random.create(SEED_BASE ^ 0xC0FFEE), Pair.of(0, contAmplitudes), true);

        DoubleList mntAmplitudes = new DoubleArrayList(MNT_OCTAVES);
        for (int i = 0; i < MNT_OCTAVES; i++) {
            mntAmplitudes.add(Math.pow(MNT_GAIN, i));
        }

        this.MNT_NOISE = new OctavePerlinNoiseSampler(Random.create(SEED_BASE ^ 0xBEEFCAFE), Pair.of(0, mntAmplitudes), true);

        this.TEMP_NOISE = new PerlinNoiseSampler(Random.create(SEED_BASE ^ 0x1234));
        this.HUM_NOISE = new PerlinNoiseSampler(Random.create(SEED_BASE ^ 0x5678));
        this.WEIRD_NOISE = new PerlinNoiseSampler(Random.create(SEED_BASE ^ 0x9ABC));
    }



    // PRESETS -------------------------------------------------------------------------------------------------------------------------------

    public enum Preset {
        DEFAULT,
        REALISTIC
    }

    public static WorldGenerationConfig getDefault(long worldSeed) {
        return new WorldGenerationConfig(
                1_000_000, // 1 million blocks from pole to pole
                333_000, // shift 333k blocks to spawn in a more temperate zone
                0.005, 0.8, 1.2,
                0.004, 0.7,
                0.15,
                0.12, 0.08, 0.7,
                100000, 0.01, 5, 2.0, 0.5, 0.7, 0.1,
                0.01, 4, 0.5, 1.0, 0.3, 0.5,
                0.01, 0.5,
                worldSeed
        );
    }

    public static WorldGenerationConfig getRealistic(long worldSeed) {
        return new WorldGenerationConfig(
                40_008_000,                                                                                     // pole-to-pole distance in blocks (Earth scale)
                0,                                                                                                              // equator offset = true equator at 0,0,0
                0.00005, 1.0, 1.5,                                                               // temperature noise freq/strength/sharpness: very smooth bands
                0.00004, 0.8,                                                                                    // humidity noise freq/strength: smooth but allows jungles
                0.1,                                                                                                            // equatorial humidity bell width (normalized 0..1)
                0.15, 0.05, 0.7,                                                                           // subtropical deserts: ~15% off equator, narrow, strong
                4_000_000, 0.00001, 6, 2.0, 0.6, 0.8, 0.1,       // continentalness: huge continents, moderate ridge shaping
                0.0001, 6, 0.6, 2.0, 0.3, 0.5,                           // mountains: high and large, erosion & depth moderate
                0.00005, 0.7,                                                                                        // weirdness: small
                worldSeed
        );
    }

    /**
     * Default selectedPreset for Terreal without a real seed
     */
    private static final WorldGenerationConfig DEFAULT_CONFIG = getDefault(0);

    /**
     * Realistic selectedPreset for Terreal without a real seed
     */
    private static final WorldGenerationConfig REALISTIC_CONFIG = getRealistic(0);


    // METHODS ------------------------------------------------------------------------------------------------------------------------------------

    /**
     * Parses a world generation config file in the TOML format.
     */
    public static WorldGenerationConfig parseFile(File file, long seed) {
        Toml toml = new Toml().read(file);
        WorldGenerationConfig selectedPreset;

        // Checks if any preset is present in the TOML File
        if (toml.contains("preset")) {
            Preset preset = Preset.valueOf(toml.getString("preset").toUpperCase());

            switch (preset) {
                case REALISTIC:
                    selectedPreset = REALISTIC_CONFIG;
                    break;
                default:
                    selectedPreset = DEFAULT_CONFIG;
            }
        } else {
            // If no preset is specified, use the default selectedPreset
            selectedPreset = DEFAULT_CONFIG;
        }

        int    POLE_TO_POLE_DISTANCE        =   Math.toIntExact(toml.getLong("pole_to_pole_distance", (long) selectedPreset.POLE_TO_POLE_DISTANCE));
        double EQUATOR_OFFSET_BLOCKS        =   toml.getDouble("equator_offset_blocks", selectedPreset.EQUATOR_OFFSET_BLOCKS);

        double TEMP_NOISE_FREQ              =   toml.getDouble("temp_noise_freq", selectedPreset.TEMP_NOISE_FREQ);
        double TEMP_NOISE_STRENGTH          =   toml.getDouble("temp_noise_strength", selectedPreset.TEMP_NOISE_STRENGTH);
        double TEMP_LAT_SHARPNESS           =   toml.getDouble("temp_lat_sharpness", selectedPreset.TEMP_LAT_SHARPNESS);

        double HUM_NOISE_FREQ               =   toml.getDouble("hum_noise_freq", selectedPreset.HUM_NOISE_FREQ);
        double HUM_NOISE_STRENGTH           =   toml.getDouble("hum_noise_strength", selectedPreset.HUM_NOISE_STRENGTH);
        double HUM_EQUATOR_BELL_WIDTH       =   toml.getDouble("hum_equator_bell_width", selectedPreset.HUM_EQUATOR_BELL_WIDTH);

        double DESERT_CENTER_LATITUDE       =   toml.getDouble("desert_center_latitude", selectedPreset.DESERT_CENTER_LATITUDE);
        double DESERT_WIDTH                 =   toml.getDouble("desert_width", selectedPreset.DESERT_WIDTH);
        double DESERT_DEPTH                 =   toml.getDouble("desert_depth", selectedPreset.DESERT_DEPTH);

        double CONTINENT_WAVELENGTH_BLOCKS  =   toml.getDouble("continent_wavelength_blocks", selectedPreset.CONTINENT_WAVELENGTH_BLOCKS);
        double CONT_FREQ                    =   toml.getDouble("cont_freq", selectedPreset.CONT_FREQ);
        int    CONT_OCTAVES                 =   Math.toIntExact(toml.getLong("cont_octaves", (long) selectedPreset.CONT_OCTAVES));
        double CONT_LACUNARITY              =   toml.getDouble("cont_lacunarity", selectedPreset.CONT_LACUNARITY);
        double CONT_GAIN                    =   toml.getDouble("cont_gain", selectedPreset.CONT_GAIN);
        double CONT_RIDGE_SHAPE             =   toml.getDouble("cont_ridge_shape", selectedPreset.CONT_RIDGE_SHAPE);
        double SHELF_PUSH                   =   toml.getDouble("shelf_push", selectedPreset.SHELF_PUSH);
        double DEPTH_FROM_CONT              =   toml.getDouble("depth_from_cont", selectedPreset.DEPTH_FROM_CONT);

        double MNT_FREQ                     =   toml.getDouble("mnt_freq", selectedPreset.MNT_FREQ);
        int    MNT_OCTAVES                  =   Math.toIntExact(toml.getLong("mnt_octaves", (long) selectedPreset.MNT_OCTAVES));
        double MNT_GAIN                     =   toml.getDouble("mnt_gain", selectedPreset.MNT_GAIN);
        double MNT_STRENGTH                 =   toml.getDouble("mnt_strength", selectedPreset.MNT_STRENGTH);
        double EROSION_FROM_MNT             =   toml.getDouble("erosion_from_mnt", selectedPreset.EROSION_FROM_MNT);

        double WEIRD_FREQ                   =   toml.getDouble("weird_freq", selectedPreset.WEIRD_FREQ);
        double WEIRD_STRENGTH               =   toml.getDouble("weird_strength", selectedPreset.WEIRD_STRENGTH);

        return new WorldGenerationConfig(
                POLE_TO_POLE_DISTANCE,
                EQUATOR_OFFSET_BLOCKS,
                TEMP_NOISE_FREQ, TEMP_NOISE_STRENGTH, TEMP_LAT_SHARPNESS,
                HUM_NOISE_FREQ, HUM_NOISE_STRENGTH,
                HUM_EQUATOR_BELL_WIDTH,
                DESERT_CENTER_LATITUDE, DESERT_WIDTH, DESERT_DEPTH,
                CONTINENT_WAVELENGTH_BLOCKS, CONT_FREQ, CONT_OCTAVES, CONT_LACUNARITY, CONT_GAIN, CONT_RIDGE_SHAPE, SHELF_PUSH,
                MNT_FREQ, MNT_OCTAVES, MNT_GAIN, MNT_STRENGTH, EROSION_FROM_MNT, DEPTH_FROM_CONT,
                WEIRD_FREQ, WEIRD_STRENGTH,
                seed
        );
    }

    public void writeFile(File location) throws IOException {
        TomlWriter tomlWriter = new TomlWriter();

        Map<String, Object> map = new HashMap<>();

        if (this.matches(DEFAULT_CONFIG)) {
            map.put("preset", Preset.DEFAULT.name());
        } else if (this.matches(REALISTIC_CONFIG)) {
            map.put("preset", Preset.REALISTIC.name());
        } else {
            map.put("pole_to_pole_distance", POLE_TO_POLE_DISTANCE);
            map.put("equator_offset_blocks", EQUATOR_OFFSET_BLOCKS);
            map.put("temp_noise_freq", TEMP_NOISE_FREQ);
            map.put("temp_noise_strength", TEMP_NOISE_STRENGTH);
            map.put("temp_lat_sharpness", TEMP_LAT_SHARPNESS);
            map.put("hum_noise_freq", HUM_NOISE_FREQ);
            map.put("hum_noise_strength", HUM_NOISE_STRENGTH);
            map.put("hum_equator_bell_width", HUM_EQUATOR_BELL_WIDTH);
            map.put("desert_center_latitude", DESERT_CENTER_LATITUDE);
            map.put("desert_width", DESERT_WIDTH);
            map.put("desert_depth", DESERT_DEPTH);
            map.put("continent_wavelength_blocks", CONTINENT_WAVELENGTH_BLOCKS);
            map.put("cont_freq", CONT_FREQ);
            map.put("cont_octaves", CONT_OCTAVES);
            map.put("cont_lacunarity", CONT_LACUNARITY);
            map.put("cont_gain", CONT_GAIN);
            map.put("cont_ridge_shape", CONT_RIDGE_SHAPE);
            map.put("shelf_push", SHELF_PUSH);
            map.put("depth_from_cont", DEPTH_FROM_CONT);
            map.put("mnt_freq", MNT_FREQ);
            map.put("mnt_octaves", MNT_OCTAVES);
            map.put("mnt_gain", MNT_GAIN);
            map.put("mnt_strength", MNT_STRENGTH);
            map.put("erosion_from_mnt", EROSION_FROM_MNT);
            map.put("weird_freq", WEIRD_FREQ);
            map.put("weird_strength", WEIRD_STRENGTH);
        }

        tomlWriter.write(map, location);
    }

    public boolean matches(WorldGenerationConfig other) {
        return this.POLE_TO_POLE_DISTANCE == other.POLE_TO_POLE_DISTANCE &&
               this.EQUATOR_OFFSET_BLOCKS == other.EQUATOR_OFFSET_BLOCKS &&
               this.TEMP_NOISE_FREQ == other.TEMP_NOISE_FREQ &&
               this.TEMP_NOISE_STRENGTH == other.TEMP_NOISE_STRENGTH &&
               this.TEMP_LAT_SHARPNESS == other.TEMP_LAT_SHARPNESS &&
               this.HUM_NOISE_FREQ == other.HUM_NOISE_FREQ &&
               this.HUM_NOISE_STRENGTH == other.HUM_NOISE_STRENGTH &&
               this.HUM_EQUATOR_BELL_WIDTH == other.HUM_EQUATOR_BELL_WIDTH &&
               this.DESERT_CENTER_LATITUDE == other.DESERT_CENTER_LATITUDE &&
               this.DESERT_WIDTH == other.DESERT_WIDTH &&
               this.DESERT_DEPTH == other.DESERT_DEPTH &&
               this.CONTINENT_WAVELENGTH_BLOCKS == other.CONTINENT_WAVELENGTH_BLOCKS &&
               this.CONT_FREQ == other.CONT_FREQ &&
               this.CONT_OCTAVES == other.CONT_OCTAVES &&
               this.CONT_LACUNARITY == other.CONT_LACUNARITY &&
               this.CONT_GAIN == other.CONT_GAIN &&
               this.CONT_RIDGE_SHAPE == other.CONT_RIDGE_SHAPE &&
               this.SHELF_PUSH == other.SHELF_PUSH &&
               this.DEPTH_FROM_CONT == other.DEPTH_FROM_CONT &&
               this.MNT_FREQ == other.MNT_FREQ &&
               this.MNT_OCTAVES == other.MNT_OCTAVES &&
               this.MNT_GAIN == other.MNT_GAIN &&
               this.MNT_STRENGTH == other.MNT_STRENGTH &&
               this.EROSION_FROM_MNT == other.EROSION_FROM_MNT &&
               this.WEIRD_FREQ == other.WEIRD_FREQ &&
               this.WEIRD_STRENGTH == other.WEIRD_STRENGTH;
    }
}
