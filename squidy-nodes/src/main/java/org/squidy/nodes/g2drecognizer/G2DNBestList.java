package org.squidy.nodes.g2drecognizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class G2DNBestList {

	public class NBestResult implements Comparable {
		//public static NBestResult Empty = new NBestResult(String.Empty, -1d, -1d, 0d)

		public String Name;
		public double Score;
		public double Distance;
		public double Angle;

		// constructor
		public NBestResult() {
			new NBestResult(null, -1d, -1d, 0d);
		}

		public NBestResult(String name, double score, double distance,
				double angle) {
			Name = name;
			Score = score;
			Distance = distance;
			Angle = angle;
		}

		// sorts in descending order of Score
		public int compareTo(Object obj) {
			if (obj instanceof NBestResult) {
				NBestResult r = (NBestResult) obj;
				if (Score < r.Score)
					return 1;
				else if (Score > r.Score)
					return -1;
				return 0;
			} else
				throw new ClassCastException("object is not a Result");
		}
	}

	private ArrayList<NBestResult> _nBestList;

	public G2DNBestList() {
		_nBestList = new ArrayList<NBestResult>();
	}

	public void AddResult(String name, double score, double distance,
			double angle) {
		NBestResult r = new NBestResult(name, score, distance, angle);
		_nBestList.add(r);
	}

	public void SortDescending() {
		Collections.sort(_nBestList);
	}

	/// <summary>
	/// Gets the gesture name of the top result of the NBestList.
	/// </summary>
	public String getName() {

		if (_nBestList.size() > 0) {
			NBestResult r = _nBestList.get(0);
			return r.Name;
		}
		return null;

	}

	/// <summary>
	/// Gets the [0..1] matching score of the top result of the NBestList.
	/// </summary>
	public double getScore() {

		if (_nBestList.size() > 0) {
			NBestResult r = (NBestResult) _nBestList.get(0);
			return r.Score;
		}
		return -1.0;

	}

	/// <summary>
	/// Gets the average pixel distance of the top result of the NBestList.
	/// </summary>
	public double getDistance() {

		if (_nBestList.size() > 0) {
			NBestResult r = (NBestResult) _nBestList.get(0);
			return r.Distance;
		}
		return -1.0;

	}

	/// <summary>
	/// Gets the average pixel distance of the top result of the NBestList.
	/// </summary>
	public double getAngle() {

		if (_nBestList.size() > 0) {
			NBestResult r = (NBestResult) _nBestList.get(0);
			return r.Angle;
		}
		return 0.0;

	}

	public NBestResult getResult(int index) {

		if (0 <= index && index < _nBestList.size()) {
			return (NBestResult) _nBestList.get(index);
		}
		return null;

	}

	public String[] getNames() {
		String[] s = new String[_nBestList.size()];
		if (_nBestList.size() > 0) {
			for (int i = 0; i < s.length; i++) {
				s[i] = ((NBestResult) _nBestList.get(i)).Name;
			}
		}
		return s;

	}

	public String getNamesString() {

		String s = "";
		if (_nBestList.size() > 0) {
			for (NBestResult r : _nBestList) {
				s += "{" + r.Name + "},";
			}
		}
		return s.substring(0, s.length() - 1);

	}

	public double[] getScores() {

		double[] s = new double[_nBestList.size()];
		if (_nBestList.size() > 0) {
			for (int i = 0; i < s.length; i++) {
				s[i] = ((NBestResult) _nBestList.get(i)).Score;
			}
		}
		return s;

	}

	public String getScoresString() {

		String s = "";
		if (_nBestList.size() > 0) {
			for (NBestResult r : _nBestList) {
				s += "{0:" + r.Score + "},";
			}
		}
		return s.substring(0, s.length() - 1);

	}

}
