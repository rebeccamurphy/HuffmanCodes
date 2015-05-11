package huffman;

/**
 * Created by rebecca on 5/4/15.
 */
import java.io.*;
import java.util.*;
public class Decode {
    public static String eof = "\u0000";
    public static int k;
    public static HashMap<String, Integer> codeLengths;
    public static void main(String[] args) throws IOException
    {
        if(args.length >= 2)
        {
            {
                codeLengths = new HashMap<>();
                k=-1;
                String secret = ReadFile(args[0]);
                String outputFile = args[1];
                DecodeToFile(secret, outputFile);
            }
        }
        else
        {
            System.out.println("Please provide sourcefile and targetfile.");

        }
    }
    /**
     * Read the encoded binary file
     * @param path File path to binary file
     * @return String  representation of encoded message
     */
    public static String ReadFile(String path) throws IOException {
        String temp="", temp2="";

            // Use this for reading the data.
            BufferedInputStream bis = null;
            InputStream inStream = new FileInputStream(path);

            bis = new BufferedInputStream(inStream);

            k = bis.read();
            for (int i=1; i<k*2+1; i+=2){
                temp= String.format("%8s", Integer.toBinaryString(bis.read() & 0xFF)).replace(' ', '0');

                String chr = (temp.equals("00000000"))?eof :""+(char) Integer.parseInt(temp, 2);

                codeLengths.put(chr, bis.read());

            }
            StringBuilder sb = new StringBuilder(bis.available()*8);
            while (bis.available()>0){
                sb.append(String.format("%8s", Integer.toBinaryString(bis.read() & 0xFF)).replace(' ', '0'));
            }
            bis.close();
            inStream.close();

            // Always close files
        return sb.toString();

    }

    /***
     * Decodes the message of the binary file
     * @param secret message of the binary file
     * @param outputFile output file path
     */
    public static void DecodeToFile(String secret, String outputFile){
        String temp ="";
        HashMap<String, String> canonCodesList = new HashMap<>();
        ArrayList<String> canonOrderList = new ArrayList<String>();


        int len = codeLengths.get(eof);

        canonOrderList = canonOrder(codeLengths, canonOrderList);

        int binNum = 0b0;

        for(int i=0; i<canonOrderList.size(); i++){
            String currChar = canonOrderList.get(i).charAt(0) +"";
            int oldCode = Integer.parseInt(canonOrderList.get(i).substring(1));
            if (oldCode!= len ){
                //need to shift binary num
                binNum = binNum >> 1;
                len =oldCode;
            }
            String bin = Integer.toBinaryString(binNum);
            for (int j=bin.length(); j<len; j++){
                bin = "0"+bin;
            }
            canonCodesList.put(bin, currChar);
            binNum++;
        }
        temp="";
        String decodedMsg ="";

        StringBuilder sb = new StringBuilder();
        for (int j =0; j<secret.length(); j++){
            temp+=""+secret.charAt(j);
            if (canonCodesList.containsKey(temp)){
                if (!canonCodesList.get(temp).equals(eof)){
                    sb.append(canonCodesList.get(temp));
                    temp="";
                }
                else
                    break;
            }
        }
        decodedMsg =sb.toString();
        writeToFile(outputFile, decodedMsg);
    }

    /***
     * Writes the decoded message to file
     * @param outputFile output file path
     * @param decodedMsg decoded message
     */
    public static void writeToFile(String outputFile, String decodedMsg){
        try {
            PrintWriter out = new PrintWriter(outputFile);
            out.println(decodedMsg);
            out.close();

        }
        catch(IOException ex) {
            System.out.println("Error writing file '" + outputFile + "'");

        }

    }

    /**
     * Puts the read in header codes in canon order
     * @param codes list of read in codes
     * @param array canon order of codes
     * @return array of canon codes
     */
    public static ArrayList<String> canonOrder(HashMap<String, Integer> codes, ArrayList<String> array){
        while (codes.size()>0){
            int max =-1;
            String key ="";
            int keyVal = 100000;
            for (Map.Entry<String, Integer> entry : codes.entrySet()) {
                if (entry.getValue() > max){
                    max= entry.getValue();
                }
            };
            for (Map.Entry<String, Integer> entry : codes.entrySet()) {
                if (entry.getValue()==max &&(int)(entry.getKey().toCharArray()[0]) <keyVal) {
                    key = entry.getKey();
                    keyVal =(int)(entry.getKey().toCharArray()[0]);
                }
            }
            array.add(key + codes.get(key));
            codes.remove(key);
        }
        return array;
    }

}
