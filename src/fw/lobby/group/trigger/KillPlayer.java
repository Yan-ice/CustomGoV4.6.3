package fw.lobby.group.trigger;

import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fw.Data;

public class KillPlayer extends TriggerBaseExt {

	public KillPlayer(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	void KillPlayerListen(EntityDamageByEntityEvent evt) {
		if (evt.getEntity() instanceof Player) {
			Player damaged = (Player) evt.getEntity();
			Player damager;
			if (((Damageable)damaged).getHealth()<=evt.getDamage()) {
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
					Strike(damager);
				}
			}
		}

	}
}
