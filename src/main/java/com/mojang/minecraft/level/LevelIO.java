package com.mojang.minecraft.level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LevelIO {
    private static final int MAGIC_NUMBER = 656127880;
    private static final int CURRENT_VERSION = 1;
    private LevelLoaderListener levelLoaderListener;
    public String error = null;

    public LevelIO(LevelLoaderListener levelLoaderListener) {
        this.levelLoaderListener = levelLoaderListener;
    }

    public boolean load(Level level, InputStream in) {
        this.levelLoaderListener.beginLevelLoading("Loading level");
        this.levelLoaderListener.levelLoadUpdate("Reading..");

        try {
            DataInputStream dis = new DataInputStream(new GZIPInputStream(in));
            int magic = dis.readInt();
            if (magic != 656127880) {
                this.error = "Bad level file format";
                return false;
            } else {
                byte version = dis.readByte();
                if (version > 1) {
                    this.error = "Bad level file format";
                    return false;
                } else {
                    String name = dis.readUTF();
                    String creator = dis.readUTF();
                    long createTime = dis.readLong();
                    int width = dis.readShort();
                    int height = dis.readShort();
                    int depth = dis.readShort();
                    byte[] blocks = new byte[width * height * depth];
                    dis.readFully(blocks);
                    dis.close();
                    level.setData(width, depth, height, blocks);
                    level.name = name;
                    level.creator = creator;
                    level.createTime = createTime;
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.error = "Failed to load level: " + e.toString();
            return false;
        }
    }

    public boolean loadLegacy(Level level, InputStream in) {
        this.levelLoaderListener.beginLevelLoading("Loading level");
        this.levelLoaderListener.levelLoadUpdate("Reading..");

        try {
            DataInputStream dis = new DataInputStream(new GZIPInputStream(in));
            String name = "--";
            String creator = "unknown";
            long createTime = 0L;
            int width = 256;
            int height = 256;
            int depth = 64;
            byte[] blocks = new byte[width * height * depth];
            dis.readFully(blocks);
            dis.close();
            level.setData(width, depth, height, blocks);
            level.name = name;
            level.creator = creator;
            level.createTime = createTime;
            return true;
        } catch (Exception var12) {
            var12.printStackTrace();
            this.error = "Failed to load level: " + var12.toString();
            return false;
        }
    }

    public void save(Level level, OutputStream out) {
        try {
            DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(out));
            dos.writeInt(656127880);
            dos.writeByte(1);
            dos.writeUTF(level.name);
            dos.writeUTF(level.creator);
            dos.writeLong(level.createTime);
            dos.writeShort(level.width);
            dos.writeShort(level.height);
            dos.writeShort(level.depth);
            dos.write(level.blocks);
            dos.close();
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }
}
