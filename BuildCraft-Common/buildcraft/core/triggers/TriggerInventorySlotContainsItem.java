package buildcraft.core.triggers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.BuildCraftCore;
import buildcraft.api.gates.ITriggerParameter;

public class TriggerInventorySlotContainsItem extends BCTrigger
{
	int id;
	public TriggerInventorySlotContainsItem(int slotID)
	{
		super(0, "Slot.ID.detect"+slotID);
		id = slotID;
	}
	@Override
	public int getIconIndex()
	{
		return BuildCraftCore.triggerInventoryContainsItem.getIconIndex();
	}
	@Override
	public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter)
	{
		if(parameter == null || parameter.getItemStack() == null)
		{
			return false;
		}
		
		if(tile != null && tile instanceof IInventory)
		{
			IInventory inv = (IInventory)tile;
			int itemID = parameter.getItemStack().itemID;
			ItemStack stack = inv.getStackInSlot(id);
			if(stack != null && stack.itemID == itemID)
			{
				return true;
			}
		}
		
		return false;
	}
	
	
	
}
