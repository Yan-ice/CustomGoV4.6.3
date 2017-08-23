package fw.lobby.group.trigger;

import java.util.List;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import fw.Data;

public class KillAllEntity extends TriggerBaseExt {

	public KillAllEntity(String Name) {
		super(Name);
		// TODO Auto-generated constructor stub
	}

	public List<String> EntityName() {
		return Id;
	}

	@EventHandler(priority=EventPriority.HIGH)
	void KillEntityListen(EntityDamageEvent evt) {
		if (evt.getEntity() instanceof Creature) {
			Creature damaged = (Creature) evt.getEntity();

			if (((Damageable)damaged).getHealth()<=evt.getDamage()) {
				List<Entity> pl = damaged.getNearbyEntities(30, 30, 30);
				for(int a = 0;a<pl.size();a++){
					if(pl.get(a) instanceof Player){
						Player player = (Player)pl.get(a);
						
						if (byGroup.hasPlayer(player)) {
							if(damaged instanceof Player){
								if (((Player) damaged).getName() != null) {
									for(int n =0;n<Id.size();n++){
										if(((Player) damaged).getName().indexOf(Id.get(n))!=-1){
											if(!SearchEntity(pl)){
												Strike(null);
												return;
											}else{
												return;
											}
										}
									}
								}
							}else{
								if (((Creature) damaged).getCustomName() != null) {
									for(int n =0;n<Id.size();n++){
										if(((Creature) damaged).getCustomName().indexOf(Id.get(n))!=-1){
											if(!SearchEntity(pl)){
												Strike(null);
												return;
											}else{
												return;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean SearchEntity(List<Entity> entity) {
		for(int a = 0;a<entity.size();a++){
			if(entity.get(a) instanceof Creature){
				if(entity.get(a) instanceof Player){
					for(int b = 0;b<Id.size();b++){
						if(((Player)entity.get(a)).getName()!=null && ((Player)entity.get(a)).getName().indexOf(Id.get(b))!=-1){
							return true;
						}
					}
				}else{
					for(int b = 0;b<Id.size();b++){
						if(((Creature)entity.get(a)).getCustomName()!=null && ((Creature)entity.get(a)).getCustomName().indexOf(Id.get(b))!=-1){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
