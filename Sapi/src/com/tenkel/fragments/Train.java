package com.tenkel.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import br.ufrj.cos.labia.aips.customviews.LocationRow;
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
import com.tenkel.sapi.dal.SharedPrefManager;
import com.tenkel.sapi.kde.FoundLocation;
import com.tenkel.sapi.kde.KDE;


public class Train extends Fragment {
	
	private IPS mIPS;
	private ToggleButton Predict;
	private Button Train;
	private TableLayout locationTable;
	private ObservacaoManager mObservacaoManager;
	private AndarManager mAndarManager;
	private LeituraWifiManager mLeituraWIFIManager;
	private PosicaoManager mPosicaoManager;
	private AccessPointManager mAccessPointManager;
	private List<Andar> mAndares;
	private WifiReceiver mReceiver;
	private Map<Long, AccessPoint> mAccessPoints;
	private TextView aquisicoes;
	private TextView guess;
	private TextView confianca;
	private TextView probabilidade;
	private int cycles;
	private Button Export_BD;
	private ProgressBar TrainProgress;
	private SharedPrefManager mSharedPrefManager;
	private Long last;
	
	public static Fragment newInstance() {
		return new Train();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.train, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		final NumberPicker input = new NumberPicker(getActivity());
		input.setMinValue(5);
		input.setMaxValue(100);
		input.setValue(mSharedPrefManager.getConfianca());
		
		
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
			.setTitle("Confiança")
			.setMessage("Escolha o nível necessário de confiança:")
			.setView(input)
			.setPositiveButton("OK"	, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int whichButton) {
			    	mSharedPrefManager.setConfianca(input.getValue());
			    	mSharedPrefManager.save();
			      }
			    })
		    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int whichButton) {
			      }
			    })
			    .create();
		
		dialog.show();
		
		return super.onOptionsItemSelected(item);
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
        setHasOptionsMenu(true);
        mSharedPrefManager = new SharedPrefManager(getActivity(), true);
        TrainProgress = (ProgressBar) view.findViewById(R.id.trainProgress);
        aquisicoes = (TextView) view.findViewById(R.id.aqc);
        guess = (TextView) view.findViewById(R.id.chute);
        confianca = (TextView) view.findViewById(R.id.confianca);
        probabilidade = (TextView) view.findViewById(R.id.probabilidade);
        Train = (Button) view.findViewById(R.id.buTrain);
        Export_BD = (Button) view.findViewById(R.id.export_bd);
        locationTable = (TableLayout) view.findViewById(R.id.locationTable);
        last=(long) -1;
        cycles = 0;
        	// Managers
     		mAndarManager = new AndarManager(getActivity());
     		mObservacaoManager = new ObservacaoManager(getActivity());
     		mLeituraWIFIManager = new LeituraWifiManager(getActivity());
     		mAndares = mAndarManager.getAllWithNullIdLocal();
     		mPosicaoManager = new PosicaoManager(getActivity());
     		mAccessPointManager = new AccessPointManager(getActivity());
     		
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

					@Override
					public void Finished() {
						// TODO Auto-generated method stub
						
					}
				})
				.show(getFragmentManager(), "training_model");
		}
		});

        Export_BD.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				String state = Environment.getExternalStorageState();
				if (Environment.MEDIA_MOUNTED.equals(state)) {
				try {
					mAccessPointManager.DBexport( getActivity().getExternalFilesDir(null).getAbsolutePath());
					Toast.makeText( getActivity(), "BD exportado.", Toast.LENGTH_SHORT).show();					
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
				else{
				Toast.makeText( getActivity(), "Media cannot be written.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		carregarAccessPoints();
        return view;
	}
	
	private void predictPosition(ScanResult[] results) {
		locationTable.removeAllViews();
		
		if (mIPS == null){
			guess.setText(String.valueOf(0));
			confianca.setText(String.valueOf(0));
			return;
		}
		
		List<WIFISignal> signals = new ArrayList<WIFISignal>();
		for (ScanResult r : results) 
			signals.add(new WIFISignal(r.BSSID, r.level));
		
		Reading reading = new Reading(signals);
		LinkedHashMap<Location, Float> map = mIPS.predict(reading);
		Location l = map.keySet().iterator().next();
		
		if (l == null || l.getPointId() == null)  {
			Log.w("DebugRoomActivity", "IPS returned null as prediction");
			return;
		}
		
		else{ 
			float probability = getProbability(map);
			if (mIPS.getConfidence() >= -mSharedPrefManager.getConfianca())
			{
				guess.setText(String.valueOf(mPosicaoManager.getFirstById(l.getPointId()).getIdRemoto()));
				confianca.setText(String.valueOf(mIPS.getConfidence()));
				probabilidade.setText(String.format("%.1f",probability)+"%");
				if (last!=mPosicaoManager.getFirstById(l.getPointId()).getIdRemoto())
				{
					AlertDialog dialog = new AlertDialog.Builder(getActivity())
						.setMessage(mPosicaoManager.getFirstById(l.getPointId()).getPropaganda())
						.setPositiveButton("OK"	, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
							}
						})
						.create();
				dialog.show();
				last = mPosicaoManager.getFirstById(l.getPointId()).getIdRemoto();
				
				}
			}
			else{
				guess.setText("?");
				confianca.setText(String.valueOf(mIPS.getConfidence()));
				probabilidade.setText(String.format("%.1f",probability)+"%");
			}
		}

		LinkedHashMap<Location, Float> probabilites = getAllProbability(map);
	    Iterator<Location> locations = map.keySet().iterator();
		while (locations.hasNext()){
			Location location = locations.next();
			Posicao posicao = mPosicaoManager.getFirstById(location.getPointId());
			Andar andar = mAndarManager.getFirstById(posicao.getIdAndar());
			FoundLocation foundlocation = new FoundLocation(posicao.getIdRemoto(), andar.getNome(), map.get(location), probabilites.get(location));
			LocationRow row = new LocationRow(getActivity(),null);
			row.setFounLocation(foundlocation);
			locationTable.addView(row,new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
			locationTable.invalidate();
		}
		

	}
	
	private LinkedHashMap<Location, Float> getAllProbability(LinkedHashMap<Location, Float> map){
		LinkedHashMap<Location, Float> probabilities = new LinkedHashMap<Location, Float>();
		
		Double soma = 0.0;
		
		Iterator<Location> locations = map.keySet().iterator();
		while (locations.hasNext()){
			Location location = locations.next();
			soma+= (Double) (Math.exp(mIPS.getConfidence(location)));
		}
		

	    Iterator<Location> locationes = map.keySet().iterator();
		while (locationes.hasNext()){
			Location location = locationes.next();
			probabilities.put(location, (float) (Math.exp(mIPS.getConfidence(location))*100.0/soma));
		}
		
		return probabilities;
	}
	
	private float getProbability(LinkedHashMap<Location, Float> map) {
		Double soma = 0.0;

		Iterator<Location> locations = map.keySet().iterator();
		while (locations.hasNext()){
			Location location = locations.next();
			soma+= (Double) (Math.exp(mIPS.getConfidence(location)));
		}
		Iterator<Location> locationes = map.keySet().iterator();
		return (float) (Math.exp(mIPS.getConfidence(locationes.next()))*100.0/soma);
	}

	private void trainModel() {
		IPS ips = new KDE();

		//TrainProgress.setProgress(0);
		//TrainProgress.setVisibility(View.VISIBLE);
		//TrainProgress.postInvalidate();
		
		List<Posicao> full_list = mPosicaoManager.getAll();
		
		//TrainProgress.setMax(full_list.size());
		for (Posicao posicao : full_list){
			List<WIFISignal> wifi_readings = new ArrayList<WIFISignal>();
			
			for (LeituraWiFi wifisample : mLeituraWIFIManager.getByidPosicao(posicao.getId()))
				wifi_readings.add(new WIFISignal(mAccessPoints.get(wifisample.getIdAccessPoint()).getbssid(),wifisample.getValor()));
			
			ips.learn(new Reading(wifi_readings), new Location(posicao.getId()));
			
			//TrainProgress.incrementProgressBy(1);
			//TrainProgress.postInvalidate();
		}
		
		//TrainProgress.setVisibility(View.INVISIBLE);
		//TrainProgress.postInvalidate();
		
		mIPS = ips;
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

	private void carregarAccessPoints() {
		mAccessPoints = new TreeMap<Long, AccessPoint>();
		List<AccessPoint> aps = mAccessPointManager.getAll();
		for (AccessPoint ap : aps) mAccessPoints.put(ap.getId(), ap);
	}
	
}
