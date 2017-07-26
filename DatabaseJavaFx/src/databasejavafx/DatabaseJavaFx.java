/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databasejavafx;

import dataModel.FilmDAO;
import inputOutput.ConnectionData;
import inputOutput.PostgreSQLConnect;
import inputOutput.XmlParser;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.layout.VBox;


/**
 *
 * @author Dilly
 */
public class DatabaseJavaFx extends Application {
    
    private static final Logger logger = Logger.getLogger(DatabaseJavaFx.class.getName());
    private static final ObservableList<FilmDAO>  data = FXCollections.observableArrayList();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage){
        // setup the tableView 
        // make sure the tablView data cannot be modified
        TableView tableView = new TableView();
        tableView.setEditable(true);
        final Label label = new Label("Film");
        label.setFont(new Font("Arial", 20));
        
        // Four instances of class TableColumn
        // one for each column of the database table we want to display 
        // call constructor and passing as an argument
        TableColumn title = new TableColumn("Title");
        title.setMinWidth(200);
        // binding the data from the FilmDAO in the UI TableView section
        title.setCellValueFactory(
                new PropertyValueFactory<FilmDAO, String>("fileName"));
        
        TableColumn description = new TableColumn("Description");
        description.setMinWidth(700);
        description.setCellValueFactory(
             new PropertyValueFactory<FilmDAO, String>("filmDescription"));
        
        TableColumn rate = new TableColumn("Rental Rate");
        rate.setMinWidth(100);
        rate.setCellValueFactory(
                new PropertyValueFactory<FilmDAO, String>("filmPrice"));
        
        TableColumn rating = new TableColumn("Rating");
        rating.setMinWidth(100);
        rating.setCellValueFactory(
                new PropertyValueFactory<FilmDAO, String>("filmRating"));
        
        // add the columns to tableView
        tableView.getColumns().addAll(title, description, rate, rating);
        
        // button to fetch films from database
        // acts the same as a JButton
        final Button fetchData = new Button("Fetch films from database");
        
        // adding an event handler to fetchData Button
        // works the same as actionListener
        fetchData.setOnAction(new EventHandler<ActionEvent>(){
            
            // same as ActionListener's actionPerformer()
            @Override
            public void handle(ActionEvent event) {
                
                fetchData(tableView);
            }    
        });
        
        // create a scene
        Scene scene = new Scene(new Group());
        // create a JLabel type of class like in swing
        final VBox vbox = new VBox();
        vbox.setPrefHeight(500);
        
        // set the style, simliar to CSS
        vbox.setStyle("-fx-background-color: cornsilk; -fx-adding: 50;");
        // everything part of the UI control VBox are the children
        vbox.getChildren().addAll(label, tableView);
        ((Group)scene.getRoot()).getChildren().addAll(vbox, fetchData);
        
        // setup the UI
        stage.setTitle("Films for Rent");
        // create the Scene using the layout manager
        stage.setScene(scene);
        // show the UI
        stage.show();   
    }
    private void fetchData(TableView tableView){
        
        // call method getConnection in the class to connect to database
        try(Connection con = getConnection()){
            
            // populate UI control ListView with the sta from the database
            // by calling method fetchData()
            tableView.setItems(fetchFilms(con));
        }
        catch(SQLException | ClassNotFoundException ex){
            
            logger.log(Level.SEVERE, null, ex);
        }
    }
    private Connection getConnection() throws ClassNotFoundException, SQLException{
        
        // using the logger to track application proprties
        logger.info("Getting a database connection");
        
        // read in the datatbase properties from XML
        // pass the location of the XML file as an argument
        XmlParser xml = new XmlParser("inputOutput/properties.xml");
        ConnectionData data = xml.getConnectionData();
        
        // create the connnection using the data from the XML file
        PostgreSQLConnect connect = new PostgreSQLConnect(data);
        Connection dbConnect = connect.getConnection();
        
        return dbConnect;
    }
    
    private ObservableList<FilmDAO> fetchFilms(Connection con) throws SQLException{
        
        logger.info("fetching film from database");
        ObservableList<FilmDAO> films = FXCollections.observableArrayList();
        
        String select = "SELECT title, rental_rate, rating, description " +
                        "FROM film " +
                        "ORDER BY title";
        
        logger.info("Select statement " + select);
        
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(select);
        
        while(rs.next()){
            
            // creat the DAO
            FilmDAO film = new FilmDAO();
            film.setFilmName(rs.getString("title"));
            film.setFilmRating(rs.getString("rating"));
            film.setFilmDescription(rs.getString("description"));
            film.setFilmPrice(rs.getDouble("rental_rate"));
            
            films.add(film);
        }
        
        logger.info("Found " + films.size() + " films");
        
        return films;
    }
    
        
}
