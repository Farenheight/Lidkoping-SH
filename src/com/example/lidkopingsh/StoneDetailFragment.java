package com.example.lidkopingsh;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.lidkopingsh.model.ModelHandler;
import com.example.lidkopingsh.model.Order;
import com.example.lidkopingsh.model.Status;
import com.example.lidkopingsh.model.Stone;
import com.example.lidkopingsh.model.Task;

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
	public static final String ORDER_ID = "item_id";

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public StoneDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) { 
		View rootView = inflater.inflate(R.layout.fragment_stone_detail,
				container, false);


		return rootView;
	}



}
