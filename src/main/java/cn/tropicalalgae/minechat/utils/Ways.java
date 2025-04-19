package cn.tropicalalgae.minechat.utils;


import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import java.util.Iterator;
import java.util.function.Predicate;

public class Ways {
    public static Entity getPointedEntity(Entity entity, double distance) {
        Vector3 pos = new Vector3().set(entity);
        Vec3 vec3 = new Vec3(entity.getX(), entity.getY() + (double)entity.getEyeHeight(), entity.getZ());
        Vec3 vec31 = entity.getViewVector(0.0F);
        Vec3 vec32 = vec3.add(vec31.x * distance, vec31.y * distance, vec31.z * distance);
        Level level = entity.getCommandSenderWorld();
        BlockHitResult result = level.clip(new ClipContext(vec3, vec32, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        Vector3 loc = new Vector3().set(result.getLocation());
        if (loc != null) {
            distance = loc.distanceTo(pos);
        }
        Vec3 vec33 = entity.getViewVector(0.0F);
        Predicate<Entity> predicate = EntitySelector.NO_SPECTATORS.and((c) -> entity.isPickable());
        predicate = predicate.and((c) -> !c.isSpectator() && c.isAlive() && c.isPickable() && !isRidingOrRider(entity, c));
        Entity hit = pos.firstEntityExcluding(distance, vec33, entity.getCommandSenderWorld(), entity, predicate);
        if (hit != null) {
            hit = hit instanceof PartEntity ? ((PartEntity<?>)hit).getParent() : hit;
        }
        return hit;
    }
    public static boolean isRidingOrRider(Entity a, Entity b) {
        Iterator<Entity> var2 = a.getIndirectPassengers().iterator();
        Entity c;
        do {
            if (!var2.hasNext()) {
                var2 = b.getIndirectPassengers().iterator();
                do {
                    if (!var2.hasNext()) {
                        return false;
                    }
                    c = var2.next();
                } while(!a.equals(c));
                return true;
            }
            c = var2.next();
        } while(!b.equals(c));
        return true;
    }
}
