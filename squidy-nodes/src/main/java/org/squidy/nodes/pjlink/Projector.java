package org.squidy.nodes.pjlink;

/**
 * <code>Projector</code>.
 * 
 * <pre>
 * Date: March 11, 2010
 * Time: 4:56:02 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: Projector.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
public class Projector {
	
	public enum PowerState {
		ON, OFF, UNDEFINED
	}
	
	public enum InputState {
		RGB1, RGB2, VIDEO, SVIDEO, DVI_D, UNDEFINED
	}
	
	private PowerState power = PowerState.UNDEFINED;;
	
	/**
	 * @return
	 */
	public PowerState getPower() {
		return power;
	}

	/**
	 * @param power
	 */
	public void setPower(PowerState power) {
		this.power = power;
	}

	private InputState input = InputState.UNDEFINED;

	/**
	 * @return
	 */
	public InputState getInput() {
		return input;
	}

	/**
	 * @param input
	 */
	public void setInput(InputState input) {
		this.input = input;
	}

	public Projector(){
		
	}
}
