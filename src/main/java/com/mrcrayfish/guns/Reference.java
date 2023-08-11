package com.mrcrayfish.guns;

import net.minecraft.resources.ResourceLocation;

public class Reference
{
	public static final String MOD_ID = "cgm";

	public static ResourceLocation getLoc(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
}
