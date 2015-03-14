package com.tenkel.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
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
import com.tenkel.sapi.dal.FuncaoPosicao;
import com.tenkel.sapi.dal.FuncaoPosicaoManager;
import com.tenkel.sapi.dal.LeituraWiFi;
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
	private FuncaoPosicaoManager mFuncaoPosicaoManager;
	private LeituraWifiManager mLeituraWIFIManager;
	private AccessPointManager mAccessPointManager;
	//private TreeMap<Long, Posicao> mPosicoes;
	private List<Posicao> mPosicoes;
	//private TreeMap<Long, Andar> mAndares;
	private List<Andar> mAndares;
	
	private Posicao actual_posicao;
	private Andar actual_andar;
	
	private NumberPicker Room;
	private NumberPicker andar;
	private ProgressBar LoopBar;
	
	private ImageButton addandar;
	private ImageButton addposicao;
	
	private TextView aquisicoes;
	private int naquisicoes;
	private TextView naps;
	private int nnaps;
	private TextView maxpower;
	private long nmaxpower;
		
	private IPS mIPS;
	private AutoWifiReceiver receiver;

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
		naquisicoes = 0;
        View view = inflater.inflate(R.layout.fragment_autoscan, container, false);

		// Managers
        mFuncaoPosicaoManager = new FuncaoPosicaoManager(getActivity());
		mAndarManager = new AndarManager(getActivity());
		mPosicaoManager = new PosicaoManager(getActivity());
		mObservacaoManager = new ObservacaoManager(getActivity());
		mLeituraWIFIManager = new LeituraWifiManager(getActivity());
		mAccessPointManager = new AccessPointManager(getActivity());

		LoopBar = (ProgressBar) view.findViewById(R.id.captura_progress);
		aquisicoes = (TextView) view.findViewById(R.id.aquisicoes);
		naps = (TextView) view.findViewById(R.id.naps);
		maxpower = (TextView) view.findViewById(R.id.maxpower);
		andar = (NumberPicker) view.findViewById(R.id.nandar);
		Room = (NumberPicker) view.findViewById(R.id.local);
		chrono = (Chronometer) view.findViewById(R.id.tempo);
		Collect = (ToggleButton) view.findViewById(R.id.captura);
		addandar = (ImageButton) view.findViewById(R.id.addAndar);
		addposicao = (ImageButton) view.findViewById(R.id.addPosicao);
		
		receiver = new AutoWifiReceiver();
		
		andar.setMinValue(1);	
		Room.setMinValue(1);	
		
		andar.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		Room.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		mAndares = mAndarManager.getAllWithNullIdLocal();
		int nandar;
		if ((nandar = mAndares.size()) < 1){
			actual_andar = new Andar();
			actual_andar.setNome("Andar Atual");
			mAndarManager.save(actual_andar);
			andar.setMaxValue(1);	
			andar.setValue(1);
			mAndares.add(actual_andar);
			
			actual_posicao = new Posicao();
			actual_posicao.setIdAndar(actual_andar.getId());
			actual_posicao.setIdRemoto((long) 1);
			mPosicaoManager.save(actual_posicao);
			Room.setMaxValue(1);
			Room.setValue(1);
			mPosicoes = new ArrayList<Posicao>();
			mPosicoes.add(actual_posicao);
		}
		else{			
			actual_andar = mAndares.get(nandar-1);
			andar.setMaxValue(nandar);	
			andar.setValue(nandar);

			int nroom;
			mPosicoes = mPosicaoManager.getByidAndar(actual_andar.getId());
			actual_posicao = mPosicoes.get((nroom = mPosicoes.size())-1);
			Room.setMaxValue(nroom);
			Room.setValue(nroom);
		}

		andar.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {			
				actual_andar = mAndares.get(newVal-1);
				mPosicoes = mPosicaoManager.getByidAndar(actual_andar.getId());
				int nroom;
				actual_posicao = mPosicoes.get((nroom = mPosicoes.size())-1);
				Room.setMaxValue(nroom);
				Room.setValue(nroom);
				FillActualData();
			}
		});
		
		Room.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				actual_posicao = mPosicoes.get(newVal-1);
				FillActualData();
			}
		});
		
		
		Collect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					
					chrono.setBase(SystemClock.elapsedRealtime());
					chrono.start();
					receiver.start();
					Bridge.start(getActivity());
					
					LoopBar.setVisibility(View.VISIBLE);
				} 
				else {
					Bridge.stop(getActivity());
					receiver.stop();
					chrono.stop();
					LoopBar.setVisibility(View.INVISIBLE);
				}

			}
		});

		addandar.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int nandar;
				actual_andar = new Andar();
				actual_andar.setNome("Andar Atual");
				mAndarManager.save(actual_andar);
				mAndares.add(actual_andar);
				andar.setMaxValue(nandar = mAndares.size());
				andar.setValue(nandar);
				
				actual_posicao = new Posicao();
				actual_posicao.setIdAndar(actual_andar.getId());
				actual_posicao.setIdRemoto((long) 1);
				mPosicaoManager.save(actual_posicao);
				Room.setValue(1);
				Room.setMaxValue(1);
				mPosicoes = new ArrayList<Posicao>();
				mPosicoes.add(actual_posicao);
				FillActualData();
			}
		});
		
		addposicao.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int nroom;
				actual_posicao = new Posicao();
				actual_posicao.setIdAndar(actual_andar.getId());
				actual_posicao.setIdRemoto((long)(nroom = (mPosicoes.size()+1)));
				mPosicaoManager.save(actual_posicao);
				Room.setMaxValue(nroom);
				Room.setValue(nroom);
				mPosicoes.add(actual_posicao);
				FillActualData();
			}
		});

		FillActualData();
		
        return view;
	}
	
	private void FillActualData(){
		if((nmaxpower = mLeituraWIFIManager.getMaxValueByidPosicao(actual_posicao.getId())) == 0){
			maxpower.setText("N/A");
			nmaxpower = Long.MIN_VALUE;
			}
		else
			maxpower.setText(String.valueOf(nmaxpower = mLeituraWIFIManager.getMaxValueByidPosicao(actual_posicao.getId())));
		naps.setText(String.valueOf(nnaps = mAccessPointManager.getByFuncaoPosicaoIdPosicao(actual_posicao.getId()).size()));
		aquisicoes.setText(String.valueOf(naquisicoes = mObservacaoManager.getByidPosicao(actual_posicao.getId()).size()));				
	}
	
	public class AutoWifiReceiver extends BroadcastReceiver {
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
			
			aquisicoes.setText(String.valueOf(++naquisicoes));
			
			Object[] objects = (Object[]) intent.getSerializableExtra("wifi");
			int level;
			
			for (int i=0; i<objects.length; ++i)
				if((level = ((ScanResult) objects[i]).level) > nmaxpower)
					nmaxpower = level;
			
			
			maxpower.setText(String.valueOf((int)nmaxpower));
			
			for(AccessPoint ap :mAccessPointManager.getByIdObservacao(idObservacao))
				mFuncaoPosicaoManager.save(new FuncaoPosicao(actual_posicao.getId(), ap.getId(), null, null));				
			
			Observacao obs = mObservacaoManager.getById(idObservacao);
			obs.setidPosicao(actual_posicao.getId());
			mObservacaoManager.update(obs);
			naps.setText(String.valueOf(nnaps = mAccessPointManager.getByFuncaoPosicaoIdPosicao(actual_posicao.getId()).size()));
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
		
		Posicao p = mPosicoes.get(1);//l.getPointId()
		return l.getPointId();

	}

}
