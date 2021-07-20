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
package ch.njol.skript.lang;

import java.util.Iterator;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Checker;

/**
 * A condition which must be fulfilled for the trigger to continue. If the condition is in a section the behaviour depends on the section.
 *
 * @see Skript#registerCondition(Class, String...)
 */
public abstract class Condition extends Statement {
	
	private boolean negated = false;

	@Nullable
	private Expression<?>[] exprs;
	
	protected Condition() {}
	
	/**
	 * Set the expressions of the registered condition's syntax in the {@link Condition#init} method
	 * This is useful when having multiple expressions in the syntax and you need to get one of them later in other methods without creating a private field for it.
	 * 
	 * @param exprs
	 */
	protected final void setExprs(final Expression<?>[] exprs) {
		this.exprs = exprs;
	}
	
	/**
	 * This is useful when having multiple expressions in the registered condition's syntax and you need to get one or more of them in other methods without creating a private field for them.
	 * This will return null if {@link Condition#setExprs} was not used in the {@link Condition#init} method or if the syntax does not have expressions.
	 * 
	 * @return All the expressions used in the registered expression's syntax
	 */
	public final Expression<?>[] getExprs() {
		return exprs;
	}
	
	/**
	 * Checks whether this condition is satisfied with the given event. This should not alter the event or the world in any way, as conditions are only checked until one returns
	 * false. All subsequent conditions of the same trigger will then be omitted.<br/>
	 * <br/>
	 * You might want to use {@link SimpleExpression#check(Event, Checker)}
	 * 
	 * @param e the event to check
	 * @return <code>true</code> if the condition is satisfied, <code>false</code> otherwise or if the condition doesn't apply to this event.
	 */
	public abstract boolean check(Event e);
	
	@Override
	public final boolean run(Event e) {
		return check(e);
	}
	
	/**
	 * Sets the negation state of this condition. This will change the behaviour of {@link Expression#check(Event, Checker, boolean)}.
	 */
	protected final void setNegated(boolean invert) {
		negated = invert;
	}
	
	/**
	 * @return whether this condition is negated or not.
	 */
	public final boolean isNegated() {
		return negated;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked", "null"})
	@Nullable
	public static Condition parse(String s, @Nullable String defaultError) {
		s = s.trim();
		while (s.startsWith("(") && SkriptParser.next(s, 0, ParseContext.DEFAULT) == s.length())
			s = s.substring(1, s.length() - 1);
		return (Condition) SkriptParser.parse(s, (Iterator) Skript.getConditions().iterator(), defaultError);
	}
	
}
