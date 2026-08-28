package com.achamadonairu.entities;

import com.badlogic.gdx.graphics.Texture;

public class Player {

    private Texture texture;
    private float x;
    private float y;
    private float width;
    private float height;
    private float velocidade;

    public Player() {

        texture = new Texture("characters/nairu.png");

        x = 30;
        y = 100;

        width = 100;
        height = 140;

        velocidade = 100;
    }
}
