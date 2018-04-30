package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.cned.emdsgil.suividevosfrais.modele.AccesDistant;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setTitle("GSB : Validation des frais");
        // chargement des méthodes événementielles
        cmdValiderEnvoyer_clic() ;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.retour_accueil))) {
            retourActivityPrincipale() ;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sur le clic du bouton "valider et envoyer" : envoi des informations au serveur
     */
    private void cmdValiderEnvoyer_clic() {
        findViewById(R.id.cmdValiderEnvoyer).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                AccesDistant accesDistant = new AccesDistant(AuthActivity.this);
                Log.d("auth", "*********************** authJSON : " + getAuthJSON().toString());
                Log.d("lesFrais", "*********************** les fraisJSON : " + getLesFraisJSON().toString());
                // envoi des données au serveur
                accesDistant.envoi("enreg", getAuthJSON(), getLesFraisJSON());
            }
        }) ;
    }

    /**
     * retourne l'idVisiteur et le mdp au format JSON
     * @return JSONArray
     */
    private JSONArray getAuthJSON(){
        String idVisiteur = ((EditText)findViewById(R.id.txtLogin)).getText().toString() ;
        String mdp = ((EditText)findViewById(R.id.txtMdp)).getText().toString() ;
        List laListe = new ArrayList();
        laListe.add(idVisiteur);
        laListe.add(mdp);
        return new JSONArray(laListe);
    }

    /**
     * retourne toutes les informations de frais sérializées au format JSON
     * @return JSONObject
     */
    private JSONArray getLesFraisJSON(){
        List listeMois = new ArrayList();
        Set<Integer> keys = Global.listFraisMois.keySet();
        for (Integer unMois: keys) {
            List listeFraisDuMois = new ArrayList();
            listeFraisDuMois.add(unMois);
            // ajout de chaque frais forfait dans la liste du mois
            listeFraisDuMois.add(Global.listFraisMois.get(unMois).getEtape());
            listeFraisDuMois.add(Global.listFraisMois.get(unMois).getKm());
            listeFraisDuMois.add(Global.listFraisMois.get(unMois).getNuitee());
            listeFraisDuMois.add(Global.listFraisMois.get(unMois).getRepas());

            List listeLesFraisHf = new ArrayList();

            for (FraisHf unFraisHf : Global.listFraisMois.get(unMois).getLesFraisHf()){
                List listeInfosDuFraisHf = new ArrayList();
                listeInfosDuFraisHf.add(unFraisHf.getJour());
                listeInfosDuFraisHf.add(unFraisHf.getMontant());
                listeInfosDuFraisHf.add(unFraisHf.getMotif());
                // ajout de la liste "listeInfosDuFraisHf" dans la liste "listeLesFraisHf"
                listeLesFraisHf.add(listeInfosDuFraisHf);
            }
            // ajout de la liste "listeLesFraisHf" dans la liste "listeFraisDuMois"
            listeFraisDuMois.add(listeLesFraisHf);


            // ajout de la liste "listeFraisDuMois" dans la liste "listeMois"
            listeMois.add(listeFraisDuMois);
        }
        return new JSONArray(listeMois);
    }

    /**
     * Retour à l'activité principale (le menu)
     */
    public void retourActivityPrincipale() {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class) ;
        // pour éviter de garder les activities en mémoires
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent) ;
    }

    /**
     * Retour à l'activité principale (le menu) et affichage du message "Frais pris en compte"
     */
    public void envoiSucces(){
        Toast.makeText(this, "Les frais ont été pris en compte.", Toast.LENGTH_LONG).show();
        // attend 2 sec, le temps de voir le message
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // retour au menu au bout de 3sec
                retourActivityPrincipale();
            }
        }, 3000);

    }

    /**
     * affiche un message "Erreur d'authentification"
     */
    public void envoiErreur(){
        Toast.makeText(this, "Identifiant ou mot de passe incorrecte.", Toast.LENGTH_LONG).show();
    }
}
