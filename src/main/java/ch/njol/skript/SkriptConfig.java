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
package ch.njol.skript;

import ch.njol.skript.config.Config;
import ch.njol.skript.config.EnumParser;
import ch.njol.skript.config.Option;
import ch.njol.skript.config.OptionSection;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.hooks.Hook;
import ch.njol.skript.hooks.VaultHook;
import ch.njol.skript.hooks.regions.GriefPreventionHook;
import ch.njol.skript.hooks.regions.PreciousStonesHook;
import ch.njol.skript.hooks.regions.ResidenceHook;
import ch.njol.skript.hooks.regions.WorldGuardHook;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.localization.Language;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.log.Verbosity;
import ch.njol.skript.timings.SkriptTimings;
import ch.njol.skript.update.ReleaseChannel;
import ch.njol.skript.util.FileUtils;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.util.Version;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.skript.util.chat.LinkParseMode;
import ch.njol.skript.variables.Variables;
import co.aikar.timings.Timings;
import org.bukkit.event.EventPriority;
import org.eclipse.jdt.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Important: don't save values from the config, a '/skript reload config/configs/all' won't work correctly otherwise!
 *
 * @author Peter Güttinger
 */
@SuppressWarnings("unused")
public class SkriptConfig {

	@Nullable
	public static Config mainConfig;
	static Collection<Config> configs = new ArrayList<>();


	@SuppressWarnings("null")
	private static final DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

