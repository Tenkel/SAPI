package com.tenkel.fragments;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tenkel.comm.BasicConnector;
import com.tenkel.comm.CommunicationException;
import com.tenkel.sapi.NavigationDrawerFragment;
import com.tenkel.sapi.R;
import com.tenkel.sapi.R.layout;
import com.tenkel.sapi.dal.SharedPrefManager;

import br.ufrj.cos.labia.aips.dto.DispositivoDTO;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Listener;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Worker;

public class RegistrarDispositivoFragment extends Fragment implements OnClickListener, Listener {

	public static RegistrarDispositivoFragment newInstance() {
        return new RegistrarDispositivoFragment();
    }

	private EditText mFdLogin;
	
	private EditText mFdEmail;
	
	private EditText mFdSenha;
	
	private Button mBtRegistrar;

	private SharedPrefManager mSharedPrefManager;

	private Button mBtDesvincular;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_registrar_dispositivo, container, false);
        
        mFdLogin = (EditText) view.findViewById(R.id.fdLogin);
        mFdEmail = (EditText) view.findViewById(R.id.fdEmail);
        mFdSenha = (EditText) view.findViewById(R.id.fdSenha);
        mBtRegistrar = (Button) view.findViewById(R.id.btRegistrar);
        mBtRegistrar.setOnClickListener(this);

        mSharedPrefManager = new SharedPrefManager(getActivity(), true);
        if (mSharedPrefManager.geToken()==null)
        	showUnregisteredFrame(view);
        else
        	showRegisteredFrame(view);
		
        mBtDesvincular = (Button) view.findViewById(R.id.btDesvincular);
        mBtDesvincular.setOnClickListener(this);
        
        return view;
    }

	private void showUnregisteredFrame(View view) {
		view.findViewById(R.id.frame1).bringToFront();
	}

	private void showRegisteredFrame(View view) {
		view.findViewById(R.id.frame2).bringToFront();
	}

	@Override
	public void onClick(View v) {
		if (v == mBtRegistrar) {
			String login = mFdLogin.getText().toString();
			String email = mFdEmail.getText().toString();
			String senha = mFdSenha.getText().toString();
			/*DispositivoDTO dispositivo = com.tenkel.sapi.dal.Dispositivo.getInfoDispositivo(getActivity());
			
			dispositivo.imei = new SharedPrefManager(getActivity(), false)
					.getImei(getActivity());
			
			dispositivo.AndroidID = new SharedPrefManager(getActivity(), false)
					.getAndroidID(getActivity());
			*/
			LoadingDialog dialog = new LoadingDialog();
			dialog.setWorker(new RegistrarWorker(login, email, senha, "Brasil"));
			dialog.setListener(this);
			dialog.show(getFragmentManager(), "registrando");
		} else {
			mSharedPrefManager.setUserID(-1);
			mSharedPrefManager.save();
			showUnregisteredFrame(getView());

			NavigationDrawerFragment drawer = (NavigationDrawerFragment) 
					getFragmentManager().findFragmentById(R.id.navigation_drawer);
			drawer.updateAdapter();
		}
	}

	@Override
	public void onStart(Worker w) {
		
	}
	
	@Override
	public void onFinish(Worker w) {
		Toast.makeText(getActivity(), "Dados enviados", Toast.LENGTH_SHORT).show();
		showRegisteredFrame(getView());
		
		NavigationDrawerFragment drawer = (NavigationDrawerFragment) 
				getFragmentManager().findFragmentById(R.id.navigation_drawer);
		drawer.updateAdapter();
	}

	@Override
	public void onError(Worker w, Exception e) {
		e.printStackTrace();
		
		if (e.getMessage() == null)
			Toast.makeText(getActivity(), "Erro", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCancel(Worker w) {
		Toast.makeText(getActivity(), "Cancelado", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	class RegistrarWorker implements Worker {
		
		private String mLogin;
		private String mEmail;
		private String mSenha;
		private String mNomePais;
		
		public RegistrarWorker(String login, String email, String senha, String nomepais) {
			mLogin = login;
			mEmail = email;
			mSenha = senha;
			mNomePais = nomepais;
		}

		@Override
		public void doHeavyWork(LoadingDialog dialog) {
			try {
				Thread.sleep(500);
				SoapObject response = BasicConnector.registrarDispositivo(mLogin, mEmail, mSenha, mNomePais);
				mSharedPrefManager.setPaisID(Long.parseLong(response.getProperty(2).toString()));
				mSharedPrefManager.setUserID(Long.parseLong(response.getProperty(3).toString()));
				mSharedPrefManager.setToken(response.getProperty(4).toString());
				mSharedPrefManager.setDT(response.getProperty(5).toString());
				mSharedPrefManager.save();
				Log.i("FragmentRegistrar", "Device is now registered with id " + response.getProperty(3).toString());
			} catch (InterruptedException e) {
				Log.e("FragmentRegistrarTelefone", "Operação interrompida");
				e.printStackTrace();
			} catch (CommunicationException e) {
				e.printStackTrace();
				throw e;
			}
		}
		
	}
	
}
/*
      <Erro>int</Erro>
      <MensagemDeErro>string</MensagemDeErro>
      <PaisId>int</PaisId>
      <UsuarioEmpresaId>int</UsuarioEmpresaId>
      <Token>string</Token>
      <DataHoraServidor>dateTime</DataHoraServidor>
      <ListaDeShoppings>
        <ListaIdNome>
          <EntityId>int</EntityId>
          <EntityName>string</EntityName>
        </ListaIdNome>
        <ListaIdNome>
          <EntityId>int</EntityId>
          <EntityName>string</EntityName>
        </ListaIdNome>
      </ListaDeShoppings>
*/
