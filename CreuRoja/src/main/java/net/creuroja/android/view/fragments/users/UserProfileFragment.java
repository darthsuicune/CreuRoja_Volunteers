package net.creuroja.android.view.fragments.users;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.creuroja.android.R;
import net.creuroja.android.model.db.CreuRojaContract;
import net.creuroja.android.model.users.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link net.creuroja.android.view.fragments.users.UserProfileFragment.OnUserProfileChangedListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
	private static final int LOADER_USER = 1;
	private OnUserProfileChangedListener mListener;
	private User user;

	private TextView mNameView;
	private TextView mEmailView;
	private TextView mRoleView;
	private TextView mTypesView;
	private TextView mPhoneView;

	public UserProfileFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment UserProfileFragment.
	 */
	public static UserProfileFragment newInstance() {
		UserProfileFragment fragment = new UserProfileFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
		prepareViews(v);
		return v;
	}

	private void prepareViews(View v) {
		mNameView = (TextView) v.findViewById(R.id.user_profile_name);
		mEmailView = (TextView) v.findViewById(R.id.user_profile_email);
		mTypesView = (TextView) v.findViewById(R.id.user_profile_types);
		mRoleView = (TextView) v.findViewById(R.id.user_profile_role);
		mPhoneView = (TextView) v.findViewById(R.id.user_profile_phone);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnUserProfileChangedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					activity.toString() + " must implement OnUserProfileChangedListener");
		}
		getLoaderManager().restartLoader(LOADER_USER, null, new UserLoaderHelper());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		user.save(getActivity().getContentResolver());
		mListener = null;
	}

	private void showUser() {
		mNameView.setText(user.toString());
		mEmailView.setText(user.email);
		mTypesView.setText(user.types());
		mRoleView.setText(user.role.toResourceString());
		mPhoneView.setText(user.phone);
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 * <p/>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface OnUserProfileChangedListener {
		public void onUserProfileChanged(User user);
	}

	private class UserLoaderHelper implements LoaderManager.LoaderCallbacks<Cursor> {
		@Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
			return new CursorLoader(getActivity(), CreuRojaContract.Users.CONTENT_USERS, null, null,
					null, null);
		}

		@Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
			user = new User(cursor);
			showUser();
		}

		@Override public void onLoaderReset(Loader<Cursor> cursorLoader) {

		}
	}
}
