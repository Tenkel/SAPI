package br.ufrj.cos.labia.aips.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tenkel.sapi.R;
import com.tenkel.sapi.dal.Bridge;
import com.tenkel.sapi.dal.Dispositivo;
import com.tenkel.sapi.dal.SharedPrefManager;

import br.ufrj.cos.labia.aips.comm.BasicConnector;
import br.ufrj.cos.labia.aips.comm.CommunicationException;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Listener;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Worker;

public class PainelDeControleFragment extends Fragment implements Listener {

	public static PainelDeControleFragment newInstance() {
        return new PainelDeControleFragment();
    }

	private SharedPrefManager mSharedPrefManager;
	private EditText mFieldImei;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_painel_de_controle, container, false);

        // Botão para iniciar a captura
		Button btStart = (Button) view.findViewById(R.id.btIniciar);
		btStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bridge.start(getActivity());
			}
		});

        // Botão para interromper a captura
		Button btStop = (Button) view.findViewById(R.id.btInterromper);
		btStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bridge.stop(getActivity());
			}
		});

        // Botão para iniciar o monitoramento 
		Button btLigarMonitoramento = (Button) view.findViewById(R.id.btLigarMonitoramento);
		btLigarMonitoramento.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ligarMonitoramento();
			}
		});

        // Botão para interromper o monitoramento 
		Button btDesligarMonitoramento = (Button) view.findViewById(R.id.btDesligarMonitoramento);
		btDesligarMonitoramento.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				desligarMonitoramento();
			}
		});
		
		mFieldImei = (EditText) view.findViewById(R.id.fdImei);
		mSharedPrefManager = new SharedPrefManager(getActivity().getApplicationContext(), true);
		String imei = mSharedPrefManager.getImei(getActivity());
		
		if (imei == null) {
			imei = Dispositivo.getImei(getActivity());
			mSharedPrefManager.setImei(imei);
		}
		
		mFieldImei.setText(imei);
		mFieldImei.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String imei = mFieldImei.getText().toString().trim();
					if (imei.equals("")) imei = Dispositivo.getImei(getActivity());
					Log.i("ControleServico", "Updating imei to " + imei);
					mFieldImei.setText(imei);
					
					mSharedPrefManager.setImei(imei);
					mSharedPrefManager.save();
				}
			}
		});
		
        return view;
    }

	private void ligarMonitoramento() {
		long idDispositivo = new SharedPrefManager(getActivity(), 
				false).getIdDispositivo();
		
		LoadingDialog dialog = new LoadingDialog();
		dialog.setWorker(new LigarMonitoramentoWorker(idDispositivo));
		dialog.setListener(this);
		dialog.show(getFragmentManager(), "registrando");
	}

	private void desligarMonitoramento() {
		long idDispositivo = new SharedPrefManager(getActivity(), 
				false).getIdDispositivo();
		
		LoadingDialog dialog = new LoadingDialog();
		dialog.setWorker(new DesligarMonitoramentoWorker(idDispositivo));
		dialog.setListener(this);
		dialog.show(getFragmentManager(), "registrando");
	}

	@Override
	public void onStart(Worker w) {
		
	}

	@Override
	public void onFinish(Worker w) {
		if (getActivity() != null)
			Toast.makeText(getActivity(), "Operação completada", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onError(Worker w, Exception e) {
		if (getActivity() != null)
			Toast.makeText(getActivity(), "Erro na operação", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCancel(Worker w) {
		if (getActivity() != null)
			Toast.makeText(getActivity(), "Operação cancelada", Toast.LENGTH_SHORT).show();
	}
	
	class LigarMonitoramentoWorker implements Worker {
		
		private long mIdDispositivo;

		public LigarMonitoramentoWorker(long idDispositivo) {
			mIdDispositivo = idDispositivo;
		}
		
		@Override
		public void doHeavyWork(LoadingDialog dialog) {
			try {
				Thread.sleep(500);
				BasicConnector.ligarMonitoramento(mIdDispositivo);
				Log.i("Controleservico", "Success");
			} catch (InterruptedException e) {
				Log.e("ControleServico", "Operação interrompida");
			} catch (CommunicationException e) {
				throw e;
			}
		}
		
	}

	class DesligarMonitoramentoWorker implements Worker {
		
		private long mIdDispositivo;

		public DesligarMonitoramentoWorker(long idDispositivo) {
			mIdDispositivo = idDispositivo;
		}
		
		@Override
		public void doHeavyWork(LoadingDialog dialog) {
			try {
				Thread.sleep(500);
				BasicConnector.desligarMonitoramento(mIdDispositivo);
				Log.i("Controleservico", "Success");
			} catch (InterruptedException e) {
				Log.e("ControleServico", "Operação interrompida");
			} catch (CommunicationException e) {
				throw e;
			}
		}
		
	}

}

