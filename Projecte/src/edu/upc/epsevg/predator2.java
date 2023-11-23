/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg;

import java.io.IOException;
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.Robot;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

/**
 *
 * @author marc
 */
public class predator2 extends TeamRobot{
    private double fase=0.0;
    private  Map<String,double[]>allies=new HashMap<>();
    private  Map<String,double[]> enemies=new HashMap<>();
    private Target target=null;
    private double MAX_LIFE=7;
    private int directionTimer=0;
    private int contador=0;
    private int angulo=90;
    private double synctime=0;
   @Override
    public void run(){
      
       
              
        while(true){
            if(fase==0.0){
            
            
            try {
                //Envia la posició on estic
                broadcastMessage(getX()+"|"+getY());
                
                System.out.println("envio");
                
            } catch (IOException ex) {
                
                Logger.getLogger(predator2.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            fase=0.2;
            
        }
            setAdjustGunForRobotTurn(true);
            setAdjustRadarForGunTurn(true);
            
        
            if(fase==0.5){
                ++contador;
                //giro fins detectar enemics
                if (contador==40){
                    while (enemies.isEmpty()){
                        double anchoCampo = getBattleFieldWidth();
                        double altoCampo = getBattleFieldHeight();
                        double esquinaOpuestaX = anchoCampo - getX();
                        double esquinaOpuestaY = altoCampo - getY();

                        double anguloHaciaEsquinaOpuesta = Math.toDegrees(Math.atan2(esquinaOpuestaX - getX(), esquinaOpuestaY - getY()));
                        setTurnRight(normalizarAngulo(anguloHaciaEsquinaOpuesta - getHeading()));

                        setAhead(100);
                        setTurnRadarRight(360);
                        execute();

                     
                        
                    }
                    contador=0;
                    targetEnemy();
                    fase=1;
             
                }
                
                
                setTurnRadarRight(360);
                execute();
            
            }
            //camino fins al enemic
            
            else if(fase==1.0){
                
                    
                double aux[]=new double[2];
                aux[0]=getX();
                aux[1]=getY();
                double taux[]=new double[2];
                taux[0]=target.getX();
                taux[1]=target.getY();
                double distancia=calcularDistancia(aux, taux);
                
                double angleToEnemy = Math.toDegrees(Math.atan2(target.getX() - getX(), target.getY() - getY()));
                setTurnRight(normalizarAngulo(angleToEnemy - getHeading()) );
                setTurnRadarRight(360);
                if (distancia>300){
                    ahead(distancia-290);
                    
                }
                else {
                    fase=2;
                        }
                
                

                    
                
                execute();
            }
            //orbita
            else if(fase==2){
              
                double angleToEnemy = Math.toDegrees(Math.atan2(target.getX() - getX(), target.getY() - getY()));
                double angleGunEnemy=normalizarAngulo(angleToEnemy-getGunHeading());
                double turnAngle;
                ++directionTimer;
                setTurnGunRight(angleGunEnemy);
               
               
                if (directionTimer == 20) {
                    
                    try {
                        broadcastMessage("girar");
                    } catch (IOException ex) {
                        Logger.getLogger(predator2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    while(directionTimer<=25){
                     
                        ++directionTimer;
                    }
                    
                        turnRight(180);
                        angulo=-angulo;
                        
                        directionTimer = 0;

                }
                
                
                turnAngle =normalizarAngulo(angleToEnemy -getHeading() -angulo);
              
                setTurnRight(turnAngle );
             
                  
                
                setAhead(40);
                setTurnRadarRight(1000);
                execute();
    }
            else if(fase==3){
                //fase de ramming
                double angleToEnemy = Math.toDegrees(Math.atan2(target.getX() - getX(), target.getY() - getY()));
                double turnAngle = normalizarAngulo(angleToEnemy - getHeading());
                setTurnRight(turnAngle);
                setAhead(400); 
                turnRadarRight(360);
                execute();
            }
            
            else if(fase==4){
                //fase de restart
                allies.clear();
                enemies.clear();
                directionTimer=0;
                contador=0;
                angulo=90;
                target=null;
                setBack(200);
                if (synctime==0){
                    
                    try {
                        synctime=getTime()+12;
                        execute();
                        
                        broadcastMessage(synctime);
                        
                        
                    } catch (IOException ex) {
                        Logger.getLogger(predator2.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                }
                while(getTime()!=synctime){
                    execute();
                }
                synctime=0;
                fase=0;
                
                
                execute();
                
                }
                execute();
            }

    }
    
    @Override
      public void onMessageReceived(MessageEvent event) {
        if (fase==0.2){
            String mensajeCompleto = (String) event.getMessage();
            String[] partes = mensajeCompleto.split("\\|");
            if (partes.length==2){
                String sender=event.getSender();
                double aux[]=new double[2];
                aux[0]=Double.parseDouble(partes[0]);
                aux[1]=Double.parseDouble(partes[1]);
                allies.put(sender, aux);
                if(allies.size()==getTeammates().length){
      
                    fase=0.5;
                 


                }
            }
        }
        else if(fase==0.5){
       
            String mensajeCompleto = (String) event.getMessage();
            String[] partes = mensajeCompleto.split("\\|");
            String enemy= partes[0];
            double aux[]=new double[2];
            aux[0]=Double.parseDouble(partes[1]);
            aux[1]=Double.parseDouble(partes[2]);
            enemies.put(enemy, aux);
            
            
        }
        else if(fase==1.0){
            String mensaje = (String) event.getMessage();
            if (mensaje.equals("girar")){
             
                angulo=-angulo;
                directionTimer=200;
            }
        
        }
        else if(fase==2.0){
             String mensaje = (String) event.getMessage();
            if (mensaje.equals("girar")){
            
              turnRight(180);
              angulo=-angulo;
              directionTimer = 200; //Para que solo mande girar el primero que llegue
     
            }
        
        }
        
        else if(fase==3.0){
            String mensaje = (String) event.getMessage();
            if (mensaje.equals("cefine")){
                
                fase=4;
                
              }
        }
        else if(fase==4.0){
            if (synctime==0){
                double mensajeCompleto = ((Double) event.getMessage()).doubleValue();
               
                synctime = mensajeCompleto;
            }
        
        }
        
      }
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
      // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        if(fase==0.5){
         String enemy=event.getName();
         if(!isAlly(enemy)){
            
            double angle=Math.toRadians(getHeading()+event.getBearing()%360);
            double ex=getX() +event.getDistance()*Math.sin(angle);
            double ey=getY() +event.getDistance()*Math.cos(angle);
            double positions[]=new double[2];
            positions[0]=ex;
            positions[1]=ey;
            //inclou en el mapa elsenemics que detecta
            enemies.put(enemy, positions);
             try {
    
                 broadcastMessage(enemy+"|"+ex+"|"+ey);
             } catch (IOException ex1) {
                 Logger.getLogger(predator2.class.getName()).log(Level.SEVERE, null, ex1);
             }
         }
     }
    if (fase==1.0){
        
        if (event.getName().equals(target.getName())){
        //actualitza la posicio del target
           double angle=Math.toRadians(getHeading()+event.getBearing()%360);
           double ex=getX() +event.getDistance()*Math.sin(angle);
           double ey=getY() +event.getDistance()*Math.cos(angle);
           target.setX(ex);
           target.setY(ey);



        }
    }
    else if(fase==2.0){
        if (event.getName().equals(target.getName())){
            //actualitza la posició del target
            double angle=Math.toRadians(getHeading()+event.getBearing()%360);
            double ex=getX() +event.getDistance()*Math.sin(angle);
            double ey=getY() +event.getDistance()*Math.cos(angle);
            target.setX(ex);
            target.setY(ey);
            double angleToEnemy = Math.toDegrees(Math.atan2(target.getX() - getX(), target.getY() - getY()));
            double angleGunEnemy=normalizarAngulo(angleToEnemy-getGunHeading());
            double life=event.getEnergy();
            
            if (life>MAX_LIFE){
                setTurnGunRight(angleGunEnemy);
                if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
                        
                        fire(0.3);
                    }
            }
            else{
                fase=3;
            }
        }
    
    
    }


 }
    
    @Override
public void onHitRobot(HitRobotEvent event) {
    if (fase == 3 && event.getName().equals(target.getName())) {
        back(50);
        if(event.getEnergy()==0){
            try {
                broadcastMessage("cefine");
                fase=4;
            } catch (IOException ex) {
                Logger.getLogger(predator2.class.getName()).log(Level.SEVERE, null, ex);
                }   
        }
    }
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
private double encontrarDistanciaMaxima(String enemy) {
    double distanciaMaxima = 0;
    double[] posicionEnemigo = enemies.get(enemy);
    for (Map.Entry<String, double[]> allyEntry : allies.entrySet()) {
        double[] posicionAliado = allyEntry.getValue();
        double distancia = calcularDistancia(posicionEnemigo, posicionAliado);
        if (distancia > distanciaMaxima) {
            distanciaMaxima = distancia;
        }
    }
    return distanciaMaxima;
}

    // Función para calcular la distancia entre dos puntos (x, y)
private double calcularDistancia(double[] punto1, double[] punto2) {
    double dx = punto1[0] - punto2[0];
    double dy = punto1[1] - punto2[1];
    return Math.sqrt(dx * dx + dy * dy);
}

    
private void targetEnemy() {
    String enemySeleccionado = null;
    double distanciaMinima = Double.MAX_VALUE;
    double aux[]=new double[2];
    aux[0]=getX();
    aux[1]=getY();
    allies.put(getName(), aux);

    for (Map.Entry<String, double[]> enemyEntry : enemies.entrySet()) {
        String enemy = enemyEntry.getKey();
        double distanciaMaxima = encontrarDistanciaMaxima(enemy);
        if (distanciaMaxima < distanciaMinima) {
            distanciaMinima = distanciaMaxima;
            enemySeleccionado = enemy;
        }
    }

    double[] posicionEnemigo = enemies.get(enemySeleccionado);
    target = new Target(enemySeleccionado, posicionEnemigo[0], posicionEnemigo[1]);

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


}


class Target {
        private String name;
        private double x;
        private double y;

        public Target(String name, double x, double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        public String getName() {
            return name;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
        public void setX(double x) {
            this.x = x;
    }
        public void setY(double y) {
            this.y = y;
    }
    }




