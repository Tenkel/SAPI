package br.ufrj.cos.labia.aips.fragments;

import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.tenkel.sapi.LocalActivity;
import com.tenkel.sapi.R;
import com.tenkel.sapi.dal.Andar;
import com.tenkel.sapi.dal.AndarManager;
import com.tenkel.sapi.dal.Local;
import com.tenkel.sapi.dal.LocalManager;
import com.tenkel.sapi.dal.Posicao;
import com.tenkel.sapi.dal.PosicaoManager;
import com.tenkel.sapi.dal.Sala;
import com.tenkel.sapi.dal.SalaManager;
import com.tenkel.sapi.dal.SharedPrefManager;

import br.ufrj.cos.labia.aips.comm.BasicConnector;
import br.ufrj.cos.labia.aips.comm.CommunicationException;
import br.ufrj.cos.labia.aips.dto.AndarDTO;
import br.ufrj.cos.labia.aips.dto.LocalDTO;
import br.ufrj.cos.labia.aips.dto.PontoDeColetaDTO;
import br.ufrj.cos.labia.aips.dto.SalaDTO;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.LoadingDialog.Worker;
import br.ufrj.cos.labia.aips.fragments.dialogs.MultipleChoiceDialog;
import br.ufrj.cos.labia.aips.fragments.dialogs.MultipleChoiceDialog.MultipleChoiceDialogListener;

public class LocaisFragment extends Fragment implements LoadingDialog.Listener {

    private ListView mListLocais;
    
	private LocalManager mLocalManager;

	private List<Local> mLocais;

	private Button mBtBaixarLocais;

	private SharedPrefManager mSharedPrefManager;

	private long mIdDispositivo;

	public List<LocalDTO> mLocaisRemotos;

	private AndarManager mAndarManager;

	private PosicaoManager mPontoDeColetaManager;

	private SalaManager mSalaManager;

	private BaixarLocaisWorker mBaixarLocaisWorker;

