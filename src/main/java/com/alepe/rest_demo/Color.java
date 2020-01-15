package com.alepe.rest_demo;

/**
 * List of available colors
 * @since 2020/01/15.
 */
public enum Color {
    NONE, RED, BLUE, GREEN, YELLOW, ORANGE, PINK, BROWN, WHITE, BLACK;

    /**
     * Gets Color from String.
     * @param color : lowercase String of color
     * @return : Color object
     * @throws IllegalArgumentException : If color was not found in list
     */
    static public Color fromString(String color) throws IllegalArgumentException {
        return color.isEmpty() ? NONE : valueOf(color.toUpperCase());
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
