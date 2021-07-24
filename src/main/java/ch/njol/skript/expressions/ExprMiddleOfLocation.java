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
package ch.njol.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.Location;
import org.eclipse.jdt.annotation.Nullable;

@Name("Middle of Location")
@Description("Returns the Middle/Center of a location. In other words returns the middle of the X and Z coords of a location.")
@Examples({
		"command /stuck:",
		"\texecutable by: players",
		"\ttrigger:",
		"\t\tteleport player to the center of player's location",
		"\t\tsend \"You're no longer stuck.\""})
@Since("INSERT VERSION")
public class ExprMiddleOfLocation extends SimplePropertyExpression<Location, Location> {
	
	static {
		register(ExprMiddleOfLocation.class, Location.class, "(middle|center) [point]", "location");
	}
	
	@Override
	@Nullable
	public Location convert(Location loc) {
        loc.setX(loc.getBlockX() + 0.5);
        loc.setY(loc.getBlockY());
        loc.setZ(loc.getBlockZ() + 0.5);
		return loc;
	}
	
	@Override
	public Class<? extends Location> getReturnType() {
		return Location.class;
	}
	
	@Override
	protected String getPropertyName() {
		return "middle point";
	}
	
}
