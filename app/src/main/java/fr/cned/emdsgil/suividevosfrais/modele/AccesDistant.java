package fr.cned.emdsgil.suividevosfrais.modele;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import fr.cned.emdsgil.suividevosfrais.AuthActivity;
import fr.cned.emdsgil.suividevosfrais.outils.AccesHTTP;
import fr.cned.emdsgil.suividevosfrais.outils.AsyncResponse;

/**
 * Created by emds on 12/01/2017.
 */

public class AccesDistant implements AsyncResponse {

    // constante

    // indiquer l'adresse du serveur local de l'examen(cmd: ipconfig):
    // private static final String SERVERADDR = "http://xx.xx.xx.xx/Suividevosfrais/serveurSuividevosfrais.php";

    // serveur OVH:
     private static final String SERVERADDR = "http://jerome-isoard.fr/Suividevosfrais/serveurSuividevosfrais.php";

    // serveur local domicile:
//    private static final String SERVERADDR = "http://192.168.0.34/Suividevosfrais/serveurSuividevosfrais.php";

    private Context contexte;

    /**
     * Constructeur
     */
    public AccesDistant(Context contexte){
        this.contexte = contexte;
    }


    /**
     * Traitement des informations qui viennent du serveur distant
     * @param output
     */
    @Override
    public void processFinish(String output) {
        // contenu du retour du serveur, pour contrôle dans la console
        Log.d("serveur", "************************" + output);
        // découpage du message reçu
        String[] message = output.split("%");
        Log.d("retour", "***************   "+message[0]);

        // contrôle si le serveur a retourné une information
        if(message.length>0){
            // test si l'utilisateur a été authentifié
            if(message[0].equals("authentification-OK")){
                // retour suite à une authentification réussi: retour au menu et alert "succès"
                ((AuthActivity)contexte).envoiSucces();
                Log.d("retour", "***************   "+message[0]);

            }else if(message[0].equals("authentification-ERROR")){
                // retour suite à une erreur d'authentification: alert "echec"
                ((AuthActivity)contexte).envoiErreur();
                Log.d("retour", "***************   "+message[0]);

            }else if(message[0].equals("Erreur !")){
                // retour suite à une erreur
                Log.d("retour", "*************** erreur");
            }
        }

    }

    /**
     * Envoi d'informations vers le serveur distant
     * @param operation
     * @param lesFraisJSON
     */
    public void envoi(String operation, JSONArray authJSON, JSONArray lesFraisJSON){
        AccesHTTP accesDonnees = new AccesHTTP();
        // permet de faire le lien asynchrone avec AccesHTTP
        accesDonnees.delegate = this;
        // paramètres POST pour l'envoi vers le serveur distant
        accesDonnees.addParam("operation", operation);
        accesDonnees.addParam("auth", authJSON.toString());
        accesDonnees.addParam("lesdonnees", lesFraisJSON.toString());
        // appel du serveur
        accesDonnees.execute(SERVERADDR);
    }

}
