

/*
 * Class:        PiecewiseConstantFunction
 * Description:  
 * Environment:  Java
 * Software:     SSJ 
 * Copyright (C) 2001  Pierre L'Ecuyer and Université de Montréal
 * Organization: DIRO, Université de Montréal
 * @author       Éric Buist
 * @since

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

package umontreal.iro.lecuyer.functions;

import java.util.Arrays;


/**
 * Represents a piecewise-constant function.
 * 
 */
public class PiecewiseConstantFunction implements MathFunction {
   private double[] x;
   private double[] y;


   /**
    * Constructs a new piecewise-constant function
    *  with $x$ and $y$ coordinates given
    *  by <TT>x</TT> and <TT>y</TT>.
    * 
    * @param x the $x$ coordinates.
    * 
    *    @param y the $y$ coordinates.
    * 
    * 
    */
   public PiecewiseConstantFunction (double[] x, double[] y) {
      if (x.length != y.length)
         throw new IllegalArgumentException();
      this.x = x.clone ();
      this.y = y.clone ();
   }


   /**
    * Returns the $x$ coordinates of the function.
    * 
    * @return the $x$ coordinates of the function.
    * 
    */
   public double[] getX() {
      return x.clone ();
   }


   /**
    * Returns the $y$ coordinates of
    *  the function.
    * 
    * @return the $y$ coordinates of the function.
    * 
    */
   public double[] getY() {
      return y.clone ();
   }


   public double evaluate (double x) {
      final int idx = Arrays.binarySearch (this.x, x);
      if (idx >= 0)
         return y[idx];
      final int insertionPoint = -(idx + 1);
      return y[insertionPoint];
   }
}
