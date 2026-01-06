package com.mojang.minecraft.level.levelgen;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelLoaderListener;
import com.mojang.minecraft.level.tile.Tile;
import java.util.ArrayList;
import java.util.Random;

public class LevelGen {
   private LevelLoaderListener levelLoaderListener;
   private int width;
   private int height;
   private int depth;
   private Random random = new Random();
   private byte[] blocks;
   private int[] coords = new int[1048576];

   public LevelGen(LevelLoaderListener levelLoaderListener) {
      this.levelLoaderListener = levelLoaderListener;
   }

   public boolean generateLevel(Level level, String userName, int width, int height, int depth) {
      this.levelLoaderListener.beginLevelLoading("Generating level");
      this.width = width;
      this.height = height;
      this.depth = depth;
      this.blocks = new byte[width * height * depth];
      this.levelLoaderListener.levelLoadUpdate("Raising..");
      double[] heightMap = this.buildHeightmap(width, height);
      this.levelLoaderListener.levelLoadUpdate("Eroding..");
      this.buildBlocks(heightMap);
      this.levelLoaderListener.levelLoadUpdate("Carving..");
      this.carveTunnels();
      this.levelLoaderListener.levelLoadUpdate("Watering..");
      this.addWater();
      this.levelLoaderListener.levelLoadUpdate("Melting..");
      this.addLava();
      level.setData(width, depth, height, this.blocks);
      level.createTime = System.currentTimeMillis();
      level.creator = userName;
      level.name = "A Nice World";
      return true;
   }

   private void buildBlocks(double[] heightMap) {
      int w = this.width;
      int h = this.height;
      int d = this.depth;

      for (int x = 0; x < w; x++) {
         for (int y = 0; y < d; y++) {
            for (int z = 0; z < h; z++) {
               int dh = d / 2;
               int rh = d / 3;
               int i = (y * this.height + z) * this.width + x;
               int id = 0;
               if (y == dh && y >= d / 2 - 1) {
                  id = Tile.grass.id;
               } else if (y <= dh) {
                  id = Tile.dirt.id;
               }

               if (y <= rh) {
                  id = Tile.rock.id;
               }

               this.blocks[i] = (byte)id;
            }
         }
      }
   }

   private double[] buildHeightmap(int width, int height) {
      return new double[width * height];
   }

