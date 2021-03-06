package com.daoran.newfactory.onefactory.util.bsdiff;

/**
 * 生成patch增量包
 */
public class DiffUtils {
	
	static DiffUtils instance;

	public static DiffUtils getInstance() {
		if (instance == null)
			instance = new DiffUtils();
		return instance;
	}

	static {
		System.loadLibrary("apk_patch_lib");
	}

	public native int genDiff(String oldApkPath, String newApkPath, String patchPath);
}