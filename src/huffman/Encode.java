package huffman;

/**
 * Created by rebecca on 5/4/15.
 */
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Encode {
    public static String eof = "\u0000";
    public static int k =-1;
    public static int msgLen=-1;
    public static void main(String[] args) throws IOException
    {
        if(args.length >= 2)
        {
            {
                String message = ReadFile(args[0]);
                String outputFile = args[1];
                EncodeToFile(message, outputFile);
            }
        }
        else
        {
            System.out.println("Please provide sourcefile and targetfile, or optionally sourcefile");

            String message = ReadFile("text/sample7.txt");
            msgLen =message.length();
            EncodeToFile(message, "butt.huf");
        }
    }
    /**
     * Read the encoded binary file
     * @param path File path to binary file
     * @return byte array representation of the binary file
     */
    static String ReadFile(String path) throws IOException
    {
        Path p = Paths.get(path);
        return new String(Files.readAllBytes(p));
    }

    public static void EncodeToFile(String message, String outputFile){
        /*
        * Change some hashmaps to priority queues when trying to get a minimum
        * */

        k =0; //number of character in alphabet
        TreeMap<String, Integer> frequency = new TreeMap<String, Integer>();
        //getting the frequencies of each letter
        for (char ch : message.toCharArray()){
            if (frequency.containsKey(""+ch)){
                frequency.put(""+ch, frequency.get(""+ch)+1);
            }
            else{
                frequency.put(""+ch, 1);
            }
        }
        //end of file
        frequency.put(eof, 1);
        System.out.println("freq");

        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        k = frequency.size();
        System.out.println("k "+k);
        //create the normal tree
        TreeNode root;
        root = makeTree(frequency);
        root.printTree(0);
        TreeMap<String, String> codes = new TreeMap<String, String>();
        TreeMap<String, String> canonCodes = new TreeMap<String, String>();
        root.getCodes("", codes);
        for (Map.Entry<String, String> entry : codes.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
        int len = codes.get(eof).length();
        //to create a binary num with the right num of 0s
        char[] temp = new char[len];
        Arrays.fill(temp, '0');
        String firstNum = new String(temp);
        canonCodes.put(eof, firstNum);
        int binNum = 0b1;
        ArrayList<String> canonOrderList = new ArrayList<String>();
        canonOrderList = canonOrder(codes, canonOrderList);
        //do eof first

        //convert to canon codes
        for(int i=0; i<canonOrderList.size(); i++){
            String currChar = canonOrderList.get(i).charAt(0) +"";
            String oldCode = canonOrderList.get(i).substring(1);
            if (oldCode.length() != len ){
               //need to shift binary num
               binNum = binNum >> 1;
               len =oldCode.length();
            }

            String bin = Integer.toBinaryString(binNum);
            for (int j=bin.length(); j<len; j++){
                bin = "0"+bin;
            }
            canonCodes.put(currChar, bin);
            binNum++;
        }
        System.out.println("Canon codes");
        for (Map.Entry<String, String> entry : canonCodes.entrySet()) {
            System.out.println(entry.getKey()+entry.getValue().length()+" : "+entry.getValue());
        }
        System.out.println("writing file");
        writeFile(outputFile, message, canonCodes);
    }
    public static TreeNode makeTree(TreeMap<String, Integer> frequency){
        //ArrayList<TreeNode> nodes = new ArrayList<TreeNode>();
        PriorityQueue<TreeNode> nodes = new PriorityQueue<TreeNode>();
        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            nodes.add(new TreeNode(entry.getKey(), entry.getValue()));
        }

        while(nodes.size()>1){
            int min =100000;
            String minKey ="";
            boolean first = true;
            for (Map.Entry<String,Integer> entry :frequency.entrySet()) {
                if (first){
                    minKey = entry.getKey();
                    first = false;
                }
                if (entry.getValue()<min||
                        (entry.getValue()==min&&entry.getKey().length()==minKey.length()&& (entry.getKey().compareTo(minKey)>=0))||
                        (entry.getValue()==min&&entry.getKey().length()<minKey.length())){
                        min = entry.getValue();
                        minKey = entry.getKey();
                }
            }
            TreeNode curr = null;
            for (int i =0; i<nodes.size(); i++){
                if (nodes.get(i).str.equals(minKey)){
                    curr = nodes.get(i);
                    break;
                }
            }

            curr =(curr==null)? new TreeNode(minKey,min):curr;
            System.out.println("minkey " + minKey);
            frequency.remove(minKey);
            int min2 =100000;
            String minKey2 ="";
            first = true;
            for (Map.Entry<String,Integer> entry :frequency.entrySet()) {
                if (first){
                    minKey2 = entry.getKey();
                    first = false;
                }
                if (entry.getValue()<min2||
                        (entry.getValue()==min2&&entry.getKey().length()==minKey2.length()&& (entry.getKey().compareTo(minKey2)>=0))||
                        (entry.getValue()==min2 &&entry.getKey().length()<minKey2.length())){
                    min2 = entry.getValue();
                    minKey2 = entry.getKey();
                }
            }
            TreeNode next = null;
            for (int j =0; j<nodes.size(); j++){
                if (nodes.get(j).str.equals(minKey2)){
                    next = nodes.get(j);
                    break;
                }
            }

            next =(next==null)? new TreeNode(minKey2,min2):next;
            frequency.remove(minKey2);
            System.out.println("minkey2 " + minKey2);
            TreeNode parent = new TreeNode(curr.str+next.str, curr.count+next.count);
            curr.setParent(parent);
            next.setParent(parent);
            System.out.println("parent " + parent);
            nodes.add(parent);
            frequency.put(parent.str, parent.count);
            System.out.println("Pair: " + minKey + ", " + minKey2);

        }
        return null;
    }

    public static ArrayList<String> canonOrder(TreeMap<String, String> codes, ArrayList<String> array){
        codes.remove(eof);
        while (codes.size()>0){
            int max =-1;
            String key ="";
            int keyVal = 100000;
            for (Map.Entry<String, String> entry : codes.entrySet()) {
                if (entry.getValue().length() > max){
                    max= entry.getValue().length();
                }
            };
            for (Map.Entry<String, String> entry : codes.entrySet()) {
                if (entry.getValue().length()==max &&(int)(entry.getKey().toCharArray()[0]) <keyVal) {
                    key = entry.getKey();
                    keyVal =(int)(entry.getKey().toCharArray()[0]);
                }
            }
            array.add(key + codes.get(key));
            codes.remove(key);
        }
        return array;
    }
    public static void writeFile(String outputFile, String message,  TreeMap<String, String> canonCodes){

        try {
            // Put some bytes in a buffer so we can
            // write them. Usually this would be
            // image data or something. Or it might
            // be unicode text.
            byte buffer;
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(k);
            outputStream = new FileOutputStream(outputFile, true);
            ArrayList<Integer> header = new ArrayList<Integer>();
            header.add(0);
            header.add(canonCodes.get(eof).length());
            ArrayList<String>encodedmsg = new ArrayList<>();

            for (Map.Entry<String, String> entry : canonCodes.entrySet()) {
                if (entry.getKey()!=eof){
                    header.add((int)(entry.getKey().charAt(0)));
                    header.add(entry.getValue().length());
                }
            }
            //System.out.println("Created header. " + message);
            String secret ="";

            for (int j=0; j<header.size(); j++){
                System.out.println(j +": "+header.get(j) + " " + Integer.toBinaryString(header.get(j)));
                buffer = header.get(j).byteValue();
                outputStream.write(buffer);
            }
            StringBuilder sb = new StringBuilder();
            if (message.length()<257){
                for (int i=0; i<message.length();i++){
                    sb.append(canonCodes.get("" + message.charAt(i)));
                    while (sb.length()>=8){
                        outputStream.write(Integer.parseInt(sb.toString().substring(0,8), 2));
                        sb.delete(0,8);
                    }

                }
                System.out.println(sb.toString());
                if (sb.toString().isEmpty())
                    outputStream.write(0);
                else{
                    while (sb.length()!=8)
                        sb.append("0");
                    outputStream.write(Integer.parseInt(sb.toString(), 2));
                    outputStream.write(0);
                }
                    

            }

            else {
                final Field field = String.class.getDeclaredField("value");
                field.setAccessible(true);
                final char[] chars = (char[]) field.get(message);
                final int len = chars.length;
                System.out.println(len);

                for (int i=0; i<len; i++){
                    sb.append(canonCodes.get("" + chars[i]));
                    while (sb.length()>=8){
                            //System.out.println(i + ": writing secret " +sb.length());
                            outputStream.write(Integer.parseInt(sb.toString().substring(0,8), 2));
                            sb.delete(0,8);
                    }
                }
                System.out.println(sb.toString());
                if (sb.toString().isEmpty())
                    outputStream.write(0);
                else{
                    while (sb.length()!=8)
                        sb.append("0");
                    outputStream.write(Integer.parseInt(sb.toString(), 2));
                    for (int i=0; i<canonCodes.get(eof).length(); i+=8)
                        outputStream.write(0);
                }

            }

            System.out.println("secret: " + secret);
/*
            for (int i=0; i<secret.length(); i+=8){
                if (i+8 <=secret.length()){
                    System.out.println("each byte: "+secret.substring(i, i+8));
                    header.add(Integer.parseInt(secret.substring(i, i + 8), 2));
                }
                else if (secret.length()-i+8 !=0){
                    String temp = secret.substring(i);
                    while (temp.length()<8)
                        temp+="0";
                    header.add(Integer.parseInt(temp, 2));
                }

            }
*/
            // write() writes as many bytes from the buffer
            // as the length of the buffer. You can also
            // use
            // write(buffer, offset, length)
            // if you want to write a specific number of
            // bytes, or only part of the buffer.

            // Always close files.
            outputStream.close();

            //System.out.println("Wrote " + header.length +" bytes");
        }
        catch(IOException ex) {
            System.out.println("Error writing file '" + outputFile + "'");

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
