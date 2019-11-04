package ch.njol.skript.variables;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.lang.Expression;

/**
 * A parsed path to a variable.
 */
public class VariablePath {

	/**
	 * Name of variable split by list token ('::'). Elements are constant
	 * Strings or expressions. Note: expression elements must NOT return
	 * strings that may contain list token.
	 * 
	 * <p>Contents of this array should be considered stable; do not write to
	 * it.
	 */
	final Object[] path;
	
	/**
	 * List containing this variable. Cached when possible.
	 */
	@Nullable
	ListVariable cachedParent;
	
	/**
	 * If this variable is global, real scope of it is cached here.
	 */
	@Nullable
	VariableScope cachedGlobalScope;
	
	/**
	 * Creates a new variable path. Only elements that are strings, integers
	 * or expressions that produce either of these are allowed.
	 * @param path Path elements.
	 */
	public VariablePath(Object... path) {
		assert checkPath(path);
		this.path = path;
	}
	
	/**
	 * Checks that path meets criteria.
	 * @param path Path to check.
	 * @return True if check passed, otherwise false.
	 */
	private static boolean checkPath(Object... path) {
		for (Object o : path) {
			if (!(o instanceof Expression<?>) && !(o instanceof String) && !(o instanceof Integer)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if this path starts with given prefix path. Note that this will
	 * not be able to compare expression elements in paths.
	 * @param prefix Prefix path.
	 * @return Whether this starts with given path or not.
	 */
	public boolean startsWith(VariablePath prefix) {
		if (prefix.path.length > path.length) {
			return false; // Prefix can't be longer than this
		}
		
		// Require full equality for parts before last
		for (int i = 0; i < prefix.path.length - 1; i++) {
			if (path[i] instanceof Expression<?> || !path[i].equals(prefix.path[i])) {
				return false; // Prefix has part this doesn't have
			}
		}
		
		// Check if part of this starts with part in prefix
		int last = prefix.path.length - 1;
		if (path[last] instanceof Expression<?> || prefix.path[last] instanceof Expression) {
			return false; // String startsWith would be safe
		}
		return ((String) path[last]).startsWith((String) prefix.path[last]);
	}
	
	public VariablePath execute(@Nullable Event event) {
		throw new UnsupportedOperationException("not yet implemented");
	}
}