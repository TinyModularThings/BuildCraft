package buildcraft.api.power;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class ILaserTargetRegistry
{
	static ArrayList<ILaserTarget> activeTargets = new ArrayList<ILaserTarget>();
	
	public static void addTarget(ILaserTarget par1)
	{
		if(!activeTargets.contains(par1))
		{
			activeTargets.add(par1);
		}
	}
	
	public static void removeTarget(ILaserTarget par1)
	{
		if(activeTargets.contains(par1))
		{
			activeTargets.remove(par1);
		}
	}
	
	public static ILaserTarget findLaser(TileEntity par1)
	{
		int meta = par1.worldObj.getBlockMetadata(par1.xCoord, par1.yCoord, par1.zCoord);
		int minX = par1.xCoord - 5;
		int minY = par1.yCoord - 5;
		int minZ = par1.zCoord - 5;
		int maxX = par1.xCoord + 5;
		int maxY = par1.yCoord + 5;
		int maxZ = par1.zCoord + 5;

		switch (ForgeDirection.values()[meta]) {
			case WEST:
				maxX = par1.xCoord;
				break;
			case EAST:
				minX = par1.xCoord;
				break;
			case DOWN:
				maxY = par1.yCoord;
				break;
			case UP:
				minY = par1.yCoord;
				break;
			case NORTH:
				maxZ = par1.zCoord;
				break;
			default:
			case SOUTH:
				minZ = par1.zCoord;
				break;
		}
		
		ArrayList<ILaserTarget> targets = new ArrayList<ILaserTarget>();
		
		for(ILaserTarget target : activeTargets)
		{
			int x = target.getXCoord();
			int y = target.getYCoord();
			int z = target.getZCoord();
			if(x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ && !target.isInvalidTarget() && target.requiresLaserEnergy())
			{
				targets.add(target);
			}
		}
		if(targets.isEmpty())
		{
			return null;
		}
		return targets.get(par1.worldObj.rand.nextInt(targets.size()));
	}
}
