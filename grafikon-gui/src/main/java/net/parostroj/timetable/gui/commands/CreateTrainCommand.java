/*
 * CreateTrainCommand.java
 * 
 * Created on 4.9.2007, 9:24:24
 */
package net.parostroj.timetable.gui.commands;

import java.util.ArrayList;
import java.util.List;
import net.parostroj.timetable.actions.RouteBuilder;
import net.parostroj.timetable.actions.TrainBuilder;
import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Command for creating trains.
 *
 * @author jub
 */
public class CreateTrainCommand extends Command {
    
    private String number;
    
    private TrainType type;
    
    private int topSpeed;
    
    private Node from;
    
    private Node to;
    
    private List<Node> through;
    
    private int time;
    
    private int defaultStop;
    
    private String description;
    
    private boolean diesel;
    
    private boolean electric;

    /**
     * creates instance of create train command.
     * 
     * @param number number of the train
     * @param type train type
     * @param topSpeed maximum speed of the train
     * @param from from station
     * @param to to station
     * @param time starting time
     * @param defaultStop default stop
     * @param description description
     * @param diesel if the train is electric
     * @param electric if the train is diesel
     */
    public CreateTrainCommand(String number, TrainType type, int topSpeed, Node from, Node to, List<Node> through, int time, int defaultStop, String description, boolean diesel, boolean electric) {
        this.number = number;
        this.type = type;
        this.topSpeed = topSpeed;
        this.from = from;
        this.to = to;
        this.through = through;
        this.time = time;
        this.defaultStop = defaultStop;
        this.description = description;
        this.diesel = diesel;
        this.electric = electric;
    }

    @Override
    public void execute(ApplicationModel model) {
        TrainBuilder trainBuilder = new TrainBuilder();
        RouteBuilder routeBuilder = new RouteBuilder();
        
        Route route = null;
        if (through == null)
            route = routeBuilder.createRoute(null, model.getDiagram().getNet(), from, to);
        else {
            List<Node> r = new ArrayList<Node>();
            r.add(from);
            r.addAll(through);
            r.add(to);
            route = routeBuilder.createRoute(null, model.getDiagram().getNet(), r);
        }

        Train train = trainBuilder.createTrain(IdGenerator.getInstance().getId(), number, type, topSpeed, route, time, model.getDiagram(), defaultStop);
        
        train.setDescription(description);
        train.setAttribute("diesel", diesel);
        train.setAttribute("electric", electric);
        
        // add train to diagram
        model.getDiagram().addTrain(train);
        
        // fire new event on the model
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_TRAIN, model, train));
    }

    @Override
    public void undo(ApplicationModel model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
