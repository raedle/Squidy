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

package org.squidy.designer.piccolo;

import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import javax.swing.JComponent;

import org.squidy.designer.component.CropScroll;
import org.squidy.designer.shape.ZoomShape;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * <code>JComponentWrapper</code>.
 * 
 * <pre>
 * Date: Mar 18, 2009
 * Time: 6:39:58 PM
 * </pre>
 * 
 * @author Roman R&amp;aumldle<br />
 *         <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz
 *         .de</a><br />
 *         Human-Computer Interaction Group<br />
 *         University of Konstanz
 * 
 * @version $Id: JComponentWrapper.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class JComponentWrapper extends PSwing {

	/**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = -6637984819926868981L;

	/**
	 * @param component
	 * @return
	 */
	public static JComponentWrapper create(JComponent component) {

		JComponentWrapper wrapper = new JComponentWrapper(component);
		return wrapper;
		
//		ProxyFactory pf = new ProxyFactory(wrapper);
////		pf.setTargetClass(JComponentWrapper.class);
//		pf.setProxyTargetClass(true);
//		pf.addAdvice(new MethodInterceptor() {
//			
//			public Object invoke(MethodInvocation mi) throws Throwable {
//				
//				if (mi.getMethod().getName().equals("reshape")) {
//					Method reshape2 = mi.getThis().getClass().getMethod("reshape2");
//					reshape2.invoke(mi.getThis());
//				}
//				
//				return null;
//			}
//		});
//		
//		return (JComponentWrapper) pf.getProxy();
		
//		ProxyFactory proxy = new ProxyFactory();
//		proxy.setSuperclass(JComponentWrapper.class);
//		proxy.setInterfaces(new Class[] { Serializable.class, Cloneable.class, Printable.class,
//				PropertyChangeListener.class });
//		
////		proxy.setFilter(new MethodFilter() {
////
////			/* (non-Javadoc)
////			 * @see javassist.util.proxy.MethodFilter#isHandled(java.lang.reflect.Method)
////			 */
////			public boolean isHandled(Method m) {
////				if ("reshape".equals(m.getName())) {
////					System.out.println("#########################");
////					System.out.println("ECHO IN HERE");
////					System.out.println("#########################");
////				}
////				return "reshape".equals(m.getName());
////			}
////		});
//		
//		proxy.setHandler(new MethodHandler() {
//			
//			/* (non-Javadoc)
//			 * @see javassist.util.proxy.MethodHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.Object[])
//			 */
//			public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
//				if ("reshape".equals(thisMethod.getName())) {
//					((JComponentWrapper) self).reshape();
//				}
//				else {
//					return proceed.invoke(self, args);
//				}
//				return null;
//			}
//		});
//		
//		try {
//			return (JComponentWrapper) proxy.create(new Class[] { JComponent.class }, new Object[] { component });
//		}
//		catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		}
//		catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		}
//		catch (InstantiationException e) {
//			e.printStackTrace();
//		}
//		catch (IllegalAccessException e) {
//			e.printStackTrace();
//		}
//		catch (InvocationTargetException e) {
//			e.printStackTrace();
//		}
//		
//		return null;
	}

	/**
	 * @param component
	 */
	public JComponentWrapper(JComponent component) {
		super(component);
		
		component.addPropertyChangeListener("preferredSize", new PropertyChangeListener() {
			
			/* (non-Javadoc)
			 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
			 */
			public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange(-1, CropScroll.CROP_SCROLLER_UPDATE, false, true);
			}
		});
	}

	void reshape2() {
		System.out.println("RESHAPE IN JCOMPONENTWRAPPER");
		getComponent().setBounds( 0, 0, getComponent().getPreferredSize().width, getComponent().getPreferredSize().height );
        setBounds( 0, 0, getComponent().getPreferredSize().width, getComponent().getPreferredSize().height );
	}
	
	/**
	 * Renders to a buffered image, then draws that image to the drawing surface
	 * associated with g2 (usually the screen).
	 * 
	 * @param g2
	 *            graphics context for rendering the JComponent
	 */
	public void paint(Graphics2D g2) {
		JComponent component = getComponent();
		if (component.getBounds().isEmpty()) {
			// The component has not been initialized yet.
			return;
		}

		PNode parent = getParent();
		while (parent != null && !(parent instanceof ZoomShape<?>)) {
			parent = parent.getParent();
		}

		if (parent != null) {
			if (((ZoomShape<?>) parent).isHierarchicalZoomInProgress()) {
				super.paint(g2);
			}
			else {
				component.paint(g2);
			}
		}
		else {
			super.paint(g2);
		}
	}
}
