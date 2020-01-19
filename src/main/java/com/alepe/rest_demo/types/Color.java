package com.alepe.rest_demo.types;

/**
 * List of available colors
 * @since 2020/01/15.
 */
@SuppressWarnings("unused")
public enum Color {
    NONE, RED, BLUE, GREEN, YELLOW, ORANGE, PURPLE, PINK, BROWN, WHITE, BLACK;

    /**
     * Gets Color from String.
     * @param color : String of color
     * @return : Color object
     */
    static public Color fromString(String color) {
        Color c;
        try {
            c = color.isEmpty() ? NONE : valueOf(color.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            c = NONE;
        }
        return c;
    }

    /**
     * Returns Color as String
     * @return lowercase String
     */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
