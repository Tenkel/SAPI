package com.tenkel.fragments;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import com.tenkel.sapi.dal.Andar;
import com.tenkel.sapi.dal.AndarManager;
import com.tenkel.sapi.dal.Posicao;
import com.tenkel.sapi.dal.PosicaoManager;
import com.tenkel.sapi.dal.SharedPrefManager;

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
	
	private int Entities[] = null;
	
	private String names[] = null;
	
	private AndarManager mAndarManager;
	
	private PosicaoManager mPosicaoManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_registrar_dispositivo, container, false);
        
        mFdLogin = (EditText) view.findViewById(R.id.fdLogin);
        mFdEmail = (EditText) view.findViewById(R.id.fdEmail);
        mFdSenha = (EditText) view.findViewById(R.id.fdSenha);
        mBtRegistrar = (Button) view.findViewById(R.id.btRegistrar);
        mBtRegistrar.setOnClickListener(this);
        mAndarManager = new AndarManager(getActivity());
        mPosicaoManager = new PosicaoManager(getActivity());
        mSharedPrefManager = new SharedPrefManager(getActivity(), true);
        if (mSharedPrefManager.getToken()==null)
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
			dialog.setWorker(new RegistrarWorker(this,login, email, senha, "Brasil"));
			dialog.setListener(this);
			dialog.show(getFragmentManager(), "registrando");
		} else {
			mSharedPrefManager.setToken(null);
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
		showRegisteredFrame(getView());
		w.Finished();
		
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
		private Listener mListener;
		
		public RegistrarWorker(Listener listener, String login, String email, String senha, String nomepais) {
			mLogin = login;
			mEmail = email;
			mSenha = senha;
			mNomePais = nomepais;
			mListener = listener;
		}

		@Override
		public void doHeavyWork(LoadingDialog dialog) {
			try {
				Thread.sleep(500);
				SoapObject response = BasicConnector.registrarDispositivo(mLogin, mEmail, mSenha, mNomePais);
				mSharedPrefManager.setPaisID(Long.parseLong(response.getProperty(2).toString()));
				mSharedPrefManager.setUserID(Integer.parseInt(response.getProperty(3).toString()));
				mSharedPrefManager.setToken(response.getProperty(4).toString());
				mSharedPrefManager.setDT(response.getProperty(5).toString());
				mSharedPrefManager.save();
				SoapObject shoppings = (SoapObject) response.getProperty(6);
				Entities = new int[shoppings.getPropertyCount()];
				names = new String[shoppings.getPropertyCount()];
				for (int i=0 ; i<shoppings.getPropertyCount() ; i++){
					SoapObject shopping = (SoapObject) shoppings.getProperty(i);
					Entities[i] = Integer.parseInt(shopping.getProperty(0).toString());
					names[i] = shopping.getProperty(1).toString();
				}
				//Log.i("FragmentRegistrar", "Device is now registered with id " + response.getProperty(3).toString());
			} catch (InterruptedException e) {
				Log.e("FragmentRegistrarTelefone", "Operação interrompida");
				e.printStackTrace();
			} catch (CommunicationException e) {
				e.printStackTrace();
				throw e;
			}
		}

		@Override
		public void Finished() {
			Toast.makeText(getActivity(), "Dados enviados", Toast.LENGTH_SHORT).show();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle(R.string.escshop)
		           .setItems(names, new DialogInterface.OnClickListener() {
		               @Override
					public void onClick(DialogInterface dialog, int which) {

		       			LoadingDialog dialog2 = new LoadingDialog();
		       			dialog2.setWorker(new AndarWorker(mSharedPrefManager.getToken(), mSharedPrefManager.getUserID(), Entities[which]));
		       			dialog2.setListener(mListener);
		       			dialog2.show(getFragmentManager(), "registrando");
		           }
		    });
		    builder.create();
		    builder.show();
			
		}
		
	}
	
	class AndarWorker implements Worker {
		
		private String mToken;
		private int mUserId;
		private int mMarcaId;
		
		public AndarWorker(String token, int UserId, int MarcaId) {
			mToken = token;
			mUserId = UserId;
			mMarcaId = MarcaId;
		}

		@Override
		public void doHeavyWork(LoadingDialog dialog) {
			try {
				Thread.sleep(500);
				SoapObject response = BasicConnector.registrarAndar(mToken, mUserId, mMarcaId);
				Andar mAndar;
				SoapObject andares = (SoapObject) response.getProperty(3);
				for (int i=0 ; i<andares.getPropertyCount() ; i++){
					SoapObject andar = (SoapObject) andares.getProperty(i);
					SoapObject response2 = BasicConnector.ListarPI(mToken, mUserId, mMarcaId, Integer.parseInt(andar.getProperty(0).toString()));
					mAndar = new Andar();
					mAndar.setNome(andar.getProperty(1).toString());
					mAndar.setIdRemoto(Long.parseLong(andar.getProperty(0).toString()));
					mAndarManager.save(mAndar);
					SoapObject PontosDeInteresse = (SoapObject) response2.getProperty(3);
					for (int j=0; j<PontosDeInteresse.getPropertyCount();j++){
						SoapObject PontoDeInteresse = (SoapObject) PontosDeInteresse.getProperty(j);
						Posicao mPosicao;
						mPosicao = new Posicao();
						mPosicao.setIdAndar(mAndar.getId());
						mPosicao.setIdRemoto(Long.parseLong(PontoDeInteresse.getProperty(6).toString()));
						mPosicao.setReferencia(PontoDeInteresse.getProperty(3).toString());
						mPosicao.setNome(PontoDeInteresse.getProperty(0).toString());
						mPosicao.setPropaganda(PontoDeInteresse.getProperty(5).toString());
						mPosicao.setX(Double.parseDouble(PontoDeInteresse.getProperty(1).toString()));
						mPosicao.setY(Double.parseDouble(PontoDeInteresse.getProperty(2).toString()));
						mPosicao.setAtivo(Boolean.parseBoolean(PontoDeInteresse.getProperty(4).toString()));
						if(!mPosicaoManager.save(mPosicao))
							mPosicaoManager.update(mPosicao);
					}
				}
			} catch (InterruptedException e) {
				Log.e("FragmentRegistrarTelefone", "Operação interrompida");
				e.printStackTrace();
			} catch (CommunicationException e) {
				e.printStackTrace();
				throw e;
			}
		}

		@Override
		public void Finished() {
			// TODO Auto-generated method stub
			
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