	private BaixarListaLocaisWorker mBaixarListaLocaisWorker;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.fragment_locais, container, false);
    	
    	mSharedPrefManager = new SharedPrefManager(getActivity(), false);
    	
    	mSalaManager = new SalaManager(getActivity());
    	mLocalManager = new LocalManager(getActivity());
    	mAndarManager = new AndarManager(getActivity());
    	mPontoDeColetaManager = new PosicaoManager(getActivity());
    	
    	mIdDispositivo = mSharedPrefManager.getIdDispositivo();
    	
    	mListLocais = (ListView) view.findViewById(R.id.listLocais);
    	mListLocais.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), LocalActivity.class);
				intent.putExtra("idLocal", mLocais.get(position).getId());
				startActivity(intent);
			}
		});
    	
    	mBtBaixarLocais = (Button) view.findViewById(R.id.btBaixarLocais);
    	mBtBaixarLocais.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIdDispositivo == -1) {
					Toast.makeText(getActivity(), "Você deve registrar este dispositivo primeiro", 
							Toast.LENGTH_LONG).show();
					return;
				}
				
				LoadingDialog dialog = new LoadingDialog();
				mBaixarListaLocaisWorker = new BaixarListaLocaisWorker(mIdDispositivo);
				dialog.setWorker(mBaixarListaLocaisWorker);
				dialog.setListener(LocaisFragment.this);
				dialog.show(getFragmentManager(), "baixando_locais");
			}
		});
    	
    	updateList();
    	
    	return view;
    }

	private void updateList() {
		mLocais = mLocalManager.getAll();
    	
    	String[] names = new String[mLocais.size()];
		for (int i = 0; i < mLocais.size(); ++i)
			names[i] = mLocais.get(i).getNome();

		mListLocais.setAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, names));
	}

	public static Fragment newInstance() {
		return new LocaisFragment();
	}

	@Override
	public void onStart(Worker w) {
		
	}

	@Override
	public void onFinish(Worker w) {
		
		if (w == mBaixarListaLocaisWorker) {
			String[] items = new String[mLocaisRemotos.size()];
			for (int i=0;i<mLocaisRemotos.size();++i) 
				items[i] = mLocaisRemotos.get(i).Nome + " (" + mLocaisRemotos.get(i).Id + ")";
			
			MultipleChoiceDialog dialog = new MultipleChoiceDialog();
			dialog.setItems(items);
			dialog.setTitle("Selecione os locais");
			dialog.setListener(new MultipleChoiceDialogListener() {
				
				@Override
				public void onSelect(MultipleChoiceDialog dialog, List<Integer> selections) {
					baixarLocaisSelecionados(mLocaisRemotos, selections);
				}
	
				@Override
				public void onCancel(MultipleChoiceDialog dialog) {
					
				}
				
			});
			
			dialog.show(getFragmentManager(), "selecionar_locais");
		}
		
		else if (w == mBaixarLocaisWorker) {
			Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
			updateList();
		}
	}

	@Override
	public void onError(Worker w, Exception e) {
		e.printStackTrace();
		if (e.getMessage() == null)
			Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(getActivity(), e.getMessage(), 
					Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCancel(Worker w) {
		Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
	}
	
	private void baixarLocaisSelecionados(List<LocalDTO> locais,
			List<Integer> selections) {
		
		for (Integer select : selections) {
			LocalDTO local = locais.get(select);
			mLocalManager.deleteByIdRemoto(local.Id);
		}

		LoadingDialog dialog = new LoadingDialog();
		mBaixarLocaisWorker = new BaixarLocaisWorker(mIdDispositivo, locais, selections);
		dialog.setWorker(mBaixarLocaisWorker);
		dialog.setListener(LocaisFragment.this);
		dialog.show(getFragmentManager(), "baixando_locais");
	}

	private class BaixarListaLocaisWorker implements Worker {

		private long mIdDispositivo;

		public BaixarListaLocaisWorker(long idDispositivo) {
			mIdDispositivo = idDispositivo;
		}
		
		@Override
		public void doHeavyWork(LoadingDialog dialog) {
			try {
				Thread.sleep(500);
				mLocaisRemotos = BasicConnector.requisitarLocais(mIdDispositivo);
				Log.i("FragmentRegistrar", "Success");
			} catch (InterruptedException e) {
				Log.e("FragmentRegistrarTelefone", "Operação interrompida");
			} catch (CommunicationException e) {
				throw e;
			}
		}
		
	}

	private class BaixarLocaisWorker implements Worker {

		private long mIdDispositivo;
		
		private List<Integer> mSelections;
		
		private List<LocalDTO> mLocais;
		
		public BaixarLocaisWorker(long idDispositivo, List<LocalDTO> locais,
				List<Integer> selections) {
			mIdDispositivo = idDispositivo;
			mSelections = selections;
			mLocais = locais;
		}

		@Override
		public void doHeavyWork(LoadingDialog dialog) {
			try {
				Thread.sleep(500);
				for (Integer select : mSelections) {
					LocalDTO localdto = BasicConnector.baixarLocal(
							mIdDispositivo, mLocais.get(select).Id);
					
					// Cria o Local
					Local local = new Local();
					local.setIdRemoto(localdto.Id);
					local.setLat0(localdto.BottomDownLat);
					local.setLong0(localdto.BottomDownLong);
					local.setLat1(localdto.TopLeftLat);
					local.setLong1(localdto.TopLeftLong);
					local.setNome(localdto.Nome);
					mLocalManager.save(local);
					
					for (AndarDTO andardto : localdto.Andares) {
						
						// Baixa a imagem do andar
						byte[] image = BasicConnector.downloadBytes(andardto.UriGMLAndar, 1024*1024);
						
						// Cria o Andar
						Andar andar = new Andar();
						andar.setIdRemoto(andardto.Id);
						andar.setIdLocal(local.getId());
						andar.setURIMapa(andardto.UriGMLAndar);
						andar.setX1(andardto.CoordenadasGml[0]);
						andar.setY1(andardto.CoordenadasGml[1]);
						andar.setX2(andardto.CoordenadasGml[2]);
						andar.setY2(andardto.CoordenadasGml[3]);
						andar.setNome(andardto.Nome);
						andar.setImage(image);
						mAndarManager.save(andar);

						for (PontoDeColetaDTO pontodto : andardto.PontosColeta) {
							
							// Cria o Ponto de Coleta
							Posicao ponto = new Posicao();
							ponto.setIdRemoto(pontodto.Id);
							ponto.setIdAndar(andar.getId());
							ponto.setX(pontodto.x);
							ponto.setY(pontodto.y);
							mPontoDeColetaManager.save(ponto);
							
						}

						for (SalaDTO saladto : andardto.Salas) {
							
							// Cria o Ponto de Coleta
							Sala sala = new Sala();
							sala.setIdRemoto(saladto.Id);
							sala.setNome(saladto.Nome);
							sala.setIdPoligono(saladto.IdPoligono);
							sala.setCoordenadas(saladto.Coordenadas);
							sala.setIdAndar(andar.getId());
							mSalaManager.save(sala);
							
						}
						
					}
				}
				Log.i("FragmentRegistrar", "Success");
			} catch (InterruptedException e) {
				Log.e("FragmentRegistrarTelefone", "Operação interrompida");
			} catch (CommunicationException e) {
				throw e;
			}
		}
		
	}
	
}
