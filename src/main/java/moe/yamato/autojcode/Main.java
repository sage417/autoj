package moe.yamato.autojcode;

import moe.yamato.autojcode.generator.DomainGenerator;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {

    private static final String PACKAGE_NAME = "packageName";
    private static final String TABLE_NAME_PREFIX = "prefix";

    private static Options buildOptions() {
        Options options = new Options();
        options.addOption(Option.builder(PACKAGE_NAME).hasArg().desc("class packageName").type(String.class).build());
        options.addOption(Option.builder(TABLE_NAME_PREFIX).hasArg().desc("tableNamePrefix").type(String.class).build());
        options.addOption(Option.builder("i").hasArg().required().argName("input file path").desc("input path").type(String.class).build());
        options.addOption(Option.builder("o").hasArg().argName("output file path").desc("output path").type(String.class).build());
        return options;
    }

    public static void main(String[] args) throws Exception {

        final Options options = buildOptions();

        final CommandLineParser parser = new DefaultParser();

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (final ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar atuojcode.jar", "", options, "", true);
            System.exit(0);
            return;
        }

        final String tableNamePrefix = cmd.getOptionValue(TABLE_NAME_PREFIX, "");
        final String packageName = cmd.getOptionValue(PACKAGE_NAME, "package");
        final String input = cmd.getOptionValue("i");
        final String output = cmd.getOptionValue("o", ".");

        final Path inputPath = Paths.get(input);
        String sqls;

        try (final BufferedReader bufferedReader = Files.newBufferedReader(inputPath, Charset.forName("utf-8"))) {
            sqls = bufferedReader.lines().collect(Collectors.joining());
        } catch (RuntimeException re) {
            System.out.println(re.getMessage());
            System.exit(-1);
            return;
        }

        String[] ddls = sqls.split(";");

        for (String ddl : ddls) {
            DomainGenerator.generateDomain(ddl, tableNamePrefix, packageName, output);
        }
    }
}
