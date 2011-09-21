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

package org.squidy.nodes.tracking.configclient.service.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.squidy.nodes.tracking.config.xml.Camera;
import org.squidy.nodes.tracking.configclient.service.Service;
import org.squidy.nodes.tracking.configclient.service.comm.CommException;



public abstract class ImageService extends Service {
	// listeners
	protected ArrayList<ImageUpdateListener> listeners = new ArrayList<ImageUpdateListener>();
	
	// image information
	protected int w;
	protected int h;
	protected Rectangle aoi;
	protected Camera camera;

	public ImageService(int w, int h, Rectangle aoi) {
		super();
		this.w = w;
		this.h = h;
		this.aoi = aoi;
	}

	public void addImageUpdateListener(ImageUpdateListener listener) {
		listeners.add(listener);
	}
	
	public void removeImageUpdateListener(ImageUpdateListener listener) {
		listeners.remove(listener);
	}
	
	public abstract void fetchImage() throws CommException;
	
	public abstract BufferedImage getCurrentImage();
	
	public abstract void setAoi(Rectangle aoi);

	protected void fireImageUpdate() {
		for (ImageUpdateListener listener : listeners) {
			listener.imageUpdated();
		}
	}

	@Override
	public Class<? extends Service> getServiceType() {
		return ImageService.class;
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
}
