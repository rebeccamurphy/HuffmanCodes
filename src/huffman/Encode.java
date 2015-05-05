package huffman;

/**
 * Created by rebecca on 5/4/15.
 */
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Encode {
    public static void main(String[] args) throws IOException
    {
        if(args.length >= 2)
        {
            /*
            if(args[0].equals("-c"))
            {
                //saving the canon tree
                String message = ReadFile(args[2]);
                EncodeToFile(args[3], args[1], true, message);
            }
            else if(args[0].equals("-h"))
            {
                String message = ReadFile(args[2]);
                EncodeToFile(args[3], args[1], false, message);
            }
            else*/
            {
                String message = ReadFile(args[0]);
                EncodeToFile(args[1], message);
            }
        }
        else
        {
            System.out.println("Please provide sourcefile and targetfile, or optionally sourcefile");
            //String message = ReadFile("samples//text//sample7.txt");

            EncodeToFile("butt", "abc");
        }
        //Decode.main(new String[0]);
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

    static void EncodeToFile(String outputFile, String message){
        TreeMap<String, Integer> frequency = new TreeMap<String, Integer>();
        //getting the frequencies of each letter
        for (char ch : message.toCharArray()){
            if (frequency.containsKey(""+ch)){
                frequency.put(""+ch, frequency.get(ch)+1);
            }
            else{
                frequency.put(""+ch, 1);
            }
        }
        //create the normal tree
        TreeNode root;
        //curr is the note with the lowest frequency
        TreeNode curr = new TreeNode(frequency.firstEntry().getKey(), frequency.firstEntry().getValue());
        frequency.remove(frequency.firstEntry().getKey());
        while (frequency.size() >0){
            TreeNode next =new TreeNode(frequency.firstEntry().getKey(), frequency.firstEntry().getValue());
            frequency.remove(frequency.firstEntry().getKey());
            TreeNode parent = new TreeNode(curr.str+next.str, curr.count+next.count);
            curr.setParent(parent);
            next.setParent(parent);
            curr= parent;
        }
        root = curr;
        root.printTree(0);
        TreeMap<String, String> codes = new TreeMap<String, String>();
        TreeMap<String, String> canonCodes = new TreeMap<String, String>();
        root.getCodes("", codes);
        for (Map.Entry<String, String> entry : codes.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }

        int len = codes.get(codes.firstKey()).length();
        //to create a binary num with the right num of 0s
        char[] temp = new char[len];
        Arrays.fill(temp, '0');
        String firstNum = new String(temp);
        int binNum = 0b0;
        System.out.println(binNum +" " +firstNum);
        //convert to canon codes
        while(codes.size()>0){
            String currChar = codes.firstKey();
            if (codes.get(currChar).length() != len ){
               //need to shift binary num
                binNum = binNum >> 1;
               len = codes.get(currChar).length();
            }

            String bin = Integer.toString(binNum);
            for (int i=bin.length(); i<len; i++){
                bin = "0"+bin;
            }
            canonCodes.put(currChar, bin);
            codes.remove(codes.firstKey());
            binNum++;
        }
        for (Map.Entry<String, String> entry : canonCodes.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }
    }
}
