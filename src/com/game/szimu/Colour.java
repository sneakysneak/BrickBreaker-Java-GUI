package com.game.szimu;
import javax.management.relation.RelationNotFoundException;
import java.awt.Color;
import java.util.*;

/**
 * Hide the specific internal representation of colours
 *  from most of the program.
 * Map to Swing color when required.
 */
//yourEnum.ordinal()
public enum Colour
    //Color az a type! mint int string egyeb...
{
    RED(Color.RED), BLUE(Color.BLUE), GRAY(Color.GRAY),
    ORANGE(Color.ORANGE), CYAN(Color.CYAN), BLACK(Color.BLACK),
    WHITE(Color.WHITE), GREEN(Color.GREEN);

    private Color c; //instance var, type Color awt lib

    Colour( Color c ) { this.c = c; } //Constructor

    public Color forSwing() {
        return c; } //Method
}