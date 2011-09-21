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

package org.squidy.manager.util;

import java.lang.annotation.Annotation;

import javax.swing.JSlider;

import org.squidy.common.util.ReflectionUtil;
import org.squidy.manager.IBasicControl;
import org.squidy.manager.controls.CheckBox;
import org.squidy.manager.controls.CheckBoxControl;
import org.squidy.manager.controls.ComboBox;
import org.squidy.manager.controls.ComboBoxControl;
import org.squidy.manager.controls.FileChooser;
import org.squidy.manager.controls.FileChooserControl;
import org.squidy.manager.controls.Gauge;
import org.squidy.manager.controls.GaugeControl;
import org.squidy.manager.controls.ImagePanel;
import org.squidy.manager.controls.ImagePanelControl;
import org.squidy.manager.controls.Slider;
import org.squidy.manager.controls.SliderControl;
import org.squidy.manager.controls.Spinner;
import org.squidy.manager.controls.SpinnerControl;
import org.squidy.manager.controls.TextField;
import org.squidy.manager.controls.TextFieldControl;
import org.squidy.manager.controls.ComboBoxControl.ComboBoxItemWrapper;
import org.squidy.manager.data.domainprovider.DomainProvider;


/**
 * <code>ControlUtils</code>.
 * 
 * <pre>
 * Date: Mar 24, 2009
 * Time: 3:33:43 PM
 * </pre>
 * 
 * @author <pre>
 * Roman R&amp;aumldle
 * &lt;a href=&quot;mailto:Roman.Raedle@uni-konstanz.de&quot;&gt;Roman.Raedle@uni-konstanz.de&lt;/a&gt;
 * Human-Computer Interaction Group
 * University of Konstanz
 * </pre>
 * 
 * @version $Id: ControlUtils.java 772 2011-09-16 15:39:44Z raedle $
 * @since 1.0.0
 */
public class ControlUtils {

	/**
	 * @param <T>
	 * @param controlAnnotation
	 * @param value
	 * @return
	 */
	public static final <T> IBasicControl<T, ?> createControl(Annotation controlAnnotation, Object value) {

		if (controlAnnotation instanceof TextField) {
			TextField textField = (TextField) controlAnnotation;
			return (IBasicControl<T, ?>) createTextFieldControl(textField, value);
		}
		else if (controlAnnotation instanceof CheckBox) {
			CheckBox checkBox = (CheckBox) controlAnnotation;
			return (IBasicControl<T, ?>) createCheckBoxControl(checkBox, value);
		}
		else if (controlAnnotation instanceof ComboBox) {
			ComboBox comboBox = (ComboBox) controlAnnotation;
			return (IBasicControl<T, ?>) createComboBoxControl(comboBox, value);
		}
		else if (controlAnnotation instanceof Slider) {
			Slider slider = (Slider) controlAnnotation;
			return (IBasicControl<T, ?>) createSliderControl(slider, value);
		}
		else if (controlAnnotation instanceof Spinner) {
			Spinner spinner = (Spinner) controlAnnotation;
			return (IBasicControl<T, ?>) createSpinnerControl(spinner, value);
		}
		else if (controlAnnotation instanceof ImagePanel) {
			ImagePanel imagePanel = (ImagePanel) controlAnnotation;
			return (IBasicControl<T, ?>) createImagePanelControl(imagePanel, value);
		}
		else if (controlAnnotation instanceof Gauge) {
			Gauge batteryGauge = (Gauge) controlAnnotation;
			return (IBasicControl<T, ?>) createBatteryGaugeControl(batteryGauge, value);
		}
		else if (controlAnnotation instanceof FileChooser) {
			FileChooser fileChooser = (FileChooser) controlAnnotation;
			return (IBasicControl<T, ?>) createFileChooserControl(fileChooser, value);
		}
		return null;
	}
	
	/**
	 * @param bg
	 * @param value
	 * @return
	 */
	public static final GaugeControl createBatteryGaugeControl(Gauge bg, Object value) {
		GaugeControl bgc = new GaugeControl((Number) value);
		return bgc;
	}
	
