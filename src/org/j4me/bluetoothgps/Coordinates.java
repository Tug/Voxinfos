package org.j4me.bluetoothgps;

import java.util.Enumeration;
import java.util.Vector;
import org.j4me.util.*;

/**
 * The <code>Coordinates</code> class represents coordinates as
 * latitude-longitude-altitude values. The latitude and longitude values are
 * expressed in degrees using floating point values. The degrees are in decimal
 * values (rather than minutes/seconds). The coordinates are given using the
 * WGS84 datum.
 * <p>
 * This class also provides convenience methods for converting between a string
 * coordinate representation and the <code>double</code> representation used in this
 * class.
 */
public class Coordinates
{
	/**
	 * This is the earth's mean radius in meters.  Using the mean gives the most
	 * accurate results for distances measured with any bearing.
	 * <p>
	 * In truth the earth is not a perfect sphere.  The radius of the equator
	 * is 6,378,137 and the polar radius is 6,356,752.3142.  The FAI's definition
	 * of 6,371,000 lies between them.
	 */
	private static final double METERS_PER_RADIAN = 6371000;

	/**
	 * Identifier for string coordinate representation Degrees, Minutes, Seconds
	 * and decimal fractions of a second.
	 */
	public static final int DD_MM_SS = 1;

	/**
	 * Identifier for string coordinate representation Degrees, Minutes, decimal
	 * fractions of a minute.
	 */
	public static final int DD_MM = 2;

	/**
	 * The altitude of the location in meters, defined as height above WGS84
	 * ellipsoid. <code>Float.Nan</code> can be used to indicate that altitude is not
	 * known.
	 */
	private float altitude;

	/**
	 * The latitude of the location. Valid range: [-90.0, 90.0]. Positive values
	 * indicate northern latitude and negative values southern latitude.
	 */
	private double latitude;

	/**
	 * The longitude of the location. Valid range: [-180.0, 180.0). Positive
	 * values indicate eastern longitude and negative values western longitude.
	 */
	private double longitude;

	/**
	 * Constructs a new <code>Coordinates</code> object with the values specified.
	 * The latitude and longitude parameters are expressed in degrees using
	 * floating point values. The degrees are in decimal values (rather than
	 * minutes/seconds).
	 * <p>
	 * The coordinate values always apply to the WGS84 datum.
	 * <p>
	 * The <code>Float.NaN</code> value can be used for altitude to indicate that
	 * altitude is not known.
	 * 
	 * @param latitude - the latitude of the location. Valid range: [-90.0,
	 *        90.0]. Positive values indicate northern latitude and negative
	 *        values southern latitude.
	 * @param longitude - the longitude of the location. Valid range: [-180.0,
	 *        180.0). Positive values indicate eastern longitude and negative
	 *        values western longitude.
	 * @param altitude - the altitude of the location in meters, defined as
	 *        height above WGS84 ellipsoid. <code>Float.Nan</code> can be used to
	 *        indicate that altitude is not known.
	 * @throws java.lang.IllegalArgumentException - if an input parameter is out
	 *         of the valid range.
	 */
	public Coordinates (double latitude, double longitude, float altitude)
	{
		setLatitude(latitude);
		setLongitude(longitude);
		setAltitude(altitude);
	}

	/**
	 * Returns the latitude component of this coordinate. Positive values
	 * indicate northern latitude and negative values southern latitude.
	 * <p>
	 * The latitude is given in WGS84 datum.
	 * 
	 * @return the latitude in degrees
	 * @see #setLatitude(double)
	 */
	public double getLatitude ()
	{
		return latitude;
	}

	/**
	 * Returns the longitude component of this coordinate. Positive values
	 * indicate eastern longitude and negative values western longitude.
	 * <p>
	 * The longitude is given in WGS84 datum.
	 * 
	 * @return the longitude in degrees
	 * @see #setLongitude(double)
	 */
	public double getLongitude ()
	{
		return longitude;
	}

	/**
	 * Returns the altitude component of this coordinate. Altitude is defined to
	 * mean height above the WGS84 reference ellipsoid. 0.0 means a location at
	 * the ellipsoid surface, negative values mean the location is below the
	 * ellipsoid surface, <code>Float.Nan</code> that no altitude is not available.
	 * 
	 * @return the altitude in meters above the reference ellipsoid
	 * @see #setAltitude(float)
	 */
	public float getAltitude ()
	{
		return altitude;
	}

