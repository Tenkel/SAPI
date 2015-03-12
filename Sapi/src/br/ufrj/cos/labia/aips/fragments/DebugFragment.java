package br.ufrj.cos.labia.aips.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tenkel.sapi.DebugRoomActivity;
import com.tenkel.sapi.R;
import com.tenkel.sapi.dal.AccessPoint;
import com.tenkel.sapi.dal.AccessPointManager;
import com.tenkel.sapi.dal.Andar;
import com.tenkel.sapi.dal.AndarManager;
import com.tenkel.sapi.dal.LeituraWiFi;
import com.tenkel.sapi.dal.LeituraWifiManager;
import com.tenkel.sapi.dal.Observacao;
import com.tenkel.sapi.dal.ObservacaoManager;
import com.tenkel.sapi.dal.Posicao;
import com.tenkel.sapi.dal.PosicaoManager;
import com.tenkel.sapi.kde.KDE;

import br.ufrj.cos.labia.aips.fragments.dialogs.AndaresSelectDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.AndaresSelectDialog.OnAndaresSelectedListener;
import br.ufrj.cos.labia.aips.fragments.dialogs.ConfirmDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.ConfirmDialog.ConfirmDialogListener;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Listener;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Worker;
import br.ufrj.cos.labia.aips.fragments.dialogs.NewRoomDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.NewRoomDialog.NewRoomDialogListener;
import br.ufrj.cos.labia.aips.ips.IPS;
import br.ufrj.cos.labia.aips.ips.Location;
import br.ufrj.cos.labia.aips.ips.Reading;
import br.ufrj.cos.labia.aips.ips.WIFISignal;

