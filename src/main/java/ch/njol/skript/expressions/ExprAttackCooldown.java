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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.eclipse.jdt.annotation.Nullable;

@Name("Attack Cooldown")
@Description("Gets the current cooldown for a player's attack. This is used to calculate damage, with 1.0 representing a fully charged attack and 0.0 representing a non-charged attack.")
@Examples({
		"set {_cd} to attack cooldown of attacker",
		"set attack cooldown of attacker to 1 # Full charged attack"
})
@Since("INSERT HERE")
public class ExprAttackCooldown extends SimplePropertyExpression<Player, Number>{
	
	static {
		register(ExprAttackCooldown.class, Number.class, "attack cooldown", "player");
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	protected String getPropertyName() {
		return "attack cooldown";
	}

	@Override
	@Nullable
	public Number convert(Player p) {
		return p.getAttackCooldown();
	}

}