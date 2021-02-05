package org.netvirus;

import org.netvirus.data.HtmlParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationHtmlParser {

    public static void main(String[] args) {
        AtomicInteger countDone = new AtomicInteger();
        try (Stream<Path> walk = Files.walk(Paths.get("C:\\IN\\"))) {
            List<Path> paths = walk.filter(Files::isRegularFile).collect(Collectors.toList());
            paths.forEach(f -> {
                String fullPath = f.toString().replace("IN", "OUT");
                String dir = f.getParent().toString().replace("IN", "OUT");
                HtmlParser htmlParser = new HtmlParser();
                File newDir = new File(dir);

                if (newDir.mkdir()) {
                    System.out.println("Created directory: " + dir);
                }
                try (FileWriter fileWriter = new FileWriter(fullPath, false)) {
                    fileWriter.write(htmlParser.load(f.toString()));
                    System.out.println("Writing a file: " + fullPath);
                    countDone.getAndIncrement();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        catch (Exception e) {  }
        System.out.println("Updated " + countDone.toString() + " files.");
    }
}