public class DebugFragment extends Fragment implements OnClickListener, OnItemClickListener, 
		OnItemLongClickListener, NewRoomDialogListener, ConfirmDialogListener, OnAndaresSelectedListener, Listener {


	private static final int CMD_FUSION = 0;

	private static final int CMD_CROSSVAL = 1;

	
	private Button mBtNew;

	private Button mBtFusao;

	private Button mBtBackup;

	private Button mBtRestore;

	private Button mBtCrossval;
	
	private ListView mListRooms;
	
	private AndarManager mAndarManager;

	private List<Andar> mAndares;

	private int mDeletePosition;

	private PosicaoManager mPosicaoManager;

	private List<Andar> mAndaresFusao;

	private ObservacaoManager mObservacaoManager;

	private LeituraWifiManager mLeiturasWIFIManager;

	
	public static DebugFragment newInstance() {
        return new DebugFragment();
    }
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debug_room_list, container, false);

		mBtNew = (Button) view.findViewById(R.id.btNew);
		mBtNew.setOnClickListener(this);

		mBtFusao = (Button) view.findViewById(R.id.btFusao);
		mBtFusao.setOnClickListener(this);

		mBtBackup = (Button) view.findViewById(R.id.btBackup);
		mBtBackup.setOnClickListener(this);

		mBtRestore = (Button) view.findViewById(R.id.btRestore);
		mBtRestore.setOnClickListener(this);
		
		mBtCrossval = (Button) view.findViewById(R.id.btCrossval);
		mBtCrossval.setOnClickListener(this);
		
		mListRooms = (ListView) view.findViewById(R.id.debugRoomList);
		mListRooms.setOnItemClickListener(this);
		mListRooms.setOnItemLongClickListener(this);

		mAndarManager = new AndarManager(getActivity());
		mPosicaoManager = new PosicaoManager(getActivity());
		mObservacaoManager = new ObservacaoManager(getActivity());
		mLeiturasWIFIManager = new LeituraWifiManager(getActivity());
		
		updateListView();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

	private void updateListView() {
		mAndares = mAndarManager.getAllWithNullIdLocal();
		String[] names = new String[mAndares.size()];
		for (int i = 0; i < mAndares.size(); ++i)
			names[i] = mAndares.get(i).getNome();

		mListRooms.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, names));
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		mDeletePosition = position;

		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setListener(this);
		dialog.setMessage("Delete this room?");
		dialog.show(getFragmentManager(), "confirm_delete");

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.i("Main", "Selected room " + mAndares.get(position).getNome());
		Intent intent = new Intent(getActivity(), DebugRoomActivity.class);
		intent.putExtra("andar_id", mAndares.get(position).getId());
		startActivity(intent);
	}

	@Override
	public void onClick(View bt) {
		if (bt == mBtNew) {
			newRoom();
		} 
		
		else if (bt == mBtFusao) {
			AndaresSelectDialog dialog = new AndaresSelectDialog();
			dialog.setListener(this);
			dialog.setKey(CMD_FUSION);
			dialog.show(getFragmentManager(), "select_andares");
		} 
		
		else if (bt == mBtBackup) {
			makeBackup();
		} 
		
		else if (bt == mBtRestore) {
			restoreBackup();
		} 
		
		else if (bt == mBtCrossval) {
			AndaresSelectDialog dialog = new AndaresSelectDialog();
			dialog.setListener(this);
			dialog.setKey(CMD_CROSSVAL);
			dialog.show(getFragmentManager(), "select_andares");
		}
	}

	private void newRoom() {
		NewRoomDialog dialog = new NewRoomDialog();
		dialog.setListener(this);
		dialog.show(getFragmentManager(), "novoandar");
	}

	private void makeBackup() {
		Log.w("DebugRoomList", "Making backup");
		File from = getActivity().getApplicationContext().getDatabasePath("database.db");

		File sd = Environment.getExternalStorageDirectory();
		File exportFold = new File(sd, "SCDP/Backup/");
		exportFold.mkdirs();
		File to = new File(exportFold, "database.db");
		
		try {
			copy(from, to);
			Log.i("DebugRoomList", "Database saved");
			Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Log.e("DebugRoomList", "Could not copy database");
			Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
		}
	}

	private void restoreBackup() {
		Log.w("DebugRoomList", "Restoring backup");
		File to = getActivity().getApplicationContext().getDatabasePath("database.db");

		File sd = Environment.getExternalStorageDirectory();
		File exportFold = new File(sd, "SCDP/Backup/");
		exportFold.mkdirs();
		File from = new File(exportFold, "database.db");
		
		try {
			copy(from, to);
			Log.i("DebugRoomList", "Database restored");
			Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Log.e("DebugRoomList", "Could not restore database");
			Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
		}

		updateListView();
	}

	public void copy(File src, File dst) throws IOException {
	    FileInputStream inStream = new FileInputStream(src);
	    FileOutputStream outStream = new FileOutputStream(dst);
	    FileChannel inChannel = inStream.getChannel();
	    FileChannel outChannel = outStream.getChannel();
	    inChannel.transferTo(0, inChannel.size(), outChannel);
	    inStream.close();
	    outStream.close();
	}
	
	@Override
	public void onCreateAndar(Andar andar, int width, int height) {
		Log.i("Main", "Salvando andar");
		
		if (mAndaresFusao == null) {
			criarAndarSimples(andar, width, height);
		} else {
			LoadingDialog dialog = new LoadingDialog();
			dialog.setWorker(new FusaoWorker(andar, width, height));
			dialog.setListener(this);
			dialog.show(getFragmentManager(), "fusao");
		}
		
		updateListView();
	}

	private void criarAndarSimples(Andar andar, int width, int height) {
		mAndarManager.save(andar);
		for (int x=0;x<width;++x) {
			for (int y=0;y<height;++y) {
				mPosicaoManager.save(new Posicao(andar.getId(), (double)x, (double)y));
			}
		}
	}

	private void criarAndarPorFusao(Andar andar, int width, int height) {
		
	}

	@Override
	public void onCreateAndarCancelled() {
		if (mAndaresFusao != null) {
			Log.w("DebugRoomList", "Fusão cancelada");
			mAndaresFusao = null;
		}
	}

	@Override
	public void onConfirm(ConfirmDialog dialog) {
		mAndarManager.delete(mAndares.get(mDeletePosition));
		updateListView();
	}

	@Override
	public void onCancel(ConfirmDialog dialog) {
		
	}

	@Override
	public void onSelectAndares(List<Andar> andares, int key) {
		if (!validAndares(andares)) {
			Toast.makeText(getActivity(), "Andares com dimensões não compátiveis", 
					Toast.LENGTH_SHORT).show();
			return;
		}
	
		if (key == CMD_FUSION) {
			String[] tmp = andares.get(0).getCamadas().split("_");
			int width = Integer.parseInt(tmp[0]);
			int height = Integer.parseInt(tmp[1]);
			
			mAndaresFusao = andares;
			NewRoomDialog dialog = new NewRoomDialog();
			dialog.setListener(this);
			dialog.setRoomSize(width, height);
			dialog.show(getFragmentManager(), "novoandar");
		}
		
		else if (key == CMD_CROSSVAL) {
			LoadingDialog dialog = new LoadingDialog();
			dialog.setWorker(new CrossvalWorker(andares));
			dialog.setListener(this);
			dialog.show(getFragmentManager(), "crossval");
		}
		
		else {
			Log.e("DebugRoomList", "Invalid cmd for onSelectAndares");
		}
	}

	private boolean validAndares(List<Andar> andares) {
		String[] tmp = andares.get(0).getCamadas().split("_");
		int w1 = Integer.parseInt(tmp[0]);
		int h1 = Integer.parseInt(tmp[1]);
		
		for (Andar andar : andares) {
			tmp = andar.getCamadas().split("_");
			int w2 = Integer.parseInt(tmp[0]);
			int h2 = Integer.parseInt(tmp[1]);
			
			if (w1 != w2 || h1 != h2) 
				return false;
		}
		
		return true;
	}

