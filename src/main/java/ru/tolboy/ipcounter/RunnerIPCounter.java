package ru.tolboy.ipcounter;

import org.tinylog.Logger;
import ru.tolboy.ipcounter.container.BitSetContainer;
import ru.tolboy.ipcounter.container.IntContainer;
import ru.tolboy.ipcounter.converter.IPConverter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

/**
 * Runner for IP unique counter task
 * @param <T>
 */
public class RunnerIPCounter<T> implements Callable<Long> {
    private final String[] args;

    public RunnerIPCounter(String[] args) {
        this.args = args;
    }

    @Override
    public Long call() {
            Thread.currentThread().setName(this.getClass().getSimpleName() + "[Thread]");
            long result = 0L;
            System.out.println(Thread.currentThread().getName() + " started");
            if (args.length != 1) {
                System.out.println("Please specify a path to a file with IP addresses to process");
                Logger.error("No argument found");
                return -1L;
            }
            Path path = Path.of(args[0]);
            Instant startTime = Instant.now();
            try (Stream<String> ipAddresses = Files.lines(path, StandardCharsets.US_ASCII)) {
                result = ipAddresses
                        .mapToInt(new IPConverter())
                        .collect(BitSetContainer::new, IntContainer::add, IntContainer::addAll)
                        .countDistinct();
            } catch (IOException e) {
                System.out.println("Error during processing file: " + path + "\n" + e);
            }

            Duration executionTime = Duration.between(startTime, Instant.now());

            Logger.info("Time elapsed overall: {0} minutes", executionTime.toMinutes());
            System.out.println(Thread.currentThread().getName() + " finished");

            return result;

    }
}