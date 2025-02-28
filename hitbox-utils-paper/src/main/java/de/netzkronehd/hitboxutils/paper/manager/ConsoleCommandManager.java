package de.netzkronehd.hitboxutils.paper.manager;

import de.netzkronehd.hitboxutils.paper.HitBoxUtils;
import de.netzkronehd.hitboxutils.paper.consolecommand.ConsoleCommand;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.logging.Level;

@Getter
public class ConsoleCommandManager extends Manager {

    private final Set<LocalTime> times;
    private final Map<String, Set<LocalTime>> timerTime;
    private final List<ConsoleCommand> commands;
    private final DateTimeFormatter dateFormat;
    private final Random random;

    private boolean enabled;
    private BukkitTask checkTask;

    public ConsoleCommandManager(HitBoxUtils hitBox) {
        super(hitBox);
        this.times = new HashSet<>();
        this.timerTime = new HashMap<>();
        this.commands = new ArrayList<>();
        this.dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        this.random = new Random();
    }

    @Override
    public void onLoad() {
        createFile();
        readFile();
        if(enabled) {
            startCheckScheduler();
        }
    }

    @Override
    public void onReload() {
        if(checkTask != null) checkTask.cancel();
        super.onReload();
    }

    @Override
    public void createFile() {
        if (!file.exists()) {
            cfg.set("enabled", false);
            cfg.set("1.12:00:00", List.of("test command", "test command1"));
            cfg.set("1.00:00:00", List.of("test command2", "test command3"));
            cfg.set("2.13:00:00", List.of("test command4", "test command5"));
            cfg.set("2.03:00:00", List.of("test command6", "test command7"));
            save();
        }
    }

    @Override
    public void readFile() {
        enabled = cfg.getBoolean("enabled", false);
        commands.clear();
        times.clear();
        timerTime.values().forEach(Set::clear);
        timerTime.clear();

        for (String timerId : cfg.getKeys(false)) {
            if(timerId.equals("enabled")) continue;

            final HashSet<LocalTime> set = new HashSet<>();
            timerTime.put(timerId, set);
            final ConfigurationSection section = cfg.getConfigurationSection(timerId);
            if (section == null) continue;
            section.getKeys(false).forEach(time -> {
                try {
                    final ConsoleCommand consoleCommand = getConsoleCommand(timerId, time);
                    commands.add(consoleCommand);
                    times.add(consoleCommand.getTime());
                    set.add(consoleCommand.getTime());
                } catch (DateTimeParseException e) {
                    log(Level.WARNING, "Cloud not parse date format '" + time + "'.");
                }
            });
        }

        log("Commands: "+this.commands);
        log("Trigger-Times: "+this.timerTime);
    }

    private ConsoleCommand getConsoleCommand(String timerId, String time) {
        final TemporalAccessor parse = dateFormat.parse(time);
        final LocalTime localTime = LocalTime.of(parse.get(ChronoField.HOUR_OF_DAY), parse.get(ChronoField.MINUTE_OF_HOUR), parse.get(ChronoField.SECOND_OF_MINUTE));
        return new ConsoleCommand(timerId, localTime, cfg.getStringList(timerId+"."+time));
    }



    public void startCheckScheduler() {
        checkTask = hitBox.getServer().getScheduler().runTaskTimer(hitBox, () -> {

            final String formattedTime = LocalTime.now().format(dateFormat);
            if(!isCommandTime(formattedTime)) return;

            final List<ConsoleCommand> currentCommands = getCommands(formattedTime);
            if (currentCommands.isEmpty()) {
                log(Level.WARNING, "Can not execute command for the time '"+formattedTime+"' because no commands has been found.");
                return;
            }

            for (ConsoleCommand currentCommand : currentCommands) {
                currentCommand.getRandomCommand().ifPresentOrElse(command -> {
                    log("Executing command '"+command+"' for the time '"+formattedTime+"' from timerId '"+currentCommand.getTimerId()+"'.");
                    hitBox.getServer().dispatchCommand(hitBox.getServer().getConsoleSender(), command);
                }, () -> log(Level.WARNING, "Can not execute command for the time '"+formattedTime+"' and timerId '"+currentCommand.getTimerId()+"' because no commands has been found."));
            }

        }, 0, 20);
    }

    private boolean isCommandTime(String time) {
        for (LocalTime localTime : times) {
            if (localTime.format(dateFormat).equals(time)) {
                return true;
            }
        }
        return false;
    }

    public List<ConsoleCommand> getCommands(String time) {
        return commands.stream()
                .filter(consoleCommand -> consoleCommand.getTime().format(dateFormat).equalsIgnoreCase(time))
                .toList();
    }

    public Optional<Duration> getTimeUntilNextCommandTime() {
        final LocalTime timeNow = LocalTime.now();
        final LocalDate dateNow = LocalDate.now();
        final LocalDateTime dateTimeNow = LocalDateTime.of(dateNow, timeNow);

        final List<LocalDateTime> times = this.times.stream().map(localTime -> {
            if(timeNow.isBefore(localTime)) {
                return LocalDateTime.of(dateNow, localTime);
            } else {
                return LocalDateTime.of(dateNow.plusDays(1), localTime);
            }
        }).toList();

        return times.stream()
                .min(LocalDateTime::compareTo)
                .map(localDateTime -> Duration.between(dateTimeNow, localDateTime));
    }

    public Optional<Duration> getTimeUntilNextCommandTime(String timerId) {
        final LocalTime timeNow = LocalTime.now();
        final LocalDate dateNow = LocalDate.now();
        final LocalDateTime dateTimeNow = LocalDateTime.of(dateNow, timeNow);

        final Set<LocalTime> localTimes = this.timerTime.get(timerId);
        if(localTimes == null) return Optional.empty();

        final List<LocalDateTime> times = localTimes.stream().map(localTime -> {
            if(timeNow.isBefore(localTime)) {
                return LocalDateTime.of(dateNow, localTime);
            } else {
                return LocalDateTime.of(dateNow.plusDays(1), localTime);
            }
        }).toList();

        return times.stream()
                .min(LocalDateTime::compareTo)
                .map(localDateTime -> Duration.between(dateTimeNow, localDateTime));
    }

}
