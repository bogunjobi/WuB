package mwn.bolfab.wakeupbuddy;

import java.io.BufferedReader;

public class RWFile {

	public static String [] readContacts(BufferedReader r){
		StringBuilder sb = new StringBuilder();
        try{
            
            String line = null;
            while ((line = r.readLine()) != null) {
                sb.append(line).append("\n");
            }
            r.close();
        } catch(OutOfMemoryError om){
            om.printStackTrace();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        String [] result = sb.toString().split(";");
        return result;
	}
}
