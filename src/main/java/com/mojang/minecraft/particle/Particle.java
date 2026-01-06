package com.mojang.minecraft.particle;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.renderer.Tesselator;

public class Particle extends Entity {
    private float xd;
    private float yd;
    private float zd;
    public int tex;
    private float uo;
    private float vo;
    private int age = 0;
    private int lifetime = 0;
    private float size;
    public static float sizeScale = 0.08F;
    public static float one_64 = 1f/64;

    public Particle(Level level, float x, float y, float z, float xa, float ya, float za, int tex) {
        super(level);
        this.tex = tex;
        this.setSize(0.2F, 0.2F);
        this.heightOffset = this.bbHeight / 2.0F;
        this.setPos(x, y, z);
        this.xd = xa + (float)(Math.random() * 2.0 - 1.0) * 0.4F;
        this.yd = ya + (float)(Math.random() * 2.0 - 1.0) * 0.4F;
        this.zd = za + (float)(Math.random() * 2.0 - 1.0) * 0.4F;
        float speed = (float)(Math.random() + Math.random() + 1.0) * 0.15F;
        float dd = (float)Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
        this.xd = this.xd / dd * speed * 0.4F;
        this.yd = this.yd / dd * speed * 0.4F + 0.1F;
        this.zd = this.zd / dd * speed * 0.4F;
        this.uo = (float)Math.random() * 3.0F;
        this.vo = (float)Math.random() * 3.0F;
        this.size = (float)(Math.random() * 0.5 + 0.5);
        this.lifetime = (int)(4.0 / (Math.random() * 0.9 + 0.1));
        this.age = 0;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.yd = (float)(this.yd - 0.04);
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98F;
        this.yd *= 0.98F;
        this.zd *= 0.98F;
        if (this.onGround) {
            this.xd *= 0.7F;
            this.zd *= 0.7F;
        }
    }

    public void render(Tesselator t, float a, float xa, float ya, float za, float xa2, float za2) {
        int textureSize = 16;

        float u0 = (this.tex % textureSize + this.uo / 4.0F) / textureSize;
        float u1 = u0 + one_64;
        float v0 = (this.tex / textureSize + this.vo / 4.0F) / textureSize;
        float v1 = v0 + one_64;

        float r = sizeScale*this.size;
        float x = this.xo + (this.x - this.xo) * a;
        float y = this.yo + (this.y - this.yo) * a;
        float z = this.zo + (this.z - this.zo) * a;
        t.vertexUV(x - xa * r - xa2 * r, y - ya * r, z - za * r - za2 * r, u0, v1);
        t.vertexUV(x - xa * r + xa2 * r, y + ya * r, z - za * r + za2 * r, u0, v0);
        t.vertexUV(x + xa * r + xa2 * r, y + ya * r, z + za * r + za2 * r, u1, v0);
        t.vertexUV(x + xa * r - xa2 * r, y - ya * r, z + za * r - za2 * r, u1, v1);
    }
}
