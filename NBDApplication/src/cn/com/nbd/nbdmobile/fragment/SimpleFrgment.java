package cn.com.nbd.nbdmobile.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SimpleFrgment extends Fragment {

	private String mTitle;

	public static final String BUNDLE_TITLE = "title";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			mTitle = bundle.getString(BUNDLE_TITLE);
		}
		
		TextView tv = new TextView(getActivity());
		tv.setText("test page");
		tv.setGravity(Gravity.CENTER);
		
		return tv;
	}

	public static SimpleFrgment newInstance(String title) {
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_TITLE, title);

		SimpleFrgment frgment = new SimpleFrgment();
		frgment.setArguments(bundle);

		return frgment;
	}

}
