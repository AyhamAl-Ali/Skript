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
import ch.njol.skript.command.Commands;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.UnparsedLiteral;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

@Name("String Concatenation")
@Description("Concatenate a string with any object or vise versa")
@Examples({
	"send \"&aUUID: \" + player's uuid",
	"broadcast \"Remaining time:\" + time since {time::last-use}"
})
@Since("INSERT VERSION")
public class ExprStringConcatenation extends SimpleExpression<String> {
	
	static {
		Skript.registerExpression(ExprStringConcatenation.class, String.class, ExpressionType.SIMPLE,
			"%string% + %object%",
			"%object% + %string%");
	}

	private boolean isObjectFirst;
	private Expression<?> object;
	private Expression<? extends String> string;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		isObjectFirst = matchedPattern == 1;
		if (isObjectFirst) {
			string = (Expression<? extends String>) exprs[1];
			object = LiteralUtils.defendExpression(exprs[0]);
		}
		else {
			string = (Expression<? extends String>) exprs[0];
			object = LiteralUtils.defendExpression(exprs[1]);
		}

		return LiteralUtils.canInitSafely(object);
	}
	
	@Nullable
	@Override
	@SuppressWarnings("null")
	protected String[] get(Event e) {
		String string = this.string.getSingle(e);
		Object object = this.object.getSingle(e);

		if (object == null)
			return new String[] {string};
		if (string == null)
			return new String[] {Classes.toString(object)};

		if (isObjectFirst)
			return new String[] {Classes.toString(object).concat(string)};
		else
			return new String[] {string.concat(Classes.toString(object))};
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}
	
	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return "concatenate " +
			(isObjectFirst ? object.toString(e, debug) + " and " + string.toString(e, debug) :
				string.toString(e, debug) + " and " + object.toString(e, debug));
	}
	
}
