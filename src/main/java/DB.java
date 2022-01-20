import lombok.Data;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;


@Data
public class DB {
    private HashMap<String, Table> tables;


    public void load(String path) throws IOException {
        HashMap<String, Table> tables = new HashMap<>();
        Class<?> tableKeyType;
        ZipInputStream zis = new ZipInputStream(new FileInputStream(path));
        ZipEntry currentEntry = zis.getNextEntry();

        while (currentEntry != null) {
            //[0] = tableName, [1] = columnName
            String[] table_column = currentEntry.getName().split("/");
            boolean entryIsTable = table_column.length == 1;

            if (entryIsTable){
                //getting input stream for the .md file of the table
                String mdEntryName = table_column[0] + "/" + table_column[0] + ".md";

                //getting the String of the .md file
                String mdString = getEntryString(path, mdEntryName);

                //new table using the md file data
                tables.put(table_column[0], new Table(table_column[0], mdString));
                tableKeyType = tables.get(tables.size()-1).getKeys().get(0).getClass();
            }
            else{
                if (!table_column[1].contains(".md")){
                    String columnString = getEntryString(zis);
                    ColumnData column = new ColumnData(table_column[1], columnString);
                    System.out.println(table_column[1] + ": ");
                    System.out.println(columnString);
                    System.out.println("");
                }
            }
            currentEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    private InputStream getInputStream(String path, String entryName) throws IOException {
        ZipEntry mdEntry = new ZipEntry(entryName);
        ZipFile file = new ZipFile(path);
        return file.getInputStream(mdEntry);
    }

    private String getEntryString(String path, String entryName) throws IOException {
        byte[] buffer = new byte[1024];
        InputStream mdStream =  getInputStream(path, entryName);
        return getEntryString(mdStream);
    }

    private String getEntryString(InputStream stream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((len = stream.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        return out.toString(StandardCharsets.UTF_8);
    }

    public void save(String path) throws IOException {
        ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(path));
        ArrayList<File> directories = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();

        for (String tableName : tables.keySet()) {
            HashMap<String, String> fileNameToData = tables.get(tableName).toWrite();
            ZipEntry folder = new ZipEntry(tableName + "/");
            outputStream.putNextEntry(folder);
            for (String fileName : fileNameToData.keySet()) {
                ZipEntry entry = new ZipEntry(tableName + "/" + fileName);
                outputStream.putNextEntry(entry);
                byte[] finalData = fileNameToData.get(fileName).getBytes(StandardCharsets.US_ASCII);
                outputStream.write(finalData, 0, finalData.length);
                outputStream.closeEntry();
            }
            outputStream.closeEntry();
        }
        outputStream.close();
    }
}
