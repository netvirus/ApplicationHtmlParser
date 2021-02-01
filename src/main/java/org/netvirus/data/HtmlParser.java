package org.netvirus.data;

import org.apache.commons.lang3.StringUtils;
import org.netvirus.model.HtmlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HtmlParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlParser.class);
    private boolean brocketLink = false;

    protected HtmlParser() {
        // Visibility
    }

    public HtmlFile load(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder("");
        HtmlFile htmlFile = new HtmlFile();
        htmlFile.setFileName(fileName);

        LOGGER.info("Loading HTML file: " + fileName);
        Files.lines(Paths.get(fileName), StandardCharsets.UTF_8).forEach(line -> {
            if (!line.equals("")) {
                if (line.startsWith("!") && line.contains(":")) {
                    String npcName = getNpcName(line);
                    stringBuilder.append("<font color=\"36DC25\">").append(npcName).append(":").append("</font><br>").append(System.lineSeparator());
                    String afterNpcName = getStringAfterNpcName(line, npcName.length());
                    if (afterNpcName != null) {
                        stringBuilder.append(getStringWithMultipleLines(afterNpcName));
                    }
                    htmlFile.setHasNpcName(true);
                } else if (line.contains("[")) {
                    stringBuilder.append(getStringUrlWithMultipleLines(line));
                    if (!htmlFile.isHasUrlLinks()) {
                        htmlFile.setHasUrlLinks(true);
                        stringBuilder.append("<table border=0 cellspacing=0 cellpadding=0 width=290 align=\"center\">").append(System.lineSeparator());
                        stringBuilder.append("<tr>").append(System.lineSeparator());
                        stringBuilder.append("    <td FIXWIDTH=90 align=center>").append(System.lineSeparator());
                    }
                } else {
                    stringBuilder.append(getStringWithMultipleLines(line));
                }
            }
        });
        htmlFile.setFileText(stringBuilder);
        return htmlFile;
    }

    public String getNpcName(String line) {
        String result = null;
        try {
            result = line.substring(line.indexOf("!") + 1, line.indexOf(":"));
        } catch (Exception e) {
            result = line;
        }
        return result;
    }

    public String getStringAfterNpcName(String line, int startIndex) {
        String result = null;
        try {
            result = line.substring(startIndex + 2);
        } catch (Exception e) {
            result = line;
        }
        return result;
    }

    public String getStringWithMultipleLines(String line) {
        String result;
        StringBuilder sb = new StringBuilder("");
        try {
            result = removeExtraCharacters(line);
            if (result != null) {
                int count = StringUtils.countMatches(result, ".");
                if (count > 0) {
                    int words = count + 1;
                    for (int i = 0; i < words; i++) {
                        String beforeDot = StringUtils.substringBefore(result, ".");

                        if (beforeDot.equals(""))
                            break;

                        if (i < count) {
                            sb.append(beforeDot).append(".<br1>").append(System.lineSeparator());
                            int startIndex = result.indexOf(beforeDot);
                            int stopIndex = startIndex + beforeDot.length() + 2;
                            StringBuilder builderResult = new StringBuilder(result);
                            result = builderResult.delete(startIndex, stopIndex).toString();
                        } else {
                            if (!beforeDot.equals("")) {
                                sb.append(beforeDot).append(" ");
                            }
                        }
                    }
                } else {
                    sb.append(result).append(" ");
                }
            }
            result = sb.toString();
        } catch (
                Exception e) {
            result = line;
        }
        return result;
    }

    public String getStringUrlWithMultipleLines(String line) {
        StringBuilder sb = new StringBuilder("");
        int countOpenTag = StringUtils.countMatches(line, "[");
        int countCloseTag = StringUtils.countMatches(line, "]");
        for (int i = 0; i < countOpenTag; i++) {
            if (line.startsWith("[") && !line.endsWith("]")) {
                brocketLink = true;
                String link = line.substring(line.indexOf("[") + 1);


            } else if (line.startsWith("[") && line.endsWith("]")) {
                String link = line.substring(line.indexOf("[") + 1, line.indexOf("|"));
                String btnName = line.substring(line.indexOf("|") + 2, line.indexOf("]") - 1);
                sb.append("<button value=\"").append(btnName).append("\" ");
                sb.append("action=\"bypass -h ").append(link).append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"280\" height=\"25\" />");
            } else {
                sb.append(StringUtils.substringBefore(line, "[").trim()).append("<br>").append(System.lineSeparator());

            }
        }
        return sb.toString();
    }

//    public String getButton(String line) {
//
//    }

    public String removeExtraCharacters(String line) {
        return (line != null) ? line.replace("%%", "").trim() : null;
    }

    public static HtmlParser getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        protected static final HtmlParser INSTANCE = new HtmlParser();
    }
}
