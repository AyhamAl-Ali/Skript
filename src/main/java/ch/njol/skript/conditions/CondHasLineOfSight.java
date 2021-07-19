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
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("Has Line of Sight")
@Description("Checks whether the living entity has block line of sight to another living entity.")
@Examples({"target entity has line of sight on player",
			"loop all players:",
			"\tplayer has sight on location of targeted block of loop-player # Checks if another player is looking at the same location",
			"\tsend \"%loop-player% is looking at the same block as yours\" to player"})
@Since("INSERT VERSION")
public class CondHasLineOfSight extends PropertyCondition<LivingEntity> {
	
	static {
		register(CondHasLineOfSight.class, PropertyType.HAVE, "([line of] sight|view) on %livingentity/location%", "livingentities");
	}
	
	private Expression<?> target;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		target = exprs[1];
		return super.init(exprs, matchedPattern, isDelayed, parseResult);
	}

	@Override
	public boolean check(LivingEntity entity) {
		if (target instanceof Location)
			return entity.hasLineOfSight((Location) target);
		else
			return entity.hasLineOfSight((LivingEntity) target);
	}

	@Override
	protected String getPropertyName() {
		return "line of sight";
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return PropertyCondition.toString(this, PropertyType.HAVE, e, debug, getExpr(), "line of sight on " + target.toString(e, debug));
	}
	
}
