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

package gameserver.quest.handlers.models.xmlQuest.operations;

import gameserver.model.gameobjects.player.Player;
import gameserver.quest.model.QuestCookie;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * @author Mr. Poke
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GiveItemOperation")
public class GiveItemOperation extends QuestOperation
{

	@XmlAttribute(name = "item_id", required = true)
	protected int	itemId;
	@XmlAttribute(required = true)
	protected int	count;

	/* (non-Javadoc)
	 * @see gameserver.quest.handlers.models.xmlQuest.operations.QuestOperation#doOperate(.gameserver.quest.model.QuestEnv)
	 */
	@Override
	public void doOperate(QuestCookie env)
	{
		Player player = env.getPlayer();
		player.getController().addItems(itemId, count);
	}

}