   public void carveTunnels() {
      int w = this.width;
      int h = this.height;
      int d = this.depth;
      int count = w * h * d / 256 / 64;

      for (int i = 0; i < count; i++) {
         float x = this.random.nextFloat() * w;
         float y = this.random.nextFloat() * d;
         float z = this.random.nextFloat() * h;
         int length = (int)(this.random.nextFloat() + this.random.nextFloat() * 150.0F);
         float dir1 = (float)(this.random.nextFloat() * Math.PI * 2.0);
         float dira1 = 0.0F;
         float dir2 = (float)(this.random.nextFloat() * Math.PI * 2.0);
         float dira2 = 0.0F;

         for (int l = 0; l < length; l++) {
            x = (float)(x + Math.sin(dir1) * Math.cos(dir2));
            z = (float)(z + Math.cos(dir1) * Math.cos(dir2));
            y = (float)(y + Math.sin(dir2));
            dir1 += dira1 * 0.2F;
            dira1 *= 0.9F;
            dira1 += this.random.nextFloat() - this.random.nextFloat();
            dir2 += dira2 * 0.5F;
            dir2 *= 0.5F;
            dira2 *= 0.9F;
            dira2 += this.random.nextFloat() - this.random.nextFloat();
            float size = (float)(Math.sin(l * Math.PI / length) * 2.5 + 1.0);

            for (int xx = (int)(x - size); xx <= (int)(x + size); xx++) {
               for (int yy = (int)(y - size); yy <= (int)(y + size); yy++) {
                  for (int zz = (int)(z - size); zz <= (int)(z + size); zz++) {
                     float xd = xx - x;
                     float yd = yy - y;
                     float zd = zz - z;
                     float dd = xd * xd + yd * yd * 2.0F + zd * zd;
                     if (dd < size * size && xx >= 1 && yy >= 1 && zz >= 1 && xx < this.width - 1 && yy < this.depth - 1 && zz < this.height - 1) {
                        int ii = (yy * this.height + zz) * this.width + xx;
                        if (this.blocks[ii] == Tile.rock.id) {
                           this.blocks[ii] = 0;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void addWater() {
      long before = System.nanoTime();
      long tiles = 0L;
      int source = 0;
      int target = Tile.calmWater.id;

      for (int x = 0; x < this.width; x++) {
         tiles += this.floodFillLiquid(x, this.depth / 2 - 1, 0, source, target);
         tiles += this.floodFillLiquid(x, this.depth / 2 - 1, this.height - 1, source, target);
      }

      for (int y = 0; y < this.height; y++) {
         tiles += this.floodFillLiquid(0, this.depth / 2 - 1, y, source, target);
         tiles += this.floodFillLiquid(this.width - 1, this.depth / 2 - 1, y, source, target);
      }

      for (int i = 0; i < this.width * this.height / 5000; i++) {
         int x = this.random.nextInt(this.width);
         int y = this.depth / 2 - 1;
         int z = this.random.nextInt(this.height);
         if (this.blocks[(y * this.height + z) * this.width + x] == 0) {
            tiles += this.floodFillLiquid(x, y, z, 0, target);
         }
      }

      long after = System.nanoTime();
      System.out.println("Flood filled " + tiles + " tiles in " + (after - before) / 1000000.0 + " ms");
   }

   public void addLava() {
      int lavaCount = 0;

      for (int i = 0; i < this.width * this.height * this.depth / 10000; i++) {
         int x = this.random.nextInt(this.width);
         int y = this.random.nextInt(this.depth / 2);
         int z = this.random.nextInt(this.height);
         if (this.blocks[(y * this.height + z) * this.width + x] == 0) {
            lavaCount++;
            this.floodFillLiquid(x, y, z, 0, Tile.calmLava.id);
         }
      }

      System.out.println("LavaCount: " + lavaCount);
   }

   public long floodFillLiquid(int x, int y, int z, int source, int tt) {
      byte target = (byte)tt;
      ArrayList<int[]> coordBuffer = new ArrayList<>();
      int p = 0;
      int wBits = 1;
      int hBits = 1;

      while (1 << wBits < this.width) {
         wBits++;
      }

      while (1 << hBits < this.height) {
         hBits++;
      }

      int hMask = this.height - 1;
      int wMask = this.width - 1;
      this.coords[p++] = ((y << hBits) + z << wBits) + x;
      long tiles = 0L;
      int upStep = this.width * this.height;

      while (p > 0) {
         int cl = this.coords[--p];
         if (p == 0 && coordBuffer.size() > 0) {
            System.out.println("IT HAPPENED!");
            this.coords = coordBuffer.remove(coordBuffer.size() - 1);
            p = this.coords.length;
         }

         int z0 = cl >> wBits & hMask;
         int y0 = cl >> wBits + hBits;
         int x0 = cl & wMask;

         int x1;
         for (x1 = x0; x0 > 0 && this.blocks[cl - 1] == source; cl--) {
            x0--;
         }

         while (x1 < this.width && this.blocks[cl + x1 - x0] == source) {
            x1++;
         }

         int z1 = cl >> wBits & hMask;
         int y1 = cl >> wBits + hBits;
         if (z1 != z0 || y1 != y0) {
            System.out.println("hoooly fuck");
         }

         boolean lastNorth = false;
         boolean lastSouth = false;
         boolean lastBelow = false;
         tiles += x1 - x0;

         for (int xx = x0; xx < x1; xx++) {
            this.blocks[cl] = target;
            if (z0 > 0) {
               boolean north = this.blocks[cl - this.width] == source;
               if (north && !lastNorth) {
                  if (p == this.coords.length) {
                     coordBuffer.add(this.coords);
                     this.coords = new int[1048576];
                     p = 0;
                  }

                  this.coords[p++] = cl - this.width;
               }

               lastNorth = north;
            }

            if (z0 < this.height - 1) {
               boolean south = this.blocks[cl + this.width] == source;
               if (south && !lastSouth) {
                  if (p == this.coords.length) {
                     coordBuffer.add(this.coords);
                     this.coords = new int[1048576];
                     p = 0;
                  }

                  this.coords[p++] = cl + this.width;
               }

               lastSouth = south;
            }

            if (y0 > 0) {
               int belowId = this.blocks[cl - upStep];
               if ((target == Tile.lava.id || target == Tile.calmLava.id) && (belowId == Tile.water.id || belowId == Tile.calmWater.id)) {
                  this.blocks[cl - upStep] = (byte)Tile.rock.id;
               }

               boolean below = belowId == source;
               if (below && !lastBelow) {
                  if (p == this.coords.length) {
                     coordBuffer.add(this.coords);
                     this.coords = new int[1048576];
                     p = 0;
                  }

                  this.coords[p++] = cl - upStep;
               }

               lastBelow = below;
            }

            cl++;
         }
      }

      return tiles;
   }
}
