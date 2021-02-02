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
    private Map<String, String> buttons = new HashMap<>();

    protected HtmlParser() {
        // Visibility
    }

    public HtmlFile load(String fileName) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder sbText = new StringBuilder();

        HtmlFile htmlFile = new HtmlFile();
        htmlFile.setFileName(fileName);

        LOGGER.info("Loading HTML file: " + fileName);
        Files.lines(Paths.get(fileName), StandardCharsets.UTF_8).forEach(stringBuilder::append);

        // Remove extra characters
        int count = StringUtils.countMatches(stringBuilder, "%%");
        for (int i = 0; i < count; i++)
        {
            stringBuilder.replace(stringBuilder.indexOf("%%"), stringBuilder.indexOf("%%") + 2, "") ;
        }

        // Change NPC name
        String npcName = stringBuilder.substring(stringBuilder.indexOf("!") + 1, stringBuilder.indexOf(":"));
        //stringBuilder.replace(stringBuilder.indexOf("!"), stringBuilder.indexOf(":") + 1, "<font color=\"36DC25\">" + npcName + ":</font><br>" + System.lineSeparator()) ;
        sbText.append("<font color=\"36DC25\">" + npcName + ":</font><br>" + System.lineSeparator());
        cutString(stringBuilder, stringBuilder.indexOf("!"), stringBuilder.indexOf(":") + 2);

        // Get all bypass and button's names
        int tag = StringUtils.countMatches(stringBuilder, "[");
        for (int i = 0; i < tag; i++)
        {
            StringBuilder sbLinks = new StringBuilder();
            sbLinks.append(stringBuilder.substring(stringBuilder.indexOf("["), stringBuilder.indexOf("]") + 1).trim());
            String bypass = sbLinks.substring(sbLinks.indexOf("[") + 1, sbLinks.indexOf("|"));
            String buttonName = sbLinks.substring(sbLinks.indexOf("|") + 1, sbLinks.indexOf("]") - 1);
            buttons.put(buttonName, bypass);
            cutString(stringBuilder, stringBuilder.indexOf("["), stringBuilder.indexOf("]") + 1);
        }

        // Get all text
        int dot = StringUtils.countMatches(stringBuilder, ".");
        for (int i = 0; i < dot; i++) {
            sbText.append(getStringBeforeDot(stringBuilder.toString().trim())).append("<br1>" + System.lineSeparator());
            cutString(stringBuilder, 0, stringBuilder.indexOf(".") + 1);
        }

        if (!buttons.isEmpty()) {
            sbText.append("<table border=0 cellspacing=0 cellpadding=0 width=290 align=\"center\">").append(System.lineSeparator());
            buttons.forEach((key, value) -> {
                sbText.append("<tr>").append(System.lineSeparator());
                sbText.append("    <td FIXWIDTH=90 align=center>").append(System.lineSeparator());
                sbText.append("          <button value=\"").append(key).append("\" ");
                sbText.append("action=\"bypass -h ").append(value).append("\" back=\"l2ui_ct1.button.button_df_small_down\" fore=\"l2ui_ct1.button.button_df_small\" width=\"280\" height=\"25\">").append(System.lineSeparator());
                sbText.append("    </td>").append(System.lineSeparator());
                sbText.append("</tr>").append(System.lineSeparator());
            });
            sbText.append("</table>");
        }

        htmlFile.setFileText(sbText);
        return htmlFile;
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

    public void cutString(StringBuilder sb, int startIndex, int stopIndex) {
        sb.delete(startIndex, stopIndex);
    }

    public static HtmlParser getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        protected static final HtmlParser INSTANCE = new HtmlParser();
    }
}
