/**
 * 
 * Copyright (C) 2016 Thomas Diewald - http://thomasdiewald.com - MIT License
 * 
 * ___PixelFlow___
 * A Processing/Java library for high performance GPU-Computing (GLSL).
 * 
 */


package ParticleCollisionSystem;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class ParticleSystem {
  
  public float PARTICLE_SCREEN_FILL_FACTOR = 0.9f;
  public int   PARTICLE_COUNT              = 500;
  public int   PARTICLE_SHADING            = 255;
  public int   PARTICLE_SHAPE_IDX          = 1;

  PApplet papplet;
  Particle[] particles;
  PShape shp_particlesystem;
  
  int size_x;
  int size_y;
  
  
  public ParticleSystem(PApplet papplet){
    this.papplet = papplet;
    
    size_x = papplet.width;
    size_y = papplet.height;
  }

  

  public void initParticles(){
    particles = new Particle[PARTICLE_COUNT];
    for (int i = 0; i < PARTICLE_COUNT; i++) {
      particles[i] = new Particle(i);
    }
    initParticlesSize();
    initParticlesPosition();
    initParticleShapes();
  }
  

  public void initParticlesSize(){

    float radius = (float)Math.sqrt((size_x * size_y * PARTICLE_SCREEN_FILL_FACTOR) / PARTICLE_COUNT) * 0.5f;
   
    radius = Math.max(radius, 1);
    float rand_range = 0.5f;
    float r_min = radius * (1.0f - rand_range);
    float r_max = radius * (1.0f + rand_range);
    
    Particle.MAX_RAD = 0;
    papplet.randomSeed(0);
    for (int i = 0; i < PARTICLE_COUNT; i++) {
      float pr = papplet.random(r_min, r_max);
      particles[i].setRadius(pr);
    }
  }
  
 
  public void initParticlesPosition(){
    papplet.randomSeed(0);
    for (int i = 0; i < PARTICLE_COUNT; i++) {
      float px = papplet.random(1, size_x - 2);
      float py = papplet.random(1, size_y - 2);
      particles[i].setposition(px, py);
    }
  }
  
  
  public void initParticleShapes(){
    clearShapes();
    
    PImage sprite = createSprite();

    papplet.shapeMode(PConstants.CORNER);
    shp_particlesystem = papplet.createShape(PShape.GROUP);
    
    for (int i = 0; i < PARTICLE_COUNT; i++) {
      particles[i].initShape(papplet, sprite, PARTICLE_SHAPE_IDX);
      shp_particlesystem.addChild(particles[i].shp_particle);
    }

    shp_particlesystem.getTessellation(); // hack
  }
  
  
  
  
  
  PImage createSprite(){
    int size = (int)(Particle.MAX_RAD * 1.5f);
    size = Math.max(9, size);

    PImage pimg = papplet.createImage(size, size, PConstants.ARGB);
    pimg.loadPixels();
    
    float center_x = size/2f;
    float center_y = size/2f;
    
    for(int y = 0; y < size; y++){
      for(int x = 0; x < size; x++){
        int pid = y * size + x;
        
        float dx = center_x - (x+0.5f);
        float dy = center_y - (y+0.5f);
        float dd = (float)Math.sqrt(dx*dx + dy*dy) * 1f;
   
        dd = dd/(size*0.5f); // normalize
        
        if(PARTICLE_SHAPE_IDX == 0){
          if(dd<0) dd=0; else if(dd>1) dd=1;
          dd*=dd; dd*=dd;
          dd = 1-dd;
          int a = (int)(dd*255);
          pimg.pixels[pid] = a << 24 | 0x00FFFFFF;
        }
        else if(PARTICLE_SHAPE_IDX == 1){
          if(dd<0) dd=0; else if(dd>1) dd=1;
          dd = 1-dd;
          dd = dd*dd;
          int a = (int)(dd*255);
          pimg.pixels[pid] = a << 24 | 0x00FFFFFF;
        }
        else if(PARTICLE_SHAPE_IDX == 2){
          if(dd<0) dd=0; else if(dd>1) dd=1;
          dd = Math.abs(dd-0.5f);
          dd /= 0.5f;
          dd = 1-dd;
          dd = dd*dd*dd;
          int a = (int)(dd*255);
          pimg.pixels[pid] = a << 24 | 0x00FFFFFF;
        }
        else if(PARTICLE_SHAPE_IDX == 3){
          int a = 255;
          if(Math.abs(dx) < size/3f && Math.abs(dy) < size/3f) a = 0;
          pimg.pixels[pid] = a << 24 | 0x00FFFFFF;
        } else {
          pimg.pixels[pid] = 0;
        }
        
      }
    }
    pimg.updatePixels();
 
    return pimg;
  }
  
  
  
  
  

  // not sure if this is necessary, but i guess opengl stuff needs to be released internally.
  public void clearShapes(){
    if(shp_particlesystem != null){
      for(int i = shp_particlesystem.getChildCount()-1; i >= 0; i--){
        shp_particlesystem.removeChild(i);
      }
    }
  }
  

  
  void display(PGraphics pg) {
    pg.shape(shp_particlesystem);
  }
  
  

}
