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
import org.eclipse.jdt.annotation.Nullable;

@Name("Last String Color")
@Description("Gets the ChatColors used at the end of the given input string.")
@Examples("set {_c} to last color of \"&aColored &cMessage\" # return &c")
@Since("INSERT HERE")
public class ExprLastColor extends SimplePropertyExpression<String, String>{
	
	static {
		register(ExprLastColor.class, String.class, "last (chat[ ]|string|) color", "string");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected String getPropertyName() {
		return "last color";
	}

	@Override
	@Nullable
	public String convert(String str) {
		return ChatColor.getLastColors(str);
	}

}