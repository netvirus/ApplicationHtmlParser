package org.netvirus.data;

import org.apache.commons.lang3.StringUtils;
import org.netvirus.model.HtmlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HtmlParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlParser.class);
    private boolean isBrockenLink = false;
    private boolean isBrockenLinkWithBypass = false;
    private boolean isTextWithLink = false;

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
                } else if (line.contains("[") || line.contains("]") || line.contains("|")) {
                    if (!htmlFile.isHasUrlLinks()) {
                        htmlFile.setHasUrlLinks(true);
                        stringBuilder.append("<table border=0 cellspacing=0 cellpadding=0 width=290 align=\"center\">").append(System.lineSeparator());
                    }

                    Map<String, String> links = getStringUrlWithMultipleLines(line);
                    if (!isBrockenLink) {
                        links.forEach((k, v) -> {
                            stringBuilder.append("<tr>").append(System.lineSeparator());
                            stringBuilder.append("    <td FIXWIDTH=90 align=center>").append(System.lineSeparator());
                            stringBuilder.append("          <button value=\"").append(k.toString()).append("\" ");
                            stringBuilder.append("action=\"bypass -h ").append(v.toString()).append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"280\" height=\"25\" />");
                            stringBuilder.append("    </td>").append(System.lineSeparator());
                            stringBuilder.append("</tr>").append(System.lineSeparator());
                        });
                    } else {
                        stringBuilder.append(links.get("line"));
                    }
                } else {
                    stringBuilder.append(getStringWithMultipleLines(line));
                }
            }
        });

        if (htmlFile.isHasUrlLinks())
            stringBuilder.append("<table>");

        htmlFile.setFileText(stringBuilder);
        return htmlFile;
    }

    public String getNpcName(String line) {
        String result;
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

    public Map<String, String> getStringUrlWithMultipleLines(String line) {
        Map<String, String> link = new HashMap<>();

        int countOpenTag = StringUtils.countMatches(line, "[");

        for (int i = 0; i < countOpenTag; i++) {
            if (line.startsWith("[") && !line.endsWith("]") && !line.contains("]"))
            {// [npc_%objectId%_Quest
                isBrockenLink = true;
                if (!line.contains("|"))
                {// [npc_%objectId%_Quest
                    isBrockenLinkWithBypass = false;
                    link.put("bypass", line.substring(line.indexOf("[") + 1));
                }
                else
                {// [npc_%objectId%_Quest|Que
                    isBrockenLinkWithBypass = true;
                    link.put("bypass", line.substring(line.indexOf("[") + 1, line.indexOf("|")));
                    link.put("btnName", line.substring(line.indexOf("|") + 2));
                }

            } else if (line.startsWith("[") && line.endsWith("]"))
            {// [npc_%objectId%_Quest|Quest]
                String bypass = line.substring(line.indexOf("[") + 1, line.indexOf("|"));
                String btnName = line.substring(line.indexOf("|") + 2, line.indexOf("]") - 1);
                link.put("bypass", bypass);
                link.put("btnName", btnName);

            } else if (!line.startsWith("[") && line.contains("[") && !line.contains("]"))
            {// Some text. [npc_%objectId%_Chat 1|Ask
                isTextWithLink = true;
                link.put("text", StringUtils.substringBefore(line, "[").trim());
            }
        }
        return link;
    }

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
