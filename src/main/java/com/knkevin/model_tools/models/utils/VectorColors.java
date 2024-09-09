package com.knkevin.model_tools.models.utils;

import org.joml.Vector3f;
import org.joml.Vector4i;

/**
 * A class for converting colors between Integers and Vector4i's.
 */
public class VectorColors {
	/**
	 * @param color The color as an integer.
	 * @return The color as a Vector4i in ARGB format.
	 */
	public static Vector4i intToARGB(int color) {
		Vector4i ARGB = new Vector4i();
		ARGB.x = (color >> 24) & 0xFF;
		ARGB.y = (color >> 16) & 0xFF;
		ARGB.z = (color >> 8) & 0xFF;
		ARGB.w = color & 0xFF;
		return ARGB;
	}

	/**
	 * @param color The color as a Vector4i in ARGB format.
	 * @return The color as an integer.
	 */
	public static int ARGBToInt(Vector4i color) {
		int rgb = Math.min(color.x, 255);
		rgb = (rgb << 8) + Math.min(color.y, 255);
		rgb = (rgb << 8) + Math.min(color.z, 255);
		rgb = (rgb << 8) + Math.min(color.w, 255);
		return rgb;
	}

	public static float colorSquaredDistance(Vector4i colorOne, Vector4i colorTwo) {
		Vector3f rgb = new Vector3f(colorOne.y - colorTwo.y, colorOne.z - colorTwo.z, colorOne.w - colorTwo.w);
		rgb.mul(rgb).mul(.3f,.59f,.11f);
		return rgb.x + rgb.y + rgb.z;
	}
}
