package com.game.szimu;


import java.awt.*;

//ugye extends Gameobj h inherit mukodjon + generics combo, h ne legyen gond
//az int meg a integer dolgokkal eees arrayt konvertalni belole
public class Brick extends GameObj{
    public Brick(float x, float y, float widthIs, float heightIs, Colour colour) {
        super(x, y, widthIs, heightIs, colour);
    }
}
