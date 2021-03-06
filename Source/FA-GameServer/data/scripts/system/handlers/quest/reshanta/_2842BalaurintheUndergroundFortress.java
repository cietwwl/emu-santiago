/*
 * This file is part of aion-unique <aion-unique.org>.
 *
 * aion-unique is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-unique is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-unique.  If not, see <http://www.gnu.org/licenses/>.
 */
package quest.reshanta;

import java.sql.Timestamp;
import java.util.Calendar;

import gameserver.configs.main.CustomConfig;
import gameserver.dataholders.DataManager;
import gameserver.model.gameobjects.Npc;
import gameserver.model.gameobjects.player.Player;
import gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import gameserver.quest.handlers.QuestHandler;
import gameserver.quest.model.QuestCookie;
import gameserver.quest.model.QuestState;
import gameserver.quest.model.QuestStatus;
import gameserver.services.InstanceService;
import gameserver.services.TeleportService;
import gameserver.utils.PacketSendUtility;
import gameserver.world.WorldMapInstance;


/**
 * @author Hilgert
 * 
 */
 
public class _2842BalaurintheUndergroundFortress extends QuestHandler
{
	private final static int	questId	= 2842;
	public static WorldMapInstance newInstance;

	public _2842BalaurintheUndergroundFortress()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.setNpcQuestData(266568).addOnQuestStart(questId);
		qe.setNpcQuestData(266568).addOnTalkEvent(questId);
		qe.setNpcQuestData(215447).addOnKillEvent(questId);
		qe.setNpcQuestData(214777).addOnKillEvent(questId);
		qe.setNpcQuestData(214773).addOnKillEvent(questId);
		qe.setNpcQuestData(214781).addOnKillEvent(questId);
		qe.setNpcQuestData(214784).addOnKillEvent(questId);
		qe.setNpcQuestData(700472).addOnKillEvent(questId);
		qe.setNpcQuestData(700474).addOnKillEvent(questId);
		qe.setNpcQuestData(700473).addOnKillEvent(questId);
		qe.addOnEnterWorld(questId);
	}

	@Override
	public boolean onDialogEvent(QuestCookie env)
	{
        final Player player = env.getPlayer();
		int targetId = 0;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

        // Check if Player has already entered instance within cooldown
        // Chamber of Roah
        int worldId = 300070000;
        int instanceMapId = DataManager.WORLD_MAPS_DATA.getTemplate(worldId).getInstanceMapId();
        if(CustomConfig.INSTANCE_COOLDOWN){
            if(InstanceService.canEnterSoloInstance(player, instanceMapId, 0) == false)
            {
                int mapname = DataManager.WORLD_MAPS_DATA.getTemplate(worldId).getMapNameId();
                int timeinMinutes = InstanceService.getTimeInfo(player).get(instanceMapId)/60;
                if (timeinMinutes >= 60 )
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_ENTER_INSTANCE_COOL_TIME_HOUR_CLIENT(mapname, timeinMinutes/60));
                else    
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_ENTER_INSTANCE_COOL_TIME_MIN_CLIENT(mapname, timeinMinutes));
                return sendQuestDialog(env, 1011);
            }
        }
        
		if(qs != null || qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 266568)
			{
				if(env.getDialogId() == 26)
					 return sendQuestDialog(env, 1011);
				else if(env.getDialogId() == 1002)
				{
				newInstance = InstanceService.getNextAvailableInstance(300070000);
				InstanceService.registerPlayerWithInstance(newInstance, player);
				
                // Set Cooldown for Instance use
                int cd = DataManager.WORLD_MAPS_DATA.getTemplate(worldId).getCooldown();
                Timestamp CDEndTime = new Timestamp(Calendar.getInstance().getTimeInMillis() + cd*60000);
                player.addInstanceCD(instanceMapId, CDEndTime, 0, 0);
                
				TeleportService.teleportTo(player, 300070000, newInstance.getInstanceId(), 504, 395, 95, 0);
				return defaultQuestStartDialog(env);
				}
				else 
					 return defaultQuestStartDialog(env);
			}
		}

		else if(qs.getStatus() == QuestStatus.REWARD && targetId == 266568)
		{
			qs.setQuestVarById(0, 0);
			updateQuestStatus(env);
			return defaultQuestEndDialog(env);
		}
		return false;
    }	

	public boolean onKillEvent(QuestCookie env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		if(qs.getStatus() == QuestStatus.START)
		{	
			if(player.getCommonData().getPosition().getMapId() == 300070000)
			{
				if(qs.getQuestVarById(0) < 38)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					return true;
				}
				else if(qs.getQuestVarById(0) == 38 || qs.getQuestVarById(0) > 38)
				{
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					TeleportService.teleportToNpc(player, 266568);
					return true;
				}
			}
		}
		return false;
	}
	
}
