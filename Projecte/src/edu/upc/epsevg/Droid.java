/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.TeamRobot;
/**
 *
 * @author marc
 */
public class Droid extends TeamRobot{
    private int  fase=0;
    private double ex;
    private double ey;
    
    @Override
    public void run(){
        while(true){
             if(estasCercaEsquina()){

                    back(400);
                }
            if (fase==0){
                try {
                //send where i am
                broadcastMessage("aliado|"+getX()+"|"+getY());
                
            } catch (IOException ex) {
                
                Logger.getLogger(Droid.class.getName()).log(Level.SEVERE, null, ex);
            }
            fase=1;
        }
            else if(fase==1){
                setTurnRight(10);
                setAhead(1000);
                
                execute();
                
            }
            else if(fase==2){
                double angleToTarget = Math.toDegrees(Math.atan2(ex - getX(), ey - getY()));
                double gunTurn = normalizarAngulo(angleToTarget - getGunHeading());
                turnGunRight(gunTurn);
                setTurnRight(10);
                setAhead(1000);
                fire(0.3);
                
                
                
                
                execute();
            }
            else if (fase==3){
                
                  double distanciaAlObjetivo = Math.sqrt(Math.pow(ex - getX(), 2) + Math.pow(ey - getY(), 2));

                // Si la distancia al objetivo es menor de 200, dispara con potencia máxima
                if (distanciaAlObjetivo < 200) {
                    setAhead(100);
                    setFire(2);  // Potencia máxima de disparo
                } else {
                    setAhead(100);
                    setFire(1);  // Disparo normal
                }

                double angleToTarget = Math.toDegrees(Math.atan2(ex - getX(), ey - getY()));
                double turn = normalizarAngulo(angleToTarget - getHeading());
                double gunTurn = normalizarAngulo(angleToTarget - getGunHeading());
                turnGunRight(gunTurn);
                setTurnRight(turn);
                
                
                execute();
            
            }
        }
        
        
        
        
    }
    
      @Override
    public void onMessageReceived(MessageEvent event) {
        if (fase>0){
            String mensajeCompleto = (String) event.getMessage();
            String[] partes = mensajeCompleto.split("\\|");
            if ("disparar".equals(partes[0])){
                
                ex=Double.parseDouble(partes[1]);
                ey=Double.parseDouble(partes[2]);
                fase=2;


            }
            else if("irydisparar".equals(partes[0])){
                ex=Double.parseDouble(partes[1]);
                ey=Double.parseDouble(partes[2]);
                fase=3;
            }
            
            
            
            
        }

        
        
        
    }
    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        String enemyName = event.getName();
        try {
            broadcastMessage("meDio|"+enemyName);
            
        } catch (IOException ex) {
            Logger.getLogger(Droid.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        }
    
      @Override
    public void onHitRobot(HitRobotEvent event) {
        if(event.getEnergy()==0){
            try {
                broadcastMessage("mate|"+event.getName());
                
            } catch (IOException ex) {
                Logger.getLogger(predator2.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    private double normalizarAngulo(double angle) {
        while (angle <= -180) {
            angle += 360;
        }
        while (angle > 180) {
            angle -= 360;
        }
        return angle;
}
     private boolean estasCercaEsquina() {
    
        double margen = 90;
        
        return getX() < margen || getX() > getBattleFieldWidth() - margen ||
               getY() < margen || getY() > getBattleFieldHeight() - margen;
        
    }
        
    
    
    
    
}



