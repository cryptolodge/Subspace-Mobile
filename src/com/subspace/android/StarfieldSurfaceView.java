package com.subspace.android;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class StarfieldSurfaceView extends GLSurfaceView {

	    public StarfieldSurfaceView(Context context){
	        super(context);

	     // Create an OpenGL ES 2.0 context
	        setEGLContextClientVersion(2);
	        // Set the Renderer for drawing on the GLSurfaceView
	        setRenderer(new StarfieldRenderer());
	        
	     // Render the view only when there is a change in the drawing data
	        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	    }
}
