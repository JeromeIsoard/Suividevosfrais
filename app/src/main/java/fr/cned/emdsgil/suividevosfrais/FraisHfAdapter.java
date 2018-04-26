package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

class FraisHfAdapter extends BaseAdapter {

	private final ArrayList<FraisHf> lesFrais ; // liste des fraisHf du mois
	private final LayoutInflater inflater ; // permet de formater la ligne
	private final Integer key ; // le mois correspondant aux FraisHf en question

    /**
	 * Constructeur de l'adapter pour valoriser les propriétés
     * @param context Accès au contexte de l'application
     * @param lesFrais Liste des frais hors forfait
     */
	public FraisHfAdapter(Context context, ArrayList<FraisHf> lesFrais, Integer key) {
		inflater = LayoutInflater.from(context) ;
		this.lesFrais = lesFrais ;
		this.key = key;

    }
	
	/**
	 * retourne le nombre d'éléments de la listview
	 */
	@Override
	public int getCount() {
		return lesFrais.size() ;
	}

	/**
	 * retourne l'item de la listview à un index précis
	 */
	@Override
	public Object getItem(int index) {
		return lesFrais.get(index) ;
	}

	/**
	 * retourne l'index de l'élément actuel
	 */
	@Override
	public long getItemId(int index) {
		return index;
	}

	/**
	 * structure contenant les éléments d'une ligne
	 * ("sous-classe": propriété privée sous forme de classe)
	 */
	private class ViewHolder {
		ImageButton cmdSuppHf ;
		TextView txtListJour ;
		TextView txtListMontant ;
		TextView txtListMotif ;
	}
	
	/**
	 * Affichage dans la liste
	 */
	@Override
	public View getView(int index, View convertView, final ViewGroup parent) {
		ViewHolder holder ;
		if (convertView == null) { // si la ligne n'existe pas, elle est créée
			holder = new ViewHolder() ;
			// la ligne est construite avec un formatage (inflater) relié à layout_liste
			convertView = inflater.inflate(R.layout.layout_liste, parent, false) ;
			// chaque propriété du holder est reliée à une propriété graphique
			holder.cmdSuppHf = convertView.findViewById(R.id.cmdSuppHf);
			holder.txtListJour = convertView.findViewById(R.id.txtListJour);
			holder.txtListMontant = convertView.findViewById(R.id.txtListMontant);
			holder.txtListMotif = convertView.findViewById(R.id.txtListMotif);
			// affecter le holder à la vue, la ligne est construite
			convertView.setTag(holder) ;
		}else{ // sinon, la ligne existe et elle est récupérée
			holder = (ViewHolder)convertView.getTag();
		}
		// valorisation du contenu du holder (donc de la ligne)
		holder.cmdSuppHf.setTag(index); // pour savoir à quelle ligne correspond le bouton
		holder.txtListJour.setText(String.format(Locale.FRANCE, "%d", lesFrais.get(index).getJour()));
		holder.txtListMontant.setText(String.format(Locale.FRANCE, "%.2f", lesFrais.get(index).getMontant())) ;
		holder.txtListMotif.setText(lesFrais.get(index).getMotif()) ;
		// clic sur la croix pour supprimer le frais
		holder.cmdSuppHf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// récupération de l'index de la ligne et du contexte
				int position = (int)v.getTag();
				Context contexte = parent.getContext();
//				Log.d("contexte", "*************************** le contexte : " + contexte);
//				Log.d("index", "*************************** l'index du fraishF : " + position);
				
				// suppression du fraisHf correspondant à l'index de la ligne
				lesFrais.remove(position);
				Global.listFraisMois.get(key).supprFraisHf(position);
				Serializer.serialize(Global.listFraisMois, contexte);
				// raffraichissement de la vue
				notifyDataSetChanged();
			}
		});

		return convertView ;
	}
	
}
