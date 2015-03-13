package com.tenkel.fragments;

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

import com.tenkel.sapi.R;
import com.tenkel.sapi.dal.AccessPointManager;
import com.tenkel.sapi.dal.AndarManager;
import com.tenkel.sapi.dal.Bridge;
import com.tenkel.sapi.dal.LeituraWifiManager;
import com.tenkel.sapi.dal.Observacao;
import com.tenkel.sapi.dal.ObservacaoManager;
import com.tenkel.sapi.dal.PosicaoManager;

public class AutoScanFragment extends Fragment {

	private AndarManager mAndarManager;
	private PosicaoManager mPosicaoManager;
	private ObservacaoManager mObservacaoManager;
	private LeituraWifiManager mLeituraWIFIManager;
	private AccessPointManager mAccessPointManager;
	
	
	private NumberPicker Room;
	private ProgressBar LoopBar;

	// Dialogs
	private Chronometer chrono;
	
    private ToggleButton Collect;

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
			
			if (mState <= 0) {
				if (!mAndarView.isEnabled()) {
					Observacao obs = mObservacaoManager.getById(idObservacao);
					obs.setidPosicao(mPosicaoIds[mAndarView.getSelectedX()][mAndarView.getSelectedY()]);
					mObservacaoManager.update(obs);
					
					mAndarView.addCollected(mAndarView.getSelectedX(), mAndarView.getSelectedY());
				}
			} else {
				mState--;
			}
		}
	}

}
