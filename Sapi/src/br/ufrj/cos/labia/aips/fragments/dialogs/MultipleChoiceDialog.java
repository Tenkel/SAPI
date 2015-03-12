package br.ufrj.cos.labia.aips.fragments.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class MultipleChoiceDialog extends DialogFragment {

	public interface MultipleChoiceDialogListener {
		void onSelect(MultipleChoiceDialog dialog, List<Integer> selections);
		void onCancel(MultipleChoiceDialog dialog);
	}
	
	private MultipleChoiceDialogListener mListener;
	
	private List<Integer> mSelected;
	
	private String[] mItems;

	private String mTitle;
	
	private String mTitleConfirm;
	
	private String mTitleCancel;
	
	public void setListener(MultipleChoiceDialogListener listener) {
		mListener = listener;
	}

	public void setItems(String[] items) {
		mItems = items;
	}
	
	public void setTitle(String title) {
		mTitle = title;
	}
	
	public void setTitleConfirm(String title) {
		mTitleConfirm = title;
	}

	public void setTitleCancel(String title) {
		mTitleCancel = title;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		setRetainInstance(true);

		if (mListener == null) 
			throw new IllegalStateException("Listener was not set");

		if (mTitle == null) 
			throw new IllegalStateException("Title was not set");

		if (mItems == null) 
			throw new IllegalStateException("Items were not set");

		if (mTitleCancel == null)
			mTitleCancel = "Cancel";

		if (mTitleConfirm == null)
			mTitleConfirm = "OK";
		
		mSelected = new ArrayList<Integer>();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(mTitle)
				.setMultiChoiceItems(mItems, null,
						new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								if (isChecked) 
									mSelected.add(Integer.valueOf(which));
								else if (mSelected.contains(which))
									mSelected.remove(Integer.valueOf(which));
							}
						})

				.setPositiveButton(mTitleConfirm, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mListener.onSelect(MultipleChoiceDialog.this, mSelected);
					}
				})

				.setNegativeButton(mTitleCancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mListener.onCancel(MultipleChoiceDialog.this);
							}
						});

		return builder.create();
	}
}
