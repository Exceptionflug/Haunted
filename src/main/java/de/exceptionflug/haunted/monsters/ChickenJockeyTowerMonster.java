package de.exceptionflug.haunted.monsters;

import de.exceptionflug.haunted.monster.Monster;
import de.exceptionflug.haunted.wave.ConfiguredWave;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;

public class ChickenJockeyTowerMonster extends AnimalMonster {

    private Chicken chicken;

    @Override
    public void spawn(Location location) {
        chicken = location.getWorld().spawn(location, Chicken.class);
        super.spawn(chicken, location);

        setAttackDamage(1);

        Entity top = chicken;
        for (int i = 0; i < 5; i++) {
            Monster monster = getContext().injector().getInstance(BabyZombieMonster.class);
            monster.spawn(location);
            ConfiguredWave wave = (ConfiguredWave) getPhase().wave();
            wave.monsters().put(monster.getUUID(), monster);
            top.addPassenger(monster.getEntity());
            top = monster.getEntity();
        }

    }

    @Override
    public void handleDeath() {
        ejectAll(chicken);
    }

    private void ejectAll(Entity entity) {
        System.out.println("ejectALl for " + entity.getType());
        entity.getPassengers().forEach(this::ejectAll);
        entity.getPassengers().forEach(entity::removePassenger);
    }

    @Override
    public void handleDamage(double damage, Entity damager) {
        super.handleDamage(damage, damager);
        ejectAll(chicken);
    }
}
