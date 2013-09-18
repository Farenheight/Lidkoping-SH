package com.example.lidkopingsh;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.lidkopingsh.dummy.DummyModel;

/**
 * A fragment representing a single Stone detail screen. This fragment is either
 * contained in a {@link StoneListActivity} in two-pane mode (on tablets) or a
 * {@link StoneDetailActivity} on handsets.
 */
public class StoneDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyModel.DummyStone mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public StoneDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyModel.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stone_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.stone_name))
					.setText(mItem.name);
			((TextView) rootView.findViewById(R.id.stone_desc))
					.setText(mItem.desc);

			LinearLayout layout = (LinearLayout) rootView
					.findViewById(R.id.task_container);

			for (int j = 0; j < 4; j++) {
				ToggleButton btnStatus = new ToggleButton(getActivity());
				btnStatus.setActivated(mItem.taskList.get(j).status);
				btnStatus.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				btnStatus.setText("Button " + (j + 1));
				btnStatus.setId(j);
				btnStatus
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								mItem.taskList.get(buttonView.getId()).status = buttonView
										.isChecked();
							}
						});
				layout.addView(btnStatus);
			}
		}

		return rootView;
	}
}
