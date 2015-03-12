package br.ufrj.cos.labia.aips.fragments;

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
import br.ufrj.cos.labia.aips.NavigationDrawerFragment;
import br.ufrj.cos.labia.aips.R;
import br.ufrj.cos.labia.aips.comm.BasicConnector;
import br.ufrj.cos.labia.aips.comm.CommunicationException;
import br.ufrj.cos.labia.aips.dal.Dispositivo;
import br.ufrj.cos.labia.aips.dal.SharedPrefManager;
import br.ufrj.cos.labia.aips.dto.DispositivoDTO;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Listener;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Worker;

public class RegistrarDispositivoFragment extends Fragment implements OnClickListener, Listener {

	public static RegistrarDispositivoFragment newInstance() {
        return new RegistrarDispositivoFragment();
    }

	private EditText mFdLogin;
	
	private EditText mFdSenha;
	
	private Button mBtRegistrar;

	private SharedPrefManager mSharedPrefManager;

	private Button mBtDesvincular;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_dispositivo, container, false);
        
        mFdLogin = (EditText) view.findViewById(R.id.fdLogin);
        mFdSenha = (EditText) view.findViewById(R.id.fdSenha);
        mBtRegistrar = (Button) view.findViewById(R.id.btRegistrar);
        mBtRegistrar.setOnClickListener(this);

        mSharedPrefManager = new SharedPrefManager(getActivity(), true);
        if (mSharedPrefManager.getIdDispositivo() == -1)
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
			String senha = mFdSenha.getText().toString();
			DispositivoDTO dispositivo = Dispositivo.getInfoDispositivo(getActivity());
			
			dispositivo.imei = new SharedPrefManager(getActivity(), false)
					.getImei(getActivity());  
			
			LoadingDialog dialog = new LoadingDialog();
			dialog.setWorker(new RegistrarWorker(login, senha, dispositivo));
			dialog.setListener(this);
			dialog.show(getFragmentManager(), "registrando");
		} else {
			mSharedPrefManager.setIdDispositivo(-1);
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
		private String mSenha;
		private DispositivoDTO mDispositivo;
		
		public RegistrarWorker(String login, String senha, DispositivoDTO dispositivo) {
			mLogin = login;
			mSenha = senha;
			mDispositivo = dispositivo;
		}

		@Override
		public void doHeavyWork(LoadingDialog dialog) {
			try {
				Thread.sleep(500);
				long idDispositivo = BasicConnector.registrarDispositivo(mLogin, mSenha, mDispositivo);
				mSharedPrefManager.setIdDispositivo(idDispositivo);
				mSharedPrefManager.save();
				Log.i("FragmentRegistrar", "Device is now registered with id " + idDispositivo);
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
