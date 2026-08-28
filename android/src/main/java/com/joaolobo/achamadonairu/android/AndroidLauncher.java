package com.joaolobo.achamadonairu.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

// IMPORTA A CLASSE PRINCIPAL DO NOSSO JOGO
import com.achamadonairu.Main;

/** Inicia o jogo no Android. */
public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AndroidApplicationConfiguration configuration =
            new AndroidApplicationConfiguration();

        configuration.useImmersiveMode = true;

        // Inicia A Chama de Nairu
        initialize(new Main(), configuration);
    }
}
