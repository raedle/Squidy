/**
 * 
 */
package org.squidy.nodes;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.Processor;
import org.squidy.manager.data.Property;
import org.squidy.manager.data.Processor.Status;
import org.squidy.manager.data.domainprovider.DomainProvider;
import org.squidy.manager.model.AbstractNode;
import org.squidy.nodes.pjlink.Projector;
import org.squidy.nodes.pjlink.Projector.InputState;
import org.squidy.nodes.pjlink.Projector.PowerState;


/**
 * <code>PjLink</code>.
 * 
 * <pre>
 * Date: March 11, 2010
 * Time: 4:54:02 PM
 * </pre>
 * 
 * @author Roman R&auml;dle, <a
 *         href="mailto:Roman.Raedle@uni-konstanz.de">Roman.
 *         Raedle@uni-konstanz.de</a>, University of Konstanz
 * @version $Id: PjLink.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.1.0
 */
@XmlType(name = "PjLink")
@Processor(
	name = "PjLink",
	types = { Processor.Type.INPUT, Processor.Type.OUTPUT },
	tags = { "" },
	status = Status.UNSTABLE
)
public class PjLink extends AbstractNode {

	// ################################################################################
	// BEGIN OF ADJUSTABLES
	// ################################################################################

	@XmlAttribute(name = "input")
	@Property(name = "Input")
	@ComboBox(domainProvider = InputDomainProvider.class)
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
		
