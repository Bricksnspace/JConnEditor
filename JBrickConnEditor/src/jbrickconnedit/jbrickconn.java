/*
	Copyright 2014-2016 Mario Pascucci <mpascucci@gmail.com>
	This file is part of JBrickConnEditor

	JBrickConnEditor is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	JBrickConnEditor is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with JBrickConnEditor.  If not, see <http://www.gnu.org/licenses/>.

*/


package jbrickconnedit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.prefs.BackingStoreException;

import javax.imageio.ImageIO;
import javax.media.opengl.GLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import bricksnspace.ldeditor.EditChangeListener;
import bricksnspace.ldeditor.LDConnectionEditor;
import bricksnspace.ldraw3d.LDRenderedPart;
import bricksnspace.ldraw3d.LDrawGLDisplay;
import bricksnspace.ldraw3d.ProgressUpdater;
import bricksnspace.ldrawdb.LDrawDB;
import bricksnspace.ldrawdb.LDrawDBImportTask;
import bricksnspace.ldrawlib.ConnectionPoint;
import bricksnspace.ldrawlib.ConnectionTypes;
import bricksnspace.ldrawlib.ImportLDrawProjectTask;
import bricksnspace.ldrawlib.LDLibManageDlg;
import bricksnspace.ldrawlib.LDPrimitive;
import bricksnspace.ldrawlib.LDrawColor;
import bricksnspace.ldrawlib.LDrawLib;
import bricksnspace.ldrawlib.LDrawPart;
import bricksnspace.appsettings.AppSettings;
import bricksnspace.appsettings.AppUIResolution;
import bricksnspace.appsettings.OptionsDialog;





/**
 * LDraw parts/bricks connection editor
 *  
 * A support utility for JBrickBuilder, a simple LDraw CAD using OpenGL
 * 
 * @author Mario Pascucci
 *
 */
public class jbrickconn implements ActionListener, ProgressUpdater, EditChangeListener
{

	
	private static final String LDRURL = "http://www.ldraw.org/library/updates/complete.zip";

	private final String appName = "JBrickConnEditor - LDraw Brick Connections Editor";
	
	private final String autoconnFile = "autoconnect.csv";
	
	// LDraw Library
	private LDrawLib ldr;
	
	// 3D OpenGL display
	private LDrawGLDisplay gldisplay;

	// 3D model and rendering
	private LDConnectionEditor connEditor;

	// save and load
	private boolean firstSave = true;
	private boolean fromFile = false;
	private JFileChooser modelFile;
	private JFileChooser connectionLoad;
	private SmartFileChooser imgFile;
	private SmartFileChooser connectionSave;
	private SmartFileChooser xmlSave;
	

	// UI
	private JToggleButton perspective;
	private JFrame frame;
	private JToggleButton light;
	private JToggleButton polygon;
	private JToggleButton wires;
	private JButton rotleft;
	private JButton rotright;
	private JButton rotup;
	private JButton rotdown;
	private JButton zoomin;
	private JButton zoomreset;
	private JButton zoomout;
	private JButton panleft;
	private JButton panright;
	private JButton pandown;
	private JButton panup;
	private JButton panreset;
	private JButton saveShot;
	private JButton explodeSubModel;
	private JButton hideParts;
	private JButton unHideAll;
	private JLabel status;
	private JButton loadModel;
	private ImageIcon icnReady;
	private ImageIcon icnBusy;
	private ImageIcon icnError;
	private JButton gridUp;
	private JButton gridDown;
	private JButton gridLeft;
	private JButton gridRight;
	private JButton setSnap;
	private JButton setGrid;
	private JButton alignGrid;
	private JButton resetGrid;
	private JButton gridNear;
	private JButton gridFar;
	private JButton gridXZ;
	private JButton gridXY;
	private JButton gridYZ;
	private JButton searchPart;
	private JButton queryBrick;
	private JToggleButton enableGrid;
	private ImageIcon[] icnImg = new ImageIcon[4];
	private LDrawDB ldrdb;
	private JMenuItem mntmLdrParts;
	private JMenuItem mntmAbout;
	private JMenuItem mntmExit;
	private LDrawPartChooser partChooser;
	private final String imageFolder;
	private JMenuItem mntmOptions;
	private JButton newConn;
	private JButton loadConn;
	private JButton saveConn;
	private JButton addConn;
	private JButton delConn;
	private JPanel topToolbars;
	private JToolBar connectTools;
	private JComboBox<ConnectionTypes> connList;
	private JTextField infoName;
	private JLabel infoDescr;

	private JButton saveConnAs;

	private JLabel infoFile;

	private JButton checkConn;

	private JToggleButton enableAxis;

	private JMenuItem mntmLdrGet;

	private JToggleButton enableSnap;

	private JButton editUndo;

	private JButton editRedo;

	private JButton exportXml;

	private JCheckBox verifiedAuto;
	private boolean autoconnChanged = false;

	private JMenuItem mntmLibs;
	
	
	/*
	 * 
	 * TODO: aggiungere scelta di quali connessioni visualizzare
	 * 
	 * 		
	 */
	
	
	
	jbrickconn() {
		
		imageFolder = AppUIResolution.getImgDir();
		
		// images for busy animation
		icnImg[0] = new ImageIcon(this.getClass().getResource("images/f0.png"));
		icnImg[1] = new ImageIcon(this.getClass().getResource("images/f1.png"));
		icnImg[2] = new ImageIcon(this.getClass().getResource("images/f2.png"));
		icnImg[3] = new ImageIcon(this.getClass().getResource("images/f3.png"));
		// images for busy/ready icon
		icnReady = new ImageIcon(this.getClass().getResource(imageFolder+"ready.png"));
		icnBusy = new ImageIcon(this.getClass().getResource(imageFolder+"busy.png"));
		icnError = new ImageIcon(this.getClass().getResource(imageFolder+"error.png"));

		ldr = null;

		initPrefs();
		
		if (!AppSettings.isConfigured()) {
			// this is a first run
			firstRun();
		}
		// loading library
		try {
			ldr = loadLibs();
			// init LDraw part lib
			LDrawPart.setLdrlib(ldr);
			// read color definition
			LDrawColor.readFromLibrary(ldr);
		}
		catch (IOException exc) {
			JOptionPane.showMessageDialog(null, 
					"Unable to read your LDraw library.\n"
					+ "Please edit your library list\n "
					+ "and restart program.\n"
					+ "Original error was:\n"+
							exc.getLocalizedMessage(), 
					"Library Error", JOptionPane.ERROR_MESSAGE);
			ldr = null;
		}
		// open parts database
		try {
			ldrdb = new LDrawDB("jbb","jbbuser","");
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, 
					"Unable to create parts database.\n"
					+"You can't use program without it.\n"
					+ "Program now exits.",
					"Database error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
			System.exit(1);
		} catch (ClassNotFoundException e1) {
			JOptionPane.showMessageDialog(null, 
					"Unable to load database driver.\n"
					+"You can't use program without it.\n"
					+ "Program now exits.",
					"Database driver error", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
			System.exit(1);
		}
		if (ldrdb.getPartCount() < 1) {
			JOptionPane.showMessageDialog(null,
					"Your search database needs an update.\n"
					+ "Click OK to start...",
					"Database update", JOptionPane.INFORMATION_MESSAGE);
			doPartDBUpdate();
		}
		
		// now checks for connections library
		try {
			ConnectionPoint.initFromFile();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null,
					"Program needs connection rules.\n"
					+"Rules are in two XML files.\n"
					+ "Check that files are in current directory.",
					"Missing rule files", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		frame = new JFrame(appName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(900, 700));
		frame.setIconImage(new ImageIcon(this.getClass().getResource("images/icon-big.png")).getImage());
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				if (canProceedDiscard()) {
					try {
						AppSettings.savePreferences();
					} catch (IOException e) {
						// ignored
						e.printStackTrace();
					} catch (BackingStoreException e) {
						// ignored
						e.printStackTrace();
					}
					closeApp();
				}
			}
		});

