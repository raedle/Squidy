/**
 * 
 */
package org.squidy.nodes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.Slider;
import org.squidy.manager.data.DataConstant;
import org.squidy.manager.data.IData;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.impl.DataButton;
import org.squidy.manager.data.impl.DataPosition2D;
import org.squidy.manager.model.AbstractNode;


/**
 * <code>IRPainter</code>.
 *
 * <pre>
 * Date: Oct 8, 2010
 * Time: 2:01:53 PM
 * </pre>
 *
 * @author Roman R&auml;dle, <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman.Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: BlobButton.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.5.0
 */
@XmlType(name = "BlobButton")
@Processor(
    types = { Processor.Type.FILTER },
    name = "Blob Button",
    tags = { "blob", "button", "IR", "multi-touch" }
)
public class BlobButton extends AbstractNode {

    // ################################################################################
    // BEGIN OF PROPERTIES
    // ################################################################################
    
    @XmlAttribute(name = "timeout-threshold")
    @Property(
        name = "Timeout threshold"
    )
    @Slider(
        minimumValue = 0,
        maximumValue = 5000,
        majorTicks = 2500,
        minorTicks = 1000,
        showLabels = true,
        showTicks = true,
        type = Integer.class
    )
    private int timeoutThreshold = 1000;
    
    public int getTimeoutThreshold() {
        return timeoutThreshold;
    }

    public void setTimeoutThreshold(int timeoutThreshold) {
        this.timeoutThreshold = timeoutThreshold;
    }
    
    // ################################################################################
    // END OF PROPERTIES
    // ################################################################################
    
//    private Map<Integer, Long> blobs = new HashMap<Integer, Long>();
//    
//    /* (non-Javadoc)
//     * @see org.squidy.manager.model.AbstractNode#onStop()
//     */
//    @Override
//    public void onStop() {
//        
//        // Clear all positions.
//        blobs.clear();
//        
//        super.onStop();
//    }
//    
//    /* (non-Javadoc)
//     * @see org.squidy.manager.model.AbstractNode#preProcess(org.squidy.manager.data.IDataContainer)
//     */
//    @Override
//    public IDataContainer preProcess(IDataContainer dataContainer) {
//        
//        for (IData data : dataContainer.getData()) {
//            if (data instanceof DataPosition2D) {
//                DataPosition2D dataPosition2D = (DataPosition2D) data;
//                
//                Integer sessionId = -1;
//                if (dataPosition2D.hasAttribute(DataConstant.SESSION_ID)) {
//                    sessionId = (Integer) dataPosition2D.getAttribute(DataConstant.SESSION_ID);
//                }
//                else if (dataPosition2D.hasAttribute(TUIO.SESSION_ID)) {
//                    sessionId = (Integer) dataPosition2D.getAttribute(TUIO.SESSION_ID);
//                }
//                
//                if (sessionId != -1) {
//                    blobs.put(sessionId, System.currentTimeMillis());
//                }
//            }
//        }
//        
//        return super.preProcess(dataContainer);
//    }

    private long lastSeenBlob;
    private boolean buttonPressed = false;
    private final Object LOCK = new Object();
    
    @Override
    public void onStart() {
        super.onStart();
        
        buttonPressed = false;
        lastSeenBlob = -1;
        
        new Thread() {
            
            /* (non-Javadoc)
             * @see java.lang.Thread#run()
             */
            public void run() {
                try {
                    while (isProcessing()) {
                        
                        if (lastSeenBlob == -1) {
                            synchronized (LOCK) {
                                LOCK.wait();
                            }
                        }
                        
                        long diffTime = System.currentTimeMillis() - lastSeenBlob;
                        
                        if (diffTime >= timeoutThreshold) {
                            buttonPressed = false;
                            lastSeenBlob = -1;
                            
                            // .. release button
                            publish(new DataButton(BlobButton.class, DataButton.BUTTON_1, false));
                        }
                        else {
                            sleep(timeoutThreshold - diffTime);
                        }
                    }
                } catch (InterruptedException e) {
                    BlobButton.this.stop();
                }
            };
        }.start();
    }
    
    /**
     * @param dataPosition2D
     * @return
     */
    public IData process(DataPosition2D dataPosition2D) {
        lastSeenBlob = System.currentTimeMillis();
        
        synchronized (LOCK) {
            LOCK.notify();
        }
        
        if (!buttonPressed) {
            buttonPressed = true;
            
            // ..release position and button
            publish(dataPosition2D, new DataButton(BlobButton.class, DataButton.BUTTON_1, true));
            return null;
        }
        
        return dataPosition2D;
    }
}
