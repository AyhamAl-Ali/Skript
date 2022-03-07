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
package ch.njol.skript.hooks.economy.expressions;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.hooks.VaultHook;
import ch.njol.skript.hooks.economy.classes.Money;

@Name("Money")
@Description("How much virtual money a player has (can be changed). This expression requires Vault and a compatible economy plugin to be installed.")
@Examples({"message \"You have %player's money%\" # the currency name will be added automatically",
		"remove 20$ from the player's balance # replace '$' by whatever currency you use",
		"add 200 to the player's account # or omit the currency altogether"})
@Since("2.0, 2.5 (offline player support)")
@RequiredPlugins({"Vault", "a permission plugin that supports Vault"})
public class ExprBalance extends SimplePropertyExpression<OfflinePlayer, Money> {

	static {
		register(ExprBalance.class, Money.class, "(money|balance|[bank] account)", "offlineplayers");
	}
	
	@Override
	public Money convert(OfflinePlayer p) {
		try {
			return new Money(VaultHook.economy.getBalance(p));
		} catch (Exception e) {
			return new Money(VaultHook.economy.getBalance(p.getName()));
		}
	}
	
	@Override
	public Class<? extends Money> getReturnType() {
		return Money.class;
	}
	
	@Override
	protected String getPropertyName() {
		return "money";
	}
	
	@Override
	@Nullable
	public Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.REMOVE_ALL)
			return null;
		return new Class[] {Money.class, Number.class};
	}
	
	@Override
	public void change(Event e, @Nullable Object[] delta, ChangeMode mode) {
		if (delta == null) { // RESET/DELETE
			for (OfflinePlayer p : getExpr().getArray(e))
				VaultHook.economy.withdrawPlayer(p, VaultHook.economy.getBalance(p));
			return;
		}

		double m = delta[0] instanceof Number ? ((Number) delta[0]).doubleValue() : ((Money) delta[0]).getAmount();
		for (OfflinePlayer p : getExpr().getArray(e)) {
			switch (mode) {
				case SET:
					double b = VaultHook.economy.getBalance(p);
					if (b < m) {
						VaultHook.economy.depositPlayer(p, m - b);
					} else if (b > m) {
						VaultHook.economy.withdrawPlayer(p, b - m);
					}
					break;
				case ADD:
					VaultHook.economy.depositPlayer(p, m);
					break;
				case REMOVE:
					VaultHook.economy.withdrawPlayer(p, m);
					break;
			}
		}
	}
	
}
