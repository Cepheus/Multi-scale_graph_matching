package util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Cette classe est un outil pour mesurer sous Windows l'utilisation du CPU
 * par le processus courant depuis sa cr�ation.
 *
 * @author Romain Raveaux � partir de l'id�e de Virginie Galtier
 * @version 1.0, 16/12/07
 *
 */

public class CpuMonitorWindows
{
	
	
    /**
     * nombre d'intervalles de 100 nanosecondes pass�es � s'ex�cuter en mode utilisateur
     */
    private  static long getUserTimeNano(){
    	return ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
    }

    /**
     * nombre d'intervalles de 100 nanosecondes pass�es � s'ex�cuter en mode privil�gi� (noyau)
     */
    private static long getKernelTimeNano(){
    	return 0;//ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
    }

    /**
     * nombre de secondes pass�es � s'ex�cuter en mode utilisateur
     * @return nombre de secondes pass�es � s'ex�cuter en mode utilisateur
     */
    public static double getUserTime()
    {
	return getUserTimeNano()/10000000.0;
    }

    /**
     * nombre de secondes pass�es � s'ex�cuter en mode privil�gi� (noyau)
     * @return nombre de secondes pass�es � s'ex�cuter en mode privil�gi�
     */
    public static double getKernelTime()
    {
	return getKernelTimeNano()/10000000.0;
    }

    /**
     * nombre de secondes pass�es � s'ex�cuter
     * @return nombre de secondes pass�es � s'ex�cuter
     */
    public static double getTime()
    {
	return getUserTime()+getKernelTime();
    }

    
}