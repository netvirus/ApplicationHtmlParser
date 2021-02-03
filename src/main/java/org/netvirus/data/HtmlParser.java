package org.netvirus.data;

import org.apache.commons.lang3.StringUtils;
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
    private Map<String, String> buttons = new HashMap<>();

    public HtmlParser() {
    }

    public StringBuilder load(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder sbText = new StringBuilder();

        LOGGER.info("Loading HTML file: " + fileName);
        Files.lines(Paths.get(fileName), StandardCharsets.UTF_8).forEach(stringBuilder::append);

        // Remove extra characters
        int countPercents = StringUtils.countMatches(stringBuilder, "%%");
        if (countPercents > 0) {
            for (int i = 0; i < countPercents; i++) {
                stringBuilder.replace(stringBuilder.indexOf("%%"), stringBuilder.indexOf("%%") + 2, "");
            }
        }

        int count4Dots = StringUtils.countMatches(stringBuilder, "....");
        if (count4Dots > 0) {
            for (int i = 0; i < count4Dots; i++) {
                stringBuilder.replace(stringBuilder.indexOf("...."), stringBuilder.indexOf("....") + 4, ".");
            }
        }

        int countDotsAsk = StringUtils.countMatches(stringBuilder, "...?");
        if (countDotsAsk > 0) {
            for (int i = 0; i < countDotsAsk; i++) {
                stringBuilder.replace(stringBuilder.indexOf("...?"), stringBuilder.indexOf("...?") + 4, "?");
            }
        }

        int countDots = StringUtils.countMatches(stringBuilder, "...");
        if (countDots > 0) {
            for (int i = 0; i < countDots; i++) {
                stringBuilder.replace(stringBuilder.indexOf("..."), stringBuilder.indexOf("...") + 3, ".");
            }
        }

        int countDot = StringUtils.countMatches(stringBuilder, ".)");
        if (countDot > 0) {
            for (int i = 0; i < countDot; i++) {
                stringBuilder.replace(stringBuilder.indexOf(".)"), stringBuilder.indexOf(".)") + 2, ")");
            }
        }

        // Change NPC name
        String npcName = stringBuilder.substring(0, stringBuilder.indexOf(":"));
        if (npcName.length() > 0) {
            int tmp = StringUtils.countMatches(npcName, "!");
            if (tmp > 0) {
                stringBuilder.replace(stringBuilder.indexOf("!"), stringBuilder.indexOf("!") + 1, "");
            }
            sbText.append("<font color=\"36DC25\">" + npcName + ":</font><br>" + System.lineSeparator());
            stringBuilder.delete(0, stringBuilder.indexOf(":") + 2);
        }

        // Get all bypass and button's names
        int tag = StringUtils.countMatches(stringBuilder, "[");
        if (tag > 0) {
            for (int i = 0; i < tag; i++) {
                StringBuilder sbLinks = new StringBuilder();
                sbLinks.append(stringBuilder.substring(stringBuilder.indexOf("["), stringBuilder.indexOf("]") + 1).trim());
                String bypass = sbLinks.substring(sbLinks.indexOf("[") + 1, sbLinks.indexOf("|"));
                String buttonName = sbLinks.substring(sbLinks.indexOf("|") + 1, sbLinks.indexOf("]") - 1);

                // Search "." and cut
                int count1 = StringUtils.countMatches(buttonName, ".");
                if (count1 > 0) {
                    for (int t = 0; t < count1; t++) {
                        buttonName = buttonName.replace(".", "");
                    }
                }

                // Search "." and cut
                int count2 = StringUtils.countMatches(buttonName, "\"");
                if (count2 > 0) {
                    for (int t = 0; t < count2; t++) {
                        buttonName = buttonName.replace("\"", "");
                    }
                }

                int countQot = StringUtils.countMatches(stringBuilder, "&quot;");
                if (countQot > 0) {
                    for (int q = 0; q < countQot; q++) {
                        buttonName = buttonName.replace("&quot;", "");
                    }
                }

                buttons.put(buttonName, bypass);
                stringBuilder.delete(stringBuilder.indexOf("["), stringBuilder.indexOf("]") + 1);
            }
        }

        // Get all text
        int dot = StringUtils.countMatches(stringBuilder, ".");
        if (dot > 0) {
            for (int i = 0; i < dot; i++) {
                sbText.append(getStringBeforeDot(stringBuilder.toString().trim())).append("<br1>" + System.lineSeparator());
                stringBuilder.delete(0, stringBuilder.indexOf(".") + 1);
            }
        } else if (stringBuilder.length() > 0) {
            sbText.append(stringBuilder.toString().trim()).append("<br1>").append(System.lineSeparator());
        }

        stringBuilder.setLength(0);

        if (!buttons.isEmpty()) {
            sbText.append("<table border=0 cellspacing=0 cellpadding=0 width=290 align=\"center\">").append(System.lineSeparator());
            buttons.forEach((key, value) -> {
                sbText.append("      <tr>").append(System.lineSeparator());
                sbText.append("          <td FIXWIDTH=90 align=center>").append(System.lineSeparator());
                sbText.append("          <button value=\"").append(key).append("\" ");
                sbText.append("action=\"bypass -h ").append(value).append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"280\" height=\"25\">").append(System.lineSeparator());
                sbText.append("          </td>").append(System.lineSeparator());
                sbText.append("      </tr>").append(System.lineSeparator());
            });
            sbText.append("</table>");
        }
        return sbText;
    }

    public String getStringBeforeDot(String line) {
        String result;
        try {
            result = line.substring(0, line.indexOf(".") + 1);
        } catch (Exception e) {
            result = line;
        }
        return result;
    }
}
