package de.netzkronehd.hitboxutils.paper.consolecommand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@AllArgsConstructor
@Builder
@Data
public class ConsoleCommand {

    private static final Random RANDOM = new Random();

    private final String timerId;
    private LocalTime time;
    private List<String> commands;

    public Optional<String> getRandomCommand() {
        if(commands.isEmpty()) return Optional.empty();
        return Optional.ofNullable(commands.get(RANDOM.nextInt(commands.size())));
    }

}
