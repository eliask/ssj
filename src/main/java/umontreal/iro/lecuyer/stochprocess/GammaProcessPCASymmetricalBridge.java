

/*
 * Class:        GammaProcessPCASymmetricalBridge
 * Description:  
 * Environment:  Java
 * Software:     SSJ 
 * Copyright (C) 2001  Pierre L'Ecuyer and Université de Montréal
 * Organization: DIRO, Université de Montréal
 * @authors      Jean-Sébastien Parent and Maxime Dion
 * @since        july 2008

 * SSJ is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License (GPL) as published by the
 * Free Software Foundation, either version 3 of the License, or
 * any later version.

 * SSJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * A copy of the GNU General Public License is available at
   <a href="http://www.gnu.org/licenses">GPL licence site</a>.
 */

package umontreal.iro.lecuyer.stochprocess;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.randvar.*;



/**
 * Same as {@link GammaProcessPCABridge}, but uses the fast inversion method
 * for the symmetrical beta distribution, proposed by L'Ecuyer and Simard, to accelerate the generation of the beta random variables.
 * This class works only in the case where the number of intervals is a power of
 * 2 and all these intervals are of equal size.
 * 
 */
public class GammaProcessPCASymmetricalBridge extends GammaProcessPCABridge  {



   /**
    * Constructs a new <TT>GammaProcessPCASymmetricalBridge</TT> 
    * with parameters 
    * <SPAN CLASS="MATH"><I>&#956;</I> = <texttt>mu</texttt></SPAN>, 
    * <SPAN CLASS="MATH"><I>&#957;</I> = <texttt>nu</texttt></SPAN> and initial 
    * value 
    * <SPAN CLASS="MATH"><I>S</I>(<I>t</I><SUB>0</SUB>) = <texttt>s0</texttt></SPAN>.
    * The {@link umontreal.iro.lecuyer.rng.RandomStream RandomStream} <TT>stream</TT>
    * is used in the 
    * {@link umontreal.iro.lecuyer.randvar.GammaGen GammaGen}
    * and in the {@link umontreal.iro.lecuyer.randvar.BetaSymmetricalGen BetaSymmetricalGen}.
    * These two generators use inversion to generate random numbers.  The first
    * uniform random number generated by <TT>stream</TT> is used for the gamma, and the 
    * other <SPAN CLASS="MATH"><I>d</I> - 1</SPAN> for the beta's.
    * 
    */
   public GammaProcessPCASymmetricalBridge (double s0, double mu, double nu,
                                            RandomStream stream)  {
        super (s0, mu, nu,  stream);
   }


   public double[] generatePath (double[] uniform01)  {
    // uniformsV[] of size d+1, but element 0 never used.
        double[] uniformsV = new double[d+1];

    // generate BrownianMotion PCA path
        double[] BMPCApath = BMPCA.generatePath(uniform01);
        int oldIndexL;
        int newIndex;
        int oldIndexR;
        double temp, y;
    // Transform BMPCA points to uniforms using an inverse bridge.
        for (int j = 0; j < 3*(d-1); j+=3) {
            oldIndexL   = BMBridge.wIndexList[j];
            newIndex    = BMBridge.wIndexList[j + 1];
            oldIndexR   = BMBridge.wIndexList[j + 2];

            temp = BMPCApath[newIndex] - BMPCApath[oldIndexL];
            temp -= (BMPCApath[oldIndexR] - BMPCApath[oldIndexL]) *
                                          BMBridge.wMuDt[newIndex];
            temp /= BMBridge.wSqrtDt[newIndex];
            uniformsV[newIndex] = NormalDist.cdf01(temp);
        }
    double dT = BMPCA.t[d] - BMPCA.t[0];
    uniformsV[d] = NormalDist.cdf01( ( BMPCApath[d] - BMPCApath[0] - BMPCA.mu*dT )/
                     ( BMPCA.sigma * Math.sqrt(dT) ) );


    // Reconstruct the bridge for the GammaProcess from the Brownian uniforms.
    // Here we have to hope that the bridge is implemented in the
    // same order for the Brownian and Gamma processes.

        path[0] = x0;
        path[d] = x0 + GammaDist.inverseF(mu2dTOverNu, muOverNu, 10, uniformsV[d]);
        for (int j = 0; j < 3*(d-1); j+=3) {
            oldIndexL   = wIndexList[j];
            newIndex    = wIndexList[j + 1];
            oldIndexR   = wIndexList[j + 2];

            y = BetaSymmetricalDist.inverseF(bMu2dtOverNuL[newIndex], uniformsV[newIndex]);

            path[newIndex] = path[oldIndexL] +
        (path[oldIndexR] - path[oldIndexL]) * y;
        }
        //resetStartProcess();
        observationIndex   = d;
        observationCounter = d;
        return path;
    }


   public double[] generatePath()  {
        double[] u = new double[d];
        for(int i =0; i < d; i++)
            u[i] = stream.nextDouble();
        return generatePath(u);
    }


    // code taken from GammaSymmetricalBridge to check time is power of 2,
    // as it is required for the symmetrical bridge.
   protected void init () {
        super.init ();
        if (observationTimesSet) {

            /* Testing to make sure number of observations n = 2^k */
                int x = d;
            int y = 1;
            while (x>1) {
            x = x / 2; 
            y = y * 2;
            }
            if (y != d) throw new IllegalArgumentException
            ( "GammaSymmetricalBridgeProcess:"
                +"Number 'n' of observation times is not a power of 2" );
       }
    }
}
