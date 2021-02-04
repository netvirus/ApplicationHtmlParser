package org.netvirus;

import org.netvirus.data.HtmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationHtmlParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationHtmlParser.class);

    public static void main(String[] args) {

        try (Stream<Path> walk = Files.walk(Paths.get("C:\\IN\\"))) {
            List<Path> paths = walk.filter(Files::isRegularFile).collect(Collectors.toList());
            paths.forEach(f -> {
                String fullPath = f.toString().replace("IN", "OUT");
                String dir = f.getParent().toString().replace("IN", "OUT");
                HtmlParser htmlParser = new HtmlParser();
                File newDir = new File(dir);

                if (newDir.mkdir()) {
                    LOGGER.info("Created directory: " + dir);
                }
                try (FileWriter fileWriter = new FileWriter(fullPath, false)) {
                    fileWriter.write(htmlParser.load(f.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        catch (Exception e) {  }
    }
}
