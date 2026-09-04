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

    private static final float LARGURA_VIRTUAL = 1200f;
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

    // =========================================
    // POSIÇÃO E TAMANHO DO PERSONAGEM
    // =========================================

    private float jogadorX;
    private float jogadorY;

    private float jogadorLargura;
    private float jogadorAltura;

    // =========================================
    // VELOCIDADE
    // =========================================

    private float velocidade = 180f;

    private boolean jogadorEmMovimento;

    @Override
    public void show() {

        batch = new SpriteBatch();
        font = new BitmapFont();

        // =========================================
        // CÂMARA DO MUNDO
        // =========================================

        camera = new OrthographicCamera();

        viewport = new FitViewport(
            LARGURA_VIRTUAL,
            ALTURA_VIRTUAL,
            camera
        );

        viewport.apply(true);

        // =========================================
        // CÂMARA DA INTERFACE
        // =========================================

        cameraUi = new OrthographicCamera();

        viewportUi = new FitViewport(
            LARGURA_VIRTUAL,
            ALTURA_VIRTUAL,
            cameraUi
        );

        viewportUi.apply(true);

        // =========================================
        // CARREGAR IMAGENS
        // =========================================

        background = new Texture(
            "backgrounds/floresta_mavala_nivel1.png"
        );

        background.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        );

        carregarAnimacoesNairu();

        configurarPersonagem();
    }

    // =============================================
    // CARREGAR ANIMAÇÕES DO NAIRU
    // =============================================

    private void carregarAnimacoesNairu() {

        Array<TextureRegion> framesIdle = carregarFrames(
            "characters/nairu/idle/idle_",
            4
        );

        Array<TextureRegion> framesWalk = carregarFrames(
            "characters/nairu/walk/walk_",
            6
        );

        // Nairu parado
        animacaoIdle = new Animation<>(
            0.22f,
            framesIdle,
            Animation.PlayMode.LOOP
        );

        // Nairu andando
        animacaoWalk = new Animation<>(
            0.08f,
            framesWalk,
            Animation.PlayMode.LOOP
        );
    }

    // =============================================
    // CARREGAR FRAMES
    // =============================================

    private Array<TextureRegion> carregarFrames(
        String prefixo,
        int quantidade
    ) {

        Array<TextureRegion> frames = new Array<>();

        for (int i = 1; i <= quantidade; i++) {

            String numero = String.format("%02d", i);

            Texture textura = new Texture(
                prefixo + numero + ".png"
            );

            textura.setFilter(
                Texture.TextureFilter.Nearest,
                Texture.TextureFilter.Nearest
            );

            texturasNairu.add(textura);

            frames.add(
                new TextureRegion(textura)
            );
        }

        return frames;
    }

    // =============================================
    // CONFIGURAR PERSONAGEM
    // =============================================

    private void configurarPersonagem() {

        float larguraTela =
            viewport.getWorldWidth();

        float alturaTela =
            viewport.getWorldHeight();

        // =========================================
        // TAMANHO DO NAIRU
        // =========================================

        jogadorLargura =
            larguraTela * 0.08f;

        jogadorAltura =
            jogadorLargura * 1.5f;

        // =========================================
        // POSIÇÃO INICIAL HORIZONTAL
        // =========================================

        jogadorX =
            larguraTela * 0.06f;

        // =========================================
        // POSIÇÃO DO CHÃO
        // =========================================
        //
        // Antes estava:
        //
        // jogadorY = alturaTela * 0.23f;
        //
        // Isso deixava Nairu acima do chão.
        //
        // Agora ele fica aproximadamente
        // apoiado sobre o relvado.
        // =========================================

        jogadorY =
            alturaTela * 0.145f;
    }

    // =============================================
    // RENDER
    // =============================================

    @Override
    public void render(float delta) {

        float larguraTela =
            viewport.getWorldWidth();

        float alturaTela =
            viewport.getWorldHeight();

        // =========================================
        // LIMPAR TELA
        // =========================================

        Gdx.gl.glClearColor(
            0,
            0,
            0,
            1
        );

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        );

        // =========================================
        // ACTUALIZAÇÕES
        // =========================================

        movimentarNairu(delta);

        atualizarAnimacao(delta);

        atualizarCamera();

        // =========================================
        // DESENHAR O MUNDO
        // =========================================

        viewport.apply();

        camera.update();

        batch.setProjectionMatrix(
            camera.combined
        );

        batch.begin();

        // =========================================
        // FUNDO - FLORESTA DE MAVALA
        // =========================================

        batch.draw(
            background,
            0f,
            0f,
            LARGURA_MUNDO,
            ALTURA_VIRTUAL
        );

        // =========================================
        // PERSONAGEM - NAIRU
        // =========================================

        Animation<TextureRegion> animacaoActual =
            jogadorEmMovimento
                ? animacaoWalk
                : animacaoIdle;

        TextureRegion frameActual =
            animacaoActual.getKeyFrame(
                tempoAnimacao,
                true
            );

        // =========================================
        // OLHANDO PARA DIREITA
        // =========================================

        if (olhandoDireita) {

            batch.draw(
                frameActual,
                jogadorX,
                jogadorY,
                jogadorLargura,
                jogadorAltura
            );

        }

        // =========================================
        // OLHANDO PARA ESQUERDA
        // =========================================

        else {

            batch.draw(
                frameActual,
                jogadorX + jogadorLargura,
                jogadorY,
                -jogadorLargura,
                jogadorAltura
            );
        }

        // =========================================
        // FIM DO PROTÓTIPO
        // =========================================

        font.getData().setScale(1.3f);

        font.draw(
            batch,
            "FIM DO PROTOTIPO",
            LARGURA_MUNDO - 235f,
            jogadorY + jogadorAltura + 35f
        );

        batch.end();

        // =========================================
        // INTERFACE
        // =========================================

        viewportUi.apply();

        cameraUi.update();

        batch.setProjectionMatrix(
            cameraUi.combined
        );

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

        // BOTÃO ESQUERDA

        font.draw(
            batch,
            "<",
            larguraTela * 0.06f,
            alturaTela * 0.15f
        );

        // BOTÃO DIREITA

        font.draw(
            batch,
            ">",
            larguraTela * 0.18f,
            alturaTela * 0.15f
        );

        batch.end();
    }

    // =============================================
    // MOVIMENTO DO NAIRU
    // =============================================

    private void movimentarNairu(float delta) {

        float larguraTela =
            viewport.getWorldWidth();

        float alturaTela =
            viewport.getWorldHeight();

        float jogadorXAnterior =
            jogadorX;

        jogadorEmMovimento =
            false;

        // =========================================
        // TECLADO - ESQUERDA
        // =========================================

        if (
            Gdx.input.isKeyPressed(
                Input.Keys.LEFT
            )
        ) {

            jogadorX -=
                velocidade * delta;

            jogadorEmMovimento =
                true;

            olhandoDireita =
                false;
        }

        // =========================================
        // TECLADO - DIREITA
        // =========================================

        if (
            Gdx.input.isKeyPressed(
                Input.Keys.RIGHT
            )
        ) {

            jogadorX +=
                velocidade * delta;

            jogadorEmMovimento =
                true;

            olhandoDireita =
                true;
        }

        // =========================================
        // CONTROLO POR TOQUE
        // =========================================

        if (
            Gdx.input.isTouched()
        ) {

            toque.set(
                Gdx.input.getX(),
                Gdx.input.getY(),
                0f
            );

            viewportUi.unproject(
                toque
            );

            float toqueX =
                toque.x;

            float toqueY =
                toque.y;

            // =====================================
            // ÁREA DO BOTÃO ESQUERDA
            // =====================================

            float esquerdaMinX =
                larguraTela * 0.02f;

            float esquerdaMaxX =
                larguraTela * 0.14f;

            float esquerdaMinY =
                0f;

            float esquerdaMaxY =
                alturaTela * 0.25f;

            // =====================================
            // ÁREA DO BOTÃO DIREITA
            // =====================================

            float direitaMinX =
                larguraTela * 0.14f;

            float direitaMaxX =
                larguraTela * 0.28f;

            float direitaMinY =
                0f;

            float direitaMaxY =
                alturaTela * 0.25f;

            // =====================================
            // TOCAR ESQUERDA
            // =====================================

            if (
                toqueX >= esquerdaMinX &&
                    toqueX <= esquerdaMaxX &&
                    toqueY >= esquerdaMinY &&
                    toqueY <= esquerdaMaxY
            ) {

                jogadorX -=
                    velocidade * delta;

                jogadorEmMovimento =
                    true;

                olhandoDireita =
                    false;
            }

            // =====================================
            // TOCAR DIREITA
            // =====================================

            if (
                toqueX >= direitaMinX &&
                    toqueX <= direitaMaxX &&
                    toqueY >= direitaMinY &&
                    toqueY <= direitaMaxY
            ) {

                jogadorX +=
                    velocidade * delta;

                jogadorEmMovimento =
                    true;

                olhandoDireita =
                    true;
            }
        }

        // =========================================
        // LIMITE ESQUERDO
        // =========================================

        if (
            jogadorX < 0
        ) {

            jogadorX =
                0;
        }

        // =========================================
        // LIMITE DIREITO
        // =========================================

        float limiteDireito =
            LARGURA_MUNDO -
                jogadorLargura;

        if (
            jogadorX >
                limiteDireito
        ) {

            jogadorX =
                limiteDireito;
        }

        // =========================================
        // WALK SOMENTE SE REALMENTE SE MOVEU
        // =========================================

        jogadorEmMovimento =
            jogadorX !=
                jogadorXAnterior;
    }

    // =============================================
    // ACTUALIZAR ANIMAÇÃO
    // =============================================

    private void atualizarAnimacao(
        float delta
    ) {

        if (
            jogadorEmMovimento !=
                estadoAnteriorEmMovimento
        ) {

            tempoAnimacao =
                0f;

            estadoAnteriorEmMovimento =
                jogadorEmMovimento;
        }

        tempoAnimacao +=
            delta;
    }

    // =============================================
    // CÂMARA
    // =============================================

    private void atualizarCamera() {

        float metadeTela =
            viewport.getWorldWidth()
                / 2f;

        float centroJogador =
            jogadorX +
                jogadorLargura / 2f;

        camera.position.x =
            Math.max(
                metadeTela,
                Math.min(
                    centroJogador,
                    LARGURA_MUNDO -
                        metadeTela
                )
            );

        camera.position.y =
            viewport.getWorldHeight()
                / 2f;
    }

    // =============================================
    // RESIZE
    // =============================================

    @Override
    public void resize(
        int width,
        int height
    ) {

        viewport.update(
            width,
            height,
            true
        );

        viewportUi.update(
            width,
            height,
            true
        );
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

    // =============================================
    // LIBERTAR MEMÓRIA
    // =============================================

    @Override
    public void dispose() {

        batch.dispose();

        font.dispose();

        background.dispose();

        for (
            Texture textura :
            texturasNairu
        ) {

            textura.dispose();
        }
    }
}