	public enum Options {
		VERSION(new Option<>("version", Skript.getVersion().toString())
			.optional(true)),
		LANGUAGE(new Option<>("language", "english")
			.optional(true)
			.setter(s -> {
				if (!Language.load(s)) {
					Skript.error("No language file found for '" + s + "'!");
				}
			})),
		CHECK_FOR_NEW_VERSION(new Option<>("check for new version", false)
			.setter(t -> {
				SkriptUpdater updater = Skript.getInstance().getUpdater();
				if (updater != null)
					updater.setEnabled(t);
			})),
		UPDATE_CHECK_INTERVAL(new Option<>("updater download tries", 7)
			.optional(true)),
		UPDATER_DOWNLOAD_TRIES(new Option<>("updater download tries", 7)
			.optional(true)),
		RELEASE_CHANNEL(new Option<>("release channel", "none")
			.setter(t -> {
				ReleaseChannel channel;
				switch (t) {
					case "alpha":
					case "beta":
						Skript.warning("'alpha' and 'beta' are no longer valid release channels. Use 'prerelease' instead.");
					case "prerelease": // All development builds are valid
						channel = new ReleaseChannel((name) -> true, t);
						break;
					case "stable":
						// TODO a better option would be to check that it is not a pre-release through GH API
						channel = new ReleaseChannel((name) -> !name.contains("pre"), t);
						break;
					case "none":
						channel = new ReleaseChannel((name) -> false, t);
						break;
					default:
						channel = new ReleaseChannel((name) -> false, t);
						Skript.error("Unknown release channel '" + t + "'.");
						break;
				}
				SkriptUpdater updater = Skript.getInstance().getUpdater();
				if (updater != null) {
					updater.setReleaseChannel(channel);
				}
			})),
		ENABLE_EFFECT_COMMANDS(new Option<>("enable effect commands", false)),
		EFFECT_COMMAND_TOKEN(new Option<>("effect command token", "!")),
		ALLOW_OPS_TO_USE_EFFECT_COMMANDS(new Option<>("allow ops to use effect commands", false)),
		/*
		 * @deprecated Will be removed in 2.8.0. Use {@link #logEffectCommands} instead.
		 */
		@Deprecated
		LOG_PLAYER_COMMANDS(new Option<>("log player commands", false).optional(true)),
		LOG_EFFECT_COMMANDS(new Option<>("log effect commands", false)),
		DATABASES(new OptionSection("databases")),
		USE_PLAYER_UUIDS_IN_VARIABLE_NAMES(new Option<>("use player UUIDs in variable names", false)),
		ENABLE_PLAYER_VARIABLE_FIX(new Option<>("player variable fix", true)),
		DATE_FORMAT(new Option<>("date format", shortDateFormat, s -> {
			try {
				if (s.equalsIgnoreCase("default"))
					return null;
				return new SimpleDateFormat(s);
			} catch (final IllegalArgumentException e) {
				Skript.error("'" + s + "' is not a valid date format. Please refer to https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html for instructions on the format.");
			}
			return null;
		})),
		VERBOSITY(new Option<>("verbosity", Verbosity.NORMAL, new EnumParser<>(Verbosity.class, "verbosity"))
			.setter(SkriptLogger::setVerbosity)),
		DEFAULT_EVENT_PRIORITY(new Option<>("plugin priority", EventPriority.NORMAL, s -> {
			try {
				return EventPriority.valueOf(s.toUpperCase(Locale.ENGLISH));
			} catch (final IllegalArgumentException e) {
				Skript.error("The plugin priority has to be one of lowest, low, normal, high, or highest.");
				return null;
			}
		})),
		NUMBER_ACCURACY(new Option<>("number accuracy", 2)),
		MAX_TARGET_BLOCK_DISTANCE(new Option<>("maximum target block distance", 100)),
		CASE_SENSITIVE(new Option<>("case sensitive", false)),
		ALLOW_FUNCTIONS_BEFORE_DEFS(new Option<>("allow function calls before definations", false)
			.optional(true)),
		DISABLE_OBJECT_CANNOT_BE_SAVED_WARNINGS(new Option<>("disable variable will not be saved warnings", false)),
		DISABLE_MISSING_AND_OR_WARNINGS(new Option<>("disable variable missing and/or warnings", false)),
		DISABLE_VARIABLE_STARTING_WITH_EXPRESSION_WARNINGS(new Option<>("disable starting a variable's name with an expression warnings", false)),
		ENABLE_SCRIPT_CACHING(new Option<>("enable script caching", false)
			.optional(true)),
		KEEP_CONFIGS_LOADED(new Option<>("keep configs loaded", false)
			.optional(true)),
		ADDON_SAFETY_CHECKS(new Option<>("addon safety checks", false)
			.optional(true)),
		API_SOFT_EXCEPTIONS(new Option<>("soft api exceptions", false)),
		ENABLE_TIMINGS(new Option<>("enable timings", false)
			.setter(t -> {
				if (!Skript.classExists("co.aikar.timings.Timings")) { // Check for Timings
					if (t) // Warn the server admin that timings won't work
						Skript.warning("Timings cannot be enabled! You are running Bukkit/Spigot, but Paper is required.");
					SkriptTimings.setEnabled(false); // Just to be sure, deactivate timings support completely
					return;
				}
				if (Timings.class.isAnnotationPresent(Deprecated.class)) { // check for deprecated Timings
					if (t) // Warn the server admin that timings won't work
						Skript.warning("Timings cannot be enabled! Paper no longer supports Timings as of 1.19.4.");
					SkriptTimings.setEnabled(false); // Just to be sure, deactivate timings support completely
					return;
				}
				// If we get here, we can safely enable timings
				if (t)
					Skript.info("Timings support enabled!");
				SkriptTimings.setEnabled(t); // Config option will be used
			})),
		PARSE_LINKS(new Option<>("parse links in chat messages", "disabled")
			.setter(t -> {
				try {
					switch (t) {
						case "false":
						case "disabled":
							ChatMessages.linkParseMode = LinkParseMode.DISABLED;
							break;
						case "true":
						case "lenient":
							ChatMessages.linkParseMode = LinkParseMode.LENIENT;
							break;
						case "strict":
							ChatMessages.linkParseMode = LinkParseMode.STRICT;
							break;
						default:
							ChatMessages.linkParseMode = LinkParseMode.DISABLED;
							Skript.warning("Unknown link parse mode: " + t + ", please use disabled, strict or lenient");
					}
				} catch (Error e) {
					// Ignore it, we're on unsupported server platform and class loading failed
				}
			})),
		CASE_INSENSITIVE_VARIABLES(new Option<>("case-insensitive variables", true)
			.setter(t -> Variables.caseInsensitiveVariables = t)),
		COLOR_RESET_CODES(new Option<>("color codes reset formatting", true)
			.setter(t -> {
				try {
					ChatMessages.colorResetCodes = t;
				} catch (Error e) {
					// Ignore it, we're on unsupported server platform and class loading failed
				}
			})),
		SCRIPT_LOADER_THREAD_SIZE(new Option<>("script loader thread size", "0")
			.setter(s -> {
				int asyncLoaderSize;

				if (s.equalsIgnoreCase("processor count")) {
					asyncLoaderSize = Runtime.getRuntime().availableProcessors();
				} else {
					try {
						asyncLoaderSize = Integer.parseInt(s);
					} catch (NumberFormatException e) {
						Skript.error("Invalid option: " + s);
						return;
					}
				}

				ScriptLoader.setAsyncLoaderSize(asyncLoaderSize);
			})
			.optional(true)),
		ALLOW_UNSAFE_PLATFORMS(new Option<>("allow unsafe platforms", false)
			.optional(true)),
		KEEP_LAST_USAGE_DATES(new Option<>("keep command last usage dates", false)
			.optional(true)),
		LOAD_DEFAULT_ALIASES(new Option<>("load default aliases", true)
			.optional(true)),
		EXECUTE_FUNCTIONS_WITH_MISSING_PARAMS(new Option<>("execute functions with missing parameters", true)
			.optional(true)
			.setter(t -> Function.executeWithNulls = t)),
		DISABLE_HOOK_VAULT(new Option<>("disable hooks.vault", false)
			.optional(true)
			.setter(value -> {
				userDisableHooks(VaultHook.class, value);
			})),
		DISABLE_HOOK_GRIEF_PREVENTION(new Option<>("disable hooks.regions.grief prevention", false)
			.optional(true)
			.setter(value -> {
				userDisableHooks(GriefPreventionHook.class, value);
			})),
		DISABLE_HOOK_PRECIOUS_STONES(new Option<>("disable hooks.regions.precious stones", false)
			.optional(true)
			.setter(value -> {
				userDisableHooks(PreciousStonesHook.class, value);
			})),
		DISABLE_HOOK_RESIDENCE(new Option<>("disable hooks.regions.residence", false)
			.optional(true)
			.setter(value -> {
				userDisableHooks(ResidenceHook.class, value);
			})),
		DISABLE_HOOK_WORLD_GUARD(new Option<>("disable hooks.regions.worldguard", false)
			.optional(true)
			.setter(value -> {
				userDisableHooks(WorldGuardHook.class, value);
			})),
		PLAYER_NAME_REGEX_PATTERN(new Option<>("player name regex pattern", Pattern.compile("[a-zA-Z0-9_]{1,16}"), s -> {
			try {
				return Pattern.compile(s);
			} catch (PatternSyntaxException e) {
				Skript.error("Invalid player name regex pattern: " + e.getMessage());
				return null;
			}
		}).optional(true)),
		LONG_PARSE_TIME_WARNING_THRESHOLD(new Option<>("long parse time warning threshold", new Timespan(0)));

