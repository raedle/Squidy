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

package org.squidy.nodes.optitrack;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.vecmath.Vector3d;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.IDataContainer;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataGesture;
import org.squidy.manager.data.impl.DataPosition3D;
import org.squidy.manager.data.impl.DataPosition6D;
import org.squidy.manager.model.AbstractNode;
import org.squidy.manager.util.DataUtility;
import org.squidy.manager.util.MathUtility;
import org.squidy.nodes.Keyboard;
import org.squidy.nodes.optitrack.gestures.*;
import org.squidy.nodes.optitrack.utils.TrackingConstant;
import org.squidy.nodes.optitrack.utils.TrackingUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import wiigee.logic.GestureType;



/*<code>StaticGestureRecognizer</code>.
* 
* <pre>
* Date: Jan 29 2010
* Time: 1:35:05 AM
* </pre>
* 
* @author Simon Faeh, <a href="mailto:simon.faeh@uni-konstanz.de">Simon.Faeh@uni-konstanz.de<a/>, University of Konstanz
* 
* @version 
*/
@XmlType(name = "StaticGestureRecognizer")
@Processor(
	name = "Static Gesture Recognizer",
	icon = "/org/squidy/nodes/image/48x48/static-gestures.png",
	description = "Recognizes Static Handgestures",
	types = {Processor.Type.FILTER},
	tags = { "gesture", "handtracking", "optitrack" }
)

