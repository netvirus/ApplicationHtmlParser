package org.netvirus.model;

public class HtmlFile {
    private String fileName;
    private boolean hasNpcName = false;
    private boolean hasUrlLinks = false;
    private StringBuilder fileText;

    public HtmlFile() { }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isHasNpcName() {
        return hasNpcName;
    }

    public void setHasNpcName(boolean hasNpcName) {
        this.hasNpcName = hasNpcName;
    }

    public boolean isHasUrlLinks() {
        return hasUrlLinks;
    }

    public void setHasUrlLinks(boolean hasUrlLinks) {
        this.hasUrlLinks = hasUrlLinks;
    }

    public StringBuilder getFileText() {
        return fileText;
    }

    public void setFileText(StringBuilder fileText) {
        this.fileText = fileText;
    }
}
