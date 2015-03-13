package com.tenkel.fragments;

import com.tenkel.sapi.dal.Bridge;

import com.tenkel.sapi.R;
import com.tenkel.sapi.dal.AndarManager;
import com.tenkel.sapi.dal.LeituraWifiManager;
import com.tenkel.sapi.dal.ObservacaoManager;
import com.tenkel.sapi.dal.PosicaoManager;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

public class AutoScanFragment extends Fragment {

	private Button captura;
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
					chrono.stop();
					LoopBar.setVisibility(View.INVISIBLE);
				}

			}
		});

        return view;
	}

}
