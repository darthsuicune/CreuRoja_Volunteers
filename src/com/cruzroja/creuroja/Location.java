package com.cruzroja.creuroja;

import com.google.android.gms.maps.model.LatLng;

public class Location {
	public static final String MARKER_NEW_LINE = "<br />";
	public static final String MARKER_STRONG = "<strong>";
	public static final String MARKER_STRONG_END = "</strong>";
	public static final String MARKER_SPACE = "&nbsp;";

	public LatLng mPosition;
	public int mIcono;
	public Contenido mContenido;

	public Location(double lat, double longit, String icon, String content) {
		mPosition = new LatLng(lat, longit);
		mIcono = getIcon(icon);
		mContenido = getContenido(content);
	}

	public int getIcon(String icon) {
		if (icon.equals("asamblea.png")) {
			return R.drawable.asamblea;
		} else if (icon.equals("bravo.png")) {
			return R.drawable.bravo;
		} else if (icon.equals("cuap.png")) {
			return R.drawable.cuap;
		} else if (icon.equals("embarcacion.png")) {
			return R.drawable.embarcacion;
		} else if (icon.equals("hospital.png")) {
			return R.drawable.hospital;
		} else {
			return 0;
		}
	}

	public Contenido getContenido(String contenido) {
		if (!contenido.contains(MARKER_NEW_LINE)) {
			return new Contenido(contenido.replace(MARKER_STRONG, "").replace(
					MARKER_STRONG_END, ""));
		}
		String nombre = contenido
				.substring(0, contenido.indexOf(MARKER_NEW_LINE))
				.replace(MARKER_STRONG, "").replace(MARKER_STRONG_END, "");

		String lugar = contenido
				.substring(contenido.indexOf(MARKER_NEW_LINE),
						contenido.lastIndexOf(MARKER_NEW_LINE))
				.replace(MARKER_NEW_LINE, "").replaceAll(MARKER_SPACE, "");
		String horario = contenido
				.substring(contenido.lastIndexOf(MARKER_NEW_LINE))
				.replace(MARKER_NEW_LINE, "").replaceAll(MARKER_SPACE, "");
		return new Contenido(nombre, lugar, horario);
	}

	public static class Contenido {
		public String mNombre;
		public String mLugar;
		public String mHorario;
		public String mSnippet;

		public Contenido(String nombre) {
			mNombre = nombre;
		}

		public Contenido(String nombre, String lugar, String horario) {
			mNombre = nombre;
			mLugar = lugar;
			mHorario = horario;
			mSnippet = mLugar + MARKER_NEW_LINE + mHorario;
		}
	}
}
