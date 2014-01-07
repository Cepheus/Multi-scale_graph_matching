package util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Cette classe est un outil pour mesurer sous Windows l'utilisation du CPU
 * par le processus courant depuis sa création.
 *
 * @author Romain Raveaux à partir de l'idée de Virginie Galtier
 * @version 1.0, 16/12/07
 *
 */

public class CpuMonitorWindows
{
	
	
    /**
     * nombre d'intervalles de 100 nanosecondes passées à s'exécuter en mode utilisateur
     */
    private  static long getUserTimeNano(){
    	return ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
    }

    /**
     * nombre d'intervalles de 100 nanosecondes passées à s'exécuter en mode privilégié (noyau)
     */
    private static long getKernelTimeNano(){
    	return 0;//ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
    }

    /**
     * nombre de secondes passées à s'exécuter en mode utilisateur
     * @return nombre de secondes passées à s'exécuter en mode utilisateur
     */
    public static double getUserTime()
    {
	return getUserTimeNano()/10000000.0;
    }

    /**
     * nombre de secondes passées à s'exécuter en mode privilégié (noyau)
     * @return nombre de secondes passées à s'exécuter en mode privilégié
     */
    public static double getKernelTime()
    {
	return getKernelTimeNano()/10000000.0;
    }

    /**
     * nombre de secondes passées à s'exécuter
     * @return nombre de secondes passées à s'exécuter
     */
    public static double getTime()
    {
	return getUserTime()+getKernelTime();
    }

    
}