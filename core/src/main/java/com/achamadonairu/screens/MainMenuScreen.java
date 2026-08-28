package com.achamadonairu.screens;

import com.achamadonairu.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenuScreen implements Screen {

    private Main jogo;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    public MainMenuScreen(Main jogo) {
        this.jogo = jogo;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {

        float largura = Gdx.graphics.getWidth();
        float altura = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0.05f, 0.08f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // =============================
        // TÍTULO CENTRALIZADO
        // =============================

        font.getData().setScale(4f);

        layout.setText(font, "A CHAMA DE NAIRU");

        float tituloX = (largura - layout.width) / 2f;
        float tituloY = altura * 0.65f;

        font.draw(
            batch,
            "A CHAMA DE NAIRU",
            tituloX,
            tituloY
        );

        // =============================
        // JOGAR CENTRALIZADO
        // =============================

        font.getData().setScale(3f);

        layout.setText(font, "JOGAR");

        float jogarX = (largura - layout.width) / 2f;
        float jogarY = altura * 0.48f;

        font.draw(
            batch,
            "JOGAR",
            jogarX,
            jogarY
        );

        batch.end();

        // =============================
        // CLIQUE / TOQUE EM JOGAR
        // =============================

        if (Gdx.input.justTouched()) {

            float toqueX = Gdx.input.getX();
            float toqueY =
                altura - Gdx.input.getY();

            // Área maior para facilitar o toque
            float centroX = largura / 2f;

            if (
                toqueX > centroX - 180 &&
                    toqueX < centroX + 180 &&
                    toqueY > altura * 0.38f &&
                    toqueY < altura * 0.55f
            ) {

                jogo.setScreen(new GameScreen());
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
