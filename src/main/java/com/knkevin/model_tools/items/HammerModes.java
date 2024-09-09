package com.knkevin.model_tools.items;

/**
 * Keeps track of the currently selected axis, transform mode, and viewing mode.
 */
public class HammerModes {
	public enum Axis {
		X("x", 0),
		Y("y", 1),
		Z("z", 2),
		ALL("all", 1);
		public final String name;
		public final int component;
		Axis(String name, int component) {
			this.name = name;
			this.component = component;
		}
	}
	public enum TransformMode {
		SCALE(),
		ROTATE(),
		TRANSLATE()
	}
	public enum ViewMode {
		BOX(),
		BLOCKS()
	}

	public static Axis selectedAxis = Axis.Y;
	public static TransformMode transformMode = TransformMode.TRANSLATE;
	public static ViewMode viewMode = ViewMode.BOX;
}
