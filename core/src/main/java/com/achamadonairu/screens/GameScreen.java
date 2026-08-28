package com.achamadonairu.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen implements Screen {

    private SpriteBatch batch;
    private BitmapFont font;

    private Texture background;
    private Texture nairu;

    // Posição e tamanho do personagem
    private float jogadorX;
    private float jogadorY;

    private float jogadorLargura;
    private float jogadorAltura;

    // Velocidade
    private float velocidade = 300f;

    @Override
    public void show() {

        batch = new SpriteBatch();
        font = new BitmapFont();

        // Carrega as imagens
        background = new Texture("backgrounds/floresta_mavala.png");
        nairu = new Texture("characters/nairu.png");

        configurarPersonagem();
    }

    private void configurarPersonagem() {

        float larguraTela = Gdx.graphics.getWidth();
        float alturaTela = Gdx.graphics.getHeight();

        // Tamanho proporcional ao ecrã
        jogadorLargura = larguraTela * 0.08f;
        jogadorAltura = jogadorLargura * 1.55f;

        // Posição inicial
        jogadorX = larguraTela * 0.06f;

        // Ajustado para ficar sobre o relvado
        jogadorY = alturaTela * 0.23f;
    }

    @Override
    public void render(float delta) {

        float larguraTela = Gdx.graphics.getWidth();
        float alturaTela = Gdx.graphics.getHeight();

        // Limpa a tela
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Movimento
        movimentarNairu(delta);

        batch.begin();

        // =========================================
        // FUNDO - FLORESTA DE MAVALA
        // =========================================

        batch.draw(
            background,
            0,
            0,
            larguraTela,
            alturaTela
        );

        // =========================================
        // PERSONAGEM - NAIRU
        // =========================================

        batch.draw(
            nairu,
            jogadorX,
            jogadorY,
            jogadorLargura,
            jogadorAltura
        );

        // =========================================
        // TÍTULO DO NÍVEL
        // =========================================

        font.getData().setScale(1.5f);

        font.draw(
            batch,
            "NIVEL 1 - FLORESTA DE MAVALA",
            larguraTela * 0.03f,
            alturaTela * 0.95f
        );

        // =========================================
        // CONTROLOS MOBILE
        // =========================================

        font.getData().setScale(5f);

        // Botão esquerda
        font.draw(
            batch,
            "<",
            larguraTela * 0.06f,
            alturaTela * 0.15f
        );

        // Botão direita
        font.draw(
            batch,
            ">",
            larguraTela * 0.18f,
            alturaTela * 0.15f
        );

        batch.end();
    }

    private void movimentarNairu(float delta) {

        float larguraTela = Gdx.graphics.getWidth();
        float alturaTela = Gdx.graphics.getHeight();

        // =========================================
        // CONTROLO PELO TECLADO
        // =========================================

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            jogadorX -= velocidade * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

            jogadorX += velocidade * delta;
        }

        // =========================================
        // CONTROLO POR TOQUE
        // =========================================

        if (Gdx.input.isTouched()) {

            float toqueX = Gdx.input.getX();

            float toqueY =
                alturaTela - Gdx.input.getY();

            // Área do botão ESQUERDA
            float esquerdaMinX =
                larguraTela * 0.02f;

            float esquerdaMaxX =
                larguraTela * 0.14f;

            float esquerdaMinY =
                0;

            float esquerdaMaxY =
                alturaTela * 0.25f;

            // Área do botão DIREITA
            float direitaMinX =
                larguraTela * 0.14f;

            float direitaMaxX =
                larguraTela * 0.28f;

            float direitaMinY =
                0;

            float direitaMaxY =
                alturaTela * 0.25f;

            // TOCAR ESQUERDA
            if (
                toqueX >= esquerdaMinX &&
                    toqueX <= esquerdaMaxX &&
                    toqueY >= esquerdaMinY &&
                    toqueY <= esquerdaMaxY
            ) {

                jogadorX -= velocidade * delta;
            }

            // TOCAR DIREITA
            if (
                toqueX >= direitaMinX &&
                    toqueX <= direitaMaxX &&
                    toqueY >= direitaMinY &&
                    toqueY <= direitaMaxY
            ) {

                jogadorX += velocidade * delta;
            }
        }

        // =========================================
        // LIMITES DA TELA
        // =========================================

        if (jogadorX < 0) {

            jogadorX = 0;
        }

        float limiteDireito =
            larguraTela - jogadorLargura;

        if (jogadorX > limiteDireito) {

            jogadorX = limiteDireito;
        }
    }

    @Override
    public void resize(int width, int height) {

        jogadorLargura = width * 0.08f;

        jogadorAltura =
            jogadorLargura * 1.55f;

        jogadorY =
            height * 0.23f;
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

        background.dispose();
        nairu.dispose();
    }
}