		///// file setup
		// part load
		modelFile = new JFileChooser(".");
		FileNameExtensionFilter ff = new FileNameExtensionFilter("LDraw models", "dat", "ldr", "mpd", "l3b","lcd");
		modelFile.setFileFilter(ff);
		modelFile.setDialogTitle("Select LDraw part");
		// connection load dialog
		connectionLoad = new JFileChooser(".");
		connectionLoad.setDialogTitle("Load connections file");
		ff = new FileNameExtensionFilter("Connections", "cxml");
		connectionLoad.setFileFilter(ff);
		// connection save dialog
		connectionSave = new SmartFileChooser(".",".cxml");
		connectionSave.setSelectedFile(new File("."));
		connectionSave.setDialogTitle("Save connection file");
		connectionSave.setDialogType(JFileChooser.SAVE_DIALOG);
		// screenshot save dialog
		imgFile = new SmartFileChooser(".",".png");
		imgFile.setDialogTitle("Save screenshot");
		imgFile.setDialogType(JFileChooser.SAVE_DIALOG);
		// part XML export dialog
		xmlSave = new SmartFileChooser(".",".xml");
		xmlSave.setSelectedFile(new File("."));
		xmlSave.setDialogTitle("Save part as XML");
		xmlSave.setDialogType(JFileChooser.SAVE_DIALOG);

