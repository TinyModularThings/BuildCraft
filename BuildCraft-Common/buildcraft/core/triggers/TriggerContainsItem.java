package buildcraft.core.triggers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.BuildCraftCore;
import buildcraft.api.gates.ITriggerParameter;
import buildcraft.core.inventory.InventoryIterator;
import buildcraft.core.inventory.InventoryIterator.IInvSlot;

public class TriggerContainsItem extends BCTrigger
{
	
	public TriggerContainsItem()
	{
		super(0, "contains.item.id");
	}
	
	@Override
	public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter)
	{
		if(parameter == null)
		{
			return false;
		}
		ItemStack stack = parameter.getItemStack();
		if(stack != null)
		{
			if(tile instanceof IInventory)
			{
				boolean found = false;
				for(IInvSlot slot : InventoryIterator.getIterable((IInventory)tile, side))
				{
					ItemStack slotItem = slot.getStackInSlot();
					if(slotItem != null && stack.itemID == slotItem.itemID)
					{
						found = true;
						break;
					}
				}
				return found;
			}
		}
		return false;	
	}

	@Override
	public int getIconIndex()
	{
		return BuildCraftCore.triggerContainsInventory.getIconIndex();
	}
	
	
	
}
