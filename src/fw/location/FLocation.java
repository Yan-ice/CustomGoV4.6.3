package fw.location;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import fw.Data;
/**
 * 为了方便进行坐标获取与传送，
 * 作为Location的代理类。
 *
 */
public class FLocation {
	Double x;
	Double y;
	Double z;
	World world;

	public FLocation(double x, double y, double z, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	public FLocation(double x, double y, double z, String world) {
		this.x = (double) x;
		this.y = (double) y;
		this.z = (double) z;
		this.world = Bukkit.getWorld(world);
	}

	/**
	 * 直接将一个固定格式的字符串读取为坐标。
	 * 
	 * @param Location
	 *            一个按照 X,Y,Z,World 格式的字符串
	 */
	public FLocation(String Location) {
		try{
			String[] LocationS = Location.split(",");
			if (LocationS.length >= 4) {
				x = Double.valueOf(LocationS[0]);
				y = Double.valueOf(LocationS[1]);
				z = Double.valueOf(LocationS[2]);
				world = Bukkit.getWorld(LocationS[3]);
			}
		}catch(NumberFormatException e){
		}

	}

	/**
	 * 获取玩家坐标来构造它。
	 * 
	 * @param Pl
	 *            需要获取坐标的玩家
	 */
	public FLocation(Player Pl) {
		World world = Pl.getWorld();
		double X = Pl.getLocation().getX();
		double Y = Pl.getLocation().getY();
		double Z = Pl.getLocation().getZ();
		X = (double) ((int) (X * 100)) / 100;
		Z = (double) ((int) (Z * 100)) / 100;
		x = X;
		y = Y;
		z = Z;
		this.world = world;
	}

	/**
	 * 获得Minecraft的坐标来构造它。
	 * 
	 * @param Loc
	 *            需要获取的坐标
	 */
	public FLocation(Location Loc) {
		double X = Loc.getX();
		double Y = Loc.getY();
		double Z = Loc.getZ();
		X = (double) ((int) (X * 100)) / 100;
		Z = (double) ((int) (Z * 100)) / 100;
		x = X;
		y = Y;
		z = Z;
		this.world = Loc.getWorld();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public World getWorld() {
		return world;
	}

	public void setLoc(double x, double y, double z, World world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}

	/**
	 * 将该坐标处理为bukkit规范坐标Location格式。
	 * 
	 * @return bukkit坐标实例Location
	 */
	public Location ToMC() {
		Location loc = new Location(world, x, y, z);
		return loc;
	}
	/**
	 * 在坐标列表中随机选择一个坐标返回。
	 */
	public static FLocation RandomLoc(List<FLocation> Loc) {
		Random random = new Random();
		for (int a = 0; a < (Loc.size() * 2); a++) {
			a = random.nextInt(Loc.size());
			if (FLocation.SafeLoc(Loc.get(a)) != null) {
				return Loc.get(a);
			}
		}
		return null;
	}
	/**
	 * 将一个坐标自动偏移移动，直到处理为安全坐标便返回。
	 * 如果无法自动找到安全位置，将返回null。
	 */
	public static FLocation SafeLoc(FLocation Loc) {
		if(Loc!=null || !Loc.isComplete()){
			for (double n = Loc.getY(); (Loc.ToMC().getBlock().getTypeId() != 0) && n <= 255; n++) {
				Loc.setLoc(Loc.getX(), n, Loc.getZ(), Loc.getWorld());
			}
			for (double n = Loc.getY(); n > 0; n--) {
				Loc.setLoc(Loc.getX(), n, Loc.getZ(), Loc.getWorld());
				if (Loc.ToMC().getBlock().getTypeId() != 0) {
					Loc.setLoc(Loc.getX(), n + 1, Loc.getZ(), Loc.getWorld());
					return Loc;
				}
			}
		}
		return null;
	}

	/**
	 * 检查坐标是否完整。
	 */
	public boolean isComplete() {
		if ((x != null) && (y != null) && (z != null) && (world != null)) {
			return true;
		}
		
		return false;
	}
}