		// display setup
		LDrawGLDisplay.setAntialias(AppSettings.getBool(MySettings.ANTIALIAS));
		try {
			gldisplay = new LDrawGLDisplay();
		}
		catch (GLException e) {
			JOptionPane.showMessageDialog(null, 
					"There is a problem with your graphic card:\n"+
							e.getLocalizedMessage(), 
					"3D Init Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		//LDrawLib.forceLoRes();
		// load preferences
		frame.getContentPane().add(gldisplay.getCanvas(),BorderLayout.CENTER);
		
		// setting menu
		// main menu
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnUpdateCatalogs = new JMenu("Catalogs");
		menuBar.add(mnUpdateCatalogs);
		
		mntmLdrGet = new JMenuItem("Download LDraw library");
		mntmLdrGet.addActionListener(this);
		mnUpdateCatalogs.add(mntmLdrGet);
		
		mnUpdateCatalogs.addSeparator();

		mntmLdrParts = new JMenuItem("Update LDraw database");
		mntmLdrParts.addActionListener(this);
		mnUpdateCatalogs.add(mntmLdrParts);
		
		// last menu in top right 
		JMenu mnProgram = new JMenu("Program");
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(mnProgram);
		
		mntmOptions = new JMenuItem("Options...");
		mntmOptions.addActionListener(this);
		mnProgram.add(mntmOptions);

		mntmLibs = new JMenuItem("LDraw libraries...");
		mntmLibs.addActionListener(this);
		mnProgram.add(mntmLibs);
		
		mnProgram.add(new JSeparator());

		mntmAbout = new JMenuItem("About...");
		mntmAbout.addActionListener(this);
		mnProgram.add(mntmAbout);
		mnProgram.add(new JSeparator());

		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(this);
		mnProgram.add(mntmExit);
		

		topToolbars = new JPanel();
		topToolbars.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		frame.getContentPane().add(topToolbars,BorderLayout.NORTH);
		
		// left toolbar
		JPanel leftToolbars = new JPanel();
		//leftToolbars.setLayout(new GridLayout(0,2));
		
		JToolBar drawHelpers = new JToolBar("Tools",JToolBar.VERTICAL);
		drawHelpers.setLayout(new GridLayout(0, 2));

		gridUp = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-up.png")));
		gridUp.setToolTipText("Move grid -Y");
		gridUp.addActionListener(this);
		drawHelpers.add(gridUp);
		
		gridDown = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-down.png")));
		gridDown.setToolTipText("Move grid +Y");
		gridDown.addActionListener(this);
		drawHelpers.add(gridDown);
		
		gridLeft = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-left.png")));
		gridLeft.setToolTipText("Move grid -X");
		gridLeft.addActionListener(this);
		drawHelpers.add(gridLeft);

		gridRight = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-right.png")));
		gridRight.setToolTipText("Move grid +X");
		gridRight.addActionListener(this);
		drawHelpers.add(gridRight);
		
		gridNear = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-near.png")));
		gridNear.setToolTipText("Move grid -Z");
		gridNear.addActionListener(this);
		drawHelpers.add(gridNear);

		gridFar = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-far.png")));
		gridFar.setToolTipText("Move grid +Z");
		gridFar.addActionListener(this);
		drawHelpers.add(gridFar);
		
		gridXZ = new JButton("XZ");
		gridXZ.setToolTipText("Align grid to plane XZ");
		gridXZ.addActionListener(this);
		drawHelpers.add(gridXZ);

		gridXY = new JButton("XY");
		gridXY.setToolTipText("Align grid to plane XY");
		gridXY.addActionListener(this);
		drawHelpers.add(gridXY);

		gridYZ = new JButton("YZ");
		gridYZ.setToolTipText("Align grid to plane YZ");
		gridYZ.addActionListener(this);
		drawHelpers.add(gridYZ);

		alignGrid = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-align-part.png")));
		alignGrid.setToolTipText("Align grid to selected element");
		alignGrid.addActionListener(this);
		drawHelpers.add(alignGrid);
		
		resetGrid = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-reset.png")));
		resetGrid.setToolTipText("Reset grid to origin");
		resetGrid.addActionListener(this);
		drawHelpers.add(resetGrid);

		enableGrid = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid.png")));
		enableGrid.setToolTipText("Grid ON");
		enableGrid.setSelected(true);
		enableGrid.addActionListener(this);
		drawHelpers.add(enableGrid);
		
		enableSnap = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"snap.png")));
		enableSnap.setToolTipText("Snap ON");
		enableSnap.setSelected(true);
		enableSnap.addActionListener(this);
		drawHelpers.add(enableSnap);
		
		enableAxis = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"axis.png")));
		enableAxis.setToolTipText("Axis ON");
		enableAxis.setSelected(true);
		enableAxis.addActionListener(this);
		drawHelpers.add(enableAxis);
		
		setSnap = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"snap-size.png")));
		setSnap.setToolTipText("Set snap size");
		setSnap.addActionListener(this);
		drawHelpers.add(setSnap);
		
		setGrid = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"grid-size.png")));
		setGrid.setToolTipText("Set grid spacing");
		setGrid.addActionListener(this);
		drawHelpers.add(setGrid);

		hideParts = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"hide.png")));
		hideParts.setToolTipText("Hide selected");
		hideParts.addActionListener(this);
		drawHelpers.add(hideParts);
		
		unHideAll = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"show.png")));
		unHideAll.setToolTipText("Unhide all");
		unHideAll.addActionListener(this);
		drawHelpers.add(unHideAll);
		
		explodeSubModel = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"ungroup.png")));
		explodeSubModel.setToolTipText("Ungroup sub-model");
		explodeSubModel.addActionListener(this);
		drawHelpers.add(explodeSubModel);
		
		queryBrick = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-info.png")));
		queryBrick.setToolTipText("Part info");
		queryBrick.addActionListener(this);
		drawHelpers.add(queryBrick);
		
		saveShot = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"camera.png")));
		saveShot.setToolTipText("Take a screenshot");
		saveShot.addActionListener(this);
		drawHelpers.add(saveShot);
		
		leftToolbars.add(drawHelpers);
		
		frame.getContentPane().add(leftToolbars, BorderLayout.WEST);

		connectTools = new JToolBar("Connection tools", JToolBar.HORIZONTAL);

		loadModel = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"load-block.png")));
		loadModel.setToolTipText("Load LDraw part");
		loadModel.addActionListener(this);
		connectTools.add(loadModel);
		
		searchPart = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-mru.png")));
		searchPart.setToolTipText("Get part from library");
		searchPart.addActionListener(this);
		connectTools.add(searchPart);
		
		connectTools.addSeparator();
		
		newConn = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"model-new.png")));
		newConn.setToolTipText("New connection map");
		newConn.addActionListener(this);
		connectTools.add(newConn);
		
		loadConn = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"model-open.png")));
		loadConn.setToolTipText("Load connection map");
		loadConn.addActionListener(this);
		connectTools.add(loadConn);
		
		saveConn = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"model-save.png")));
		saveConn.setToolTipText("Save connection map");
		saveConn.addActionListener(this);
		saveConn.setEnabled(false);
		connectTools.add(saveConn);
		
		saveConnAs = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"model-save-as.png")));
		saveConnAs.setToolTipText("Save connection map with name");
		saveConnAs.addActionListener(this);
		connectTools.add(saveConnAs);
		
		addConn = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"conn-add.png")));
		addConn.setToolTipText("Add connection point");
		addConn.addActionListener(this);
		connectTools.add(addConn);
		
		connList = new JComboBox<ConnectionTypes>();
		for (int ct : ConnectionTypes.listById()) {
			connList.addItem(ConnectionTypes.getById(ct));
		}
		
		connectTools.add(connList);

		delConn = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"conn-del.png")));
		delConn.setToolTipText("Remove connection point");
		delConn.addActionListener(this);
		connectTools.add(delConn);
		
		checkConn = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"conn-check.png")));
		checkConn.setToolTipText("Check connection points");
		checkConn.addActionListener(this);
		connectTools.add(checkConn);

		connectTools.addSeparator();

		editUndo = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"undo.png")));
		editUndo.setToolTipText("Undo last edit");
		editUndo.addActionListener(this);
		editUndo.setEnabled(false);
		connectTools.add(editUndo);
		
		editRedo = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"redo.png")));
		editRedo.setToolTipText("Redo last edit");
		editRedo.addActionListener(this);
		editRedo.setEnabled(false);
		connectTools.add(editRedo);
		
		connectTools.addSeparator();
		
		exportXml = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"model-save-xml.png")));
		exportXml.setToolTipText("Export part as XML");
		exportXml.addActionListener(this);
		connectTools.add(exportXml);

		topToolbars.add(connectTools);
		
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLoweredBevelBorder());
		panel.setLayout(new GridBagLayout());
		//panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.ipady = 2;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		
		JToolBar rotPanel = new JToolBar("Rotate");
		rotPanel.setBorder(BorderFactory.createTitledBorder("Rotate model"));
		rotPanel.setLayout(new GridLayout(2, 3));

		gbc.gridx = 0;
		gbc.gridy = 0;

		panel.add(rotPanel,gbc);
		
		rotPanel.add(Box.createRigidArea(new Dimension(16, 16)));
		
		rotup = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"rotate-up.png")));
		rotup.addActionListener(this);
		rotPanel.add(rotup);

		rotPanel.add(Box.createRigidArea(new Dimension(16, 16)));
		
		rotleft = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"rotate-left.png")));
		rotleft.addActionListener(this);
		rotPanel.add(rotleft);
		
		rotdown = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"rotate-down.png")));
		rotdown.addActionListener(this);
		rotPanel.add(rotdown);

		rotright = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"rotate-right.png")));
		rotright.addActionListener(this);
		rotPanel.add(rotright);

		JToolBar panPanel = new JToolBar("Pan");
		panPanel.setBorder(BorderFactory.createTitledBorder("Zoom & Pan"));
		panPanel.setLayout(new GridLayout(2,5));

		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(panPanel,gbc);

		panPanel.add(Box.createRigidArea(new Dimension(16, 16)));

		panup = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"pan-up.png")));
		panup.addActionListener(this);
		panPanel.add(panup);

		panPanel.add(Box.createRigidArea(new Dimension(16, 16)));

		panreset = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"view-restore-2.png")));
		panreset.setToolTipText("Reset view");
		panreset.addActionListener(this);
		panPanel.add(panreset);
		
		zoomin = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"zoom-in.png")));
		zoomin.addActionListener(this);
		panPanel.add(zoomin);
		
		panleft = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"pan-left.png")));
		panleft.addActionListener(this);
		panPanel.add(panleft);
		
		pandown = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"pan-down.png")));
		pandown.addActionListener(this);
		panPanel.add(pandown);

		panright = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"pan-right.png")));
		panright.addActionListener(this);
		panPanel.add(panright);

		zoomreset = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"zoom-fit-best.png")));
		zoomreset.setToolTipText("Reset zoom");
		zoomreset.addActionListener(this);
		panPanel.add(zoomreset);
		
		zoomout = new JButton(new ImageIcon(this.getClass().getResource(imageFolder+"zoom-out.png")));
		zoomout.addActionListener(this);
		panPanel.add(zoomout);

		gbc.gridx = 2;
		gbc.gridy = 0;
		JToolBar optionPanel = new JToolBar("3D options");
		optionPanel.setBorder(BorderFactory.createTitledBorder("3D options"));
		optionPanel.setLayout(new GridLayout(2, 2));
		panel.add(optionPanel,gbc);

		perspective = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-filled.png")));
		perspective.setToolTipText("Orthogonal projection");
		perspective.setSelected(false);
		perspective.addActionListener(this);
		optionPanel.add(perspective);
		
		light = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"light-on.png")));
		light.setToolTipText("Use light");
		light. setSelected(true);
		light.addActionListener(this);
		optionPanel.add(light);
		
		polygon = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-filled.png")));
		polygon.setToolTipText("Polygon fill");
		polygon.setSelected(true);
		polygon.addActionListener(this);
		optionPanel.add(polygon);
		
		wires = new JToggleButton(new ImageIcon(this.getClass().getResource(imageFolder+"brick-wire.png")));
		wires.setToolTipText("Display edges");
		wires.setSelected(true);
		wires.addActionListener(this);
		optionPanel.add(wires);
		
		gbc.gridx = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createTitledBorder("Part info"));
		infoPanel.setLayout(new GridLayout(4, 1));
		
		infoName = new JTextField("-");
		infoPanel.add(infoName);
		infoName.setEditable(false);
		infoDescr = new JLabel("-");
		infoPanel.add(infoDescr);
		verifiedAuto = new JCheckBox("Autoconnect verified");
		verifiedAuto.setMargin(new Insets(0, 0, 0, 0));
		verifiedAuto.setSelected(false);
		verifiedAuto.addActionListener(this);
		infoPanel.add(verifiedAuto);
		infoFile = new JLabel("From CXML: -");
		infoPanel.add(infoFile);
		panel.add(infoPanel,gbc);
		
		gbc.gridx++;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2;
		panel.add(Box.createHorizontalGlue(),gbc);
		
		gbc.gridx++;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 2;
		panel.add(Box.createHorizontalGlue(),gbc);
		
		gbc.gridwidth = 4;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		status = new JLabel("Ready", icnReady, SwingConstants.LEFT);
		panel.add(status, gbc);
				
		partChooser = new LDrawPartChooser(frame, "Search for part",true,ldrdb);
		
		LDRenderedPart.enableAuxLines();
		LDRenderedPart.useBoundingSelect();
		LDrawPart model = LDrawPart.newEmptyPart();
		connEditor = LDConnectionEditor.newLDConnectionEditor(model,gldisplay);
		connEditor.registerChangeListener(this);
		float gridStep = AppSettings.getFloat(MySettings.GRIDSIZE);
		if (gridStep == 0) {
			gridStep = 20;
		}
		float snap = AppSettings.getFloat(MySettings.SNAPSIZE);
		if (snap == 0) {
			snap = 4;
		}
		connEditor.setGridSize(gridStep);
		connEditor.setSnap(snap);
		
		Thread t = connEditor.getRenderTask(this);
		t.start();

		frame.getContentPane().add(panel,BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
	}

	
	// App init

	
	/**
	 * init preferences
	 */
	private void initPrefs() {

		AppSettings.openPreferences(this);
		AppSettings.addPref(MySettings.ANTIALIAS, "Use Antialias (Req. restart)", AppSettings.BOOLEAN);
		AppSettings.defBool(MySettings.ANTIALIAS, true);
		AppSettings.addPref(MySettings.GRIDSIZE, "Grid sixe (LDU)", AppSettings.FLOAT);
		AppSettings.defFloat(MySettings.GRIDSIZE, 20.0f);
		AppSettings.addPref(MySettings.SNAPSIZE, "Snap size (LDU)", AppSettings.FLOAT);
		AppSettings.defFloat(MySettings.SNAPSIZE, 2.0f);
		// private settings
		AppSettings.addPrivatePref(MySettings.LDRAWZIP, "LDraw lib file name", AppSettings.STRING);
		AppSettings.defString(MySettings.LDRAWZIP, "complete.zip");
		AppSettings.addPrivatePref(MySettings.LIBLIST, "LDraw lib list", AppSettings.STRING);
	}

	
	/**
	 * do some fist run tasks: setup libraries or download it
	 */
	private void firstRun() {
		
		JOptionPane.showMessageDialog(null,
				"Thank you for using "+appName+"!\n" +
				"This is first run.\n"+
				"Program needs some other files to properly work:\n" +
				"- LDraw official library.\n" +
				"- Connection database\n"+
				"I'll ask you some question...",
				"Welcome!", JOptionPane.INFORMATION_MESSAGE, 
				new ImageIcon(this.getClass().getResource("images/icon-about.png")));
		int res = JOptionPane.showConfirmDialog(null, 
				"Do you already have LDraw library?\n"
				+ "You can use your LDraw library (folder or zip) and\n"
				+ "any other personal or unofficial library.\n"
				+ "Click 'No' if you don't have any LDraw library\n"
				+ "Program will download latest LDraw library.",
				"LDraw library choice", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
		if (res == JOptionPane.YES_OPTION) {
			JFileChooser jfc = new JFileChooser(".");
			FileFilter ff = new FileNameExtensionFilter("LDraw Library ZIP", "zip");
			jfc.setFileFilter(ff);
			jfc.setDialogTitle("Select Official LDraw library (ZIP or folder)");
			jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			while (true) {
				res = jfc.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					if (LDrawLib.officialSanityCheck(jfc.getSelectedFile())) {
						try {
							ldr = new LDrawLib(jfc.getSelectedFile().getPath());
							break;
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null,
									"Unable to use library "+jfc.getSelectedFile().getName()+"\n" +
									"Cause: "+e.getLocalizedMessage()+"\n"+
									"Try again.",
									"Library error", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
					else {
						JOptionPane.showMessageDialog(null,
								"File/folder "+jfc.getSelectedFile().getName()+"\n" +
								"isn't an official LDraw library.\n"+
								"Try again.",
								"Unknown folder/file", JOptionPane.ERROR_MESSAGE);
					}
				}
				else {
					res = JOptionPane.showConfirmDialog(null, 
							"Do you want to exit program?\n",
							"Confirm", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
					if (res == JOptionPane.YES_OPTION) {
						System.exit(0);
					}
				}
			}
		}
		else {
			// download library
			boolean found = false;
			for (int tries=0;tries<3;tries++) {
				doLDrawLibDownload();
				File l = new File(AppSettings.get(MySettings.LDRAWZIP));
				if (LDrawLib.officialSanityCheck(l)) {
					try {
						ldr = new LDrawLib(AppSettings.get(MySettings.LDRAWZIP));
						found = true;
						break;
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null,
								"Unable to use downloaded file\n" +
								"Cause: "+e.getLocalizedMessage()+"\n"+
								"Try again.",
								"Library error", JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
				}
				else {
					JOptionPane.showMessageDialog(null,
							"Downloaded file is corrupted.\n" +
							"Try to download again.",
							"Broken file", JOptionPane.ERROR_MESSAGE);
				}
			}
			if (!found) {
				// problems downloading library, exit
				JOptionPane.showMessageDialog(null,
						"Unable to download LDraw library.\n" +
						"Cannot continue, exiting...",
						"Giving up", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		}
		LDLibManageDlg dlg = new LDLibManageDlg(frame, "Edit/add other libraries to use", true, ldr);
		dlg.setVisible(true);
		if (ldr.getOfficialLibrary() < 0) {
			// no official library defined!
			JOptionPane.showMessageDialog(frame, "Official LDraw library not in list.\nYou may have weird/unstable results.", 
					"Check your libraries", JOptionPane.WARNING_MESSAGE);
		}
		saveLibs(ldr);
		// here we have all data to continue
		try {
			AppSettings.savePreferences();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, 
					"Cannot save preferences.\n"
					+ "Problem is:\n"
					+ e.getLocalizedMessage(),
					"Preferences error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (BackingStoreException e) {
			JOptionPane.showMessageDialog(null, 
					"Cannot save preferences.\n"
					+ "Problem is:\n"
					+ e.getLocalizedMessage(),
					"Preferences error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	
	
	
	// Actions
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource() == perspective) {
			if (perspective.isSelected()) {
				perspective.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"perspective.png")));
				perspective.setToolTipText("Perspective projection");
			}
			else {
				perspective.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"brick-filled.png")));
				perspective.setToolTipText("Orthogonal projection");
			}
			gldisplay.setPerspective(perspective.isSelected());
		}
		else if (e.getSource() == searchPart) {
			if (canProceedDiscard()) {
				partChooser.setLocationRelativeTo(frame);
				partChooser.setVisible(true);
				if (partChooser.getResponse() != JOptionPane.OK_OPTION) 
					return;
				if (partChooser.getSelected() == null)
					return;
				String part = partChooser.getSelected().getLdrawId();
				connEditor.closeEditor();
				connEditor = LDConnectionEditor.newLDConnectionEditor(
						LDrawPart.getPart(part),gldisplay);
				connEditor.registerChangeListener(this);
				Thread t = connEditor.getRenderTask(this);
				t.start();
				File cn = new File(ConnectionPoint.CONNFOLDER,part.substring(0, part.length()-4)+".cxml");
				if (cn.isFile() && cn.canRead()) {
					List<ConnectionPoint> lp;
					try {
						lp = ConnectionPoint.readFromFile(LDrawPart.getPart(part).getId(),
								new FileInputStream(cn));
						connEditor.loadConnections(lp);
						connectionLoad.setSelectedFile(cn);
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(frame, 
								"Unable to read connection data file:\n"+e1.getLocalizedMessage(),
								"File error",JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					} catch (XMLStreamException e1) {
						JOptionPane.showMessageDialog(frame, 
								"Error reading data file:\n"+e1.getLocalizedMessage(),
								"Format error",JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
					fromFile = true;
				}
				else {
					fromFile = false;
				}
				firstSave = true;
				setModelInfo();
			}
		}
		else if (e.getSource() == loadModel) {
			if (canProceedDiscard()) {
				int res = modelFile.showOpenDialog(frame);
				if (res != JFileChooser.APPROVE_OPTION)
					return;
				LDrawPart model = doLoadPart(modelFile.getSelectedFile());
				connEditor.closeEditor();
				connEditor = LDConnectionEditor.newLDConnectionEditor(model,gldisplay);
				connEditor.registerChangeListener(this);
				String part = model.getLdrawId();
				Thread t = connEditor.getRenderTask(this);
				t.start();
				File cn = new File(ConnectionPoint.CONNFOLDER,part.substring(0, part.length()-4)+".cxml");
				if (cn.isFile() && cn.canRead()) {
					List<ConnectionPoint> lp;
					try {
						lp = ConnectionPoint.readFromFile(LDrawPart.getPart(part).getId(),
								new FileInputStream(cn));
						connEditor.loadConnections(lp);
						connectionLoad.setSelectedFile(cn);
					} catch (FileNotFoundException e1) {
						JOptionPane.showMessageDialog(frame, 
								"Unable to read connection data file:\n"+e1.getLocalizedMessage(),
								"File error",JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					} catch (XMLStreamException e1) {
						JOptionPane.showMessageDialog(frame, 
								"Error reading data file:\n"+e1.getLocalizedMessage(),
								"Format error",JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
					fromFile = true;
				}
				else {
					fromFile = false;
				}
				firstSave = true;
				setModelInfo();
			}
		}
		else if (e.getSource() == loadConn) {
			if (canProceedDiscard()) {
				int res = connectionLoad.showOpenDialog(frame);
				if (res != JFileChooser.APPROVE_OPTION)
					return;
				try {
					// use a dummy id
					List<ConnectionPoint> lp = ConnectionPoint.readFromFile(42,
							new FileInputStream(connectionLoad.getSelectedFile()));
					connEditor.loadConnections(lp);
					fromFile = false;
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(frame, 
							"Unable to read connection data file:\n"+e1.getLocalizedMessage(),
							"File error",JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (XMLStreamException e1) {
					JOptionPane.showMessageDialog(frame, 
							"Error reading data file:\n"+e1.getLocalizedMessage(),
							"Format error",JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		}
		else if (e.getSource() == saveConn) {
			File s = null;
			if (fromFile) {
				s = connectionLoad.getSelectedFile();
			}
			else {
				String part = connEditor.getLdrawid().toLowerCase();
				if (part.length() == 0) {
					part = "empty.dat";
				}
				part = part.substring(0, part.length()-4)+".cxml";
				if (firstSave) {
					//System.out.println(connectionSave.getSelectedFile());
					if (connectionSave.getSelectedFile() != null 
							&& connectionSave.getSelectedFile().getParent() != null) {
						connectionSave.setSelectedFile(
								new File(connectionSave.getSelectedFile().getParent(),part));
					}
					else {
						connectionSave.setSelectedFile(
								new File(part));
					}
					int res = connectionSave.showSaveDialog(frame);
					if (res == JFileChooser.APPROVE_OPTION) {
						s = connectionSave.getSelectedFile();
						firstSave = false;
					}
					else {
						s = null;
					}
				}
				else {
					s = connectionSave.getSelectedFile();
				}
			}
			if (s != null) {
				try {
					dosaveXml(s);
				} catch (XMLStreamException e1) {
					JOptionPane.showMessageDialog(frame,
						    e1.getLocalizedMessage(),
						    "XML Error",
						    JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame,
						    e1.getLocalizedMessage(),
						    "I/O Error",
						    JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
				connEditor.markSave();
			}
		}
		else if (e.getSource() == saveConnAs) {
			File s = null;
			if (fromFile) {
				s = connectionLoad.getSelectedFile();
				connectionSave.setSelectedFile(s);
			}
			else {
				String part = connEditor.getLdrawid().toLowerCase();
				if (part.length() == 0) {
					part = "empty.dat";
				}
				part = part.substring(0, part.length()-4)+".cxml";
				if (connectionSave.getSelectedFile() != null 
						&& connectionSave.getSelectedFile().getParent() != null) {
					connectionSave.setSelectedFile(
							new File(connectionSave.getSelectedFile().getParent(),part));
				}
				else {
					connectionSave.setSelectedFile(
							new File(part));
				}
			}
			//System.out.println(connectionSave.getSelectedFile());
			int res = connectionSave.showSaveDialog(frame);
			if (res == JFileChooser.APPROVE_OPTION) {
				s = connectionSave.getSelectedFile();
				firstSave = false;
			}
			else {
				s = null;
			}
			if (s != null) {
				try {
					dosaveXml(s);
				} catch (XMLStreamException e1) {
					JOptionPane.showMessageDialog(frame,
						    e1.getLocalizedMessage(),
						    "XML Error",
						    JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(frame,
						    e1.getLocalizedMessage(),
						    "I/O Error",
						    JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
				connEditor.markSave();
			}
		}
		else if (e.getSource() == exportXml) {
			int res = xmlSave.showSaveDialog(frame);
			if (res == JFileChooser.APPROVE_OPTION) {
				 try {
					exportXml(xmlSave.getSelectedFile());
				} catch (XMLStreamException e1) {
					// nothing to do...
					e1.printStackTrace();
				} catch (IOException e1) {
					// nothing to do...
					e1.printStackTrace();
				}
			}

		}
		else if (e.getSource() == light) {
			if (light.isSelected()) {
				light.setToolTipText("Use light");
				light.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"light-on.png")));
			}
			else {
				light.setToolTipText("Flat lighting");
				light.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"light-off.png")));
			}
			gldisplay.setLighting(light.isSelected());
		}
		else if (e.getSource() == polygon) {
			if (polygon.isSelected()) {
				polygon.setToolTipText("Polygon filled");
				polygon.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"brick-filled.png")));
			}
			else {
				polygon.setToolTipText("Polygon hidden");
				polygon.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"brick-wire.png")));				
			}
			gldisplay.setPolygon(polygon.isSelected());
		}
		else if (e.getSource() == wires) {
			if (wires.isSelected()) {
				wires.setToolTipText("Display edges");
				wires.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"brick-wire.png")));
			}
			else {
				wires.setToolTipText("Edges hidden");
				wires.setIcon(new ImageIcon(this.getClass().getResource(imageFolder+"brick-wire-off.png")));
			}
			gldisplay.setWireframe(wires.isSelected());
		}
		else if (e.getSource() == panleft) {
			gldisplay.setOffsetx(10f);
		}
		else if (e.getSource() == panright) {
			gldisplay.setOffsetx(-10f);
		}
		else if (e.getSource() == panup) {
			gldisplay.setOffsety(10f);
		}
		else if (e.getSource() == alignGrid) {
			connEditor.alignGridToSelectedPart();
		}
		else if (e.getSource() == gridXY) {
			connEditor.alignXY();
		}
		else if (e.getSource() == gridYZ) {
			connEditor.alignYZ();
		}
		else if (e.getSource() == gridXZ) {
			connEditor.alignXZ();
		}
		else if (e.getSource() == gridDown) {
			connEditor.upY();
		}
		else if (e.getSource() == gridUp) {
			connEditor.downY();
		}
		else if (e.getSource() == gridRight) {
			connEditor.upX();
		}
		else if (e.getSource() == gridLeft) {
			connEditor.downX();
		}
		else if (e.getSource() == gridNear) {
			connEditor.downZ();
		}
		else if (e.getSource() == gridFar) {
			connEditor.upZ();
		}
		else if (e.getSource() == queryBrick) {
			//LDrawPart.listcache();
			connEditor.dumpConnections();
//			if (selectedParts.size() == 1) {
//				LDrawPart pt = LDrawPart.getPart(LDRenderedPart.getByGlobalId(selectedParts.iterator().next()).getPlacedPart().getLdrawId());
//				for (LDPrimitive p: pt.getPrimitives()) {
//					System.out.println(p);
//				}
//			}
//			else {
//				for (LDPrimitive p: currentModel.getPrimitives()) {
//					System.out.println(p);
//				}
//			}
		}
		else if (e.getSource() == verifiedAuto) {
			if (connEditor.getLdrawid() != null) {
				if (verifiedAuto.isSelected()) {
					ConnectionPoint.addAutodetected(ConnectionPoint.getPartMainName(connEditor.getLdrawid()));
				}
				else {
					ConnectionPoint.delAutodetected(ConnectionPoint.getPartMainName(connEditor.getLdrawid()));
				}
				autoconnChanged = true;
			}
		}
		else if (e.getSource() == setGrid) {
			String g = JOptionPane.showInputDialog(frame, "Set grid size (LDU)", 
					String.format(Locale.US,"%s", connEditor.getGridSize()));
			if (g == null)
				return;
			try {
				float n = Float.parseFloat(g);
				if (n < 0.1 || n > 1000) 
					return;
				connEditor.setGridSize(n);
				AppSettings.putFloat(MySettings.GRIDSIZE, connEditor.getGridSize());
			}
			catch (NumberFormatException ex) {
				return;
			}
		}
		else if (e.getSource() == resetGrid) {
			connEditor.resetGrid();
		}
		else if (e.getSource() == setSnap) {
			String g = JOptionPane.showInputDialog(frame, "Set snap size (LDU)", 
					String.format(Locale.US,"%s", connEditor.getSnap()));
			if (g == null)
				return;
			try {
				float n = Float.parseFloat(g);
				if (n < 0.1 || n > 100) 
					return;
				connEditor.setSnap(n);
				AppSettings.putFloat(MySettings.SNAPSIZE, connEditor.getSnap());
			}
			catch (NumberFormatException ex) {
				return;
			}
		}
		else if (e.getSource() == pandown) {
			gldisplay.setOffsety(-10f);
		}
		else if (e.getSource() == panreset) {
			connEditor.resetView();
		}
		else if (e.getSource() == zoomin) {
			gldisplay.setZoomFactor(0.7f);
		}
		else if (e.getSource() == zoomout) {
			gldisplay.setZoomFactor(1.3f);
		}
		else if (e.getSource() == zoomreset) {
			gldisplay.resetZoom();
		}
		else if (e.getSource() == rotup) {
			gldisplay.rotateX(5);
		}
		else if (e.getSource() == rotdown) {
			gldisplay.rotateX(-5);
		}
		else if (e.getSource() == rotleft) {
			gldisplay.rotateY(-5);
		}
		else if (e.getSource() == addConn) {
			connEditor.startAddConnection((ConnectionTypes) connList.getSelectedItem());
		}
		else if (e.getSource() == delConn) {
			connEditor.startDeleteConnection();
		}
		else if (e.getSource() == checkConn) {
			// do duplicate check
			int duplicates = connEditor.dupCheck();
			if (duplicates > 0) {
				int res = JOptionPane.showConfirmDialog(frame,
						"Found "+duplicates+" duplicated connections.\n"+
						"Do you want to delete duplicated connections?",
						"Delete duplicated", 
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); 
				if (res == JOptionPane.YES_OPTION) {
					connEditor.dupRemove();
					JOptionPane.showMessageDialog(frame, 
							"Removed "+duplicates+" duplicated connections",
							"Dup removed",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		else if (e.getSource() == rotright) {
			gldisplay.rotateY(5);
		}
		else if (e.getSource() == mntmAbout) {
			//LDPlacedPart.listCache();
			//currentModel.listParts();
			//LDRenderedPart.listCache();
			AboutDialog dlg = new AboutDialog(frame, appName, 
					new ImageIcon(this.getClass().getResource(imageFolder+"icon-big.png")));
			dlg.setVisible(true);
		}
		else if (e.getSource() == hideParts) {
			connEditor.hideSelected();
		}
		else if (e.getSource() == unHideAll) {
			connEditor.showAll();
		}
		else if (e.getSource() == saveShot) {
			int res = imgFile.showSaveDialog(frame);
			if (res != JFileChooser.APPROVE_OPTION)
				return;
			try {
				BufferedImage img = gldisplay.getScreenShot();
				ImageIO.write(img, "PNG",imgFile.getSelectedFile());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, 
						"Unable to write screenshot image:\n"+e1.getLocalizedMessage(),
						"Write error",JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == explodeSubModel) {
			connEditor.explodeSelected();
		}
		else if (e.getSource() == editUndo) {
			connEditor.undoLastEdit();
		}
		else if (e.getSource() == editRedo) {
			connEditor.redoLastEdit();
		}
		else if (e.getSource() == enableGrid) {
			if (enableGrid.isSelected()) {
				enableGrid.setToolTipText("Grid ON");
			}
			else {
				enableGrid.setToolTipText("Grid OFF");
			}
			connEditor.enableGrid(enableGrid.isSelected());
		}
		else if (e.getSource() == enableSnap) {
			if (enableSnap.isSelected()) {
				enableSnap.setToolTipText("Snap ON");
			}
			else {
				enableSnap.setToolTipText("Snap OFF");
			}
			LDConnectionEditor.setSnapping(enableSnap.isSelected());
		}
		else if (e.getSource() == enableAxis) {
			if (enableAxis.isSelected()) {
				enableAxis.setToolTipText("Axis ON");
			}
			else {
				enableAxis.setToolTipText("Axis OFF");
			}
			connEditor.enableAxis(enableAxis.isSelected());
		}
		else if (e.getSource() == saveShot) {
			int res = imgFile.showSaveDialog(frame);
			if (res != JFileChooser.APPROVE_OPTION)
				return;
			try {
				BufferedImage img = gldisplay.getScreenShot();
				ImageIO.write(img, "PNG",imgFile.getSelectedFile());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(frame, 
						"Unable to write screenshot image:\n"+e1.getLocalizedMessage(),
						"Write error",JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == mntmLdrGet) {
			doLDrawLibDownload();
			JOptionPane.showMessageDialog(frame, 
					"You must restart program to use new library\n",
					"New library",JOptionPane.INFORMATION_MESSAGE);
		}
		else if (e.getSource() == mntmLdrParts) {
			doPartDBUpdate();
		}
		else if (e.getSource() == mntmOptions) {
			OptionsDialog dlg = new OptionsDialog(frame, "Program options", true);
			dlg.setVisible(true);
			if (dlg.getResponse() == JOptionPane.OK_OPTION) {
				// save new preferences
				try {
					AppSettings.savePreferences();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, 
							"Cannot save preferences.\n"
							+ "Problem is:\n"
							+ e1.getLocalizedMessage(),
							"Preferences error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				} catch (BackingStoreException e1) {
					JOptionPane.showMessageDialog(null, 
							"Cannot save preferences.\n"
							+ "Problem is:\n"
							+ e1.getLocalizedMessage(),
							"Preferences error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		}
		else if (e.getSource() == mntmLibs) {
			LDLibManageDlg dlg = new LDLibManageDlg(frame, "Manage LDraw libraries", true, ldr);
			dlg.setVisible(true);
			saveLibs(ldr);
		}
		else if (e.getSource() == mntmExit) {
			if (canProceedDiscard()) {
				closeApp();
			}
		}
		saveConn.setEnabled(connEditor.isModified());
		editUndo.setEnabled(connEditor.isUndoAvailable());
		editRedo.setEnabled(connEditor.isRedoAvailable());
		gldisplay.getCanvas().requestFocusInWindow();
	}


	
	private void closeApp() {
		try {
			AppSettings.savePreferences();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		frame.dispose();
		System.exit(0);
	}
	
	
	

	
	
	
	/////////////////////////////
	//
	//   Part load and search
	//
	/////////////////////////////
	
	
	
	private LDrawPart doLoadPart(File selected) {
		LDrawPart.clearCustomParts();
		BusyDialog busyDialog = new BusyDialog(frame,"Reading project",true,true,icnImg);
		ImportLDrawProjectTask ldrproject = new ImportLDrawProjectTask(selected);
		busyDialog.setTask(ldrproject);
		Timer timer = new Timer(200, busyDialog);
		ldrproject.execute();
		timer.start();
		busyDialog.setVisible(true);
		// after completing task return here
		timer.stop();
		busyDialog.dispose();
		//System.out.println(ldrproject.getModel());
		try {
			ldrproject.get(10, TimeUnit.MILLISECONDS);
			if (ldrproject.isWarnings()) {
				JOptionPane.showMessageDialog(frame, "There are some errors/missing parts. See logs for details", 
						"Part load messages", JOptionPane.WARNING_MESSAGE);
			}
		}
		catch (ExecutionException ex) {
			ex.printStackTrace();
			System.out.println(ex.getLocalizedMessage());
		} catch (InterruptedException e1) {
			System.out.println(e1.getLocalizedMessage());
		} catch (TimeoutException e1) {
			System.out.println(e1.getLocalizedMessage());
		}
		LDrawPart part = ldrproject.getModel();
		return part;
	}
	

	
	
	private void exportXml(File f) throws XMLStreamException, IOException {
		
		XMLOutputFactory output = XMLOutputFactory.newInstance();

		XMLStreamWriter writer = output.createXMLStreamWriter(new FileOutputStream(f),"UTF-8");
		writer.writeStartDocument("utf-8", "1.0");
		writer.writeCharacters("\n");
		// a comment for exported list
		Calendar cal = Calendar.getInstance();
		writer.writeComment("Exported: "+f.getName()+
				" Date: "+DateFormat.getInstance().format(cal.getTime()));
		writer.writeCharacters("\n");
		writer.writeStartElement("date");
		writer.writeCharacters(String.format("%04d-%02d-%02d",cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH)));
		writer.writeEndElement();
		writer.writeCharacters("\n");
		LDrawPart p = LDrawPart.getPart(connEditor.getLdrawid());
		p.exportAsXml(writer);
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}
	
	
	
	
	
	private boolean canProceedDiscard() {

		if (autoconnChanged) {
			try {
				ConnectionPoint.saveAutoconnList(new File(autoconnFile));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, 
						"There is a problem saving autoconnect file:\n"+
								e.getLocalizedMessage(), 
						"File Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return false;
			}
		}
		if (connEditor.isModified()) {
			int res = JOptionPane.showConfirmDialog(frame, 
					"Model modified.\nDiscarding changes, are you sure?", 
					"Confirm unsaved", JOptionPane.YES_NO_OPTION);
			return (res == JOptionPane.YES_OPTION);			
		}
		
		return true;
	}
	
	

	private void doLDrawLibDownload() {
		
		BusyDialog busyDialog = new BusyDialog(null,"Download LDraw library",true,true,icnImg);
		busyDialog.setMsg("Downloading library...");
		GetFileFromURL task;
		try {
			task = new GetFileFromURL(new URL(LDRURL),new File(AppSettings.get(MySettings.LDRAWZIP)),busyDialog);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		busyDialog.setTask(task);
		Timer timer = new Timer(200, busyDialog);
		task.execute();
		timer.start();
		busyDialog.setVisible(true);
		// after completing task return here
		timer.stop();
		busyDialog.dispose();
		try {
			task.get(10, TimeUnit.MILLISECONDS);
			JOptionPane.showMessageDialog(null, "LDraw library successfully downloaded.", 
						"Download LDraw", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (ExecutionException ex) {
			ex.printStackTrace();
			System.out.println(ex.getLocalizedMessage());
		} catch (InterruptedException e1) {
			System.out.println(e1.getLocalizedMessage());
		} catch (TimeoutException e1) {
			System.out.println(e1.getLocalizedMessage());
		}

	}
	
	
	
	private void doPartDBUpdate() {
		
		BusyDialog busyDialog = new BusyDialog(frame,"Update part database",true,true,icnImg);
		busyDialog.setMsg("Reading part from library...");
		LDrawDBImportTask task = new LDrawDBImportTask(ldr, ldrdb);
		busyDialog.setTask(task);
		Timer timer = new Timer(200, busyDialog);
		task.execute();
		timer.start();
		busyDialog.setVisible(true);
		// after completing task return here
		timer.stop();
		busyDialog.dispose();
		try {
			int i = task.get(10, TimeUnit.MILLISECONDS);
			JOptionPane.showMessageDialog(frame, "Imported/updated "+i+" LDraw parts.", 
						"Ldraw part database", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (ExecutionException ex) {
			ex.printStackTrace();
			System.out.println(ex.getLocalizedMessage());
		} catch (InterruptedException e1) {
			System.out.println(e1.getLocalizedMessage());
		} catch (TimeoutException e1) {
			System.out.println(e1.getLocalizedMessage());
		}

	}


	
	
	
	
	
	private void setModelInfo() {
		
		infoName.setText(connEditor.getLdrawid());
		infoDescr.setText(connEditor.getDescription());
		if (fromFile) {
			infoFile.setText("<HTML>From CXML: <span style='color:green;'>YES</span></HTML>");
		}
		else {
			infoFile.setText("<HTML>From CXML: <span style='color:red;'>NO</span></HTML>");			
		}
		verifiedAuto.setSelected(ConnectionPoint.isAutoconnChecked(connEditor.getLdrawid()));
	}
	
	
	
	
	

	
	private void dosaveXml(File part) throws XMLStreamException, IOException {
		
		XMLOutputFactory output = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = output.createXMLStreamWriter(new FileWriter(part));
		writer.writeStartDocument("utf-8", "1.0");
		writer.writeCharacters("\n");
		// a comment for update serial and date
		writer.writeComment("Part: "+connEditor.getLdrawid()+"\n"+
				"Description: "+connEditor.getDescription()+"\n"+
				"Date: "+DateFormat.getInstance().format(Calendar.getInstance().getTime()));
		writer.writeCharacters("\n");
		// global start tag
		writer.writeStartElement("connections");
		writer.writeCharacters("\n");
		// connections
		for (ConnectionTypes ct: ConnectionTypes.listTypes()) {
			for (ConnectionPoint cp: connEditor.getConnectionsByType(ct.getId())) {
				cp.XMLWrite(writer);
			}
		}
		// close global tag
		writer.writeEndElement();
		writer.writeCharacters("\n");
		writer.writeEndDocument();
		writer.writeCharacters("\n");
		writer.flush();
		writer.close();
	}


	
	
	public void saveLibs(LDrawLib l) {
		
		String preflib = "";
		
		for (int i=0;i<l.count();i++) {
			if (i > 0) 
				preflib += "|";	// adds a separator
			preflib += l.getPath(i)+"|"+(l.isOfficial(i)?"t":"f")+"|"+(l.isEnabled(i)?"t":"f"); 
		}
		AppSettings.put(MySettings.LIBLIST, preflib);
	}


	
	
	public LDrawLib loadLibs() throws IOException {
		
		LDrawLib l = new LDrawLib();
		String preflib = AppSettings.get(MySettings.LIBLIST);
		String[] libs = preflib.split("\\|");
		int n = 0;
		while (n < libs.length) {
			// add library and set if official and enabled (string == "t" is for "true")
			l.addLDLib(libs[n],libs[n+1].equals("t"),libs[n+2].equals("t"));
			n += 3;
		}
		return l;
	}
	


	
	////////////////////
	//
	//   status icon
	//
	////////////////////
	
	
	@Override
	public void updateDone(int done, int total) {
		// ignored
		
	}



	@Override
	public void updateRemaining(int todo, int total) {
		// ignored
		
	}



	@Override
	public void updateDoing() {
		// ignored
		
	}



	@Override
	public void updateStart() {
		
		status.setIcon(icnBusy);
		status.setText("Busy");
	}



	@Override
	public void updateComplete() {

		status.setIcon(icnReady);
		status.setText("Ready");
	}



	@Override
	public void updateIncomplete() {
		
		status.setIcon(icnError);
		status.setText("Error");
	}





	
	/**
	 * Main
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
		            // Set cross-platform Java L&F (also called "Metal")
			        UIManager.setLookAndFeel(
			            UIManager.getCrossPlatformLookAndFeelClassName());
			        jbrickconn app = new jbrickconn();
					app.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}



	@Override
	public void undoAvailableNotification(boolean available) {
		
		editUndo.setEnabled(available);

	}



	@Override
	public void redoAvailableNotification(boolean available) {
		
		editRedo.setEnabled(available);

	}



	@Override
	public void modifiedNotification(boolean modified) {
		
		saveConn.setEnabled(modified);

	}



	@Override
	public void selectedPartChanged(LDPrimitive p) {
		
		if (p != null) {
			status.setText(
				"LDraw: " + p.getLdrawId() + 
						" - " + p.getDescription() +
						" Color: " +  Integer.toString(p.getColorIndex()) +"\n" +
						" - " + LDrawColor.getById(p.getColorIndex()).getName() +
						" ID: " + Integer.toString(p.getId()) + 
						" - " + (p.getType()) 
				);
		}
		else {
			status.setText("-");
		}		
	}


	@Override
	public void selectedConnChanged(ConnectionPoint cp) {
		
		if (cp != null) {
			status.setText(
					cp.getType() + 
							" - " + cp.getP1() +
							" - " + cp.getP2() +
							" ID: " + Integer.toString(cp.getId()) + 
							" - " + cp.getPartId() 
					);
		}
		else {
			status.setText("-");
		}
	}



	@Override
	public void cutCopyAvailable(boolean available) {
		
	}



	@Override
	public void pasteAvailable(boolean available) {
		
	}




	
}