		private Option<?> option;
		private OptionSection optionSection;

		Options(Option<?> option) {
			this.option = option;
		}

		Options(OptionSection optionSection) {
			this.optionSection = optionSection;
		}

		public Option<?> getOption() {
			return option;
		}

		public void setOption(Option<?> option) {
			this.option = option;
		}
	}

	/**
	 * Disables the specified hook depending on the option value, or gives an error if this isn't allowed at this time.
	 */
	private static void userDisableHooks(Class<? extends Hook<?>> hookClass, boolean value) {
		if (Skript.isFinishedLoadingHooks()) {
			Skript.error("Hooks cannot be disabled once the server has started. " +
				"Please restart the server to disable the hooks.");
			return;
		}
		if (value) {
			Skript.disableHookRegistration(hookClass);
		}
	}

	/**
	 * This should only be used in special cases
	 */
	@Nullable
	public static Config getConfig() {
		return mainConfig;
	}

	// also used for reloading
	static boolean load() {
		try {
			final File oldConfigFile = new File(Skript.getInstance().getDataFolder(), "config.cfg");
			final File configFile = new File(Skript.getInstance().getDataFolder(), "config.sk");
			if (oldConfigFile.exists()) {
				if (!configFile.exists()) {
					oldConfigFile.renameTo(configFile);
					Skript.info("[1.3] Renamed your 'config.cfg' to 'config.sk' to match the new format");
				} else {
					Skript.error("Found both a new and an old config, ignoring the old one");
				}
			}
			if (!configFile.exists()) {
				Skript.error("Config file 'config.sk' does not exist!");
				return false;
			}
			if (!configFile.canRead()) {
				Skript.error("Config file 'config.sk' cannot be read!");
				return false;
			}

			Config mc;
			try {
				mc = new Config(configFile, false, false, ":");
			} catch (final IOException e) {
				Skript.error("Could not load the main config: " + e.getLocalizedMessage());
				return false;
			}
			mainConfig = mc;

			String configVersion = mc.get(Options.VERSION.option.key);
			if (configVersion == null || Skript.getVersion().compareTo(new Version(configVersion)) != 0) {
				try {
					final InputStream in = Skript.getInstance().getResource("config.sk");
					if (in == null) {
						Skript.error("Your config is outdated, but Skript couldn't find the newest config in its jar.");
						return false;
					}
					final Config newConfig = new Config(in, "Skript.jar/config.sk", false, false, ":");
					in.close();

					boolean forceUpdate = false;

					if (mc.getMainNode().get("database") != null) { // old database layout
						forceUpdate = true;
						try {
							final SectionNode oldDB = (SectionNode) mc.getMainNode().get("database");
							assert oldDB != null;
							final SectionNode newDBs = (SectionNode) newConfig.getMainNode().get(Options.DATABASES.optionSection.key);
							assert newDBs != null;
							final SectionNode newDB = (SectionNode) newDBs.get("database 1");
							assert newDB != null;

							newDB.setValues(oldDB);

							// '.db' was dynamically added before
							final String file = newDB.getValue("file");
							assert file != null;
							if (!file.endsWith(".db"))
								newDB.set("file", file + ".db");

							final SectionNode def = (SectionNode) newDBs.get("default");
							assert def != null;
							def.set("backup interval", "" + mc.get("variables backup interval"));
						} catch (final Exception e) {
							Skript.error("An error occurred while trying to update the config's database section.");
							Skript.error("You'll have to update the config yourself:");
							Skript.error("Open the new config.sk as well as the created backup, and move the 'database' section from the backup to the start of the 'databases' section");
							Skript.error("of the new config (i.e. the line 'databases:' should be directly above 'database:'), and add a tab in front of every line that you just copied.");
							return false;
						}
					}

					if (newConfig.setValues(mc, Options.VERSION.option.key, Options.DATABASES.optionSection.key) || forceUpdate) { // new config is different
						final File bu = FileUtils.backup(configFile);
						newConfig.getMainNode().set(Options.VERSION.option.key, Skript.getVersion().toString());
						if (mc.getMainNode().get(Options.DATABASES.optionSection.key) != null)
							newConfig.getMainNode().set(Options.DATABASES.optionSection.key, mc.getMainNode().get(Options.DATABASES.optionSection.key));
						mc = mainConfig = newConfig;
						mc.save(configFile);
						Skript.info("Your configuration has been updated to the latest version. A backup of your old config file has been created as " + bu.getName());
					} else { // only the version changed
						mc.getMainNode().set(Options.VERSION.option.key, Skript.getVersion().toString());
						mc.save(configFile);
					}
				} catch (final IOException e) {
					Skript.error("Could not load the new config from the jar file: " + e.getLocalizedMessage());
				}
			}

			mc.load(SkriptConfig.class);

//			if (!keepConfigsLoaded.value())
//				mainConfig = null;
		} catch (final RuntimeException e) {
			Skript.exception(e, "An error occurred while loading the config");
			return false;
		}
		return true;
	}

}
