package phobos.engine.views.game;
//: c01:Ingame.java

//lib general

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.opengl.renderer.Renderer;
import org.newdawn.slick.opengl.renderer.SGL;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import phobos.engine.Camera;
import phobos.engine.Engine;
import phobos.engine.Spritesheet;
import phobos.engine.States;
import phobos.engine.controllers.lights.LightController;
import phobos.engine.controllers.motions.MotionController;
import phobos.engine.models.entities.Entity;
import phobos.engine.models.entities.MobileEntity;
import phobos.engine.models.entities.Orientation;
import phobos.engine.models.lights.PulsatingLight;
import phobos.engine.models.motions.Rotation;
import phobos.engine.models.render.Vbo_Render;
import phobos.engine.models.world.Chunk;
import phobos.engine.models.world.Tile;
import phobos.generators.Generator;
import phobos.ui.SlickGUI;
import phobos.ui.SlickGuiTextField;
import phobos.ui.SlickGuiWindow;
import phobos.utils.math.Vector;

/**
 * Game State when we are playing the game, implements singleton pattern
 * @author half
 *
 */
public class Ingame extends BasicGameState {

	/**
	 * unique instance of the singleton
	 */
	static private Ingame instance = null ;

	protected static SGL GL = Renderer.get();
	public LightController lightController = new LightController();
	public MotionController motionController = new MotionController();
	public Queue<Entity> entityList = new ConcurrentLinkedQueue<Entity>();
	
	private int testNbrUp = 0 ;
	private boolean quit ;
	private SlickGUI gui ;
	private UnicodeFont font ;
	private int nbrTileShow = 0 ;
	private HashMap<Integer, Boolean> keyMap = new HashMap<Integer, Boolean>();
	private Color bgColor = new Color(0.0f,0.0f,0.0f,1.0f) ;
	private Color colorSelected = new Color(0.5f,0.5f,1.0f,1.0f) ;
	/*
	private Color colorAmbiance = new Color(0.0f,0.0f,0.0f,1.0f) ;
	private Color colorAmbianceDest = new Color(0.0f,0.0f,0.0f,1.0f) ;
	private Color colorAmbianceDay = new Color(1.0f,1.0f,0.8f,1.0f) ;
	private Color colorAmbianceNight = new Color(0.0f,0.0f,0.0f,1.0f) ;
	*/
	private Color colorAmbiance = new Color(0.2557f,0.29f,0.42f,1.0f) ;
	private Color colorAmbianceDest = new Color(0.2557f,0.29f,0.42f,1.0f) ;
	private Color colorAmbianceNight = new Color(0.2557f,0.29f,0.42f,1.0f) ;
	private Color colorAmbianceDay = new Color(1.0f,1.0f,0.8f,1.0f) ;
	
	private boolean colorChangement = false ;
	private int rateColorChange = 1 ;
	private int rateColorChangeCount = 0 ;
	
	private Chunk[][] Chunks ;
	private Camera camera ;

	private Tile tileActive ;

	private boolean active = false ;

	private Spritesheet spriteSheet ;

	private int vertexShader ;
	private int pixelShader ;
	private int shaderProgram ;
	
	private MobileEntity testBlueSphere;
	private MobileEntity testRedSphere;
	private MobileEntity testGreenSphere;
	private float speed = 12f;

	//VBO TEST
	Vbo_Render vbo = new Vbo_Render() ;
	
