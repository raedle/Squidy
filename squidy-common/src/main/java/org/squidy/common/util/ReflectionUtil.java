/**
 * Squidy Interaction Library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Squidy Interaction Library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Squidy Interaction Library. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * 2009 Human-Computer Interaction Group, University of Konstanz.
 * <http://hci.uni-konstanz.de>
 * 
 * Please contact info@squidy-lib.de or visit our website
 * <http://www.squidy-lib.de> for further information.
 */

package org.squidy.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.squidy.common.dynamiccode.DynamicCodeClassLoader;


/**
 * <code>ReflectionUtil</code>.
 * 
 * <pre>
 * Date: Jun 25, 2008
 * Time: 1:32:47 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University
 *         of Konstanz
 * @version $Id: ReflectionUtil.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class ReflectionUtil {

	// Logger to log info, error, debug,... messages.
	private static final Log LOG = LogFactory.getLog(ReflectionUtil.class);
	
	/**
	 * @param <T>
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> loadClass(String className) {
		try {
			
			ClassLoader classLoader = DynamicCodeClassLoader.DYNAMIC_CODE;
//			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			
//			LOG.debug(classLoader);
			
			return (Class<T>) classLoader.loadClass(className);
		}
		catch (ClassNotFoundException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
			return null;
		}
	}

	/**
	 * @param <T>
	 * @param className
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createInstance(String className) {
		Class<?> type = loadClass(className);
		return (T) createInstance(type);
	}

	/**
	 * @param type
	 * @param parameters
	 * @return
	 */
	public static <T> T createInstance(Class<T> type, Object... parameters) {
		try {
			if (parameters.length > 0) {

				Class<?>[] parameterTypes = new Class<?>[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					parameterTypes[i] = parameters[i].getClass();
				}
				Constructor<T> constructor = type.getConstructor(parameterTypes);

				return constructor.newInstance(parameters);
			}
			else {
				return type.newInstance();
			}
		}
		catch (Exception e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	/**
	 * @param <T>
	 * @param method
	 * @param invokable
	 * @param parameters
	 * @return
	 */
	public static <T> T callMethod(Method method, Object invokable, Object... parameters) {
		try {
			return (T) method.invoke(invokable, parameters);
		}
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param invokable
	 * @param methodName
	 * @param parameters
	 * @param parameterTypes
	 * @return
	 */
	public static Object callMethod(Object invokable, String methodName, Object[] parameters, Class[] parameterTypes) {
		try {
			Method method = invokable.getClass().getMethod(methodName, parameterTypes);
			method.setAccessible(true);
			return method.invoke(invokable, parameters);
		}
		catch (SecurityException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		catch (IllegalArgumentException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		catch (NoSuchMethodException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		catch (IllegalAccessException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		catch (InvocationTargetException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * @param invokable
	 * @param methodName
	 * @param parameters
	 * @return
	 */
	public static Object callMethod(Object invokable, String methodName, Object... parameters) {
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			parameterTypes[i] = parameters[i].getClass();
		}

		return callMethod(invokable, methodName, parameters, parameterTypes);
	}
	
	/**
	 * @param annotationType
	 * @param classType
	 * @return
	 */
	public static <A extends Annotation> A getAnnotation(Class<A> annotationType, Class<?> classType) {
		return classType.getAnnotation(annotationType);
	}

	/**
	 * @param type
	 * @param name
	 * @return
	 */
	public static Field getFieldInObjectHierarchy(Class<? extends Object> type, String name) {
		if (type == Object.class) {
			return null;
		}

		try {
			return type.getDeclaredField(name);
		}
		catch (SecurityException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(e.getMessage(), e);
			}
		}
		catch (NoSuchFieldException e) {
			// TODO: [RR] Check whether trace will be logged only if developer requires.
			if (LOG.isTraceEnabled()) {
				LOG.trace(e.getMessage(), e);
			}
		}

		return getFieldInObjectHierarchy(type.getSuperclass(), name);
	}

	/**
	 * @param type
	 * @return
	 */
	public static Field[] getFieldsInObjectHierarchy(Class<? extends Object> type) {
		List<Field> fields = new ArrayList<Field>();

		return getFieldsInObjectHierarchy0(fields, type).toArray(new Field[0]);
	}

	/**
	 * @param fields
	 * @param type
	 * @return
	 */
	private static List<Field> getFieldsInObjectHierarchy0(List<Field> fields, Class<? extends Object> type) {
		if (type == Object.class) {
			return fields;
		}

		for (Field field : type.getDeclaredFields()) {
			fields.add(field);
		}
		return getFieldsInObjectHierarchy0(fields, type.getSuperclass());
	}
	
	/**
	 * @param classLoader
	 * @param classNames
	 * @param augmentClasses
	 * @return
	 */
	public static Class<?>[] loadContextClasses(ClassLoader classLoader, String[] classNames, Class<?>... augmentClasses) {
		List<Class<?>> classes = new ArrayList<Class<?>>();

		// Augment context classes with augment classes.
		for (Class<?> type : augmentClasses) {
			classes.add(type);
		}

		for (String className : classNames) {
			Class<?> type = null;
			try {
				className = className.replace('/', '.');
				type = classLoader.loadClass(className);
			}
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (type != null) {
				if (type.isAnnotationPresent(XmlType.class)) {
					classes.add(type);
				}
			}
		}
		return classes.toArray(new Class[0]);
	}

//	/**
//	 * Load classes listed in class names array and returns class in an array.
//	 * 
//	 * @param classNames
//	 *            Classes to be load.
//	 * @param augmentClasses
//	 *            Augment return result with augment classes.
//	 * @return The loaded classes in an array.
//	 */
//	public static Class<?>[] loadContextClasses(String[] classNames, Class<?>... augmentClasses) {
//		return loadContextClasses(ReflectionUtil.class.getClassLoader(), classNames, augmentClasses);
//	}
	
	/**
	 * @param source
	 * @param target
	 */
	public static void mapFieldsWithAnnotation(Class<? extends Annotation> annotationClass, Object source, Object target) {
		
		Class<?> sourceClass = source.getClass();
		Class<?> targetClass = target.getClass();
		
		Field targetField;
		for (Field sourceField : sourceClass.getDeclaredFields()) {
			sourceField.setAccessible(true);
			
			if (sourceField.isAnnotationPresent(annotationClass)) {
				try {
					targetField = targetClass.getDeclaredField(sourceField.getName());
					targetField.setAccessible(true);
					
					try {
						targetField.set(target, sourceField.get(source));
					}
					catch (IllegalArgumentException e) {
						if (LOG.isDebugEnabled()) {
							LOG.debug(e);
						}
					}
					catch (IllegalAccessException e) {
						if (LOG.isDebugEnabled()) {
							LOG.debug(e);
						}
					}
				}
				catch (SecurityException e) {
					if (LOG.isWarnEnabled()) {
						LOG.warn(e);
					}
				}
				catch (NoSuchFieldException e) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(e);
					}
				}
			}
		}
	}
}
