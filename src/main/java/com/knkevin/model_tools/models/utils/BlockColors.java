package com.knkevin.model_tools.models.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector4i;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to read through a jar file and extract the names and colors of valid blocks.
 * Valid blocks are blocks that can be used to approximate a pixel color.
 */
public class BlockColors {
	/**
	 * A regex for getting key value pairs in json.
	 */
	private static final Pattern keyValuePattern = Pattern.compile("\"([^\"]+)\": \"([^\"]+)\"(?:,)?|(\\{\\})");

	/**
	 * @param jarFile A Minecraft jar file.
	 * @return A Map of block names to integers representing their average colors.
	 * @throws IOException The JarFile could not be read.
	 */
	public static Map<String, Integer> getBlockColors(JarFile jarFile) throws IOException {
		Map<String, Integer> blockColors = new HashMap<>();
		List<JarEntry> blockStates = getBlockStateEntriesFromJar(jarFile);
		for (JarEntry entry: blockStates) {
			JarEntry firstModel = getFirstModelEntry(jarFile, entry);
			if (firstModel != null && isCubeModel(jarFile, firstModel) && isValidBlock(entry) && isValidName(entry)) {
				String blockName = entry.getName().replace("assets/minecraft/blockstates/","").replace(".json","");
				JarEntry texture = getTexture(jarFile, firstModel);
				int color = getTextureColor(jarFile, texture);
				blockColors.put(blockName, color);
			}
		} return blockColors;
	}

	/**
	 * Searches through the JarFile and returns a list of entries that represent blockstate json files.
	 * @param jarFile A Minecraft jar file.
	 * @return A list of JarEntries to blockstate files.
	 */
	private static List<JarEntry> getBlockStateEntriesFromJar(JarFile jarFile) {
		List<JarEntry> blockStates = new ArrayList<>();
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.getName().startsWith("assets/minecraft/blockstates/"))
				blockStates.add(entry);
		} return blockStates;
	}

	/**
	 * Searches through the blockstate file and returns an entry to the first model specified in the file.
	 * @param jarFile  A Minecraft jar file.
	 * @param blockState A JarEntry representing the blockstate file.
	 * @return A JarEntry that represents a model json file.
	 * @throws IOException The JarFile could not be read.
	 */
	private static JarEntry getFirstModelEntry(JarFile jarFile, JarEntry blockState) throws IOException {
		InputStream inputStream = jarFile.getInputStream(blockState);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			Matcher m = keyValuePattern.matcher(line.strip());
			if (m.find()) {
				String key = m.group(1);
				String value = m.group(2);
				if (key.equals("model")) {
					String modelName = value.replace("minecraft:", "");
					String modelFileName = "assets/minecraft/models/" + modelName + ".json";
					return jarFile.getJarEntry(modelFileName);
				}
			}
		} return null;
	}

	/**
	 * @param jarFile  A Minecraft jar file.
	 * @param model A JarEntry representing the model file.
	 * @return True if the given model is a cube_all model or if it has a parent model that is one.
	 * @throws IOException The JarFile could not be read.
	 */
	private static boolean isCubeModel(JarFile jarFile, JarEntry model) throws IOException {
		InputStream inputStream = jarFile.getInputStream(model);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			Matcher m = keyValuePattern.matcher(line.strip());
			if (!m.find()) continue;
			String key = m.group(1), value = m.group(2);
			if (!key.equals("parent")) continue;
			if (value.equals("minecraft:block/cube_all")) return true;
			String modelName = value.replace("minecraft:", "");
			String modelFileName = "assets/minecraft/models/" + modelName + ".json";
			return isCubeModel(jarFile, jarFile.getJarEntry(modelFileName));
		} return false;
	}

	/**
	 * @param entry A JarEntry representing a blockstate file.
	 * @return True if the block isn't an instance of one of the listed blocks, false otherwise.
	 */
	private static boolean isValidBlock(JarEntry entry) {
		String blockName = entry.getName().replace("assets/minecraft/blockstates/","").replace(".json","");
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
		if (block == null || block.defaultBlockState().isAir() || block.requiredFeatures().contains(FeatureFlags.UPDATE_1_20)) return false;
		List<Class<?>> classes = List.of(
				FallingBlock.class,
				AbstractGlassBlock.class,
				LeavesBlock.class,
				SlabBlock.class,
				InfestedBlock.class,
				DropExperienceBlock.class,
				WeatheringCopper.class,
				CoralBlock.class,
				EntityBlock.class
		);
		for (Class<?> clazz: classes) {
			if (clazz.isInstance(block)) return false;
		} return true;
	}

	/**
	 * @param entry A JarEntry representing a blockstate file.
	 * @return True if the block's name doesn't contain one of the listed substrings, false otherwise.
	 */
	private static boolean isValidName(JarEntry entry) {
		String[] invalidStrings = new String[] {
				"_ore",
				"_coral_block"
		};
		String entryName = entry.getName();
		for (String invalidString: invalidStrings) {
			if (entryName.contains(invalidString)) return false;
		} return true;
	}

	/**
	 * @param jarFile A Minecraft jar file.
	 * @param model A JarEntry representing block model file.
	 * @return A JarEntry representing a texture file.
	 * @throws IOException The JarFile could not be read.
	 */
	private static JarEntry getTexture(JarFile jarFile, JarEntry model) throws IOException {
		InputStream inputStream = jarFile.getInputStream(model);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			Matcher m = keyValuePattern.matcher(line.strip());
			if (!m.find()) continue;
			String key = m.group(1), value = m.group(2);
			if (!key.equals("all")) continue;
			String textureName = value.replace("minecraft:","");
			return jarFile.getJarEntry("assets/minecraft/textures/" + textureName + ".png");
		} return null;
	}

	/**
	 * @param jarFile A Minecraft jar file.
	 * @param texture A JarEntry representing a texture file.
	 * @return The average color of the texture as a Vector4i in ARGB format.
	 * @throws IOException The JarFile could not be read.
	 */
	private static int getTextureColor(JarFile jarFile, JarEntry texture) throws IOException {
		BufferedImage img = ImageIO.read(jarFile.getInputStream(texture));
		Vector4i average = new Vector4i();
		int count = 0;
		for (int y = 0; y < img.getHeight(); ++y) {
			for (int x = 0; x < img.getWidth(); ++x) {
				Vector4i pixel = VectorColors.intToARGB(img.getRGB(x, y));
				average.add(pixel);
				++count;
			}
		} return VectorColors.ARGBToInt(average.div(count));
	}
}
