/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.HitByBulletEvent;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;


/**
 *
 * @author marc
 */
public class lider  extends TeamRobot {
    private int fase=0;
    private  Map<String,double[]>allies=new HashMap<>();
    private  Map<String,double[]> enemies=new HashMap<>();
    private HashSet<String> noLideres = new HashSet<>();  
    private String liderEnemigo=null;
    

    
    @Override
    public void run(){
        double anchoCampo = getBattleFieldWidth();
        double altoCampo = getBattleFieldHeight();
        
        setColors(Color.green,Color.green,Color.green);
         setAdjustGunForRobotTurn(true);
         setAdjustRadarForGunTurn(true);
        while(true){
            double esquinaOpuestaX = anchoCampo - getX();
            double esquinaOpuestaY = altoCampo - getY();
           double anguloHaciaEsquinaOpuesta = Math.toDegrees(Math.atan2(esquinaOpuestaX - getX(), esquinaOpuestaY - getY()));
            System.out.println("fase"+fase);
             if(estasCercaEsquina()){

                    back(400);
                }
           
            if ((enemies.size())==(noLideres.size()+1)||getEnergy()<100){
                //vas a por el lider enemigo
                
                if(getEnergy()>100){
                    liderEnemigo=encontrarLiderEnemigo();
                }
                fase=2;
                setTurnRadarRight(360);
                setTurnRight(normalizarAngulo(anguloHaciaEsquinaOpuesta - getHeading()));
                setAhead(100);
                
                execute();
            
            
            }
            else{
                
                setTurnRadarRight(350);
                 setTurnRight(normalizarAngulo(anguloHaciaEsquinaOpuesta - getHeading()));
                setAhead(100);
               
                execute();
                fase=1;
                




            }
            execute();
        }

    
    
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
         // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
         String mensajeCompleto = (String) event.getMessage();
         String[] partes = mensajeCompleto.split("\\|");
         if ("aliado".equals(partes[0])){
             
             String sender=event.getSender();
             double aux[]=new double[2];
             aux[0]=Double.parseDouble(partes[1]);
             aux[1]=Double.parseDouble(partes[2]);
             allies.put(sender, aux);
            
           
         
         }
         else if("meDio".equals(partes[0])){
            String robotNoLider = partes[1];
            if (!isAlly(robotNoLider)){
                
                noLideres.add(robotNoLider);
            }
            
            
        
        
        }
         else if("mate".equals(partes[0])){
           enemies.remove(partes[1]);
           noLideres.remove(partes[1]);
             
           
         
         
         
         
         }
         
         
    }
    
    
    
    
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        
        String enemy=event.getName();
        
        if (fase==1){
            
            if(!isAlly(enemy)){
                double angle=Math.toRadians(getHeading()+event.getBearing()%360);
                double ex=getX() +event.getDistance()*Math.sin(angle);
                double ey=getY() +event.getDistance()*Math.cos(angle);
                double positions[]=new double[2];
                positions[0]=ex;
                positions[1]=ey;
              
                enemies.put(enemy, positions);
                try {
                  
                    broadcastMessage("disparar|"+ex+"|"+ey);
                } catch (IOException ex1) {
                    Logger.getLogger(lider.class.getName()).log(Level.SEVERE, null, ex1);
                }
              
            
             }
        }
        else if(fase==2){
            if(enemy==liderEnemigo){
                double energiaenemigo=event.getEnergy();
                if (energiaenemigo<30){
                    
                    noLideres.add(enemy);
                
                } 
                double angle=Math.toRadians(getHeading()+event.getBearing()%360);
                double ex=getX() +event.getDistance()*Math.sin(angle);
                double ey=getY() +event.getDistance()*Math.cos(angle);
              
                try {
                    broadcastMessage("irydisparar|"+ex+"|"+ey);
                } catch (IOException ex1) {
                    Logger.getLogger(lider.class.getName()).log(Level.SEVERE, null, ex1);
                }
                
            
            
            }
            else if(!isAlly(enemy)){
                if (getEnergy()<100){
                    liderEnemigo=event.getName();
                
                
                
                }
                double angle=Math.toRadians(getHeading()+event.getBearing()%360);
                double ex=getX() +event.getDistance()*Math.sin(angle);
                double ey=getY() +event.getDistance()*Math.cos(angle);
                double positions[]=new double[2];
                positions[0]=ex;
                positions[1]=ey;
             
                enemies.put(enemy, positions);
            
            
            }
        
        
        }

        
        
        
        
        
        
        
        
    }
    @Override
    public void onHitByBullet(HitByBulletEvent event) {
      String enemyName = event.getName();
      noLideres.add(enemyName);




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
    
    
    private boolean isAlly(String enemy) {
      if (getTeammates()!=null){
          String[] teammates = getTeammates();
          for (String teammate : teammates) {
              if (teammate.equals(enemy)) {
                  return true;
              }
          }

      }
      return false;

  }
    private boolean IsInnnoLider(String enemy) {
        for (String noLider : noLideres) {
            if (noLider.equals(enemy)) {
                return true; // El robot escaneado no es el líder
            }
        }
        return false; // El robot escaneado es el líder
}
    private String encontrarLiderEnemigo() {
    String lider = null;
    for (Map.Entry<String, double[]> entry : enemies.entrySet()) {
        String enemyName = entry.getKey();
        if (!IsInnnoLider(enemyName)) {
            lider = enemyName;
            break;  // Encontraste al líder, puedes salir del bucle
        }
    }
    return lider;
}
   
    
    

    
}