	/**
	 * @param imagePanel
	 * @param value
	 * @return
	 */
	public static final ImagePanelControl createImagePanelControl(ImagePanel imagePanel, Object value) {
		ImagePanelControl imagePanelControl = new ImagePanelControl();
		return imagePanelControl;
	}
	
	
	/**
	 * @param textField
	 * @param value
	 * @return
	 */
	public static final TextFieldControl createTextFieldControl(TextField textField, Object value) {
		TextFieldControl textFieldControl = new TextFieldControl(value.toString());
		return textFieldControl;
	}

	/**
	 * @param checkBox
	 * @param value
	 * @return
	 */
	public static final CheckBoxControl createCheckBoxControl(CheckBox checkBox, Object value) {
		CheckBoxControl checkBoxControl = new CheckBoxControl((Boolean) value);
		return checkBoxControl;
	}

	/**
	 * @param comboBox
	 * @param value
	 * @return
	 */
	public static final ComboBoxControl createComboBoxControl(ComboBox comboBox, Object value) {
		DomainProvider domainProvider = ReflectionUtil.createInstance(comboBox.domainProvider());

		Object[] values = domainProvider.getValues();

		ComboBoxControl comboBoxControl = new ComboBoxControl(values);
		for (Object o : values) {
			if (o instanceof ComboBoxItemWrapper) {
				ComboBoxItemWrapper wrapper = (ComboBoxItemWrapper) o;
				
				if (value != null && value.equals(wrapper.getValue())) {
					comboBoxControl.getComponent().setSelectedItem(wrapper);
				}
			}
		}

		return comboBoxControl;
	}

	/**
	 * @param slider
	 * @param value
	 * @return
	 */
	public static final SliderControl<Integer> createSliderControl(Slider slider, Object value) {
		Class<? extends Number> type = slider.type();
		Double minimumValue = slider.minimumValue();
		Double maximumValue = slider.maximumValue();
		
		if ((Integer)value <  minimumValue)
			value = (int) minimumValue.doubleValue();
		else if ((Integer)value > maximumValue)
			value = (int) maximumValue.doubleValue();
		
		SliderControl<Integer> sliderControl = new SliderControl<Integer>((Integer) value, minimumValue.intValue(),
				maximumValue.intValue());

		JSlider sliderComponent = sliderControl.getComponent();
		sliderComponent.setPaintLabels(slider.showLabels());
		sliderComponent.setPaintTicks(slider.showTicks());
		sliderComponent.setMajorTickSpacing(((Double) slider.majorTicks()).intValue());
		sliderComponent.setMinorTickSpacing(((Double) slider.minorTicks()).intValue());
		sliderComponent.setSnapToTicks(slider.snapToTicks());

		return sliderControl;
	}
	
	/**
	 * @param spinner
	 * @param value
	 * @return
	 */
	public static final SpinnerControl createSpinnerControl(Spinner spinner, Object value) {
		
		Class<? extends Number> type = spinner.type();
		
		// Security check if type is instance of type.
		value = type.cast(value);
		
		Comparable<? extends Number> minimumValue = (Comparable<? extends Number>) spinner.minimumValue();
		Comparable<? extends Number> maximumValue = (Comparable<? extends Number>) spinner.maximumValue();
		Number step = spinner.step();
		
		if (Integer.class.isAssignableFrom(type)) {
			minimumValue = ((Double) minimumValue).intValue();
			maximumValue = ((Double) maximumValue).intValue();
			step = ((Double) spinner.step()).intValue();
		}
		step = type.cast(step);
		
		SpinnerControl spinnerControl = new SpinnerControl((Number) value, minimumValue, maximumValue, step);
		
		return spinnerControl;
	}
	
	public static final FileChooserControl createFileChooserControl(FileChooser fileChooser, Object value) {
		
		FileChooserControl fileChooserControl = new FileChooserControl((String) value, fileChooser.title());
		
		return fileChooserControl;
	}
}
