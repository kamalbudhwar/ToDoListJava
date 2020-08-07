package sample.dataModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class ToDoData {
    private static ToDoData instance=new ToDoData();
    private static String fileName="ToDoListItems.txt";
    private ObservableList<toDoItem> toDoItems;
    private DateTimeFormatter formatter;

    public static ToDoData getInstance(){
        return instance;
    }

    private ToDoData(){
         formatter=DateTimeFormatter.ofPattern("dd-MM -yyyy");
    }

    public ObservableList<toDoItem> getList(){
        return toDoItems;
    }

    public void addItem(toDoItem item){
        toDoItems.add(item);
    }

    public void loadTodoItems() throws IOException{
        toDoItems= FXCollections.observableArrayList();
        Path path= Paths.get(fileName);
        BufferedReader br= Files.newBufferedReader(path);
        String input;
        try{
            while((input=br.readLine())!=null){
                String[] itemPieces=input.split("\t");
                String shortDescription=itemPieces[0];
                String detail=itemPieces[1];
                String dateString=itemPieces[2];
                LocalDate date=LocalDate.parse(dateString,formatter);
                toDoItem doItem=new toDoItem(shortDescription,detail,date);
                toDoItems.add(doItem);
            }

        }finally {
            if(br!=null){
                br.close();
            }
        }
    }

    public void storeToDoItems() throws IOException{
        Path path=Paths.get(fileName);
        BufferedWriter bw=Files.newBufferedWriter(path);
        try{
            Iterator<toDoItem> iter=toDoItems.iterator();
             while (iter.hasNext()){
               toDoItem doItem=iter.next();
               bw.write(String.format("%s\t%s\t%s",doItem.getShortDescription(),doItem.getDetails(),doItem.getDeadline().format(formatter)));
               bw.newLine();
             }
        }
        finally {
            if(bw!=null){
                bw.close();
            }
        }
    }

    public void deleteToDoItem(toDoItem item){
        toDoItems.remove(item);
    }
}
