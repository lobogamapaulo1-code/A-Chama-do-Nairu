package com.achamadonairu.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

    private static final float LARGURA_VIRTUAL = 960f;
    private static final float ALTURA_VIRTUAL = 540f;
    private static final float LARGURA_MUNDO = 2100f;

    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;
    private OrthographicCamera cameraUi;
    private Viewport viewport;
    private Viewport viewportUi;
    private final Vector3 toque = new Vector3();

    private Texture background;
    private final Array<Texture> texturasNairu = new Array<>();
    private Animation<TextureRegion> animacaoIdle;
    private Animation<TextureRegion> animacaoWalk;
    private float tempoAnimacao;
    private boolean estadoAnteriorEmMovimento;
    private boolean olhandoDireita = true;

    // Posição e tamanho do personagem
    private float jogadorX;
    private float jogadorY;

    private float jogadorLargura;
    private float jogadorAltura;

    // Velocidade
    private float velocidade = 180f;

    private boolean jogadorEmMovimento;

    @Override
    public void show() {

        batch = new SpriteBatch();
        font = new BitmapFont();

        camera = new OrthographicCamera();
        viewport = new FitViewport(LARGURA_VIRTUAL, ALTURA_VIRTUAL, camera);
        viewport.apply(true);

        cameraUi = new OrthographicCamera();
        viewportUi = new FitViewport(LARGURA_VIRTUAL, ALTURA_VIRTUAL, cameraUi);
        viewportUi.apply(true);

        // Carrega as imagens
        background = new Texture("backgrounds/floresta_mavala_nivel1.png");
        background.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        carregarAnimacoesNairu();

        configurarPersonagem();
    }

    private void carregarAnimacoesNairu() {

        Array<TextureRegion> framesIdle = carregarFrames(
            "characters/nairu/idle/idle_",
            4
        );
        Array<TextureRegion> framesWalk = carregarFrames(
            "characters/nairu/walk/walk_",
            6
        );

        animacaoIdle = new Animation<>(0.22f, framesIdle, Animation.PlayMode.LOOP);
        animacaoWalk = new Animation<>(0.08f, framesWalk, Animation.PlayMode.LOOP);
    }

    private Array<TextureRegion> carregarFrames(String prefixo, int quantidade) {

        Array<TextureRegion> frames = new Array<>();

        for (int i = 1; i <= quantidade; i++) {
            String numero = String.format("%02d", i);
            Texture textura = new Texture(prefixo + numero + ".png");
            textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            texturasNairu.add(textura);
            frames.add(new TextureRegion(textura));
        }

        return frames;
    }

    private void configurarPersonagem() {

        float larguraTela = viewport.getWorldWidth();
        float alturaTela = viewport.getWorldHeight();

        // Tamanho proporcional ao ecrã
        jogadorLargura = larguraTela * 0.08f;
        jogadorAltura = jogadorLargura * 1.5f;

        // Posição inicial
        jogadorX = larguraTela * 0.06f;

        // Ajustado para ficar sobre o relvado
        jogadorY = alturaTela * 0.23f;
    }

    @Override
    public void render(float delta) {

        float larguraTela = viewport.getWorldWidth();
        float alturaTela = viewport.getWorldHeight();

        // Limpa a tela
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Movimento
        movimentarNairu(delta);
        atualizarAnimacao(delta);
        atualizarCamera();

        viewport.apply();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // =========================================
        // FUNDO - FLORESTA DE MAVALA
        // =========================================

        batch.draw(background, 0f, 0f, LARGURA_MUNDO, ALTURA_VIRTUAL);

        // =========================================
        // PERSONAGEM - NAIRU
        // =========================================

        Animation<TextureRegion> animacaoActual = jogadorEmMovimento
            ? animacaoWalk
            : animacaoIdle;
        TextureRegion frameActual = animacaoActual.getKeyFrame(tempoAnimacao, true);

        if (olhandoDireita) {
            batch.draw(frameActual, jogadorX, jogadorY, jogadorLargura, jogadorAltura);
        } else {
            batch.draw(
                frameActual,
                jogadorX + jogadorLargura,
                jogadorY,
                -jogadorLargura,
                jogadorAltura
            );
        }

        font.getData().setScale(1.3f);
        font.draw(
            batch,
            "FIM DO PROTOTIPO",
            LARGURA_MUNDO - 235f,
            jogadorY + jogadorAltura + 35f
        );

        batch.end();

        // A interface usa uma câmara própria e permanece fixa no ecrã.
        viewportUi.apply();
        cameraUi.update();
        batch.setProjectionMatrix(cameraUi.combined);
        batch.begin();

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

        float larguraTela = viewport.getWorldWidth();
        float alturaTela = viewport.getWorldHeight();
        float jogadorXAnterior = jogadorX;
        jogadorEmMovimento = false;

        // =========================================
        // CONTROLO PELO TECLADO
        // =========================================

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            jogadorX -= velocidade * delta;
            jogadorEmMovimento = true;
            olhandoDireita = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {

            jogadorX += velocidade * delta;
            jogadorEmMovimento = true;
            olhandoDireita = true;
        }

        // =========================================
        // CONTROLO POR TOQUE
        // =========================================

        if (Gdx.input.isTouched()) {

            toque.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
            viewportUi.unproject(toque);

            float toqueX = toque.x;
            float toqueY = toque.y;

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
                jogadorEmMovimento = true;
                olhandoDireita = false;
            }

            // TOCAR DIREITA
            if (
                toqueX >= direitaMinX &&
                    toqueX <= direitaMaxX &&
                    toqueY >= direitaMinY &&
                    toqueY <= direitaMaxY
            ) {

                jogadorX += velocidade * delta;
                jogadorEmMovimento = true;
                olhandoDireita = true;
            }
        }

        // =========================================
        // LIMITES DA TELA
        // =========================================

        if (jogadorX < 0) {

            jogadorX = 0;
        }

        float limiteDireito =
            LARGURA_MUNDO - jogadorLargura;

        if (jogadorX > limiteDireito) {

            jogadorX = limiteDireito;
        }

        // Só mantém WALK quando houve deslocamento real dentro dos limites do mundo.
        jogadorEmMovimento = jogadorX != jogadorXAnterior;
    }

    private void atualizarAnimacao(float delta) {

        if (jogadorEmMovimento != estadoAnteriorEmMovimento) {
            tempoAnimacao = 0f;
            estadoAnteriorEmMovimento = jogadorEmMovimento;
        }

        tempoAnimacao += delta;
    }

    private void atualizarCamera() {

        float metadeTela = viewport.getWorldWidth() / 2f;
        float centroJogador = jogadorX + jogadorLargura / 2f;

        camera.position.x = Math.max(
            metadeTela,
            Math.min(centroJogador, LARGURA_MUNDO - metadeTela)
        );
        camera.position.y = viewport.getWorldHeight() / 2f;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        viewportUi.update(width, height, true);
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

        for (Texture textura : texturasNairu) {
            textura.dispose();
        }
    }
}
