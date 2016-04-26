//(c) Alex Ellison 2013
package tsp;

public class BigFactorial {

    /*
     * calculates factorials of large numbers approximately.
     * can go up to 250 million roughly, with first 3 digits accurate.
     * underlying method can calculate the log of any base of any n!
     */
        

    static String bigFactorial(int num) {
       
        double d = logfact(num, 10);
        int i = (int) d;
        double digits = d - (double) i;
        return Math.pow(10, digits) + "E" + i;

    }

    static public double logfact(int num, int base) {
        // returns log(i!) of specified base. If base is power of 2, it uses shifts
        int out = 0;
        double log = 0;
        double baselog= Math.log(base);
        int baseTemp = base;
        int pow2 = 0;
        while (baseTemp % 2 == 0) {
            pow2++;
            baseTemp = baseTemp >> 1;
        }
        //if baseTemp=1 then base is a power of 2
        if (baseTemp == 1) {
            for (int i = 1; i <= num; i++) {
                int temp = i;
                while (temp % base == 0) {
                    out++;
                    temp = temp >> pow2;
                }
              
                log += Math.log(temp) / baselog;
      
            }
        } else {
            // not "nice" base, slower to compute
            for (int i = 1; i <= num; i++) {
                int temp = i;
              while (temp % base == 0) {
                    out++;
                    temp /= base;
               }
              // backup
              log+=Math.log(temp)/baselog;
             
            }
        }
        return out + log;
    }
}
