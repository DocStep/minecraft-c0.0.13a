package com.mojang.minecraft.particle;

import com.mojang.minecraft.Player;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Textures;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class ParticleEngine {
   protected Level level;
   private List<Particle> particles = new ArrayList<>();
   private Textures textures;

   public ParticleEngine(Level level, Textures textures) {
      this.level = level;
      this.textures = textures;
   }

   public void add(Particle p) {
      this.particles.add(p);
   }

   public void tick() {
      for (int i = 0; i < this.particles.size(); i++) {
         Particle p = this.particles.get(i);
         p.tick();
         if (p.removed) {
            this.particles.remove(i--);
         }
      }
   }

   public void render(Player player, float a, int layer) {
      if (this.particles.size() != 0) {
         GL11.glEnable(GL11.GL_TEXTURE_2D);
         int id = this.textures.loadTexture("/assets/terrain.png", GL11.GL_NEAREST);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
         float xa = -((float)Math.cos(player.yRot * Math.PI / 180.0));
         float za = -((float)Math.sin(player.yRot * Math.PI / 180.0));
         float xa2 = -za * (float)Math.sin(player.xRot * Math.PI / 180.0);
         float za2 = xa * (float)Math.sin(player.xRot * Math.PI / 180.0);
         float ya = (float)Math.cos(player.xRot * Math.PI / 180.0);
         Tesselator t = Tesselator.instance;
         GL11.glColor4f(0.8F, 0.8F, 0.8F, 0.0F);
         t.begin();

         for (int i = 0; i < this.particles.size(); i++) {
            Particle p = this.particles.get(i);
            if (p.isLit() ^ layer == 1) {
               p.render(t, a, xa, ya, za, xa2, za2);
            }
         }

         t.end();
         GL11.glDisable(GL11.GL_TEXTURE_2D);
      }
   }
}
