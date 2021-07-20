/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.conditions.base.PropertyCondition.PropertyType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Has Line of Sight")
@Description("Checks whether the living entity has block line of sight to another living entity.")
@Examples({"target entity has line of sight on player",
			"",
			"loop all players:",
			"\tloop-player is not player # Filter player",
			"\tloop-player has sight on player # Checks if another player is looking at player",
			"\tsend \"%loop-player% is looking at you!\" to player"})
@Since("INSERT VERSION")
public class CondHasLineOfSight extends Condition {
	
	static {
		PropertyCondition.register(CondHasLineOfSight.class, PropertyType.HAVE, "([line of] sight|view) on %entity%", "livingentities");
	}
	
	private Expression<LivingEntity> entity;
	private Expression<Entity> target;

	@Override
	public boolean init(Expression[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		entity = (Expression<LivingEntity>) exprs[0];
		target = (Expression<Entity>) exprs[1];
		setNegated(matchedPattern == 1);
		return true;
	}

	@Override
	public boolean check(Event e) {
		Entity target = this.target.getSingle(e);
		if (target == null)
			return false;
		
		return entity.check(e, ev -> ev.hasLineOfSight(target), isNegated());
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return PropertyCondition.toString(this, PropertyType.HAVE, e, debug, entity, "line of sight on " + target.toString(e, debug));
	}
	
}