	/**
	 * Sets the geodetic altitude for this point.
	 * 
	 * @param altitude - the altitude of the location in meters, defined as
	 *        height above the WGS84 ellipsoid. 0.0 means a location at the
	 *        ellipsoid surface, negative values mean the location is below the
	 *        ellipsoid surface, <code>Float.Nan</code> that no altitude is not
	 *        available
	 * @see #getAltitude()
	 */
	public void setAltitude (float altitude)
	{
		this.altitude = altitude;
	}

	/**
	 * Sets the geodetic latitude for this point. Latitude is given as a double
	 * expressing the latitude in degrees in the WGS84 datum.
	 * 
	 * @param latitude - the latitude component of this location in degrees.
	 *        Valid range: [-90.0, 90.0].
	 * @throws java.lang.IllegalArgumentException - if latitude is out of the
	 *         valid range
	 * @see #getLatitude()
	 */
	public void setLatitude (double latitude)
	{
		if ( Double.isNaN(latitude) || (latitude < -90.0 || latitude >= 90.0) )
		{
			throw new IllegalArgumentException("Latitude (" + latitude + ") is invalid.");
		}
		else
		{
			this.latitude = latitude;
		}
	}

	/**
	 * Sets the geodetic longitude for this point. Longitude is given as a
	 * double expressing the longitude in degrees in the WGS84 datum.
	 * 
	 * @param longitude - the longitude of the location in degrees. Valid range:
	 *        [-180.0, 180.0)
	 * @throws java.lang.IllegalArgumentException - if longitude is out of the
	 *         valid range
	 * @see #getLongitude()
	 */
	public void setLongitude (double longitude)
	{
		if ( Double.isNaN(longitude) || (longitude < -180.0 || longitude >= 180.0) )
		{
			throw new IllegalArgumentException("Longitude (" + longitude + ") is invalid.");
		}
		else
		{
			this.longitude = longitude;
		}
	}

	/**
	 * Calculates the azimuth between the two points according to the ellipsoid
	 * model of WGS84. The azimuth is relative to true north. The Coordinates
	 * object on which this method is called is considered the origin for the
	 * calculation and the Coordinates object passed as a parameter is the
	 * destination which the azimuth is calculated to. When the origin is the
	 * North pole and the destination is not the North pole, this method returns
	 * 180.0. When the origin is the South pole and the destination is not the
	 * South pole, this method returns 0.0. If the origin is equal to the
	 * destination, this method returns 0.0. The implementation shall calculate
	 * the result as exactly as it can. However, it is required that the result
	 * is within 1 degree of the correct result.
	 * 
	 * @param to - the <code>Coordinates</code> of the destination
	 * @return the azimuth to the destination in degrees. Result is within the
	 *         range [0.0 ,360.0).
	 * @throws java.lang.NullPointerException - if the parameter is <code>null</code>
	 */
	public float azimuthTo (Coordinates to)
	{
		if ( to == null )
		{
			throw new IllegalArgumentException( "azimuthTo does not accept a null parameter." );
		}
		
		// Convert from degrees to radians.
		double lat1 = Math.toRadians( latitude );
		double lon1 = Math.toRadians( longitude );
		double lat2 = Math.toRadians( to.latitude );
		double lon2 = Math.toRadians( to.longitude );
		
		// Formula for computing the course between two points.
		// It is explained in detail here:
		//   http://williams.best.vwh.net/avform.htm
		//   http://www.movable-type.co.uk/scripts/LatLong.html
		// course = atan2(
		//            sin(lon2-lon1)*cos(lat2),                                 // c1
		//            cos(lat1)*sin(lat2)-sin(lat1)*cos(lat2)*cos(lon2-lon1))   // c2
		
		double deltaLon = lon2 - lon1;
		double cosLat2 = Math.cos( lat2 );
		double c1 = Math.sin(deltaLon) * cosLat2;
		double c2 = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * cosLat2 * Math.cos(deltaLon);
		double courseInRadians = MathFunc.atan2( c1, c2 );
		
		double course = Math.toDegrees( courseInRadians );
		course = (360.0 + course) % 360.0;  // Normalize to [0,360)
		return (float)course;
	}
	
