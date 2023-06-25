package net.orange.game.data.nbt;

import net.orange.game.data.DataTool;

public class NBTAnalyze {
    public static TAG_Compound run(String string){
        string = simply(string);
        try{
            return compound(string);
        }catch (NBTAnalyzeException e){
            System.out.println("can't analyze nbt "+string+" : "+e);
        }
        return null;
    }
    public static String simply(String string){
        string = string.replace("\t","").replace("\r","");
        int index = 0;
        int quote = 0;
        boolean slash = false;
        StringBuilder out = new StringBuilder();
        while (index<string.length()){
            char ch = string.charAt(index);
            if(quote == 0){
                if (ch != ' ' && ch != '\n'){
                    out.append(ch);
                    if (ch == '\"'){
                        quote = 2;
                    }
                    if (ch == '\''){
                        quote = 1;
                    }
                }
            }else if (quote == 1){
                if (ch != '\n') {
                    if (!slash && ch == '\'') quote = 0;
                    slash = ch == '\\' && !slash;
                    out.append(ch);
                }
            }else {
                if (ch != '\n') {
                    if (!slash && ch == '\"') quote = 0;
                    slash = ch == '\\' && !slash;
                    out.append(ch);
                }
            }
            index++;
        }
        return out.toString();
    }
    public static NBTObj analyze(String string) throws NBTAnalyzeException {
        if (string.startsWith("{") && string.endsWith("}")) {
            return compound(string);
        } else if (string.startsWith("[I;") && string.endsWith("]")) {
            String[] split = string.substring(3, string.length() - 1).split(",");
            int[] data = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                data[i] = Integer.parseInt(split[i]);
            }
            return new TAG_Int_Array(data);
        } else if (string.startsWith("[B;") && string.endsWith("]")) {
            String[] split = string.substring(3, string.length() - 1).split(",");
            byte[] data = new byte[split.length];
            for (int i = 0; i < split.length; i++) {
                data[i] = Byte.parseByte(split[i]);
            }
            return new TAG_Byte_Array(data);
        } else if (string.startsWith("[L;") && string.endsWith("]")) {
            String[] split = string.substring(3, string.length() - 1).split(",");
            long[] data = new long[split.length];
            for (int i = 0; i < split.length; i++) {
                data[i] = Long.parseLong(split[i]);
            }
            return new TAG_Long_Array(data);
        } else if (string.startsWith("[") && string.endsWith("]")) {
            return list(string);
        } else if ((string.startsWith("\"") && string.endsWith("\"")) || (string.startsWith("'") && string.endsWith("'"))) {
            return new TAG_String(DataTool.removeBrackets(string.substring(1, string.length() - 1)));
        } else {
            try {
                if (string.endsWith("s")) {
                    return new TAG_Short(Short.parseShort(string.substring(0, string.length() - 1)));
                } else if (string.endsWith("b")) {
                    return new TAG_Byte(Byte.parseByte(string.substring(0, string.length() - 1)));
                } else if (string.endsWith("l") || string.endsWith("L")) {
                    return new TAG_Long(Long.parseLong(string.substring(0, string.length() - 1)));
                } else if (string.endsWith("f")) {
                    return new TAG_Float(Float.parseFloat(string.substring(0, string.length() - 1)));
                } else if (string.endsWith("d") || string.contains(".")) {
                    return new TAG_Double(Double.parseDouble(string.substring(0, string.length() - 1)));
                } else {
                    return new TAG_Int(Integer.parseInt(string));
                }
            }
            catch(NumberFormatException e){
                return new TAG_String(string);
            }
        }
    }
    public static TAG_List list(String string) throws NBTAnalyzeException {
        int bbc = 0; // big brackets count
        int mbc = 0; // middle brackets count
        int index = 1;
        int startindex = 1;
        int quote = 0;
        boolean slash = false;
        TAG_List r = new TAG_List();
        while (index<string.length()){
            char ch = string.charAt(index);
            if(quote == 0){
                if (ch == '\"'){
                    quote = 2;
                }
                if (ch == '\''){
                    quote = 1;
                }
                if (ch == '{'){
                    bbc++;
                }
                if (ch == '}'){
                    bbc--;
                }
                if (ch == '['){
                    mbc++;
                }
                if (ch == ']'){
                    mbc--;
                }
                if (bbc == 0 && mbc == 0 && ch == ','){
                    r.add(analyze(string.substring(startindex,index)));
                    startindex = index + 1;
                }
            }else if (quote == 1){
                if (!slash && ch == '\'') quote = 0;
                slash = ch == '\\' && !slash;
            }else {
                if (!slash && ch == '\"') quote = 0;
                slash = ch == '\\' && !slash;
            }
            index ++;
        }
        r.add(analyze(string.substring(startindex,index-1)));
        return r;
    }
    public static TAG_Compound compound(String string) throws NBTAnalyzeException {
        TAG_Compound r = new TAG_Compound();
        int bbc = 0; // big brackets count
        int mbc = 0; // middle brackets count
        int index = 1;
        int startindex = 1;
        int quote = 0;
        boolean slash = false;
        boolean key = true;
        String currentkey = "";
        while (index<string.length()){
            if (key){
                int sindex = string.indexOf("\"",startindex);
                index = string.indexOf(":",startindex);
                if (index<0){
                    break;
                }
                if (sindex>=0 && sindex<index){
                    int nindex = sindex;
                    do {
                        nindex = string.indexOf("\"", nindex + 1);
                    }while (nindex >= 0 && string.charAt(nindex - 1) == '\\');
                    if (nindex>=0) {
                        currentkey = DataTool.removeBrackets(string.substring(sindex+1, nindex));
                        index = string.indexOf(":", nindex);
                    }else {
                        currentkey = string.substring(startindex,index);
                    }
                }else {
                    currentkey = string.substring(startindex,index);
                }
                startindex = index + 1;
                key = false;
            }
            char ch = string.charAt(index);
            if (quote == 0) {
                if (ch == '\"') {
                    quote = 2;
                }
                if (ch == '\'') {
                    quote = 1;
                }
                if (ch == '{') {
                    bbc++;
                }
                if (ch == '}') {
                    bbc--;
                }
                if (ch == '[') {
                    mbc++;
                }
                if (ch == ']') {
                    mbc--;
                }
                if (bbc == 0 && mbc == 0 && ch == ',') {
                    r.put(currentkey, analyze(string.substring(startindex, index)));
                    key = true;
                    startindex = index + 1;
                }
            } else if (quote == 1) {
                if (!slash && ch == '\'') quote = 0;
                slash = ch == '\\' && !slash;
            } else {
                if (!slash && ch == '\"') quote = 0;
                slash = ch == '\\' && !slash;
            }
            index ++;
        }
        if (!key && startindex<index-1) r.put(currentkey, analyze(string.substring(startindex,index-1)));
        return r;
    }
}
