package org.squidy.nodes.g2drecognizer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class G2DRecognizer {
	private static Log logger = LogFactory.getLog(G2DRecognizer.class);


        public static final int NumResamplePoints = 64;
        private static final double DX = 250.0;
        public static final G2DSize ResampleScale = new G2DSize(DX, DX);
        public static final double Diagonal = Math.sqrt(DX * DX + DX * DX);
        public static final double HalfDiagonal = 0.5 * Diagonal;
        public static final G2DPoint ResampleOrigin = new G2DPoint(0, 0);
        private static final double Phi = 0.5 * (-1 + Math.sqrt(5)); // Golden Ratio

        // batch testing
        private static final int NumRandomTests = 100;

		private Hashtable<String, G2DGesture> _gestures;
		
		String filename = "gestureModel2D.ser";
	
		public G2DRecognizer()
		{
            
			
			_gestures = new Hashtable<String, G2DGesture>(256);
            
//            G2DGesture[] templates = new G2DGesture[16];
//        	templates[0] = new G2DGesture("triangle", new G2DPoint[]{new G2DPoint(137,139),new G2DPoint(135,141),new G2DPoint(133,144),new G2DPoint(132,146),new G2DPoint(130,149),new G2DPoint(128,151),new G2DPoint(126,155),new G2DPoint(123,160),new G2DPoint(120,166),new G2DPoint(116,171),new G2DPoint(112,177),new G2DPoint(107,183),new G2DPoint(102,188),new G2DPoint(100,191),new G2DPoint(95,195),new G2DPoint(90,199),new G2DPoint(86,203),new G2DPoint(82,206),new G2DPoint(80,209),new G2DPoint(75,213),new G2DPoint(73,213),new G2DPoint(70,216),new G2DPoint(67,219),new G2DPoint(64,221),new G2DPoint(61,223),new G2DPoint(60,225),new G2DPoint(62,226),new G2DPoint(65,225),new G2DPoint(67,226),new G2DPoint(74,226),new G2DPoint(77,227),new G2DPoint(85,229),new G2DPoint(91,230),new G2DPoint(99,231),new G2DPoint(108,232),new G2DPoint(116,233),new G2DPoint(125,233),new G2DPoint(134,234),new G2DPoint(145,233),new G2DPoint(153,232),new G2DPoint(160,233),new G2DPoint(170,234),new G2DPoint(177,235),new G2DPoint(179,236),new G2DPoint(186,237),new G2DPoint(193,238),new G2DPoint(198,239),new G2DPoint(200,237),new G2DPoint(202,239),new G2DPoint(204,238),new G2DPoint(206,234),new G2DPoint(205,230),new G2DPoint(202,222),new G2DPoint(197,216),new G2DPoint(192,207),new G2DPoint(186,198),new G2DPoint(179,189),new G2DPoint(174,183),new G2DPoint(170,178),new G2DPoint(164,171),new G2DPoint(161,168),new G2DPoint(154,160),new G2DPoint(148,155),new G2DPoint(143,150),new G2DPoint(138,148),new G2DPoint(136,148)});
//    		templates[1] = new G2DGesture("x", new G2DPoint[]{new G2DPoint(87,142),new G2DPoint(89,145),new G2DPoint(91,148),new G2DPoint(93,151),new G2DPoint(96,155),new G2DPoint(98,157),new G2DPoint(100,160),new G2DPoint(102,162),new G2DPoint(106,167),new G2DPoint(108,169),new G2DPoint(110,171),new G2DPoint(115,177),new G2DPoint(119,183),new G2DPoint(123,189),new G2DPoint(127,193),new G2DPoint(129,196),new G2DPoint(133,200),new G2DPoint(137,206),new G2DPoint(140,209),new G2DPoint(143,212),new G2DPoint(146,215),new G2DPoint(151,220),new G2DPoint(153,222),new G2DPoint(155,223),new G2DPoint(157,225),new G2DPoint(158,223),new G2DPoint(157,218),new G2DPoint(155,211),new G2DPoint(154,208),new G2DPoint(152,200),new G2DPoint(150,189),new G2DPoint(148,179),new G2DPoint(147,170),new G2DPoint(147,158),new G2DPoint(147,148),new G2DPoint(147,141),new G2DPoint(147,136),new G2DPoint(144,135),new G2DPoint(142,137),new G2DPoint(140,139),new G2DPoint(135,145),new G2DPoint(131,152),new G2DPoint(124,163),new G2DPoint(116,177),new G2DPoint(108,191),new G2DPoint(100,206),new G2DPoint(94,217),new G2DPoint(91,222),new G2DPoint(89,225),new G2DPoint(87,226),new G2DPoint(87,224)});
//    		templates[2] = new G2DGesture("rectangle", new G2DPoint[]{new G2DPoint(78,149),new G2DPoint(78,153),new G2DPoint(78,157),new G2DPoint(78,160),new G2DPoint(79,162),new G2DPoint(79,164),new G2DPoint(79,167),new G2DPoint(79,169),new G2DPoint(79,173),new G2DPoint(79,178),new G2DPoint(79,183),new G2DPoint(80,189),new G2DPoint(80,193),new G2DPoint(80,198),new G2DPoint(80,202),new G2DPoint(81,208),new G2DPoint(81,210),new G2DPoint(81,216),new G2DPoint(82,222),new G2DPoint(82,224),new G2DPoint(82,227),new G2DPoint(83,229),new G2DPoint(83,231),new G2DPoint(85,230),new G2DPoint(88,232),new G2DPoint(90,233),new G2DPoint(92,232),new G2DPoint(94,233),new G2DPoint(99,232),new G2DPoint(102,233),new G2DPoint(106,233),new G2DPoint(109,234),new G2DPoint(117,235),new G2DPoint(123,236),new G2DPoint(126,236),new G2DPoint(135,237),new G2DPoint(142,238),new G2DPoint(145,238),new G2DPoint(152,238),new G2DPoint(154,239),new G2DPoint(165,238),new G2DPoint(174,237),new G2DPoint(179,236),new G2DPoint(186,235),new G2DPoint(191,235),new G2DPoint(195,233),new G2DPoint(197,233),new G2DPoint(200,233),new G2DPoint(201,235),new G2DPoint(201,233),new G2DPoint(199,231),new G2DPoint(198,226),new G2DPoint(198,220),new G2DPoint(196,207),new G2DPoint(195,195),new G2DPoint(195,181),new G2DPoint(195,173),new G2DPoint(195,163),new G2DPoint(194,155),new G2DPoint(192,145),new G2DPoint(192,143),new G2DPoint(192,138),new G2DPoint(191,135),new G2DPoint(191,133),new G2DPoint(191,130),new G2DPoint(190,128),new G2DPoint(188,129),new G2DPoint(186,129),new G2DPoint(181,132),new G2DPoint(173,131),new G2DPoint(162,131),new G2DPoint(151,132),new G2DPoint(149,132),new G2DPoint(138,132),new G2DPoint(136,132),new G2DPoint(122,131),new G2DPoint(120,131),new G2DPoint(109,130),new G2DPoint(107,130),new G2DPoint(90,132),new G2DPoint(81,133),new G2DPoint(76,133)});
//    		templates[3] = new G2DGesture("circle", new G2DPoint[]{new G2DPoint(127,141),new G2DPoint(124,140),new G2DPoint(120,139),new G2DPoint(118,139),new G2DPoint(116,139),new G2DPoint(111,140),new G2DPoint(109,141),new G2DPoint(104,144),new G2DPoint(100,147),new G2DPoint(96,152),new G2DPoint(93,157),new G2DPoint(90,163),new G2DPoint(87,169),new G2DPoint(85,175),new G2DPoint(83,181),new G2DPoint(82,190),new G2DPoint(82,195),new G2DPoint(83,200),new G2DPoint(84,205),new G2DPoint(88,213),new G2DPoint(91,216),new G2DPoint(96,219),new G2DPoint(103,222),new G2DPoint(108,224),new G2DPoint(111,224),new G2DPoint(120,224),new G2DPoint(133,223),new G2DPoint(142,222),new G2DPoint(152,218),new G2DPoint(160,214),new G2DPoint(167,210),new G2DPoint(173,204),new G2DPoint(178,198),new G2DPoint(179,196),new G2DPoint(182,188),new G2DPoint(182,177),new G2DPoint(178,167),new G2DPoint(170,150),new G2DPoint(163,138),new G2DPoint(152,130),new G2DPoint(143,129),new G2DPoint(140,131),new G2DPoint(129,136),new G2DPoint(126,139)});
//    		templates[4] = new G2DGesture("check", new G2DPoint[]{new G2DPoint(91,185),new G2DPoint(93,185),new G2DPoint(95,185),new G2DPoint(97,185),new G2DPoint(100,188),new G2DPoint(102,189),new G2DPoint(104,190),new G2DPoint(106,193),new G2DPoint(108,195),new G2DPoint(110,198),new G2DPoint(112,201),new G2DPoint(114,204),new G2DPoint(115,207),new G2DPoint(117,210),new G2DPoint(118,212),new G2DPoint(120,214),new G2DPoint(121,217),new G2DPoint(122,219),new G2DPoint(123,222),new G2DPoint(124,224),new G2DPoint(126,226),new G2DPoint(127,229),new G2DPoint(129,231),new G2DPoint(130,233),new G2DPoint(129,231),new G2DPoint(129,228),new G2DPoint(129,226),new G2DPoint(129,224),new G2DPoint(129,221),new G2DPoint(129,218),new G2DPoint(129,212),new G2DPoint(129,208),new G2DPoint(130,198),new G2DPoint(132,189),new G2DPoint(134,182),new G2DPoint(137,173),new G2DPoint(143,164),new G2DPoint(147,157),new G2DPoint(151,151),new G2DPoint(155,144),new G2DPoint(161,137),new G2DPoint(165,131),new G2DPoint(171,122),new G2DPoint(174,118),new G2DPoint(176,114),new G2DPoint(177,112),new G2DPoint(177,114),new G2DPoint(175,116),new G2DPoint(173,118)});
//    		templates[5] = new G2DGesture("caret", new G2DPoint[]{new G2DPoint(79,245),new G2DPoint(79,242),new G2DPoint(79,239),new G2DPoint(80,237),new G2DPoint(80,234),new G2DPoint(81,232),new G2DPoint(82,230),new G2DPoint(84,224),new G2DPoint(86,220),new G2DPoint(86,218),new G2DPoint(87,216),new G2DPoint(88,213),new G2DPoint(90,207),new G2DPoint(91,202),new G2DPoint(92,200),new G2DPoint(93,194),new G2DPoint(94,192),new G2DPoint(96,189),new G2DPoint(97,186),new G2DPoint(100,179),new G2DPoint(102,173),new G2DPoint(105,165),new G2DPoint(107,160),new G2DPoint(109,158),new G2DPoint(112,151),new G2DPoint(115,144),new G2DPoint(117,139),new G2DPoint(119,136),new G2DPoint(119,134),new G2DPoint(120,132),new G2DPoint(121,129),new G2DPoint(122,127),new G2DPoint(124,125),new G2DPoint(126,124),new G2DPoint(129,125),new G2DPoint(131,127),new G2DPoint(132,130),new G2DPoint(136,139),new G2DPoint(141,154),new G2DPoint(145,166),new G2DPoint(151,182),new G2DPoint(156,193),new G2DPoint(157,196),new G2DPoint(161,209),new G2DPoint(162,211),new G2DPoint(167,223),new G2DPoint(169,229),new G2DPoint(170,231),new G2DPoint(173,237),new G2DPoint(176,242),new G2DPoint(177,244),new G2DPoint(179,250),new G2DPoint(181,255),new G2DPoint(182,257)});
//    		templates[6] = new G2DGesture("question", new G2DPoint[]{new G2DPoint(104,145),new G2DPoint(103,142),new G2DPoint(103,140),new G2DPoint(103,138),new G2DPoint(103,135),new G2DPoint(104,133),new G2DPoint(105,131),new G2DPoint(106,128),new G2DPoint(107,125),new G2DPoint(108,123),new G2DPoint(111,121),new G2DPoint(113,118),new G2DPoint(115,116),new G2DPoint(117,116),new G2DPoint(119,116),new G2DPoint(121,115),new G2DPoint(124,116),new G2DPoint(126,115),new G2DPoint(128,114),new G2DPoint(130,115),new G2DPoint(133,116),new G2DPoint(135,117),new G2DPoint(140,120),new G2DPoint(142,121),new G2DPoint(144,123),new G2DPoint(146,125),new G2DPoint(149,127),new G2DPoint(150,129),new G2DPoint(152,130),new G2DPoint(154,132),new G2DPoint(156,134),new G2DPoint(158,137),new G2DPoint(159,139),new G2DPoint(160,141),new G2DPoint(160,143),new G2DPoint(160,146),new G2DPoint(160,149),new G2DPoint(159,153),new G2DPoint(158,155),new G2DPoint(157,157),new G2DPoint(155,159),new G2DPoint(153,161),new G2DPoint(151,163),new G2DPoint(146,167),new G2DPoint(142,170),new G2DPoint(138,172),new G2DPoint(134,173),new G2DPoint(132,175),new G2DPoint(127,175),new G2DPoint(124,175),new G2DPoint(122,176),new G2DPoint(120,178),new G2DPoint(119,180),new G2DPoint(119,183),new G2DPoint(119,185),new G2DPoint(120,190),new G2DPoint(121,194),new G2DPoint(122,200),new G2DPoint(123,205),new G2DPoint(123,211),new G2DPoint(124,215),new G2DPoint(124,223),new G2DPoint(124,225)});
//    		templates[7] = new G2DGesture("arrow", new G2DPoint[]{new G2DPoint(68,222),new G2DPoint(70,220),new G2DPoint(73,218),new G2DPoint(75,217),new G2DPoint(77,215),new G2DPoint(80,213),new G2DPoint(82,212),new G2DPoint(84,210),new G2DPoint(87,209),new G2DPoint(89,208),new G2DPoint(92,206),new G2DPoint(95,204),new G2DPoint(101,201),new G2DPoint(106,198),new G2DPoint(112,194),new G2DPoint(118,191),new G2DPoint(124,187),new G2DPoint(127,186),new G2DPoint(132,183),new G2DPoint(138,181),new G2DPoint(141,180),new G2DPoint(146,178),new G2DPoint(154,173),new G2DPoint(159,171),new G2DPoint(161,170),new G2DPoint(166,167),new G2DPoint(168,167),new G2DPoint(171,166),new G2DPoint(174,164),new G2DPoint(177,162),new G2DPoint(180,160),new G2DPoint(182,158),new G2DPoint(183,156),new G2DPoint(181,154),new G2DPoint(178,153),new G2DPoint(171,153),new G2DPoint(164,153),new G2DPoint(160,153),new G2DPoint(150,154),new G2DPoint(147,155),new G2DPoint(141,157),new G2DPoint(137,158),new G2DPoint(135,158),new G2DPoint(137,158),new G2DPoint(140,157),new G2DPoint(143,156),new G2DPoint(151,154),new G2DPoint(160,152),new G2DPoint(170,149),new G2DPoint(179,147),new G2DPoint(185,145),new G2DPoint(192,144),new G2DPoint(196,144),new G2DPoint(198,144),new G2DPoint(200,144),new G2DPoint(201,147),new G2DPoint(199,149),new G2DPoint(194,157),new G2DPoint(191,160),new G2DPoint(186,167),new G2DPoint(180,176),new G2DPoint(177,179),new G2DPoint(171,187),new G2DPoint(169,189),new G2DPoint(165,194),new G2DPoint(164,196)});
//    		templates[8] = new G2DGesture("left square bracket", new G2DPoint[]{new G2DPoint(140,124),new G2DPoint(138,123),new G2DPoint(135,122),new G2DPoint(133,123),new G2DPoint(130,123),new G2DPoint(128,124),new G2DPoint(125,125),new G2DPoint(122,124),new G2DPoint(120,124),new G2DPoint(118,124),new G2DPoint(116,125),new G2DPoint(113,125),new G2DPoint(111,125),new G2DPoint(108,124),new G2DPoint(106,125),new G2DPoint(104,125),new G2DPoint(102,124),new G2DPoint(100,123),new G2DPoint(98,123),new G2DPoint(95,124),new G2DPoint(93,123),new G2DPoint(90,124),new G2DPoint(88,124),new G2DPoint(85,125),new G2DPoint(83,126),new G2DPoint(81,127),new G2DPoint(81,129),new G2DPoint(82,131),new G2DPoint(82,134),new G2DPoint(83,138),new G2DPoint(84,141),new G2DPoint(84,144),new G2DPoint(85,148),new G2DPoint(85,151),new G2DPoint(86,156),new G2DPoint(86,160),new G2DPoint(86,164),new G2DPoint(86,168),new G2DPoint(87,171),new G2DPoint(87,175),new G2DPoint(87,179),new G2DPoint(87,182),new G2DPoint(87,186),new G2DPoint(88,188),new G2DPoint(88,195),new G2DPoint(88,198),new G2DPoint(88,201),new G2DPoint(88,207),new G2DPoint(89,211),new G2DPoint(89,213),new G2DPoint(89,217),new G2DPoint(89,222),new G2DPoint(88,225),new G2DPoint(88,229),new G2DPoint(88,231),new G2DPoint(88,233),new G2DPoint(88,235),new G2DPoint(89,237),new G2DPoint(89,240),new G2DPoint(89,242),new G2DPoint(91,241),new G2DPoint(94,241),new G2DPoint(96,240),new G2DPoint(98,239),new G2DPoint(105,240),new G2DPoint(109,240),new G2DPoint(113,239),new G2DPoint(116,240),new G2DPoint(121,239),new G2DPoint(130,240),new G2DPoint(136,237),new G2DPoint(139,237),new G2DPoint(144,238),new G2DPoint(151,237),new G2DPoint(157,236),new G2DPoint(159,237)});
//    		templates[9] = new G2DGesture("right square bracket", new G2DPoint[]{new G2DPoint(112,138),new G2DPoint(112,136),new G2DPoint(115,136),new G2DPoint(118,137),new G2DPoint(120,136),new G2DPoint(123,136),new G2DPoint(125,136),new G2DPoint(128,136),new G2DPoint(131,136),new G2DPoint(134,135),new G2DPoint(137,135),new G2DPoint(140,134),new G2DPoint(143,133),new G2DPoint(145,132),new G2DPoint(147,132),new G2DPoint(149,132),new G2DPoint(152,132),new G2DPoint(153,134),new G2DPoint(154,137),new G2DPoint(155,141),new G2DPoint(156,144),new G2DPoint(157,152),new G2DPoint(158,161),new G2DPoint(160,170),new G2DPoint(162,182),new G2DPoint(164,192),new G2DPoint(166,200),new G2DPoint(167,209),new G2DPoint(168,214),new G2DPoint(168,216),new G2DPoint(169,221),new G2DPoint(169,223),new G2DPoint(169,228),new G2DPoint(169,231),new G2DPoint(166,233),new G2DPoint(164,234),new G2DPoint(161,235),new G2DPoint(155,236),new G2DPoint(147,235),new G2DPoint(140,233),new G2DPoint(131,233),new G2DPoint(124,233),new G2DPoint(117,235),new G2DPoint(114,238),new G2DPoint(112,238)});
//    		templates[10] = new G2DGesture("v", new G2DPoint[]{new G2DPoint(89,164),new G2DPoint(90,162),new G2DPoint(92,162),new G2DPoint(94,164),new G2DPoint(95,166),new G2DPoint(96,169),new G2DPoint(97,171),new G2DPoint(99,175),new G2DPoint(101,178),new G2DPoint(103,182),new G2DPoint(106,189),new G2DPoint(108,194),new G2DPoint(111,199),new G2DPoint(114,204),new G2DPoint(117,209),new G2DPoint(119,214),new G2DPoint(122,218),new G2DPoint(124,222),new G2DPoint(126,225),new G2DPoint(128,228),new G2DPoint(130,229),new G2DPoint(133,233),new G2DPoint(134,236),new G2DPoint(136,239),new G2DPoint(138,240),new G2DPoint(139,242),new G2DPoint(140,244),new G2DPoint(142,242),new G2DPoint(142,240),new G2DPoint(142,237),new G2DPoint(143,235),new G2DPoint(143,233),new G2DPoint(145,229),new G2DPoint(146,226),new G2DPoint(148,217),new G2DPoint(149,208),new G2DPoint(149,205),new G2DPoint(151,196),new G2DPoint(151,193),new G2DPoint(153,182),new G2DPoint(155,172),new G2DPoint(157,165),new G2DPoint(159,160),new G2DPoint(162,155),new G2DPoint(164,150),new G2DPoint(165,148),new G2DPoint(166,146)});
//    		templates[11] = new G2DGesture("delete", new G2DPoint[]{new G2DPoint(123,129),new G2DPoint(123,131),new G2DPoint(124,133),new G2DPoint(125,136),new G2DPoint(127,140),new G2DPoint(129,142),new G2DPoint(133,148),new G2DPoint(137,154),new G2DPoint(143,158),new G2DPoint(145,161),new G2DPoint(148,164),new G2DPoint(153,170),new G2DPoint(158,176),new G2DPoint(160,178),new G2DPoint(164,183),new G2DPoint(168,188),new G2DPoint(171,191),new G2DPoint(175,196),new G2DPoint(178,200),new G2DPoint(180,202),new G2DPoint(181,205),new G2DPoint(184,208),new G2DPoint(186,210),new G2DPoint(187,213),new G2DPoint(188,215),new G2DPoint(186,212),new G2DPoint(183,211),new G2DPoint(177,208),new G2DPoint(169,206),new G2DPoint(162,205),new G2DPoint(154,207),new G2DPoint(145,209),new G2DPoint(137,210),new G2DPoint(129,214),new G2DPoint(122,217),new G2DPoint(118,218),new G2DPoint(111,221),new G2DPoint(109,222),new G2DPoint(110,219),new G2DPoint(112,217),new G2DPoint(118,209),new G2DPoint(120,207),new G2DPoint(128,196),new G2DPoint(135,187),new G2DPoint(138,183),new G2DPoint(148,167),new G2DPoint(157,153),new G2DPoint(163,145),new G2DPoint(165,142),new G2DPoint(172,133),new G2DPoint(177,127),new G2DPoint(179,127),new G2DPoint(180,125)});
//    		templates[12] = new G2DGesture("left curly brace", new G2DPoint[]{new G2DPoint(150,116),new G2DPoint(147,117),new G2DPoint(145,116),new G2DPoint(142,116),new G2DPoint(139,117),new G2DPoint(136,117),new G2DPoint(133,118),new G2DPoint(129,121),new G2DPoint(126,122),new G2DPoint(123,123),new G2DPoint(120,125),new G2DPoint(118,127),new G2DPoint(115,128),new G2DPoint(113,129),new G2DPoint(112,131),new G2DPoint(113,134),new G2DPoint(115,134),new G2DPoint(117,135),new G2DPoint(120,135),new G2DPoint(123,137),new G2DPoint(126,138),new G2DPoint(129,140),new G2DPoint(135,143),new G2DPoint(137,144),new G2DPoint(139,147),new G2DPoint(141,149),new G2DPoint(140,152),new G2DPoint(139,155),new G2DPoint(134,159),new G2DPoint(131,161),new G2DPoint(124,166),new G2DPoint(121,166),new G2DPoint(117,166),new G2DPoint(114,167),new G2DPoint(112,166),new G2DPoint(114,164),new G2DPoint(116,163),new G2DPoint(118,163),new G2DPoint(120,162),new G2DPoint(122,163),new G2DPoint(125,164),new G2DPoint(127,165),new G2DPoint(129,166),new G2DPoint(130,168),new G2DPoint(129,171),new G2DPoint(127,175),new G2DPoint(125,179),new G2DPoint(123,184),new G2DPoint(121,190),new G2DPoint(120,194),new G2DPoint(119,199),new G2DPoint(120,202),new G2DPoint(123,207),new G2DPoint(127,211),new G2DPoint(133,215),new G2DPoint(142,219),new G2DPoint(148,220),new G2DPoint(151,221)});
//    		templates[13] = new G2DGesture("right curly brace", new G2DPoint[]{new G2DPoint(117,132),new G2DPoint(115,132),new G2DPoint(115,129),new G2DPoint(117,129),new G2DPoint(119,128),new G2DPoint(122,127),new G2DPoint(125,127),new G2DPoint(127,127),new G2DPoint(130,127),new G2DPoint(133,129),new G2DPoint(136,129),new G2DPoint(138,130),new G2DPoint(140,131),new G2DPoint(143,134),new G2DPoint(144,136),new G2DPoint(145,139),new G2DPoint(145,142),new G2DPoint(145,145),new G2DPoint(145,147),new G2DPoint(145,149),new G2DPoint(144,152),new G2DPoint(142,157),new G2DPoint(141,160),new G2DPoint(139,163),new G2DPoint(137,166),new G2DPoint(135,167),new G2DPoint(133,169),new G2DPoint(131,172),new G2DPoint(128,173),new G2DPoint(126,176),new G2DPoint(125,178),new G2DPoint(125,180),new G2DPoint(125,182),new G2DPoint(126,184),new G2DPoint(128,187),new G2DPoint(130,187),new G2DPoint(132,188),new G2DPoint(135,189),new G2DPoint(140,189),new G2DPoint(145,189),new G2DPoint(150,187),new G2DPoint(155,186),new G2DPoint(157,185),new G2DPoint(159,184),new G2DPoint(156,185),new G2DPoint(154,185),new G2DPoint(149,185),new G2DPoint(145,187),new G2DPoint(141,188),new G2DPoint(136,191),new G2DPoint(134,191),new G2DPoint(131,192),new G2DPoint(129,193),new G2DPoint(129,195),new G2DPoint(129,197),new G2DPoint(131,200),new G2DPoint(133,202),new G2DPoint(136,206),new G2DPoint(139,211),new G2DPoint(142,215),new G2DPoint(145,220),new G2DPoint(147,225),new G2DPoint(148,231),new G2DPoint(147,239),new G2DPoint(144,244),new G2DPoint(139,248),new G2DPoint(134,250),new G2DPoint(126,253),new G2DPoint(119,253),new G2DPoint(115,253)});
//    		templates[14] = new G2DGesture("star", new G2DPoint[]{new G2DPoint(75,250),new G2DPoint(75,247),new G2DPoint(77,244),new G2DPoint(78,242),new G2DPoint(79,239),new G2DPoint(80,237),new G2DPoint(82,234),new G2DPoint(82,232),new G2DPoint(84,229),new G2DPoint(85,225),new G2DPoint(87,222),new G2DPoint(88,219),new G2DPoint(89,216),new G2DPoint(91,212),new G2DPoint(92,208),new G2DPoint(94,204),new G2DPoint(95,201),new G2DPoint(96,196),new G2DPoint(97,194),new G2DPoint(98,191),new G2DPoint(100,185),new G2DPoint(102,178),new G2DPoint(104,173),new G2DPoint(104,171),new G2DPoint(105,164),new G2DPoint(106,158),new G2DPoint(107,156),new G2DPoint(107,152),new G2DPoint(108,145),new G2DPoint(109,141),new G2DPoint(110,139),new G2DPoint(112,133),new G2DPoint(113,131),new G2DPoint(116,127),new G2DPoint(117,125),new G2DPoint(119,122),new G2DPoint(121,121),new G2DPoint(123,120),new G2DPoint(125,122),new G2DPoint(125,125),new G2DPoint(127,130),new G2DPoint(128,133),new G2DPoint(131,143),new G2DPoint(136,153),new G2DPoint(140,163),new G2DPoint(144,172),new G2DPoint(145,175),new G2DPoint(151,189),new G2DPoint(156,201),new G2DPoint(161,213),new G2DPoint(166,225),new G2DPoint(169,233),new G2DPoint(171,236),new G2DPoint(174,243),new G2DPoint(177,247),new G2DPoint(178,249),new G2DPoint(179,251),new G2DPoint(180,253),new G2DPoint(180,255),new G2DPoint(179,257),new G2DPoint(177,257),new G2DPoint(174,255),new G2DPoint(169,250),new G2DPoint(164,247),new G2DPoint(160,245),new G2DPoint(149,238),new G2DPoint(138,230),new G2DPoint(127,221),new G2DPoint(124,220),new G2DPoint(112,212),new G2DPoint(110,210),new G2DPoint(96,201),new G2DPoint(84,195),new G2DPoint(74,190),new G2DPoint(64,182),new G2DPoint(55,175),new G2DPoint(51,172),new G2DPoint(49,170),new G2DPoint(51,169),new G2DPoint(56,169),new G2DPoint(66,169),new G2DPoint(78,168),new G2DPoint(92,166),new G2DPoint(107,164),new G2DPoint(123,161),new G2DPoint(140,162),new G2DPoint(156,162),new G2DPoint(171,160),new G2DPoint(173,160),new G2DPoint(186,160),new G2DPoint(195,160),new G2DPoint(198,161),new G2DPoint(203,163),new G2DPoint(208,163),new G2DPoint(206,164),new G2DPoint(200,167),new G2DPoint(187,172),new G2DPoint(174,179),new G2DPoint(172,181),new G2DPoint(153,192),new G2DPoint(137,201),new G2DPoint(123,211),new G2DPoint(112,220),new G2DPoint(99,229),new G2DPoint(90,237),new G2DPoint(80,244),new G2DPoint(73,250),new G2DPoint(69,254),new G2DPoint(69,252)});
//    		templates[15] = new G2DGesture("pigtail", new G2DPoint[]{new G2DPoint(81,219),new G2DPoint(84,218),new G2DPoint(86,220),new G2DPoint(88,220),new G2DPoint(90,220),new G2DPoint(92,219),new G2DPoint(95,220),new G2DPoint(97,219),new G2DPoint(99,220),new G2DPoint(102,218),new G2DPoint(105,217),new G2DPoint(107,216),new G2DPoint(110,216),new G2DPoint(113,214),new G2DPoint(116,212),new G2DPoint(118,210),new G2DPoint(121,208),new G2DPoint(124,205),new G2DPoint(126,202),new G2DPoint(129,199),new G2DPoint(132,196),new G2DPoint(136,191),new G2DPoint(139,187),new G2DPoint(142,182),new G2DPoint(144,179),new G2DPoint(146,174),new G2DPoint(148,170),new G2DPoint(149,168),new G2DPoint(151,162),new G2DPoint(152,160),new G2DPoint(152,157),new G2DPoint(152,155),new G2DPoint(152,151),new G2DPoint(152,149),new G2DPoint(152,146),new G2DPoint(149,142),new G2DPoint(148,139),new G2DPoint(145,137),new G2DPoint(141,135),new G2DPoint(139,135),new G2DPoint(134,136),new G2DPoint(130,140),new G2DPoint(128,142),new G2DPoint(126,145),new G2DPoint(122,150),new G2DPoint(119,158),new G2DPoint(117,163),new G2DPoint(115,170),new G2DPoint(114,175),new G2DPoint(117,184),new G2DPoint(120,190),new G2DPoint(125,199),new G2DPoint(129,203),new G2DPoint(133,208),new G2DPoint(138,213),new G2DPoint(145,215),new G2DPoint(155,218),new G2DPoint(164,219),new G2DPoint(166,219),new G2DPoint(177,219),new G2DPoint(182,218),new G2DPoint(192,216),new G2DPoint(196,213),new G2DPoint(199,212),new G2DPoint(201,211)});
//    		
//    		for(int i=0;i<templates.length;i++){
//    			G2DGesture tmp = templates[i];
//    			_gestures.put(tmp.Name, tmp);
//    		}
//    		
//    		saveModel(filename);
			
			loadModel(filename);
    		
		}
		
		public void addGesture(String name, G2DPoint[] points){
			if(points.length==0) return;
			G2DGesture gest = new G2DGesture(name, points);
			_gestures.put(name, gest);
		}
		
		public void saveModel(String filename){
	    	FileOutputStream fos = null;
	    	ObjectOutputStream out = null;
	    	try{
		    	fos = new FileOutputStream(filename);
		    	out = new ObjectOutputStream(fos);
		    	out.writeObject(_gestures);
		    	out.close();
	    	}catch(IOException ex){
	    		ex.printStackTrace();
	    		logger.error("Could not save model to file: "+filename);
	    	}
	    }
	    
	    public void loadModel(String filename){
			try{
				FileInputStream fis = new FileInputStream(filename);
				ObjectInputStream in = new ObjectInputStream(fis);
				_gestures = (Hashtable<String, G2DGesture>)in.readObject();
			    in.close();
			}catch(IOException ex){
				logger.error("Could not load model from file: "+filename);
			}catch(ClassNotFoundException ex){
				logger.error("Could not find file: "+filename);
			}
	    }

        public G2DNBestList Recognize(G2DPoint[] points) // candidate points
        {
        	if(_gestures.size()==0 || points.length==0) return null;
        	
            // resample to a common number of points
            points = G2DUtils.Resample(points, NumResamplePoints);

            // rotate so that the centroid-to-1st-G2DPoint is at zero degrees
            double radians = G2DUtils.AngleInRadians(G2DUtils.Centroid(points), (G2DPoint) points[0], false); // indicative angle
            points = G2DUtils.RotateByRadians(points, -radians); // undo angle

            // scale to a common (square) dimension
            points = G2DUtils.ScaleTo(points, ResampleScale);

            // translate to a common origin
            points = G2DUtils.TranslateCentroidTo(points, ResampleOrigin);

            G2DNBestList nbest = new G2DNBestList();
           
            Enumeration<String> keys = _gestures.keys();
            while(keys.hasMoreElements())
            {
            	String key = keys.nextElement();
            	G2DGesture p = _gestures.get(key);
                double[] best = GoldenSectionSearch(
                    points,                 // to rotate
                    p.Points,               // to match
                    G2DUtils.Deg2Rad(-45.0),   // lbound
                    G2DUtils.Deg2Rad(+45.0),   // ubound
                    G2DUtils.Deg2Rad(2.0));    // threshold

                double score = 1d - best[0] / HalfDiagonal;
                nbest.AddResult(p.Name, score, best[0], best[1]); // name, score, distance, angle
            }
            nbest.SortDescending(); // sort so that nbest[0] is best result
            return nbest;
        }

        // From http://www.math.uic.edu/~jan/mcs471/Lec9/gss.pdf
        private double[] GoldenSectionSearch(G2DPoint[] pts1, G2DPoint[] pts2, double a, double b, double threshold)
        {
            double x1 = Phi * a + (1 - Phi) * b;
            G2DPoint[] newPoints = G2DUtils.RotateByRadians(pts1, x1);
            double fx1 = G2DUtils.PathDistance(newPoints, pts2);

            double x2 = (1 - Phi) * a + Phi * b;
            newPoints = G2DUtils.RotateByRadians(pts1, x2);
            double fx2 = G2DUtils.PathDistance(newPoints, pts2);

            double i = 2.0; // calls
            while (Math.abs(b - a) > threshold)
            {
                if (fx1 < fx2)
                {
                    b = x2;
                    x2 = x1;
                    fx2 = fx1;
                    x1 = Phi * a + (1 - Phi) * b;
                    newPoints = G2DUtils.RotateByRadians(pts1, x1);
                    fx1 = G2DUtils.PathDistance(newPoints, pts2);
                }
                else
                {
                    a = x1;
                    x1 = x2;
                    fx1 = fx2;
                    x2 = (1 - Phi) * a + Phi * b;
                    newPoints = G2DUtils.RotateByRadians(pts1, x2);
                    fx2 = G2DUtils.PathDistance(newPoints, pts2);
                }
                i++;
            }
            return new double[] { Math.min(fx1, fx2), G2DUtils.Rad2Deg((b + a) / 2.0), i }; // distance, angle, calls to pathdist
        }

        // continues to rotate 'pts1' by 'step' degrees as long as points become ever-closer 
        // in path-distance to pts2. the initial distance is given by D. the best distance
        // is returned in array[0], while the angle at which it was achieved is in array[1].
        // array[3] contains the number of calls to PathDistance.
        private double[] HillClimbSearch(G2DPoint[] pts1, G2DPoint[] pts2, double D, double step)
        {
            double i = 0.0;
            double theta = 0.0;
            double d = D;
            do
            {
                D = d; // the last angle tried was better still
                theta += step;
                G2DPoint[] newPoints = G2DUtils.RotateByDegrees(pts1, theta);
                d = G2DUtils.PathDistance(newPoints, pts2);
                i++;
            }
            while (d <= D);
            return new double[] { D, theta - step, i }; // distance, angle, calls to pathdist
        }

//        private double[] FullSearch(G2DPoint[] pts1, G2DPoint[] pts2, StreamWriter writer)
//        {
//            double bestA = 0d;
//            double bestD = G2DUtils.PathDistance(pts1, pts2);
//
//            for (int i = -180; i <= +180; i++)
//            {
//            	G2DPoint[] newPoints = G2DUtils.RotateByDegrees(pts1, i);
//                double d = G2DUtils.PathDistance(newPoints, pts2);
//                if (writer != null)
//                {
//                    writer.WriteLine("{0}\t{1:F3}", i, Math.round(d, 3));
//                }
//                if (d < bestD)
//                {
//                    bestD = d;
//                    bestA = i;
//                }
//            }
//            writer.WriteLine("\nFull Search (360 rotations)\n{0:F2}{1}\t{2:F3} px", Math.Round(bestA, 2), (char) 176, Math.Round(bestD, 3)); // calls, angle, distance
//            return new double[] { bestD, bestA, 360.0 }; // distance, angle, calls to pathdist
//        }

       public int getNumGestures()
		{
			
                return _gestures.size();
			
		}

        public ArrayList<G2DGesture> getGestures()
        {
            
                ArrayList<G2DGesture> list = new ArrayList<G2DGesture>(_gestures.values());
                Collections.sort(list);
                return list;
            
        }

		public void ClearGestures()
		{
            _gestures.clear();
		}

//		public bool SaveGesture(string filename, ArrayList points)
//		{
//			// add the new prototype with the name extracted from the filename.
//            string name = Gesture.ParseName(filename);
//            if (_gestures.ContainsKey(name))
//                _gestures.Remove(name);
//			Gesture newPrototype = new Gesture(name, points);
//            _gestures.Add(name, newPrototype);
//
//            // figure out the duration of the gesture
//            G2DPoint p0 = (G2DPoint) points[0];
//            G2DPoint pn = (G2DPoint) points[points.Count - 1];
//
//			// do the xml writing
//			bool success = true;
//			XmlTextWriter writer = null;
//			try
//			{
//				// save the prototype as an Xml file
//				writer = new XmlTextWriter(filename, Encoding.UTF8);
//				writer.Formatting = Formatting.Indented;
//				writer.WriteStartDocument(true);
//				writer.WriteStartElement("Gesture");
//				writer.WriteAttributeString("Name", name);
//				writer.WriteAttributeString("NumPts", XmlConvert.ToString(points.Count));
//                writer.WriteAttributeString("Millseconds", XmlConvert.ToString(pn.T - p0.T));
//                writer.WriteAttributeString("AppName", Assembly.GetExecutingAssembly().GetName().Name);
//				writer.WriteAttributeString("AppVer", Assembly.GetExecutingAssembly().GetName().Version.ToString());
//				writer.WriteAttributeString("Date", DateTime.Now.ToLongDateString());
//				writer.WriteAttributeString("TimeOfDay", DateTime.Now.ToLongTimeString());
//
//				// write out the raw individual points
//				foreach (G2DPoint p in points)
//				{
//					writer.WriteStartElement("G2DPoint");
//					writer.WriteAttributeString("X", XmlConvert.ToString(p.X));
//					writer.WriteAttributeString("Y", XmlConvert.ToString(p.Y));
//                    writer.WriteAttributeString("T", XmlConvert.ToString(p.T));
//					writer.WriteEndElement(); // <G2DPoint />
//				}
//
//				writer.WriteEndDocument(); // </Gesture>
//			}
//			catch (XmlException xex)
//			{
//				Console.Write(xex.Message);
//				success = false;
//			}
//            catch (Exception ex)
//            {
//                Console.Write(ex.Message);
//                success = false;
//            }
//			finally
//			{
//				if (writer != null)
//					writer.Close();
//			}
//			return success; // Xml file successfully written (or not)
//		}

//		public bool LoadGesture(string filename)
//		{
//			bool success = true;
//			XmlTextReader reader = null;
//            try
//            {
//                reader = new XmlTextReader(filename);
//                reader.WhitespaceHandling = WhitespaceHandling.None;
//                reader.MoveToContent();
//
//                Gesture p = ReadGesture(reader);
//
//                // remove any with the same name and add the prototype gesture
//                if (_gestures.ContainsKey(p.Name))
//                    _gestures.Remove(p.Name);
//                _gestures.Add(p.Name, p);
//            }
//            catch (XmlException xex)
//            {
//                Console.Write(xex.Message);
//                success = false;
//            }
//            catch (Exception ex)
//            {
//                Console.Write(ex.Message);
//                success = false;
//            }
//			finally
//			{
//				if (reader != null)
//					reader.Close();
//			}
//			return success;
//		}

//        // assumes the reader has been just moved to the head of the content.
//        private Gesture ReadGesture(XmlTextReader reader)
//        {
//            Debug.Assert(reader.LocalName == "Gesture");
//            string name = reader.GetAttribute("Name");
//            
//            ArrayList points = new ArrayList(XmlConvert.ToInt32(reader.GetAttribute("NumPts")));
//            
//            reader.Read(); // advance to the first G2DPoint
//            Debug.Assert(reader.LocalName == "G2DPoint");
//
//            while (reader.NodeType != XmlNodeType.EndElement)
//            {
//                G2DPoint p = G2DPoint.Empty;
//                p.X = XmlConvert.ToDouble(reader.GetAttribute("X"));
//                p.Y = XmlConvert.ToDouble(reader.GetAttribute("Y"));
//                p.T = XmlConvert.ToInt32(reader.GetAttribute("T"));
//                points.Add(p);
//                reader.ReadStartElement("G2DPoint");
//            }
//
//            return new Gesture(name, points);
//        }

        

//        /// <summary>
//        /// Assemble the gesture filenames into categories that contain 
//        /// potentially multiple examples of the same gesture.
//        /// </summary>
//        /// <param name="filenames"></param>
//        /// <returns>A 1D arraylist of category instances that each
//        /// contain the same number of examples, or <b>null</b> if an
//        /// error occurs.</returns>
//        /// <remarks>
//        /// See the comments above MainForm.BatchProcess_Click.
//        /// </remarks>
//        public ArrayList AssembleBatch(string[] filenames)
//        {
//            Hashtable categories = new Hashtable();
//
//            for (int i = 0; i < filenames.Length; i++)
//            {
//                string filename = filenames[i];
//
//                XmlTextReader reader = null;
//                try
//                {
//                    reader = new XmlTextReader(filename);
//                    reader.WhitespaceHandling = WhitespaceHandling.None;
//                    reader.MoveToContent();
//                    
//                    Gesture p = ReadGesture(reader);
//                    string catName = Category.ParseName(p.Name);
//                    if (categories.ContainsKey(catName))
//                    {
//                        Category cat = (Category) categories[catName];
//                        cat.AddExample(p); // if the category has been made before, just add to it
//                    }
//                    else // create new category
//                    {
//                        categories.Add(catName, new Category(catName, p));
//                    }
//                }
//                catch (XmlException xex)
//                {
//                    Console.Write(xex.Message);
//                    categories.Clear();
//                    categories = null;
//                }
//                catch (Exception ex)
//                {
//                    Console.Write(ex.Message);
//                    categories.Clear();
//                    categories = null;
//                }
//                finally
//                {
//                    if (reader != null)
//                        reader.Close();
//                }
//            }
//
//            // now make sure that each category has the same number of elements in it
//            ArrayList list = null;
//            if (categories != null)
//            {
//                list = new ArrayList(categories.Values);
//                int numExamples = ((Category) list[0]).NumExamples;
//                foreach (Category c in list)
//                {
//                    if (c.NumExamples != numExamples)
//                    {
//                        Console.WriteLine("Different number of examples in gesture categories.");
//                        list.Clear();
//                        list = null;
//                        break;
//                    }
//                }
//            }
//            return list;
//        }
//
//        /// <summary>
//        /// Tests an entire batch of files. See comments atop MainForm.TestBatch_Click().
//        /// </summary>
//        /// <param name="subject">Subject number.</param>
//        /// <param name="speed">"fast", "medium", or "slow"</param>
//        /// <param name="categories">A list of gesture categories that each contain lists of
//        /// prototypes (examples) within that gesture category.</param>
//        /// <param name="dir">The directory into which to write the output files.</param>
//        /// <returns>True if successful; false otherwise.</returns>
//        public bool TestBatch(int subject, string speed, ArrayList categories, string dir)
//        {
//            bool success = true;
//            StreamWriter mainWriter = null;
//            StreamWriter recWriter = null;
//            try
//            {
//                //
//                // set up a main results file and detailed recognition results file
//                //
//                int start = Environment.TickCount;
//                string mainFile = String.Format("{0}\\geometric_main_{1}.txt", dir, start);
//                string recFile = String.Format("{0}\\geometric_data_{1}.txt", dir, start);
//
//                mainWriter = new StreamWriter(mainFile, false, Encoding.UTF8);
//                mainWriter.WriteLine("Subject = {0}, Recognizer = geometric, Speed = {1}, StartTime(ms) = {2}", subject, speed, start);
//                mainWriter.WriteLine("Subject Recognizer Speed NumTraining GestureType RecognitionRate\n");
//
//                recWriter = new StreamWriter(recFile, false, Encoding.UTF8);
//                recWriter.WriteLine("Subject = {0}, Recognizer = geometric, Speed = {1}, StartTime(ms) = {2}", subject, speed, start);
//                recWriter.WriteLine("Correct? NumTrain Tested 1stCorrect Pts Ms Angle : (NBestNames) [NBestScores]\n");
//
//                //
//                // determine the number of gesture categories and the number of examples in each one
//                //
//                int numCategories = categories.Count;
//                int numExamples = ((Category) categories[0]).NumExamples;
//                double totalTests = (numExamples - 1) * NumRandomTests;
//
//                //
//                // outermost loop: trains on N=1..9, tests on 10-N (for e.g., numExamples = 10)
//                //
//                for (int n = 1; n <= numExamples - 1; n++)
//                {
//                    // storage for the final avg results for each category for this N
//                    double[] results = new double[numCategories];
//
//                    //
//                    // run a number of tests at this particular N number of training examples
//                    //
//                    for (int r = 0; r < NumRandomTests; r++)
//                    {
//                        _gestures.Clear(); // clear any (old) loaded prototypes
//
//                        // load (train on) N randomly selected gestures in each category
//                        for (int i = 0; i < numCategories; i++)
//                        {
//                            Category c = (Category) categories[i]; // the category to load N examples for
//                            int[] chosen = G2DUtils.Random(0, numExamples - 1, n); // select N unique indices
//                            for (int j = 0; j < chosen.Length; j++)
//                            {
//                                Gesture p = c[chosen[j]]; // get the prototype from this category at chosen[j]
//                                _gestures.Add(p.Name, p); // load the randomly selected test gestures into the recognizer
//                            }
//                        }
//
//                        //
//                        // testing loop on all unloaded gestures in each category. creates a recognition
//                        // rate (%) by averaging the binary outcomes (correct, incorrect) for each test.
//                        //
//                        for (int i = 0; i < numCategories; i++)
//                        {
//                            // pick a random unloaded gesture in this category for testing
//                            // instead of dumbly picking, first find out what indices aren't
//                            // loaded, and then randomly pick from those.
//                            Category c = (Category) categories[i];
//                            int[] notLoaded = new int[numExamples - n];
//                            for (int j = 0, k = 0; j < numExamples; j++)
//                            {
//                                Gesture g = c[j];
//                                if (!_gestures.ContainsKey(g.Name))
//                                    notLoaded[k++] = j; // jth gesture in c is not loaded
//                            }
//                            int chosen = G2DUtils.Random(0, notLoaded.Length - 1); // index
//                            Gesture p = c[notLoaded[chosen]]; // gesture to test
//                            Debug.Assert(!_gestures.ContainsKey(p.Name));
//                            
//                            // do the recognition!
//                            ArrayList testPts = G2DUtils.RotateByDegrees(p.RawPoints, G2DUtils.Random(0, 359));
//                            G2DNBestList result = this.Recognize(testPts);
//                            string category = Category.ParseName(result.Name);
//                            int correct = (c.Name == category) ? 1 : 0;
//
//                            recWriter.WriteLine("{0} {1} {2} {3} {4} {5} {6:F1}{7} : ({8}) [{9}]",
//                                correct,                            // Correct?
//                                n,                                  // NumTrain 
//                                p.Name,                             // Tested 
//                                FirstCorrect(p.Name, result.Names), // 1stCorrect
//                                p.RawPoints.Count,                  // Pts
//                                p.Duration,                         // Ms 
//                                Math.Round(result.Angle, 1), (char) 176, // Angle tweaking :
//                                result.NamesString,                 // (NBestNames)
//                                result.ScoresString);               // [NBestScores]
//
//                            results[i] += correct;
//                        }
//
//                        // provide feedback as to how many tests have been performed thus far.
//                        double testsSoFar = ((n - 1) * NumRandomTests) + r;
//                        ProgressChangedEvent(this, new ProgressEventArgs(testsSoFar / totalTests)); // callback
//                    }
//
//                    //
//                    // now create the final results for this N and write them to a file
//                    //
//                    for (int i = 0; i < numCategories; i++)
//                    {
//                        results[i] /= (double) NumRandomTests; // normalize by the number of tests at this N
//                        Category c = (Category) categories[i];
//                        // Subject Recognizer Speed NumTraining GestureType RecognitionRate
//                        mainWriter.WriteLine("{0} geometric {1} {2} {3} {4:F3}", subject, speed, n, c.Name, Math.Round(results[i], 3));
//                    }
//                }
//
//                // time-stamp the end of the processing
//                int end = Environment.TickCount;
//                mainWriter.WriteLine("\nEndTime(ms) = {0}, Minutes = {1:F2}", end, Math.Round((end - start) / 60000.0, 2));
//                recWriter.WriteLine("\nEndTime(ms) = {0}, Minutes = {1:F2}", end, Math.Round((end - start) / 60000.0, 2));
//            }
//            catch (Exception ex)
//            {
//                Console.WriteLine(ex.Message);
//                success = false;
//            }
//            finally
//            {
//                if (mainWriter != null)
//                    mainWriter.Close();
//                if (recWriter != null)
//                    recWriter.Close();
//            }
//            return success;
//        }
//
//        private int FirstCorrect(string name, string[] names)
//        {
//            string category = Category.ParseName(name);
//            for (int i = 0; i < names.Length; i++)
//            {
//                string c = Category.ParseName(names[i]);
//                if (category == c)
//                {
//                    return i + 1;
//                }
//            }
//            return -1;
//        }

//        #endregion
//
//        #region Rotation Graph
//
//        public bool CreateRotationGraph(string file1, string file2, string dir, bool similar)
//        {
//            bool success = true;
//            StreamWriter writer = null;
//            XmlTextReader reader = null;
//            try
//            {
//                // read gesture file #1
//                reader = new XmlTextReader(file1);
//                reader.WhitespaceHandling = WhitespaceHandling.None;
//                reader.MoveToContent();
//                Gesture g1 = ReadGesture(reader);
//                reader.Close();
//
//                // read gesture file #2
//                reader = new XmlTextReader(file2);
//                reader.WhitespaceHandling = WhitespaceHandling.None;
//                reader.MoveToContent();
//                Gesture g2 = ReadGesture(reader);
//
//                // create output file for results
//                string outfile = String.Format("{0}\\{1}({2}, {3})_{4}.txt", dir, similar ? "o" : "x", g1.Name, g2.Name, Environment.TickCount);
//                writer = new StreamWriter(outfile, false, Encoding.UTF8);
//                writer.WriteLine("Rotated: {0} --> {1}. {2}, {3}\n", g1.Name, g2.Name, DateTime.Now.ToLongDateString(), DateTime.Now.ToLongTimeString());
//
//                // do the full 360 degree rotations
//                double[] full = FullSearch(g1.Points, g2.Points, writer);
//
//                // use bidirectional hill climbing to do it again
//                double init = G2DUtils.PathDistance(g1.Points, g2.Points); // initial distance
//                double[] pos = HillClimbSearch(g1.Points, g2.Points, init, 1d);
//                double[] neg = HillClimbSearch(g1.Points, g2.Points, init, -1d);
//                double[] best = new double[3];
//                best = (neg[0] < pos[0]) ? neg : pos; // min distance
//                writer.WriteLine("\nHill Climb Search ({0} rotations)\n{1:F2}{2}\t{3:F3} px", pos[2] + neg[2] + 1, Math.Round(best[1], 2), (char) 176, Math.Round(best[0], 3)); // calls, angle, distance
//
//                // use golden section search to do it yet again
//                double[] gold = GoldenSectionSearch(
//                    g1.Points,              // to rotate
//                    g2.Points,              // to match
//                    G2DUtils.Deg2Rad(-45.0),   // lbound
//                    G2DUtils.Deg2Rad(+45.0),   // ubound
//                    G2DUtils.Deg2Rad(2.0));    // threshold
//                writer.WriteLine("\nGolden Section Search ({0} rotations)\n{1:F2}{2}\t{3:F3} px", gold[2], Math.Round(gold[1], 2), (char) 176, Math.Round(gold[0], 3)); // calls, angle, distance
//
//                // for pasting into Excel
//                writer.WriteLine("\n{0} {1} {2:F2} {3:F2} {4:F3} {5:F3} {6} {7:F2} {8:F2} {9:F3} {10} {11:F2} {12:F2} {13:F3} {14}",
//                    g1.Name,                    // rotated
//                    g2.Name,                    // into
//                    Math.Abs(Math.Round(full[1], 2)), // |angle|
//                    Math.Round(full[1], 2),     // Full Search angle
//                    Math.Round(full[0], 3),     // Full Search distance
//                    Math.Round(init, 3),        // Initial distance w/o any search
//                    full[2],                    // Full Search iterations
//                    Math.Abs(Math.Round(best[1], 2)), // |angle|
//                    Math.Round(best[1], 2),     // Bidirectional Hill Climb Search angle
//                    Math.Round(best[0], 3),     // Bidirectional Hill Climb Search distance
//                    pos[2] + neg[2] + 1,        // Bidirectional Hill Climb Search iterations
//                    Math.Abs(Math.Round(gold[1], 2)), // |angle|
//                    Math.Round(gold[1], 2),     // Golden Section Search angle
//                    Math.Round(gold[0], 3),     // Golden Section Search distance
//                    gold[2]);                   // Golden Section Search iterations
//            }
//            catch (XmlException xml)
//            {
//                Console.Write(xml.Message);
//                success = false;
//            }
//            catch (Exception ex)
//            {
//                Console.Write(ex.Message);
//                success = false;
//            }
//            finally
//            {
//                if (reader != null)
//                    reader.Close();
//                if (writer != null)
//                    writer.Close();
//            }
//            return success;
//        }
//
//        #endregion
//    }
}