package com.mojang.minecraft;

public class HitResult {
    public int type;
    public int x;
    public int y;
    public int z;
    public int f;

    public HitResult(int type, int x, int y, int z, int f) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.f = f;
    }

    public boolean isCloserThan(Player player, HitResult o, boolean creative) {
        float dist = this.distanceTo(player, false);
        float dist2 = o.distanceTo(player, false);
        if (dist < dist2) {
            return true;
        } else {
            dist = this.distanceTo(player, creative);
            dist2 = o.distanceTo(player, creative);
            return dist < dist2;
        }
    }

    private float distanceTo(Player player, boolean creative) {
        int xx = this.x;
        int yy = this.y;
        int zz = this.z;
        if (creative) {
            if (this.f == 0) yy--;
            if (this.f == 1) yy++;
            if (this.f == 2) zz--;
            if (this.f == 3) zz++;
            if (this.f == 4) xx--;
            if (this.f == 5) xx++;
        }

        float xd = xx - player.x;
        float yd = yy - player.y;
        float zd = zz - player.z;
        return xd * xd + yd * yd + zd * zd;
    }
}
