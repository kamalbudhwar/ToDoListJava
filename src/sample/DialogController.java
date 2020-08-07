package sample;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.dataModel.ToDoData;
import sample.dataModel.toDoItem;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescription;
    @FXML
    private TextArea details;
    @FXML
    private DatePicker datePicker;

    public toDoItem processResults(){
        String description=shortDescription.getText().trim();
        String detailinput=details.getText().trim();
        LocalDate date=datePicker.getValue();
        toDoItem newItem=new toDoItem(description,detailinput,date);
        ToDoData.getInstance().addItem(newItem);
        return newItem;
    }


}
