package org.squidy.nodes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.TextField;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>HandGesture</code>.
 * 
 * <pre>
 * Date: Jul 09, 2007
 * Time: 11:27:00 AM
 * </pre>
 * 
 * @author Stephanie Foehrenbach, <a
 *         href="mailto:Stephanie.Foehrenbach@uni-konstanz.de">Stephanie.Foehrenbach@uni-konstanz.de</a>,
 *         University of Konstanz
 *         
 * @version $Id: HandGesture.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.2
 */

@XmlType(name = "HandGesture")
@Processor(
	name = "Hand Gesture",
	tags = { "hand gesture", "hand", "gesture", "OptiTrack", "ARTracking" },
	types = { Processor.Type.FILTER }
)
public class HandGesture extends AbstractNode {
	
	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "hand-id")
	@Property(
		name = "Hand Id",
		description = "Id of target on hand (Sf Glove)."
	)
	@TextField
	private String handId = "1";
	
	/**
	 * @return the handId
	 */
	public String getHandId() {
		return handId;
	}

	/**
	 * @param handId
	 *            the handId to set
	 */
	public void setHandId(String handId) {
		this.handId = handId;
	}
	
	
	@XmlAttribute(name = "hand-side-type")
	@Property(
		name = "Hand side type",
		description = "Side of hand (Sf Glove) [left;right]."
	)
	@TextField
	private String handSideType = "right";
	
	/**
	 * @return the handSideType
	 */
	public String getHandSideType() {
		return handSideType;
	}

	/**
	 * @param handSideType
	 *            the handSideType to set
	 */
	public void setHandSideType(String handSideType) {
		this.handSideType = handSideType;
	}

	

	@XmlAttribute(name = "number-fingers")
	@Property(
		name = "Number fingers",
		description = "Number of finger marker on hand (Sf Glove)."
	)
	@TextField
	private int numFingersHand = 2;
	
	/**
	 * @return the numFingersHand
	 */
	public int getNumFingersHand() {
		return numFingersHand;
	}

	/**
	 * @param numFingersHand
	 *            the numFingersHand to set
	 */
	public void setNumFingersHand(int numFingersHand) {
		this.numFingersHand = numFingersHand;

//  ... unterstütze Version eingestellt?
		if (numFingersHand != 2 && numFingersHand != 4){
//      ... wenn nicht als default 2 Finger einstellen
			numFingersHand = 2;
		}
	}
	
	
	@XmlAttribute(name = "hand-extend")
	@Property(
		name = "Hand extend",
		description = "Maximum extend of hand in mm (Sf Glove)."
	)
	@TextField
	private int handExtend = 200;
	
	/**
	 * @return the handExtend
	 */
	public int getHandExtend() {
		return handExtend;
	}

	/**
	 * @param handExtend
	 *            the handExtend to set
	 */
	public void setHandExtend(int handExtend) {
		this.handExtend = handExtend;
		
		if (handExtend < 0){
			handExtend = 0;
		}
	}
	
	@XmlAttribute(name = "marker-1-target-x")
	@Property(
		name = "Marker 1 target X",
		description = "X Value of marker 1 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker1TargetX = 52.293904;
	
	/**
	 * @return the marker1TargetX
	 */
	public Double getMarker1TargetX() {
		return marker1TargetX;
	}

	/**
	 * @param marker1TargetX
	 *            the marker1TargetX to set
	 */
	public void setMarker1TargetX(Double marker1TargetX) {
		this.marker1TargetX = marker1TargetX;
//		setTargetMarker();
	}

	@XmlAttribute(name = "marker-1-target-y")
	@Property(
		name = "Marker 1 target Y",
		description = "Y Value of marker 1 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker1TargetY = 18.217167;
	
	/**
	 * @return the marker1TargetY
	 */
	public Double getMarker1TargetY() {
		return marker1TargetY;
	}

	/**
	 * @param marker1TargetY
	 *            the marker1TargetY to set
	 */
	public void setMarker1TargetY(Double marker1TargetY) {
		this.marker1TargetY = marker1TargetY;
//		setTargetMarker();
	}
	
	@XmlAttribute(name = "marker-1-target-z")
	@Property(
		name = "Marker 1 target Z",
		description = "Z Value of marker 1 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker1TargetZ = 42.236839;
	
	/**
	 * @return the marker1TargetZ
	 */
	public Double getMarker1TargetZ() {
		return marker1TargetZ;
	}

	/**
	 * @param marker1TargetZ
	 *            the marker1TargetZ to set
	 */
	public void setMarker1TargetZ(Double marker1TargetZ) {
		this.marker1TargetZ = marker1TargetZ;
//		setTargetMarker();
	}
	
	@XmlAttribute(name = "marker-2-target-x")
	@Property(
		name = "Marker 2 target X",
		description = "X Value of marker 2 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker2TargetX = 29.04349;
	
	/**
	 * @return the marker2TargetX
	 */
	public Double getMarker2TargetX() {
		return marker2TargetX;
	}

	/**
	 * @param marker2TargetX
	 *            the marker2TargetX to set
	 */
	public void setMarker2TargetX(Double marker2TargetX) {
		this.marker2TargetX = marker2TargetX;
//		setTargetMarker();
	}

	@XmlAttribute(name = "marker-2-target-y")
	@Property(
		name = "Marker 2 target Y",
		description = "Y Value of marker 2 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker2TargetY = 38.460574;
	
	/**
	 * @return the marker2TargetY
	 */
	public Double getMarker2TargetY() {
		return marker2TargetY;
	}

	/**
	 * @param marker2TargetY
	 *            the marker2TargetY to set
	 */
	public void setMarker2TargetY(Double marker2TargetY) {
		this.marker2TargetY = marker2TargetY;
//		setTargetMarker();
	}
	
	@XmlAttribute(name = "marker-2-target-z")
	@Property(
		name = "Marker 2 target Z",
		description = "Z Value of marker 2 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker2TargetZ = 63.027431;
	
	/**
	 * @return the marker2TargetZ
	 */
	public Double getMarker2TargetZ() {
		return marker2TargetZ;
	}

	/**
	 * @param marker2TargetZ
	 *            the marker2TargetZ to set
	 */
	public void setMarker2TargetZ(Double marker2TargetZ) {
		this.marker2TargetZ = marker2TargetZ;
//		setTargetMarker();
	}
	
	@XmlAttribute(name = "marker-3-target-x")
	@Property(
		name = "Marker 3 target X",
		description = "X Value of marker 3 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker3TargetX = 74.792438;
	
	/**
	 * @return the marker3TargetX
	 */
	public Double getMarker3TargetX() {
		return marker3TargetX;
	}

	/**
	 * @param marker3TargetX
	 *            the marker3TargetX to set
	 */
	public void setMarker3TargetX(Double marker3TargetX) {
		this.marker3TargetX = marker3TargetX;
//		setTargetMarker();
	}

	@XmlAttribute(name = "marker-3-target-y")
	@Property(
		name = "Marker 3 target Y",
		description = "Y Value of marker 3 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker3TargetY = 71.941399;
	
	/**
	 * @return the marker3TargetY
	 */
	public Double getMarker3TargetY() {
		return marker3TargetY;
	}

	/**
	 * @param marker3TargetY
	 *            the marker3TargetY to set
	 */
	public void setMarker3TargetY(Double marker3TargetY) {
		this.marker3TargetY = marker3TargetY;
//		setTargetMarker();
	}
	
	@XmlAttribute(name = "marker-3-target-z")
	@Property(
		name = "Marker 3 target Z",
		description = "Z Value of marker 3 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker3TargetZ = 63.394787;
	
	/**
	 * @return the marker3TargetZ
	 */
	public Double getMarker3TargetZ() {
		return marker3TargetZ;
	}

	/**
	 * @param marker3TargetZ
	 *            the marker3TargetZ to set
	 */
	public void setMarker3TargetZ(Double marker3TargetZ) {
		this.marker3TargetZ = marker3TargetZ;
//		setTargetMarker();
	}
	
	@XmlAttribute(name = "marker-4-target-x")
	@Property(
		name = "Marker 4 target X",
		description = "X Value of marker 4 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker4TargetX = 0.0;
	
	/**
	 * @return the marker4TargetX
	 */
	public Double getMarker4TargetX() {
		return marker4TargetX;
	}

	/**
	 * @param marker4TargetX
	 *            the marker4TargetX to set
	 */
	public void setMarker4TargetX(Double marker4TargetX) {
		this.marker4TargetX = marker4TargetX;
//		setTargetMarker();
	}

	@XmlAttribute(name = "marker-4-target-y")
	@Property(
		name = "Marker 4 target Y",
		description = "Y Value of marker 4 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker4TargetY = 0.0;
	
	/**
	 * @return the marker4TargetY
	 */
	public Double getMarker4TargetY() {
		return marker4TargetY;
	}

	/**
	 * @param marker4TargetY
	 *            the marker4TargetY to set
	 */
	public void setMarker4TargetY(Double marker4TargetY) {
		this.marker4TargetY = marker4TargetY;
//		setTargetMarker();
	}
	
	@XmlAttribute(name = "marker-4-target-z")
	@Property(
		name = "Marker 4 target Z",
		description = "Z Value of marker 4 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker4TargetZ = 0.0;
	
	/**
	 * @return the marker4TargetZ
	 */
	public Double getMarker4TargetZ() {
		return marker4TargetZ;
	}

	/**
	 * @param marker4TargetZ
	 *            the marker4TargetZ to set
	 */
	public void setMarker4TargetZ(Double marker4TargetZ) {
		this.marker4TargetZ = marker4TargetZ;
//		setTargetMarker();
	}
	
	@XmlAttribute(name = "marker-5-target-x")
	@Property(
		name = "Marker 5 target X",
		description = "X Value of marker 5 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker5TargetX = 68.685688;
	
	/**
	 * @return the marker5TargetX
	 */
	public Double getMarker5TargetX() {
		return marker5TargetX;
	}

	/**
	 * @param marker5TargetX
	 *            the marker5TargetX to set
	 */
	public void setMarker5TargetX(Double marker5TargetX) {
		this.marker5TargetX = marker5TargetX;
//		setTargetMarker();
	}

	@XmlAttribute(name = "marker-5-target-y")
	@Property(
		name = "Marker 5 target Y",
		description = "Y Value of marker 5 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker5TargetY = -3.94777;
	
	/**
	 * @return the marker5TargetY
	 */
	public Double getMarker5TargetY() {
		return marker5TargetY;
	}

	/**
	 * @param marker5TargetY
	 *            the marker5TargetY to set
	 */
	public void setMarker5TargetY(Double marker5TargetY) {
		this.marker5TargetY = marker5TargetY;
//		setTargetMarker();
}
	
	@XmlAttribute(name = "marker-5-target-z")
	@Property(
		name = "Marker 5 target Z",
		description = "Z Value of marker 5 of hand target (Sf Glove)."
	)
	@TextField
	private Double marker5TargetZ = -8.283098;
	
	/**
	 * @return the marker5TargetZ
	 */
	public Double getMarker5TargetZ() {
		return marker5TargetZ;
	}

	/**
	 * @param marker5TargetZ
	 *            the marker5TargetZ to set
	 */
	public void setMarker5TargetZ(Double marker5TargetZ) {
		this.marker5TargetZ = marker5TargetZ;
//		setTargetMarker();
	}
	
	@XmlAttribute(name = "target-marker-tolerance")
	@Property(
		name = "Target marker tolerance",
		description = "Tolerance for target marker identification in mm (Sf Glove)."
	)
	@TextField
	private int targetMarkerTolerance = 8;
	
	/**
	 * @return the targetMarkerTolerance
	 */
	public int getTargetMarkerTolerance() {
		return targetMarkerTolerance;
	}

	/**
	 * @param targetMarkerTolerance
	 *            the targetMarkerTolerance to set
	 */
	public void setTargetMarkerTolerance(int targetMarkerTolerance) {
		this.targetMarkerTolerance = targetMarkerTolerance;
	}
	
	@XmlAttribute(name = "finger-memory")
	@Property(
		name = "Finger memory",
		description = "Use previous finger Ids for classification (Sf Glove) [-1;1])."
	)
	@TextField
	private int fingerMemory = -1;
	
	/**
	 * @return the fingerMemory
	 */
	public int getFingerMemory() {
		return fingerMemory;
	}

	/**
	 * @param fingerMemory
	 *            the fingerMemory to set
	 */
	public void setFingerMemory(int fingerMemory) {
		this.fingerMemory = fingerMemory;
	}

//	Thresholds for finger classification
	@XmlAttribute(name = "min-dist-z-thumb")
	@Property(
		name = "Minimum distance Z thumb",
		description = "Min Distance Z-Thumb in mm (threshold for finger classification)."
	)
	@TextField
	private double minDistZThumb = 4.0;
	
	/**
	 * @return the minDistZThumb
	 */
	public double getMinDistZThumb() {
		return minDistZThumb;
	}

	/**
	 * @param minDistZThumb
	 *            the minDistZThumb to set
	 */
	public void setMinDistZThumb(double minDistZThumb) {
		this.minDistZThumb = minDistZThumb;
	}

//	Thresholds for finger classification
	@XmlAttribute(name = "min-dist-x-indexmiddle")
	@Property(
		name = "Minimum distance X index middle",
		description = "Min Distance X Index-Middlefinger in mm (threshold for finger classification)."
	)
	@TextField
	private double minDistXIndexMiddle = 10.0;
	
	/**
	 * @return the minDistXIndexMiddle
	 */
	public double getMinDistXIndexMiddle() {
		return minDistXIndexMiddle;
	}

	/**
	 * @param minDistXIndexMiddle
	 *            the minDistXIndexMiddle to set
	 */
	public void setMinDistXIndexMiddle(double minDistXIndexMiddle) {
		this.minDistXIndexMiddle = minDistXIndexMiddle;
	}
	
	
//	Thresholds for finger classification
	@XmlAttribute(name = "min-dist-y-indexmiddle")
	@Property(
		name = "Minimum distance Y index middle",
		description = "Min Distance Y Index-Middlefinger in mm (threshold for finger classification)."
	)
	@TextField
	private double minDistYIndexMiddle = 16.0;
	
	/**
	 * @return the minDistYIndexMiddle
	 */
	public double getMinDistYIndexMiddle() {
		return minDistYIndexMiddle;
	}

	/**
	 * @param minDistYIndexMiddle
	 *            the minDistYIndexMiddle to set
	 */
	public void setMinDistYIndexMiddle(double minDistYIndexMiddle) {
		this.minDistYIndexMiddle = minDistYIndexMiddle;
	}
	
	@XmlAttribute(name = "rotation-angle-DT-y")
	@Property(
		name = "Rotation angle DT Y",
		description = "Rotation angle of target around the y-axis (DTrack Glove)."
	)
	@TextField
	private double rotationAngleDTY = -80.0;
	
	/**
	 * @return the rotationAngleDTY
	 */
	public double getRotationAngleDTY() {
		return rotationAngleDTY;
	}

	/**
	 * @param rotationAngleDTY
	 *            the rotationAngleDTY to set
	 */
	public void setRotationAngleDTY(double rotationAngleDTY) {
		this.rotationAngleDTY = rotationAngleDTY;
	}
	



	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################

	private static final int NUM_TARGET_MARKER = 5;
	
	private Double targetMarkerPosition[][] = new Double[NUM_TARGET_MARKER][3];
	
	/*
	 * @TODO: new DataType adoption required. DataPosition6D, DataPosition3D, DataFiner, DataHand 
	 *
	 */
	
	
//  Offsets of hand target from origin - not fully implemented - future adjustables	
/*	private static double offSetSfX = 0.0;
	private static double offSetSfY = 0.0;
	private static double offSetSfZ = 0.0;

	private Vector<MemoryHelper> vecGroupIdFor3d = new Vector<MemoryHelper>();
	private Vector<MemoryHelper> vecGroupIdFor6d = new Vector<MemoryHelper>();
	private Vector<String> vecKnownTargetBodyIds = new Vector<String>();
	private Vector<DataPosition3D> vec3dCollection = new Vector<DataPosition3D>();
	private Vector<DataPosition6D> vec6dCollection = new Vector<DataPosition6D>();
	private Vector<DataHand> vecPrevHands = new Vector<DataHand>();

	private DataHand hand;
	private DataGlove glove;
	private DataPosition6D pos6dRot;
	private DataPosition6D pos6d;
	private DataPosition3D pos3d;
	private MemoryHelper objGroupIdFor3dMH;
	private MemoryHelper objGroupIdFor6dMH;

	private Vector<DataPosition3D> vec3dGroup = new Vector<DataPosition3D>();
	private Vector<DataPosition6D> vec6dGroup = new Vector<DataPosition6D>();
	private Vector<FingerSet> vecFingerSet = new Vector<FingerSet>();
	private FingerSet fingerSet;
	private DataFinger fingerRk;
	private DataFinger fingerHk;
	private int handPositionIndex;
	private double extend;

	private MathUtility mu = new MathUtility();
	private double[][] m6d = new double[3][3];

	private double logData[][] = new double [3000][17];
	private int logDataIndex;

	/*
	public HandGesture() {
		setTargetMarker();
	}


	// 3D Positionen der Marker des rechten Targets setzen
	private void setTargetMarker(){
		
		// Marker 1 	
		targetMarkerPosition[0][0] = marker1TargetX;
		targetMarkerPosition[0][1] = marker1TargetY;
		targetMarkerPosition[0][2] = marker1TargetZ;
		
		// Marker 2 	
		targetMarkerPosition[1][0] = marker2TargetX;
		targetMarkerPosition[1][1] = marker2TargetY;
		targetMarkerPosition[1][2] = marker2TargetZ;
		
		// Marker 3 	
		targetMarkerPosition[2][0] = marker3TargetX;
		targetMarkerPosition[2][1] = marker3TargetY;
		targetMarkerPosition[2][2] = marker3TargetZ;
		
		// Marker 4 	
		targetMarkerPosition[3][0] = marker4TargetX;
		targetMarkerPosition[3][1] = marker4TargetY;
		targetMarkerPosition[3][2] = marker4TargetZ;
		
		// Marker 5 	
		targetMarkerPosition[4][0] = marker5TargetX;
		targetMarkerPosition[4][1] = marker5TargetY;
		targetMarkerPosition[4][2] = marker5TargetZ;
	}
	

	public void refreshMemory(){
		vecGroupIdFor3d.clear();
		vecGroupIdFor6d.clear();
		vecKnownTargetBodyIds.clear();
		vec3dCollection.clear();
		vec6dCollection.clear();
		vecPrevHands.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	/*public synchronized IData process(DataPosition3D dataPosition3D) {
		boolean elementAdded = false;
		pos3d = dataPosition3D;
		// Wenn 3d Objekt nicht bereits als Element des Handtargets bekannt
		// ist ...
		if (isKnownTarget(pos3d) == false) {
			// ... der allgemeinen 3d Sammlung als potentieller Finger
			// hinzufügen
			vec3dCollection.add(pos3d);
			elementAdded = true;
		}

		if (countDownGroupMembers(pos3d, elementAdded) == 0) {
			if (extractGroup(pos3d.groupID) == true) {
				buildHands();
			}
		}

		reduceCollections();

		return dataPosition3D;
	}

	public synchronized IData process(DataPosition6D dataPosition6D) {
		boolean elementAdded = false;
		pos6d = dataPosition6D;
		// Wenn 6d Objekt als Hand bekannt ist ...
		if (isKnownHand(pos6d) != -1) {
			// ... der allgemeinen 6d Sammlung hinzufügen
			vec6dCollection.add(pos6d);
			elementAdded = true;
		}

		if (countDownGroupMembers(pos6d, elementAdded) == 0) {
			if (extractGroup(pos6d.groupID) == true) {
				buildHands();
			}
		}

		reduceCollections();

		// Wurde das Element als Hand hinzugefügt ...
		if (elementAdded == true)
		// ... kein Element zurückgeben
			return null;
		else
		// ... sonst 6DOF zur Cursorpositionierung zurück
			return dataPosition6D;
	}

	public synchronized IData process(DataGlove dataGlove) {
		return glove2Hand(dataGlove);
	}

	private int countDownGroupMembers(DataPosition3D data, boolean elementAdded){
//		Wenn bereits mit dem Sammeln der Mitglieder von Gruppe data.groupID begonnen wurde ...
		for(int i=vecGroupIdFor3d.size()-1; i >= 0 ; i--)
		{
			if (vecGroupIdFor3d.elementAt(i).identifier == data.groupID)
			{
				if (elementAdded == true){
//				... Anzahl hinzugefügter Elemente erhöhen wenn ein Element hinzugefügt wurde
					vecGroupIdFor3d.elementAt(i).collectedMembers++;
				}
//			... und verbleibende Anzahl noch zu sammelnder Mitglieder reduzieren und zurückgeben
				vecGroupIdFor3d.elementAt(i).remainingMembers--;
				return vecGroupIdFor3d.elementAt(i).remainingMembers;
			}
		}
//		... sonst neue Gruppe der Sammelliste hinzufügen und noch zu sammelnde Mitglieder setzen
		vecGroupIdFor3d.add(new MemoryHelper(data.groupID, data.numGroup-1, 1));
		return data.numGroup-1;
	}

	private int countDownGroupMembers(DataPosition6D data, boolean elementAdded){
//		Wenn bereits mit dem Sammeln der Mitglieder von Gruppe data.groupID begonnen wurde ...
		int i;
		for( i=vecGroupIdFor6d.size()-1; i >= 0 ; i--)
		{
			if (vecGroupIdFor6d.elementAt(i).identifier == data.groupID)
			{
				if (elementAdded == true){
//					... Anzahl hinzugefügter Elemente erhöhen wenn ein Element hinzugefügt wurde
						vecGroupIdFor6d.elementAt(i).collectedMembers++;
				}
//			... verbleibende Anzahl noch zu sammelnder Mitglieder reduzieren und zurückgeben
				vecGroupIdFor6d.elementAt(i).remainingMembers--;
				return vecGroupIdFor6d.elementAt(i).remainingMembers;
			}
		}

//		... sonst neue Gruppe der Sammelliste hinzufügen und noch zu sammelnde Mitglieder setzen
		vecGroupIdFor6d.add(new MemoryHelper(data.groupID, data.numGroup-1, 1));
		return data.numGroup-1;
	}

	private boolean isKnownTarget(DataPosition3D data){
		for(int i=0; i < vecKnownTargetBodyIds.size(); i++)
		{
			if (data.hasAttribute(DataConstant.IDENTIFIER)) {
				if (vecKnownTargetBodyIds.elementAt(i).compareTo((String)
						data.getAttribute(DataConstant.IDENTIFIER)) == 0) {
					return true;
				}
			}
		}

		return false;
	}

	private int isKnownHand(DataPosition6D data){
		if (data.hasAttribute(DataConstant.IDENTIFIER)) {
			return (handId.compareTo((String) data.getAttribute(DataConstant.IDENTIFIER)) == 0) ? 1 : -1;
		}
		return -1;
	}

	private int lookupHandSide(){
		return (handSideType.compareToIgnoreCase("left") == 0)?DataHand.LEFT:DataHand.RIGHT ;
	}
	
	private boolean extractGroup(int groupID){
//		Wenn alle 3d und 6d Mitglieder der Gruppe gesammelt wurden ...
		if (checkGroupIdCollected(groupID) == false) return false;

//	... 6d Mitglieder aus allgemeiner Sammlung extrahieren, sollte es ...
		if (extract6dMembers(groupID) == 0) {
//		... keine 6d Mitglieder geben, die 3d Gruppenmitglieder aus allgemeiner Sammlung löschen
			extract3dMembers(groupID, false);
//		... und erfolglose Extraktion zurückmelden
			return false;
		}
		else
		{
//			... ansonsten 3d Gruppenmitglieder aus allgemeiner Sammlung in Gruppen Sammlung übernehmen
			extract3dMembers(groupID, true);
//			... und erfolgreiche Extraktion zurückmelden
			return true;
		}
	}

//	Prüft ob alle 3d und 6d Mitglieder der Gruppe groupID komplett gesammelt wurden.
//	Wenn ja, werden die Zähler für die Gruppe entfernt und true zurückgeliefert
//	         die Daten für die Gruppe werden dabei in das Feld objGroupIdFor[6d|3d]MH übernommen
//	Wenn nicht, wird false zurückgeliefert und die Zähler bleiben bestehen
	private boolean checkGroupIdCollected(int groupID){
		int i;
		int index3d = -1;
		int index6d = -1;

		for(i=vecGroupIdFor6d.size()-1; i >= 0; i--)
		{
			if (vecGroupIdFor6d.elementAt(i).identifier == groupID)
			{
				index6d = i;
				i = 0;
			}
		}
		for(i=vecGroupIdFor3d.size()-1; i >= 0; i--)
		{
			if (vecGroupIdFor3d.elementAt(i).identifier == groupID)
			{
				index3d = i;
				i = 0;
			}
		}

		if (index3d > -1 && index6d > -1){
//		Wurden alle 3d und 6d der Gruppe groupID gesammelt ...
			if (vecGroupIdFor6d.elementAt(index6d).remainingMembers == 0 &&
					vecGroupIdFor3d.elementAt(index3d).remainingMembers == 0)
//		... Zähler entfernen
			objGroupIdFor6dMH = vecGroupIdFor6d.remove(index6d);
			objGroupIdFor3dMH = vecGroupIdFor3d.remove(index3d);
			return true;
		}
		else
		{
			return false;
		}
	}

//	Entfernt die 6d Mitglieder der Gruppe groupID aus der allgemeinen Sammlung
//	und fügt sie in die gruppenspezifische Sammlung ein
//	Rückgabewert = Anzahl der übernommenen 6d Mitglieder
	private int extract6dMembers(int groupID){
		int anz=0;
		vec6dGroup.clear();

		if (objGroupIdFor6dMH.collectedMembers == 0) return anz;

		for(int i=vec6dCollection.size()-1; i >= 0; i--)
		{
			if (vec6dCollection.elementAt(i).groupID == groupID)
			{
				vec6dGroup.add(vec6dCollection.remove(i));
				anz++;

				if (anz == objGroupIdFor6dMH.collectedMembers){
					i = 0;
				}
			}
		}
		return anz;
	}

//	Entfernt die 3d Mitglieder der Gruppe groupID aus der allgemeinen Sammlung
//	Ist uebernehmen = true werden die Mitglieder in die gruppenspezifische Sammlung eingefügt
	private int extract3dMembers(int groupID, boolean uebernehmen){
		int anz=0;
		vec3dGroup.clear();

		if (objGroupIdFor3dMH.collectedMembers == 0) return anz;

		for(int i=vec3dCollection.size()-1; i >= 0; i--)
		{
			if (vec3dCollection.elementAt(i).groupID == groupID)
			{
				if (uebernehmen == true){
					vec3dGroup.add(vec3dCollection.remove(i));
				}
				else{
					vec3dCollection.remove(i);
				}
				anz++;

				if (anz == objGroupIdFor3dMH.collectedMembers){
					i = 0;
				}
			}
		}
		return anz;
	}

	private void buildHands(){
		int handSide;
		int fingerType;
		int anzClassifiedFingers;

//		Für jedes 6d Objekt der Gruppe eine Hand zusammensetzen
		for (int i=0; i<vec6dGroup.size(); i++){
			anzClassifiedFingers = 0;
			vecFingerSet.clear();

			handSide = lookupHandSide();

			hand = new DataHand(this.getClass(), handSide,
					vec6dGroup.elementAt(i).getX(), vec6dGroup.elementAt(i).getY(), vec6dGroup.elementAt(i).z,
					vec6dGroup.elementAt(i).maxX, vec6dGroup.elementAt(i).maxY, vec6dGroup.elementAt(i).maxZ,
					vec6dGroup.elementAt(i).rxx, vec6dGroup.elementAt(i).ryx, vec6dGroup.elementAt(i).rzx,
					vec6dGroup.elementAt(i).rxy, vec6dGroup.elementAt(i).ryy, vec6dGroup.elementAt(i).rzy,
					vec6dGroup.elementAt(i).rxz, vec6dGroup.elementAt(i).ryz, vec6dGroup.elementAt(i).rzz,
					vec6dGroup.elementAt(i).groupID, vec6dGroup.elementAt(i).numGroup);
			hand.setAttribute(DataConstant.IDENTIFIER, "handSF"+vec6dGroup.elementAt(i).getAttribute(DataConstant.IDENTIFIER));

			hand.handType = DataHand.hSF;
			extend = handExtend;

			lookupPreviousHand(hand);

			for (int j=vec3dGroup.size()-1; j>=0; j--){

//				Wenn das 3d Objekt zu weit von der aktuell bearbeiteten Hand entfernt ist ...
				if (isToRemote(vec3dGroup.elementAt(j), hand) == true){
//				... weiter mit dem nächsten 3d
					continue;
				}

				fingerSet = obj3d2FingerSet(vec3dGroup.elementAt(j), hand);

//				Wenn BodyIDs übernommen werden soll, wenn diese als frühere Finger klassifiziert wurden ...
				if (fingerMemory == 1){
//					... pruefen ob das 3d Objekt schon als Finger bekannt ist ...
					fingerType = isKnownFinger(fingerSet, hand);
					if (fingerType != -1){
//						... Finger als schon bekannter Finger sofort der Hand hinzufügen ...
						setFinger(fingerType, fingerSet, hand, true);
						anzClassifiedFingers++;
//						... 3d aus Gruppensammlung löschen
						vec3dGroup.removeElementAt(j);
//						... und weiter mit nächstem 3d
						continue;
					}
				}

//				Wenn das 3d Objekt zum Handtarget gehört ...
				if (isTarget(fingerSet, hand) == true){
//				... id der Targetliste hinzufügen
					if (fingerSet.fingerHk.hasAttribute(DataConstant.IDENTIFIER)) {
						vecKnownTargetBodyIds.add((String) fingerSet.fingerHk.getAttribute(DataConstant.IDENTIFIER));
					}
//				... 3d Objekt aus Gruppensammlung löschen
					vec3dGroup.removeElementAt(j);
//				... und weiter mit nächstem 3d
					continue;
				}

//				Ist das 3d Objekt ein noch unbekannter Finger der Fingerliste zum späteren sortieren hinzufügen ...
				vecFingerSet.add(fingerSet);
//				... und 3d Objekt aus Gruppensammlung löschen
				vec3dGroup.removeElementAt(j);


			}
			classifyAndSetFingers(hand, anzClassifiedFingers);
			setPreviousHand(hand);
			
//			2FingerExtendedIndex - 18600
//			2FingerExtendedIndexPanRechtsLinks - 21739
//			2FingerRingImmerWieder - 39938
//			2FingerRingImmerWieder-reserve - 6303
//			2FingerRingPanRechtsLinks - 12914
//			2FingerStatischRechtsLinks - 37482
//			4FingerExtendedIndexImmerWieder - 3842
//			4FingerExtendedIndexPanRechtsLinks - 5774
//			4FingerRingImmerWieder - 8808
//			4FingerRingPanRechtsLinks - 13396
//			4FingerStatischRechtsLinks - 29663
//			
//			0_2FingersRingImmerWiederSchraeg - 73000
//			0_2FingerRingImmerWieder - 69262
//			0_2FingersRingPanRechtsLinks - 74925
//			0_2FingersRingPanRechtsLinksSchraeg - 77300
//			0_2FingersExtIndexImmerWiederSchraeg - 86100
//			0_2FingersExtIndexPanRechtsLinksSchraeg - 89901
			
//			logData(hand, 89901);
			
			pushSample(hand);
		}


	}

	private void logData(DataHand hand, int printIndex){

		if (hand.thumb != null){
			logData[logDataIndex][0] = Integer.parseInt((String) hand.thumb.getAttribute(DataConstant.IDENTIFIER));
			logData[logDataIndex][1] = hand.thumb.getX();
			logData[logDataIndex][2] = hand.thumb.getY();
			logData[logDataIndex][3] = hand.thumb.z;

		}
		else{
			logData[logDataIndex][0] = -1;
			logData[logDataIndex][1] = -1;
			logData[logDataIndex][2] = -1;
			logData[logDataIndex][3] = -1;
		}
		if (hand.indexFinger != null){
			logData[logDataIndex][4+0] = Integer.parseInt((String) hand.indexFinger.getAttribute(DataConstant.IDENTIFIER));
			logData[logDataIndex][4+1] = hand.indexFinger.getX();
			logData[logDataIndex][4+2] = hand.indexFinger.getY();
			logData[logDataIndex][4+3] = hand.indexFinger.z;
		}
		else{
			logData[logDataIndex][4+0] = -1;
			logData[logDataIndex][4+1] = -1;
			logData[logDataIndex][4+2] = -1;
			logData[logDataIndex][4+3] = -1;
		}
		if (hand.middleFinger != null){
			logData[logDataIndex][8+0] = Integer.parseInt((String) hand.middleFinger.getAttribute(DataConstant.IDENTIFIER));
			logData[logDataIndex][8+1] = hand.middleFinger.getX();
			logData[logDataIndex][8+2] = hand.middleFinger.getY();
			logData[logDataIndex][8+3] = hand.middleFinger.z;
		}
		else{
			logData[logDataIndex][8+0] = -1;
			logData[logDataIndex][8+1] = -1;
			logData[logDataIndex][8+2] = -1;
			logData[logDataIndex][8+3] = -1;
		}
		if (hand.littleFinger != null){
			logData[logDataIndex][12+0] = Integer.parseInt((String) hand.littleFinger.getAttribute(DataConstant.IDENTIFIER));
			logData[logDataIndex][12+1] = hand.littleFinger.getX();
			logData[logDataIndex][12+2] = hand.littleFinger.getY();
			logData[logDataIndex][12+3] = hand.littleFinger.z;
		}
		else{
			logData[logDataIndex][12+0] = -1;
			logData[logDataIndex][12+1] = -1;
			logData[logDataIndex][12+2] = -1;
			logData[logDataIndex][12+3] = -1;
		}
		logData[logDataIndex][16] = hand.groupID;
		logDataIndex ++;
		if (hand.groupID > printIndex){
			System.out.println("Thumb id; Thumb x; Thumb y; Thumb z; Index id; Index x; Index y; Index z; Middle id; Middle x; Middle y; Middle z; Little id; Little x; Little y; Little z; GroupID; ");
			for (int i = 0; i<3000; i++){
				if(logData[i][12] == 0){
					i=3000;
					continue;
				}

				System.out.println(
						(int)logData[i][0]+";"+
						(int)logData[i][1]+";"+
						(int)logData[i][2]+";"+
						(int)logData[i][3]+";"+
						(int)logData[i][4]+";"+
						(int)logData[i][5]+";"+
						(int)logData[i][6]+";"+
						(int)logData[i][7]+";"+
						(int)logData[i][8]+";"+
						(int)logData[i][9]+";"+
						(int)logData[i][10]+";"+
						(int)logData[i][11]+";"+
						(int)logData[i][12]+";"+
						(int)logData[i][13]+";"+
						(int)logData[i][14]+";"+
						(int)logData[i][15]+";"+
						(int)logData[i][16]+";");
			}
		}



	}


//	Prüft ob 3d Objekt zu weit entfernt von der hand ist
//	Rückgabewert true = zu weit entfernt, false = nicht zu weit entfernt
	private boolean isToRemote(DataPosition3D obj3d, DataHand hand){
		return (mu.euclidDist(obj3d.getX(), obj3d.getY(), obj3d.z, hand.getX(), hand.getY(), hand.z) > extend)?true:false;
	}

//	Legt für obj3d ein Finger in den Raumkoordinaten an, rechnet diesen
//	in die Handkoordinaten von hand um und gibt beide Finger als FingerSet zurück
	private FingerSet obj3d2FingerSet (DataPosition3D obj3d, DataHand hand){
		fingerRk = new DataFinger(this.getClass(), DataFinger.NOTCLASSIFIED, hand.handSide,
				obj3d.getX(), obj3d.getX(), obj3d.z,
				obj3d.maxX, obj3d.maxY, obj3d.maxZ,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				obj3d.groupID, obj3d.numGroup);
		fingerRk.setAttribute(DataConstant.IDENTIFIER, obj3d.getAttribute(DataConstant.IDENTIFIER));
		fingerHk = new DataFinger(this.getClass(), DataFinger.NOTCLASSIFIED, hand.handSide,
				obj3d.getX(), obj3d.getY(), obj3d.z,
				obj3d.maxX, obj3d.maxY, obj3d.maxZ,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				obj3d.groupID, obj3d.numGroup);
		fingerHk.setAttribute(DataConstant.IDENTIFIER, obj3d.getAttribute(DataConstant.IDENTIFIER));
		
//		zu rotierenden Finger in 3d Vektor schreiben
		double[] f = new double[3];
		f[0] =  fingerRk.getX();
		f[1] =  fingerRk.getY();
		f[2] =  fingerRk.z;

//		Handrückenschwerpunkt als Punkt um welchen rotiert wird in 3d Vektor schreiben
		double[] o = new double[3];
		o[0] =  hand.getX();
		o[1] =  hand.getY();
		o[2] =  hand.z;

// 		Meta Rotationsmatrix übernehmen ? Stimmt das oder müssen die Werte da nicht anderst
//		übernommen werden?
//		m6d = mu.dataPosition6D2matrix(hand);
		m6d[0][0] = hand.rxx;
		m6d[0][1] = hand.ryx;
		m6d[0][2] = hand.rzx;

		m6d[1][0] = hand.rxy;
		m6d[1][1] = hand.ryy;
		m6d[1][2] = hand.rzy;

		m6d[2][0] = hand.rxz;
		m6d[2][1] = hand.ryz;
		m6d[2][2] = hand.rzz;


//		Rotationsmatrix transponieren (-> ergibt inverse, da zurückrotiert werden muss)
		m6d = mu.transpose(m6d);

// 		Punkt rotieren und Werte in Bezug auf 0/0/0 Punkt übernehmen
		f = mu.rotatePoint(f, o, m6d, false);

//		Rotierten Punkt als Finger übernehmen ...
		fingerHk.setX(f[0]);
		fingerHk.setY(f[1]);
		fingerHk.z = f[2];

//		Offsetwerte verrechnen, damit Ursprung im Indexgelenk liegt
		fingerHk.setX(fingerHk.getX() + offSetSfX);
		fingerHk.setY(fingerHk.getY() + offSetSfY);
		fingerHk.z = fingerHk.z + offSetSfZ;
		
//		Bei linker Hand x-werte drehen
		if (hand.handSide == DataHand.LEFT){
			fingerHk.setX(fingerHk.getX() * (-1));
			fingerHk.setY(fingerHk.getY() * (-1));
			fingerHk.z = fingerHk.z * (-1);
		}

		FingerSet fingerSetNew = new FingerSet(fingerRk, fingerHk);
		return fingerSetNew;
	}

//	Schaut nach ob dieser Finger aus einem vorhergehenden Zusammenbau dieser Hand
//	bekannt ist. Wenn ja wird die Identität des jeweiligen Fingers zurückgeliefert
//	ansonsten -1
	private int isKnownFinger(FingerSet fingerSet, DataHand hand){
//		Raus, wenn's keine vorhergehende Hand gibt
		if (handPositionIndex == -1){
			return -1;
		}
//		Raus wenn's keine richtige id (d.h. >0) gibt
		if (((String) fingerSet.fingerHk.getAttribute(DataConstant.IDENTIFIER)).compareTo("0") == 0){
			return -1;
		}

//		Auf Übereinstimmung prüfen und wenn ja Finger in Hand übernehmen, Finger muss dazu vorher auch als entsprechender Finger
//		bestätigt, d.h. als THUMB, ... klassifiziert worden sein
		if (vecPrevHands.elementAt(handPositionIndex).thumb != null)
			if (((String) vecPrevHands.elementAt(handPositionIndex).thumb.getAttribute(DataConstant.IDENTIFIER)).compareTo((String) fingerSet.fingerHk.getAttribute(DataConstant.IDENTIFIER)) == 0
			&& vecPrevHands.elementAt(handPositionIndex).thumb.fingerType == DataFinger.THUMB){
			return DataFinger.THUMB;
		}

		if (vecPrevHands.elementAt(handPositionIndex).indexFinger != null)
			if (((String) vecPrevHands.elementAt(handPositionIndex).indexFinger.getAttribute(DataConstant.IDENTIFIER)).compareTo((String) fingerSet.fingerHk.getAttribute(DataConstant.IDENTIFIER)) == 0
				&& vecPrevHands.elementAt(handPositionIndex).indexFinger.fingerType == DataFinger.INDEXFINGER){
			return DataFinger.INDEXFINGER;

		}

		if (vecPrevHands.elementAt(handPositionIndex).middleFinger != null)
			if (((String) vecPrevHands.elementAt(handPositionIndex).middleFinger.getAttribute(DataConstant.IDENTIFIER)).compareTo((String) fingerSet.fingerHk.getAttribute(DataConstant.IDENTIFIER)) == 0
				&& vecPrevHands.elementAt(handPositionIndex).middleFinger.fingerType == DataFinger.MIDDLEFINGER){
			return DataFinger.MIDDLEFINGER;

		}

		if (vecPrevHands.elementAt(handPositionIndex).littleFinger != null)
			if(((String) vecPrevHands.elementAt(handPositionIndex).littleFinger.getAttribute(DataConstant.IDENTIFIER)).compareTo((String) fingerSet.fingerHk.getAttribute(DataConstant.IDENTIFIER)) == 0
				&& vecPrevHands.elementAt(handPositionIndex).littleFinger.fingerType == DataFinger.LITTLEFINGER){
			return DataFinger.LITTLEFINGER;
		}

		return -1;
	}

//	Fügt das fingerSet der hand als Finger "fingerType" hinzu. Ist classified = true, wird der
//	Finger in der Hand noch als entsprechender Typ klassifiziert/bestätigt.
//	Beispiel: der Daumen soll gesetzt werden:
//	Unabhängig vom classified flag werden die Objekte thumbRk und thumbHk der Hand hand gesetzt
//	mit classified = true werden den beiden Fingern dann noch der fingerType = THUMB zugeordnet
//	mit classified = false werden den Fingern keine Typen zugeordnet, d.h. es steht was in thumbRk
//	und thumbHk aber es ist nicht sicher ob es auch tatsächlich der Daumen ist
	private void setFinger(int fingerType, FingerSet fingerSet, DataHand hand, boolean classified){
		switch(fingerType){
			case(DataFinger.THUMB):
			{
				hand.thumbRk = fingerSet.fingerRk;
				hand.thumb = fingerSet.fingerHk;
				if (classified == true){
					hand.thumbRk.fingerType = DataFinger.THUMB;
					hand.thumb.fingerType = DataFinger.THUMB;
				}
				break;
			}
			case(DataFinger.INDEXFINGER):
			{
				hand.indexFingerRk = fingerSet.fingerRk;
				hand.indexFinger = fingerSet.fingerHk;
				if (classified == true){
					hand.indexFingerRk.fingerType = DataFinger.INDEXFINGER;
					hand.indexFinger.fingerType = DataFinger.INDEXFINGER;
				}
				break;
			}
			case(DataFinger.MIDDLEFINGER):
			{
				hand.middleFingerRk = fingerSet.fingerRk;
				hand.middleFinger = fingerSet.fingerHk;
				if (classified == true){
					hand.middleFingerRk.fingerType = DataFinger.MIDDLEFINGER;
					hand.middleFinger.fingerType = DataFinger.MIDDLEFINGER;
				}
				break;
			}
			case(DataFinger.LITTLEFINGER):
			{
				hand.littleFingerRk = fingerSet.fingerRk;
				hand.littleFinger = fingerSet.fingerHk;
				if (classified == true){
					hand.littleFingerRk.fingerType = DataFinger.LITTLEFINGER;
					hand.littleFinger.fingerType = DataFinger.LITTLEFINGER;
				}
				break;
			}
		}
	}

//	Prüft ob der Finger im fingerSet zum Handtarget der Hand hand gehört
	private boolean isTarget(FingerSet fingerSet, DataHand hand){

		if (((String) hand.getAttribute(DataConstant.IDENTIFIER)).compareTo("hand"+handId) == 0)
		{
			for(int i = 0; i < NUM_TARGET_MARKER; i++){
				if (mu.euclidDist(fingerSet.fingerHk.getX(), fingerSet.fingerHk.getY(), fingerSet.fingerHk.z,
								  targetMarkerPosition[i][0], targetMarkerPosition[i][1], targetMarkerPosition[i][2]) < targetMarkerTolerance ){
					return true;
				}
			}
		}

		return false;
	}

//	Klassifiziert die Finger welche noch keinem bestimmten Finger zugeordnet sind
//	und legt diese dann in der Hand hand an
	private boolean classifyAndSetFingers(DataHand hand, int anzClassifiedFingers){
		int anzRemainingFingers = vecFingerSet.size();

//		Raus, wenn bereits alle Finger zugeordnet sind
		if (anzRemainingFingers == 0) return true;
//		Raus, wenn nicht alle Finger erkannt wurden (4er oder 2er Version möglich
		if (anzClassifiedFingers + anzRemainingFingers != numFingersHand) return false;

//		Finger werden nur klassifiziert, wenn alle 4 bzw. 2 Finger erkannt wurden, wurden weniger oder mehr erkannt werden nur die
//		Finger in die Hand übernommen, die schon aus vorherigen Durchläufen bekannt sind.
//		D.h. die erstmalige Neukonfigurierung wird immer mit allen 4 bzw. 2 Fingern durch geführt.
//		Die Funktion merkt sich dabei welche BodyIds als welche Finger übernommen wurden
//		Bei nachfolgenden Klassifizierungen werden bereits bekannte BodyIds wieder direkt in den vorhergehenden Finger übernommen.
//		Neue BodyIds (wenn DTrack ein Marker verloren und dann wieder gefunden hat) wird dann eine neue Klassifizierung durchgeführt.
//		Die Klassifizierung wird dabei nur durchgeführt, wenn insgesamt 4 bzw. 2 Fingerb getrackt wurden.
//
//		Es ist anzunehmen, daß zu Begin eine entspannte Hand- und Fingerhaltung eingenommen wird, so daß die Klassifikation auf eindeutige
//		Fälle zurückgreifen kann (z.B. Zeige- und Mittelfinger nebeneinander und nicht überkreuzt)
//		Sollten im Laufe der Interaktion unklarere Klassifikationsfälle auftauchen, dürfte das Gedächtnis der Funktion dazu beitragen trotzdem
//		eine hohe Klassifikationsgenauigkeit zu erreichen

//		Constrains zur Fingerbestimmung:
//		Der kleine Finger kommt nach links nie bis zum Mittelfinger -> ist also immer der am weitesten rechte Finger (-> grösster x-Wert)

//		Vorgehensweise für Kleinen Finger:
//		Finger mit grösstem x-Wert ist der kleine Finger

//		1. Kleinen Finger zuordnen, wenn 4 Finger Glove im Einsatz ist ...
		if (numFingersHand == 4){
//         ... und der Finger nicht bereits gesetzt ist
			if (hand.littleFinger == null){
				int maxXPos = -1;
				double maxX = -extend;
	
				for (int i=0; i<anzRemainingFingers; i++){
					if (vecFingerSet.elementAt(i).fingerHk.getX() > maxX){
						maxX = vecFingerSet.elementAt(i).fingerHk.getX();
						maxXPos = i;
					}
				}
	
				setFinger(DataFinger.LITTLEFINGER, vecFingerSet.remove(maxXPos) , hand, true);
				anzRemainingFingers--;
			}
		}


//		Kein Finger kommt direkt unter den Daumen oder noch weiter nach links
//		Der Daumen kommt unter den Zeige- und Mittelfinger, aber nie bis direkt unter den Mittelfinger
//		Daumen kommt nicht weiter nach rechts als der Mittelfinger
//
//		Der Handursprung liegt im, oder knapp über dem Zeigefingergelenk,
//			die z-Achse liegt im Zeigefinger, und geht zum Körper hin,
//		    die x-Achse geht orthogonal vom Zeigefinger in Richtung Mittelfinger weg
//		    die y-Achse geht zu beiden Achsen orthogonal nach oben hin weg
//		 -> z-Wert des Zeigefingers- und Mittelfingers kann nicht positiv werden z-Wert = negativ oder 0
//		 -> z-Wert des Daumens kann nicht negativ werden -> z-Wert = 0 oder positiv
//			Ansatz: Daumen ist der Finger mit dem positiven (grössten) z-Wert, Zeige- und Mittelfinger haben negative z-Werte
//		    -> theoretisch können die Finger in den Bewegungsgrenzen die gleichen z-Werte erreichen -> der Bereich um 0 ist für alle Finger möglich
//		       Mögliche Fingerpositionen im Grenzbereich (um 0), sind die Fälle in denen der Zeige- und Mittelfinger stark angewinkelt sind:
//		       1. Annäherungsposition: Daumen unterhalb im Handinnern, beiden anderen Finger gebeugt: (Finger haben nur oberhalb des Daumens Platz)
//		          Zeige- und Mittelfinger sind über dem Daumen -> Daumen hat auch weiterhin den grössten z-Wert
//		       2. Annäherungsposition: Nur Zeige-und/oder Mittelfinger gebeugt, Daumen links beliebig neben dem Zeigefinger (Zeige-Mittelfinger
//									   können im äussersten Gelenk gestreckt sein):
//		          a) bei Positionierung des Markers auf mittlerem Phalanx des Zeige- und Mittelfingers -> Daumem weiterhin den grössten z-Wert
//				  b) bei Positionierung des Markers auf äusserem Phalanx des Zeigefingers
//					 z-Werte der Finger möglicherweise identisch bzw. mit sehr kleinen Unterschieden, dann ist aber
//		 			 der x-Wert des Daumens immer kleiner, da er links neben dem Zeigefinger ist.
//			   3. Annäherungsposition: Zeige-und/oder Mittelfinger gebeugt, Daumen liegt vor dem Zeige-, und/oder Mittelfinger (Zeige-Mittelfinger
//		   								müssen im äussersten Gelenk angewinkelt sein):
//				  a) bei Positionierung des Markers auf mittlerem Phalanx des Zeige- und Mittelfingers -> Daumen weiterhin den grössten z-Wert
//				  b) bei Positionierung des Markers auf äusserem Phalanx des Zeigefingers: Positionierung des Daumens nur möglich auf unterem
//					 Phalanx des Mittelfingers, dann kommt der Zeigefinger, aber nicht an dem Marker auf dem Daumen vorbei (in Richtung neg->
//					 positiver z-Richtung -> soweit die Fingerstellung möglich ist, hat der Daumen den grössten z-Wert


//		Vorgehensweise für Daumen: (kleiner Finger muss schon weg sein)
//		Finger mit grösstem z-Wert suchen. Haben z-Werte anderer Finger einen z-Wert > z-Wert-Daumen - 4 (näher als 4 mm am Daumen-z-Wert)
//		ist von den nahen Fingern der mit dem kleinsten x-Wert der Daumen (nur bei Fingerposition 2.b) also mit Marker auf äusserem
//		Zeigefingerphalanx )
//		Ermittlung des Abstandwertes: Zeigefinger so weit wie möglich nach unten hinten abgebeugt, äusserstes Gelenk dabei
//		gestreckt. Daumen liegt links am Zeigefinger nach vorne. Wert= Wenn Zeigefinger einen grösseren Z-Wert als der Daumen hat ->
//		Abstand in z-Werten zwischen beiden Fingern (positiv). Hat der Daumen den grössten Z-Wert (trotz DTrack Messschwankungen) ist der Wert = 0;
//      Wird ein Wert gesetzt, diesen lieber noch 1-2 mm grösser angeben um Messschwankungen zu berücksichtigen -> also auch wenn der Daumen
//		eigentlich einen grösseren z-Wert hat, die DTrack Werte für die beiden Finger sich aber nicht eindeutig unterscheiden und mal grösser/kleiner
//		werden.

//		2. Daumen klassifizieren
		if (hand.thumb == null){
			int maxZPos = -1;
			double maxZ = -extend;
			int minXPos = -1;
			double minX = extend;

//			Groessten z-Wert suchen ...
			for (int i= 0; i<anzRemainingFingers; i++){
				if (vecFingerSet.elementAt(i).fingerHk.z > maxZ){
					maxZ = vecFingerSet.elementAt(i).fingerHk.z;
					maxZPos = i;
				}
			}

//			... den x-Wert und die Position des groessten z-Wert abspeichern
			minXPos = maxZPos;
			minX = vecFingerSet.elementAt(minXPos).fingerHk.getX();

//			Dann die restlichen Finger pruefen, ob da noch einer innerhalb der moeglichen Daumen z-Werte liegt ...
			for (int i = 0; i<anzRemainingFingers; i++){
				if (i == maxZPos) continue;
//				... liegt der z-Wert des Fingers innerhalb der moeglichen Daumen z-Werte
				if (vecFingerSet.elementAt(i).fingerHk.z >= minDistZThumb ){
//					... x-Wert und Position des Fingers speichern, wenn dieser kleiner ist als der bisher kleinste x-Wert
					if (vecFingerSet.elementAt(i).fingerHk.getX() < minX){
						minX = vecFingerSet.elementAt(i).fingerHk.getX();
						minXPos = i;
					}
				}
			}

			setFinger(DataFinger.THUMB, vecFingerSet.remove(minXPos) , hand, true);
			anzRemainingFingers--;
		}

//		Zeige- und Mittelfinger
//		Es gibt 2 mögliche Fingerpositionen zu unterscheiden:
//		1. Finger liegen nebeneinander:
//		    Zu erkennen :
//				1. x-Werte sind mind. 10 mm voneinander entfernt
//						(Wert: Abstand zwischen den Markern bei aneinanderliegendem Zeige- und Mittelfinger,
//		                oder der Abstand den die Marker haben, wenn der Zeigefinger max. unter/überhalb dem Mittelfinger ist!
//						= grösserer der beiden Werte ergibt den Schwellwert, nicht kleiner werden, lieber 1-2 mm mehr angeben, den
//						sobald der Abstand der Finger grösser ist, werden sie nur in Bezug darauf klassifiziert)
//						-> kleinerer x-Wert -> Zeigefinger
//				2. x-Werte sind weniger als 10 mm voneinander entfernt -> differenz y-Wert < 16 mm ? (Wert: Fingerdicke+Markerhöhe,
//						wenn Zeigefingermarker auf mittlerem Phalanx, Fingerdicke wenn Zeigefingermarker auf äusserem Phalanx)
//		                - zum messen:
//		                  unabhängig von Zeigefingermarkerposition: Zeigefinger unter Mittelfinger durch, und dabei so weit wie möglich
//					      nach rechts oben mit dem Zeigefinger und Mittelfinger so nah an Zeigefinger beugen wie möglich.
//						  Abstand der Marker in dieser Position.
//		                  ergibt den Mindestwert für den Abstand, lieber noch 1-2 mm weniger angeben )
//						  -> ist der Abstand welcher benötigt wird um die Marker untereinander zu positionieren)
//
//						ja = kleinerer x-Wert -> Zeigefinger
//					    nein = Finger liegen unter bzw. übereinander
//		2. Finger liegen unter bzw. übereinander (Position ist im Idealfall schon durch das Fingergedächtnis der Funktion abgefangen)
//			Tritt nur auf, wenn sowohl Zeige- als auch Mittelfinger gestreckt sind. Markerpositionen sind dann mehrdeutig, keine
//			sichere Klassifikation möglich, evtl. Anwendung der Heuristik: oberer Wert ist der Zeigefinger, da dies evtl. beim Zeigen mit
//		    dem Zeigefinger als natürliche Geste vorkommen könnte? -> Übernahme als Zeige- und Mittelfinger aber nicht als classifiziert
//		    kennzeichnen, damit er beim nächsten mal nicht einfach übernommen wird sondern neu klassifiziert wird.

//		Wenn die z-Achse vom Zeigefingergelenk parallel zur Zeigerichtung verläuft (-> entweder entlang dem Zeigefinger, oder etwas links von
//		der Fingerkuppe vorbei, kann der x-Wert des Mittelfingers nicht negativ werden, da der Mittelfinger aus seinem Gelenk heraus nicht
//		so weit nach links bewegt werden kann (Hand so kalibrieren, daß Ursprung über Zeigefingergelenk liegt. Kommt der Mittelfinger in negative
//		x-Bereiche den Ursprung noch etwas weiter nach links verschieben. -> geht das nicht, kann evtl. mit einem offset gearbeitet werden?

//		3. Zeigefinger und Mittelfinger setzen wenn noch nicht gesetzt
//		es dürfen nur noch entweder der Zeigefinger- und/oder der Mittelfinger übrig sein
//		alle anderen Finger - Daumen+kleiner Finger - müssen schon gesetzt sein

//		Wenn nur noch ein Finger übrig ist ... (z.B. bei 2 Finger Version, oder bei 4 Finger, wenn vorherige Finger bereits gesetzt wurden)
		if (anzRemainingFingers == 1){
//		... den noch fehlenden Finger direkt setzen
			if (hand.indexFinger == null){
//          ... zuerst den index Finger abpruefen, damit 2 Finger Version immer Indexfinger und nicht den Middlefinger setzt				
				setFinger(DataFinger.INDEXFINGER, vecFingerSet.remove(0) , hand, true);
				anzRemainingFingers--;
			}
			else if (hand.middleFinger == null){
				setFinger(DataFinger.MIDDLEFINGER, vecFingerSet.remove(0) , hand, true);
				anzRemainingFingers--;
			}
		}
//		sind noch zwei Finger übrig, diese sortieren
		else if (anzRemainingFingers == 2){
			int minXPos = (vecFingerSet.elementAt(0).fingerHk.getX() < vecFingerSet.elementAt(1).fingerHk.getX())?0:1;
			int maxXPos = (minXPos == 0)?1:0;

//		    Wenn der Abstand zwischen den beiden Fingern grösser ist als der kleinste Abstand bei nebeneinanderliegenden Fingern ...
			if ( (vecFingerSet.elementAt(minXPos).fingerHk.getX() - vecFingerSet.elementAt(maxXPos).fingerHk.getX())*(-1.0) > minDistXIndexMiddle ){
//			... ist der Zeigefinger der Finger mit dem kleinsten x-Wert
				setFinger(DataFinger.INDEXFINGER, vecFingerSet.elementAt(minXPos) , hand, true);
				anzRemainingFingers--;
				setFinger(DataFinger.MIDDLEFINGER, vecFingerSet.elementAt(maxXPos) , hand, true);
				anzRemainingFingers--;
			}
			else
			{
//			... sonst schauen ob die Differenz in y-Richtung ausreicht um die Finger untereinander zu bringen
				int minYPos = (vecFingerSet.elementAt(0).fingerHk.getY() < vecFingerSet.elementAt(1).fingerHk.getY())?0:1;
				int maxYPos = (minYPos == 0)?1:0;

//				wenn ja ...
				if ((vecFingerSet.elementAt(minYPos).fingerHk.getY() - vecFingerSet.elementAt(maxYPos).fingerHk.getY())*(-1.0) > minDistYIndexMiddle ){
//				... ist eine Klassifizierung nicht eindeutig möglich - Heuristik: oberer Finger ist Zeigefinger (kommt eher vor, daß der
//					Zeigefinger	oberhalb des Mittelfingers bewegt wird um irgendwo hin zu zeigen, als dass er unterhalb des Mittelfingers geführt
//					wird)
//					Da dies aber nur eine Annahme ist werden die Finger gesetzt aber nicht als eindeutig klassifiziert/bestätigt gekennzeichnet
//					- damit werden sie beim nächsten Lauf erneut klassifiziert und nicht einfach übernommen anhand der BodyId
					setFinger(DataFinger.INDEXFINGER, vecFingerSet.elementAt(maxYPos) , hand, false);
					anzRemainingFingers--;
					setFinger(DataFinger.MIDDLEFINGER, vecFingerSet.elementAt(minYPos) , hand, false);
					anzRemainingFingers--;
				}
//				wenn nicht ...
				else{
//				... ist der Zeigefinger der Finger mit dem kleinsten x-Wert
					setFinger(DataFinger.INDEXFINGER, vecFingerSet.elementAt(minXPos) , hand, true);
					anzRemainingFingers--;
					setFinger(DataFinger.MIDDLEFINGER, vecFingerSet.elementAt(maxXPos) , hand, true);
					anzRemainingFingers--;
				}
			}

		}

		return true;

	}


	private int lookupPreviousHand(DataHand Hand){
//		Liste der vorhergehenden Hände nach Hand durchsuchen
		handPositionIndex = -1;
		for (int k=0; k<vecPrevHands.size(); k++){
			if (((String) vecPrevHands.elementAt(k).getAttribute(DataConstant.IDENTIFIER)).compareTo((String) hand.getAttribute(DataConstant.IDENTIFIER)) == 0)
			{
				handPositionIndex = k;
				k=vecPrevHands.size();
			}
		}

		return handPositionIndex;
	}

//	Fügt die Hand der Liste der vorhergehenden Hände hinzu. Ist die Hand dort bereits
//	enthalten wird das bisherige Element durch das aktuelle ersetzt.
	private void setPreviousHand(DataHand hand){
		if(handPositionIndex > -1){
			vecPrevHands.set(handPositionIndex, hand);
		}
		else
		{
			vecPrevHands.add(hand);

//			Groesse des Vektors pruefen ...
			if (handPositionIndex > 250){
//			... und wenn zu gross die ältesten Hände rauslöschen (Platz für 200 individuelle Hände - sollte genügen)
				for (int i= 0; i<50; i++){
					vecPrevHands.remove(0);
				}
			}
		}
	}

//	Herauslöschen von Elementen aus den Vektoren vec3dCollection, vec6dCollection,  vecGroupIdFor3d, vecGroupIdFor6d
//	welche zur allgemeinen Sammlung dienen, um zu verhindern, daß diese Vektoren zu groß werden
//	und überlaufen, durch Elemente die zwar hinzugefügt, aber nicht mehr entnommen werden
//	Bei DTrack z.B. der Fall, wenn Packete geschickt werden, bei denen entweder
//	- eine 3d Zeile aber keine 6d Zeile, bzw. eine 6d Zeile aber 0 Elemente, vorhanden ist
//	- eine 6d Zeile aber keine 3d Zeile, bzw. eine 3d Zeile aber 0 Elemente, vorhanden ist

//	DTrack sendet mit 60 Hz, d.h. 60 mögliche Einträge in den GroupId Vectoren pro Sekunde
//	20 Sekunden wird gewartet (dann sind Daten von der Zeit her uninteressant)-> 20x60-> 1000 Elemente
//	Annahme: 10 6d pro Paket, 40 6d pro Paket (würde bei nur Handdaten für 10 Hände genügen)
//	20 Sekunden wird gewartet -> 10x20x60-> 10000 6d Elemente und 40x20x60 -> 40000 3d Elemente
//	Um genau zu sein könnten auch aus den Collection Vektoren nur die Elemente der im selben
//	Schritt gelöschten Gruppen herausfliegen, wird im Moment aber noch nicht gemacht, schauen ob es auch
//	mit der schnelleren Heuristik geht.
	private void reduceCollections(){
		int frequenz = 60;
		int wartezeit = 20;
		int anz3dproPaket = 40;
		int anz6dproPaket = 10;
		int loeschIntervall = 500;


//		3d Gruppe reduzieren
		if (vecGroupIdFor3d.size() > frequenz*wartezeit+loeschIntervall){
			for (int i=0; i<loeschIntervall; i++)
				vecGroupIdFor3d.removeElementAt(0);
		}

//		6d Gruppe reduzieren
		if (vecGroupIdFor6d.size() > frequenz*wartezeit+loeschIntervall){
			for (int i=0; i<loeschIntervall; i++)
				vecGroupIdFor6d.removeElementAt(0);
		}

		if (vec3dCollection.size() > frequenz*wartezeit*anz3dproPaket+loeschIntervall){
			for (int i=0; i<loeschIntervall; i++)
				vec3dCollection.removeElementAt(0);
		}

		if (vec6dCollection.size() > frequenz*wartezeit*anz6dproPaket+loeschIntervall){
			for (int i=0; i<loeschIntervall; i++)
				vec6dCollection.removeElementAt(0);
		}

		if (vecKnownTargetBodyIds.size() > 1000){
			for (int i=0; i<500; i++)
				vecKnownTargetBodyIds.removeElementAt(0);

		}

	}

	private IData glove2Hand(IData data) {
		glove = (DataGlove) data;

		hand = new DataHand(this.getClass(), glove.handSide, glove.getX(), glove.getY(), glove.z,
				glove.maxX, glove.maxY, glove.maxZ, glove.rxx, glove.ryx, glove.rzx, glove.rxy, glove.ryy, glove.rzy,
				glove.rxz, glove.ryz, glove.rzz, glove.groupID, glove.numGroup);
		hand.setAttribute(DataConstant.IDENTIFIER, "hand" + glove.getAttribute(DataConstant.IDENTIFIER));

		hand.handType = DataHand.hDTRACK;
		hand.thumb = glove.thumb;
		hand.indexFinger = glove.indexFinger;
		hand.middleFinger = glove.middleFinger;

		pos6dRot = rotate6d(hand, rotationAngleDTY);

		hand.rxx = pos6dRot.rxx;
		hand.rxy = pos6dRot.rxy;
		hand.rxz = pos6dRot.rxz;
		hand.ryx = pos6dRot.ryx;
		hand.ryy = pos6dRot.ryy;
		hand.ryz = pos6dRot.ryz;
		hand.rzx = pos6dRot.rzx;
		hand.rzy = pos6dRot.rzy;
		hand.rzz = pos6dRot.rzz;

		pushSample(hand);
		return null;
	}


	private DataPosition6D rotate6d(DataPosition6D pos6D, double angleAroundY){
		m6d = mu.dataPosition6D2matrix(pos6D);
		m6d = mu.rotateMatrix(MathUtility.Y_AXIS, m6d, angleAroundY);
		return mu.matrix2DataPosition6D(m6d);
	}

	private void pushSample(DataHand data){

		publish(data);
	}

	private class MemoryHelper{
		public int identifier;
		public int remainingMembers;
		public int collectedMembers;

		public MemoryHelper(int identifier, int remainingMembers, int collectedMembers){
			this.identifier = identifier;
			this.remainingMembers = remainingMembers;
			this.collectedMembers = collectedMembers;
		}
	}

	private class FingerSet{
		public DataFinger fingerRk;
		public DataFinger fingerHk;


		public FingerSet(DataFinger fingerRk, DataFinger fingerHk)
		{
			this.fingerRk = fingerRk;
			this.fingerHk = fingerHk;
		}
	}

*/
}
