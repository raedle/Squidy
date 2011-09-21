/**
 * Squidy Interaction Library is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. Squidy Interaction Library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General
 * Public License along with Squidy Interaction Library. If not, see <http://www.gnu.org/licenses/>. 2009 Human-Computer
 * Interaction Group, University of Konstanz. <http://hci.uni-konstanz.de> Please contact info@squidy-lib.de or visit
 * our website <http://www.squidy-lib.de> for further information.
 */

package org.squidy.manager.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.squidy.SquidyException;
import org.squidy.manager.data.DataType;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.impl.DataAnalog;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataGesture;
import org.squidy.manager.data.impl.DataInertial;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.data.impl.DataString;
import org.squidy.manager.scanner.PackageScanner;


/**
 * <code>DataUtility</code>.
 * 
 * <pre>
 * Date: Nov 11, 2008
 * Time: 5:55:03 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: DataUtility.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class DataUtility {

	// All data types that are found within the classpath
	public static final Collection<Class<? extends IData>> ALL_DATA_TYPES = new ArrayList<Class<? extends IData>>();
	static {
		for (Class<? extends IData> type : PackageScanner.<IData> findAllClassesWithAnnotation(DataType.class)) {
			ALL_DATA_TYPES.add(type);
		}
	}

	// Hierarchy of data types
	public static final Class<? extends IData>[] DATA_FIRST_LEVEL = new Class[] { DataAnalog.class,
			DataPosition2D.class, DataDigital.class, DataString.class };
	public static final Class<? extends IData>[] DATA_SECOND_LEVEL = new Class[] { DataInertial.class,
			DataPosition3D.class, DataButton.class, DataGesture.class };
	public static final Class<? extends IData>[] DATA_THIRD_LEVEL = new Class[] { DataPosition6D.class };

	/**
	 * Returns the high level data type (first level) of the given data type (method parameter).
	 * 
	 * @param type The data type used to identify high level data type.
	 * @return A high level data type corresponding to the data type (parameter).
	 */
	public static Class<? extends IData> getHighLevelDataType(Class<? extends IData> type) {
		for (Class<? extends IData> highLevelType : DATA_FIRST_LEVEL) {
			if (highLevelType.isAssignableFrom(type)) {
				return highLevelType;
			}
		}
		throw new SquidyException("No high level class found for data type " + type.getName());
	}

	/**
	 * Return new instances of the given data objects (new instances).
	 * 
	 * @param data The data objects to be cloned.
	 * @return An array of cloned data types.
	 */
	public static final IData[] getClones(IData... data) {
		IData[] clones = new IData[data.length];

		for (int i = 0; i < data.length; i++) {
			clones[i] = data[i].getClone();
		}
		return clones;
	}

	/**
	 * @param <T>
	 * @param type
	 * @param container
	 * @return
	 */
	public static <T extends IData> List<T> getDataOfType(Class<T> type, IDataContainer container) {

		List<T> typedData = new ArrayList<T>();

		IData[] data = container.getData();
		for (IData d : data) {
			if (type.isAssignableFrom(d.getClass())) {
				typedData.add((T) d);
			}
		}
		return typedData;
	}
}