	/**
	 * Calculates the geodetic distance between the two points according to the
	 * ellipsoid model of WGS84. Altitude is neglected from calculations.
	 * <p>
	 * The implementation shall calculate this as exactly as it can. However, it
	 * is required that the result is within 0.35% of the correct result.
	 * 
	 * @param to - the <code>Coordinates</code> of the destination
	 * @return the distance to the destination in meters
	 * @throws java.lang.NullPointerException - if the parameter is <code>null</code>
	 */
	public float distance (Coordinates to)
	{
		if ( to == null )
		{
			throw new IllegalArgumentException( "distance does not accept a null parameter." );
		}
		
		// Convert from degrees to radians.
		double lat1 = Math.toRadians( latitude );
		double lon1 = Math.toRadians( longitude );
		double lat2 = Math.toRadians( to.latitude );
		double lon2 = Math.toRadians( to.longitude );

		// Use the Haversine formula for greater accuracy when measuring
		// short distances.  It is explained in detail here:
		//   http://williams.best.vwh.net/avform.htm
		//   http://www.movable-type.co.uk/scripts/LatLong.html
		// d = 2*asin(sqrt(
		//          (sin((lat1-lat2)/2))^2 +    // d2
		//          cos(lat1)*cos(lat2) *       // d3
		//          (sin((lon1-lon2)/2))^2) )   // d5
		
		double d1 = Math.sin( (lat1 - lat2) / 2.0 );
		double d2 = d1 * d1;
		double d3 = Math.cos( lat1 ) * Math.cos( lat2 );
		double d4 = Math.sin( (lon1 - lon2) / 2.0 );
		double d5 = d4 * d4;
		double d6 = d2 + d3 * d5;
		double distanceInRadians = 2.0 * MathFunc.asin( Math.sqrt(d6) );
		
		double distance = METERS_PER_RADIAN * distanceInRadians; 
		return (float)distance;
	}
	
	/**
	 * Compares if two <code>Coordinates</code> object reference the same location.
	 * 
	 * @param other is another <code>Coordinates</code> object.
	 * @return <code>true</code> if the two reference the same location;
	 *  <code>false</code> otherwise.
	 */
	public boolean equals (Object other)
	{
		// This is an allowable difference to account for floating point imprecisions.
		final double tolerance = 0.000001;
		
		if ( other == null )
		{
			return false;
		}

		if ( (other instanceof Coordinates) == false )
		{
			return false;
		}
		
		// Otherwise it is a Coordinates object.
		Coordinates c = (Coordinates)other;
		
		if ( (latitude < c.latitude - tolerance) || (latitude > c.latitude + tolerance) )
		{
			return false;
		}
		
		if ( (longitude < c.longitude - tolerance) || (longitude > c.longitude + tolerance) )
		{
			return false;
		}
		
		if ( (Float.isNaN(altitude) == true) && (Float.isNaN(c.altitude) == false) )
		{
			return false;
		}
		if ( (Float.isNaN(altitude) == false) && (Float.isNaN(c.altitude) == true) )
		{
			return false;
		}
		if ( Float.isNaN(altitude) && Float.isNaN(c.altitude) )
		{
			return true;
		}
		if ( (altitude < c.altitude - tolerance) || (altitude > c.altitude + tolerance) )
		{
			return false;
		}
		
		// If we got here the two coordinates are equal.
		return true;
	}
	
	/**
	 * Provides a string representation of the coordinates.
	 *
	 * @return A string such as "79.32�N 169.8�W 25.7m" where the words are the
	 *  latitude, longitude, and altitude (in meters).
	 */
	public String toString()
	{
		String s;
		
		// Add the latitude.
		if ( latitude >= 0.0 )
		{
			s = String.valueOf( latitude );
			s += "�N ";
		}
		else
		{
			s = String.valueOf( -1 * latitude );
			s += "�S ";
		}
		
		// Add the longitude.
		if ( longitude >= 0.0 )
		{
			s += String.valueOf( longitude );
			s += "�E";
		}
		else
		{
			s += String.valueOf( -1 * longitude );
			s += "�W";
		}
		
		// Add the altitude.
		if ( Float.isNaN( altitude ) == false )
		{
			s += (" " + altitude + "m");
		}
		
		return s;
	}







