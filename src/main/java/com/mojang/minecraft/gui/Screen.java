package com.mojang.minecraft.gui;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.renderer.Tesselator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Screen {
   protected Minecraft minecraft;
   protected int width;
   protected int height;

   public void render(int xMouse, int yMouse) {
   }

   public void init(Minecraft minecraft, int width, int height) {
      this.minecraft = minecraft;
      this.width = width;
      this.height = height;
      this.init();
   }

   public void init() {
   }

   protected void fill(int x0, int y0, int x1, int y1, int col) {
      float a = (col >> 24 & 0xFF) / 255.0F;
      float r = (col >> 16 & 0xFF) / 255.0F;
      float g = (col >> 8 & 0xFF) / 255.0F;
      float b = (col & 0xFF) / 255.0F;
      Tesselator t = Tesselator.instance;
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glColor4f(r, g, b, a);
      t.begin();
      t.vertex(x0, y1, 0.0F);
      t.vertex(x1, y1, 0.0F);
      t.vertex(x1, y0, 0.0F);
      t.vertex(x0, y0, 0.0F);
      t.end();
      GL11.glDisable(3042);
   }

   protected void fillGradient(int x0, int y0, int x1, int y1, int col1, int col2) {
      float a1 = (col1 >> 24 & 0xFF) / 255.0F;
      float r1 = (col1 >> 16 & 0xFF) / 255.0F;
      float g1 = (col1 >> 8 & 0xFF) / 255.0F;
      float b1 = (col1 & 0xFF) / 255.0F;
      float a2 = (col2 >> 24 & 0xFF) / 255.0F;
      float r2 = (col2 >> 16 & 0xFF) / 255.0F;
      float g2 = (col2 >> 8 & 0xFF) / 255.0F;
      float b2 = (col2 & 0xFF) / 255.0F;
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glBegin(7);
      GL11.glColor4f(r1, g1, b1, a1);
      GL11.glVertex2f(x1, y0);
      GL11.glVertex2f(x0, y0);
      GL11.glColor4f(r2, g2, b2, a2);
      GL11.glVertex2f(x0, y1);
      GL11.glVertex2f(x1, y1);
      GL11.glEnd();
      GL11.glDisable(3042);
   }

   public void drawCenteredString(String str, int x, int y, int color) {
      Font font = this.minecraft.font;
      font.drawShadow(str, x - font.width(str) / 2, y, color);
   }

   public void drawString(String str, int x, int y, int color) {
      Font font = this.minecraft.font;
      font.drawShadow(str, x, y, color);
   }

   public void updateEvents() {
      while (Mouse.next()) {
         if (Mouse.getEventButtonState()) {
            int xm = Mouse.getEventX() * this.width / this.minecraft.width;
            int ym = this.height - Mouse.getEventY() * this.height / this.minecraft.height - 1;
            this.mouseClicked(xm, ym, Mouse.getEventButton());
         }
      }

      while (Keyboard.next()) {
         if (Keyboard.getEventKeyState()) {
            this.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
         }
      }
   }

   protected void keyPressed(char eventCharacter, int eventKey) {
   }

   protected void mouseClicked(int x, int y, int button) {
   }

   public void tick() {
   }
}
