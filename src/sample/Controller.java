package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import sample.dataModel.ToDoData;
import sample.dataModel.toDoItem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Controller {
    private List<toDoItem> items;
    @FXML
    private ListView<toDoItem> todoListView;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;
    @FXML
    public void initialize(){
        listContextMenu=new ContextMenu();
        MenuItem deleteMenuItem=new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                toDoItem item=todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);
//        toDoItem item1=new toDoItem("Gym","at benliegh", LocalDate.of(2020, Month.MAY,23));
//        toDoItem item2=new toDoItem("Birthday","Bajinder's birthday today", LocalDate.of(2020, Month.MARCH,19));
//        toDoItem item3=new toDoItem("Bills","Rent Transfer and Electricity", LocalDate.of(2020, Month.JANUARY,3));
//        toDoItem item4=new toDoItem("Meeting","in Brisbane Get some notes first for improvements of the project", LocalDate.of(2020, Month.AUGUST,17));
//
//        items=new ArrayList<toDoItem>();
//        items.add(item1);
//        items.add(item2);
//        items.add(item3);
//        items.add(item4);
//        items.addAll(ToDoData.getInstance().getList());
//        ToDoData.getInstance().setToDoItems(items);

        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<toDoItem>() {
            @Override
            public void changed(ObservableValue<? extends toDoItem> observable, toDoItem oldValue, toDoItem newValue) {
                if(newValue!=null){
                    toDoItem item=todoListView.getSelectionModel().getSelectedItem();
                    descriptionArea.setText(item.getDetails());
                    DateTimeFormatter df=DateTimeFormatter.ofPattern("MMMM d,YYYY");
                    deadlineLabel.setText(df.format(item.getDeadline()));
                }
            }
        });
        SortedList<toDoItem> sortedList=new SortedList<toDoItem>(ToDoData.getInstance().getList(), new Comparator<toDoItem>() {
            @Override
            public int compare(toDoItem o1, toDoItem o2) {
                return o1.getDeadline().compareTo(o2.getDeadline());
            }
        });
//        todoListView.setItems(ToDoData.getInstance().getList());
        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();
        todoListView.setCellFactory(new Callback<ListView<toDoItem>, ListCell<toDoItem>>() {
            @Override
            public ListCell<toDoItem> call(ListView<toDoItem> param) {
                ListCell<toDoItem> cell=new ListCell<toDoItem>(){
                    @Override
                    protected void updateItem(toDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty){
                            setText(null);
                        }
                        else{
                            setText(item.getShortDescription());
                            if(item.getDeadline().isBefore(LocalDate.now().plusDays(1))){
                                setTextFill(Color.TOMATO);
                            }
                            else if(item.getDeadline().equals(LocalDate.now().plusDays(1))){
                                setTextFill(Color.MAROON);
                            }
                        }
                    }
                };

                cell.emptyProperty().addListener((obs,wasEmpty,isNowEmpty)->{
                     if(isNowEmpty){
                         cell.setContextMenu(null);
                     }
                     else {
                         cell.setContextMenu(listContextMenu);
                     }
                });

                return cell;
            }

        });
    }

    @FXML
    public void showNewItemDialog(){
        Dialog<ButtonType> dialog=new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new to do item");
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialogue.fxml"));


        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
//            Parent root= FXMLLoader.load(getClass().getResource("todoItemDialogue.fxml"));
//            dialog.getDialogPane().setContent(root);

        }catch (IOException e){
            System.out.println("Could not load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result=dialog.showAndWait();
        if(result.isPresent()&&result.get()==ButtonType.OK){
            DialogController controller=fxmlLoader.getController();
            toDoItem newItem=controller.processResults();
            todoListView.getSelectionModel().select(newItem);
        }

    }

    @FXML
    public void handleKeyPress(KeyEvent keyEvent){
        toDoItem selectedItem=todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem!=null){
            System.out.println(keyEvent.getCode().equals(KeyCode.BACK_SPACE));               // SOUT - temporary
            if(keyEvent.getCode().equals(KeyCode.BACK_SPACE)){
                deleteItem(selectedItem);
            }
        }
    }

    @FXML
    public void handleClickListView(){
       toDoItem item=(toDoItem)todoListView.getSelectionModel().getSelectedItem();
       descriptionArea.setText(item.getDetails());
       deadlineLabel.setText(item.getDeadline().toString());

    }

    public void deleteItem(toDoItem item){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item: "+item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to Confirm, or Cancel to back out.");
        Optional<ButtonType> result= alert.showAndWait();

        if(result.isPresent()&&result.get()==ButtonType.OK){
              ToDoData.getInstance().deleteToDoItem(item);
        }

    }

    public void handleFilterButton(){

    }
}
