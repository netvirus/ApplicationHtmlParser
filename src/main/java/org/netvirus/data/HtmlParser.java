package org.netvirus.data;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HtmlParser {

    private Map<String, String> buttons = new HashMap<>();

    public HtmlParser() {
    }

    public String load(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder sbText = new StringBuilder();
        StringBuilder fakeBypass = new StringBuilder();;

        Files.lines(Paths.get(fileName), StandardCharsets.UTF_8).forEach(stringBuilder::append);

        // Remove extra characters
        int countPercents = StringUtils.countMatches(stringBuilder, "%% %% ");
        if (countPercents > 0) {
            for (int i = 0; i < countPercents; i++) {
                stringBuilder.replace(stringBuilder.indexOf("%% %% "), stringBuilder.indexOf("%% %% ") + 6, "");
            }
        }

        int countPercent1 = StringUtils.countMatches(stringBuilder, "\"%%");
        if (countPercent1 > 0) {
            for (int i = 0; i < countPercent1; i++) {
                stringBuilder.replace(stringBuilder.indexOf("\"%%"), stringBuilder.indexOf("\"%%") + 3, "");
            }
        }

        int countPercent2 = StringUtils.countMatches(stringBuilder, "%%");
        if (countPercent2 > 0) {
            for (int i = 0; i < countPercent2; i++) {
                stringBuilder.replace(stringBuilder.indexOf("%%"), stringBuilder.indexOf("%%") + 2, "");
            }
        }

        int count5Dots = StringUtils.countMatches(stringBuilder, ".....");
        if (count5Dots > 0) {
            for (int i = 0; i < count5Dots; i++) {
                stringBuilder.replace(stringBuilder.indexOf("....."), stringBuilder.indexOf(".....") + 5, ".");
            }
        }

        int count4Dots = StringUtils.countMatches(stringBuilder, "....");
        if (count4Dots > 0) {
            for (int i = 0; i < count4Dots; i++) {
                stringBuilder.replace(stringBuilder.indexOf("...."), stringBuilder.indexOf("....") + 4, ".");
            }
        }

        int countManyDots = StringUtils.countMatches(stringBuilder, ", ... ");
        if (countManyDots > 0) {
            for (int i = 0; i < countManyDots; i++) {
                stringBuilder.replace(stringBuilder.indexOf(", ... "), stringBuilder.indexOf(", ... ") + 8, ", ");
            }
        }

        int countManyDots2 = StringUtils.countMatches(stringBuilder, "... ... ");
        if (countManyDots2 > 0) {
            for (int i = 0; i < countManyDots2; i++) {
                stringBuilder.replace(stringBuilder.indexOf("... ... "), stringBuilder.indexOf("... ... ") + 8, " ");
            }
        }

        int countDotsAsk = StringUtils.countMatches(stringBuilder, "...?");
        if (countDotsAsk > 0) {
            for (int i = 0; i < countDotsAsk; i++) {
                stringBuilder.replace(stringBuilder.indexOf("...?"), stringBuilder.indexOf("...?") + 4, "?");
            }
        }

        int countDotsPrc = StringUtils.countMatches(stringBuilder, "...%%");
        if (countDotsPrc > 0) {
            for (int i = 0; i < countDotsPrc; i++) {
                stringBuilder.replace(stringBuilder.indexOf("...%%"), stringBuilder.indexOf("...%%") + 5, ".");
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
        try {
            String npcName = stringBuilder.substring(0, stringBuilder.indexOf(":"));
            int tmp = StringUtils.countMatches(npcName, "!");
            if (tmp > 0) {
                npcName = npcName.replace("!", "");
            }
            sbText.append("<font color=\"36DC25\">" + npcName + ":</font><br>" + System.lineSeparator());
            stringBuilder.delete(0, stringBuilder.indexOf(":") + 2);
        } catch (Exception e) {
            //  Remove !" where no Npc name
            int ch1 = StringUtils.countMatches(stringBuilder, "!");
            if (ch1 > 0) {
                stringBuilder.replace(stringBuilder.indexOf("!"), stringBuilder.indexOf("!") + 1, "");
            }
            int ch2 = StringUtils.countMatches(stringBuilder, "\"");
            if (ch2 > 0) {
                stringBuilder.replace(stringBuilder.indexOf("\""), stringBuilder.indexOf("\"") + 1, "");
            }
        }

        // Get all bypass and button's names
        int tag = StringUtils.countMatches(stringBuilder, "[");
        if (tag > 0) {
            for (int i = 0; i < tag; i++) {
                StringBuilder sbLinks = new StringBuilder();
                sbLinks.append(stringBuilder.substring(stringBuilder.indexOf("["), stringBuilder.indexOf("]") + 1).trim());
                String bypass = "";
                String buttonName = "";
                if (StringUtils.countMatches(sbLinks, "|") > 0) {
                    bypass = sbLinks.substring(sbLinks.indexOf("[") + 1, sbLinks.indexOf("|")).trim();
                    buttonName = sbLinks.substring(sbLinks.indexOf("|") + 1, sbLinks.indexOf("]") - 1).trim();
                } else {
                    fakeBypass.append(sbLinks.toString()).append("<br1>").append(System.lineSeparator());
                }

                if (!buttonName.equals("")) {
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
                }
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

        if (fakeBypass.length() > 0) {
            sbText.append(fakeBypass);
        }

        stringBuilder.setLength(0);

        if (!buttons.isEmpty()) {
            sbText.append("<table border=0 cellspacing=0 cellpadding=0 width=290 align=\"center\">").append(System.lineSeparator());
            buttons.forEach((key, value) -> {
                sbText.append("      <tr>").append(System.lineSeparator());
                sbText.append("          <td FIXWIDTH=90 align=center>").append(System.lineSeparator());
                sbText.append("               <button value=\"").append(key).append("\" ");
                sbText.append("action=\"bypass -h ").append(value).append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"280\" height=\"25\">").append(System.lineSeparator());
                sbText.append("          </td>").append(System.lineSeparator());
                sbText.append("      </tr>").append(System.lineSeparator());
            });
            sbText.append("</table>");
        }
        return sbText.toString();
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
