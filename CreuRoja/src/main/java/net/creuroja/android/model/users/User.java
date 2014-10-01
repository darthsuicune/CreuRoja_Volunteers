package net.creuroja.android.model.users;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import net.creuroja.android.R;
import net.creuroja.android.model.Settings;
import net.creuroja.android.model.db.CreuRojaContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by denis on 14.06.14.
 */
public class User {
	public static final String sRemoteId = "id";
	public static final String sName = "name";
	public static final String sSurname = "surname";
	public static final String sEmail = "email";
	public static final String sRole = "role";
	public static final String sActive = "active";
	public static final String sAccessToken = "accessToken";
	public static final String sTypes = "types";
	public static final String sPhone = "phone";

	public int remoteId;
	public String name;
	public String surname;
	public String email;
	public String phone;
	public Role role;
	public boolean active;
	public String accessToken;
	List<String> types;

	public User(String name, String surname, String email, Role role, boolean active,
				String accessToken, int remoteId, List<String> types, String phone) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.role = role;
		this.active = active;
		this.remoteId = remoteId;
		this.accessToken = accessToken;
		this.types = types;
		this.phone = phone;
	}

	public User(Cursor c) {
		if (c.moveToFirst()) {
			name = c.getString(c.getColumnIndex(CreuRojaContract.Users.NAME));
			surname = c.getString(c.getColumnIndex(CreuRojaContract.Users.SURNAME));
			email = c.getString(c.getColumnIndex(CreuRojaContract.Users.EMAIL));
			role = Role.getRole(c.getString(c.getColumnIndex(CreuRojaContract.Users.ROLE)));
			active = c.getInt(c.getColumnIndex(CreuRojaContract.Users.ACTIVE)) > 0;
			accessToken = c.getString(c.getColumnIndex(CreuRojaContract.Users.ACCESS_TOKEN));
			phone = c.getString(c.getColumnIndex(CreuRojaContract.Users.PHONE));
			remoteId = c.getInt(c.getColumnIndex(CreuRojaContract.Users.REMOTE_ID));
			types = parseTypes(c.getString(c.getColumnIndex(CreuRojaContract.Users.TYPES)));

		}
	}

	public User(JSONObject object) throws JSONException {
		name = object.getString(sName);
		surname = object.getString(sSurname);
		email = object.getString(sEmail);
		remoteId = object.getInt(sRemoteId);
		role = Role.getRole(object.getString(sRole));
		active = object.getString(sActive).equals("true");
		accessToken = object.getString(sAccessToken);
		types = parseTypes(object.getString(sTypes));
		phone = object.getString(sPhone);
	}

	private List<String> parseTypes(String semicolonSeparatedTypes) {
		StringTokenizer tokenizer = new StringTokenizer(semicolonSeparatedTypes, ";");
		List<String> types = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) {
			types.add(tokenizer.nextToken());
		}
		return types;
	}

	public Uri save(ContentResolver cr) {
		return cr.insert(CreuRojaContract.Users.CONTENT_USERS, toValues());
	}

	public void deactivate(SharedPreferences prefs, ContentResolver cr) {
		active = false;
		Settings.clean(prefs, cr);
	}

	public ContentValues toValues() {
		ContentValues values = new ContentValues();
		if (name != null) {
			values.put(CreuRojaContract.Users.NAME, name);
			values.put(CreuRojaContract.Users.SURNAME, surname);
			values.put(CreuRojaContract.Users.EMAIL, email);
			values.put(CreuRojaContract.Users.ROLE, role.toString());
			values.put(CreuRojaContract.Users.ACTIVE, active);
			values.put(CreuRojaContract.Users.ACCESS_TOKEN, accessToken);
			values.put(CreuRojaContract.Users.REMOTE_ID, remoteId);
			values.put(CreuRojaContract.Users.TYPES, buildTypesString(types));
			values.put(CreuRojaContract.Users.PHONE, phone);
		}
		return values;
	}

	private String buildTypesString(List<String> types) {
		StringBuilder builder = new StringBuilder();
		for(String type : types) {
			builder.append(type);
		}
		return builder.toString();
	}

	@Override public String toString() {
		return name + " " + surname;
	}

	public enum Role {
		VOLUNTEER(R.string.user_role_volunteer), TECHNICIAN(R.string.user_role_technician),
		ADMIN(R.string.user_role_admin);

		public int mResId;

		Role(int resId) {
			mResId = resId;
		}

		public static Role getRole(String string) {
			switch (string.toLowerCase()) {
				case "admin":
					return ADMIN;
				case "technician":
					return TECHNICIAN;
				case "volunteer":
				default:
					return VOLUNTEER;
			}
		}

		public int toResourceString() {
			return mResId;
		}
	}
}
