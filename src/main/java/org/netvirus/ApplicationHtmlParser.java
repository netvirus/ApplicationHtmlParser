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

            List<String> files = walk.map(x -> x.toString()).filter(f -> f.endsWith(".htm")).collect(Collectors.toList());

            files.forEach(fileName -> {
                StringBuilder fileText = new StringBuilder();
                HtmlParser htmlParser = new HtmlParser();

                try {
                    fileText = htmlParser.load(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                fileName = fileName.replace("IN", "OUT");
                Path path = Paths.get(fileName);
                String dir =  path.getParent().toString();
                File newDir = new File(dir);
                if (!Files.exists(path)) {
                    newDir.mkdir();
                    LOGGER.info("Created directory: " + dir);
                }

                try {
                    File newFile = new File(fileName);
                    FileWriter fileWriter = new FileWriter(newFile, false);
                    fileWriter.write(fileText.toString());
                    fileWriter.close();
                    LOGGER.info("Writed a file: " + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