	/**
	 * Converts a double representation of a coordinate with decimal degrees into a string
	 * representation. There are string syntaxes supported are the same as for the
	 * #convert(String) method. The implementation shall provide as many significant
	 * digits for the decimal fractions as are allowed by the string syntax definition.
	 *
	 * @param coordinate
	 *            a double representation of a coordinate
	 * @param outputType
	 *            identifier of the type of the string representation wanted for output
	 *            The constant {@link #DD_MM_SS} identifies the syntax 1 and the constant
	 *            {@link #DD_MM} identifies the syntax 2.
	 * @throws IllegalArgumentException
	 *             if the outputType is not one of the two costant values defined in this
	 *             class or if the coordinate value is not within the range [-180.0,
	 *             180.0) or is Double.NaN
	 * @return a string representation of the coordinate in a representation indicated by
	 *         the parameter
	 * @see #convert(String)
	 */
	public static String convert(double coordinate, int outputType)
			throws IllegalArgumentException {
		if ((coordinate < -180.0) || (coordinate > 180.0))
			throw new IllegalArgumentException();

		int degrees;
		if (coordinate >= 0.0) {
			degrees = (int) Math.floor(coordinate);
		} else {
			degrees = (int) Math.ceil(coordinate);
		}

		String dd = Integer.toString(degrees);
		double minutes = Math.abs(coordinate - degrees) * 60.0;

		if (outputType == DD_MM_SS) {
			int wholeMinutes = (int) Math.floor(minutes);
			double seconds = (minutes - wholeMinutes) * 60;
			String ss = doubleFormat(seconds, 2, 3);
			return dd + ":" + wholeMinutes + ":" + cullTrailingZeros(ss);
		} else if (outputType == DD_MM) {
			String mm = doubleFormat(minutes, 2, 5);
			return dd + ":" + cullTrailingZeros(mm);
		} else
			throw new IllegalArgumentException();
	}

	// fps = digits before decimal point
	// dsp = digits after decimal point
	private static String doubleFormat(double number, int fps, int dps) {
		String out = number < 0 ? "-" : "";
		number = Math.abs(number);
		number = number + 0.5 / tenPow(dps); // for rounding
		if (number > tenPow(fps))
			throw new IllegalArgumentException(String.valueOf(number));
		int front = (int) Math.floor(number);
		out += front;
		if (dps == 0)
			return out;
		while (out.length() < fps) {
			out = "0" + out;
		}
		out += ".";
		double remainder = number - front;
		for (int i = 0; i < dps; i++) {
			remainder *= 10.0;
			int floored = (int) Math.floor(remainder);
			out += floored;
			remainder -= floored;
		}
		return out;
	}

	private static double tenPow(int exponent) {
		double d = 10.0;
		for (int i = 1; i < exponent; i++) {
			d *= 10.0;
		}
		return d;
	}

	private static String cullTrailingZeros(String number) {
		for (int i = number.length() - 1 ; i > 0 ; i--) {
			char c = number.charAt(i);
			if (c == '.' || c == '0')
				continue;
			if (i == number.length() - 1)
				return number;
			return number.substring(0, i + 1);
		}
		return "0";
	}

