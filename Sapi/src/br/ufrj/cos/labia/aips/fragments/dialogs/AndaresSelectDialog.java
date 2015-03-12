package br.ufrj.cos.labia.aips.fragments.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import br.ufrj.cos.labia.aips.dal.Andar;
import br.ufrj.cos.labia.aips.dal.AndarManager;

public class AndaresSelectDialog extends DialogFragment {

	public interface OnAndaresSelectedListener {
		void onSelectAndares(List<Andar> andares, int key);
	}

	private ArrayList<Integer> mSelected;
	private OnAndaresSelectedListener mListener;
	private List<Andar> mAndares;
	private int mKey;

	public void setListener(OnAndaresSelectedListener listener) {
		mListener = listener;
	}

	public void setKey(int key) {
		mKey = key;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		setRetainInstance(true);
		
		mAndares = new AndarManager(getActivity()).getAllWithNullIdLocal();
		CharSequence[] titles = detectClasses();
		mSelected = new ArrayList<Integer>();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Selecione as salas")
				.setMultiChoiceItems(titles, null,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) {
									mSelected.add(Integer.valueOf(which));
								} else if (mSelected.contains(which)) {
									mSelected.remove(Integer.valueOf(which));
								}
							}
						})

				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<Andar> andares = new ArrayList<Andar>();
						for (Integer i : mSelected)
							andares.add(mAndares.get(i));
						
						if (andares.size() < 2) {
							Toast.makeText(getActivity(), "Você deve selecionar pelo menos dois andares", 
									Toast.LENGTH_SHORT).show();
						} else { 
							dismiss();
							mListener.onSelectAndares(andares, mKey);
						}
					}
				})

				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dismiss();
							}
						});

		return builder.create();
	}

	private CharSequence[] detectClasses() {
		int i=0;
		CharSequence[] names = new CharSequence[mAndares.size()];
		for (Andar andar : mAndares) {
			names[i++] = andar.getNome();
		}
		
		return names;
	}

}
