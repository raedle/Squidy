package org.squidy.nodes.g2drecognizer;

import java.io.Serializable;

public class G2DGesture implements Comparable, Serializable{

		private static final long serialVersionUID = -5175411357522600136L;
		
		public String Name;
        public G2DPoint[] RawPoints; // raw points (for drawing) -- read in from XML
        public G2DPoint[] Points;    // resampled points (for matching) -- done when loaded

		public G2DGesture()
		{
			this.Name = null;
            this.RawPoints = null;
            this.Points = null;
		}

        // when a new prototype is made, its raw points are resampled into n equidistantly spaced
        // points, then it is scaled to a preset size and translated to a preset origin. this is
        // the same treatment applied to each candidate stroke, and it allows us to thereafter
        // simply step through each point in each stroke and compare those points' distances.
        // in other words, it removes the challenge of determining corresponding points in each gesture.
        // after resampling, scaling, and translating, we compute the "indicative angle" of the 
        // stroke as defined by the angle between its centroid point and first point.
		public G2DGesture(String name, G2DPoint[] points)
		{
			this.Name = name;
            this.RawPoints = points.clone(); // copy (saved for drawing)

            // resample first (influences calculation of centroid)
            Points = G2DUtils.Resample(points, G2DRecognizer.NumResamplePoints);

            // rotate so that the centroid-to-1st-point is at zero degrees
            double radians = G2DUtils.AngleInRadians(G2DUtils.Centroid(Points), Points[0], false);
            Points = G2DUtils.RotateByRadians(Points, -radians); // undo angle
            
            // scale to a common (square) dimension
            Points = G2DUtils.ScaleTo(Points, G2DRecognizer.ResampleScale);

            // finally, translate to a common origin
            Points = G2DUtils.TranslateCentroidTo(Points, G2DRecognizer.ResampleOrigin);
		}

        public int getDuration()
        {
            
                if (RawPoints.length >= 2)
                {
                    G2DPoint p0 = (G2DPoint) RawPoints[0];
                    G2DPoint pn = (G2DPoint) RawPoints[RawPoints.length - 1];
                    return pn.T - p0.T;
                }
                else
                {
                    return 0;
                }
            
        }

        // sorts in descending order of Score
        public int compareTo(Object obj)
        {
            if (obj instanceof G2DGesture)
            {
                G2DGesture g = (G2DGesture) obj;
                return Name.compareTo(g.Name);
            }
            else throw new ClassCastException("object is not a Gesture");
        }

        /// <summary>
        /// Pulls the gesture name from the file name, e.g., "circle03" from "C:\gestures\circles\circle03.xml".
        /// </summary>
        /// <param name="s"></param>
        /// <returns></returns>
        public static String ParseName(String filename)
        {
            int start = filename.lastIndexOf('\\');
            int end = filename.lastIndexOf('.');
            return filename.substring(start + 1, end - start - 1);
        }


    }

