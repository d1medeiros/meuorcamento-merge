package org.meuorcamento.util;

import java.security.SecureRandom;

public class TokenGenerator {

	 protected static SecureRandom random = new SecureRandom();
	    
	    public static  synchronized String generateToken( String word ) {
	            long longToken = Math.abs( random.nextLong() );
	            String random = Long.toString( longToken, 16 );
	            return  random;
	    }
	
}
