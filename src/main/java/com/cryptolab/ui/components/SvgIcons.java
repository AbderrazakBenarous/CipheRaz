package com.cryptolab.ui.components;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;

/**
 * Resolution-independent SVG vector icon factory for CryptoLab.
 * High-contrast, scalable, and independent of external font or image files.
 *
 * Developed by Abderrazak BENAROUS
 */
public final class SvgIcons {

    private SvgIcons() {}

    // SVG path data (Lucide/Heroicons inspired clean outlines)
    public static final String ICON_SHIELD = "M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z";
    public static final String ICON_LOCK = "M19 11H5a2 2 0 00-2 2v7a2 2 0 002 2h14a2 2 0 002-2v-7a2 2 0 00-2-2zm-12 0V7a5 5 0 0110 0v4";
    public static final String ICON_SEARCH = "M11 19a8 8 0 100-16 8 8 0 000 16zm10 2l-4.35-4.35";
    public static final String ICON_EYE = "M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8zm11 5a5 5 0 100-10 5 5 0 000 10z";
    public static final String ICON_PEN = "M12 20h9M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z";
    public static final String ICON_BOOK = "M4 19.5A2.5 2.5 0 016.5 17H20M4 19.5A2.5 2.5 0 006.5 22H20V2H6.5A2.5 2.5 0 004 4.5v15z";
    public static final String ICON_HISTORY = "M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z";
    public static final String ICON_SETTINGS = "M12 15a3 3 0 100-6 3 3 0 000 6zm7.4 1.4l1.5-1.5-1.4-2.4 2-1-.7-2.1-2.2-.4-.8-2.1 1.7-1.5-1.5-1.5-2.1.8-1.5-1.7-.4-2.2-2.1-.7-1 2-2.4-1.4-1.5 1.5.8 2.1-1.7 1.5-.4 2.2-2.1.7-1-2-2.4 1.4 1.5 1.5-.8 2.1 1.7 1.5.4 2.2 2.1.7 1-2 2.4 1.4-1.5-1.5z";
    public static final String ICON_INFO = "M12 22a10 10 0 100-20 10 10 0 000 20zm-1-11h2v6h-2zm0-4h2v2h-2z";
    public static final String ICON_DASHBOARD = "M3 3h7v7H3zm11 0h7v7h-7zm0 11h7v7h-7zM3 14h7v7H3z";
    public static final String ICON_PLAY = "M5 3l14 9-14 9V3z";
    public static final String ICON_ALERT = "M12 9v4m0 4h.01M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z";
    public static final String ICON_CPU = "M4 4h16v16H4zm5 5h6v6H9zM9 1v3m6-3v3m-6 16v3m6-3v3M1 9h3m-3 6h3m16-6h3m-3 6h3";

    public static Node createIcon(String pathData, double size, Paint fill) {
        SVGPath path = new SVGPath();
        path.setContent(pathData);
        path.setFill(fill != null ? fill : Color.web("#9CA3AF"));

        // Normalizing scale assuming standard 24x24 path bounding box
        double scale = size / 24.0;
        path.setScaleX(scale);
        path.setScaleY(scale);
        return path;
    }

    public static Node createIcon(String pathData, double size, String hexColor) {
        return createIcon(pathData, size, Color.web(hexColor));
    }
}
