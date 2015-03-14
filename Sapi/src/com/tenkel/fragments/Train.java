package com.tenkel.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import br.ufrj.cos.labia.aips.customviews.AndarGridView;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Listener;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Worker;
import br.ufrj.cos.labia.aips.ips.IPS;
import br.ufrj.cos.labia.aips.ips.Location;
import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;

import com.tenkel.sapi.DebugRoomActivity.WifiReceiver2;
import com.tenkel.sapi.R;
import com.tenkel.sapi.dal.AccessPoint;
import com.tenkel.sapi.dal.AccessPointManager;
import com.tenkel.sapi.dal.Andar;
import com.tenkel.sapi.dal.AndarManager;
import com.tenkel.sapi.dal.LeituraWifiManager;
import com.tenkel.sapi.dal.ObservacaoManager;
import com.tenkel.sapi.dal.Posicao;
import com.tenkel.sapi.dal.PosicaoManager;


public class Train extends Fragment implements Listener {
	
	private IPS mIPS;
	private TreeMap<Long, Posicao> mPosicoes;
	private ToggleButton Predict;
	private Button Train;
	private ObservacaoManager mObservacaoManager;
	private long mIdLocal;
	
	private AndarManager mAndarManager;
	private PosicaoManager mPosicaoManager;
	private LeituraWifiManager mLeituraWIFIManager;
	private AccessPointManager mAccessPointManager;
	private long mAndarId;
	private Andar mAndar;
	private AndarGridView mAndarView;
	private WifiReceiver2 mReceiver;
	private Button mBtCollect;
	private Button mBtTrain;
	private int mState;
	private Map<Long, AccessPoint> mAccessPoints;
	private Long[][] mPosicaoIds;
	private int mWidth;
	private int mHeight;
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
        
        	// Managers
     		mAndarManager = new AndarManager(getApplicationContext());
     		mPosicaoManager = new PosicaoManager(getApplicationContext());
     		mObservacaoManager = new ObservacaoManager(getApplicationContext());
     		mLeituraWIFIManager = new LeituraWifiManager(getApplicationContext());
     		mAccessPointManager = new AccessPointManager(getApplicationContext());
     		
     		// Carrega o andar escolhido 
     		mAndarId = getIntent().getExtras().getLong("andar_id");
     		mAndar = mAndarManager.getFirstById(mAndarId);
     		
     		// Ouvinte para os eventos do Bridge
     		mReceiver = new WifiReceiver2();
        
        Predict = (ToggleButton) view.findViewById(R.id.predict);
        Predict.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {

				} 
				else {
				}

			}
		});
        
        Train = (Button) view.findViewById(R.id.buTrain);
		Train.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LoadingDialog dialog = new LoadingDialog();
				dialog.setWorker(new Worker() {
						@Override
						public void doHeavyWork(LoadingDialog dialog) {
							try {
								Thread.sleep(500);
								trainModel();
							} catch (InterruptedException e) {
								
							}
						}
					});
				dialog.setListener(Train.this);
				dialog.show(getFragmentManager(), "training");
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
	
	private void trainModel() {
		
	}

	@Override
	public void onStart(Worker w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish(Worker w) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Worker w, Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancel(Worker w) {
		// TODO Auto-generated method stub
		
	}
	
	
}
