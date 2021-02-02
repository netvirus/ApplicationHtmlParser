package org.netvirus;

import org.netvirus.data.HtmlParser;
import org.netvirus.model.HtmlFile;

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

    public static void main(String[] args) throws IOException {

        HtmlParser htmlParser = HtmlParser.getInstance();
//        File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
//        System.out.println(jarDir.getAbsolutePath());

        Path start = Paths.get("C:\\b\\");
        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            List<String> collect = stream
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.toList());

            collect.forEach(fileName -> {
                if (fileName.contains(".htm")) {
                    HtmlFile htmlFile = new HtmlFile();
                    try {
                        htmlFile = htmlParser.load(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        File newFile = new File(fileName);
                        FileWriter fileWriter = new FileWriter(newFile, false);
                        fileWriter.write(htmlFile.getFileText().toString());
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
