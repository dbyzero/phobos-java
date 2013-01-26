package phobos.engine.models.world;

/**
 * 
 * Chunks are a square part of the heightmap. Clients have only a few chunks loaded by session and they are loaded by streaming
 * @author half
 *
 */
public class Chunk {
	static private int nbr_tiles = 64 ;
	private Tile[][] tileSet ;
	private int X,Y ;

	/**
	 * 
	 * @return number of Tile displayed by the Chunk
	 */
	static public int getNbrTiles() {
		return nbr_tiles ;
	}

	public Chunk(int x,int y) {
		//init tiles ;
		this.X = x ;
		this.Y = y ;
		tileSet = new Tile[nbr_tiles][] ;
		for(int i=0;i<nbr_tiles;i++) {
			tileSet[i] = new Tile[nbr_tiles] ;
			for(int j=0;j<nbr_tiles;j++) {
				tileSet[i][j] = new Tile((byte) 0,i + X * getNbrTiles(),j + Y * getNbrTiles(),0) ;
				tileSet[i][j].setSpriteSurface(96, 32, 32, 32) ;
				tileSet[i][j].setSpriteCliff(0, 32, 32, 32) ;
			}
		}
	}

	public Tile[][] getTileSet() {
		return tileSet ;
	}

	/**
	 * Calculate how high are top of each Tile
	 * @param chunks list of chunks
	 */
	public void calcDeltaZBorder(Chunk[][] chunks) {
		for(int j=0;j<nbr_tiles;j++) {
			for(int i=0;i<nbr_tiles;i++) {
				tileSet[i][j].calcCliffHeight();
			}
		}
	}
}