	public void init(GameContainer container,StateBasedGame engine) throws SlickException
	{		
		
		
		//ENTITIES
		Log.info("Making entities") ;
		testBlueSphere = new MobileEntity(106.0f, 60.0f, 50.0f,  Orientation.SOUTHWEST) ;
		testRedSphere = new MobileEntity(106.0f, 80.0f, 50.0f, Orientation.SOUTHWEST) ;
		testGreenSphere = new MobileEntity(106.0f, 90.0f, 50.0f, Orientation.SOUTHWEST) ;
		testBlueSphere.setParamDisplay(32,32,0,16) ;
		testRedSphere.setParamDisplay(32,32,0,16) ;
		testGreenSphere.setParamDisplay(32,32,0,16) ;
		try {
			testBlueSphere.setSprite(Orientation.SOUTHWEST, 0, 64) ;
			testRedSphere.setSprite(Orientation.SOUTHWEST, 32, 64) ;
			testGreenSphere.setSprite(Orientation.SOUTHWEST, 64, 64) ;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//LIGHTS
		Log.info("Making lights") ;
		testBlueSphere.addLight(new PulsatingLight(new Color(20,72,255),testBlueSphere,(byte)5,(byte) 20,1200));
		testRedSphere.addLight(new PulsatingLight(new Color(255,20,72),testRedSphere,(byte)5,(byte) 20,1000));
		testGreenSphere.addLight(new PulsatingLight(new Color(72,255,20),testGreenSphere,(byte)5,(byte) 20,800));
		
		//IMPULSES
		Log.info("Making impulses") ;
		testRedSphere.addImpulse(new Rotation(testBlueSphere,testRedSphere, -5000));
		testGreenSphere.addImpulse(new Rotation(testRedSphere,testGreenSphere, 2500));
		//testEntity.setSpeed(new Vector(0.1f,0,0));
		
		//GLuint shader
		/* creation */
		vertexShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		pixelShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(vertexShader,"void main(void) { gl_FrontColor = gl_Color; gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex * vec4(1.0,1.0,1.0,1.0); }") ;
		GL20.glCompileShader(vertexShader) ;
		GL20.glShaderSource(pixelShader,"void main(void) { gl_FragColor = gl_Color;}") ;
		GL20.glCompileShader(pixelShader) ;
		shaderProgram = GL20.glCreateProgram();
		GL20.glAttachShader(shaderProgram, vertexShader);
		GL20.glAttachShader(shaderProgram, pixelShader);
		GL20.glLinkProgram(shaderProgram);

		if(GL20.glIsShader(pixelShader)) Log.info("Pixel shader Log: "+GL20.glGetShaderInfoLog(pixelShader, 255) ) ;
		if(GL20.glIsShader(vertexShader)) Log.info("Vertex shader Log: "+GL20.glGetShaderInfoLog(vertexShader, 255) ) ;
		if(GL20.glIsProgram(shaderProgram)) Log.info("Program Log: "+GL20.glGetProgramInfoLog(shaderProgram, 255) ) ;

		//repeat array input
		Engine.getInstance().getContainer().getInput().enableKeyRepeat() ;

		//video tweak
		Engine.getInstance().getContainer().getGraphics().setDrawMode(Graphics.MODE_NORMAL) ;
		Engine.getInstance().getContainer().getGraphics().setAntiAlias(false) ;

		//Camera
		camera = new Camera(155,403,Engine.getInstance().getContainer().getWidth(),Engine.getInstance().getContainer().getHeight()) ;

		try {
			//spriteSheet = new Spritesheet("res/spriteSheet.png") ;
			//spriteSheet = new Spritesheet("res/test-png8192.png") ;
			//spriteSheet = new Spritesheet("res/test-png4096.png") ;
			spriteSheet = new Spritesheet("res/test-png_2048.png") ;
		} catch (SlickException e) {
			Log.error("[error] Error when make Spreetsheet") ;
		}
		
		
		//making Chunks
		Chunks = new Chunk[3][] ;
		for(int x=0;x<3;x++) {
			Chunks[x] = new Chunk[3] ;
			for(int y=0;y<3;y++) {
				Log.info("Initialise chunk "+x+" "+y) ;
				Chunks[x][y] = new Chunk(x,y) ;
			}
		}


		//calculing deltaZ for tiles (after settings chunk to get resources from neighbors)
		for(int x=0;x<3;x++) {
			for(int y=0;y<3;y++) {
				Log.info("Calcule DZ for Tiles "+x+" "+y) ;
				Chunks[x][y].calcDeltaZBorder(Chunks) ;
			}
		}
		
		
		getTile(testBlueSphere.getX(),testBlueSphere.getY()).addEntity(testBlueSphere);
		getTile(testRedSphere.getX(),testRedSphere.getY()).addEntity(testRedSphere);
		getTile(testGreenSphere.getX(),testGreenSphere.getY()).addEntity(testGreenSphere);
		this.entityList.add(testBlueSphere);
		this.entityList.add(testRedSphere);
		this.entityList.add(testGreenSphere);
		
		Engine.getInstance().getContainer().setMinimumLogicUpdateInterval(Engine.getMinUpdateMs()) ;
		Engine.getInstance().getContainer().setMaximumLogicUpdateInterval(Engine.getMaxUpdateMs()) ;
		this.quit = false ;
		this.gui = new SlickGUI() ;
		
		String fontPath = "res/FreeSans.ttf";
		font = new UnicodeFont(fontPath , 22, false, false); 
		font.addAsciiGlyphs();   
		//font.getEffects().add(new ShadowEffect(java.awt.Color.BLACK,4,4,0.9f)); 
		font.getEffects().add(new ColorEffect(java.awt.Color.WHITE)); 
		font.loadGlyphs();  

		/**Set GUI **/
		//make a window
		final SlickGuiWindow window = new SlickGuiWindow(new Image("res/windowBg.png"),100,100,200,500,"First Test Window") ;
		
		//Mouse area for the window
		window.addMouseArea("res/soleil.png",128,240,50,50,
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					colorAmbianceDest.r = colorAmbianceDay.r ;
					colorAmbianceDest.g = colorAmbianceDay.g ;
					colorAmbianceDest.b = colorAmbianceDay.b ;
					colorAmbianceDest.a = colorAmbianceDay.a ;
					colorChangement = true ;
					//quit = true ;
				}
			},"First Test Window Sun"
		) ;
		window.addMouseArea("res/lune.png",24,240,50,50,
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					colorAmbianceDest.r = colorAmbianceNight.r ;
					colorAmbianceDest.g = colorAmbianceNight.g ;
					colorAmbianceDest.b = colorAmbianceNight.b ;
					colorAmbianceDest.a = colorAmbianceNight.a ;
					colorChangement = true ;
					//quit = true ;
				}
			},"First Test Window Moon"
		) ;
		window.hideWindow() ;
		gui.addWindow(window) ;

		//make a window
		final SlickGuiWindow window2 = new SlickGuiWindow(new Image("res/perlinWindow.png"),350,150,400,324,"Perlin Noise Window") ;
		//Textfield for the window
		window2.addTextField(font, 147, 115, 100, 30, 
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					//SlickGuiTextField sourceTF = (SlickGuiTextField) source ;
					//Log.info("Saisie : " + sourceTF.getText());
					SlickGuiTextField tfZoom = (SlickGuiTextField) window2.getComponent(1) ;
					SlickGuiTextField tfHM = (SlickGuiTextField) window2.getComponent(2) ;
					SlickGuiTextField tfOctave = (SlickGuiTextField) window2.getComponent(3) ;
					SlickGuiTextField tfP = (SlickGuiTextField) window2.getComponent(4) ;

					Generator.setZoom(Integer.parseInt(tfZoom.getText())) ;	
					Generator.setMaxHeight(Integer.parseInt(tfHM.getText())) ;	
					Generator.setOctave(Integer.parseInt(tfOctave.getText())) ;	
					Generator.setP(Double.parseDouble(tfP.getText())) ;	
					
					for(int j=0;j<3;j++) {
						for(int i=0;i<3;i++) {
							Tile[][] tileSet = Chunks[j][i].getTileSet() ;
							for(int k=0;k<Chunk.getNbrTiles();k++) {
								for(int l=0;l<Chunk.getNbrTiles();l++) {
									tileSet[k][l].setDeltaZPerlinNoise() ;
								}
							}
						}
					}
					
					for(int j=0;j<3;j++) {
						for(int i=0;i<3;i++) {
							Tile[][] tileSet = Chunks[j][i].getTileSet() ;
							for(int k=0;k<Chunk.getNbrTiles();k++) {
								for(int l=0;l<Chunk.getNbrTiles();l++) {
									tileSet[k][l].calcCliffHeight() ;
								}
							}
						}
					}
				}
			},"Perlin noise btn Zoom"
		) ;
		SlickGuiTextField textF = (SlickGuiTextField) window2.getLastAdded() ;
		textF.setText(Generator.getZoom()+"") ;

		window2.addTextField(font, 231, 165, 100, 30, 
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					//SlickGuiTextField sourceTF = (SlickGuiTextField) source ;
					//Log.info("Saisie : " + sourceTF.getText());
					SlickGuiTextField tfZoom = (SlickGuiTextField) window2.getComponent(1) ;
					SlickGuiTextField tfHM = (SlickGuiTextField) window2.getComponent(2) ;
					SlickGuiTextField tfOctave = (SlickGuiTextField) window2.getComponent(3) ;
					SlickGuiTextField tfP = (SlickGuiTextField) window2.getComponent(4) ;

					Generator.setZoom(Integer.parseInt(tfZoom.getText())) ;	
					Generator.setMaxHeight(Integer.parseInt(tfHM.getText())) ;	
					Generator.setOctave(Integer.parseInt(tfOctave.getText())) ;	
					Generator.setP(Double.parseDouble(tfP.getText())) ;	
					
					for(int j=0;j<3;j++) {
						for(int i=0;i<3;i++) {
							Tile[][] tileSet = Chunks[j][i].getTileSet() ;
							for(int k=0;k<Chunk.getNbrTiles();k++) {
								for(int l=0;l<Chunk.getNbrTiles();l++) {
									tileSet[k][l].setDeltaZPerlinNoise() ;
								}
							}
						}
					}
					
					for(int j=0;j<3;j++) {
						for(int i=0;i<3;i++) {
							Tile[][] tileSet = Chunks[j][i].getTileSet() ;
							for(int k=0;k<Chunk.getNbrTiles();k++) {
								for(int l=0;l<Chunk.getNbrTiles();l++) {
									tileSet[k][l].calcCliffHeight() ;
								}
							}
						}
					}
				}
			},"Perlin noise btn max height"
		) ;
		textF = (SlickGuiTextField) window2.getLastAdded() ;
		textF.setText(Generator.getMaxHeight()+"") ;

		window2.addTextField(font, 167, 213, 100, 30, 
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					//SlickGuiTextField sourceTF = (SlickGuiTextField) source ;
					//Log.info("Saisie : " + sourceTF.getText());
					SlickGuiTextField tfZoom = (SlickGuiTextField) window2.getComponent(1) ;
					SlickGuiTextField tfHM = (SlickGuiTextField) window2.getComponent(2) ;
					SlickGuiTextField tfOctave = (SlickGuiTextField) window2.getComponent(3) ;
					SlickGuiTextField tfP = (SlickGuiTextField) window2.getComponent(4) ;

					Generator.setZoom(Integer.parseInt(tfZoom.getText())) ;	
					Generator.setMaxHeight(Integer.parseInt(tfHM.getText())) ;	
					Generator.setOctave(Integer.parseInt(tfOctave.getText())) ;	
					Generator.setP(Double.parseDouble(tfP.getText())) ;	
					
					for(int j=0;j<3;j++) {
						for(int i=0;i<3;i++) {
							Tile[][] tileSet = Chunks[j][i].getTileSet() ;
							for(int k=0;k<Chunk.getNbrTiles();k++) {
								for(int l=0;l<Chunk.getNbrTiles();l++) {
									tileSet[k][l].setDeltaZPerlinNoise() ;
								}
							}
						}
					}
					
					for(int j=0;j<3;j++) {
						for(int i=0;i<3;i++) {
							Tile[][] tileSet = Chunks[j][i].getTileSet() ;
							for(int k=0;k<Chunk.getNbrTiles();k++) {
								for(int l=0;l<Chunk.getNbrTiles();l++) {
									tileSet[k][l].calcCliffHeight() ;
								}
							}
						}
					}
				}
			},"Perlin noise btn octave"
		) ;
		textF = (SlickGuiTextField) window2.getLastAdded() ;
		textF.setText(Generator.getOctave()+"") ;

		window2.addTextField(font, 140, 256, 100, 30, 
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 
					//SlickGuiTextField sourceTF = (SlickGuiTextField) source ;
					//Log.info("Saisie : " + sourceTF.getText());
					SlickGuiTextField tfZoom = (SlickGuiTextField) window2.getComponent(1) ;
					SlickGuiTextField tfHM = (SlickGuiTextField) window2.getComponent(2) ;
					SlickGuiTextField tfOctave = (SlickGuiTextField) window2.getComponent(3) ;
					SlickGuiTextField tfP = (SlickGuiTextField) window2.getComponent(4) ;

					Generator.setZoom(Integer.parseInt(tfZoom.getText())) ;	
					Generator.setMaxHeight(Integer.parseInt(tfHM.getText())) ;	
					Generator.setOctave(Integer.parseInt(tfOctave.getText())) ;	
					Generator.setP(Double.parseDouble(tfP.getText())) ;	
					
					for(int j=0;j<3;j++) {
						for(int i=0;i<3;i++) {
							Tile[][] tileSet = Chunks[j][i].getTileSet() ;
							for(int k=0;k<Chunk.getNbrTiles();k++) {
								for(int l=0;l<Chunk.getNbrTiles();l++) {
									tileSet[k][l].setDeltaZPerlinNoise() ;
								}
							}
						}
					}
					
					for(int j=0;j<3;j++) {
						for(int i=0;i<3;i++) {
							Tile[][] tileSet = Chunks[j][i].getTileSet() ;
							for(int k=0;k<Chunk.getNbrTiles();k++) {
								for(int l=0;l<Chunk.getNbrTiles();l++) {
									tileSet[k][l].calcCliffHeight() ;
								}
							}
						}
					}
				}
			},"Perlin noise btn Pitch"
		) ;
		textF = (SlickGuiTextField) window2.getLastAdded() ;
		String s = ""+Generator.getP() ;
		textF.setText(s.substring(0,3)) ;
		
		//Mouse area for the window
		window2.addMouseArea("res/regenPN.png",338,276,40,40,
			new ComponentListener() {
				public void componentActivated(AbstractComponent source) { 

					SlickGuiTextField tfZoom = (SlickGuiTextField) window2.getComponent(1) ;
					SlickGuiTextField tfHM = (SlickGuiTextField) window2.getComponent(2) ;
					SlickGuiTextField tfOctave = (SlickGuiTextField) window2.getComponent(3) ;
					SlickGuiTextField tfP = (SlickGuiTextField) window2.getComponent(4) ;

					Generator.setZoom(Integer.parseInt(tfZoom.getText())) ;	
					Generator.setMaxHeight(Integer.parseInt(tfHM.getText())) ;	
					Generator.setOctave(Integer.parseInt(tfOctave.getText())) ;	
					Generator.setP(Double.parseDouble(tfP.getText())) ;	
					
					for(int j=0;j<3;j++) {
						for(int i=0;i<3;i++) {
							Tile[][] tileSet = Chunks[j][i].getTileSet() ;
							for(int k=0;k<Chunk.getNbrTiles();k++) {
								for(int l=0;l<Chunk.getNbrTiles();l++) {
									tileSet[k][l].setDeltaZPerlinNoise() ;
								}
							}
						}
					}
					
					for(int j=0;j<3;j++) {
						for(int i=0;i<3;i++) {
							Tile[][] tileSet = Chunks[j][i].getTileSet() ;
							for(int k=0;k<Chunk.getNbrTiles();k++) {
								for(int l=0;l<Chunk.getNbrTiles();l++) {
									tileSet[k][l].calcCliffHeight() ;
								}
							}
						}
					}
				}
			},"Perlin Noise Rregen Btn"
		) ;

		window2.hideWindow() ;
		gui.addWindow(window2) ;

		//Toggle button for first window
		gui.addToggleButton("res/showWindow.png",0,200,50,50,window,"Toggle First Test Window") ;

		//Toggle button for second second
		gui.addToggleButton("res/showWindow.png",0,250,50,50,window2,"Toggle Perlin Noise") ;

		//push windows in first front
		Engine.getInstance().getContainer().getInput().removeListener(window) ;
		Engine.getInstance().getContainer().getInput().addPrimaryListener(window) ;
		Engine.getInstance().getContainer().getInput().removeListener(window2) ;
		Engine.getInstance().getContainer().getInput().addPrimaryListener(window2) ;
		

		/** End GUI **/
		
		
		/** VBO TEST **/
		vbo.init() ;
		
	}
	
	static public Ingame getInstance() {
		if(Ingame.instance == null){
			Ingame.instance = new Ingame();
		}
		return Ingame.instance ;
	}
	
	/**
	 * Return the Tile at given coords
	 * @param x X of the tile
	 * @param y Y of the tile
	 * @return Tile located at X Y in Tile coords
	 */
	public Tile getTile(float x, float y) {
		return getChunks()[(int) (x/64)][(int) (y/64)].getTileSet()[(int) (x%64)][(int) (y%64)] ;
	}
	
	public Color getColorAmbiance() {
		return colorAmbiance ;
	}

	public void render(GameContainer container,StateBasedGame engine, Graphics g)
	{
		
		if(active) {


			
			//link main texture/spritesheet
			spriteSheet.bind() ;

			g.setBackground(bgColor) ;
			vbo.render(g) ;
			//GL20.glUseProgram(shaderProgram);

			//desactive mapping when rendering text 
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST) ;
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST) ;
			
			GL11.glBegin(SGL.GL_QUADS);
			/** TILE LOOP RENDER **/
			//show nbr Tiles
			nbrTileShow = 0 ;
			tileActive = null ;

			for(int j=0;j<3;j++) {
				for(int i=0;i<3;i++) {
					Tile[][] tileSet = Chunks[j][i].getTileSet() ;
					for(int k=0;k<Chunk.getNbrTiles();k++) {
						for(int l=0;l<Chunk.getNbrTiles();l++) {
							if(!tileSet[k][l].onScreen()) continue ;
							nbrTileShow += tileSet[k][l].render(g) ;

							//Position active boolean (under mouse)
							//unfocus old one
							tileSet[k][l].setFocus(false) ;
							if(tileSet[k][l].isMouseOver(Engine.getInstance().getContainer().getInput().getMouseX(),Engine.getInstance().getContainer().getInput().getMouseY())) {
								//remove old tile active{
								if(tileActive != null) {
									tileActive.setFocus(false) ;
									tileActive.setColorFilterToDefaultValues() ;
								}
								//put new tile active
								tileActive = tileSet[k][l] ;
								tileSet[k][l].setFocus(true) ;
								tileSet[k][l].addColorToSurfaceLightColor(colorSelected) ;
								for(int i1 = 0; i1 < (int) tileSet[k][l].getCliffHeight();i1++) {
									tileSet[k][l].addColorToCliffLightColor(colorSelected,i1) ;
								}
							}
						}
					}
				}
			}			
			GL11.glEnd();

			gui.render(g);

			g.drawString("Update : " + testNbrUp, 10, 30);
			g.drawString("Tile showed : " + nbrTileShow, 10, 50);
			g.drawString("Camera : X:" + camera.getX() + " Y:" + camera.getY() + " Resolution : " + camera.getWidth() + "x" + camera.getHeight() + " Zoom:" + camera.getCoeffZoom(), 10, 70);
			g.drawString("Entity : X:"+testBlueSphere.getX()+" Y:"+testBlueSphere.getY() + " Z:"+testBlueSphere.getZ() + " WASD / QE to modify", 10, 110);
			g.drawString("KEY_PRESSED :", 10, 130);
			g.drawString("Color : R:"+colorAmbiance.getRed()+ " B:"+colorAmbiance.getBlue()+" G:"+colorAmbiance.getGreen(), 10, 150);
		}
	}
	
	public void update(GameContainer container,StateBasedGame engine, int delta)
	{
		if(active) {
			
			this.motionController.update(delta);
			this.lightController.update(delta);
			
			if(colorChangement) {
				if(rateColorChangeCount == 0) {
					colorAmbiance.r = colorAmbiance.r + (colorAmbianceDest.r - colorAmbiance.r) /50;
					colorAmbiance.g = colorAmbiance.g + (colorAmbianceDest.g - colorAmbiance.g) /50;
					colorAmbiance.b = colorAmbiance.b + (colorAmbianceDest.b - colorAmbiance.b) /50;
					colorAmbiance.a = colorAmbiance.a + (colorAmbianceDest.a - colorAmbiance.a) /50;
					rateColorChangeCount = rateColorChange ;
				} else {
					rateColorChangeCount -- ;
				}
			}
			gui.update(delta);
			
			testNbrUp++;
			if(!gui.hasFocus()) {
				if(Engine.getInstance().getContainer().getInput().isKeyDown(Input.KEY_ESCAPE)) {
					engine.enterState(States.MAIN_MENU.getCode()) ;
				}
				if(Engine.getInstance().getContainer().getInput().isKeyPressed(Input.KEY_U)) {
					Chunks[1][1].getTileSet()[2][2].calcCliffHeight() ;
				}
			}
			if(quit) engine.enterState(States.MAIN_MENU.getCode()) ;
		}
	} 

	public Camera getCamera()
	{
		return camera ;
	} 

	public Spritesheet getSpritsheet() {
		return spriteSheet ;
	}

	public Chunk[][] getChunks() {
		return Chunks ;
	}

	public boolean getActive() {
		return active ;
	}

	/**
	 * Wake up GUI when we go back to the state
	 */
	@Override
	public void enter(GameContainer container,StateBasedGame engine)
	{
		gui.wakeup() ;
		quit = false ;
		active = true ;
	} 

	/**
	 * Make GUI sleep when we go out of the state
	 */
	@Override
	public void leave(GameContainer container,StateBasedGame engine)
	{
		try {
			super.leave(Engine.getInstance().getContainer(), engine);
			gui.sleep() ;
			active = false ;
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
	} 

	@Override
	public int getID()
	{
		return States.INGAME.getCode() ;
	} 

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		super.mouseClicked(button, x, y, clickCount);
		if(active) {
			try {
				Log.info(tileActive+"") ;
				tileActive.mouseClick(button) ;
			} catch (NullPointerException e) {
				Log.error("OUT OF TILESET !!! (x:"+x+" y:"+y+")") ;
			}
		}
	}

	@Override
	public void mouseDragged(int oldx,int oldy,int newx,int newy) {
		super.mouseDragged(oldx, oldy, newx, newy);
		if(active) {
			camera.setX(camera.getX() - (newx - oldx)) ;
			camera.setY(camera.getY() - (newy - oldy)) ;
		}
	}

	@Override
	public void mouseMoved(int oldx,int oldy,int newx,int newy) {
		super.mouseMoved(oldx, oldy, newx, newy);
	}

	@Override
	public void mouseWheelMoved(int newValue) {
		super.mouseWheelMoved(newValue);
		if(active) {
			
			float newval ;
			boolean change = true ;
			if(newValue > 0){
				newval = camera.getCoeffZoom() * 2.0f ;
				if(newval > 16.0f) {
					newval = 16.0f ;
					change = false ;
				}
			} else {
				newval = camera.getCoeffZoom() / 2.0f ;
				if(newval < 0.25f) {
						newval = 0.25f ;
						change = false ;
				}
			}
			camera.setCoeff((float) (Math.round(newval*100)/100.0d));
			camera.centerTo(testBlueSphere) ;
			if(change) {
				Log.info("CamX"+camera.getX()) ;
				Log.info("CamY"+camera.getY()) ;
				Log.info("BlueSphereX"+testBlueSphere.getInScreenCoordX()) ;
				Log.info("BlueSphereY"+testBlueSphere.getRelativeToCameraY()) ;
			}
		}
	}

	@Override
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);
		if(active && (keyMap.get(key) == null || !keyMap.get(key))){
			keyMap.put(key, true);
			switch(key){
			case Input.KEY_A : 
				if(!Engine.isAzerty()){
					testBlueSphere.addSpeed((new Vector(-speed, +speed, 0)));
				}else{
					testBlueSphere.addSpeed((new Vector(0, 0, +speed)));
				}
				
				break;
			case Input.KEY_Z : 
				if(Engine.isAzerty()){
					testBlueSphere.addSpeed((new Vector(-speed, -speed, 0)));
				}else {
					camera.centerTo(testBlueSphere) ;
				}
				break;
			case Input.KEY_E : 
				testBlueSphere.addSpeed(new Vector(0, 0, -speed));
				break;
			case Input.KEY_Q : 
				if(!Engine.isAzerty()){
					testBlueSphere.addSpeed(new Vector(0, 0, +speed));
				}else{
					testBlueSphere.addSpeed((new Vector(-speed, +speed, 0)));
				}
				break;
			case Input.KEY_S : 
				testBlueSphere.addSpeed(new Vector(speed, speed, 0));
				break;
			case Input.KEY_D : 
				testBlueSphere.addSpeed(new Vector(+speed, -speed, 0));
				break;
			case Input.KEY_W : 
				if(!Engine.isAzerty()){
					testBlueSphere.addSpeed(new Vector(-speed, -speed, 0));}
				else {
					camera.centerTo(testBlueSphere) ;
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		super.keyReleased(key, c);
		if(active && (keyMap.get(key) != null && keyMap.get(key))){
			keyMap.put(key, false);
			switch(key){
			case Input.KEY_A : 
				if(!Engine.isAzerty()){
					testBlueSphere.addSpeed((new Vector(+speed, -speed, 0)));
				}else{
					testBlueSphere.addSpeed((new Vector(0, 0, -speed)));
				}
				break;
			case Input.KEY_Z : 
				if(Engine.isAzerty()){
					testBlueSphere.addSpeed((new Vector(speed, speed, 0)));
				}
				break;
			case Input.KEY_E : 
				testBlueSphere.addSpeed((new Vector(0, 0, +speed)));
				break;
			case Input.KEY_Q : 
				if(!Engine.isAzerty()){
					testBlueSphere.addSpeed((new Vector(0, 0, -speed)));
				}else{
					testBlueSphere.addSpeed((new Vector(+speed, -speed, 0)));
				}
				break;
			case Input.KEY_S : 
				testBlueSphere.addSpeed((new Vector(-speed, -speed, 0)));
				break;
			case Input.KEY_D : 
				testBlueSphere.addSpeed((new Vector(-speed, +speed, 0)));
				break;
			case Input.KEY_W : 
				if(!Engine.isAzerty()){
					testBlueSphere.addSpeed((new Vector(speed, speed, 0)));
				}
				break;
			default:
				break;
			}
		}
	}

	public FloatBuffer floatBuffer(float a, float b, float c, float d) {
		float[] data = new float[]{a,b,c,d};
		FloatBuffer fb = BufferUtils.createFloatBuffer(data.length);
		fb.put(data);
		fb.flip();
		return fb;
	}
}