package com.tenkel.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Listener;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Worker;
import br.ufrj.cos.labia.aips.ips.IPS;
import br.ufrj.cos.labia.aips.ips.Location;
import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;

import com.tenkel.sapi.R;
import com.tenkel.sapi.dal.AccessPoint;
import com.tenkel.sapi.dal.AccessPointManager;
import com.tenkel.sapi.dal.Andar;
import com.tenkel.sapi.dal.AndarManager;
import com.tenkel.sapi.dal.Bridge;
import com.tenkel.sapi.dal.LeituraWiFi;
import com.tenkel.sapi.dal.LeituraWifiManager;
import com.tenkel.sapi.dal.Observacao;
import com.tenkel.sapi.dal.ObservacaoManager;
import com.tenkel.sapi.dal.Posicao;
import com.tenkel.sapi.dal.PosicaoManager;
import com.tenkel.sapi.kde.KDE;


public class Train extends Fragment {
	
	private IPS mIPS;
	private ToggleButton Predict;
	private Button Train;
	private ObservacaoManager mObservacaoManager;
	private AndarManager mAndarManager;
	private LeituraWifiManager mLeituraWIFIManager;
	private List<Andar> mAndares;
	private WifiReceiver mReceiver;
	private Map<Long, AccessPoint> mAccessPoints;
	private TextView aquisicoes;
	private TextView guess;
	private int cycles;
	
	public static Fragment newInstance() {
		return new Train();
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train, container, false);
        
        aquisicoes = (TextView) view.findViewById(R.id.aqc);
        guess = (TextView) view.findViewById(R.id.chute);
        Train = (Button) view.findViewById(R.id.buTrain);
        
        cycles = 0;
        	// Managers
     		mAndarManager = new AndarManager(getActivity());
     		mObservacaoManager = new ObservacaoManager(getActivity());
     		mLeituraWIFIManager = new LeituraWifiManager(getActivity());
     		
     		mAndares = mAndarManager.getAllWithNullIdLocal();
     		// Ouvinte para os eventos do Bridge
     		mReceiver = new WifiReceiver();
        
        Predict = (ToggleButton) view.findViewById(R.id.predict);
        Predict.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Train.setClickable(false);
					mReceiver.start();
					Bridge.start(getActivity());
				} 
				else {
					Bridge.stop(getActivity());
					mReceiver.stop();
					Train.setClickable(true);
				}

			}
		});
        
		Train.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new LoadingDialog()
				.setWorker(new Worker() {
					@Override
					public void doHeavyWork(LoadingDialog dialog) {
						trainModel();
					}
				})
				.show(getFragmentManager(), "training_model");
		}
		});

        return view;
	}
	
	public void onReceive(Context context, Intent intent) {
		Log.i("RoomActivity", "Processing event from Bridge");
		
		long idObservacao = intent.getLongExtra("idObservacao", -1);
		if (idObservacao == -1) {
			Log.e("DebugRoomActivity", "Bridge did not send idObservacao, as expected");
			return;
		}
		
		Object[] objects = (Object[]) intent.getSerializableExtra("wifi");
		ScanResult[] results = new ScanResult[objects.length];
		for (int i=0; i<objects.length; ++i) 
			results[i] = (ScanResult) objects[i];
		
		
		predictPosition(results);
	}
	
	private void predictPosition(ScanResult[] results) {
		if (mIPS == null) 
			return;
		
		List<WIFISignal> signals = new ArrayList<WIFISignal>();
		for (ScanResult r : results) 
			signals.add(new WIFISignal(r.BSSID, r.level));
		
		Reading reading = new Reading(signals);
		Location l = mIPS.predict(reading);
		
		if (l == null || l.getPointId() == null)  {
			Log.w("DebugRoomActivity", "IPS returned null as prediction");
			return;
		}
		
		else guess.setText(String.valueOf(l.getPointId()));

	}
	
	private void trainModel() {
		for (Andar andar : mAndares){
		List<Observacao> samples = mObservacaoManager.getByIdAndar(andar.getId());
		
		List<WIFISignal> all = new ArrayList<WIFISignal>();
		List<List<WIFISignal>> internal = new ArrayList<List<WIFISignal>>();
		
		for (Observacao s : samples) {
			List<LeituraWiFi> signals = mLeituraWIFIManager.getByidObservacao(s.getId());
			List<WIFISignal> signals2 = new ArrayList<WIFISignal>();
			
			for (LeituraWiFi s2 : signals) {
				AccessPoint ap = mAccessPoints.get(s2.getIdAccessPoint());
				WIFISignal s3 = new WIFISignal(ap.getbssid(), s2.getValor());
				signals2.add(s3);
				all.add(s3);
			}
			
			internal.add(signals2);
		}
		
		if (all.size() == 0) {
			Toast.makeText(getActivity(), "No signals detected", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (mIPS != null) mIPS.close();
		IPS ips = new KDE();
		
		int i = 0;
		for (Observacao s : samples) {
			ips.learn(new Reading(internal.get(i++)), new Location(s.getIdPosicao()));
		}
		
		mIPS = ips;
		}
	}

	public class WifiReceiver extends BroadcastReceiver {
		public void start(){
			getActivity().registerReceiver(this, new IntentFilter(
					Bridge.BROADCAST_SENSOR_DATA));
		}
		
		public void stop(){
			getActivity().unregisterReceiver(this);
		}


		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i("RoomActivity", "Processing event from Bridge");
			
			long idObservacao = intent.getLongExtra("idObservacao", -1);
			if (idObservacao == -1) {
				Log.e("DebugRoomActivity", "Bridge did not send idObservacao, as expected");
				return;
			}
			
			aquisicoes.setText(String.valueOf(++cycles));
			
			Object[] objects = (Object[]) intent.getSerializableExtra("wifi");
			ScanResult[] results = new ScanResult[objects.length];
			for (int i=0; i<objects.length; ++i) 
				results[i] = (ScanResult) objects[i];
			
			predictPosition(results);
		}
	}

	
	
}
