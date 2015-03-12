package br.ufrj.cos.labia.aips.fragments.dialogs;

import br.ufrj.cos.labia.aips.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class LoadingDialog extends DialogFragment {

	public interface Worker {
		void doHeavyWork(LoadingDialog dialog);
	}

	public interface Listener {
		public void onStart(Worker w);
		public void onFinish(Worker w);
		public void onError(Worker w, Exception e);
		public void onCancel(Worker w);
	}
	
	private Worker mWorker;
	
	private Listener mListener;
	
	private LoadingThread mThread;
	
	public LoadingDialog setListener(Listener listener) {
		mListener = listener;
		return this;
	}
	
	public LoadingDialog setWorker(Worker worker) {
		mWorker = worker;
		return this;
	}
	
	public Worker getWorker() {
		return mWorker;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		setRetainInstance(true);
		setCancelable(false);
		
		Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(getActivity().getLayoutInflater()
				.inflate(R.layout.dialogfragment_loading, null));
		
		return builder.create();
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance())
			getDialog().setDismissMessage(null);
		super.onDestroyView();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (mWorker == null)
			throw new IllegalStateException("Worker is not defined");
		
		if (mThread == null) {
			mThread = new LoadingThread();
			mThread.registerListener(this);
			mThread.start();
		} else {
			mThread.registerListener(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mThread.unregisterListener();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		mThread.interrupt();
	}

	public void callOnProgress(int progress) {
		if (mThread != null) {
			mThread.callOnProgress(progress);
		}
	}

	protected void cancel() {
		if (mThread != null) {
			mThread.interrupt();
		}
	}

	public void doHeavyWork() throws Exception {
		throw new RuntimeException(
				"You must either overwrite doHeavyWork or define a Worker with setWorker");
	}

	public void onStartWork() {
		if (mListener != null)
			mListener.onStart(mWorker);
	}

	public void onProgressWork(int progress) {

	}

	public void onFinishWork() {
		dismiss();
		if (mListener != null)
			mListener.onFinish(mWorker);
	}

	public void onCancelWork() {
		dismiss();
		if (mListener != null)
			mListener.onCancel(mWorker);
	}

	public void onErrorWork(Exception e) {
		//e.printStackTrace();
		dismiss();
		if (mListener != null)
			mListener.onError(mWorker, e);
	}

	public class LoadingThread extends Thread {

		private static final int STATE_IDLE = 0;
		private static final int STATE_RUNNING = 1;
		private static final int STATE_FINISHED = 2;
		private static final int STATE_CANCELLED = 3;
		private static final int STATE_ERROR = 4;

		private LoadingDialog mListener;
		private Exception mLastError;
		private int mLastProgress;
		private Integer mState;

		public LoadingThread() {
			super();
			mState = STATE_IDLE;
		}

		@Override
		public void run() {
			super.run();

			try {
				callOnStart();
				
				if (mWorker == null)
					doHeavyWork();
				else
					mWorker.doHeavyWork(LoadingDialog.this);
				
				callOnFinish();
			} catch (InterruptedException e) {
				callOnCancel();
			} catch (Exception e) {
				callOnError(e);
			}
		}

		public void registerListener(LoadingDialog listener) {
			synchronized (mState) {
				mListener = listener;
				
				if (mListener == null)
					return;
				
				if (mState == STATE_RUNNING)
					mListener.onProgressWork(mLastProgress);
				else if (mState == STATE_CANCELLED)
					mListener.onCancelWork();
				else if (mState == STATE_FINISHED)
					mListener.onFinishWork();
				else if (mState == STATE_ERROR)
					mListener.onErrorWork(mLastError);
			}
		}

		public void unregisterListener() {
			synchronized (mState) {
				mListener = null;
			}
		}

		private void callOnStart() {
			mLastProgress = 0;
			mState = STATE_RUNNING;
			if (getActivity() == null)
				return;
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					synchronized (mState) {
						if (mListener != null) {
							mListener.onStartWork();
						}
					}
				}
			});
		}

		private void callOnProgress(int progress) {
			mLastProgress = progress;
			if (getActivity() == null)
				return;
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					synchronized (mState) {
						if (mListener != null) {
							mListener.onProgressWork(mLastProgress);
						}
					}
				}
			});
		}

		private void callOnFinish() {
			mLastProgress = 100;
			mState = STATE_FINISHED;
			if (getActivity() == null)
				return;
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					synchronized (mState) {
						if (mListener != null) {
							mListener.onFinishWork();
						}
					}
				}
			});
		}

		private void callOnCancel() {
			mState = STATE_CANCELLED;
			if (getActivity() == null)
				return;
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					synchronized (mState) {
						if (mListener != null) {
							mListener.onCancelWork();
						}
					}
				}
			});
		}

		private void callOnError(Exception e) {
			mState = STATE_ERROR;
			mLastError = e;
			if (getActivity() == null)
				return;
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					synchronized (mState) {
						if (mListener != null) {
							mListener.onErrorWork(mLastError);
						}
					}
				}
			});
		}

	}

}
