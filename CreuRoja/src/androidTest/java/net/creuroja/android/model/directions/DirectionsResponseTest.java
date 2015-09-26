package net.creuroja.android.model.directions;

import com.dlgdev.directions.DirectionsException;
import com.dlgdev.directions.DirectionsResponse;
import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

import java.util.List;

public class DirectionsResponseTest extends TestCase {
	public static final String SAMPLE_ERROR = "{\"routes\": [], \"status\": \"ZERO_RESULTS\"}";
	public static final String SAMPLE_SUCCESS = "{\n" +
												"  \"status\": \"OK\",\n" +
												"  \"routes\": [ {\n" +
												"    \"summary\": \"I-40 W\",\n" +
												"    \"legs\": [ {\n" +
												"      \"steps\": [ {\n" +
												"        \"travel_mode\": \"DRIVING\",\n" +
												"        \"start_location\": {\n" +
												"          \"lat\": 41.8507300,\n" +
												"          \"lng\": -87.6512600\n" +
												"        },\n" +
												"        \"end_location\": {\n" +
												"          \"lat\": 41.8525800,\n" +
												"          \"lng\": -87.6514100\n" +
												"        },\n" +
												"        \"polyline\": {\n" +
												"          \"points\": \"a~l~Fjk~uOwHJy@P\"\n" +
												"        },\n" +
												"        \"duration\": {\n" +
												"          \"value\": 19,\n" +
												"          \"text\": \"1 min\"\n" +
												"        },\n" +
												"        \"html_instructions\": \"Head \\u003cb\\u003enorth\\u003c/b\\u003e on \\u003cb\\u003eS Morgan St\\u003c/b\\u003e toward \\u003cb\\u003eW Cermak Rd\\u003c/b\\u003e\",\n" +
												"        \"distance\": {\n" +
												"          \"value\": 207,\n" +
												"          \"text\": \"0.1 mi\"\n" +
												"        }\n" +
												"      } ],\n" + // End of steps for leg
												"      \"duration\": {\n" +
												"        \"value\": 74384,\n" +
												"        \"text\": \"20 hours 40 mins\"\n" +
												"      },\n" +
												"      \"distance\": {\n" +
												"        \"value\": 2137146,\n" +
												"        \"text\": \"1,328 mi\"\n" +
												"      },\n" +
												"      \"start_location\": {\n" +
												"        \"lat\": 35.4675602,\n" +
												"        \"lng\": -97.5164276\n" +
												"      },\n" +
												"      \"end_location\": {\n" +
												"        \"lat\": 34.0522342,\n" +
												"        \"lng\": -118.2436849\n" +
												"      },\n" +
												"      \"start_address\": \"Oklahoma City, OK, USA\",\n" +
												"      \"end_address\": \"Los Angeles, CA, USA\"\n" +
												"    } ],\n" + // End of legs for route
												"    \"copyrights\": \"Map data ©2010 Google, Sanborn\",\n" +
												"    \"overview_polyline\": {\n" +
												"      \"points\": \"a~l~Fjk~uOnzh@vlbBtc~@tsE`vnApw{A`dw@~w\\\\|tNtqf@l{Yd_Fblh@rxo@b}@xxSfytAblk@xxaBeJxlcBb~t@zbh@jc|Bx}C`rv@rw|@rlhA~dVzeo@vrSnc}Axf]fjz@xfFbw~@dz{A~d{A|zOxbrBbdUvpo@`cFp~xBc`Hk@nurDznmFfwMbwz@bbl@lq~@loPpxq@bw_@v|{CbtY~jGqeMb{iF|n\\\\~mbDzeVh_Wr|Efc\\\\x`Ij{kE}mAb~uF{cNd}xBjp]fulBiwJpgg@|kHntyArpb@bijCk_Kv~eGyqTj_|@`uV`k|DcsNdwxAott@r}q@_gc@nu`CnvHx`k@dse@j|p@zpiAp|gEicy@`omFvaErfo@igQxnlApqGze~AsyRzrjAb__@ftyB}pIlo_BflmA~yQftNboWzoAlzp@mz`@|}_@fda@jakEitAn{fB_a]lexClshBtmqAdmY_hLxiZd~XtaBndgC\"\n" +
												"    },\n" +
												"    \"warnings\": [ ],\n" +
												"    \"waypoint_order\": [ 0, 1 ],\n" +
												"    \"bounds\": {\n" +
												"      \"southwest\": {\n" +
												"        \"lat\": 34.0523600,\n" +
												"        \"lng\": -118.2435600\n" +
												"      },\n" +
												"      \"northeast\": {\n" +
												"        \"lat\": 41.8781100,\n" +
												"        \"lng\": -87.6297900\n" +
												"      }\n" +
												"    }\n" +
												"  } ]\n" + // End of route information
												"}";

	DirectionsResponse response;

	public void testError() throws Exception {
		try {
			createResponse(SAMPLE_ERROR);
		} catch (DirectionsException e) {
			expectExceptionToCome(e);
		}
	}

	private void createResponse(String sample) {
		response = new DirectionsResponse(sample);
	}

	private void expectExceptionToCome(DirectionsException e) {
		assertNotNull(e);
	}

	public void testValid() throws Exception {
		try {
			createResponse(SAMPLE_SUCCESS);
			expectResponseToBeValid();
			expectResponseToContainOneRoute();
			expectResponseToContain4Points();
		} catch (DirectionsException e) {
			expectExceptionNotToCome(e);
		}
	}

	private void expectResponseToBeValid() {
		assertNotNull(response.routes());
		assertFalse(response.routes().isEmpty());
	}

	private void expectResponseToContainOneRoute() {
		assertTrue(response.routes().size() == 1);
	}

	private void expectResponseToContain4Points() {
		//From the sample, we should be having 4 points in the path + the 3 on the polyline
		//Get the path for the first route
		List<LatLng> path = response.routes().get(0).path();
		assertTrue(path.size() == 7);
	}

	private void expectExceptionNotToCome(DirectionsException e) {
		assertNull(e);
	}
}