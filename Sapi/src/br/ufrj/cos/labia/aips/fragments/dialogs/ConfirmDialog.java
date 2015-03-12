package br.ufrj.cos.labia.aips.fragments.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class ConfirmDialog extends DialogFragment {

	public interface ConfirmDialogListener {
		public void onConfirm(ConfirmDialog dialog);
		public void onCancel(ConfirmDialog dialog);
	}
	
	private String message;

	private String title;

	private String confirmText;
	
	private String cancelText;
	
	protected ConfirmDialogListener mListener;

	public ConfirmDialogListener getListener() {
		return mListener;
	}

	public String getMessage() {
		return message;
	}

	public String getTitle() {
		return title;
	}

	public String getConfirmText() {
		return confirmText;
	}

	public String getCancelText() {
		return cancelText;
	}

	public ConfirmDialog setConfirmText(String text) {
		this.confirmText = text;
		return this;
	}

	public ConfirmDialog setCancelText(String text) {
		this.cancelText = text;
		return this;
	}

	public ConfirmDialog setMessage(String message) {
		this.message = message;
		return this;
	}

	public ConfirmDialog setTitle(String title) {
		this.title = title;
		return this;
	}

	public ConfirmDialog setListener(ConfirmDialogListener listener) {
		this.mListener = listener;
		return this;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		if (mListener == null)
			throw new IllegalStateException("Listener is not set");
		
		if (title == null)
			title = "Confirm";
		
		if (message == null)
			message = "Are you sure?";
		
		if (confirmText == null)
			confirmText = "OK";
		
		if (cancelText == null)
			cancelText = "Cancel";
		
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("New Room");
		builder.setMessage(message);
		builder.setTitle(title);
		
		builder.setPositiveButton(confirmText, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onConfirm(ConfirmDialog.this);
			}
		});
		
		builder.setNegativeButton(cancelText, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mListener.onCancel(ConfirmDialog.this);
			}
		});
		
		return builder.create(); 
	}
	
}
