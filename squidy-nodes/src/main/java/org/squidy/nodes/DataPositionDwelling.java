/**
 * 
 */
package org.squidy.nodes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.Slider;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataDigital;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>DataPositionDwelling</code>.
 *
 * <pre>
 * Date: Sep 21, 2010
 * Time: 6:48:27 PM
 * </pre>
 *
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: DataPositionDwelling.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 */
@XmlType(name = "DataPositionDwelling")
@Processor(
    name = "DataPosition Dwelling",
    types = { Processor.Type.FILTER },
    tags = { "screen", "dwelling", "position" },
    status = Status.UNSTABLE
)
public class DataPositionDwelling extends AbstractNode {

    // ################################################################################
    // BEGIN OF PROPERTIES
    // ################################################################################
    
    @XmlAttribute(name = "release-threshold")
    @Property(
        name = "Release Threshold",
        suffix = "\u0025"
    )
    @Slider(
        minimumValue = 0,
        maximumValue = 100,
        majorTicks = 50,
        minorTicks = 25,
        showTicks = true,
        showLabels = true,
        type = Integer.class
    )
    private int releaseThreshold = 250;
    
    public int getReleaseThreshold() {
        return releaseThreshold;
    }

    public void setReleaseThreshold(int releaseThreshold) {
        this.releaseThreshold = releaseThreshold;
    }
    
    @XmlAttribute(name = "movement-threshold")
    @Property(
        name = "Movement Threshold",
        suffix = "\u0025"
    )
    @Slider(
        minimumValue = 0,
        maximumValue = 100,
        majorTicks = 50,
        minorTicks = 25,
        showTicks = true,
        showLabels = true,
        type = Integer.class
    )
    private int movementThreshold = 2;
    
    public int getMovementThreshold() {
        return movementThreshold;
    }

    public void setMovementThreshold(int movementThreshold) {
        this.movementThreshold = movementThreshold;
    }

    @XmlAttribute(name = "minimum-dwelling-time")
    @Property(
        name = "Minimum Dwelling Time",
        suffix = "ms"
    )
    @Slider(
        minimumValue = 0,
        maximumValue = 5000,
        majorTicks = 1000,
        minorTicks = 500,
        showTicks = true,
        showLabels = true,
        type = Integer.class
    )
    private int minimumDwellingTime = 750;

    public int getMinimumDwellingTime() {
        return minimumDwellingTime;
    }

    public void setMinimumDwellingTime(int minimumDwellingTime) {
        this.minimumDwellingTime = minimumDwellingTime;
    }
    
    @XmlAttribute(name = "maximum-dwelling-time")
    @Property(
        name = "Maximum Dwelling Time",
        suffix = "ms"
    )
    @Slider(
        minimumValue = 0,
        maximumValue = 5000,
        majorTicks = 1000,
        minorTicks = 500,
        showTicks = true,
        showLabels = true,
        type = Integer.class
    )
    private int maximumDwellingTime = 1000;

    public int getMaximumDwellingTime() {
        return maximumDwellingTime;
    }

    public void setMaximumDwellingTime(int maximumDwellingTime) {
        this.maximumDwellingTime = maximumDwellingTime;
    }
    
    // ################################################################################
    // END OF PROPERTIES
    // ################################################################################

    private DataPosition2D dwellAroundPosition;
    
    private long lastReleaseTime;
    
    public IData process(DataPosition2D dataPosition2D) {
        
        if (System.currentTimeMillis() - lastReleaseTime < releaseThreshold) {
            return null;
        }
        
        if (dwellAroundPosition == null) {
            dwellAroundPosition = dataPosition2D.getClone();
            return dataPosition2D;
        }
        
        long dwellTime = dataPosition2D.getTimestamp() - dwellAroundPosition.getTimestamp();
        double dwellDistance = dwellAroundPosition.distance(dataPosition2D);
        
        if (dwellTime > maximumDwellingTime) {
            dwellAroundPosition = null;
            return dataPosition2D;
        }
        
        if (dwellDistance > ((double) movementThreshold) / 100.0) {
            dwellAroundPosition = null;
            return dataPosition2D;
        }
        
        if (dwellTime > minimumDwellingTime) {
            
            publish(new DataButton(DataPositionDwelling.class, DataButton.BUTTON_1, true));
            
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            publish(new DataButton(DataPositionDwelling.class, DataButton.BUTTON_1, false));
            lastReleaseTime = System.currentTimeMillis();
            return null;
        }
        return dataPosition2D;
    }
}
