/*
 * Copyright (C) 2011
 * Tim Gustafson
 * tjg@tgustafson.com
 * http://tgustafson.com/
 * All Rights Reserved
 *
 * Licensed for non-commercial use by an individual for educational purposes only.
 */

package phobos.engine.models.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Date;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.Log;

public class Vbo_Render {

	private float[] vertex_data_array = {
			//   x      y      z     r    g      b      a
			// quad 1
			0.0f,  0.0f,  0.0f,   1.0f,  0.0f,  0.0f,  1.0f,
			0.0f,  1.0f,  0.0f,   1.0f,  1.0f,  1.0f,  1.0f,
			1.0f, 1.0f,  0.0f,   1.0f,  0.0f,  0.0f,  1.0f,
			1.0f, 0.0f,  0.0f,    1.0f,  0.0f,  0.0f,  1.0f,
//			
//			// quad 2
//			0.0f,  0.0f,  0.0f, 0.0f,  1.0f,  0.0f,  1.0f,
//			0.0f,  -1.0f,  0.0f, 0.0f,  1.0f,  1.0f,  1.0f,
//			1.0f, -1.0f,  0.0f, 1.0f,  0.0f,  1.0f,  1.0f,
//			1.0f, 0.0f,  0.0f, 1.0f,  1.0f,  0.0f,  1.0f,
//			
//			// quad 3
//			0.0f,  0.0f,  0.0f, 1.0f,  1.0f,  0.0f,  1.0f,
//			0.0f,  -1.0f,  0.0f, 1.0f,  1.0f,  1.0f,  1.0f,
//			-1.0f, -1.0f,  0.0f, 0.0f,  0.0f,  0.0f,  1.0f,
//			-1.0f, 0.0f,  0.0f, 0.0f,  1.0f,  0.0f,  1.0f,
//			
//			// quad 4
//			0.0f,  0.0f,  0.0f, 1.0f,  1.0f,  0.0f,  1.0f,
//			0.0f,  1.0f,  0.0f, 0.0f,  0.0f,  1.0f,  1.0f,
//			-1.0f, 1.0f,  0.0f, 1.0f,  1.0f,  0.0f,  1.0f,
//			-1.0f, 0.0f,  0.0f, 0.0f,  1.0f,  0.0f,  1.0f,

	};

	private FloatBuffer vertex_buffer_data ; 

	public Vbo_Render() {
		// create our vertex buffer objects
	}

	public void init() {

		// set up OpenGL
		//GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
		GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 25.0f);

		
		// set up the camera
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		GL11.glOrtho(0,800,600,0,-1,1);

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		// create our vertex buffer objects
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL15.glGenBuffers(buffer);

		int vertex_buffer_id = buffer.get(0);
		FloatBuffer vertex_buffer_data = BufferUtils.createFloatBuffer(vertex_data_array.length);
		vertex_buffer_data.put(vertex_data_array);
		vertex_buffer_data.rewind();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertex_buffer_id);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertex_buffer_data, GL15.GL_STATIC_DRAW);

	}

	public void set() {
		Log.info("set");
	}

	public int render(Graphics g) {

	//	g.setDrawMode(Graphics.MODE_ALPHA_BLEND) ;
		  
//		// perform rotation transformations
		GL11.glPushMatrix();
//		
//		// render the cube
	    GL11.glVertexPointer(3, GL11.GL_FLOAT,  vertex_data_array.length, 0);
		GL11.glColorPointer(4, GL11.GL_FLOAT,  vertex_data_array.length, 12);
        
		GL11.glDrawArrays(GL11.GL_QUADS, 0, vertex_data_array.length / 7);
//		
//		// restore the matrix to pre-transformation values
		GL11.glPopMatrix();

		return vertex_data_array.length / 7 ;
	}

	public FloatBuffer floatBuffer(float a, float b, float c, float d) {
		float[] data = new float[]{a,b,c,d};
		FloatBuffer fb = BufferUtils.createFloatBuffer(data.length);
		fb.put(data);
		fb.flip();
		return fb;
	}
}
