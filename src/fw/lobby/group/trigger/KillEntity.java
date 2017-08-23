package fw.lobby.group.trigger;

import java.util.List;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KillEntity extends TriggerBaseExt {

	public KillEntity(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	public List<String> EntityName() {
		return Id;
	}

	@EventHandler(priority=EventPriority.HIGH)
	void KillEntityListen(EntityDamageByEntityEvent evt) {
		if (evt.getEntity() instanceof Creature) {
			Creature damaged = (Creature) evt.getEntity();
			boolean dir = false;
			Player damager;
			if (evt.getDamager() instanceof Player) {
				damager = (Player) evt.getDamager();
			} else if(evt.getDamager() instanceof Projectile){
				if(((Projectile)evt.getDamager()).getShooter() instanceof Player){
					damager = (Player)((Projectile)evt.getDamager()).getShooter();
				}else{
					return;
				}
			}else{
				return;
			}

			if (byGroup.hasPlayer(damager)) {
				if(damaged instanceof Player){
					if (((Player) damaged).getName() != null) {
						for (int c = 0; c < Id.size(); c++) {
							if (((Player) damaged).getName().indexOf(Id.get(c)) != -1) {
								if (((Damageable)damaged).getHealth()<=evt.getDamage()) {
									dir = true;
								}
							}
						}
					}
				}else{
					if (damaged.getCustomName() != null) {
						for (int c = 0; c < Id.size(); c++) {
							if (((Creature) damaged).getCustomName().indexOf(Id.get(c)) != -1) {
								if (((Damageable)damaged).getHealth()<=evt.getDamage()) {
									dir = true;
								}
							}
						}
					}
				}

			}
			if (dir) {
				Strike(damager);
			}
		}
	}
}
