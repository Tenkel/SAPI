package com.tenkel.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.ToggleButton;
import br.ufrj.cos.labia.aips.ips.IPS;
import br.ufrj.cos.labia.aips.ips.Location;
import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;

import com.tenkel.sapi.R;
import com.tenkel.sapi.dal.AccessPointManager;
import com.tenkel.sapi.dal.AndarManager;
import com.tenkel.sapi.dal.Bridge;
import com.tenkel.sapi.dal.LeituraWifiManager;
import com.tenkel.sapi.dal.Observacao;
import com.tenkel.sapi.dal.ObservacaoManager;
import com.tenkel.sapi.dal.Posicao;
import com.tenkel.sapi.dal.PosicaoManager;

public class AutoScanFragment extends Fragment {

	@Override
	public void onPause() {
		chrono.stop();
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private AndarManager mAndarManager;
	private PosicaoManager mPosicaoManager;
	private ObservacaoManager mObservacaoManager;
	private LeituraWifiManager mLeituraWIFIManager;
	private AccessPointManager mAccessPointManager;
	private TreeMap<Long, Posicao> mPosicoes;
	
	
	private NumberPicker Room;
	private ProgressBar LoopBar;
	
	private IPS mIPS;

	// Dialogs
	private Chronometer chrono;
	
    private ToggleButton Collect;

	public static Fragment newInstance() {
		return new AutoScanFragment();
	}
    
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_autoscan, container, false);

		LoopBar = (ProgressBar) view.findViewById(R.id.captura_progress);
		
		Room = (NumberPicker) view.findViewById(R.id.local);
		Room.setMaxValue(100);
		Room.setMinValue(1);		

		chrono = (Chronometer) view.findViewById(R.id.tempo);

		Collect = (ToggleButton) view.findViewById(R.id.captura);
		Collect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					chrono.setBase(SystemClock.elapsedRealtime());
					chrono.start();
					Bridge.start(getActivity());
					LoopBar.setVisibility(View.VISIBLE);
				} 
				else {
					Bridge.stop(getActivity());
					chrono.stop();
					LoopBar.setVisibility(View.INVISIBLE);
				}

			}
		});

        return view;
	}
	
	public class WifiReceiver2 extends BroadcastReceiver {

		@Override
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
			
			Observacao obs = mObservacaoManager.getById(idObservacao);
			obs.setidPosicao(mPosicaoIds[mAndarView.getSelectedX()][mAndarView.getSelectedY()]);
			mObservacaoManager.update(obs);
			
			mAndarView.addCollected(mAndarView.getSelectedX(), mAndarView.getSelectedY());
		}
	}
	
	
	private Long predictPosition(ScanResult[] results) {
		if (mIPS == null) 
			return null;
		
		List<WIFISignal> signals = new ArrayList<WIFISignal>();
		for (ScanResult r : results) 
			signals.add(new WIFISignal(r.BSSID, r.level));
		
		Reading reading = new Reading(signals);
		Location l = mIPS.predict(reading);
		
		if (l == null || l.getPointId() == null)  {
			Log.w("DebugRoomActivity", "IPS returned null as prediction");
			return null;
		}
		
		Posicao p = mPosicoes.get(l.getPointId());
		return p.getId();

	}

}
