package com.liteflags.config;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Config extends AbstractConfig {

    static Config config;
    static int version;
    public Config() {
        super(new File(System.getProperty("user.home") + File.separator + "share" + File.separator + "configs" + File.separator + "LiteFlags"), "config.yml");
    }

    public static void reload() {
        config = new Config();

        version = config.getInt("config-version", 1);
        config.set("config-version", 1);

        config.readConfig(Config.class, null);
    }

    public static String ALERT_ACTIVE_FLAGS = "<gray><hover:show_text:\"Click to view <player>'s flags\"><click:run_command:\"/flag list <player>\">" +
            "<player> has <red><total_act_flags></red> active flags!</click></hover> " +
            "(<red><console_flags></red> Console, <red><staff_flags></red> Staff)</gray>";
    public static String PLAYER_FLAGS_HEADER = "<yellow><player> has <red><flag_amount></red> flags: </yellow>";
    public static List<String> PLAYER_FLAG_LIST_SHORT = List.of("<yellow>- <active> <flag> <gray><flag_time> ago</gray></yellow>");
    public static List<String> PLAYER_FLAG_LIST_LONG = List.of(
            "<nl>",
            "<gray><st>----</st> <flag_time> ago <st>----</st></gray>",
            "<dark_gray><player> was <yellow>flagged</yellow> for <flag_length> by <yellow><staff></yellow></dark_gray>",
            "<dark_gray>Reason: <gold><reason> <active></gold></dark_gray>");
    public static String REMOVE_BUTTON = "<white>[<red>✖</red>]</white>";
    public static String ACTIVE_FLAGS = "<white>[<green>Active</green>]</white>";
    public static String EXPIRED_FLAGS = "<white>[<red>Expired</red>]</white>";
    public static String NO_PERMISSION = "<red>You do not have permission to do that.</red>";
    public static String CONFIG_RELOADED = "<green>LiteFlags config reloaded successfully!</green>";
    public static String ACTIVE_FLAGS_LIMIT = "<yellow><player> <gray>has reached the active flags limit.</gray></yellow>";
    public static String FLAGGED_PLAYER = "<gold>* <staff> has flagged <player> for (<flag_length>)\nReason: <reason></gold>";
    public static String AUTHENTICATE = "<gray>You need to authenticate! Type <gold><code></gold> in chat to authenticate. (CaSe SeNsiTiVe)</gray>";
    public static String AUTHENTICATE_FAILED = "<gray>You need to authenticate! Type <gold><code></gold> in chat to authenticate. (CaSe SeNsiTiVe)</gray>";
    public static String AUTHENTICATE_SUCCESS = "<gray>You have authed! You can now play!</gray>";
    public static String NO_ACTIVE_FLAGS = "<gray>No players have active flags.";
    public static String UNKNOWN_PLAYER = "<red>No player called <player> found.";
    public static String FLAG_REMOVED = "<gray>You have successfully removed the flag <flag_reason> from <yellow><target></yellow>'s flag history";
    public static String NO_FLAGS_FOUND = "<red>No flags found for <target> </red>";
    public static String NO_FLAG_FOUND = "<red>No flag found for <target> with id: <id></red>";
    public static String INVALID_TIME_ARGUMENT = "<red>Invalid time argument <arg>! Example: 1d</red>";
    public static String FLAG_CONFIRM = "<white>Are you sure you want to remove the flag <flag_reason> from " +
            "<yellow><target_name></yellow>'s flag history? " +
            "<hover:show_text:\"Click to confirm\"><click:run_command:\"/flag remove <id> <target_name> -c\">[<green>Confirm</green>]</click></hover></white>";
    private static void loadMessages() {
        ALERT_ACTIVE_FLAGS = config.getString("messages.alert-active-flags", ALERT_ACTIVE_FLAGS);
        PLAYER_FLAGS_HEADER = config.getString("messages.player-flags-header", PLAYER_FLAGS_HEADER);
        PLAYER_FLAG_LIST_SHORT = config.getList("messages.player-flag-list-short", PLAYER_FLAG_LIST_SHORT).stream()
                .filter(object -> object instanceof String) //TODO check if this is needed
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());;
        PLAYER_FLAG_LIST_LONG = config.getList("messages.player-flag-list-long", PLAYER_FLAG_LIST_LONG).stream()
                .filter(object -> object instanceof String) //TODO check if this is needed
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
        REMOVE_BUTTON = config.getString("messages.remove-button", REMOVE_BUTTON);
        ACTIVE_FLAGS = config.getString("messages.active-flags", ACTIVE_FLAGS);
        EXPIRED_FLAGS = config.getString("messages.expired-flags", EXPIRED_FLAGS);
        NO_PERMISSION = config.getString("messages.no-permission", NO_PERMISSION);
        CONFIG_RELOADED = config.getString("messages.config-reloaded", CONFIG_RELOADED);
        ACTIVE_FLAGS_LIMIT = config.getString("messages.active-flags-limit", ACTIVE_FLAGS_LIMIT);
        FLAGGED_PLAYER = config.getString("messages.flagged-player", FLAGGED_PLAYER);
        AUTHENTICATE = config.getString("messages.authenticate", AUTHENTICATE);
        AUTHENTICATE_FAILED = config.getString("messages.authenticate-failed", AUTHENTICATE_FAILED);
        AUTHENTICATE_SUCCESS = config.getString("messages.authenticate-success", AUTHENTICATE_SUCCESS);
        NO_ACTIVE_FLAGS = config.getString("messages.no-active-flags", NO_ACTIVE_FLAGS);
        UNKNOWN_PLAYER = config.getString("messages.unknown-player", UNKNOWN_PLAYER);
        FLAG_REMOVED = config.getString("messages.flag-removed", FLAG_REMOVED);
        NO_FLAGS_FOUND = config.getString("messages.no-flags-found", NO_FLAGS_FOUND);
        NO_FLAG_FOUND = config.getString("messages.no-flag-found", NO_FLAG_FOUND);
        INVALID_TIME_ARGUMENT = config.getString("messages.invalid-time-argument", INVALID_TIME_ARGUMENT);
        FLAG_CONFIRM = config.getString("messages.flag-confirm", FLAG_CONFIRM);
    }

    public static String HELP_MESSAGE_WRAPPER = "<gold>LiteFlags help:\n<commands></gold>";
    public static String HELP_MESSAGE = "<green><gold>/flag help</gold>: Show this menu</green>";
    public static String HELP_FLAG_LIST = "<green><gold>/flag list</gold>: Show all the players with active flags</green>";
    public static String HELP_FLAG_LIST_PLAYER = "<green><gold>/flag list <player> [full]</gold>: Show the flags a player has</green>";
    public static String HELP_FLAG_PLAYER = "<green><gold>/flag <player> <time> <reason></gold>: Flag a player for " +
            "a given reason <i>Ex: /flag BobTheBuilder 7d Possible hacked client.</i></green>";
    public static String HELP_FLAG_REMOVE = "<green><gold>/flag remove <player> <id>></gold>: Remove a specific flag from a player " +
            "(easier to use from /flag list <player>)";
    public static String HELP_RELOAD = "<green><gold>/flag reload</gold>: Reload the config for LiteFlags</green>";
    private static void loadHelp() {
        HELP_MESSAGE_WRAPPER = config.getString("help.help-wrapper", HELP_MESSAGE_WRAPPER);
        HELP_MESSAGE = config.getString("help.help", HELP_MESSAGE);
        HELP_FLAG_LIST = config.getString("help.flag-list", HELP_FLAG_LIST);
        HELP_FLAG_LIST_PLAYER = config.getString("help.flag-list-player", HELP_FLAG_LIST_PLAYER);
        HELP_FLAG_PLAYER = config.getString("help.flag-player", HELP_FLAG_PLAYER);
    }

    public static String IP = "localhost";
    public static String PORT = "3306";
    public static String DATABASE = "liteflags";
    public static String USERNAME = "root";
    public static String PASSWORD = "root";
    public static String DRIVERS = "mysql";
    private static void loadDatabaseSettings() {
        IP = config.getString("database.ip", IP);
        PORT = config.getString("database.port", PORT);
        DATABASE = config.getString("database.database", DATABASE);
        USERNAME = config.getString("database.username", USERNAME);
        PASSWORD = config.getString("database.password", PASSWORD);
        DRIVERS = config.getString("database.drivers", DRIVERS);
    }

    public static int MAX_ACTIVE_FLAGS = 5;
    public static int MAX_FLAGS_LISTED = 10;
    public static int MIN_RANDOM_RE_AUTH = 14;
    public static int MAX_RANDOM_RE_AUTH = 30;
    public static int AUTH_MESSAGE_REPEAT_TIMER = 10;
    public static String TIME_FORMAT = "Days";
    public static String AUTH_MESSAGE_COMMAND = "cmi titlemessage <player> -keep:80 &7You need to authenticate! <nl>&7Type &6<code> &7in chat to authenticate. (CaSe SeNsiTiVe)";
    public static String AUTH_SUCCESS_COMMAND = "lp user <player> permission settemp <permission> true <expire_time>";
    private static void loadSettings() {
        MAX_ACTIVE_FLAGS = config.getInt("settings.max-active-flags", MAX_ACTIVE_FLAGS);
        MAX_FLAGS_LISTED = config.getInt("settings.max-flags-listed", MAX_FLAGS_LISTED);
        MIN_RANDOM_RE_AUTH = config.getInt("settings.min-random-re-auth", MIN_RANDOM_RE_AUTH);
        MAX_RANDOM_RE_AUTH = config.getInt("settings.max-random-re-auth", MAX_RANDOM_RE_AUTH);
        AUTH_MESSAGE_REPEAT_TIMER = config.getInt("settings.auth-message-repeat-timer", AUTH_MESSAGE_REPEAT_TIMER);
        TIME_FORMAT = config.getString("settings.time-format", TIME_FORMAT);
        AUTH_MESSAGE_COMMAND = config.getString("settings.auth-message-command", AUTH_MESSAGE_COMMAND);
        AUTH_SUCCESS_COMMAND = config.getString("settings.auth-success-command", AUTH_SUCCESS_COMMAND);
    }

}
