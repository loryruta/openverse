package xyz.upperlevel.openverse.client.gui;

public class GuiBounds {
    public static GuiBounds EMPTY = new GuiBounds(0, 0, 0, 0);
    public static GuiBounds FULL = new GuiBounds(0, 0, 1, 1);

    public final double minX, minY, maxX, maxY;

    public GuiBounds(double minX, double minY, double maxX, double maxY) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
    }

    public boolean isInside(double x, double y) {
        return  x >= minX && x <= maxX &&
                y >= minY && y <= maxY;
    }

    public GuiBounds translate(double x, double y) {
        return new GuiBounds(
                minX + x,
                minY + y,
                maxX + x,
                maxY + y
        );
    }

    public GuiBounds insideRelative(GuiBounds child) {
        double dx = maxX - minX;
        double dy = maxX - minX;

        return new GuiBounds(
                minX + dx * child.minX,
                minY + dy * child.minY,
                minX + dx * child.maxX,
                minY + dy * child.maxY
        );
    }

    public int hashCode() {
        // Some tests lead to take those parts of the float as the most significant for low precision calculations
        // For the values of the float between 0 and 1 (with intervals of 0.1) the first 5 bits didn't change, so this is what remains
        // (code & comments taken from legacy code)
        return  (Float.floatToRawIntBits((float) minX) >> 19) & 0x000000FF |
                (Float.floatToRawIntBits((float) maxX) >> 11) & 0x0000FF00 |
                (Float.floatToRawIntBits((float) minY) >> 3)  & 0x00FF0000 |
                (Float.floatToRawIntBits((float) maxY) << 5)  & 0xFF000000;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof GuiBounds)) return false;

        GuiBounds o = (GuiBounds) other;
        return  this.minX == o.minX &&
                this.maxX == o.maxX &&
                this.minY == o.minY &&
                this.maxY == o.maxY;
    }

    @Override
    public String toString() {
        return "{" + minX + ", " + minY + ", " + maxX + ", " + maxY + "}";
    }

    public boolean isNormal() {
        return minX >= 0f && minY >= 0f && maxX <= 1f && maxY <= 1f;
    }
}
