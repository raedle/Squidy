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
package org.squidy.performance.pipeline;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.squidy.manager.ProcessException;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.Pipe;
import org.squidy.manager.model.Pipeline;
import org.squidy.manager.model.Piping;
import org.squidy.manager.model.Workspace;
import org.squidy.manager.util.DataUtility;


/**
 * <code>DataflowBenchmarkTest</code>.
 * 
 * <pre>
 * Date: Jul 22, 2009
 * Time: 2:43:38 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: DataflowBenchmarkTest.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 */
public class DataflowBenchmarkTest {

	private static Workspace workspace;
	private static Pipeline pipeline;
	private static NoOpNode noOp1;
	private static NoOpNode noOp2;
	private static GaugeNode gauge;
	
	/**
	 * 
	 */
	@BeforeClass
	public static void setUp() {
		workspace = new Workspace() {
			
			/* (non-Javadoc)
			 * @see org.squidy.manager.model.Processable#start()
			 */
			@Override
			public void start() throws ProcessException {
				super.start();
				
				gauge.publish(new DataPosition2D(GaugeNode.class, Math.random(), Math.random()));
			}
		};
		
		// Push pipeline onto workspace.
		pipeline = new Pipeline();
		workspace.addSubProcessable(pipeline);
		
		gauge = new GaugeNode();
		pipeline.addSubProcessable(gauge);
		
		noOp1 = new NoOpNode();
		pipeline.addSubProcessable(noOp1);
		noOp2 = new NoOpNode();
		pipeline.addSubProcessable(noOp2);
		
		linkNodes(gauge, noOp1);
		linkNodes(noOp1, noOp2);
		linkNodes(noOp2, gauge);
	}
	
	/**
	 * 
	 */
	@AfterClass
	public static void tearDown() {
	}

	/**
	 * 
	 */
	@Test
	public void test3Nodes() {
		int fps = runTest();
		Assert.assertTrue("Dataflow performance for 3 nodes less than 30000 fps in average. Current value was " + fps, fps > 30000);
	}
	
	/**
	 * 
	 */
	@Test
	public void test10Nodes() {
		for (int i = pipeline.getSubProcessables().size(); i < 10; i++) {
			incrementNodes();
		}

		int fps = runTest();
		Assert.assertTrue("Dataflow performance for 10 nodes less than 2000 fps in average. Current value was " + fps, fps > 2000);
	}
	
	/**
	 * 
	 */
	@Test
	public void test100Nodes() {
		for (int i = pipeline.getSubProcessables().size(); i < 100; i++) {
			incrementNodes();
		}

		int fps = runTest();
		Assert.assertTrue("Dataflow performance for 100 nodes less than 1000 fps in average. Current value was " + fps, fps > 1000);
	}
	
//	/**
//	 * 
//	 */
//	@Test
//	public void test1000Nodes() {
//		for (int i = pipeline.getSubProcessables().size(); i < 1000; i++) {
//			incrementNodes();
//		}
//		runTest(1000);
//	}
//	
//	/**
//	 * 
//	 */
//	@Test
//	public void test2000Nodes() {
//		for (int i = pipeline.getSubProcessables().size(); i < 2000; i++) {
//			incrementNodes();
//		}
//		runTest(10000);
//	}
	
	/**
	 * @return
	 */
	private int runTest() {
		
		workspace.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		workspace.stop();
		
		return gauge.getMeanAverageFPS();
	}
	
	/**
	 * 
	 */
	private void incrementNodes() {
		Collection<Pipe> outgoingPipes = noOp1.getOutgoingPipes();
		
		NoOpNode noOp = new NoOpNode();
		pipeline.addSubProcessable(noOp);
		
		for (Pipe pipe : outgoingPipes) {
			Piping tmpTarget = (Piping) pipe.getTarget();
			
			tmpTarget.removeIncomingPipe(pipe);
			noOp.addIncomingPipe(pipe);
			
			pipe.setTarget(noOp);
			
			linkNodes(noOp, tmpTarget);
		}
	}
	
	/**
	 * @param source
	 * @param target
	 */
	private static void linkNodes(Piping source, Piping target) {
		Pipe pipe = new Pipe();	
		pipe.setSource(source);
		pipe.setTarget(target);
		pipe.setInputTypes(new ArrayList<Class<? extends IData>>(DataUtility.ALL_DATA_TYPES));
		pipe.setOutputTypes(new ArrayList<Class<? extends IData>>(DataUtility.ALL_DATA_TYPES));
		
		source.addOutgoingPipe(pipe);
		target.addIncomingPipe(pipe);
	}
}
