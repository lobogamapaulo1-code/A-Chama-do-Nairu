package com.achamadonairu;

import com.badlogic.gdx.Game;
import com.achamadonairu.screens.MainMenuScreen;

public class Main extends Game {

    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
