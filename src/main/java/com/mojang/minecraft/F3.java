package com.mojang.minecraft;

import com.mojang.minecraft.level.Chunk;

public class F3 {
    public static F3 instance;

    public static void Update (Minecraft minecraft) {

    }

    public static void Draw (Minecraft minecraft) {
        minecraft.font.drawShadow("0.0.13a", 2, 2, 16777215);

        String fpsString = minecraft.frames + " fps, " + Chunk.updates + " chunk updates";
        minecraft.font.drawShadow(fpsString, 2, 12, 16777215);

    }

}
