package com.achamadonairu.screens;

import com.achamadonairu.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen implements Screen {

    private static final float LARGURA_VIRTUAL = 960f;
    private static final float ALTURA_VIRTUAL = 540f;

    private Main jogo;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private OrthographicCamera camera;
    private Viewport viewport;
    private final Vector3 toque = new Vector3();

    public MainMenuScreen(Main jogo) {
        this.jogo = jogo;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();

        camera = new OrthographicCamera();
        viewport = new FitViewport(LARGURA_VIRTUAL, ALTURA_VIRTUAL, camera);
        viewport.apply(true);
    }

    @Override
    public void render(float delta) {

        float largura = viewport.getWorldWidth();
        float altura = viewport.getWorldHeight();

        Gdx.gl.glClearColor(0.05f, 0.08f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
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

            toque.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            viewport.unproject(toque);

            float toqueX = toque.x;
            float toqueY = toque.y;

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
        viewport.update(width, height, true);
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