		sendInput(input);
	}
	
	public static class InputDomainProvider implements DomainProvider {

		public Object[] getValues() {
			ComboBoxItemWrapper[] values = new ComboBoxItemWrapper[5];
			values[0] = new ComboBoxItemWrapper(InputState.RGB1, "RGB1");
			values[1] = new ComboBoxItemWrapper(InputState.RGB2, "RGB2");
			values[2] = new ComboBoxItemWrapper(InputState.DVI_D, "DVI-D");
			values[3] = new ComboBoxItemWrapper(InputState.VIDEO, "VIDEO");
			values[4] = new ComboBoxItemWrapper(InputState.SVIDEO, "SVIDEO");
			return values;
		}
	}
	
	// ################################################################################
	// END OF ADJUSTABLES
	// ################################################################################
	
	@Override
	public void onStart() {
		super.onStart();
		
		setPower(PowerState.ON);
	}

	@Override
	public void onStop() {
		super.onStop();
		
		setPower(PowerState.OFF);
	}
	
	private void setPower(PowerState power) {
		Socket pjLinkSocket;
		try {
			System.out.println("Opening connection");
			pjLinkSocket = new Socket("192.168.1.2", 4352);

			DataOutputStream ostream = new DataOutputStream(pjLinkSocket
					.getOutputStream());
			BufferedReader istream = new BufferedReader(new InputStreamReader(
					pjLinkSocket.getInputStream()));

			System.out.println(istream.readLine());

			if (power == PowerState.UNDEFINED) {
				ostream.writeBytes("%1POWR ?\r");
				System.out.println(istream.readLine());
			} else if (power == PowerState.ON) {
				ostream.writeBytes("%1POWR 1\r");
			} else if (power == PowerState.OFF) {
				ostream.writeBytes("%1POWR 0\r");
			}

			ostream.close();
			istream.close();
			pjLinkSocket.close();
			
			System.out.println("DONE");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendInput(InputState input) {
		
		Socket pjLinkSocket;
		try {
			System.out.println("Opening connection");
			pjLinkSocket = new Socket("192.168.1.2", 4352);

			DataOutputStream ostream = new DataOutputStream(pjLinkSocket
					.getOutputStream());
			BufferedReader istream = new BufferedReader(new InputStreamReader(
					pjLinkSocket.getInputStream()));

			System.out.println(istream.readLine());

			if (input == InputState.UNDEFINED) {
				ostream.writeBytes("%1INPT ?\r");
				System.out.println(istream.readLine());
			} else if (input == InputState.RGB1) {
				ostream.writeBytes("%1INPT 11\r");
			} else if (input == InputState.RGB2) {
				ostream.writeBytes("%1INPT 12\r");
			} else if (input == InputState.VIDEO) {
				ostream.writeBytes("%1INPT 21\r");
			} else if (input == InputState.SVIDEO) {
				ostream.writeBytes("%1INPT 22\r");
			} else if (input == InputState.DVI_D) {
				ostream.writeBytes("%1INPT 31\r");
			}

			ostream.close();
			istream.close();
			pjLinkSocket.close();
			
			System.out.println("DONE");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Projector projector = new Projector();

		for (String s : args) {
			// Turn power on
			if (s.equals("on") && projector.getPower() == PowerState.UNDEFINED) {
				projector.setPower(PowerState.ON);
				System.out.println("Turning Power On");
			}

			// Turn power off
			if (s.equals("off") && !PowerState.OFF.equals(projector.getPower())) {
				projector.setPower(PowerState.OFF);
				System.out.println("Turning Power Off");
			}

			// Switch Input to: rgb1
			if (s.equals("rgb1")
					&& !InputState.RGB1.equals(projector.getInput())) {
				projector.setInput(InputState.RGB1);
				System.out.println("Switching Input to RGB1");
			}
			// Switch Input to: rgb2
			if (s.equals("rgb2")
					&& !InputState.RGB2.equals(projector.getInput())) {
				projector.setInput(InputState.RGB2);
				System.out.println("Switching Input to RGB2");
			}
			// Switch Input to: video
			if (s.equals("video")
					&& !InputState.VIDEO.equals(projector.getInput())) {
				projector.setInput(InputState.VIDEO);
				System.out.println("Switching Input to VIDEO");
			}
			// Switch Input to: svideo
			if (s.equals("svideo")
					&& !InputState.SVIDEO.equals(projector.getInput())) {
				projector.setInput(InputState.SVIDEO);
				System.out.println("Switching Input to S-VIDEO");
			}
			// Switch Input to: dvi
			if (s.equals("dvi-d")
					&& !InputState.DVI_D.equals(projector.getInput())) {
				projector.setInput(InputState.DVI_D);
				System.out.println("Switching Input to DVI-D");
			}
		}

		Socket pjLinkSocket;
		try {
			System.out.println("Opening connection");
			pjLinkSocket = new Socket("192.168.1.2", 4352);

			DataOutputStream ostream = new DataOutputStream(pjLinkSocket
					.getOutputStream());
			BufferedReader istream = new BufferedReader(new InputStreamReader(
					pjLinkSocket.getInputStream()));

			System.out.println(istream.readLine());

			if (projector.getInput() == InputState.UNDEFINED
					&& projector.getPower() == PowerState.UNDEFINED) {
				System.out.println("\n\nSyntax:\n");
				System.out
						.println("\tTurn on/off projector: java pjLink.PjLinkCom on|off");
				System.out
						.println("\tSwitch input on projector: java pjLink.PjLinkCom rgb1|rgb2|video|svideo|dvi-d");
				System.out
						.println("\n\tExample: java pjLink.PjLinkCom on rgb2");
			}

			if (projector.getInput() == InputState.UNDEFINED) {
				ostream.writeBytes("%1INPT ?\r");
				System.out.println(istream.readLine());
			} else if (projector.getInput() == InputState.RGB1) {
				ostream.writeBytes("%1INPT 11\r");
			} else if (projector.getInput() == InputState.RGB2) {
				ostream.writeBytes("%1INPT 12\r");
			} else if (projector.getInput() == InputState.VIDEO) {
				ostream.writeBytes("%1INPT 21\r");
			} else if (projector.getInput() == InputState.SVIDEO) {
				ostream.writeBytes("%1INPT 22\r");
			} else if (projector.getInput() == InputState.DVI_D) {
				ostream.writeBytes("%1INPT 31\r");
			}

			if (projector.getPower() == PowerState.UNDEFINED) {
				ostream.writeBytes("%1POWR ?\r");
				System.out.println(istream.readLine());
			} else if (projector.getPower() == PowerState.ON) {
				ostream.writeBytes("%1POWR 1\r");
			} else if (projector.getPower() == PowerState.OFF) {
				ostream.writeBytes("%1POWR 0\r");
			}

			ostream.close();
			istream.close();
			pjLinkSocket.close();
			
			System.out.println("DONE");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
