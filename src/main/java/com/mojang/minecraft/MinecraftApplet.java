package com.mojang.minecraft;


import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

public class MinecraftApplet extends Applet {
   private Canvas canvas;
   private Minecraft minecraft;
   private Thread thread = null;

   @Override
   public void init() {
      this.canvas = new Canvas() {
         @Override
         public void addNotify() {
            super.addNotify();
            MinecraftApplet.this.startGameThread();
         }

         @Override
         public void removeNotify() {
            MinecraftApplet.this.stopGameThread();
            super.removeNotify();
         }
      };
      this.minecraft = new Minecraft(this.canvas, this.getWidth(), this.getHeight(), false);
      this.minecraft.appletMode = true;
      this.setLayout(new BorderLayout());
      this.add(this.canvas, "Center");
      this.canvas.setFocusable(true);
      this.validate();
   }

   public void startGameThread() {
      if (this.thread == null) {
         this.thread = new Thread(this.minecraft);
         this.thread.start();
      }
   }

   @Override
   public void start() {
      this.minecraft.pause = false;
   }

   @Override
   public void stop() {
      this.minecraft.pause = true;
   }

   @Override
   public void destroy() {
      this.stopGameThread();
   }

   public void stopGameThread() {
      if (this.thread != null) {
         this.minecraft.stop();

         try {
            this.thread.join(5000L);
         } catch (InterruptedException var4) {
            try {
               this.minecraft.destroy();
            } catch (Exception var3) {
               var3.printStackTrace();
            }
         }

         this.thread = null;
      }
   }
}