public class StaticGestureRecognizer extends AbstractNode {
		

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "gesture1")
	@Property(
		name = "Gestrue Definition File",
		description = "Path to the gesture Definition File (*.xml)"
	)
	@TextField
	private String gestureFile = "D:\\Development\\Optitrack\\TrackingToolProjects\\StaticGestures.xml";

	public final String getGestureFile() {
		return gestureFile;
	}

	public final void setGestureFile(String aGestureFile) {
		this.gestureFile = aGestureFile;
	}
	
	// ################################################################################		
	
	@XmlAttribute(name = "simulateButtons")
	@Property(
			name = "Simulate Mouse-Buttons", 
			description = "Check if gestuers simulates mouse-buttons")
	@CheckBox
	private boolean sendMouseButtons =  false;

	public final boolean getSendMouseButtons() {
		return sendMouseButtons;
	}

	public final void setSendMouseButtons(boolean sendMouseButtons) {
		this.sendMouseButtons = sendMouseButtons;
	}
		
	// ################################################################################	
	
	@XmlAttribute(name = "maxTranslation")
	@Property(
		name = "Maximum Target Translation",
		description = "Maximum translation of the target in a single frame (mm)",
		suffix = " mm"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 100,
		showLabels = true,
		showTicks = true,
		majorTicks = 50,
		minorTicks = 1,
		snapToTicks = true
	)
	private int maxTranslation = 20;

	public int getMaxTranslation() {
		return maxTranslation;
	}

	public void setMaxTranslation(int maxTranslation) {
		this.maxTranslation = maxTranslation;
	}
	
	// ################################################################################	
	
	@XmlAttribute(name = "maxRot")
	@Property(
		name = "Maximum Target Rotation",
		description = "Maximum rotation of the target in a single frame (degrees)",
		suffix = " deg"
	)
	@Slider(
		type = Integer.class,
		minimumValue = 0,
		maximumValue = 10,
		showLabels = true,
		showTicks = true,
		majorTicks = 1,
		minorTicks = 1,
		snapToTicks = true
	)
	private int maxRotation = 4;

	public int getMaxRotation() {
		return maxRotation;
	}

	public void setMaxRotation(int maxRotation) {
		this.maxRotation = maxRotation;
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################	

	private File gestureXML;
	private NodeList gestureLst = null;
	private MathUtility mu = new MathUtility();
	private Document doc;
	
	private double sumOfAllDist, sumOfDist;
	private int currentFrame, leafCounter;
	private double frameMatrix[][];
	///private ArrayList<ArrayList<DataPosition3D>> gestureList;
	//private ArrayList<DataPosition3D> singleGesture;
	private ArrayList<DataPosition3D> fingerAvg;
	private ArrayList<DataPosition3D> fingerMarkers;
	private DataPosition3D rididBodyRoomCords; 
	private DataPosition6D rigidbody;
	private String gestureName;
	private int handSide;
	private int gestureId, gestureIndex, gestureEventId;
	private Octree octree;
	private ArrayList<StaticGesture> gestureList;
	private StaticGesture staticGesture;
	private DataButton dataButton;
	private DataDigital dataDigital;
	private StaticGesture recognizedGesture;
	private int dataKey;
	private int internalFrameCounter;
	
	@Override
	public void onStart() 
	{
		fingerAvg = new ArrayList<DataPosition3D>();
		gestureList = new ArrayList<StaticGesture>();
		octree = new Octree(1,0,1,0,1,0,50);
		recognizedGesture = null;
		try{
			gestureXML = new File(gestureFile);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(gestureXML);
			doc.getDocumentElement().normalize();
			gestureLst = doc.getElementsByTagName("gesture");
			frameMatrix = new double[100][gestureLst.getLength()];
			leafCounter = 1;
			for (int s = 0; s < gestureLst.getLength(); s++) 
			{
				//NodeList fingerLst = gestureLst.getElementsByTagName("finger");
				Node nGesture = gestureLst.item(s);
				Element elFingerLst = (Element) nGesture;
				NodeList fingerLst = elFingerLst.getElementsByTagName("finger");
				gestureName = elFingerLst.getAttribute("gestureName");
				handSide = Integer.parseInt(elFingerLst.getAttribute("handSide"));
				gestureId = Integer.parseInt(elFingerLst.getAttribute("gestureID"));
				gestureEventId = Integer.parseInt(elFingerLst.getAttribute("eventID"));
				staticGesture =  new StaticGesture(gestureName, gestureId, gestureEventId, handSide);
				gestureList.add(gestureIndex,staticGesture);
				for( int q = 0; q < fingerLst.getLength(); q++)
				{
					Node finger = fingerLst.item(q);
					Element elFinger = (Element) finger;
					//System.out.println(elFinger.getAttribute("fingertype"));
					octree.AddNode(Double.valueOf(elFinger.getAttribute("x")),
							Double.valueOf(elFinger.getAttribute("y")),
							Double.valueOf(elFinger.getAttribute("z")),
							staticGesture,Integer.parseInt(elFinger.getAttribute("fingerIndex")),
							gestureName,String.valueOf(handSide));
				}			
			}
		} catch (Exception e)
		{e.printStackTrace();}
	}
		
	private DataPosition3D lastPosition;
	private int currentGestureID, currentEventID = 0;
	private boolean gestureChanged;
	private int tuioCounter=0;
	private int gestureSent = 0;
	private int currentHandSide = 0;
	private Vector3d lastDirection = new Vector3d(0,0,1);
	private DataPosition3D unitDirection = new DataPosition3D(Optitrack.class, 0, 0, 1);
	private double lastAngle = 0;
	
    @Override
	public IDataContainer preProcess(IDataContainer dataContainer) 
    {
    	List<DataPosition6D> rigidBodies = DataUtility.getDataOfType(DataPosition6D.class, dataContainer);
		if (rigidBodies.size() > 0)
		{
			rigidbody = rigidBodies.get(0);
			this.currentFrame = rigidbody.getGroupID();
			rididBodyRoomCords = rigidbody.getClone();
			rididBodyRoomCords = TrackingUtility.Norm2RoomCoordinates(Optitrack.class,rididBodyRoomCords);
			if (lastPosition != null)
			{
				if (mu.euclidDist2D(rididBodyRoomCords, lastPosition) > this.maxTranslation)					
				{
					if (recognizedGesture != null)
					{
						if (gestureSent == 0)
							gestureSent = 1;
						rigidbody.setAttribute(TrackingConstant.GESTUREID, gestureSent + 5500);
						rigidbody.setAttribute(TrackingConstant.HANDSIDE, currentHandSide);
						if (gestureSent > 0)
							rigidbody.setAttribute(TrackingConstant.TUIOID, tuioCounter);
						else
							rigidbody.setAttribute(TrackingConstant.TUIOID, 0);
							
					}
					else
					{
						rigidbody.setAttribute(TrackingConstant.GESTUREID, 5501);
						rigidbody.setAttribute(TrackingConstant.HANDSIDE, currentHandSide);
						if (gestureSent > 0)
							rigidbody.setAttribute(TrackingConstant.TUIOID, tuioCounter);
						else
							rigidbody.setAttribute(TrackingConstant.TUIOID, 0);
					}
					lastPosition = rididBodyRoomCords.getClone();
					return dataContainer;
				}
			}
			lastPosition = rididBodyRoomCords.getClone();
			if (lastDirection != null)
			{
				unitDirection = mu.rotatePoint(unitDirection, rigidbody, rigidbody, false, false);
				Vector3d unitD = new Vector3d(unitDirection.getX(), unitDirection.getY(), unitDirection.getZ());
				if (Math.abs(lastAngle - unitD.angle(lastDirection)) * 100000 > this.maxRotation * 100)
				{
					//System.out.println(Math.abs(lastAngle - unitD.angle(lastDirection)) * 100000);	
					if (recognizedGesture != null)
					{
						if (gestureSent == 0)
							gestureSent = 1;
						rigidbody.setAttribute(TrackingConstant.GESTUREID, gestureSent + 5500);
						rigidbody.setAttribute(TrackingConstant.HANDSIDE, currentHandSide);
						if (gestureSent > 0)
							rigidbody.setAttribute(TrackingConstant.TUIOID, tuioCounter);
						else
							rigidbody.setAttribute(TrackingConstant.TUIOID, 0);
							
					}
					else
					{
						rigidbody.setAttribute(TrackingConstant.GESTUREID, 5501);
						rigidbody.setAttribute(TrackingConstant.TUIOID, 0);
						rigidbody.setAttribute(TrackingConstant.HANDSIDE, 0);
					}
					lastAngle = unitD.angle(lastDirection);
					lastDirection = unitD;
					unitDirection.setX(0);
					unitDirection.setY(0);
					unitDirection.setZ(1);
					return dataContainer;
				}
				lastDirection = unitD;
				unitDirection.setX(0);
				unitDirection.setY(0);
				unitDirection.setZ(1);

				
//				if (lastAngle - unitD.angle(lastDirection) * -10000 > this.maxRotation * 1000)
//				{
//					if (recognizedGesture != null)
//					{
//						rigidbody.setAttribute(TrackingConstant.GESTUREID, gestureSent);
//						rigidbody.setAttribute(TrackingConstant.HANDSIDE, currentHandSide);
//						if (gestureSent > 0)
//							rigidbody.setAttribute(TrackingConstant.TUIOID, tuioCounter);
//						else
//							rigidbody.setAttribute(TrackingConstant.TUIOID, 0);
//							
//					}
//					else
//					{
//						rigidbody.setAttribute(TrackingConstant.GESTUREID, 0);
//						rigidbody.setAttribute(TrackingConstant.TUIOID, 0);
//						rigidbody.setAttribute(TrackingConstant.HANDSIDE, 0);
//					}
//					return dataContainer;
//				}
				

			}
	
			List<DataPosition3D> additionalMarker = DataUtility.getDataOfType(DataPosition3D.class, dataContainer);
			if (additionalMarker.size() == 0)
				return dataContainer;
			additionalMarker.remove(0);
			
			if(additionalMarker.size() > 0)
			{
				if (dataKey > 0)
				{
					System.out.println("init");
				}
				fingerMarkers = new ArrayList<DataPosition3D>(4);
				int arrayPos = 0;
				for (DataPosition3D d3d : additionalMarker)
				{	
					DataPosition3D d3dRot = d3d;
					d3dRot = TrackingUtility.Norm2RoomCoordinates(Optitrack.class, d3dRot);
					d3dRot = mu.rotatePoint(d3dRot, rididBodyRoomCords, rigidbody, false, true);
				}
				Collections.sort(additionalMarker,new SortByY());
				if (additionalMarker.size() >= 4)
				{
					for(int i = 4; i < additionalMarker.size(); i++)
					{
						additionalMarker.remove(i);
						i--;
					}
				}
				Collections.sort(additionalMarker, new SortByX());
				for (DataPosition3D d3d : additionalMarker){
					
					DataPosition3D d3dRot = d3d;
					if (dataKey > 0)
					{
						if (additionalMarker.size() == 4)
						{
						    fingerMarkers.add(d3dRot);
						    System.out.println("SGR finger added");
						}
					}
					OctreeLeaf otl =  (OctreeLeaf)octree.GetNode(d3dRot);
					if (otl != null)
						d3d.setAttribute(TrackingConstant.FINGERINDEX, otl.LeafIndex);
				}
			    
				if (dataKey > 0)
				{
					//insertionSort(fingerMarkers);
					if (fingerAvg.size() == 0)
						fingerAvg = (ArrayList<DataPosition3D>) fingerMarkers.clone();
					for(int i = 0; i < fingerMarkers.size(); i++)
					{
						if (fingerAvg.size() == 4)
						{
							DataPosition3D tmp3d = new DataPosition3D();
							tmp3d.setX((fingerAvg.get(i).getX() + fingerMarkers.get(i).getX()) / 2);
							tmp3d.setY((fingerAvg.get(i).getY() + fingerMarkers.get(i).getY()) / 2);
							tmp3d.setZ((fingerAvg.get(i).getZ() + fingerMarkers.get(i).getZ()) / 2);
							fingerAvg.set(i, tmp3d);
						}	
					}
				}			
				
				double minDist = Double.MAX_VALUE;
				for (StaticGesture sg : gestureList)
				{
	//				System.out.println(sg.getName() + " " + sg.getDistanceSum());				
					if (minDist >= sg.getDistanceSum())
					{
						minDist = sg.getDistanceSum();
	//					if (sg.getDistanceSum() < 55)
	//					{
	//						System.out.println("FOCUS " + sg.getName());
	//					}
							recognizedGesture = sg;
						//System.out.println(sg.getName());
					}
					if (sg.getGestureId() != currentGestureID)
						sg.decRecoCounter();
					sg.setMax();
				}
	//			System.out.println(recognizedGesture.getDistanceSum());
				if (recognizedGesture != null)
				{
					recognizedGesture.incRecoCounter();
					currentGestureID = recognizedGesture.getGestureId();
				}
	
				if (recognizedGesture != null && recognizedGesture.getRecoCounter() > 4)
				{		
					//
//					int counter = 0;
//					if (gestureSent != recognizedGesture.getEventId())
//						System.out.println("CHANGE " + recognizedGesture.getGestureId() + " " + counter++);
					gestureSent = recognizedGesture.getEventId();
					currentHandSide = recognizedGesture.getHandId();
					if (recognizedGesture.getEventId() > 0 )
					{
						if (this.sendMouseButtons)
						{
							if (dataButton == null)
							{
								dataButton = new DataButton(Optitrack.class, recognizedGesture.getEventId()-1, true);
								dataButton.setAttribute(TrackingConstant.GESTURE, recognizedGesture.getName());
								dataButton.setAttribute(TrackingConstant.GESTUREID, recognizedGesture.getEventId());
								publish(dataButton);
							}
						}
					}
					else
					{
						if (this.sendMouseButtons)
						{
							if(dataButton != null)
							{
								dataButton.setFlag(false);
								dataButton.setAttribute(TrackingConstant.GESTURE, recognizedGesture.getName());
								dataButton.setAttribute(TrackingConstant.GESTUREID, recognizedGesture.getEventId());
								publish(dataButton);
								dataButton = null;
							}
						}
					}
					// preparing for TUIO
					if (!sendMouseButtons)
					{
						if (currentEventID != recognizedGesture.getEventId() && recognizedGesture.getEventId() > 0 )
						{							recognizedGesture.incTuioCounter();
							tuioCounter++;
						}
						
						currentEventID = recognizedGesture.getEventId();
						
						//
						if (recognizedGesture.getEventId() == 1)
							rigidbody.setAttribute(TrackingConstant.TUIOID, tuioCounter);
						else
							rigidbody.setAttribute(TrackingConstant.TUIOID, 0);
						rigidbody.setAttribute(TrackingConstant.HANDSIDE, recognizedGesture.getHandId());
						rigidbody.setAttribute(TrackingConstant.GESTUREID, recognizedGesture.getEventId()+ 5500);
						
						//rigidbody.setAttribute(TrackingConstant.TUIOID, tuioCounter);
						System.out.println("GESTURE " + recognizedGesture.getName() + "  " +  recognizedGesture.getEventId() + " "  + recognizedGesture.getRecoCounter() + " " + recognizedGesture.getTuioCounter());
	
					}
				}
				else
				{
					rigidbody.setAttribute(TrackingConstant.GESTUREID, 5501);
					rigidbody.setAttribute(TrackingConstant.HANDSIDE, 0);
					rigidbody.setAttribute(TrackingConstant.TUIOID, 0);
				}
			}
			ArrayList<IData> alIData = new ArrayList<IData>();
			alIData.add(rigidbody);
			for (DataPosition3D  dataPosition3D : additionalMarker)
			{
				alIData.add(TrackingUtility.Room2NormCoordinates(Optitrack.class, dataPosition3D));
			}
			publish(alIData);
			return null;
		}
		return dataContainer;
    }
    
    public void process(DataDigital dataDigital) 
    {
    	if (dataDigital.hasAttribute(Keyboard.KEY_EVENT))
    	{
        	Integer key_event = (Integer) dataDigital.getAttribute(Keyboard.KEY_EVENT);
        	if (dataDigital.getFlag())
        	{
        		if (key_event - KeyEvent.VK_0 >= 0 &&  key_event - KeyEvent.VK_0 <10 )
        		{
        			dataKey = key_event - KeyEvent.VK_0;
        			internalFrameCounter = 0;
        		}
        	}
        	else
        	{    		
        		if (key_event - KeyEvent.VK_0 >= 0 &&  key_event - KeyEvent.VK_0 <10 )
        		{
        			for (DataPosition3D gesture3D : fingerAvg)
	        		{
	        			System.out.println(gesture3D.getX() +" \t " + gesture3D.getY() + " \t " + gesture3D.getZ());
	        		}
        			XMLReplace(dataKey);
        			dataKey = -1;
        		}
        	}
    	}    		
    }
	
    
	private void XMLReplace(int dataKey)
	{
		for (int s = 0; s < gestureLst.getLength(); s++) 
		{
			//NodeList fingerLst = gestureLst.getElementsByTagName("finger");
			Node nGesture = gestureLst.item(s);
			Element elFingerLst = (Element) nGesture;
			if (Integer.parseInt(elFingerLst.getAttribute("gestureID")) == dataKey)
			{
				NodeList fingerLst = elFingerLst.getElementsByTagName("finger");
				for( int q = 0; q < fingerLst.getLength(); q++)
				{
					Node finger = fingerLst.item(q);
					Element elFinger = (Element) finger;
					//System.out.println(elFinger.getAttribute("fingertype"));
					elFinger.setAttribute("x", String.valueOf(fingerAvg.get(q).getX()));
					elFinger.setAttribute("y", String.valueOf(fingerAvg.get(q).getY()));
					elFinger.setAttribute("z", String.valueOf(fingerAvg.get(q).getZ()));
				}
				break;
			}	    
		}
	     //write the content into xml file
	     TransformerFactory transformerFactory = TransformerFactory.newInstance();
	     Transformer transformer;
	     DOMSource source = new DOMSource(doc);
	     StreamResult result =  new StreamResult(new File(gestureFile));
	     try {
			transformer = transformerFactory.newTransformer();
			transformer.transform(source, result);
	     } catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	     } catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	     }
	     onStart();
	}

	
	/**
	 * insertionSort
	 * @param list ArrayList with 3D Positions 
	 */
	public static void insertionSort(ArrayList<DataPosition3D> list) 
	{
	    int firstOutOfOrder, location;
	    DataPosition3D temp;
	    for(firstOutOfOrder = 1; firstOutOfOrder < list.size(); firstOutOfOrder++) 
	    { //Starts at second term, goes until the end of the array.
	    	
	        if(list.get(firstOutOfOrder).getX() < list.get(firstOutOfOrder - 1).getX()) { //If the two are out of order, we move the element to its rightful place.
	            temp = list.get(firstOutOfOrder);
	            location = firstOutOfOrder;
	            do { //Keep moving down the array until we find exactly where it's supposed to go.
	                list.set(location,list.get(location-1));
	                location--;
	            }
	            while (location > 0 && list.get(location-1).getX() > temp.getX());
	            list.set(location,temp);
	        }
	    }
	} 
	
	private class SortByX implements Comparator<DataPosition3D>
	{
		public int compare(DataPosition3D d3d1, DataPosition3D d3d2)
		{
			if (d3d1.getX() == d3d2.getX())
				return 0;
			else if (d3d1.getX() < d3d2.getX())
				return -1;
			else 
				return 1;
		}
	}
	
	private class SortByY implements Comparator<DataPosition3D>
	{
		public int compare(DataPosition3D d3d1, DataPosition3D d3d2)
		{
			if (d3d1.getY() == d3d2.getY())
				return 0;
			else if (d3d1.getY() < d3d2.getY())
				return -1;
			else 
				return 1;
		}
	}
}

