package xyz.upperlevel.openverse.client.util;

import lombok.Getter;

public class Color {
    @Getter
    public final float r, g, b, a;

    private Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    private byte[] data = null;

    public byte[] getData() {
        if (data == null) {
            data = new byte[]{
                    (byte) getRedAsInt(),
                    (byte) getBlueAsInt(),
                    (byte) getGreenAsInt(),
                    (byte) getAlphaAsInt()
            };
        }
        return data;
    }

    public int getRedAsInt() {
        return (int) (r * 255);
    }

    public int getGreenAsInt() {
        return (int) (g * 255);
    }

    public int getBlueAsInt() {
        return (int) (b * 255);
    }

    public int getAlphaAsInt() {
        return (int) (a * 255);
    }

    public Color add(Color other) {
        return rgba(
                r + other.r,
                g + other.g,
                b + other.b,
                a + other.a
        );
    }

    public Color sub(Color other) {
        return rgba(
                r - other.r,
                g - other.g,
                b - other.b,
                a - other.a
        );
    }

    public Color mul(Color other) {
        return rgba(
                r * other.r,
                g * other.g,
                b * other.b,
                a * other.a
        );
    }

    public Color div(Color other) {
        return rgba(
                r / other.r,
                g / other.g,
                b / other.b,
                a / other.a
        );
    }

    @Override
    public String toString() {
        return "{" + getRedAsInt() + "," + getGreenAsInt() + "," + getBlueAsInt() + "," + getAlphaAsInt() + "}";
    }

    @Override
    public int hashCode() {
        // rgba = 4 bytes = sizeof(int)
        return  (getRedAsInt()     << 8 * 0) |
                (getGreenAsInt()   << 8 * 1) |
                (getBlueAsInt()    << 8 * 2) |
                (getAlphaAsInt() << 8 * 3);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Color)) return false;
        Color other = (Color) o;
        return other.r == r && other.g == g && other.b == b && other.a == a;
    }

    public static Color rgb(int r, int g, int b) {
        return rgba(r, g, b, 255);
    }

    public static Color rgba(int r, int g, int b, int a) {
        return new Color(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public static Color rgb(float r, float g, float b) {
        return rgba(r, g, b, 1f);
    }

    private static float normalizeColorValue(float v) {
        return v < 0 ? 0 : (v > 1f ? 1f : v);
    }

    public static Color rgba(float r, float g, float b, float a) {
        return new Color(normalizeColorValue(r), normalizeColorValue(g), normalizeColorValue(b), normalizeColorValue(a));
    }

    public static Color color(java.awt.Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    private static byte fromHex(char[] in, int index) {
        return (byte) ((Character.digit(in[index], 16) << 4) + Character.digit(in[index + 1], 16));
    }

    public static Color hex(String hex) {
        byte r, g, b, a = (byte) 255;
        if (hex.length() < 6)
            throw new IllegalArgumentException("Bad hex length");
        char[] str = hex.toCharArray();

        r = fromHex(str, 0);
        g = fromHex(str, 2);
        b = fromHex(str, 4);

        if (hex.length() >= 8)
            a = fromHex(str, 6);
        return rgba((int) r, (int) g, (int) b, (int) a);
    }


    //----------------------------- DEF COLORS -----------------------------

    public static final Color TRANSPARENT = rgba(0f, 0f, 0f, 0f);

    public static final Color WHITE       = rgb(255, 255, 255);
    public static final Color BLACK       = rgb(0  , 0  , 0  );
    public static final Color RED         = rgb(255, 0  , 0  );
    public static final Color GREEN       = rgb(0  , 255, 0  );
    public static final Color BLUE        = rgb(0  , 0  , 255);

    public static final Color DARK_BLUE   = rgb(0  , 170, 170);
    public static final Color DARK_GREEN  = rgb(0  , 0  , 0  );
    public static final Color DARK_AQUA   = rgb(0  , 170, 170);
    public static final Color DARK_RED    = rgb(170, 0  , 0  );
    public static final Color DARK_PURPLE = rgb(170, 0  , 170);
    public static final Color GOLD        = rgb(255, 170, 0  );
    public static final Color GRAY        = rgb(170, 170, 170);
    public static final Color DARK_GRAY   = rgb(85 , 85 , 85 );
    public static final Color INDIGO      = rgb(85 , 85 , 255);
    public static final Color AQUA        = rgb(85 , 255, 255);
    public static final Color PINK        = rgb(255, 85 , 255);
    public static final Color YELLOW      = rgb(255, 255, 85 );
}