	/**
	 * Converts a String representation of a coordinate into the double representation as
	 * used in this API. There are two string syntaxes supported:
	 * <p>
	 * 1. Degrees, minutes, seconds and decimal fractions of seconds. This is expressed as
	 * a string complying with the following BNF definition where the degrees are within
	 * the range [-179, 179] and the minutes and seconds are within the range [0, 59], or
	 * the degrees is -180 and the minutes, seconds and decimal fractions are 0:
	 * <p>
	 * coordinate = degrees &quot;:&quot; minutes &quot;:&quot; seconds &quot;.&quot;
	 * decimalfrac | degrees &quot;:&quot; minutes &quot;:&quot; seconds | degrees
	 * &quot;:&quot; minutes<br />
	 * degrees = degreedigits | &quot;-&quot; degreedigits<br />
	 * degreedigits = digit | nonzerodigit digit | &quot;1&quot; digit digit<br />
	 * minutes = minsecfirstdigit digit<br />
	 * seconds = minsecfirstdigit digit<br />
	 * decimalfrac = 1*3digit <br />
	 * digit = &quot;0&quot; | &quot;1&quot; | &quot;2&quot; | &quot;3&quot; |
	 * &quot;4&quot; | &quot;5&quot; | &quot;6&quot; | &quot;7&quot; | &quot;8&quot; |
	 * &quot;9&quot;<br />
	 * nonzerodigit = &quot;1&quot; | &quot;2&quot; | &quot;3&quot; | &quot;4&quot; |
	 * &quot;5&quot; | &quot;6&quot; | &quot;7&quot; | &quot;8&quot; | &quot;9&quot;<br />
	 * minsecfirstdigit = &quot;0&quot; | &quot;1&quot; | &quot;2&quot; | &quot;3&quot; |
	 * &quot;4&quot; | &quot;5&quot;<br />
	 * <p>
	 * 2. Degrees, minutes and decimal fractions of minutes. This is expressed as a string
	 * complying with the following BNF definition where the degrees are within the range
	 * [-179, 179] and the minutes are within the range [0, 59], or the degrees is -180
	 * and the minutes and decimal fractions are 0:
	 * <p>
	 * coordinate = degrees &quot;:&quot; minutes &quot;.&quot; decimalfrac | degrees
	 * &quot;:&quot; minutes<br/> degrees = degreedigits | &quot;-&quot; degreedigits<br/>
	 * degreedigits = digit | nonzerodigit digit | &quot;1&quot; digit digit<br/> minutes =
	 * minsecfirstdigit digit<br/> decimalfrac = 1*5digit<br/> digit = &quot;0&quot; |
	 * &quot;1&quot; | &quot;2&quot; | &quot;3&quot; | &quot;4&quot; | &quot;5&quot; |
	 * &quot;6&quot; | &quot;7&quot; | &quot;8&quot; | &quot;9&quot;<br/> nonzerodigit =
	 * &quot;1&quot; | &quot;2&quot; | &quot;3&quot; | &quot;4&quot; | &quot;5&quot; |
	 * &quot;6&quot; | &quot;7&quot; | &quot;8&quot; | &quot;9&quot;<br/>
	 * minsecfirstdigit = &quot;0&quot; | &quot;1&quot; | &quot;2&quot; | &quot;3&quot; |
	 * &quot;4&quot; | &quot;5&quot;
	 * <p>
	 * For example, for the double value of the coordinate 61.51d, the corresponding
	 * syntax 1 string is "61:30:36" and the corresponding syntax 2 string is "61:30.6".
	 *
	 * @param coordinate
	 *            a String in either of the two representation specified above
	 * @return a double value with decimal degrees that matches the string representation
	 *         given as the parameter
	 * @throws IllegalArgumentException
	 *             if the coordinate input parameter does not comply with the defined
	 *             syntax for the specified types
	 * @throws NullPointerException
	 *             if the coordinate string is null convert
	 */
	public static double convert(String coordinate)
			throws IllegalArgumentException, NullPointerException {
		/*
		 * A much more academic way to do this would be to generate some tree-based parser
		 * code using the BNF definition, but that seems a little too heavyweight for such
		 * short strings.
		 */
		if (coordinate == null)
			throw new NullPointerException();

		/*
		 * We don't have Java 5 regex or split support in Java 1.3, making this task a bit
		 * of a pain to code.
		 */

		/*
		 * First we check that all the characters are valid, whilst also counting the
		 * number of colons and decimal points (we check that colons do not follow
		 * decimals). This allows us to know what type the string is.
		 */
		int length = coordinate.length();
		int colons = 0;
		int decimals = 0;
		for (int i = 0; i < length; i++) {
			char element = coordinate.charAt(i);
			if (!convertIsValidChar(element))
				throw new IllegalArgumentException();
			if (element == ':') {
				if (decimals > 0)
					throw new IllegalArgumentException();
				colons++;
			} else if (element == '.') {
				decimals++;
				if (decimals > 1)
					throw new IllegalArgumentException();
			}
		}

		/*
		 * Then we break the string into its components and parse the individual pieces
		 * (whilst also doing bounds checking). Code looks ugly because there is a lot of
		 * Exception throwing for bad syntax.
		 */
		String[] parts = convertSplit(coordinate);

		try {
			double out = 0.0;
			// the first 2 parts are the same, regardless of type
			int degrees = Integer.valueOf(parts[0]).intValue();
			if ((degrees < -180) || (degrees > 179))
				throw new IllegalArgumentException();
			boolean negative = false;
			if (degrees < 0) {
				negative = true;
				degrees = Math.abs(degrees);
			}

			out += degrees;

			int minutes = Integer.valueOf(parts[1]).intValue();
			if ((minutes < 0) || (minutes > 59))
				throw new IllegalArgumentException();
			out += minutes * 0.1 / 6;

			if (colons == 2) {
				// type 1
				int seconds = Integer.valueOf(parts[2]).intValue();
				if ((seconds < 0) || (seconds > 59))
					throw new IllegalArgumentException();
				// degrees:minutes:seconds
				out += seconds * 0.01 / 36;
				if (decimals == 1) {
					// degrees:minutes:seconds.decimalfrac
					double decimalfrac = Double.valueOf("0." + parts[3]).doubleValue();
					// note that spec says this should be 1*3digit, but we don't
					// restrict the digit count
					if ((decimalfrac < 0) || (decimalfrac >= 1))
						throw new IllegalArgumentException();
					out += decimalfrac * 0.01 / 36;
				}
			} else if ((colons == 1) && (decimals == 1)) {
				// type 2
				// degrees:minutes.decimalfrac
				double decimalfrac = Double.valueOf("0." + parts[2]).doubleValue();
				// note that spec says this should be 1*5digit, but we don't
				// restrict the digit count
				if ((decimalfrac < 0) || (decimalfrac >= 1))
					throw new IllegalArgumentException();
				out += decimalfrac * 0.1 / 6;
			} else
				throw new IllegalArgumentException();

			if (negative) {
				out = -out;
			}

			// do a final check on bounds
			if ((out < -180.0) || (out >= 180.0))
				throw new IllegalArgumentException();
			return out;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Helper method for {@link #convert(String)}
	 *
	 * @param element
	 * @return
	 */
	private static boolean convertIsValidChar(char element) {
		if ((element == '-') || (element == ':') || (element == '.')
				|| Character.isDigit(element))
			return true;
		return false;
	}

	/**
	 * Helper method for {@link #convert(String)}
	 *
	 * @param in
	 * @return
	 */
	private static String[] convertSplit(String in)
			throws IllegalArgumentException {
		Vector parts = new Vector(4);

		int start = 0;
		int length = in.length();
		for (int i = 0; i <= length; i++) {
			if ((i == length) || (in.charAt(i) == ':') || (in.charAt(i) == '.')) {
				// syntax checking
				if (start - i == 0)
					throw new IllegalArgumentException();
				String part = in.substring(start, i);
				parts.addElement(part);
				start = i + 1;
			}
		}

		// syntax checking
		if ((parts.size() < 2) || (parts.size() > 4))
			throw new IllegalArgumentException();
		// return an array
		String[] partsArray = new String[parts.size()];
		Enumeration en = parts.elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			partsArray[i] = (String) en.nextElement();
		}
		return partsArray;
	}

	/**
	 * @param fromLongitude
	 * @param toLongitude
	 * @return true if toLongitude is east of fromLongitude. If both have the same
	 *         longitude, or are 180 degrees away in this plane, report true.
	 */
	private static boolean isEast(double fromLongitude, double toLongitude) {
		double diff = toLongitude - fromLongitude;
		// if the same longitude, report east
		// if equally east/west, report east
		if (((diff >= 0.0) && (diff <= 180.0)) || (diff <= -180.0))
			return true;
		return false;
	}

	/**
	 * @param fromLatitude
	 * @param toLatitude
	 * @return true if toLatitude is north of fromLatitude. If both have the same latitude
	 *         report true.
	 */
	private static boolean isNorth(double fromLatitude, double toLatitude) {
		double diff = toLatitude - fromLatitude;
		// if the same longitiude, report north
		// if equally north/south, report north
		if ((diff >= 0.0) && (diff <= 90.0))
			return true;
		return false;
	}

}
