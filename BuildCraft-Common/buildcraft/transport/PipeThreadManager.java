package buildcraft.transport;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent;
import buildcraft.core.utils.BCLog;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class PipeThreadManager implements ITickHandler
{
	public static final PipeThreadManager instance = new PipeThreadManager();
	
	public final HashMap<Integer, PipeWorldThreadManager> pipeManagers = new HashMap<Integer, PipeWorldThreadManager>();
	
	public void addPipe(TileGenericPipe pipe)
	{
		if(!pipeManagers.containsKey(pipe.getWorld().provider.dimensionId))
		{
			pipeManagers.put(pipe.getWorld().provider.dimensionId, new PipeWorldThreadManager());
		}
		pipeManagers.get(pipe.getWorld().provider.dimensionId).addPipe(pipe);
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		World world = (World)tickData[0];
		if(pipeManagers.containsKey(world.provider.dimensionId))
		{
			pipeManagers.get(world.provider.dimensionId).onTick();
		}
	}


	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
		
	}


	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.WORLD);
	}


	@Override
	public String getLabel()
	{
		return "BCThreaded Pipes";
	}
	
	@ForgeSubscribe
	public void onUnload(WorldEvent.Unload evt)
	{
		if(pipeManagers.containsKey(evt.world.provider.dimensionId))
		{
			pipeManagers.get(evt.world.provider.dimensionId).unload();
			pipeManagers.remove(evt.world.provider.dimensionId);
		}
	}
	
	public class PipeWorldThreadManager
	{
		
		protected final TickerMonitorObject waiter = new TickerMonitorObject();
		
		private final ArrayList<TileGenericPipe> pipes = new ArrayList<TileGenericPipe>();
		private final HashSet<TileGenericPipe> pipesToRemove = new HashSet<TileGenericPipe>();
		
		private PipeTicker pipeTicker = new PipeTicker();
		private Thread pipeThread;
		
		protected void unload()
		{
			if(pipeTicker != null)
			{
				pipeTicker.shouldStop = true;
			}
		}
		
		protected void addPipe(TileGenericPipe pipe)
		{
			pipes.add(pipe);
		}
		
		protected void onTick()
		{
			if(pipeThread != null)
			{
				if(pipeTicker.left > 0)
				{
					BCLog.logger.warning("WARNING: Pipes do not keep up. Pipes Left: "+pipeTicker.left);
					return;
				}
				synchronized(waiter)
				{
					waiter.notify();
				}
			}
			else
			{
				if(pipeTicker != null)
				{
					pipeThread = new Thread(pipeTicker);
					pipeThread.start();
				}
			}
		}
		
		public class TickerMonitorObject
		{
			
		}
		
		public class PipeTicker implements Runnable
		{
			public boolean shouldStop = false;
			
			public int left = -1;
			
			@Override
			public void run()
			{
				while(!shouldStop)
				{
					left = pipes.size() - 1;
					for(TileGenericPipe pipe : ImmutableList.copyOf(pipes))
					{
						if(pipe.isInvalid())
						{
							pipesToRemove.add(pipe);
						}
						else
						{
							pipe.updateEntity();
						}
						left--;
					}
					pipes.removeAll(pipesToRemove);
					pipesToRemove.clear();
					left = -1;
					try
					{
						synchronized(waiter)
						{
							waiter.wait();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			
		}
	}



}
