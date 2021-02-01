package org.netvirus;

import org.netvirus.data.HtmlParser;
import org.netvirus.model.HtmlFile;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.stream.Stream;

public class ApplicationHtmlParser {

    public static void main(String[] args) throws IOException {

        HtmlParser htmlParser = HtmlParser.getInstance();
        HtmlFile htmlFile = htmlParser.load("test.htm");
        System.out.println(htmlFile.getFileText());
    }
}
