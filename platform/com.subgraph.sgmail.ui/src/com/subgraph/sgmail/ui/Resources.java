package com.subgraph.sgmail.ui;


import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class Resources {

    public final static String COLOR_ERROR_MESSAGE = "com.subgraph.sgmail.colors.error";
    public final static String COLOR_SENDER_SECTION = "com.subgraph.sgmail.colors.sender";
    public final static String COLOR_DATE_SECTION = "com.subgraph.sgmail.colors.date";
    public final static String COLOR_SUBJECT_SECTION = "com.subgraph.sgmail.colors.subject";
    public final static String COLOR_BODY_SECTION = "com.subgraph.sgmail.colors.body";
    public final static String COLOR_NEW_MESSAGE_BADGE = "com.subgraph.sgmail.colors.badge";
    public final static String COLOR_SELECTED_ELEMENT_FOREGROUND = "com.subgraph.sgmail.colors.selected";
    public final static String COLOR_HIGHLIGHT_BACKGROUND = "com.subgraph.sgmail.colors.highlight.background";
    public final static String COLOR_HIGHLIGHT_FOREGROUND = "com.subgraph.sgmail.colors.highlight.foreground";
    public final static String COLOR_WHITE = "com.subgraph.colors.white";
    public final static String COLOR_BLACK = "com.subgraph.colors.black";
    public final static String COLOR_HEADER = "com.subgraph.sgmail.colors.header";
    public final static String COLOR_ATTACHMENT_PANEL_HIGHLIGHT = "com.subgraph.sgmail.colors.attachment.highlight";

    public final static String FONT_HEADER = "com.subgraph.sgmail.fonts.header";
    public final static String FONT_SENDER = "com.subgraph.sgmail.fonts.sender";
    public final static String FONT_DATE = "com.subgraph.sgmail.fonts.date";
    public final static String FONT_SUBJECT = "com.subgraph.sgmail.fonts.subject";
    public final static String FONT_SUBJECT_BOLD = "com.subgraph.sgmail.fonts.subject.bold";
    public final static String FONT_BODY_SNIPPET = "com.subgraph.sgmail.fonts.body";
    public final static String FONT_BODY_SNIPPET_BOLD = "com.subgraph.sgmail.fonts.body.bold";
    public final static String FONT_MESSAGE_BODY = "com.subgraph.sgmail.fonts.message";

    public static void initialize() {
        final String red = "255,0,0";
        final String black = "0,0,0";
        final String greyish = "119,136,153";
        final String neonYellow = "255,255,122";

        addColor(COLOR_ERROR_MESSAGE, red);
        addColor(COLOR_SENDER_SECTION, black);
        addColor(COLOR_SUBJECT_SECTION, black);
        addColor(COLOR_DATE_SECTION, greyish);
        addColor(COLOR_BODY_SECTION, greyish);
        addColor(COLOR_SELECTED_ELEMENT_FOREGROUND, black);
        addColor(COLOR_HIGHLIGHT_BACKGROUND, neonYellow);
        addColor(COLOR_HIGHLIGHT_FOREGROUND, black);
        addColor(COLOR_HEADER, greyish);

        addColor(COLOR_NEW_MESSAGE_BADGE,"128,140,152");
        addColor(COLOR_WHITE, "255,255,255");
        addColor(COLOR_BLACK, black);
        addColor(COLOR_ATTACHMENT_PANEL_HIGHLIGHT, "182,207,255");

        initializeFonts();
    }

    public static void addColor(String name, RGB rgb) {
        JFaceResources.getColorRegistry().put(name, rgb);
    }

    public static void addColor(String name, String rgb) {
        addColor(name, StringConverter.asRGB(rgb));
    }

    public static void initializeFonts() {
        final FontRegistry fonts = JFaceResources.getFontRegistry();
        final Font systemFont = Display.getDefault().getSystemFont();
        final FontData[] systemFontData = systemFont.getFontData();
        final FontData base = systemFontData[0];
        final String baseName = base.getName();

        fonts.put(FONT_HEADER, createFont(baseName, "bold-13"));
        fonts.put(FONT_SENDER, createFont(baseName, "bold-13" ));
        fonts.put(FONT_DATE, createFont(baseName, "regular-13" ));
        fonts.put(FONT_SUBJECT, createFont(baseName, "regular-11" ));
        fonts.put(FONT_SUBJECT_BOLD, createFont(baseName, "bold-11"));
        fonts.put(FONT_BODY_SNIPPET, createFont(baseName, "regular-10" ));
        fonts.put(FONT_BODY_SNIPPET_BOLD, createFont(baseName, "bold-10"));
        fonts.put(FONT_MESSAGE_BODY, JFaceResources.getTextFont().getFontData());
    }

    private static FontData[] createFont(String baseName, String fontInfo) {
        final FontData fd = StringConverter.asFontData(baseName + "-" + fontInfo);
        return new FontData[] { fd };
    }
}
