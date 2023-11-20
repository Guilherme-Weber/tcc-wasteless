package com.guilhermeweber.wasteless.activity.helper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class LocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this) //Be aware of state of the connection
                .addOnConnectionFailedListener(this) //Be aware of failures
                .build();

        //Tentando conexão com o Google API. Se a tentativa for bem sucessidade, o método onConnected() será chamado, senão, o método onConnectionFailed() será chamado.
        googleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        pararConexaoComGoogleApi();
    }

    public void pararConexaoComGoogleApi() {
        //Verificando se está conectado para então cancelar a conexão!
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Depois que o método connect() for chamado, esse método será chamado de forma assíncrona caso a conexão seja bem sucedida.
     *
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        //Conexão com o serviços do Google Service API foi estabelecida!
    }

    /**
     * Esse método é chamado quando o client está temporariamente desconectado. Isso pode acontecer quando houve uma falha ou problema com o serviço que faça com que o client seja desligado.
     * Nesse estado, todas as requisições e listeners são cancelados.
     * Não se preocupe em tentar reestabelecer a conexão, pois isso acontecerá automaticamente.
     * As aplicações devem desabilitar recursos visuais que estejam relacionados com o uso dos serviços e habilitá-los novamente quando o método onConnected() for chamado, indicando reestabelecimento da conexão.
     */
    @Override
    public void onConnectionSuspended(int i) {
        // Aguardando o GoogleApiClient reestabelecer a conexão.
    }

    /**
     * Método chamado quando um erro de conexão acontece e não é possível acessar os serviços da Google Service.
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //A conexão com o Google API falhou!
        pararConexaoComGoogleApi();
    }
}
