package br.ufrj.cos.labia.aips.fragments.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tenkel.sapi.R;
import com.tenkel.sapi.dal.Andar;

public class NewRoomDialog extends DialogFragment implements OnClickListener {

	public interface NewRoomDialogListener {
		public void onCreateAndar(Andar andar, int width, int height);

		public void onCreateAndarCancelled();
	}

	private NewRoomDialogListener mListener;
	private TextView mNome;
	private TextView mReferencia;
	private EditText mLargura;
	private EditText mAltura;
	private AlertDialog mDialog;
	private Integer mWidth;
	private Integer mHeight;

	public void setListener(NewRoomDialogListener listener) {
		mListener = listener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);

		if (mListener == null)
			throw new IllegalStateException("Listener is not set");

		View view = getActivity().getLayoutInflater().inflate(
				R.layout.dialogfragment_novoandar, null);

		mNome = (TextView) view.findViewById(R.id.campoNome);
		mReferencia = (TextView) view.findViewById(R.id.campoReferencia);
		mLargura = (EditText) view.findViewById(R.id.campoLargura);
		mAltura = (EditText) view.findViewById(R.id.campoAltura);

		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setPositiveButton("OK", null);
		builder.setTitle("Novo Andar");
		builder.setView(view);
		
		if (mWidth != null) {
			mLargura.setText(mWidth.toString());
			mLargura.setEnabled(false);
		}

		if (mHeight != null) {
			mAltura.setText(mHeight.toString());
			mAltura.setEnabled(false);
		}
		
		mDialog = builder.create();
		mDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button btOK = mDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				btOK.setOnClickListener(NewRoomDialog.this);
			}
		});
		
		return mDialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		mListener.onCreateAndarCancelled();
	}
	
	@Override
	public void onClick(View v) {
		String name      = mNome.getText().toString().trim();
		String reference = mReferencia.getText().toString().trim();
		
		int width, height;
		
		try {
			width = Integer.parseInt(mLargura.getText().toString());
		} catch (NumberFormatException e) {
			Toast.makeText(getActivity(), "Valor inválido para a Largura", Toast.LENGTH_SHORT).show();
			return;
		}
		
		try {	
			height = Integer.parseInt(mAltura.getText().toString());
		} catch (NumberFormatException e) {
			Toast.makeText(getActivity(), "Valor inválido para a Altura", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (name.equals("")) {
			Toast.makeText(getActivity(), "Nome não pode ser vazio", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Andar andar = new Andar();
		andar.setNome(name);
		andar.setURIMapa(reference);
		andar.setCamadas(width + "_" + height);
		
		mListener.onCreateAndar(andar, width, height);
		dismiss();
	}

	public void setRoomSize(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

}
