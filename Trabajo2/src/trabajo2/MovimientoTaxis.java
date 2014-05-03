/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 fasdfasfd
 */
package trabajo2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tavo
 */
public class MovimientoTaxis extends Thread {

    public static final int dx[] = {-1, 0, 1, 0};
    public static final int dy[] = {0, 1, 0, -1};
    private List<Taxi> taxis;
    private Ciudad ciudad;
    private Graficador graficador;
    private ControladorAplicacion controladorAplicacion;
    public static boolean moverTaxis = false;
    public static boolean esperaInicio = false;

    private volatile Thread hilo;

    public MovimientoTaxis(List<Taxi> taxis, Ciudad ciudad, Graficador graficador) {
        this.taxis = taxis;
        this.ciudad = ciudad;
        this.graficador = graficador;
    }

    public MovimientoTaxis() {
    }

    public void setTaxis(List<Taxi> taxis) {
        this.taxis = taxis;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public void setGraficador(Graficador graficador) {
        this.graficador = graficador;
    }

    @Override
    public void run() {
        Thread hiloActual = Thread.currentThread();
        while (hilo == hiloActual) {
            if (esperaInicio) {
                try {
                    hiloActual.sleep(3000);
                    esperaInicio = false;
                } catch (InterruptedException ex) {
                    break;
                }
            }

            while (moverTaxis && !esperaInicio) {
                int[][] matriz = ciudad.getMatrizActual();
                int opcionElegida;
                ArrayList<Integer> nroOpciones;//aloja las opciones posibles... op1:Arriba op2:derecha op3:abajo op4:izquierda
                for (Taxi taxi : taxis) {
                    if (Math.abs(matriz[taxi.y][taxi.x]) != 1) {
                        matriz[taxi.y][taxi.x] = 0;
                    }
                    if (taxi.isEnCarrera()) {
                        Point siguiente = taxi.siguientePosicion();
                        taxi.removerPosicion();
                        if (siguiente == null && taxi.getIndex() == 0) {
                            taxi.incrementarIndex();
                            controladorAplicacion.setDestination(taxi);
                            continue;
                        } else if (siguiente == null) {

                            controladorAplicacion.notificarFinCarrera(taxi);
                            continue;
                        }
                        if (Math.abs(matriz[taxi.y][taxi.x]) != 1) {
                            matriz[taxi.y][taxi.x] = 2;
                        }
                        taxi.setLocation(siguiente);
                        continue;
                    }
                    nroOpciones = new ArrayList();
                    for (int i = 0; i < 4; i++) {
                        if (Math.abs(matriz[taxi.y + dy[i]][taxi.x + dx[i]]) != 1) {
                            nroOpciones.add(i + 1);
                        }
                    }
                    opcionElegida = (nroOpciones.size() > 0) ? nroOpciones.get((int) (Math.random() * nroOpciones.size())) : 5;
                    if (opcionElegida != 5) {
                        taxi.y += dy[opcionElegida - 1];
                        taxi.x += dx[opcionElegida - 1];
                        if (Math.abs(matriz[taxi.y][taxi.x]) != 1) {
                            matriz[taxi.y][taxi.x] = 2;
                        }
                    }
                }
                if (moverTaxis) {
                    graficador.graficarTaxis(taxis);
                }
                break;
            }
            try {
                hiloActual.sleep(1000);
            } catch (InterruptedException ex) {
                break;
            }
        }
    }

    @Override
    public void start() {
        hilo = new Thread(this);
        hilo.start();
    }

    public void stopThread() {
        Thread hiloParar = hilo;
        hilo = null;
        hiloParar.interrupt();
    }

    public boolean isRunning() {
        return hilo != null;
    }

    /**
     * @return the controladorAplicacion
     */
    public ControladorAplicacion getControladorAplicacion() {
        return controladorAplicacion;
    }

    /**
     * @param controladorAplicacion the controladorAplicacion to set
     */
    public void setControladorAplicacion(ControladorAplicacion controladorAplicacion) {
        this.controladorAplicacion = controladorAplicacion;
    }
}
