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

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.eclipse.jdt.annotation.Nullable;

@Name("Inventory Close Reason")
@Description("The <a href='/classes.html#inventoryclosereason'>inventory close reason</a> of a <a href='/events.html#inventory_close'>inventory close event</a>.")
@Examples({
	"on inventory close:",
		"\tif inventory close reason is teleport:",
			"\t\tsend \"Your inventory closed due to teleporting!\" to player"
})
@Events("Inventory Close")
@Since("INSERT VERSION")
public class ExprInventoryCloseReason extends SimpleExpression<InventoryCloseEvent.Reason> {
	
	static {
		Skript.registerExpression(ExprInventoryCloseReason.class, InventoryCloseEvent.Reason.class, ExpressionType.SIMPLE, "[the] inventory clos(e|ing) (reason|cause)");
	}
	
	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!getParser().isCurrentEvent(InventoryCloseEvent.class)) {
			Skript.error("The 'inventory close reason' expression can only be used in an inventory close event");
			return false;
		}
		return true;
	}
	
	@Override
	protected InventoryCloseEvent.Reason[] get(Event event) {
		if (!(event instanceof InventoryCloseEvent))
			return null;

		return new InventoryCloseEvent.Reason[]{((InventoryCloseEvent) event).getReason()};
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@Override
	public Class<? extends InventoryCloseEvent.Reason> getReturnType() {
		return InventoryCloseEvent.Reason.class;
	}
	
	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "inventory close reason";
	}
	
}