//	private void trainIPS2(HashMap<Long, String> bssids, IPS whips, Andar current, List<Andar> andares) {
//		int width = Integer.parseInt(current.getCamadas().split("_")[0]);
//		
//		for (Andar andar : andares) {
//			if (andar == current) continue;
//			HashMap<Long, Location> targets = mapPosicoesToTarget(andar, width);
//			
//			HashMap<Long, Map<Long, Amostragem>> params = new HashMap<Long, Map<Long, Amostragem>>();
//			Map<Long, Amostragem> atual;
//			
//			List<Observacao> observacoes = mObservacaoManager.getByIdAndar(andar.getId());
//			for (Observacao ob : observacoes) {
//				
//				if (!params.containsKey(ob.getIdPosicao())) {
//					atual = new HashMap<Long, Amostragem>();
//					params.put(ob.getIdPosicao(), atual);
//				} else {
//					atual = params.get(ob.getIdPosicao());
//				}
//				
//				List<LeituraWiFi> leituras = mLeiturasWIFIManager.getByidObservacao(ob.getId());
//				for (LeituraWiFi l : leituras) {
//					if (!atual.containsKey(l.getIdAccessPoint())) {
//						atual.put(l.getIdAccessPoint(), new Amostragem(l.getValor()));
//					} else {
//						atual.get(l.getIdAccessPoint()).add(l.getValor());
//					}
//				}
//				
//				for (Long key : params.keySet()) {
//					List<WIFISignal> signals = new ArrayList<WIFISignal>();
//					atual = params.get(key);
//					
//					for (Long key2: atual.keySet()) {
//						int level = (int) Math.round(atual.get(key2).getMean());
//						signals.add(new WIFISignal(bssids.get(key2), level));
//					}
//					
//					Location location = targets.get(key);
//					whips.learn(new Reading(signals), location);
//				}
//			}
//		}
//	}

	@Override
	public void onStart(Worker w) {
		
	}

	@Override
	public void onFinish(Worker w) {
		if (w instanceof CrossvalWorker) {
			CrossvalWorker w2 = (CrossvalWorker) w; 
			Log.i("DebugRoomList", "AvgAcc: " + w2.getAccuracy());
			Toast.makeText(getActivity(), "AvgAcc: " + w2.getAccuracy(), 
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onError(Worker w, Exception e) {
		e.printStackTrace();
	}

	@Override
	public void onCancel(Worker w) {
		
	}
	
	private class FusaoWorker implements Worker {
		
		private Andar andar;
		private int width;
		private int height;

		public FusaoWorker(Andar andar, int width, int height) {
			this.andar = andar;
			this.width = width;
			this.height = height;
		}

		@Override
		public void doHeavyWork(LoadingDialog dialog) {
			long[][] novasPosicoes = new long[width][height];
			
			// Cria as novas posições no andar
			mAndarManager.save(andar);
			for (int x=0;x<width;++x) {
				for (int y=0;y<height;++y) {
					Posicao p = new Posicao(andar.getId(), (double)x, (double)y);
					mPosicaoManager.save(p);
					novasPosicoes[x][y] = p.getId();
				}
			}
			
			// Copia as observacoes de todos os andares na fusão
			for (Andar other : mAndaresFusao) {
				Log.i("DebugRoomList", "Making fusion " + other.getNome() + " -> " + andar.getNome());
				
				// Cria o mapeamento das posições antigas para as novas
				HashMap<Long, Long> mapeamentoDePosicoes = new HashMap<Long, Long>();
				List<Posicao> velhasPosicoes = mPosicaoManager.getByidAndar(other.getId());
				for (Posicao p : velhasPosicoes) {
					int x = (int) Math.round(p.getX());
					int y = (int) Math.round(p.getY());
					mapeamentoDePosicoes.put(p.getId(), novasPosicoes[x][y]);
				}
				
				// Copia as observações antigas para as novas posições
				List<Observacao> observations = mObservacaoManager.getByIdAndar(other.getId());
				for (Observacao ob : observations) {
					Observacao novaObservacao = new Observacao(ob);
					novaObservacao.setidPosicao(mapeamentoDePosicoes.get(ob.getIdPosicao()));
					mObservacaoManager.save(novaObservacao);
					
					// Copia as leituras de WIFI para as novas observações
					List<LeituraWiFi> leituras = mLeiturasWIFIManager.getByidObservacao(ob.getId());
					for (LeituraWiFi l : leituras) {
						LeituraWiFi novaLeitura = new LeituraWiFi(l);
						novaLeitura.setidObservacao(novaObservacao.getId());
						mLeiturasWIFIManager.save(novaLeitura);
					}
				}
			}
			
			mAndaresFusao = null;
		}
		
	}
	
	private class CrossvalWorker implements Worker {

		private List<Andar> andares;
		private double mAcc;

		public CrossvalWorker(List<Andar> andares) {
			this.andares = andares;
		}
		
		public double getAccuracy() {
			return mAcc;
		}
		
		@Override
		public void doHeavyWork(LoadingDialog dialog) {
			AccessPointManager apm = new AccessPointManager(getActivity());
			List<AccessPoint> aps = apm.getAll();
			HashMap<Long, String> bssids = new HashMap<Long, String>();
			for (AccessPoint ap : aps) bssids.put(ap.getId(), ap.getbssid());
			
			double acc = 0.0;
			for (Andar andar : andares) {
				Log.i("DebugRoomList", "Current test fold: " + andar.getNome());
				IPS whips = createIPS(bssids, andar, andares);
				trainIPS(bssids, whips, andar, andares);
				acc += testIPS(whips, andar, bssids);
			}
			
			acc /= andares.size();
			mAcc = acc;
		}

		private IPS createIPS(HashMap<Long, String> bssids, Andar current, List<Andar> andares) {
/*			List<WIFISignal> signals = new ArrayList<WIFISignal>();
			
			for (Andar andar : andares) {
				if (andar == current) continue;
				
				List<Observacao> observacoes = mObservacaoManager.getByIdAndar(andar.getId());
				for (Observacao ob : observacoes) {
					List<LeituraWiFi> leituras = mLeiturasWIFIManager.getByidObservacao(ob.getId());
					
					for (LeituraWiFi l : leituras) {
						String bssid = bssids.get(l.getIdAccessPoint());
						int level = l.getValor();
						signals.add(new WIFISignal(bssid, level));
					}
				}
			}
			
			return new WHIPS(signals);*/
			return new KDE();
		}

		private void trainIPS(HashMap<Long, String> bssids, IPS whips, Andar current, List<Andar> andares) {
			int width = Integer.parseInt(current.getCamadas().split("_")[0]);
			
			for (Andar andar : andares) {
				if (andar == current) continue;
				
				HashMap<Long, Location> targets = mapPosicoesToTarget(andar, width);
				
				List<Observacao> observacoes = mObservacaoManager.getByIdAndar(andar.getId());
				for (Observacao ob : observacoes) {
					Reading reading = buildReadingFrom(ob, bssids);
					Location location = targets.get(ob.getIdPosicao());
					whips.learn(reading, location);
				}
			}
		}

		private double testIPS(IPS whips, Andar current, HashMap<Long, String> bssids) {
			int width = Integer.parseInt(current.getCamadas().split("_")[0]);
			HashMap<Long, Location> targets = mapPosicoesToTarget(current, width);
			
			int hits = 0;
			List<Observacao> observacoes = mObservacaoManager.getByIdAndar(current.getId());
			for (Observacao ob : observacoes) {
				Reading reading = buildReadingFrom(ob, bssids);
				Location location = targets.get(ob.getIdPosicao());
				Location predicted = whips.predict(reading);
				
				if (location.getPointId() == predicted.getPointId())
					hits += 1;
			}
			
			return hits / (double) observacoes.size();
		}

		private Reading buildReadingFrom(Observacao ob, HashMap<Long, String> bssids) {
			List<WIFISignal> signals = new ArrayList<WIFISignal>();
			
			List<LeituraWiFi> leituras = mLeiturasWIFIManager.getByidObservacao(ob.getId());
			for (LeituraWiFi l : leituras) {
				signals.add(new WIFISignal(bssids.get(l.getIdAccessPoint()), l.getValor()));
			}
			
			return new Reading(signals);
		}

		private HashMap<Long, Location> mapPosicoesToTarget(Andar andar, int width) {
			List<Posicao> posicoes = mPosicaoManager.getByidAndar(andar.getId());
			HashMap<Long, Location> mapa = new HashMap<Long, Location>();
			
			for (Posicao p : posicoes) {
				mapa.put(p.getId(), new Location((long) (p.getY() * width + p.getX())));
			}
			
			return mapa;
		}

	}

}
