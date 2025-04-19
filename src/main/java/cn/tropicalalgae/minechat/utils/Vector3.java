package cn.tropicalalgae.minechat.utils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Vector3 {
    public double x, y, z;
    public static Vector3 entity(Entity e) {
        return e != null ? new Vector3().set(e.getX(), e.getY() + (double)e.getEyeHeight(), e.getZ()) : null;
    }
    Vector3() {this.x = this.y = this.z = 0.0;}
    public double distanceTo(Vector3 vec) {
        this.x = this.y = this.z = 0.0;
        for(int i = 0; i < 3; ++i) {
            double j = this.get(i) - vec.get(i);
            if (i == 0) {
                this.x = j;
            } else if (i == 1) {
                this.y = j;
            } else {
                this.z = j;
            }
        }
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    public Entity firstEntityExcluding(double range, Vec3 vec31, Level level, Entity entity, Predicate<Entity> predicate) {
        Entity pointedEntity = null;
        if (predicate == null) {
            predicate = EntitySelector.NO_SPECTATORS;
        }
        Vec3 vec3 = new Vec3(this.x, this.y, this.z);
        Vec3 vec32 = vec3.add(vec31.x * range, vec31.y * range, vec31.z * range);
        AABB aabb = new AABB(this.x, this.y, this.z, this.x, this.y, this.z)
                .expandTowards(vec31.x * range, vec31.y * range, vec31.z * range)
                .inflate(2.5, 2.5, 2.5);
        List<Entity> mobs = level.getEntities(entity, aabb, predicate);
        double ds = range * range;
        for (Entity entity1 : mobs) {
            AABB aabb1 = entity1.getBoundingBox().inflate(0.30000001192092896);
            Optional<Vec3> optional = aabb1.clip(vec3, vec32);
            if (optional.isPresent()) {
                double d1 = vec3.distanceToSqr(optional.get());
                if (d1 < ds) {
                    pointedEntity = entity1;
                    ds = d1;
                }
            }
        }
        return pointedEntity;
    }
    public double get(int i) {return i == 0 ? this.x : (i == 1 ? this.y : this.z);}
    public Vector3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    public Vector3 set(Entity e) {
        if (e != null) {
            this.x = e.getX();
            this.y = e.getY() + (double)(e.getBbHeight() / 2.0F);
            this.z = e.getZ();
        }
        return this;
    }
    public Vector3 set(Object o) {
        if (o instanceof Vec3 p) {
            this.set(p.x, p.y, p.z);
        }
        return this;
    }
    public String toString() {
        return "x:" + this.x + " y:" + this.y + " z:" + this.z;
    }
}

